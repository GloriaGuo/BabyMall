package com.goodbaby.babymall.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabWidget;
import android.widget.TextView;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;
import com.goodbaby.babymall.activity.WebFragment.UIUpdateInterface;

public class NavigationActivity extends FragmentActivity implements UIUpdateInterface {

    private static final String TAG = BabyMallApplication.getApplicationTag()
            + NavigationActivity.class.getSimpleName();
    
    private MyHandler myHandler;
    private BadgeView mBadge;
    
    private static final int UPDATE_WHAT_TITLE = 0;
    private static final int UPDATE_WHAT_BADGE = 1;
    

    private static final String UPDATE_KEY_TITLE = "title";
    private static final String UPDATE_KEY_BADGE = "badge";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        

        ImageView tw = (ImageView) ((TabWidget) findViewById(android.R.id.tabs)).getChildAt(3).findViewById(R.id.tab_image);
        mBadge = new BadgeView(NavigationActivity.this, tw);
        
        myHandler = new MyHandler();
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

    @Override
    public void onBadgeUpdate(int cartNumber) {
        Bundle b = new Bundle();
        b.putInt(UPDATE_KEY_BADGE, cartNumber);
        Message msg = new Message();
        msg.what = UPDATE_WHAT_BADGE;
        msg.setData(b);
        this.myHandler.sendMessage(msg);
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
                int cartNumber = msg.getData().getInt(UPDATE_KEY_BADGE);
                mBadge.setText(String.valueOf(cartNumber));
                mBadge.setBackgroundResource(R.drawable.badge);
                mBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                mBadge.setGravity(Gravity.CENTER);
                mBadge.show();
                Log.v(TAG, "cartNumber=" + cartNumber);
            }
        }
    }


}
