package com.translationexchange.android.utils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.translationexchange.core.Application;
import com.translationexchange.core.HttpClient;
import com.translationexchange.core.Tml;
import com.translationexchange.core.Utils;
import com.translationexchange.core.cache.CacheVersion;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ababenko on 9/8/16.
 */
public class AndroidHttpClient extends HttpClient {
    /**
     * Default constructor
     *
     * @param application a {@link Application} object.
     */
    public AndroidHttpClient(Application application) {
        super(application);
    }

    @Override
    public Map<String, Object> getJSON(String path, Map<String, Object> params, Map<String, Object> options) throws Exception {
        String responseText = null;
        String cacheKey = (String) options.get("cache_key");
        Map<String, Object> result = null;

        CacheVersion cacheVersion = Tml.getCache().verifyCacheVersion(getApplication());

        // put the current version into options
        options.put(CacheVersion.VERSION_KEY, cacheVersion.getVersion());

        responseText = (String) Tml.getCache().fetch(cacheKey, options);
        if (responseText != null)
            return processJSONResponse(responseText, options);

        // if no data in the local cache
        switch (Tml.getConfig().getTmlMode()) {
            case API_LIVE:
                responseText = get(path, params, options);
                break;
            case CDN:
            case NONE:
                responseText = getFromCDN(cacheKey, options);
                break;
        }

        if (responseText == null)
            return null;

        result = processJSONResponse(responseText, options);

        Map<String, Object> extensions = (Map<String, Object>) result.get(EXTENSIONS_KEY);

        // never store extension in cache
        if (extensions != null) {
            result.remove(EXTENSIONS_KEY);
            responseText = Utils.buildJSON(result);
            result.put(EXTENSIONS_KEY, extensions);
        }

        Tml.getCache().store(cacheKey, responseText, options);
        return result;
    }

    @Override
    public Object post(String path, Map<String, Object> params, Map<String, Object> options) throws Exception {
        if (getAccessToken() == null) {
            throw new IOException("Unauthorized");
        }
        URL url = Utils.buildURL(getApplication().getHost(), API_PATH + path, Utils.buildMap("access_token", getAccessToken()));

        Tml.getLogger().debug("HTTP Post: " + url.toString());
        Tml.getLogger().debug("HTTP Params: " + params.toString());

        long t0 = new Date().getTime();

        FormEncodingBuilder formBuilder = new FormEncodingBuilder();

        Iterator entries = params.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            formBuilder = formBuilder.add((String) entry.getKey(), (String) entry.getValue());
        }
        RequestBody formBody = formBuilder.build();

        Request.Builder builder = new Request.Builder()
                .url(url.toString())
                .addHeader("User-Agent", Tml.getFullVersion());
//                .addHeader("Authorization", "Access-Token " + getAccessToken());
        builder = builder.post(formBody);
        Request request = builder.build();

        Response response = getOkHttpClient().newCall(request).execute();
        if (!response.isSuccessful()) {
            if (response.code() == 401 || response.code() == 403) {
                clearAccessCode();
            }
            throw new IOException("Unexpected code " + response);
        }
//        String responseStr = response.body().string();
//        Tml.getLogger().debug(responseStr);

        long t1 = new Date().getTime();

        Tml.getLogger().debug("HTTP Post took: " + (t1 - t0) + " mls");
        String responseText = response.body().string();
        Tml.getLogger().debug("HTTP Post response: " + responseText);
        return responseText;
    }
}
