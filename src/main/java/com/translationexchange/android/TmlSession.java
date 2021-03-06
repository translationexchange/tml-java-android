package com.translationexchange.android;

import android.os.Handler;
import android.os.Looper;

import com.translationexchange.core.Session;
import com.translationexchange.core.TranslationKey;
import com.translationexchange.core.Utils;
import com.translationexchange.core.cache.CacheVersion;
import com.translationexchange.core.languages.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

/**
 * Created by ababenko on 10/6/16.
 */

public class TmlSession extends Session {

    private TmlApplication application;
    private ArrayList<Observer> observers = new ArrayList<>();

    /**
     * Initializes current application
     *
     * @param options a {@link Map} object.
     */
    public TmlSession(Map<String, Object> options) {
        super(options);
    }

    @Override
    public void init(Map<String, Object> options, Map<String, Object> applicationParams) {
        try {
            boolean sync = options.containsKey("sync") && (Boolean) options.get("sync");
            setApplication(new TmlApplication(options));
            getApplication().setSession(this);
            if (sync) {
                getApplication().load(Utils.map());
                if (getApplication().isLoaded()) {
                    setCurrentLocale(getApplication().getFirstAcceptedLocale((String) options.get("locale")));
                }
            } else {
                String cacheVersion = (String) options.get(CacheVersion.VERSION_KEY);
                getApplication().loadLocal(cacheVersion);
                setCurrentLocale(getApplication().getFirstAcceptedLocale((String) options.get("locale")), cacheVersion);
            }

        } catch (Exception ex) {
            Tml.getLogger().logException("Failed to load application. Therefore session could not be loaded", ex);
        }
        Tml.getConfig().setDecorator("html");
    }

    /**
     * Sets current language in the singleton instance
     *
     * @param locale a {@link String} object.
     */
    private void setCurrentLocale(String locale, String cacheVersion) {
        setCurrentLanguage(getApplication().getLanguageLocal(locale, cacheVersion));
    }

    @Override
    public TmlApplication getApplication() {
        return application;
    }

    public void setApplication(TmlApplication application) {
        this.application = application;
    }

    void switchLanguageLocal(Language language, Map<String, Object> options) {
//        if (getCurrentLanguage().equals(language))
//            return;

        language = getApplication().getLanguage(language.getLocale());
        setCurrentLanguage(language);

        getApplication().resetTranslations();
        getApplication().loadTranslationsLocal(language, (String) options.get(CacheVersion.VERSION_KEY));

        Tml.initSource("index", language.getLocale(), options);

        setChanged();
        notifyObservers(language);
    }

    /**
     * <p>switchLanguage.</p>
     *
     * @param l a {@link com.translationexchange.core.languages.Language} object.
     */
    public void switchLanguage(Language l) {
//        if (getCurrentLanguage().equals(language))
//            return;

        final Language language = getApplication().getLanguage(l.getLocale());
        setCurrentLanguage(language);

        getApplication().resetTranslations();
        getApplication().loadTranslations(language);

        Tml.initSource("index", language.getLocale());

        setChanged();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyObservers(language);
            }
        });

    }

    /**
     * <p>tr.</p>
     *
     * @param label       a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param tokens      a {@link java.util.Map} object.
     * @param options     a {@link java.util.Map} object.
     * @return a {@link java.lang.String} object.
     */
    public String translate(String label, String description, Map<String, Object> tokens, Map<String, Object> options) {
        if (options == null)
            options = new HashMap<String, Object>();
        options.put(Session.SESSION_KEY, this);
        return (String) getCurrentLanguage().translateLocal(label, description, tokens, options);
    }

    /**
     * <p>translateStyledString.</p>
     *
     * @param label       a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param tokens      a {@link java.util.Map} object.
     * @param options     a {@link java.util.Map} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object translateStyledString(String label, String description, Map<String, Object> tokens, Map<String, Object> options) {
        if (options == null)
            options = new HashMap<String, Object>();

        options.put(Session.SESSION_KEY, this);
        options.put(TranslationKey.TOKENIZER_KEY, TranslationKey.DEFAULT_TOKENIZERS_STYLED);
        return getCurrentLanguage().translateLocal(label, description, tokens, options);
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    public ArrayList<Observer> getObservers() {
        return observers;
    }

    public void update(){
        setChanged();
        notifyObservers();
    }
}
