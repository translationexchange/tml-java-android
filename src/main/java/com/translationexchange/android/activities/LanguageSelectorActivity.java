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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.translationexchange.android.R;
import com.translationexchange.android.TmlAndroid;
import com.translationexchange.android.adapters.LanguageAdapter;
import com.translationexchange.android.utils.PreferenceUtil;
import com.translationexchange.core.Utils;
import com.translationexchange.core.languages.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LanguageSelectorActivity extends BaseActivity implements LanguageAdapter.OnLanguageListener {

    protected static void open(Context context) {
        context.startActivity(new Intent(context, LanguageSelectorActivity.class));
    }

    private LanguageAdapter languageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        setupActionBar(true);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build())
                .build();
        ImageLoader.getInstance().init(config);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(languageAdapter = new LanguageAdapter(this));

        loadLanguagesFromNetwork();
    }

    private void loadLanguagesFromApplication() {
        languageAdapter.setLanguages(TmlAndroid.getApplication().getLanguages());
    }

    private void loadLanguagesFromNetwork() {
        final ProgressDialog dialog = ProgressDialog.show(this, TmlAndroid.translate("Language Selector"), TmlAndroid.translate("Loading languages..."));

        new AsyncTask<Void, Void, List<Language>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @SuppressWarnings("unchecked")
            @Override
            protected List<Language> doInBackground(Void... params) {
                try {
                    Map<String, Object> results = TmlAndroid.getAndroidApplication().getHttpClient().getJSON("application/languages", Utils.buildMap(), Utils.buildMap("cache_key", "application"));
                    List<Map<String, Object>> langs = (List<Map<String, Object>>) results.get("languages");
                    List<Language> languages = new ArrayList<Language>();
                    for (Map<String, Object> attrs : langs) {
                        languages.add(new Language(attrs));
                    }
                    return languages;
                } catch (Exception ex) {
                    TmlAndroid.getLogger().logException("Failed to load languages", ex);
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Language> result) {
                super.onPostExecute(result);
                dialog.dismiss();
                if (result == null) {
                    loadLanguagesFromApplication();
                } else {
                    languageAdapter.setLanguages(result);
                }
            }

        }.execute();
    }

    @Override
    public void onClick(Language language) {
        PreferenceUtil.setCurrentLocation(getApplicationContext(), new Locale(language.getLocale(), language.getLocale()));
        finish();
    }
}
