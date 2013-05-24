package com.goodbaby.babymall.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.goodbaby.babymall.R;

public class NavigationActivity extends FragmentActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title); 

    }

}
