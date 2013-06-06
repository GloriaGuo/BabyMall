package com.goodbaby.babymall.activity;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;

public class GalleryActivity extends Activity 
    implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    
    private static final String TAG = BabyMallApplication.getApplicationTag()
            + GalleryActivity.class.getSimpleName();
    
    private static HashMap<String, SoftReference<Bitmap>> imagesCache = new HashMap<String, SoftReference<Bitmap>>(); 
    
    private MyGallery mMainGallery;
    private ImageAdapter mImageAdapter;
    private MyHandler mHandler;
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.gallery_image); 
        init();
    }  
    
    private void init(){
        String urls = getIntent().getStringExtra("urls");
        JSONArray jsonArray;
        ArrayList<String> urlList = new ArrayList<String>();
        try {
            jsonArray = new JSONArray(urls);
            if (jsonArray != null) { 
                int len = jsonArray.length();
                for (int i=0;i<len;i++){ 
                    urlList.add(jsonArray.get(i).toString());
                } 
             }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException e: " + e.getMessage());
            //return;
        }
        
        mMainGallery = (MyGallery) findViewById(R.id.gallery);
        mImageAdapter = new ImageAdapter(urlList, this);
        mMainGallery.setAdapter(mImageAdapter);
        mMainGallery.setOnItemSelectedListener(this);
        
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
            SoftReference<Bitmap> image = null;  
            
            image = imagesCache.get(imageUrls.get(position));
            if (image == null) {
                LoadImageTask task = new LoadImageTask();  
                task.execute(imageUrls.get(position)); 
            } 
            
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(image.get()); 
            imageView.setDrawingCacheEnabled(true);
            imageView.setAdjustViewBounds(true);
            imageView.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      
            return imageView;  
        }  
      
        class LoadImageTask extends AsyncTask<String,Void,Bitmap> {  
      
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
                    bitmap = BitmapFactory.decodeStream(is); 
//                    imagesCache.put(params[0], bitmap);
                    is.close();  
                } catch (Exception e) {  
                    Log.e(TAG, "Download image failed : " + e.getMessage());  
                }
                
                mHandler.sendEmptyMessage(0);
  
                return bitmap;  
            }  
        }
    }
    
    private class MyHandler extends Handler {
        public MyHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mImageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "---> onClick do nothing.");         
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        Log.d(TAG, "---> onItemSelected do nothing."); 
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Log.d(TAG, "---> onNothingSelected do nothing."); 
    }  
}
