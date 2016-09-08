package com.translationexchange.android;

import android.content.Context;
import android.content.Intent;

import com.translationexchange.android.activities.InAppTranslatorActivity;
import com.translationexchange.android.model.Auth;
import com.translationexchange.android.utils.AndroidHttpClient;
import com.translationexchange.core.Application;
import com.translationexchange.core.HttpClient;
import com.translationexchange.core.Utils;

import java.util.Map;

/**
 * Created by ababenko on 9/7/16.
 */
public class AndroidApplication extends Application {

    private AndroidHttpClient httpClient;
    private Auth auth;

    /**
     * Default constructor
     */
    public AndroidApplication() {
        super();
    }

    /**
     * <p>Constructor for Application.</p>
     *
     * @param attributes a {@link Map} object.
     */
    public AndroidApplication(Map<String, Object> attributes) {
        super(attributes);
    }

    /**
     * <p>Getter for the field <code>accessToken</code>.</p>
     *
     * @return a {@link String} object.
     */
    @Override
    public String getAccessToken() {
        if (super.getAccessToken() == null) {
            auth = Auth.getAuth();
            if (auth != null) {
                super.setAccessToken(auth.getAccessToken());
            }
        }
        if (auth != null && auth.isExpired()) {
            clearAccessCode();
        }
        return super.getAccessToken();
    }

    @Override
    public void clearAccessCode() {
        super.clearAccessCode();
        auth = null;
        super.setAccessToken(null);
        TmlAndroid.getCache().delete("auth", Utils.buildMap());
        if (!TmlAndroid.getObjects().isEmpty()) {
            Object o = TmlAndroid.getObjects().get(0);
            if (o instanceof Context) {
                Context context = (Context) o;
                context.startActivity(new Intent(context, InAppTranslatorActivity.class));
            }
        }
    }

    @Override
    public HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new AndroidHttpClient(this);
        }
        return httpClient;
    }
}
