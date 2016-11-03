/**
 * Copyright (c) 2015 Translation Exchange, Inc. All rights reserved.
 * <p/>
 * _______                  _       _   _             ______          _
 * |__   __|                | |     | | (_)           |  ____|        | |
 * | |_ __ __ _ _ __  ___| | __ _| |_ _  ___  _ __ | |__  __  _____| |__   __ _ _ __   __ _  ___
 * | | '__/ _` | '_ \/ __| |/ _` | __| |/ _ \| '_ \|  __| \ \/ / __| '_ \ / _` | '_ \ / _` |/ _ \
 * | | | | (_| | | | \__ \ | (_| | |_| | (_) | | | | |____ >  < (__| | | | (_| | | | | (_| |  __/
 * |_|_|  \__,_|_| |_|___/_|\__,_|\__|_|\___/|_| |_|______/_/\_\___|_| |_|\__,_|_| |_|\__, |\___|
 * __/ |
 * |___/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.translationexchange.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.translationexchange.android.activities.TmlAndroidActivity;
import com.translationexchange.android.cache.FileCache;
import com.translationexchange.android.cache.TmlCacheVersion;
import com.translationexchange.android.logger.Logger;
import com.translationexchange.android.model.Auth;
import com.translationexchange.android.service.TmlService;
import com.translationexchange.android.tokenizers.SpannableStringTokenizer;
import com.translationexchange.android.utils.Decompress;
import com.translationexchange.android.utils.FileUtils;
import com.translationexchange.android.utils.PreferenceUtil;
import com.translationexchange.android.utils.ViewUtils;
import com.translationexchange.core.TranslationKey;
import com.translationexchange.core.Utils;
import com.translationexchange.core.cache.Cache;
import com.translationexchange.core.cache.CacheVersion;
import com.translationexchange.core.languages.Language;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class Tml extends com.translationexchange.core.Tml {

    private static List<Object> objects;
    private static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;
    private static Auth auth;
    private static TmlSession session = null;
    private static Resources resources;

    /**
     * <p>Initializes the SDK</p>
     */
    public static void init(Context context, String zipVersion) {
        startRecognizeTouch(context);
        if (getSession() == null) {
            initConfig(context);
            if (!TextUtils.isEmpty(zipVersion) && !Tml.hasZipVersion(zipVersion)) {
                Cache cache = Tml.getCache();
                if (cache != null && cache instanceof FileCache) {
                    FileCache fileCache = ((FileCache) cache);
                    Decompress.unzipFromRes(context, zipVersion, fileCache.getCachePath());
                }
            }
            CacheVersion cacheVersion = null;
            if ((cacheVersion = Tml.hasCachedVersion()) != null) {
                Map<String, Object> options = Tml.getConfig().getApplication();
                options.put(CacheVersion.VERSION_KEY, cacheVersion.getVersion());
                Tml.setSession(new TmlSession(options));

                Locale locale = PreferenceUtil.getCurrentLocation(context);
                if (Tml.getAndroidApplication().isSupportedLocale(locale.getLanguage())) {
                    Language language = Tml.getAndroidApplication().getLanguageLocal(locale.getLanguage(), cacheVersion.getVersion());
                    if (language != null && language.isLoaded()) {
                        Tml.switchLanguageLocal(language, options);
                    }
                }
            }

            TmlService.startSync(context);
            startScheduledTasks();
        }
    }

    public static void startScheduledTasks() {
        if (applicationScheduleHandler != null)
            return;

        applicationScheduleHandler = scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                getLogger().debug("Running scheduled tasks...");
                getSession().getApplication().submitMissingTranslationKeys();
            }
        }, 10, 5, TimeUnit.SECONDS);
    }

    private static void initConfig(Context context) {
        Tml.getConfig().setLogger(Utils.map(
                "class", Logger.class.getName()
        ));

        Tml.getConfig().setCache(Utils.map(
                "enabled", true,
                "class", FileCache.class.getName(),
                "cache_dir", FileUtils.getBaseDirectory(context)
        ));

        Tml.getConfig().setApplicationClass(TmlApplication.class.getName());
        Tml.getConfig().addTokenizerClass(TranslationKey.DEFAULT_TOKENIZERS_STYLED, SpannableStringTokenizer.class.getName());
    }

    private static void stop() {
        Map<String, Object> application = getConfig().getApplication();
        setApplication(null);
        setCache(null);
        setConfig(null);

        getConfig().setApplication(application);
    }

    public static void reInit(Context context) {
        stop();
        initConfig(context);
        TmlService.startSync(context);
    }

    private static void initRes(Context context) {
//        if (resources != null && getAndroidApplication() != null) {
//            Locale locale = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                locale = resources.getConfiguration().getLocales().get(0);
//            } else {
//                locale = resources.getConfiguration().locale;
//            }
//            if (!locale.getCountry().equals(getAndroidApplication().getDefaultLocale())) {
//                resources = null;
//            }
//        }
        if (resources == null) {
            Configuration conf = context.getResources().getConfiguration();
            Locale locale = new Locale("en");
            if (getAndroidApplication() != null) {
                locale = new Locale(getAndroidApplication().getDefaultLocale());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleList localeList = new LocaleList(locale);
                conf.setLocales(localeList);
            } else {
                conf.locale = locale;
            }
            DisplayMetrics metrics = new DisplayMetrics();
            resources = new Resources(context.getAssets(), metrics, conf);
        }
    }

    private static void startRecognizeTouch(Context context) {
        if (activityLifecycleCallbacks == null) {
            ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle bundle) {
                    Tml.getLogger().error("onActivityCreated", activity.getClass().getSimpleName());
                }

                @Override
                public void onActivityStarted(Activity activity) {
                    Tml.getLogger().error("onActivityStarted", activity.getClass().getSimpleName());
                    Tml.addObject(activity);
                    View view = activity.getWindow().getDecorView();
                    if (view != null) {
                        ViewUtils.findViews(view);
                        view.setOnTouchListener(new View.OnTouchListener() {

                            @Override
                            public boolean onTouch(View view, MotionEvent event) {
                                Tml.getLogger().debug("onTouch", event.toString());
                                if (event.getAction() == MotionEvent.ACTION_POINTER_3_UP) {
                                    view.getContext().startActivity(new Intent(view.getContext(), TmlAndroidActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                }

                @Override
                public void onActivityResumed(Activity activity) {
                }

                @Override
                public void onActivityPaused(Activity activity) {
                }

                @Override
                public void onActivityStopped(Activity activity) {
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    Tml.removeObject(activity);

                }
            });
        }
    }

    /**
     * <p>tr.</p>
     *
     * @param label Label to be translated
     * @return translated label
     */
    public static String tr(String label) {
        return tr(label, "");
    }

    public static String tr(Context context, int stringResId) {
        initRes(context);
        return tr(resources.getString(stringResId));
    }

    public static String tr(Context context, int stringResId, Object... formatArgs) {
        initRes(context);
        return tr(resources.getString(stringResId, formatArgs));
    }

    public static void tr(View textLabel, String label) {
        if (textLabel instanceof TextView) {
            textLabel.setTag(R.id.tml_key_hash_id, TranslationKey.generateKey(label));
            ((TextView) textLabel).setText(tr(label));
        }
    }

    public static void tr(View textLabel, int stringResId) {
        initRes(textLabel.getContext());
        String label = resources.getString(stringResId);
        tr(textLabel, label);
    }

    public static void tr(View textLabel, int stringResId, Object... formatArgs) {
        initRes(textLabel.getContext());
        String label = resources.getString(stringResId, formatArgs);
        tr(textLabel, label);
    }

    /**
     * <p>tr.</p>
     *
     * @param label       a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String tr(String label, String description) {
        return tr(label, description, null);
    }

    public static void tr(View textLabel, String label, String description) {
        if (textLabel instanceof TextView) {
            textLabel.setTag(R.id.tml_key_hash_id, TranslationKey.generateKey(label, description));
            ((TextView) textLabel).setText(tr(label, description));
        }
    }

    /**
     * <p>tr.</p>
     *
     * @param label       a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param tokens      a {@link java.util.Map} object.
     * @return a {@link java.lang.String} object.
     */
    public static String tr(String label, String description, Map<String, Object> tokens) {
        return getSession() == null ? label : getSession().translate(label, description, tokens, null);
    }

    public static void tr(View textLabel, String label, String description, Map<String, Object> tokens) {
        if (textLabel instanceof TextView) {
            textLabel.setTag(R.id.tml_key_hash_id, TranslationKey.generateKey(label, description));
            ((TextView) textLabel).setText(tr(label, description, tokens));
        }
    }

    /**
     * <p>tr.</p>
     *
     * @param label  a {@link java.lang.String} object.
     * @param tokens a {@link java.util.Map} object.
     * @return a {@link java.lang.String} object.
     */
    public static String tr(String label, Map<String, Object> tokens) {
        return getSession() == null ? label : getSession().translate(label, tokens);
    }

    public static void tr(View textLabel, String label, Map<String, Object> tokens) {
        if (textLabel instanceof TextView) {
            textLabel.setTag(R.id.tml_key_hash_id, TranslationKey.generateKey(label));
            ((TextView) textLabel).setText(tr(label, tokens));
        }
    }

    /**
     * <p>tr.</p>
     *
     * @param label   a {@link java.lang.String} object.
     * @param tokens  a {@link java.util.Map} object.
     * @param options a {@link java.util.Map} object.
     * @return a {@link java.lang.String} object.
     */
    public static String tr(String label, Map<String, Object> tokens, Map<String, Object> options) {
        return getSession() == null ? label : getSession().translate(label, tokens, options);
    }

    public static void tr(View textLabel, String label, Map<String, Object> tokens, Map<String, Object> options) {
        if (textLabel instanceof TextView) {
            textLabel.setTag(R.id.tml_key_hash_id, TranslationKey.generateKey(label));
            ((TextView) textLabel).setText(tr(label, tokens, options));
        }
    }

    /**
     * Translates the SpannableString
     *
     * @param label a {@link java.lang.String} object.
     * @return
     */
    public static Spannable trs(String label) {
        return getSession() == null ? new SpannableString(label) : trs(label, "");
    }

    public static void trs(View textLabel, String label) {
        if (textLabel instanceof TextView) {
            textLabel.setTag(R.id.tml_key_hash_id, TranslationKey.generateKey(label));
            ((TextView) textLabel).setText(trs(label));
        }
    }

    /**
     * Translates the SpannableString
     *
     * @param label       a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @return
     */
    public static Spannable trs(String label, String description) {
        return getSession() == null ? new SpannableString(label) : trs(label, description, null);
    }

    public static void trs(View textLabel, String label, String description) {
        if (textLabel instanceof TextView) {
            textLabel.setTag(R.id.tml_key_hash_id, TranslationKey.generateKey(label, description));
            ((TextView) textLabel).setText(trs(label, description));
        }
    }

    /**
     * Translates the SpannableString
     *
     * @param label       a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param tokens      a {@link java.lang.String} object.
     * @return
     */
    public static Spannable trs(String label, String description, Map<String, Object> tokens) {
        return getSession() == null ? new SpannableString(label) : trs(label, description, tokens, null);
    }

    public static void trs(View textLabel, String label, String description, Map<String, Object> tokens) {
        if (textLabel instanceof TextView) {
            textLabel.setTag(R.id.tml_key_hash_id, TranslationKey.generateKey(label, description));
            ((TextView) textLabel).setText(trs(label, description, tokens));
        }
    }

    /**
     * Translates the SpannableString
     *
     * @param label  a {@link java.lang.String} object.
     * @param tokens a {@link java.lang.String} object.
     * @return
     */
    public static Spannable trs(String label, Map<String, Object> tokens) {
        return getSession() == null ? new SpannableString(label) : trs(label, null, tokens, null);
    }

    public static void trs(View textLabel, String label, Map<String, Object> tokens) {
        if (textLabel instanceof TextView) {
            textLabel.setTag(R.id.tml_key_hash_id, TranslationKey.generateKey(label));
            ((TextView) textLabel).setText(trs(label, tokens));
        }
    }

    /**
     * Translates the SpannableString
     *
     * @param label   a {@link java.lang.String} object.
     * @param tokens  a {@link java.lang.String} object.
     * @param options a {@link java.lang.String} object.
     * @return
     */
    public static Spannable trs(String label, Map<String, Object> tokens, Map<String, Object> options) {
        return getSession() == null ? new SpannableString(label) : trs(label, null, tokens, options);
    }

    public static void trs(View textLabel, String label, Map<String, Object> tokens, Map<String, Object> options) {
        if (textLabel instanceof TextView) {
            textLabel.setTag(R.id.tml_key_hash_id, TranslationKey.generateKey(label));
            ((TextView) textLabel).setText(trs(label, tokens, options));
        }
    }

    /**
     * Translates the SpannableString
     *
     * @param label       a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param tokens      a {@link java.lang.String} object.
     * @param options     a {@link java.lang.String} object.
     * @return
     */
    public static Spannable trs(String label, String description, Map<String, Object> tokens, Map<String, Object> options) {
        if (getSession() == null) {
            return new SpannableString(label);
        } else {
            Object o = getSession().translateStyledString(label, tokens, options);
            if (o instanceof Spannable) {
                return (Spannable) o;
            } else {
                return new SpannableString(Html.fromHtml(o.toString()));
            }
        }

    }

    public static void trs(View textLabel, String label, String description, Map<String, Object> tokens, Map<String, Object> options) {
        if (textLabel instanceof TextView) {
            textLabel.setTag(R.id.tml_key_hash_id, TranslationKey.generateKey(label, description));
            ((TextView) textLabel).setText(trs(label, description, tokens, options));
        }
    }

    private static boolean hasZipVersion(String version) {
        Cache cache = Tml.getCache();
        if (cache != null && cache instanceof FileCache) {
            FileCache fileCache = ((FileCache) cache);
            return new File(fileCache.getCachePath(), version).exists();
        }
        return false;
    }

    private static CacheVersion hasCachedVersion() {
        Cache cache = Tml.getCache();
        if (cache != null) {
            CacheVersion cacheVersion = new TmlCacheVersion();
            if (cacheVersion.fetchFromCache()) {
                return cacheVersion;
            }
        }
        return null;
    }

    public static List<Object> getObjects() {
        if (objects == null) {
            objects = new ArrayList<>();
        }
        return objects;
    }

    private static void addObject(Object c) {
        if (!getObjects().contains(c)) {
            getObjects().add(c);
        }
    }

    private static void removeObject(Object c) {
        if (getObjects().contains(c)) {
            getObjects().remove(c);
        }
    }

    public static TmlApplication getAndroidApplication() {
        return getSession() == null ? null : getSession().getApplication();
    }

    public static Auth getAuth() {
        if (auth == null) {
            auth = Auth.getAuth();
        }
        return auth;
    }

    public static void setAuth(Auth auth) {
        Tml.auth = auth;
        if (auth == null) {
            Tml.getCache().delete("auth", Utils.map());
            Tml.getAndroidApplication().setAccessToken(null);
        } else {
            Tml.getAndroidApplication().setAccessToken(auth.getAccessToken());
        }
    }

    public static TmlSession getSession() {
        return session;
    }

    public static void setSession(TmlSession session) {
        Tml.session = session;
    }

    private static void switchLanguageLocal(Language language, Map<String, Object> options) {
        getSession().switchLanguageLocal(language, options);
    }

    public static void switchLanguage(Language language) {
        getSession().switchLanguage(language);
    }

    /**
     * <p>initSource.</p>
     *
     * @param key    a {@link java.lang.String} object.
     * @param locale a {@link java.lang.String} object.
     */
    public static void initSource(String key, String locale, Map<String, Object> options) {
        getAndroidApplication().getSource(key, locale, options);
    }

    /**
     * <p>initSource.</p>
     *
     * @param key    a {@link java.lang.String} object.
     * @param locale a {@link java.lang.String} object.
     */
    public static void initSource(String key, String locale) {
        getAndroidApplication().getSource(key, locale, null);
    }

    /**
     * <p>setApplication.</p>
     *
     * @param application a {@link com.translationexchange.core.Application} object.
     */
    public static void setApplication(com.translationexchange.core.Application application) {
        getSession().setApplication(application);
    }

    /**
     * <p>addObserver.</p>
     *
     * @param observer a {@link java.util.Observer} object.
     */
    public static void addObserver(Observer observer) {
        if (getSession() != null) {
            getSession().addObserver(observer);
        }
    }

    public static void deleteObserver(Observer observer) {
        if (getSession() != null) {
            getSession().deleteObserver(observer);
        }
    }
}
