package com.goodbaby.babymall;

import java.io.File;

import android.app.Application;
import android.content.Context;
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
    
    public static final boolean DEBUG = true;
    
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
        
        // Configuration
        mConfiguration = new BabyMallConfiguration(mContext);
    }

}
