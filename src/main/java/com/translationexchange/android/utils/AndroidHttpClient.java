package com.translationexchange.android.utils;

import com.translationexchange.core.Application;
import com.translationexchange.core.HttpClient;
import com.translationexchange.core.Tml;
import com.translationexchange.core.Utils;
import com.translationexchange.core.cache.CacheVersion;

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
}
