package com.goodbaby.babymall.activity;

import android.app.Activity;
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

	private static final String TAG = "FragmentTabs";
	public static final String TAB_HOME = "home";
	public static final String TAB_CATALOGUE = "catalogue";
	public static final String TAB_PROFILE = "profile";
	public static final String TAB_CART = "cart";
	public static final String TAB_MORE = "more";

	private View mRoot;
	private TabHost mTabHost;
	private int mCurrentTab;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
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
		updateTab(TAB_HOME, R.id.tab_1);
	}

	private void setupTabs() {
		mTabHost.setup(); // important!
		mTabHost.addTab(newTab(TAB_CATALOGUE, R.string.tab_text_1, R.id.tab_1));
		mTabHost.addTab(newTab(TAB_PROFILE, R.string.tab_text_2, R.id.tab_2));
		mTabHost.addTab(newTab(TAB_HOME, R.string.tab_text_3, R.id.tab_3));
		mTabHost.addTab(newTab(TAB_CART, R.string.tab_text_4, R.id.tab_4));
		mTabHost.addTab(newTab(TAB_MORE, R.string.tab_text_5, R.id.tab_5));
	}

	private TabSpec newTab(String tag, int labelId, int tabContentId) {
		Log.d(TAG, "buildTab(): tag=" + tag);

		View indicator = LayoutInflater.from(getActivity()).inflate(
				R.layout.tab,
				(ViewGroup) mRoot.findViewById(android.R.id.tabs), false);
		if (tag.equals(TAB_HOME)) {
			((ImageView) indicator.findViewById(R.id.tab_image)).setImageResource(R.drawable.tab_home);
		}
//		((TextView) indicator.findViewById(R.id.text)).setText(labelId);

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
			tw.setBackground(getResources().getDrawable(R.drawable.tabbar_0));
			updateTab(tabId, R.id.tab_1);
			mCurrentTab = 0;
			return;
		}
		if (TAB_PROFILE.equals(tabId)) {
			tw.setBackground(getResources().getDrawable(R.drawable.tabbar_1));
			updateTab(tabId, R.id.tab_2);
			mCurrentTab = 1;
			return;
		}
		if (TAB_HOME.equals(tabId)) {
			tw.setBackground(getResources().getDrawable(R.drawable.tabbar_2));
			updateTab(tabId, R.id.tab_3);
			mCurrentTab = 1;
			return;
		}
		if (TAB_CART.equals(tabId)) {
			tw.setBackground(getResources().getDrawable(R.drawable.tabbar_3));
			updateTab(tabId, R.id.tab_4);
			mCurrentTab = 1;
			return;
		}
		if (TAB_MORE.equals(tabId)) {
			tw.setBackground(getResources().getDrawable(R.drawable.tabbar_4));
			updateTab(tabId, R.id.tab_5);
			mCurrentTab = 1;
			return;
		}
	}

	private void updateTab(String tabId, int placeholder) {
		FragmentManager fm = getFragmentManager();
		if (fm.findFragmentByTag(tabId) == null) {
			fm.beginTransaction()
					.replace(placeholder, new WebFragment(tabId), tabId)
					.commit();
		}
	}

}
