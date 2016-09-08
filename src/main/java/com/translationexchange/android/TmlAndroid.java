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
import android.os.Bundle;
import android.text.Spannable;
import android.view.MotionEvent;
import android.view.View;

import com.translationexchange.android.activities.OptionActivity;
import com.translationexchange.android.cache.FileCache;
import com.translationexchange.android.service.TmlService;
import com.translationexchange.core.Tml;
import com.translationexchange.core.TmlMode;
import com.translationexchange.core.cache.Cache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TmlAndroid extends com.translationexchange.core.Tml {

    private static List<Object> objects;
    private static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;

    /**
     * <p>Initializes the SDK</p>
     */
    public static void init(Activity activity, TmlMode tmlMode) {
        init(activity, tmlMode, null);
    }

    /**
     * <p>Initializes the SDK</p>
     */
    public static void init(Activity activity, TmlMode tmlMode, String zip) {
        if (getSession() == null) {
            TmlService.startInit(activity, tmlMode, zip);
            startScheduledTasks();
        }
        startRecognizeTouch(activity);
    }

    public static void destroy(Activity activity) {
        stopScheduledTasks();
        TmlAndroid.removeObject(activity);
        setSession(null);
        if (activityLifecycleCallbacks != null) {
            activity.getApplication().unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
            activityLifecycleCallbacks = null;
        }
    }

    private static void startRecognizeTouch(Context activity) {
        if (activityLifecycleCallbacks == null) {
            ((Application) activity.getApplicationContext()).registerActivityLifecycleCallbacks(activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle bundle) {
                    Tml.getLogger().error("onActivityCreated", activity.getClass().getSimpleName());
                }

                @Override
                public void onActivityStarted(Activity activity) {
                    Tml.getLogger().error("onActivityStarted", activity.getClass().getSimpleName());
                    View view = activity.findViewById(android.R.id.content);
                    if (view != null) {
                        view.setOnTouchListener(new View.OnTouchListener() {

                            @Override
                            public boolean onTouch(View view, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_POINTER_3_UP) {
                                    view.getContext().startActivity(new Intent(view.getContext(), OptionActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    return false;
                                }
                                return true;
                            }
                        });
                    }
                }

                @Override
                public void onActivityResumed(Activity activity) {
                    Tml.getLogger().error("onActivityResumed", activity.getClass().getSimpleName());
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

                }
            });
        }
    }

    /**
     * <p>translate.</p>
     *
     * @param label Label to be translated
     * @return translated label
     */
    public static String translate(String label) {
        return getSession() == null ? label : getSession().translate(label);
    }

    /**
     * <p>translate.</p>
     *
     * @param label       a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String translate(String label, String description) {
        return getSession() == null ? label : getSession().translate(label, description);
    }

    /**
     * <p>translate.</p>
     *
     * @param label       a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param tokens      a {@link java.util.Map} object.
     * @return a {@link java.lang.String} object.
     */
    public static String translate(String label, String description, Map<String, Object> tokens) {
        return getSession() == null ? label : getSession().translate(label, description, tokens);
    }

    /**
     * <p>translate.</p>
     *
     * @param label  a {@link java.lang.String} object.
     * @param tokens a {@link java.util.Map} object.
     * @return a {@link java.lang.String} object.
     */
    public static String translate(String label, Map<String, Object> tokens) {
        return getSession() == null ? label : getSession().translate(label, tokens);
    }

    /**
     * <p>translate.</p>
     *
     * @param label   a {@link java.lang.String} object.
     * @param tokens  a {@link java.util.Map} object.
     * @param options a {@link java.util.Map} object.
     * @return a {@link java.lang.String} object.
     */
    public static String translate(String label, Map<String, Object> tokens, Map<String, Object> options) {
        return getSession() == null ? label : getSession().translate(label, tokens, options);
    }

    /**
     * Translates the SpannableString
     *
     * @param label
     * @return
     */
    public static Spannable translateSpannableString(String label) {
        return translateSpannableString(label, "");
    }

    /**
     * Translates the SpannableString
     *
     * @param label
     * @param description
     * @return
     */
    public static Spannable translateSpannableString(String label, String description) {
        return translateSpannableString(label, description, null);
    }

    /**
     * Translates the SpannableString
     *
     * @param label
     * @param description
     * @param tokens
     * @return
     */
    public static Spannable translateSpannableString(String label, String description, Map<String, Object> tokens) {
        return translateSpannableString(label, description, tokens, null);
    }

    /**
     * Translates the SpannableString
     *
     * @param label
     * @param tokens
     * @return
     */
    public static Spannable translateSpannableString(String label, Map<String, Object> tokens) {
        return translateSpannableString(label, null, tokens, null);
    }

    /**
     * Translates the SpannableString
     *
     * @param label
     * @param tokens
     * @param options
     * @return
     */
    public static Spannable translateSpannableString(String label, Map<String, Object> tokens, Map<String, Object> options) {
        return translateSpannableString(label, null, tokens, options);
    }

    /**
     * Translates the SpannableString
     *
     * @param label
     * @param description
     * @param tokens
     * @param options
     * @return
     */
    public static Spannable translateSpannableString(String label, String description, Map<String, Object> tokens, Map<String, Object> options) {
        return (Spannable) com.translationexchange.core.Tml.getSession().translateStyledString(label, tokens, options);
    }

    public static boolean hasCachedVersion(String version) {
        Cache cache = TmlAndroid.getCache();
        if (cache != null && cache instanceof FileCache) {
            FileCache fileCache = ((FileCache) cache);
            return new File(fileCache.getCachePath(), version).exists();
        }
        return false;
    }

    public static List<Object> getObjects() {
        if (objects == null) {
            objects = new ArrayList<>();
        }
        return objects;
    }

    public static void addObject(Object c) {
        if (!getObjects().contains(c)) {
            getObjects().add(c);
        }
    }

    public static void removeObject(Object c) {
        if (getObjects().contains(c)) {
            getObjects().remove(c);
        }
    }
}
