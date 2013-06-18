package com.goodbaby.babymall.activity;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;

import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PhotoViewActivity extends Activity {

    private static final String TAG = BabyMallApplication.getApplicationTag()
            + PhotoViewActivity.class.getSimpleName();
    
    private static HashMap<String, Bitmap> imagesCache = new HashMap<String, Bitmap>(); 
    private ArrayList<String> mUrlsList;
    private ViewPager mViewPager;
    private MyPagerAdapter mPagerAdapter;
    private MyHandler mHandler;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private int mCurrentPosition;
    
    private static int RELOAD_VIEW = 0;
    private static int SHOW_PROGRESS = 1;
    private static int ERASE_PROGRESS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);
        init();
    }
    
    @Override
    protected void onDestroy() {
        if (imagesCache != null) {
            for (Entry<String, Bitmap> entry : imagesCache.entrySet()) {
                Bitmap b = entry.getValue();
                if (b != null) {
                    b.recycle();
                    b = null;
                }
            }
            imagesCache.clear();
        }
        
        super.onDestroy();
    }
    
    private void init(){
        String[] data = getIntent().getStringExtra("urls").split("\n");
        JSONArray jsonArray;
        
        mUrlsList = new ArrayList<String>();
        int position = 0;
        try {
            jsonArray = new JSONArray(data[0]);
            if (jsonArray != null) { 
                int len = jsonArray.length();
                for (int i=0;i<len;i++){
                    String url = jsonArray.get(i).toString();
                    if (url.equalsIgnoreCase(data[1])) {
                        position = i;
                    }
                    mUrlsList.add(url);
                } 
             }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException e: " + e.getMessage());
        }
        
        mViewPager = (MyViewPager)findViewById(R.id.image_viewpager);
        mTextView = (TextView)findViewById(R.id.image_position);
        mProgressBar = (ProgressBar)findViewById(R.id.image_progress);

        mHandler = new MyHandler();

        mPagerAdapter = new MyPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(position);
        mCurrentPosition = position;
        mTextView.setText((position+1) + "/" + mUrlsList.size());
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                if (null == imagesCache.get(mUrlsList.get(position))) {
                    mHandler.sendEmptyMessage(SHOW_PROGRESS);
                } else {
                    mHandler.sendEmptyMessage(ERASE_PROGRESS);
                }
            }
        });
        
    }

    public static class MyViewPager extends ViewPager {

        public MyViewPager(Context context) {
            super(context);
        }

        public MyViewPager(Context context, AttributeSet attrs)
        {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
        }
        
    }
    
    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mUrlsList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            final Bitmap image = imagesCache.get(mUrlsList.get(position));

            if (image != null) {
                photoView.setImageBitmap(image);
            } else {
                LoadImageTask task = new LoadImageTask();  
                task.execute(mUrlsList.get(position), String.valueOf(position));
            }

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            return photoView;
        }
        
        @Override  
        public int getItemPosition(Object object) {  
            return POSITION_NONE;  
        } 

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
    
    private class MyHandler extends Handler {
        public MyHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RELOAD_VIEW) {
                mPagerAdapter.notifyDataSetChanged();
                if (msg.getData().getString("position").equals(String.valueOf(mCurrentPosition))) {
                    mProgressBar.setVisibility(View.GONE);
                }
            } else if (msg.what == SHOW_PROGRESS) {
                mProgressBar.setVisibility(View.VISIBLE);
                mTextView.setText((mCurrentPosition+1) + "/" + mUrlsList.size());
            } else if (msg.what == ERASE_PROGRESS) {
                mProgressBar.setVisibility(View.GONE);
                mTextView.setText((mCurrentPosition+1) + "/" + mUrlsList.size());
            }
        }
    }
    
    private class LoadImageTask extends AsyncTask<String,Void,Bitmap> {  
        
        LoadImageTask() {  
        }  

        @Override  
        protected Bitmap doInBackground(String... params) { 
            if (imagesCache.containsKey(params[0])) {
                Log.d(TAG, "Already in downloading... ignore.");
                return null;
            }
            
            imagesCache.put(params[0], null);
            Bitmap bitmap = null;  
            try { 
                URL url = new URL(params[0]);  
                URLConnection conn = url.openConnection();  
                conn.connect();  
                InputStream is = conn.getInputStream(); 
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = 10;
                bitmap = BitmapFactory.decodeStream(is, null, options); 
                imagesCache.remove(params[0]);
                imagesCache.put(params[0], bitmap);
                is.close();  
            } catch (Exception e) {  
                Log.e(TAG, "Download image failed : " + e.getMessage());  
            } catch (OutOfMemoryError e) {
                Log.e(TAG, "Should NOT get this error!");
            }
            
            Message msg = new Message();
            msg.what = RELOAD_VIEW;
            Bundle b = new Bundle();
            b.putString("position", params[1]);
            msg.setData(b);
            mHandler.sendMessage(msg);

            return bitmap;  
        }  
    }

}
