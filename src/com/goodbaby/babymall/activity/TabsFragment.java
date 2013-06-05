package com.goodbaby.babymall.activity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

import com.goodbaby.babymall.R;

public class TabsFragment extends Fragment implements OnTabChangeListener {

	private static final String TAG = "TabsFragment";
	public static final String TAB_HOME = "home";
	public static final String TAB_CATALOGUE = "catalogue";
	public static final String TAB_PROFILE = "profile";
	public static final String TAB_CART = "cart";
	public static final String TAB_MORE = "more";
	
	public static final int TAB_INDEX_HOME = 2;
    public static final int TAB_INDEX_CATALOGUE = 0;
    public static final int TAB_INDEX_PROFILE = 1;
    public static final int TAB_INDEX_CART = 3;
    public static final int TAB_INDEX_MORE = 4;

	private View mRoot;
	private TabHost mTabHost;
	private int mCurrentTab = 2;

    private SensorManager mSensorManager;
    
    @Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	}

	/* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSensorManager = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.tabs_fragment, null);
		mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
		setupTabs();
		return mRoot;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(mCurrentTab);
		// manually start loading stuff in the first tab
		updateTab(TAB_HOME, R.id.tab_2);
	}

	private void setupTabs() {
		mTabHost.setup(); // important!
		mTabHost.addTab(newTab(TAB_CATALOGUE, R.id.tab_0));
		mTabHost.addTab(newTab(TAB_PROFILE, R.id.tab_1));
		mTabHost.addTab(newTab(TAB_HOME, R.id.tab_2));
		mTabHost.addTab(newTab(TAB_CART, R.id.tab_3));
		mTabHost.addTab(newTab(TAB_MORE, R.id.tab_4));
	}

	private TabSpec newTab(String tag, int tabContentId) {
		Log.d(TAG, "buildTab(): tag=" + tag);

		View indicator = LayoutInflater.from(getActivity()).inflate(
				R.layout.tab,
				(ViewGroup) mRoot.findViewById(android.R.id.tabs), false);
		if (tag.equals(TAB_HOME)) {
			((ImageView) indicator.findViewById(R.id.tab_image)).setImageResource(R.drawable.tabbar_mainbtn);
		}

		TabSpec tabSpec = mTabHost.newTabSpec(tag);
		tabSpec.setIndicator(indicator);
		tabSpec.setContent(tabContentId);
		return tabSpec;
	}

	@Override
	public void onTabChanged(String tabId) {
		Log.d(TAG, "onTabChanged(): tabId=" + tabId);
		TabWidget tw = (TabWidget) mRoot.findViewById(android.R.id.tabs);
		if (TAB_CATALOGUE.equals(tabId)) {
            tw.setBackgroundResource(R.drawable.tabbar_0);
			updateTab(tabId, R.id.tab_0);
			mCurrentTab = 0;
			return;
		}
		if (TAB_PROFILE.equals(tabId)) {
            tw.setBackgroundResource(R.drawable.tabbar_1);
			updateTab(tabId, R.id.tab_1);
			mCurrentTab = 1;
			return;
		}
		if (TAB_HOME.equals(tabId)) {
            tw.setBackgroundResource(R.drawable.tabbar_2);
			updateTab(tabId, R.id.tab_2);
			mCurrentTab = 2;
			return;
		}
		if (TAB_CART.equals(tabId)) {
            tw.setBackgroundResource(R.drawable.tabbar_3);
			updateTab(tabId, R.id.tab_3);
			mCurrentTab = 3;
			return;
		}
		if (TAB_MORE.equals(tabId)) {
            tw.setBackgroundResource(R.drawable.tabbar_4);
			updateTab(tabId, R.id.tab_4);
			mCurrentTab = 4;
			return;
		}
	}

	private void updateTab(String tabId, int placeholder) {	 
//	    FragmentManager fm = getFragmentManager();
//	    CustomWebView wf = (CustomWebView) fm.findFragmentByTag("web");
//	    if (null == wf) {
//            fm.beginTransaction().replace(placeholder, new CustomWebView(tabId), "web").commit();
//	    } else {
//	        wf.updateUrl(tabId);
//	    }
	}

	private final SensorEventListener mSensorListener = new SensorEventListener() {
	    /** Minimum movement force to consider. */
	    private static final int MIN_FORCE = 10;
	    /**
	     * Minimum times in a shake gesture that the direction of movement needs to
	     * change.
	     */
	    private static final int MIN_DIRECTION_CHANGE = 3;
	    /** Maximum pause between movements. */
	    private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;
	    /** Maximum allowed time for shake gesture. */
	    private static final int MAX_TOTAL_DURATION_OF_SHAKE = 400;
	    /** Time when the gesture started. */
	    private long mFirstDirectionChangeTime = 0;
	    /** Time when the last movement started. */
	    private long mLastDirectionChangeTime;
	    /** How many movements are considered so far. */
	    private int mDirectionChangeCount = 0;
	    /** The last x position. */
	    private float lastX = 0;
	    /** The last y position. */
	    private float lastY = 0;
	    /** The last z position. */
	    private float lastZ = 0;
	    
	    public void onSensorChanged(SensorEvent se) {
	        // get sensor data
	        float x = se.values[0];
	        float y = se.values[1];
	        float z = se.values[2];
	        // calculate movement
	        float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);

	        if (totalMovement > MIN_FORCE) {
                // get time
	            long now = System.currentTimeMillis();

	            // store first movement time
	            if (mFirstDirectionChangeTime == 0) {
	                mFirstDirectionChangeTime = now;
	                mLastDirectionChangeTime = now;
                }
                
                // check if the last movement was not long ago
                long lastChangeWasAgo = now - mLastDirectionChangeTime;
                if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {
                    // store movement data
                    mLastDirectionChangeTime = now;
                    mDirectionChangeCount++;
    	            // store last sensor data 
    	            lastX = x;
    	            lastY = y;
    	            lastZ = z;
    
    	            // check how many movements are so far
    	            if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {
                        // check total duration
                        long totalDuration = now - mFirstDirectionChangeTime;
                        if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) {
                            Log.v(TAG, "Shaking...");
                            mTabHost.setCurrentTab(TAB_INDEX_CART);
//                            mShakeListener.onShake();
                            resetShakeParameters();
                        }
    	            }
    	        } else {
                    resetShakeParameters();
                }
            }
	    }

	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    }

	    private void resetShakeParameters() {
            mFirstDirectionChangeTime = 0;
            mDirectionChangeCount = 0;
            mLastDirectionChangeTime = 0;
            lastX = 0;
            lastY = 0;
            lastZ = 0;
	    }
	};
	
	@Override
    public void onResume() {
	    super.onResume();
	    mSensorManager.registerListener(
	            mSensorListener, 
	            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
	            SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
    public void onPause() {
	    mSensorManager.unregisterListener(mSensorListener);
	    super.onPause();
	}
	
}
