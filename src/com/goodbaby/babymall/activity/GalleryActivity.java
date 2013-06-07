package com.goodbaby.babymall.activity;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;
import android.widget.ProgressBar;

public class GalleryActivity extends Activity 
    implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    
    private static final String TAG = BabyMallApplication.getApplicationTag()
            + GalleryActivity.class.getSimpleName();
    
    private static int RELOAD_VIEW = 0;
    
    private static HashMap<String, Bitmap> imagesCache = new HashMap<String, Bitmap>(); 
    
    private ProgressBar mProgressBar;
    private MyGallery mMainGallery;
    private ImageAdapter mImageAdapter;
    private MyHandler mHandler;
        
    @Override  
    protected void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.gallery_image); 
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
        
        ArrayList<String> urlsList = new ArrayList<String>();
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
                    urlsList.add(url);
                } 
             }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException e: " + e.getMessage());
        }
        
        mProgressBar = (ProgressBar) findViewById(R.id.gallery_progress);
        mMainGallery = (MyGallery) findViewById(R.id.gallery);
        mImageAdapter = new ImageAdapter(urlsList, this);
        mMainGallery.setAdapter(mImageAdapter);
        mMainGallery.setOnItemSelectedListener(this);
        mMainGallery.setSelection(position);

        mHandler = new MyHandler();
    }
    
    public static class MyGallery extends Gallery
    {
        private GestureDetector mGestureDetector;
        private MyGuestureImp mMyGuestureImp;
        private Context mContext;

        public MyGallery(Context context)
        {
            super(context);
            mContext = context;
            mMyGuestureImp = new MyGuestureImp();
            mGestureDetector = new GestureDetector(mContext, mMyGuestureImp);
        }

        public MyGallery(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            mContext = context;
            mMyGuestureImp = new MyGuestureImp();
            mGestureDetector = new GestureDetector(mContext, mMyGuestureImp);
        }

        public MyGallery(Context context, AttributeSet attrs, int defStyle)
        {
            super(context, attrs, defStyle);
            mContext = context;
            mMyGuestureImp = new MyGuestureImp();
            mGestureDetector = new GestureDetector(mContext, mMyGuestureImp);
            setLongClickable(true);
            //mGestureDetector.setIsLongpressEnabled(false);
        }
        
        public boolean onTouchEvent(MotionEvent event)
        {
            mGestureDetector.onTouchEvent(event);
            return super.onTouchEvent(event);
        }
        
        public class MyGuestureImp extends GestureDetector.SimpleOnGestureListener
        {
            public boolean onDoubleTap(MotionEvent e)
            {
                return true;
            }
        }

    }
    
    private class ImageAdapter extends BaseAdapter{  
        private List<String> imageUrls; 
        private Context context;  
      
        public ImageAdapter(List<String> imageUrls, Context context) {  
            this.imageUrls = imageUrls;  
            this.context = context;  
        }  
      
        public int getCount() {  
            return imageUrls.size();  
        }  
      
        public Object getItem(int position) {  
            return imageUrls.get(position);  
        }  
      
        public long getItemId(int position) {  
            return position;  
        }  
      
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {  
            Bitmap image = null;  
            
            ImageView imageView = new ImageView(context);
            image = imagesCache.get(imageUrls.get(position));
            if (image != null) {
                imageView.setImageBitmap(image); 
            } else {
                LoadImageTask task = new LoadImageTask();  
                task.execute(imageUrls.get(position));
            }
            
            imageView.setDrawingCacheEnabled(true);
            imageView.setAdjustViewBounds(true);
            imageView.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            
            return imageView;  
        }  
    }
    
    private class MyHandler extends Handler {
        public MyHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RELOAD_VIEW) {
                mImageAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }
  
    private class LoadImageTask extends AsyncTask<String,Void,Bitmap> {  
  
        LoadImageTask() {  
        }  

        @Override  
        protected Bitmap doInBackground(String... params) {  
            Bitmap bitmap = null;  
            try { 
                URL url = new URL(params[0]);  
                URLConnection conn = url.openConnection();  
                conn.connect();  
                InputStream is = conn.getInputStream(); 
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeStream(is, null, options); 
                imagesCache.put(params[0], bitmap);
                is.close();  
            } catch (Exception e) {  
                Log.e(TAG, "Download image failed : " + e.getMessage());  
            } catch (OutOfMemoryError e) {
                Log.e(TAG, "Should NOT get this error!");
            }
            
            mHandler.sendEmptyMessage(RELOAD_VIEW);

            return bitmap;  
        }  
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "---> onClick do nothing.");         
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        Log.d(TAG, "---> onItemSelected index === " + arg2);
//        galleryWhetherStop(arg2);
    } 

//    private void galleryWhetherStop(final int index) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    //Thread.sleep(1000);
//                    if (index == 0) {
//                        mImageUrlsList.add(mAllUrlsList.get(index));
//                    }
//                    if (index!=0 && mAllUrlsList.get(index-1)  != null) {
//                        mImageUrlsList.add(mAllUrlsList.get(index-1));
//                    }
//                    if (index != mAllUrlsList.size()-1 && mAllUrlsList.get(index+1) != null) {
//                        mImageUrlsList.add(mAllUrlsList.get(index+1));
//                    }
//                    mHandler.sendEmptyMessage(DOWNLOAD_IMAGES);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }                
//            }
//            
//        }).start();
//    }
    
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Log.d(TAG, "---> onNothingSelected do nothing."); 
    }  

}
