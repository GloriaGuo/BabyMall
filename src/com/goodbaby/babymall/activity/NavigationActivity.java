package com.goodbaby.babymall.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;
import com.goodbaby.babymall.activity.CustomWebView.UIUpdateInterface;

public class NavigationActivity extends Activity
        implements UIUpdateInterface, View.OnClickListener{

    private static final String TAG = BabyMallApplication.getApplicationTag()
            + NavigationActivity.class.getSimpleName();
    
    private MyHandler myHandler;
    private BadgeView mBadge;
    private CustomWebView mCustomWebView = null;
    private ProgressBar mProgressBar;
    private LinearLayout mTabsLayout;
    private Button mTabButton0;
    private Button mTabButton1;
    private Button mTabButton2;
    private Button mTabButton3;
    private Button mTabButton4;
    private Button mTitleButtonLeft;
    private Button mTitleButtonRight;
    
    private static final int UPDATE_WHAT_TITLE = 0;
    private static final int UPDATE_WHAT_UI_PAGE_START = 1;
    private static final int UPDATE_WHAT_UI_PAGE_FINISH = 2;
    private static final int UPDATE_WHAT_UI_PAGE_BADGE = 3;
    
    private static final String UPDATE_KEY_TITLE = "title";
    private static final String UPDATE_KEY_URL = "url";
    
    private int mCartNumber = 0;
    private Boolean mCanPay = false;
    
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    
    private int mGoBackSteps = -1;
    private String mCurrentUrl;
    
    private Thread mUpdateBadgeThread;
    private boolean isRunUpdateBadge = true;
    
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
        
        mTitleButtonLeft = (Button) findViewById(R.id.imageButtonLeft);
        mTitleButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomWebView.getWebView().goBackOrForward(mGoBackSteps);
                mGoBackSteps = -1;
            }
        });
        
        mTitleButtonRight = (Button) findViewById(R.id.imageButtonRight);
        mTitleButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomWebView.handleRightButton();
            }
        });
        
        myHandler = new MyHandler();
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();   

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                mTabsLayout.setBackgroundResource(R.drawable.tabbar_3);
                mCustomWebView.updateUrl(mCustomWebView.TAB_CART);
            }
        });
        

        mUpdateBadgeThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(isRunUpdateBadge) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    NavigationActivity.this.onWebPageUpdateBadge();
                }
            }
            
        });
        mUpdateBadgeThread.start();
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
	protected void onDestroy() {
    	isRunUpdateBadge = false;
		super.onDestroy();
	}

	/* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        super.onResume();
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
                mCurrentUrl = msg.getData().getString(UPDATE_KEY_URL);
                Log.d(TAG, "Page Finished, url == " + mCurrentUrl);
                try {
                    String path = new URL(mCurrentUrl).getPath();
                    mTitleButtonLeft.setVisibility(View.GONE);
                    mTitleButtonRight.setVisibility(View.GONE);
                    if (mCustomWebView.getWebView().canGoBack()) {
                        if (NavigationActivity.this.canGoBack(mCurrentUrl)) {
                            mTitleButtonLeft.setVisibility(View.VISIBLE);
                        }
                    }
                    
                    // update tab
                    if (path.equalsIgnoreCase(getString(R.string.category_url_path))) {
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_0);
                    }
                    else if (path.equalsIgnoreCase(getString(R.string.member_url_path))) {
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_1);
                    }
                    else if (path.equalsIgnoreCase(getString(R.string.home_url_path))) {
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_2);
                    }
                    else if (path.equalsIgnoreCase(getString(R.string.cart_url_path))) {
                        updateTitleButtonCheckout();
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_3);
                    }
                    else if (path.equalsIgnoreCase(getString(R.string.more_url_path))) {
                        mTabsLayout.setBackgroundResource(R.drawable.tabbar_4);
                    }
                    else if (path.equalsIgnoreCase(getString(R.string.add_receiver_url_path1))
                            || path.equalsIgnoreCase(getString(R.string.add_receiver_url_path2))) {
                        updateTitleButtonAddReceiver();
                    }
                    else if (path.equalsIgnoreCase(getString(R.string.checkout_url_path))) {
                        updateTitleButtonSubmit();
                    }
                    else if (path.contains(getString(R.string.orderdetail_url_path))) {
                        if (mCanPay) {
                            updateTitleButtonPay();
                        }
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "Invalie url : " + e.getMessage());
                }
                
                mProgressBar.setVisibility(View.GONE);
            }
            else if (msg.what == UPDATE_WHAT_UI_PAGE_BADGE) {
                if (null != mCurrentUrl) {
                    updateCartNumber(mCurrentUrl);
                }
            }
            else if (msg.what == UPDATE_WHAT_UI_PAGE_START) {
                // show progress bar
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }
    
    public void updateTitleButtonCheckout() {
        if (mCartNumber > 0) {
            mTitleButtonRight.setText(R.string.nav_button_right_checkout);
            mTitleButtonRight.setVisibility(View.VISIBLE);
        }
    }
    
    public void updateTitleButtonAddReceiver() {
        mTitleButtonRight.setText(R.string.nav_button_right_add);
        mTitleButtonRight.setVisibility(View.VISIBLE);
    }
    
    public void updateTitleButtonSubmit() {
        mTitleButtonRight.setText(R.string.nav_button_right_submit);
        mTitleButtonRight.setVisibility(View.VISIBLE);
    }
    
    public void updateTitleButtonPay() {
        mTitleButtonRight.setText(R.string.nav_button_right_pay);
        mTitleButtonRight.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPhotoBrowserStart(String urls, String clickedUrl) {
        Intent intent = new Intent(NavigationActivity.this, PhotoViewActivity.class);
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

    public void onWebPageUpdateBadge() {
        this.myHandler.sendEmptyMessage(UPDATE_WHAT_UI_PAGE_BADGE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mCustomWebView.getWebView().canGoBack()) {
            if (canGoBack(mCurrentUrl)) {
                mCustomWebView.getWebView().goBackOrForward(mGoBackSteps);
                mGoBackSteps = -1;
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
        if (path.equals(getResources().getString(R.string.payment_path))) {
            mGoBackSteps = -2;
        }
        Pattern pattern = Pattern.compile(getString(R.string.order_done_path) + "[0-9]{14}\\.html");
        Matcher matcher = pattern.matcher(path);
        if (path.equalsIgnoreCase(getString(R.string.home_url_path)) ||
            matcher.matches()) {
            return false;
        }
        return true;
    }
    
    private int getCartNumber(String url) {
        int cart_number = 0;
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            String cookies = cookieManager.getCookie(url);
            if (null == cookies) {
            	return -1;
            }
            Log.e(TAG, "cookies === " + cookies);
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
                }
            }
        } catch(Exception e) {
            Log.e(TAG, "Get cookie failed : " + e.getMessage());
        }
        return cart_number;
    }

    @Override
    public void onCanPaySet(String length) {
        mCanPay = length.equals("0") ? false : true;
    }

	private void updateCartNumber(String url) {
		int newCartNumber = getCartNumber(url);
		if (newCartNumber < 0) {
			return;
		}
		mCartNumber = newCartNumber;
		if (mCartNumber > 0) {
		    String cartText = mCartNumber >= 10 ? "N" : String.valueOf(mCartNumber);
		    mBadge.setText(cartText);
		    mBadge.setBackgroundResource(R.drawable.badge);
		    mBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		    mBadge.setBadgeMargin(25, 23);
		    mBadge.setGravity(Gravity.CENTER);
		    mBadge.show();
		}
		else {
		    mBadge.hide();
		}
	}

    @Override
    public void onReceiveError(String message) {
        AlertDialog mErrorDialog = new AlertDialog.Builder(this)
            .setMessage(
                    this.getResources().getString(R.string.alert_cannot_access) + message)
            .setCancelable(true)
            .setPositiveButton(
                    R.string.ok_button,
                    new OnClickListener() {

                        @Override
                        public void onClick(final DialogInterface dialog, 
                                final int which) {
                            
                        }
                
            }).show();
      
        mErrorDialog.setOwnerActivity(this);
    }
    
}
