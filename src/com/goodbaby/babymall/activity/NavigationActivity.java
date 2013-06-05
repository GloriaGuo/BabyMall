package com.goodbaby.babymall.activity;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;
import com.goodbaby.babymall.activity.CustomWebView.UIUpdateInterface;

public class NavigationActivity extends FragmentActivity 
    implements UIUpdateInterface {

    private static final String TAG = BabyMallApplication.getApplicationTag()
            + NavigationActivity.class.getSimpleName();
    
    private MyHandler myHandler;
    private BadgeView mBadge;
    private CustomWebView mCustomWebView;
    private Button mTabButton0;
    private Button mTabButton1;
    private Button mTabButton2;
    private Button mTabButton3;
    private Button mTabButton4;
    
    private static final int UPDATE_WHAT_TITLE = 0;
    private static final int UPDATE_WHAT_BADGE = 1;
    private static final int UPDATE_WHAT_LEFTBUTTON = 2;
    
    private static final String UPDATE_KEY_TITLE = "title";
    private static final String UPDATE_KEY_BADGE = "badge";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        
        
        mCustomWebView = new CustomWebView();
        mCustomWebView.init(this, R.id.wv, R.id.wv_progress);
        
        myHandler = new MyHandler();
    }
    
    void initTab() {
        mTabButton0 = (Button) findViewById(R.id.tab_button_0);
        mTabButton1 = (Button) findViewById(R.id.tab_button_1);
        mTabButton2 = (Button) findViewById(R.id.tab_button_2);
        mTabButton3 = (Button) findViewById(R.id.tab_button_3);
        mTabButton4 = (Button) findViewById(R.id.tab_button_4);
        
        mTabButton2.setBackgroundResource(R.drawable.tabbar_mainbtn);
        mBadge = new BadgeView(NavigationActivity.this, mTabButton3);
    }
    
    OnClickListener mTabButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            
        }
        
    };

    @Override
    public void onTitleUpdate(String title) {
        Bundle b = new Bundle();
        b.putString(UPDATE_KEY_TITLE, title);
        Message msg = new Message();
        msg.what = UPDATE_WHAT_TITLE;
        msg.setData(b);
        this.myHandler.sendMessage(msg);
    }

    @Override
    public void onBadgeUpdate(String cartNumber) {
        Bundle b = new Bundle();
        b.putString(UPDATE_KEY_BADGE, cartNumber);
        Message msg = new Message();
        msg.what = UPDATE_WHAT_BADGE;
        msg.setData(b);
        this.myHandler.sendMessage(msg);
    }
    
    @Override
    public void onLeftButtonUpdate() {
        this.myHandler.sendEmptyMessage(UPDATE_WHAT_LEFTBUTTON);
    }
    
    private class MyHandler extends Handler {
        public MyHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.v(TAG, "got msg=" + msg.what);
            if (msg.what == UPDATE_WHAT_TITLE) {
                TextView tv_title = (TextView) findViewById(R.id.navTitle);
                tv_title.setText(msg.getData().getString(UPDATE_KEY_TITLE));
            }
            else if (msg.what == UPDATE_WHAT_BADGE) {
                mBadge.setText(msg.getData().getString(UPDATE_KEY_BADGE));
                mBadge.setBackgroundResource(R.drawable.badge);
                mBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                mBadge.setGravity(Gravity.CENTER);
                mBadge.show();
            }
            else if (msg.what == UPDATE_WHAT_LEFTBUTTON) {
                ImageButton leftButton = (ImageButton) findViewById(R.id.imageButtonLeft);
                if (mCustomWebView.getWebView().canGoBack()) {
                    leftButton.setVisibility(View.VISIBLE);
                } else {
                    leftButton.setVisibility(View.GONE);
                }
               
                leftButton.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mCustomWebView.getWebView().goBack();
                    }
                    
                });
            }
        }
    }

    @Override
    public void onPhotoBrowserStart(List<String> urls) {
        // TODO Auto-generated method stub
        
    }

}
