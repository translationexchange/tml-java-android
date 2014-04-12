package com.tr8n.android.tasks;

import android.os.AsyncTask;

import com.tr8n.android.interfaces.Initializable;

public class InitializationTask extends AsyncTask<Void, Void, Void> {

	private Initializable initializable;
	
	public InitializationTask(Initializable initializable) {
		this.initializable = initializable;
	}
    	
	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    initializable.onTr8nBeforeInit();
	}
 
    @Override
    protected Void doInBackground(Void...arguments) {
    	initializable.onTr8nInit();
        return null;
    }
 
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        initializable.onTr8nAfterInit();
    }
}
