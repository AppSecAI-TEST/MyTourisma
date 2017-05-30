package com.ftl.tourisma;

/**
 * Created by fipl11111 on 11-Mar-16.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import static android.view.Window.FEATURE_NO_TITLE;

public class TwitterDialog extends Activity {

    static final float[] DIMENSIONS_DIFF_LANDSCAPE = {10, 40};
    static final float[] DIMENSIONS_DIFF_PORTRAIT = {20, 30};
    static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;
    static final String DISPLAY_STRING = "touch";
    static final String FB_ICON = "icon.png";
    String urlTwitter;
    int w, h;
    int titleHeight, buttonHeight;
    SharedPreferences pref;
    private boolean loadingFinished = true;
    private boolean redirect = false;
    private String mUrl, title;
    private ProgressDialog mSpinner;
    private WebView mWebView;
    private ScrollView mContent;
    private LinearLayout linearLayout;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlTwitter = "https://twitter.com/share?text=" + getIntent().getStringExtra("msg");
        Display d = getWindowManager().getDefaultDisplay();
        w = d.getWidth();
        h = d.getHeight();

        titleHeight = 30;
        buttonHeight = 50;

        mSpinner = new ProgressDialog(this);
        mSpinner.requestWindowFeature(FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        mContent = new ScrollView(this);
        mContent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setScrollView();
        setUpTitle();
        setUpWebView();
        setContentView(mContent);
    }

    private void setScrollView() {
        linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setClipToPadding(false);
        mContent.addView(linearLayout);
    }

    private void setUpTitle() {
        requestWindowFeature(FEATURE_NO_TITLE);
        mTitle = new TextView(this);
        mTitle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, titleHeight));
        mTitle.setText(title);
        mTitle.setTextColor(Color.parseColor("#38B0DE"));
        mTitle.setTypeface(null, Typeface.BOLD);
        mTitle.setBackgroundColor(Color.TRANSPARENT);
        linearLayout.addView(mTitle);
    }

    private void setUpWebView() {
        mWebView = new WebView(this);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        mWebView.loadUrl(urlTwitter);
        mWebView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, h - titleHeight - buttonHeight - 40));
        linearLayout.addView(mWebView);
        TextView cancelButton = new TextView(this);
        cancelButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        cancelButton.setGravity(Gravity.CENTER);
        cancelButton.setBackgroundResource(R.drawable.signup_btn);
        cancelButton.setText("Cancel");
        cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        cancelButton.setTextColor(Color.WHITE);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                setResult(100);
                TwitterDialog.this.finish();
            }
        });
        linearLayout.addView(cancelButton);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!loadingFinished) {
                redirect = true;
            }
            loadingFinished = false;
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            loadingFinished = false;
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // do what you want to do

            if (!redirect) {
                loadingFinished = true;
            }
            if (loadingFinished && !redirect) {
                // HIDE LOADING IT HAS FINISHED
                if (mSpinner != null) {
                    if (mSpinner.isShowing()) {
                        mSpinner.dismiss();
                    }
                }
            } else {
                redirect = false;
            }
        }
    }
}