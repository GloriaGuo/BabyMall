package com.goodbaby.babymall.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LoadActivity extends Activity {
    
    private static final String TAG = "LoadActivity";
    
    private MyHandler myHandler;
    
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
    
    public Bitmap getHttpBitmap(String url){
        URL mRemoteUrl;
        Bitmap bitmap = null;
        
        try {
            mRemoteUrl = new URL(url);
            HttpURLConnection conn;
            conn = (HttpURLConnection)mRemoteUrl.openConnection();
            
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);
            conn.setUseCaches(false);

            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            Log.e(TAG, "Open the advertisement icon failed: " + e.getMessage());
        }

        return bitmap;
    }
    
    private void showAlert(int message) {
        AlertDialog mErrorDialog = new AlertDialog.Builder(this)
            .setTitle(R.string.alert_dialog_title) 
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
    
    private void saveBitmapToFile(Bitmap bitmap) {
        File f = new File(BabyMallApplication.EXTERNAL_STORAGE_PATH);
        f.mkdirs();
        
        f = new File(BabyMallApplication.EXTERNAL_STORAGE_PATH + BabyMallApplication.ADVERTISEMENT_IMAGE);
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    
    private class MyHandler extends Handler {
        public MyHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
            if (null == mAdvertisementBitmap) {
                showAlert(R.string.alert_advertisement_download_failed);
            } else {
                saveBitmapToFile(mAdvertisementBitmap);
                Intent intent = new Intent(LoadActivity.this, AdvertismentActivity.class);  
                LoadActivity.this.startActivity(intent);  
                LoadActivity.this.finish();  
            }
        }
    }

}
