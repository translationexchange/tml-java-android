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

import java.util.Map;

import com.translationexchange.core.TranslationKey;
import com.translationexchange.android.tokenizers.SpannableStringTokenizer;

import android.text.Spannable;

public class TmlAndroid extends com.translationexchange.core.Tml {

    /**
     * <p>init.</p>
     */
    public static void init() {
        Map<String, Object> options = getConfig().getApplication();
        init((String) options.get("key"), (String) options.get("token"), options);
    }

    /**
     * Initializes the SDK
     *
     * @param key
     * @param secret
     * @param host
     */
    public static void init(String key, String secret, String host) {
        com.translationexchange.core.Tml.init(key, secret);
        com.translationexchange.core.Tml.getConfig().addTokenizerClass(
                TranslationKey.DEFAULT_TOKENIZERS_STYLED,
                SpannableStringTokenizer.class.getName());
    }

    /**
     * <p>translate.</p>
     *
     * @param label Label to be translated
     * @return translated label
     */
    public static String translate(String label) {
        return getSession().translate(label);
    }

    /**
     * <p>translate.</p>
     *
     * @param label       a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String translate(String label, String description) {
        return getSession().translate(label, description);
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
        return getSession().translate(label, description, tokens);
    }

    /**
     * <p>translate.</p>
     *
     * @param label  a {@link java.lang.String} object.
     * @param tokens a {@link java.util.Map} object.
     * @return a {@link java.lang.String} object.
     */
    public static String translate(String label, Map<String, Object> tokens) {
        return getSession().translate(label, tokens);
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
        return getSession().translate(label, tokens, options);
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

}
