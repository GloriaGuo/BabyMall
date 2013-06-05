package com.goodbaby.babymall.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

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
	}
	
    public interface UIUpdateInterface {
        public void onTitleUpdate(String title);
        public void onBadgeUpdate(String cartNumber);
        public void onLeftButtonUpdate();
        public void onPhotoBrowserStart(List<String> urls);
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
        Log.d(TAG, "---> onCreateView");
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
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);  
        
        mCustomJavaScriptInterface = new CustomJavaScriptInterface();

        initWebView();
        loadURL();

        return mRoot;
    }
    
    public void updateUrl(String tag) {
        mTag = tag;
        initWebView();
        loadURL();
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
        mWebView.onResume();

        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
    
    private void initWebView() {

        mWebView.setWebViewClient(new WebViewClient(){  
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.v(TAG, "---> shouldOverrideUrlLoading === " + url);

                mProgressBar.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                return true;       
            }

            /* (non-Javadoc)
             * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.v(TAG, "---> onPageFinished");
                mProgressBar.setVisibility(View.GONE);
                view.loadUrl("javascript:window.APP_TITLE.getAppTitle(app_title)");
                view.loadUrl("javascript:window.APP_TITLE.getGalleryList(hhz_gallery)");
                updateLeftButton();

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
        
    @Override
    public void onAttach(Activity activity){
        if (!(activity instanceof UIUpdateInterface)) {
            throw new ClassCastException(activity.toString() + "must implement UIUpdateInterface!");
        }
        super.onAttach(activity);
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

	private void updateLeftButton() {
	    ((UIUpdateInterface)getActivity()).onLeftButtonUpdate();
	}
	
	public class CustomJavaScriptInterface {

        /** retrieve the app title */
    	@JavascriptInterface
        public void getAppTitle(final String title) {
    	    ((UIUpdateInterface)getActivity()).onTitleUpdate(title);
        }
    	
        /** retrieve the cart number */
        @JavascriptInterface
        public void getCartNumber(final int cartTitle) {
            ((UIUpdateInterface)getActivity()).onBadgeUpdate(
                    cartTitle >= 10 ? "N" : String.valueOf(cartTitle));
        }
        
        /** retrieve the image list */
        @JavascriptInterface
        public void getGalleryList(final String gallery) {
            Log.e(TAG, "gallery == " + gallery);
            JSONArray jsonArray;
            List<String> urls = new ArrayList<String>();
            try {
                jsonArray = new JSONArray(gallery);
                if (jsonArray != null) { 
                    int len = jsonArray.length();
                    for (int i=0;i<len;i++){ 
                        urls.add(jsonArray.get(i).toString());
                    } 
                 }
            } catch (JSONException e) {
                Log.e(TAG, "JSONException e: " + e.getMessage());
                return;
            }

            ((UIUpdateInterface)getActivity()).onPhotoBrowserStart(urls);
        }
       
    }
}
