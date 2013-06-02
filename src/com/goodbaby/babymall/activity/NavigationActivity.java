package com.goodbaby.babymall.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.TextView;

import com.goodbaby.babymall.R;
import com.goodbaby.babymall.activity.WebFragment.UIUpdateInterface;

public class NavigationActivity extends FragmentActivity implements UIUpdateInterface {
    
    private MyHandler myHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title); 
        
        myHandler = new MyHandler();
    }

    @Override
    public void onTitleUpdate(String title) {
        Bundle b = new Bundle();
        b.putString("title", title);
        Message msg = new Message();
        msg.what = 0;
        msg.setData(b);
        this.myHandler.sendMessage(msg);
    }
    
    private class MyHandler extends Handler {
        public MyHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
            if (msg.what == 0) {
                // update the title
                TextView tv_title = (TextView) findViewById(R.id.navTitle);
                tv_title.setText(msg.getData().getString("title"));
            }
        }
    }

}
