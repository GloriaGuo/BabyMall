package com.goodbaby.babymall.activity;

import android.app.Activity;
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

public class WebFragment extends Fragment {

	private static final String TAG = BabyMallApplication.getApplicationTag()
	        + WebFragment.class.getSimpleName();

	private static final String CART_NUMBER = "S[CART_NUMBER]";
    private View mRoot;
    private WebView mWebView;
    private ProgressBar mProgressBar;
	private String mTag;
	private LayoutInflater mInflater;
	private CustomJavaScriptInterface mCustomJavaScriptInterface;

	public WebFragment() {
	}

	public WebFragment(String tag) {
		mTag = tag;

		Log.d(TAG, "Constructor: tag=" + tag);
	}

    public interface UIUpdateInterface {
        public void onTitleUpdate(String title);
        public void onBadgeUpdate(int cartNumber);
    }
    
	/* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        mWebView = (WebView) mRoot.findViewById(R.id.wv);
        mProgressBar = (ProgressBar) mRoot.findViewById(R.id.wv_progress);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.requestFocus();
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setAppCacheEnabled(false);
        
        mCustomJavaScriptInterface = new CustomJavaScriptInterface();

        initWebView();
        loadURL();
        
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

	}

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
    }


    private void initWebView() {

        mWebView.setWebViewClient(new WebViewClient(){       
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
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("javascript:window.APP_TITLE.getAppTitle(app_title)");
                
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

    private void loadURL() {

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

    }
    
    UIUpdateInterface mUIUpdateInterface;
    
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mUIUpdateInterface = (UIUpdateInterface)activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement OnArticleSelectedListener");
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
//		Log.v(TAG, "COOKIES:" + cookieManager.getCookie(url));
		if (null != cookies && cookies.contains(CART_NUMBER)) {
			int start = cookies.indexOf(CART_NUMBER) + CART_NUMBER.length() + 1;
			int end = cookies.indexOf(';', start);
			cart_number = Integer.parseInt(cookies.substring(start, end));
			Log.v(TAG, "Have CART_NUMBER=" + cart_number);
		}
		return cart_number;
	}

	public class CustomJavaScriptInterface {

//        Context mContext;
//
//        /** Instantiate the interface and set the context */
//        CustomJavaScriptInterface(Context c) {
//            mContext = c;
//        }
        /** retrieve the app title */
    	@JavascriptInterface
        public void getAppTitle(final String title) {
            mUIUpdateInterface.onTitleUpdate(title);
        }
        /** retrieve the cart number */
        @JavascriptInterface
        public void getCartNumber(final int cartTitle) {
            mUIUpdateInterface.onBadgeUpdate(cartTitle);
        }
    }
}
