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
            Environment.getExternalStorageDirectory() + "/" + ".babymall";
    
    public static final String ADVERTISEMENT_IMAGE = "advertisement.png";
    
    public static final String TAB_HOME_URL_PATH = "/";
    public static final String TAB_CATEGORY_URL_PATH = "/category.html";
    public static final String TAB_MEMBER_URL_PATH = "/member.html";
    public static final String TAB_CART_URL_PATH = "/cart.html";
    public static final String TAB_MORE_URL_PATH = "/inapp/more.html";
   
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
    
    /**
     * Gets the configuration
     * @return the current configuration
     */
    public static BabyMallConfiguration getConfiguration() {
        return mConfiguration;
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
        
        // Configuration
        mConfiguration = new BabyMallConfiguration(mContext);
    }
    
    public static void saveBitmapToFile(String name, Bitmap bitmap) {      
        File f = new File(BabyMallApplication.EXTERNAL_STORAGE_PATH + name);
        FileOutputStream fOut = null;
        try {
            if (f.exists()) {
                Log.d(mApplicationTag, "Remove the previous file.");
                f.delete();
            }
            f.createNewFile();
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            Log.e(mApplicationTag, "Create the advertisement file failed: " + e.getMessage());
        }
    }

}
