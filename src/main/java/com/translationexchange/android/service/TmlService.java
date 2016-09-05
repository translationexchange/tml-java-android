package com.translationexchange.android.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

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

    public static void startInit(Context context, TmlMode tmlMode, String zip) {
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
        intent.putExtra("zip", zip);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(ACTION_INIT)) {
                actionInit(getApplicationContext(), intent.getStringExtra("zip"));
            }
        }
    }

    private static void actionInit(Context context, String zipVersion) {
        try {
            if (!TextUtils.isEmpty(zipVersion) && !TmlAndroid.hasCachedVersion(zipVersion)) {
                Cache cache = TmlAndroid.getCache();
                if (cache != null && cache instanceof FileCache) {
                    FileCache fileCache = ((FileCache) cache);
                    Decompress.unzipFromRes(context, zipVersion, fileCache.getCachePath());
                }
            }

            TmlAndroid.setSession(new Session());

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
            update();
        }
    }

    private static void update() {
        for (final Object o : TmlAndroid.getObjects()) {
            Method[] m = o.getClass().getDeclaredMethods();
            for (final Method method : m) {
                if (method.isAnnotationPresent(com.translationexchange.android.interfaces.TmlAnnotation.class)) {
                    if (o instanceof Activity) {
                        ((Activity) o).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    method.invoke(o);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}
