/**
 * Copyright (c) 2015 Translation Exchange, Inc. All rights reserved.
 *
 *  _______                  _       _   _             ______          _
 * |__   __|                | |     | | (_)           |  ____|        | |
 *    | |_ __ __ _ _ __  ___| | __ _| |_ _  ___  _ __ | |__  __  _____| |__   __ _ _ __   __ _  ___
 *    | | '__/ _` | '_ \/ __| |/ _` | __| |/ _ \| '_ \|  __| \ \/ / __| '_ \ / _` | '_ \ / _` |/ _ \
 *    | | | | (_| | | | \__ \ | (_| | |_| | (_) | | | | |____ >  < (__| | | | (_| | | | | (_| |  __/
 *    |_|_|  \__,_|_| |_|___/_|\__,_|\__|_|\___/|_| |_|______/_/\_\___|_| |_|\__,_|_| |_|\__, |\___|
 *                                                                                        __/ |
 *                                                                                       |___/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.translationexchange.android.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.translationexchange.core.Utils;
import com.translationexchange.android.Tml;
import com.translationexchange.android.adapters.LaguageListAdapter;
import com.translationexchange.core.languages.Language;

public class LanguageSelectorActivity extends LocalizedActivity {

	LinearLayout layout;
	ListView languageList;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        layout = new LinearLayout(this);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.WHITE);
        
        languageList = new ListView(this);
        languageList.setDivider(new ColorDrawable(Color.parseColor("#b5b5b5")));
        languageList.setDividerHeight(1);
                
        layout.addView(languageList, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        languageList.setAdapter(new LaguageListAdapter(this));
        languageList.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LaguageListAdapter adapter = (LaguageListAdapter) ((ListView) parent).getAdapter();
				Language language = adapter.getLanguages().get(position);
				selectLanguage(language);
			}
		});
        
        setContentView(layout);
        
    	if (isNetworkAvailable()) 
    		loadLanguagesFromNetwork();
    	else
    		loadLanguagesFromApplication();
    			

        translate();
    }

    private void loadLanguagesFromApplication() {
    	LaguageListAdapter adapter = (LaguageListAdapter) languageList.getAdapter();
    	adapter.setLanguages(Tml.getApplication().getLanguages());
        adapter.notifyDataSetChanged();
    }
    
    private void loadLanguagesFromNetwork() {
    	final ProgressDialog dialog = ProgressDialog.show(this, Tml.translate("Language Selector"), Tml.translate("Loading languages..."));
    	
    	new AsyncTask<Void, Void, Void>() {
    		List<Language> languages;
    		
    		@Override
    		protected void onPreExecute() {
    		    super.onPreExecute();
    		}
    		
    	    @SuppressWarnings("unchecked")
			@Override
    	    protected Void doInBackground(Void... params) {
    	    	try {
    	    		Map<String, Object> results = (Map<String, Object>) Tml.getApplication().getHttpClient().getJSON("application/languages");
    	    		List<Map<String, Object>> langs = (List<Map<String, Object>>) results.get("results");
    	    		languages = new ArrayList<Language>();
    	    		for (Map<String, Object> attrs : langs) {
    	    			languages.add(new Language(attrs));
    	    		}
    	    	} catch(Exception ex) {
    	    		Tml.getLogger().logException("Failed to load languages", ex);
    	    	}
    	        return null;
    	    }
    	 
    	    @Override
    	    protected void onPostExecute(Void result) {
    	        super.onPostExecute(result);
    	        dialog.dismiss();
    	        
    	        if (languages == null) { 
    	        	loadLanguagesFromApplication();
    	        	return;
    	        }
    	        
    	    	LaguageListAdapter adapter = (LaguageListAdapter) languageList.getAdapter();
    	    	adapter.setLanguages(Tml.getApplication().getLanguages());
    	    	adapter.notifyDataSetChanged();
    	    }
    		
    	}.execute();
    }
    
    protected void selectLanguage(Language language) {
    	final ProgressDialog dialog = ProgressDialog.show(this, Tml.translate("Language Selector"), Tml.translate("Changing language..."));

    	new AsyncTask<Language, Void, Void>() {
    		Map<String, Object> options;
    		
    		@Override
    		protected void onPreExecute() {
    		    super.onPreExecute();
    		    if (!isNetworkAvailable())
    		    	options = Utils.buildMap("offline", true);
    		}
    		
    	    @Override
    	    protected Void doInBackground(Language... languages) {
    	    	Tml.switchLanguage(languages[0], options);
    	        return null;
    	    }
    	 
    	    @Override
    	    protected void onPostExecute(Void result) {
    	        super.onPostExecute(result);
    	        dialog.dismiss();
    	        finish();
    	    }
    		
    	}.execute(language);
    }
    
	public void onLocalize() {
        setTitle(Tml.translate("Select Language"));
	}
    
}
