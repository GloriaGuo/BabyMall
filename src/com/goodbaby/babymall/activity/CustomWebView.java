package com.goodbaby.babymall.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.goodbaby.babymall.BabyMallApplication;
import com.goodbaby.babymall.R;

public class CustomWebView {

	private static final String TAG = BabyMallApplication.getApplicationTag()
	        + CustomWebView.class.getSimpleName();

	private Context mContext;
    private WebView mWebView;
	private String mCurrentTab;
	private CustomJavaScriptInterface mCustomJavaScriptInterface;
	private String mClickedUrl;
	
    public final String TAB_HOME = "home";
    public final String TAB_CATALOGUE = "catalogue";
    public final String TAB_PROFILE = "profile";
    public final String TAB_CART = "cart";
    public final String TAB_MORE = "more";
    
    public final static String TITLE_BUTTON_SUBMIT_ORDER = "submit";
    public final static String TITLE_BUTTON_PAY = "pay";
    public final static String TITLE_BUTTON_SETTLE = "settle";
    public final static String TITLE_BUTTON_ADD_ADDR = "add_addr";
    
	public CustomWebView() {
	}
	
    public interface UIUpdateInterface {
        public void onTitleUpdate(String title);
        public void onPhotoBrowserStart(String urls, String clickedUrl);
        public void onWebPageStart();
        public void onWebPageFinished(String url);
    }
    
    public void init(Context context, int webViewId) {
        Log.d(TAG, "---> onCreateView");

        mContext = context;
        mWebView = (WebView) ((Activity) mContext).findViewById(webViewId);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.requestFocus();
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);  
        
        mCustomJavaScriptInterface = new CustomJavaScriptInterface();

        initWebView();
        loadDefaultURL();
    }  
    
    public WebView getWebView() {
        return mWebView;
    }
    
    private void initWebView() {

        mWebView.setWebViewClient(new WebViewClient(){  
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "---> shouldOverrideUrlLoading url == " + url);

                if (MimeTypeMap.getFileExtensionFromUrl(url).equalsIgnoreCase("jpg") ||
                    MimeTypeMap.getFileExtensionFromUrl(url).equalsIgnoreCase("png")) {
                    mClickedUrl = url;
                    view.loadUrl("javascript:window.APP_TITLE.getGalleryList(hhz_gallery)");
                } else {
                    view.loadUrl(url);
                }
                
                return true;       
            }
            
            /* (non-Javadoc)
             * @see android.webkit.WebViewClient#onPageStarted(android.webkit.WebView, java.lang.String)
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                ((UIUpdateInterface) mContext).onWebPageStart();
            }

            /* (non-Javadoc)
             * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.APP_TITLE.getAppTitle(app_title)");
                ((UIUpdateInterface) mContext).onWebPageFinished(url);
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
                    final JsResult result) {
                Builder builder = new Builder(mContext).
                        setMessage(message).
                        setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();   
                            }
                        }).
                        setCancelable(false);
                builder.create().show();
                return true;
            }

            /* (non-Javadoc)
             * @see android.webkit.WebChromeClient#onJsConfirm(android.webkit.WebView, java.lang.String, java.lang.String, android.webkit.JsResult)
             */
            @Override
            public boolean onJsConfirm(WebView view, String url,
                    String message, final JsResult result) {
                Builder builder = new Builder(mContext).
                        setMessage(message).
                        setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        }).
                        setNeutralButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        }).
                        setCancelable(false);
                builder.create().show();
                return true;
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
        this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_home_url));
    }
    
    public void updateUrl(String tab) {
        mCurrentTab = tab;

        if (mCurrentTab.endsWith(TAB_HOME)) {
            this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_home_url));
        }
        else if (mCurrentTab.endsWith(TAB_CATALOGUE)) {
            this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_category_url));
        }
        else if (mCurrentTab.endsWith(TAB_PROFILE)) {
            this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_member_url));
        }
        else if (mCurrentTab.endsWith(TAB_CART)) {
            this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_cart_url));
        }
        else if (mCurrentTab.endsWith(TAB_MORE)) {
            this.mWebView.loadUrl(mContext.getResources().getString(R.string.tab_more_url));
        }
        else {
            Log.v(TAG, "got tag=" + mCurrentTab);
        }

    }
    
    public class CustomJavaScriptInterface {

        /** retrieve the app title */
    	@JavascriptInterface
        public void getAppTitle(final String title) {
    	    ((UIUpdateInterface) mContext).onTitleUpdate(title);
        }
    	
        /** retrieve the image list */
        @JavascriptInterface
        public void getGalleryList(final String gallery) {
            ((UIUpdateInterface)mContext).onPhotoBrowserStart(gallery, mClickedUrl);
        }
       
    }
    
    public void handleButtonSubmit() {
        mWebView.loadUrl(mContext.getString(R.string.submit_url));
    }
    
    public void handleButtonPay() {
        
    }
    
    public void handleButtonCheckout() {
        mWebView.loadUrl(mContext.getString(R.string.checkout_url));
    }
    
    public void handleButtonAddReceiber() {
        mWebView.loadUrl(mContext.getString(R.string.add_receiver_url));
    }
}
