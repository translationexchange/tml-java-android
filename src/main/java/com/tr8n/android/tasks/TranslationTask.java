package com.tr8n.android.tasks;

import android.os.AsyncTask;

import com.tr8n.android.interfaces.Translatable;
import com.tr8n.core.Tr8n;
import com.tr8n.core.Utils;

public class TranslationTask extends AsyncTask<Void, Void, Void> {

	private Translatable translatable;
	
	public TranslationTask(Translatable translatable) {
		this.translatable = translatable;
	}
    	
	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	}
 
    @Override
    protected Void doInBackground(Void... arg0) {
        translatable.onTr8nBeforeTranslate();
        return null;
    }
 
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        
        Tr8n.beginBlockWithOptions(Utils.buildMap("source", translatable.getTr8nSource()));
        translatable.onTr8nTranslate();
        Tr8n.endBlock();
    }
}
