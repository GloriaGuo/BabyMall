package com.goodbaby.babymall.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;

public class LoadActivity extends Activity {
    
    private static final String TAG = "LoadActivity";
    
    private Boolean isAlive = true;
    
    Bitmap mAdvertisementBitmap = null;
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);  
        Log.d(TAG, "----> Register app... isRegisted == " + 
            BabyMallApplication.getConfiguration().getIsRegisted());
        if (!BabyMallApplication.getConfiguration().getIsRegisted()) {
            registerApp();
        }
    }

    private void registerApp() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String url = LoadActivity.this.getResources().getString(R.string.register_url) +
                        "&appId=" + LoadActivity.this.getResources().getString(R.string.app_id) +
                        "&udid=" + BabyMallApplication.getMAC() +
                        "&source=" + LoadActivity.this.getResources().getString(R.string.source);
                HttpURLConnection conn = null;
                try {
                    URL registerUrl = new URL(url);
                    conn = (HttpURLConnection)registerUrl.openConnection();
                    
                    conn.setConnectTimeout(5000);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        StringBuffer sBuffer = new StringBuffer();  
                        byte[] buf = new byte[1024];  
                        for (int n; (n = is.read(buf)) != -1;) {  
                            sBuffer.append(new String(buf, 0, n, "utf-8"));  
                        }  

                        JSONObject object = new JSONObject(sBuffer.toString()); 
                        BabyMallApplication.getConfiguration().setIsRegisted(
                                object.getString("success").equals("true") ? true : false);
                        is.close();
                    }
                    
                } catch (MalformedURLException e) {
                    Log.e(TAG, "Invalid URL : " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "Register app failed : " + e.getMessage());
                } catch (JSONException e) {
                    Log.e(TAG, "Invalid json string : " + e.getMessage());
                }
                if (null != conn) {
                    conn.disconnect();
                }
                    
            }
            
        }).start();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // check network status
        if (!BabyMallApplication.isConnected()) {
            showAlert(R.string.alert_network_not_available);
            return;
        }
        isAlive = true;
        if (null == mAdvertisementBitmap) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // Download the advertisement icon
                    mAdvertisementBitmap = getHttpBitmap(getResources().getString(R.string.advertisement));
                }
                
            }).start();
        } 
        
        new Handler().postDelayed(new Runnable(){  
            @Override  
            public void run() {
                if (!isAlive) {
                    return;
                }
                Intent intent;
                if (null == mAdvertisementBitmap) {
                    intent = new Intent(LoadActivity.this, NavigationActivity.class); 
                } else {
                    BabyMallApplication.saveBitmapToFile(
                            BabyMallApplication.ADVERTISEMENT_IMAGE, mAdvertisementBitmap);
                    intent = new Intent(LoadActivity.this, AdvertisementActivity.class); 
                }
                LoadActivity.this.startActivity(intent);  
                LoadActivity.this.finish();  
            }  
        }, 1000);
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
            
            conn.setConnectTimeout(1000);
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
            })
            .setPositiveButton(
                    R.string.ok_button,
                    new OnClickListener() {

                        @Override
                        public void onClick(final DialogInterface dialog, 
                                final int which) {
                            finish();
                        }
                
            }).show();
            
        mErrorDialog.setOwnerActivity(this);
    }
    
}
