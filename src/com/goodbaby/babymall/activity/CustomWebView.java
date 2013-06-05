package com.goodbaby.babymall.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;

public class CustomWebView {

	private static final String TAG = BabyMallApplication.getApplicationTag()
	        + CustomWebView.class.getSimpleName();

	private static final String CART_NUMBER = "S[CART_NUMBER]";
	private Context mContext;
    private WebView mWebView;
    private ProgressBar mProgressBar;
	private String mCurrentTab;
	private CustomJavaScriptInterface mCustomJavaScriptInterface;
	
    public static final String TAB_HOME = "home";
    public static final String TAB_CATALOGUE = "catalogue";
    public static final String TAB_PROFILE = "profile";
    public static final String TAB_CART = "cart";
    public static final String TAB_MORE = "more";

	public CustomWebView() {
	}
	
    public interface UIUpdateInterface {
        public void onTitleUpdate(String title);
        public void onBadgeUpdate(int cartNumber);
        public void onLeftButtonUpdate(WebView view);
    }
    
    public void init(Context context, int webViewId, int processId) {
        Log.d(TAG, "---> onCreateView");

        mContext = context;
        mWebView = (WebView) ((Activity) mContext).findViewById(webViewId);
        mProgressBar = (ProgressBar) ((Activity) mContext).findViewById(processId);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.requestFocus();
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);  
        
        mCustomJavaScriptInterface = new CustomJavaScriptInterface();

        initWebView();
        loadDefaultURL();

    }
    
    
    private void initWebView() {

        mWebView.setWebViewClient(new WebViewClient(){  
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.v(TAG, url);

                mProgressBar.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                return true;       
            }

            /* (non-Javadoc)
             * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
                view.loadUrl("javascript:window.APP_TITLE.getAppTitle(app_title)");
                updateLeftButton(view);

                updateCartNumber(url);
                super.onPageFinished(view, url);
            }

            /* (non-Javadoc)
             * @see android.webkit.WebViewClient#onReceivedError(android.webkit.WebView, int, java.lang.String, java.lang.String)
             */
            @Override
            public void onReceivedError(WebView view, int errorCode,
                    String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            /* (non-Javadoc)
             * @see android.webkit.WebViewClient#onReceivedHttpAuthRequest(android.webkit.WebView, android.webkit.HttpAuthHandler, java.lang.String, java.lang.String)
             */
            @Override
            public void onReceivedHttpAuthRequest(WebView view,
                    HttpAuthHandler handler, String host, String realm) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }

            /* (non-Javadoc)
             * @see android.webkit.WebViewClient#onReceivedLoginRequest(android.webkit.WebView, java.lang.String, java.lang.String, java.lang.String)
             */
            @Override
            public void onReceivedLoginRequest(WebView view, String realm,
                    String account, String args) {
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
                return super.onJsConfirm(view, url, message, result);
            }

            /* (non-Javadoc)
             * @see android.webkit.WebChromeClient#onProgressChanged(android.webkit.WebView, int)
             */
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            /* (non-Javadoc)
             * @see android.webkit.WebChromeClient#onReceivedIcon(android.webkit.WebView, android.graphics.Bitmap)
             */
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
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
        
        mWebView.addJavascriptInterface(mCustomJavaScriptInterface, "APP_TITLE");
    }


    private void loadDefaultURL() {
        mCurrentTab = TAB_HOME;
        this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_home));
    }
    
    private void updateUrl(String tab) {
        mCurrentTab = tab;

        if (mCurrentTab.endsWith(TabsFragment.TAB_HOME)) {
            this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_home));
        }
        else if (mCurrentTab.endsWith(TabsFragment.TAB_CATALOGUE)) {
            this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_catalogue));
        }
        else if (mCurrentTab.endsWith(TabsFragment.TAB_PROFILE)) {
            this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_profile));
        }
        else if (mCurrentTab.endsWith(TabsFragment.TAB_CART)) {
            this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_cart));
        }
        else if (mCurrentTab.endsWith(TabsFragment.TAB_MORE)) {
            this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_more));
        }
        else {
            Log.v(TAG, "got tag=" + mCurrentTab);
        }

    }
        
    private void updateCartNumber(String url) {
		int cart_number = getCartNumber(url);
		if (cart_number > 0) {
		    mCustomJavaScriptInterface.getCartNumber(cart_number);
		}
	}

	private int getCartNumber(String url) {
		int cart_number = 0;
		CookieManager cookieManager = CookieManager.getInstance();
		String cookies = cookieManager.getCookie(url);
		if (null != cookies && cookies.contains(CART_NUMBER)) {
			int start = cookies.indexOf(CART_NUMBER) + CART_NUMBER.length() + 1;
			int end = cookies.indexOf(';', start);
			cart_number = Integer.parseInt(cookies.substring(start, end));
			Log.d(TAG, "Have CART_NUMBER=" + cart_number);
		}
		return cart_number;
	}

	private void updateLeftButton(WebView view) {
	    ((UIUpdateInterface) mContext).onLeftButtonUpdate(view);
	}
	
	public class CustomJavaScriptInterface {

        /** retrieve the app title */
    	@JavascriptInterface
        public void getAppTitle(final String title) {
    	    ((UIUpdateInterface) mContext).onTitleUpdate(title);
        }
    	
        /** retrieve the cart number */
        @JavascriptInterface
        public void getCartNumber(final int cartTitle) {
            ((UIUpdateInterface) mContext).onBadgeUpdate(cartTitle);
        }
       
    }
}
