package com.goodbaby.babymall.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.TextView;

import com.goodbaby.babymall.R;
import com.goodbaby.babymall.activity.WebFragment.UIUpdateInterface;

public class NavigationActivity extends FragmentActivity implements UIUpdateInterface{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title); 

    }

    @Override
    public void onTitleUpdate(String title) {
        TextView tv_title = (TextView) findViewById(R.id.navTitle);
        tv_title.setText(title);
    }

}
