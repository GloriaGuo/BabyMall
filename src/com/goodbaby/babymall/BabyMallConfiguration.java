package com.goodbaby.babymall;

import android.content.Context;
import android.content.SharedPreferences;

public class BabyMallConfiguration {
    private final Context mContext;
    
    private final SharedPreferences mSharedPreferences;
    
    private static final String SHARED_PREFS_NAME = "babymall.preferences";
    
    public static final String PREFERENCE_KEY_IS_REGISTED = "is_registed";
    
    /**
     * Creates a new Configuration instance 
     * @param appContext application context
     */
    BabyMallConfiguration(final Context appContext) {
        this.mContext = appContext;
        mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);  
    }
    
    public boolean getIsRegisted() {
        return this.mSharedPreferences.getBoolean(PREFERENCE_KEY_IS_REGISTED, false);
    }
    
    public void setIsRegisted(boolean result) {
        mSharedPreferences.edit().putBoolean(
                PREFERENCE_KEY_IS_REGISTED, result).commit();
    }
}