package com.goodbaby.babymall;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
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

}
