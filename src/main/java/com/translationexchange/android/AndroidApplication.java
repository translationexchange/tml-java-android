package com.translationexchange.android;

import com.translationexchange.android.model.Auth;
import com.translationexchange.android.utils.AndroidHttpClient;
import com.translationexchange.core.Application;
import com.translationexchange.core.HttpClient;

import java.util.Map;

/**
 * Created by ababenko on 9/7/16.
 */
public class AndroidApplication extends Application {

    private AndroidHttpClient httpClient;

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
            Auth auth = Auth.getAuth();
            if (auth != null) {
                super.setAccessToken(auth.getAccessToken());
            }
        }
        return super.getAccessToken();
    }

    @Override
    public HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new AndroidHttpClient(this);
        }
        return httpClient;
    }
}
