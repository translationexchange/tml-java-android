package com.translationexchange.android.interfaces;

import android.webkit.JavascriptInterface;

import com.translationexchange.android.TmlAndroid;

/**
 * Created by ababenko on 9/6/16.
 */
public class WebAppInterface {
    @JavascriptInterface
    public void postMessage(String message) {
        TmlAndroid.getLogger().info("WebAppInterface", message);
    }
}
