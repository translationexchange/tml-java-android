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

package com.translationexchange.android.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;

import com.translationexchange.android.Tml;
import com.translationexchange.android.cache.FileCache;
import com.translationexchange.android.interfaces.Initializable;
import com.translationexchange.android.logger.Logger;
import com.translationexchange.android.tasks.InitializationTask;
import com.translationexchange.android.tokenizers.SpannableStringTokenizer;
import com.translationexchange.core.TranslationKey;
import com.translationexchange.core.Utils;
import com.translationexchange.core.languages.Language;

import java.util.Locale;

@SuppressLint("Registered")
public abstract class InitializationActivity extends Activity implements Initializable {

    public void startInit() {
        new InitializationTask(this).execute();
    }

    /**
     * Override this method to configure anything in Tr8n before it is initialized
     */
    public void onBeforeInit() {
        Tml.getConfig().setCache(Utils.buildMap(
                "enabled", true,
                "class", FileCache.class.getName(),
                "cache_dir", Environment.getExternalStorageDirectory()
        ));

        Tml.getConfig().setLogger(Utils.buildMap(
                "class", Logger.class.getName()
        ));

        Tml.getConfig().addTokenizerClass(TranslationKey.DEFAULT_TOKENIZERS_STYLED, SpannableStringTokenizer.class.getName());
    }

    /**
     * Called during initialization
     */
    public void onInit() {
        Tml.init();

        String locale = Locale.ENGLISH.toString();
//        String locale = Locale.getDefault().toString();
        Tml.getLogger().debug("System locale: " + locale);
        locale = locale.replaceAll("_", "-");

        // TODO: map android locales to tr8nhub
        locale = locale.split("-")[0];

        if (!Tml.getApplication().isSupportedLocale(locale))
            return;

        Language language = Tml.getApplication().getLanguage(locale);
        if (language != null && language.isLoaded())
            Tml.switchLanguage(language);
    }

    /**
     * Called after initialization thread is finished
     */
    public void onAfterInit() {
        finish();
    }

}