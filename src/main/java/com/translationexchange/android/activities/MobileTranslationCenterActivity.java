/**
 * Copyright (c) 2015 Translation Exchange, Inc. All rights reserved.
 * <p/>
 * _______                  _       _   _             ______          _
 * |__   __|                | |     | | (_)           |  ____|        | |
 * | |_ __ __ _ _ __  ___| | __ _| |_ _  ___  _ __ | |__  __  _____| |__   __ _ _ __   __ _  ___
 * | | '__/ _` | '_ \/ __| |/ _` | __| |/ _ \| '_ \|  __| \ \/ / __| '_ \ / _` | '_ \ / _` |/ _ \
 * | | | | (_| | | | \__ \ | (_| | |_| | (_) | | | | |____ >  < (__| | | | (_| | | | | (_| |  __/
 * |_|_|  \__,_|_| |_|___/_|\__,_|\__|_|\___/|_| |_|______/_/\_\___|_| |_|\__,_|_| |_|\__, |\___|
 * __/ |
 * |___/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.translationexchange.android.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.translationexchange.android.R;
import com.translationexchange.android.TmlAndroid;
import com.translationexchange.android.model.MTC;
import com.translationexchange.android.service.TmlService;
import com.translationexchange.core.TranslationKey;

@SuppressLint("SetJavaScriptEnabled")
public class MobileTranslationCenterActivity extends BaseActivity {

    public static void translate(Context context, String url) {
        context.startActivity(new Intent(context, MobileTranslationCenterActivity.class).putExtra("url", url));
    }

    private ProgressBar progressBar;

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        enableBackButton(true);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.addJavascriptInterface(new WebAppInterface(this), "tmlMessageHandler");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                TmlAndroid.getLogger().debug("web_Started", url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                TmlAndroid.getLogger().debug("web_Finished", url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });

        String url = getIntent().getStringExtra("url");
        TmlAndroid.getLogger().info("web_translate_url", url);
        webView.loadUrl(url);
    }

    public static class WebAppInterface {
        private Activity activity;

        WebAppInterface(Activity activity) {
            this.activity = activity;
        }

        @JavascriptInterface
        public void postMessage(String text) {
            byte[] dataDecoded = Base64.decode(text, Base64.DEFAULT);
            String message = new String(dataDecoded);
            Gson gson = new Gson();
            TmlAndroid.getLogger().info("web_postMessage", message);
            MTC mtc = gson.fromJson(message, MTC.class);
            if (mtc != null && !TextUtils.isEmpty(mtc.getAction())) {
                if (mtc.getAction().equals("update")) {
                    if (TmlAndroid.getAndroidApplication() != null) {
                        TranslationKey translationKey = TmlAndroid.getAndroidApplication().getTranslationKey(mtc.getTranslationKey());
                        translationKey.setLabel(mtc.getTranslation());
                        TmlService.update();
                    }
                } else if (mtc.getAction().equals("next")) {
                    activity.finish();
                }
            } else {
                activity.finish();
            }
        }
    }

}
