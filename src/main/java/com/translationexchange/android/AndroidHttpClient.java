package com.translationexchange.android;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.translationexchange.core.Application;
import com.translationexchange.core.HttpClient;
import com.translationexchange.core.Tml;
import com.translationexchange.core.Utils;
import com.translationexchange.core.cache.CacheVersion;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    /**
     * Instantiates and return OkHttp Client
     *
     * @return
     */
    protected OkHttpClient getOkHttpClient() {
        if (client == null) {
            client = new OkHttpClient();
            client.setConnectTimeout(5, TimeUnit.SECONDS);
            client.setWriteTimeout(5, TimeUnit.SECONDS);
            client.setReadTimeout(5, TimeUnit.SECONDS);
        }
        return client;
    }

    /**
     * Fetch data from the CDN
     *
     * @param cacheKey
     * @return
     */
    public String getFromCDN(String cacheKey, Map<String, Object> options) throws Exception {
        CacheVersion cacheVersion = Tml.getCache().verifyCacheVersion(getApplication());
        if (cacheVersion.isUnreleased() && !cacheKey.equals("version"))
            return null;

        try {
            String cachePath = cacheKey;

            if (!cacheKey.startsWith(File.separator))
                cachePath = File.separator + cachePath;

            if (cacheKey.equals("version")) {
                cachePath = getCdnPath(cachePath) + ".json";
            } else
                cachePath = getCdnPath(cacheVersion.getVersion() + cachePath) + ".json.gz";

            String response = get(Utils.buildURL(getApplication().getCdnHost(), cachePath), options);

            // check if CDN responded with an error, and return an empty JSON result
            if (response.indexOf("<?xml") != -1) {
                response = cacheKey.equals("version") ? null : "{}";
            }

            return response;
        } catch (Exception ex) {
            Tml.getLogger().error("Failed to get from CDN " + cacheKey + " with error: " + ex.getMessage());
            return cacheKey.equals("version") ? (String) Tml.getCache().fetch(cacheVersion.getVersionKey(), Utils.buildMap("cache_key", CacheVersion.VERSION_KEY)) : null;
        }
    }

    /**
     * @return Application Key
     * @throws Exception
     */
    private String getCdnPath(String path) throws Exception {
        if (!path.startsWith(File.separator))
            path = File.separator + path;

        return getApplication().getKey() + path;
    }

    @Override
    public Map<String, Object> getJSON(String path, Map<String, Object> params, Map<String, Object> options) throws Exception {
        String responseText = null;
        String cacheKey = (String) options.get("cache_key");
        Map<String, Object> result = null;

        CacheVersion cacheVersion = Tml.getCache().verifyCacheVersion(getApplication());

        // load version from server
        if (TmlAndroid.getAuth() != null && TmlAndroid.getAuth().isInlineMode() && !cacheVersion.getVersion().equals("live")) {
            cacheVersion.setVersion("live");
            cacheVersion.markAsUpdated();
//            Tml.getCache().store(cacheVersion.getVersionKey(), cacheVersion.toJSON(), Utils.buildMap());
        } else {
            if (cacheVersion.isExpired()) {
                Tml.getLogger().debug("load version from the server...");
                cacheVersion.updateFromCDN(getFromCDN("version", Utils.buildMap("uncompressed", true)));
            }
        }

        Tml.getLogger().debug("Cache version: " + cacheVersion.getVersion() + " " + cacheVersion.getExpirationMessage());

        // put the current version into options
        options.put(CacheVersion.VERSION_KEY, cacheVersion.getVersion());

        responseText = (String) Tml.getCache().fetch(cacheKey, options);

        if (responseText != null)
            return processJSONResponse(responseText, options);

        // if no data in the local cache
        if (TmlAndroid.getAuth() != null && TmlAndroid.getAuth().isInlineMode()) {
            responseText = get(path, params, options);
        } else {
            responseText = getFromCDN(cacheKey, options);
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
        builder = builder.post(formBody);
        Request request = builder.build();

        Response response = getOkHttpClient().newCall(request).execute();
        if (!response.isSuccessful()) {
            if (response.code() == 401 || response.code() == 403) {
                TmlAndroid.getAndroidApplication().clearAccessCode(true);
            }
            throw new IOException("Unexpected code " + response);
        }
        long t1 = new Date().getTime();

        Tml.getLogger().debug("HTTP Post took: " + (t1 - t0) + " mls");
        String responseText = response.body().string();
        Tml.getLogger().debug("HTTP Post response: " + responseText);
        return responseText;
    }
}
