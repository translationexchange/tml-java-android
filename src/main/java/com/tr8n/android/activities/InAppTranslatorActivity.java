package com.tr8n.android.activities;

import com.tr8n.core.Tr8n;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.LinearLayout;

@SuppressLint("SetJavaScriptEnabled")
public class InAppTranslatorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        // Define the LinearLayout's characteristics
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.WHITE);
        
        WebView webView = new WebView(this);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(Tr8n.getSession().getApplication().getHost() + "/mobile/login");
		
		layout.addView(webView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(layout);
    }
  
  
}
