package com.goodbaby.babymall.activity;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;
import com.goodbaby.babymall.activity.CustomWebView.UIUpdateInterface;

public class NavigationActivity extends Activity
        implements UIUpdateInterface, OnClickListener{

    private static final String TAG = BabyMallApplication.getApplicationTag()
            + NavigationActivity.class.getSimpleName();
    
    private MyHandler myHandler;
    private BadgeView mBadge;
    private CustomWebView mCustomWebView;
    private ProgressBar mProgressBar;
    private LinearLayout mTabsLayout;
    private Button mTabButton0;
    private Button mTabButton1;
    private Button mTabButton2;
    private Button mTabButton3;
    private Button mTabButton4;
    
    private static final int UPDATE_WHAT_TITLE = 0;
    private static final int UPDATE_WHAT_UI_PAGE_START = 1;
    private static final int UPDATE_WHAT_UI_PAGE_FINISH = 2;
    
    private static final String UPDATE_KEY_TITLE = "title";
    private static final String UPDATE_KEY_URL = "url";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        
        initTab();
        mProgressBar = (ProgressBar) findViewById(R.id.wv_progress);

        mCustomWebView = new CustomWebView();
        mCustomWebView.init(this, R.id.wv);
        
        myHandler = new MyHandler();
    }
    
    void initTab() {
        mTabsLayout = (LinearLayout) findViewById(R.id.tabs);
        mTabButton0 = (Button) findViewById(R.id.tab_button_0);
        mTabButton1 = (Button) findViewById(R.id.tab_button_1);
        mTabButton2 = (Button) findViewById(R.id.tab_button_2);
        mTabButton3 = (Button) findViewById(R.id.tab_button_3);
        mTabButton4 = (Button) findViewById(R.id.tab_button_4);
        mTabButton0.setOnClickListener(this);
        mTabButton1.setOnClickListener(this);
        mTabButton2.setOnClickListener(this);
        mTabButton3.setOnClickListener(this);
        mTabButton4.setOnClickListener(this);

        mTabButton2.setBackgroundResource(R.drawable.tabbar_mainbtn);
        
        mBadge = new BadgeView(this, mTabButton3);
        mBadge.setBackgroundResource(R.drawable.badge);
        
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tab_button_0:
            mTabsLayout.setBackgroundResource(R.drawable.tabbar_0);
            mCustomWebView.updateUrl(mCustomWebView.TAB_CATALOGUE);
            break;
        case R.id.tab_button_1:
            mTabsLayout.setBackgroundResource(R.drawable.tabbar_1);
            mCustomWebView.updateUrl(mCustomWebView.TAB_PROFILE);
            break;
        case R.id.tab_button_2:
            mTabsLayout.setBackgroundResource(R.drawable.tabbar_2);
            mCustomWebView.updateUrl(mCustomWebView.TAB_HOME);
            break;
        case R.id.tab_button_3:
            mTabsLayout.setBackgroundResource(R.drawable.tabbar_3);
            mCustomWebView.updateUrl(mCustomWebView.TAB_CART);
            break;
        case R.id.tab_button_4:
            mTabsLayout.setBackgroundResource(R.drawable.tabbar_4);
            mCustomWebView.updateUrl(mCustomWebView.TAB_MORE);
            break;
        default:
            Log.v(TAG, "should not get here");
            break;
        }
    }

    @Override
    public void onTitleUpdate(String title) {
        Bundle b = new Bundle();
        b.putString(UPDATE_KEY_TITLE, title);
        Message msg = new Message();
        msg.what = UPDATE_WHAT_TITLE;
        msg.setData(b);
        this.myHandler.sendMessage(msg);
    }

    private class MyHandler extends Handler {
        public MyHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_WHAT_TITLE) {
                TextView tv_title = (TextView) findViewById(R.id.navTitle);
                tv_title.setText(msg.getData().getString(UPDATE_KEY_TITLE));
            }
            else if (msg.what == UPDATE_WHAT_UI_PAGE_FINISH) {
                // update left Button
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
                
                String url = msg.getData().getString(UPDATE_KEY_URL);
                // update tab
                try {
                    String path = new URL(url).getPath();
                    if (path.equalsIgnoreCase(BabyMallApplication.TAB_CATEGORY_URL_PATH)) {
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_0);
                    }else if (path.equalsIgnoreCase(BabyMallApplication.TAB_MEMBER_URL_PATH)) {
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_1);
                    }else if (path.equalsIgnoreCase(BabyMallApplication.TAB_HOME_URL_PATH)) {
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_2);
                    }else if (path.equalsIgnoreCase(BabyMallApplication.TAB_CART_URL_PATH)) {
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_3);
                    }else if (path.equalsIgnoreCase(BabyMallApplication.TAB_MORE_URL_PATH)) {
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_4);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                
                // update cart number
                int cart_number = getCartNumber(url);
                if (cart_number > 0) {
                    String cartText = cart_number >= 10 ? "N" : String.valueOf(cart_number);
                    mBadge.setText(cartText);
                    mBadge.setBackgroundResource(R.drawable.badge);
                    mBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                    mBadge.setGravity(Gravity.CENTER);
                    mBadge.show();
                }
                
                // disable progress bar
                mProgressBar.setVisibility(View.GONE);
            }
            else if (msg.what == UPDATE_WHAT_UI_PAGE_START) {
                // show progress bar
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPhotoBrowserStart(String urls) {
        Intent intent = new Intent(NavigationActivity.this, GalleryActivity.class);
        intent.putExtra("urls", urls);
        startActivity(intent);
    }
    
    @Override
    public void onWebPageStart() {
        this.myHandler.sendEmptyMessage(UPDATE_WHAT_UI_PAGE_START);        
    }
    
    @Override
    public void onWebPageFinished(String url) {
        Bundle b = new Bundle();
        b.putString(UPDATE_KEY_URL, url);
        Message msg = new Message();
        msg.what = UPDATE_WHAT_UI_PAGE_FINISH;
        msg.setData(b);
        this.myHandler.sendMessage(msg);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mCustomWebView.getWebView().canGoBack()) {
            if (canGoBack(mCustomWebView.getWebView().getUrl())) {
                mCustomWebView.getWebView().goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private Boolean canGoBack(String url) {
        return true;
    }
    
    private int getCartNumber(String url) {
        int cart_number = 0;
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(url);
        if (null != cookies && cookies.contains(BabyMallApplication.CART_NUMBER)) {
            int start = cookies.indexOf(BabyMallApplication.CART_NUMBER) + 
                    BabyMallApplication.CART_NUMBER.length() + 1;
            int end = cookies.indexOf(';', start);
            cart_number = Integer.parseInt(cookies.substring(start, end));
            Log.d(TAG, "Have CART_NUMBER=" + cart_number);
        }
        return cart_number;
    }

}
