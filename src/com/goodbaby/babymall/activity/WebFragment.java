package com.goodbaby.babymall.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;

public class WebFragment extends Fragment {

	private static final String TAG = BabyMallApplication.getApplicationTag()
	        + WebFragment.class.getSimpleName();

    private View mRoot;
    private WebView mWebView;
	private String mTag;
	private LayoutInflater mInflater;

	public WebFragment() {
	}

	public WebFragment(String tag) {
		mTag = tag;

		Log.d(TAG, "Constructor: tag=" + tag);
	}

	/* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mInflater = LayoutInflater.from(getActivity());
        mRoot = mInflater.inflate(R.layout.webview, null);

        this.mWebView = (WebView) mRoot.findViewById(R.id.wv);
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.requestFocus();
        

        if (mTag.endsWith(TabsFragment.TAB_HOME)) {
            this.mWebView.loadUrl(getResources().getString(R.string.tab_home));
        }
        else if (mTag.endsWith(TabsFragment.TAB_CATALOGUE)) {
            this.mWebView.loadUrl(getResources().getString(R.string.tab_catalogue));
        }
        else if (mTag.endsWith(TabsFragment.TAB_PROFILE)) {
            this.mWebView.loadUrl(getResources().getString(R.string.tab_profile));
        }
        else if (mTag.endsWith(TabsFragment.TAB_CART)) {
            this.mWebView.loadUrl(getResources().getString(R.string.tab_cart));
        }
        else if (mTag.endsWith(TabsFragment.TAB_MORE)) {
            this.mWebView.loadUrl(getResources().getString(R.string.tab_more));
        }
        else {
            Log.v(TAG, "got tag=" + mTag);
        }

        mWebView.setWebViewClient(new WebViewClient(){       
            public boolean shouldOverrideUrlLoading(WebView view, String url) {       
                view.loadUrl(url);       
                return true;       
            }

            /* (non-Javadoc)
             * @see android.webkit.WebViewClient#onReceivedError(android.webkit.WebView, int, java.lang.String, java.lang.String)
             */
            @Override
            public void onReceivedError(WebView view, int errorCode,
                    String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            /* (non-Javadoc)
             * @see android.webkit.WebViewClient#onReceivedHttpAuthRequest(android.webkit.WebView, android.webkit.HttpAuthHandler, java.lang.String, java.lang.String)
             */
            @Override
            public void onReceivedHttpAuthRequest(WebView view,
                    HttpAuthHandler handler, String host, String realm) {
                // TODO Auto-generated method stub
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }

            /* (non-Javadoc)
             * @see android.webkit.WebViewClient#onReceivedLoginRequest(android.webkit.WebView, java.lang.String, java.lang.String, java.lang.String)
             */
            @Override
            public void onReceivedLoginRequest(WebView view, String realm,
                    String account, String args) {
                // TODO Auto-generated method stub
                super.onReceivedLoginRequest(view, realm, account, args);
            }
            
        });
        
        mWebView.setWebChromeClient(new WebChromeClient() {

            /* (non-Javadoc)
             * @see android.webkit.WebChromeClient#onJsAlert(android.webkit.WebView, java.lang.String, java.lang.String, android.webkit.JsResult)
             */
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                    JsResult result) {

                return super.onJsAlert(view, url, message, result);
            }

            /* (non-Javadoc)
             * @see android.webkit.WebChromeClient#onJsConfirm(android.webkit.WebView, java.lang.String, java.lang.String, android.webkit.JsResult)
             */
            @Override
            public boolean onJsConfirm(WebView view, String url,
                    String message, JsResult result) {
                // TODO Auto-generated method stub
                return super.onJsConfirm(view, url, message, result);
            }

            /* (non-Javadoc)
             * @see android.webkit.WebChromeClient#onProgressChanged(android.webkit.WebView, int)
             */
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
            }

            /* (non-Javadoc)
             * @see android.webkit.WebChromeClient#onReceivedIcon(android.webkit.WebView, android.graphics.Bitmap)
             */
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                // TODO Auto-generated method stub
                super.onReceivedIcon(view, icon);
            }

            /* (non-Javadoc)
             * @see android.webkit.WebChromeClient#onReceivedTitle(android.webkit.WebView, java.lang.String)
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
            
        });
        return mRoot;
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// this is really important in order to save the state across screen
		// configuration changes for example
		setRetainInstance(true);

		// you only need to instantiate these the first time your fragment is
		// created; then, the method above will do the rest

		// initiate the loader to do the background work
	}



}
