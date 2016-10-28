package com.translationexchange.android;

import com.translationexchange.core.Source;
import com.translationexchange.core.Utils;
import com.translationexchange.core.cache.CacheVersion;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ababenko on 10/7/16.
 */

public class TmlSource extends Source {

    public TmlSource(Map<String, Object> stringObjectMap) {
        super(stringObjectMap);
    }

    /**
     * Creates cache key for source
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String getCacheKey() {
        return getLocale() + File.separator + "translations";
    }

    /**
     * Loading source from service
     *
     * @param options a {@link java.util.Map} object.
     */
    public void load(Map<String, Object> options) {
        try {
            if (options == null)
                options = new HashMap<String, Object>();
            if (!options.containsKey("dry") || !Boolean.valueOf((String) options.get("dry"))) {
                options.put("cache_key", getCacheKey());
                updateTranslationKeys(getApplication().getHttpClient().getJSONMap("sources/" + this.generateMD5Key() + "/translations", Utils.map("app_id", getApplication().getKey(), "all", "true", "locale", getLocale()), options));
            }
            setLoaded(true);
        } catch (Exception ex) {
            setLoaded(false);
            Tml.getLogger().logException("Failed to load source", ex);
        }
    }

    /**
     * Loading source from service
     *
     * @param cacheVersion a {@link String} object.
     */
    public void loadLocal(String cacheVersion) {
        try {
            updateTranslationKeys(getApplication().getHttpClient().getJSONMap(Utils.map("cache_key", getCacheKey(), CacheVersion.VERSION_KEY, cacheVersion)));
            setLoaded(true);
        } catch (Exception ex) {
            setLoaded(false);
            Tml.getLogger().logException("Failed to load source", ex);
        }
    }
}
