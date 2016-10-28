package com.translationexchange.android;

import com.translationexchange.core.Application;
import com.translationexchange.core.HttpClient;
import com.translationexchange.core.Source;
import com.translationexchange.core.TranslationKey;
import com.translationexchange.core.Utils;
import com.translationexchange.core.cache.CacheVersion;
import com.translationexchange.core.languages.Language;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ababenko on 9/7/16.
 */
public class TmlApplication extends Application {

    private TmlHttpClient httpClient;

    /**
     * Default constructor
     */
    public TmlApplication() {
        super();
    }

    /**
     * <p>Constructor for Application.</p>
     *
     * @param attributes a {@link Map} object.
     */
    public TmlApplication(Map<String, Object> attributes) {
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
            if (Tml.getAuth() != null) {
                super.setAccessToken(Tml.getAuth().getAccessToken());
            }
        }
        return super.getAccessToken();
    }

    @Override
    public HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new TmlHttpClient(this);
        }
        return httpClient;
    }

    /**
     * Submits missing translations keys to the server
     */
    public synchronized void submitMissingTranslationKeys() {
        if (getMissingTranslationKeysBySources().size() == 0 || Tml.getAuth() == null || !Tml.getAuth().isInlineMode())
            return;
//        if (!isKeyRegistrationEnabled() || getMissingTranslationKeysBySources().size() == 0)
//            return;

        Tml.getLogger().debug("Submitting missing translation keys...");

        List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();

        List<String> sourceKeys = new ArrayList<String>();

        Iterator<Map.Entry<String, Map<String, TranslationKey>>> entries = missingTranslationKeysBySources.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Map<String, TranslationKey>> entry = entries.next();
            String source = entry.getKey();

            if (!sourceKeys.contains(source))
                sourceKeys.add(source);

            Map<String, TranslationKey> translationKeys = entry.getValue();
            List<Object> keys = new ArrayList<Object>();

            for (Object object : translationKeys.values()) {
                TranslationKey translationKey = (TranslationKey) object;
                keys.add(translationKey.toMap());
            }

            params.add(Utils.map("source", source, "keys", keys));
        }

        registerKeys(Utils.map("source_keys", Utils.buildJSON(params), "app_id", getKey()));

        this.missingTranslationKeysBySources.clear();
    }

    public void loadLocal(String cacheVersion) {
        try {
            Map<String, Object> data = getHttpClient().getJSONMap(Utils.map("cache_key", "application", CacheVersion.VERSION_KEY, cacheVersion));
            if (data == null || data.isEmpty()) {
                setDefaultLocale(Tml.getConfig().getDefaultLocale());
                addLanguage(Tml.getConfig().getDefaultLanguage());
                Tml.getLogger().debug("No release has been published or no cache has been provided");
                setLoaded(false);
            } else {
                updateAttributes(data);
                setLoaded(true);
            }
        } catch (Exception ex) {
            setLoaded(false);
            addLanguage(Tml.getConfig().getDefaultLanguage());
            Tml.getLogger().logException("Failed to load application", ex);
        }
    }

    /**
     * Loads application from the service with extra parameters
     *
     * @param params Options for loading application
     */
    public void load(Map<String, Object> params) {
        try {
            Tml.getLogger().debug("Loading application...");
            Map<String, Object> data = getHttpClient().getJSONMap("projects/" + getKey() + "/definition",
                    params,
                    Utils.map("cache_key", "application")
            );
            if (data == null || data.isEmpty()) {
                setDefaultLocale(Tml.getConfig().getDefaultLocale());
                addLanguage(Tml.getConfig().getDefaultLanguage());
                Tml.getLogger().debug("No release has been published or no cache has been provided");
                setLoaded(false);
            } else {
                updateAttributes(data);
                setLoaded(true);
            }
        } catch (Exception ex) {
            setLoaded(false);
            addLanguage(Tml.getConfig().getDefaultLanguage());
            Tml.getLogger().logException("Failed to load application", ex);
        }
    }

    /**
     * <p>getLanguage.</p>
     *
     * @param locale a {@link java.lang.String} object.
     * @return a {@link com.translationexchange.core.languages.Language} object.
     */
    public Language getLanguageLocal(String locale, String cacheVersion) {
        if (getLanguagesByLocale().get(locale) == null) {
            getLanguagesByLocale().put(locale, new Language(Utils.map("application", this, "locale", locale)));
        }

        Language language = getLanguagesByLocale().get(locale);
        if (!language.hasDefinition()) {
            language.loadLocal(cacheVersion);
        }
        return language;
    }

    /**
     * Get source with translations for a specific locale
     *
     * @param key     a {@link java.lang.String} object.
     * @param locale  a {@link java.lang.String} object.
     * @param options a {@link java.util.Map} object.
     * @return a {@link com.translationexchange.core.Source} object.
     */
    public Source getSource(String key, String locale, Map<String, Object> options) {
        if (getSourcesByKeys().get(key) == null) {
            TmlSource source = new TmlSource(Utils.map("application", this, "key", key, "locale", locale));
            if (options == null) {
                source.load(null);
            } else {
                source.loadLocal((String) options.get(CacheVersion.VERSION_KEY));
            }
            getSourcesByKeys().put(key, source);
        }

        return getSourcesByKeys().get(key);
    }
}
