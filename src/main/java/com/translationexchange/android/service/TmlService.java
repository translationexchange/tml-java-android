package com.translationexchange.android.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.translationexchange.android.TmlAndroid;
import com.translationexchange.android.TmlSession;
import com.translationexchange.android.model.Auth;
import com.translationexchange.android.utils.PreferenceUtil;
import com.translationexchange.core.Tml;
import com.translationexchange.core.Utils;
import com.translationexchange.core.cache.Cache;
import com.translationexchange.core.languages.Language;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Observer;

public class TmlService extends IntentService {
    private static final String ACTION_SYNC = "com.translationexchange.android.action.SYNC";
    private static final String ACTION_CHANGE_LANGUAGE = "com.translationexchange.android.action.ACTION_CHANGE_LANGUAGE";

    public TmlService() {
        super("TmlService");
    }

    public static void startSync(Context context) {
        Intent intent = new Intent(context, TmlService.class);
        intent.setAction(ACTION_SYNC);
        context.startService(intent);
    }

    public static void startChangeLanguage(Context context) {
        if (TmlAndroid.getSession() != null) {
            Intent intent = new Intent(context, TmlService.class);
            intent.setAction(ACTION_CHANGE_LANGUAGE);
            context.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(ACTION_SYNC)) {
                actionSync(getApplicationContext());
            } else if (action.equals(ACTION_CHANGE_LANGUAGE)) {
                actionChangeLanguage(getApplicationContext());
            }
        }
    }

    private void actionSync(Context context) {
        try {
            Auth auth = TmlAndroid.getAuth();
            if (auth != null && auth.isInlineMode() && !TextUtils.isEmpty(auth.getAccessToken())) {
                Cache cache = TmlAndroid.getCache();
                cache.delete("live", Utils.buildMap("directory", true));
            }

            Map<String, Object> options = Tml.getConfig().getApplication();
            options.put("sync", true);
            TmlSession tmlSessionOld = TmlAndroid.getSession();
            ArrayList<Observer> o = null;
            if (tmlSessionOld != null) {
                o = tmlSessionOld.getObservers();
            }
            TmlAndroid.setSession(new TmlSession(options));
            if (!TmlAndroid.getAndroidApplication().isLoaded()) {
                TmlAndroid.setSession(tmlSessionOld);
            } else {
                if (o != null) {
                    for (Observer observer : o) {
                        TmlAndroid.getSession().addObserver(observer);
                    }
                }
            }

            actionChangeLanguage(context);
        } finally {
            update();
        }
    }

    private void actionChangeLanguage(Context context) {
        try {
            Locale locale = PreferenceUtil.getCurrentLocation(context);
            TmlAndroid.getLogger().debug("Locale is: " + locale);

            if (TmlAndroid.getAndroidApplication().isLoaded() && TmlAndroid.getAndroidApplication().isSupportedLocale(locale.getLanguage())) {
                Language language = TmlAndroid.getAndroidApplication().getLanguage(locale.getLanguage());
                if (language != null && language.isLoaded()) {
                    TmlAndroid.switchLanguage(language);
                }
            }
        } finally {
            update();
        }
    }

    public static void update() {
        for (final Object o : TmlAndroid.getObjects()) {
            Method[] m = o.getClass().getDeclaredMethods();
            for (final Method method : m) {
                if (method.isAnnotationPresent(com.translationexchange.android.interfaces.TmlAnnotation.class)) {
                    if (o instanceof Activity) {
                        ((Activity) o).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    method.setAccessible(true);
                                    method.invoke(o);
                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    Log.e("TmlAnnotation", e.getMessage());
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}
