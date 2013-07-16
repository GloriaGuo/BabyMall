package com.goodbaby.babymall;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

public class BabyMallApplication extends Application {

	/**
     * Log tag for this application.
     */
    protected static String mApplicationTag = "BabyMall";
    
    public static final String EXTERNAL_STORAGE_PATH = 
            Environment.getExternalStorageDirectory() + "/.babymall/";
    
    public static final String ADVERTISEMENT_IMAGE = "advertisement.png";
    
    public static final String CART_NUMBER = "S[CART_NUMBER]";
    
    /**
     * The application context
     */
    protected static Context mContext = null;
    /**
     * Application configuration
     */
    private static BabyMallConfiguration mConfiguration = null;
    
    /**
     * @return the application tag (used in application logs)
     */
    public static String getApplicationTag() {
        return mApplicationTag;
    }
    
    public static Context getContext() {
        return mContext;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mContext = this;
        
        // Clean installed apk file automatically
        final File apk = new File(Environment.getExternalStorageDirectory() + "/Download/babymall.apk");
        if (apk.exists()) {
            // Found update apk in storage, delete it
            Log.i(mApplicationTag, "Cleaning existing update file " 
                + apk.getAbsolutePath());
            apk.delete();
        } 
        
        File dir = new File(BabyMallApplication.EXTERNAL_STORAGE_PATH);
        dir.mkdirs();
        
        mConfiguration = new BabyMallConfiguration(mContext);
    }
    
    public static void saveBitmapToFile(String name, Bitmap bitmap) {      
        File f = new File(BabyMallApplication.EXTERNAL_STORAGE_PATH + name);
        try {
            if (f.exists()) {
                Log.d(mApplicationTag, "Already existed. Removed!");
                f.delete();
            }
            f.createNewFile();
            FileOutputStream fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            Log.e(mApplicationTag, "Save the image file failed: " + e.getMessage());
        }
    }
    
    public static void revomeFile(String name) {
        File f = new File(BabyMallApplication.EXTERNAL_STORAGE_PATH + name);
        try {
            if (f.exists()) {
                Log.d(mApplicationTag, "Remove the previous file " + name);
                f.delete();
            }
        } catch (Exception e) {
            Log.e(mApplicationTag, "Remove the image file failed: " + e.getMessage());
        }
    }
    
    /**
     * Check if there is an active working network connection
     * @return true if there is an active working network connection
     */
    public static boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)mContext.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return null != info && info.isConnected();
    }

    /**
     * Gets the MAC when there's no available IMEI
     */
    public static String getMAC() {
        WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);  
        WifiInfo info = wifi.getConnectionInfo();  
        return info.getMacAddress().replace(":", "");
    }
    
    /**
     * Gets the configuration
     * @return the current configuration
     */
    public static BabyMallConfiguration getConfiguration() {
        return mConfiguration;
    }
}
