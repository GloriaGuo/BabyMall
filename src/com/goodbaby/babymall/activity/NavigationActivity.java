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
    private Button mTitleButtonRight;
    
    private static final int UPDATE_WHAT_TITLE = 0;
    private static final int UPDATE_WHAT_UI_PAGE_START = 1;
    private static final int UPDATE_WHAT_UI_PAGE_FINISH = 2;
    
    private static final String UPDATE_KEY_TITLE = "title";
    private static final String UPDATE_KEY_URL = "url";
    
    private int mCartNumber = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        
        initTab();
        mProgressBar = (ProgressBar) findViewById(R.id.wv_progress);

        mTitleButtonRight = (Button) findViewById(R.id.imageButtonRight);
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
                String url = msg.getData().getString(UPDATE_KEY_URL);
                try {
                    String path = new URL(url).getPath();
                    // update left Button
                    Button leftButton = (Button) findViewById(R.id.imageButtonLeft);
                    if (mCustomWebView.getWebView().canGoBack()) {
                        if (path.equalsIgnoreCase(getString(R.string.home_url_path))) {
                            leftButton.setVisibility(View.GONE);
                        } else {
                            leftButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        leftButton.setVisibility(View.GONE);
                    }
                   
                    leftButton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mCustomWebView.getWebView().goBack();
                        }
                        
                    });
                    
                    // update tab
                    if (path.equalsIgnoreCase(getString(R.string.category_url_path))) {
                        disableTitleButtonRight();
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_0);
                    }
                    else if (path.equalsIgnoreCase(getString(R.string.member_url_path))) {
                        disableTitleButtonRight();
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_1);
                    }
                    else if (path.equalsIgnoreCase(getString(R.string.home_url_path))) {
                        disableTitleButtonRight();
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_2);
                    }
                    else if (path.equalsIgnoreCase(getString(R.string.cart_url_path))) {
                        updateTitleButtonCheckout();
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_3);
                    }
                    else if (path.equalsIgnoreCase(getString(R.string.more_url_path))) {
                        disableTitleButtonRight();
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_4);
                    }
                    else if (path.contains(getString(R.string.add_receiver_url_path))) {
                        updateTitleButtonAddReceiver();
                    }
                    else if (path.contains(getString(R.string.checkout_url_path))) {
                        updateTitleButtonSubmit();
                    }
                    else if (path.contains(getString(R.string.pay_url_path))) {
                        updateTitleButtonPay();
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "Invalie url : " + e.getMessage());
                }
                
                // update cart number
                mCartNumber = getCartNumber(url);
                if (mCartNumber > 0) {
                    String cartText = mCartNumber >= 10 ? "N" : String.valueOf(mCartNumber);
                    mBadge.setText(cartText);
                    mBadge.setBackgroundResource(R.drawable.badge);
                    mBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                    mBadge.setGravity(Gravity.CENTER);
                    mBadge.show();
                }
                else {
                    mBadge.hide();
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
    
    public void disableTitleButtonRight() {
        mTitleButtonRight.setVisibility(View.GONE);
    }
    
    public void updateTitleButtonCheckout() {
        if (mCartNumber > 0) {
            mTitleButtonRight.setText(R.string.nav_button_right_checkout);
            mTitleButtonRight.setVisibility(View.VISIBLE);
            mTitleButtonRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCustomWebView.handleButtonCheckout();
                }
            });
        }
    }
    
    public void updateTitleButtonAddReceiver() {
        mTitleButtonRight.setText(R.string.nav_button_right_add_receiver);
        mTitleButtonRight.setVisibility(View.VISIBLE);
        mTitleButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomWebView.handleButtonAddReceiber();
            }
        });
    }
    
    public void updateTitleButtonSubmit() {
        mTitleButtonRight.setText(R.string.nav_button_right_submit);
        mTitleButtonRight.setVisibility(View.VISIBLE);
        mTitleButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomWebView.handleButtonSubmit();
            }
        });
    }
    
    public void updateTitleButtonPay() {
        mTitleButtonRight.setText(R.string.nav_button_right_pay);
        mTitleButtonRight.setVisibility(View.VISIBLE);
        mTitleButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomWebView.handleButtonPay();
            }
        });
    }

    @Override
    public void onPhotoBrowserStart(String urls, String clickedUrl) {
        Intent intent = new Intent(NavigationActivity.this, GalleryActivity.class);
        intent.putExtra("urls", urls + "\n" + clickedUrl);
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
        String path = null;
        try {
            path = new URL(url).getPath();
        } catch (MalformedURLException e) {
            return true;
        }
        if (path.equalsIgnoreCase(getString(R.string.home_url_path))) {
            return false;
        }
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
            if (start > cookies.length() || end > cookies.length()
                    || start < 0 || end <= 0 || start > end) {
                cart_number = 0;
            }
            else {
                cart_number = Integer.parseInt(cookies.substring(start, end));
                Log.d(TAG, "Have CART_NUMBER=" + cart_number);
            }
        }
        return cart_number;
    }

}
