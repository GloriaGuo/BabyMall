package com.goodbaby.babymall.activity;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;

public class WebFragment extends ListFragment implements
		LoaderCallbacks<Void> {

	private static final String TAG = "FragmentTabs";

	private String mTag;
	private LayoutInflater mInflater;

	public WebFragment() {
	}

	public WebFragment(String tag) {
		mTag = tag;

		Log.d(TAG, "Constructor: tag=" + tag);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// this is really important in order to save the state across screen
		// configuration changes for example
		setRetainInstance(true);

		mInflater = LayoutInflater.from(getActivity());

		// you only need to instantiate these the first time your fragment is
		// created; then, the method above will do the rest

		// initiate the loader to do the background work
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Void> onCreateLoader(int id, Bundle args) {
		AsyncTaskLoader<Void> loader = new AsyncTaskLoader<Void>(getActivity()) {

			@Override
			public Void loadInBackground() {
				return null;
			}
		};
		// somehow the AsyncTaskLoader doesn't want to start its job without
		// calling this method
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Void> loader, Void result) {

		// add the new item and let the adapter know in order to refresh the
		// views

		// advance in your list with one step
	}

	@Override
	public void onLoaderReset(Loader<Void> loader) {
	}

}
