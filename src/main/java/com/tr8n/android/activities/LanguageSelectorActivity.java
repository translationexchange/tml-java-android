package com.tr8n.android.activities;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.tr8n.android.adapters.LaguageListAdapter;
import com.tr8n.core.Language;
import com.tr8n.core.Tr8n;
import com.tr8n.core.Utils;

public class LanguageSelectorActivity extends Tr8nActivity {

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
    	adapter.setLanguages(Tr8n.getApplication().getlanguages());
        adapter.notifyDataSetChanged();
    }
    
    private void loadLanguagesFromNetwork() {
    	final ProgressDialog dialog = ProgressDialog.show(this, Tr8n.translate("Language Selector"), Tr8n.translate("Loading languages..."));
    	
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
    	    		Map<String, Object> results = (Map<String, Object>) Tr8n.getApplication().getHttpClient().getJSON("application/languages");
    	    		List<Map<String, Object>> langs = (List<Map<String, Object>>) results.get("results");
    	    		languages = new ArrayList<Language>();
    	    		for (Map<String, Object> attrs : langs) {
    	    			languages.add(new Language(attrs));
    	    		}
    	    	} catch(Exception ex) {
    	    		Tr8n.getLogger().logException("Failed to load languages", ex);
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
    	    	adapter.setLanguages(Tr8n.getApplication().getlanguages());
    	    	adapter.notifyDataSetChanged();
    	    }
    		
    	}.execute();
    }
    
    protected void selectLanguage(Language language) {
    	final ProgressDialog dialog = ProgressDialog.show(this, Tr8n.translate("Language Selector"), Tr8n.translate("Changing language..."));

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
    	    	Tr8n.switchLanguage(languages[0], options);
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
    
	public void onTr8nTranslate() {
        setTitle(Tr8n.translate("Select Language"));
	}
    
}
