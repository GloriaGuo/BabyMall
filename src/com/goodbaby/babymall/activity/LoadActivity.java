package com.goodbaby.babymall.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;

public class LoadActivity extends Activity {
    
    private static final String TAG = "LoadActivity";
        
    private MyHandler myHandler;
    
    private Boolean isAlive = true;
    
    Bitmap mAdvertisementBitmap = null;
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);  
        
        myHandler = new MyHandler();
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                // Download the advertisement icon
                mAdvertisementBitmap = getHttpBitmap(getResources().getString(R.string.advertisement));
                LoadActivity.this.myHandler.sendEmptyMessage(0);
            }
        }).start();
    }
    
    @Override
    protected void onResume() {
        super.onPause();
        isAlive = true;
    }
    
    @Override
    protected void onPause() {
        isAlive = false;
        super.onPause();
    }
    
    @Override  
    protected void onDestroy() {
        isAlive = false;
        if (null != mAdvertisementBitmap) {
            mAdvertisementBitmap.recycle();
        }
        super.onDestroy();
    }
    
    public Bitmap getHttpBitmap(String url){
        URL mRemoteUrl;
        Bitmap bitmap = null;
        
        try {
            mRemoteUrl = new URL(url);
            HttpURLConnection conn;
            conn = (HttpURLConnection)mRemoteUrl.openConnection();
            
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setUseCaches(false);

            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            Log.e(TAG, "Open the advertisement image failed: " + e.getMessage());
        }

        return bitmap;
    }
    
    private void showAlert(int message) {
        AlertDialog mErrorDialog = new AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(true)
            .setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(final DialogInterface dialog) {
                    finish();
                }
            }).show();
            
        mErrorDialog.setOwnerActivity(this);
    }
    
    private class MyHandler extends Handler {
        public MyHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
            if (!isAlive) {
                return;
            }
            
            Intent intent;
            if (null == mAdvertisementBitmap) {
//                showAlert(R.string.alert_advertisement_download_failed);
                intent = new Intent(LoadActivity.this, NavigationActivity.class); 
            } else {
                BabyMallApplication.saveBitmapToFile(
                        BabyMallApplication.ADVERTISEMENT_IMAGE, mAdvertisementBitmap);
                intent = new Intent(LoadActivity.this, AdvertisementActivity.class); 
            }
            LoadActivity.this.startActivity(intent);  
            LoadActivity.this.finish();
        }
    }

}
