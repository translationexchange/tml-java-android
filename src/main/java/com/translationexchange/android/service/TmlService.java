package com.translationexchange.android.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.translationexchange.android.TmlAndroid;
import com.translationexchange.android.cache.FileCache;
import com.translationexchange.android.logger.Logger;
import com.translationexchange.android.tokenizers.SpannableStringTokenizer;
import com.translationexchange.android.utils.Decompress;
import com.translationexchange.android.utils.FileUtils;
import com.translationexchange.core.Session;
import com.translationexchange.core.TmlMode;
import com.translationexchange.core.TranslationKey;
import com.translationexchange.core.Utils;
import com.translationexchange.core.cache.Cache;
import com.translationexchange.core.languages.Language;

import java.lang.reflect.Method;
import java.util.Locale;

public class TmlService extends IntentService {
    private static final String ACTION_INIT = "com.translationexchange.android.action.INIT";

    public TmlService() {
        super("TmlService");
    }

    public static void startInit(Context context, TmlMode tmlMode) {
        TmlAndroid.getConfig().setLogger(Utils.buildMap(
                "class", Logger.class.getName()
        ));

        TmlAndroid.getConfig().setCache(Utils.buildMap(
                "enabled", true,
                "class", FileCache.class.getName(),
                "cache_dir", FileUtils.getBaseDirectory(context)
        ));

        TmlAndroid.getConfig().setAndroidApp(true);
        TmlAndroid.getConfig().setTmlMode(tmlMode);
        TmlAndroid.getConfig().addTokenizerClass(TranslationKey.DEFAULT_TOKENIZERS_STYLED, SpannableStringTokenizer.class.getName());
        TmlAndroid.addObject(context);
        Intent intent = new Intent(context, TmlService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(ACTION_INIT)) {
                actionInit(getApplicationContext());
            }
        }
    }

    private static void actionInit(Context context) {
        try {
            if (!TmlAndroid.hasCachedVersion("20160825154951")) {
                Cache cache = TmlAndroid.getCache();
                if (cache != null && cache instanceof FileCache) {
                    FileCache fileCache = ((FileCache) cache);
                    Decompress.unzipFromRes(context, "20160825154951", fileCache.getCachePath());
                }
            }

            TmlAndroid.setSession(new Session(TmlAndroid.getConfig().getApplication()));

            Locale locale = new Locale("ru", "ua");
            TmlAndroid.getLogger().debug("System locale: " + locale);

            if (!TmlAndroid.getApplication().isSupportedLocale(locale.getLanguage()))
                return;

            Language language = TmlAndroid.getApplication().getLanguage(locale.getLanguage());
            if (language != null && language.isLoaded()) {
                TmlAndroid.switchLanguage(language);
                TmlAndroid.initSource("index", language.getLocale());
            }
        } finally {
            try {
                for (Object o : TmlAndroid.getObjects()) {
                    Method[] m = o.getClass().getDeclaredMethods();
                    for (Method method : m) {
                        if (method.isAnnotationPresent(com.translationexchange.android.interfaces.TmlAnnotation.class)) {
                            method.invoke(o);
                        }
                    }
                }

                TmlAndroid.getObjects().clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
