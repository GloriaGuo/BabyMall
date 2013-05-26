package com.goodbaby.babymall;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Environment;
import android.util.Log;

public class BabyMallApplication extends Application {

	/**
     * Log tag for this application.
     */
    protected static String mApplicationTag = "PM";
    
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
        final File apk = new File(Environment.getExternalStorageDirectory() + "/Download/kids.apk");
        if (apk.exists()) {
            // Found update apk in storage, delete it
            Log.i(mApplicationTag, "Cleaning existing update file " 
                + apk.getAbsolutePath());
            apk.delete();
        } 
        
        // Configuration
        mConfiguration = new BabyMallConfiguration(mContext);

        mConfiguration.registerPreferenceChangeListener(this.mSettingsListener);
    }
    
    private final OnSharedPreferenceChangeListener mSettingsListener = 
            new OnSharedPreferenceChangeListener() {
                /* (non-Javadoc)
                 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
                 */
                @Override
                public void onSharedPreferenceChanged(
                        final SharedPreferences sharedPreferences, final String key) {
                    BabyMallApplication.getConfiguration();
                }
            };

}
