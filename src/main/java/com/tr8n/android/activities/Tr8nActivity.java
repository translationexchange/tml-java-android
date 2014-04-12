package com.tr8n.android.activities;

import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.tr8n.android.interfaces.Translatable;
import com.tr8n.android.tasks.TranslationTask;
import com.tr8n.core.Session;
import com.tr8n.core.Tr8n;

@SuppressLint("Registered")
public abstract class Tr8nActivity extends Activity implements Translatable, Observer {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tr8n.addObserver(this);
    }
	
	/**
	 * Checks if network is available
	 * @return
	 */
	public boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}	
	
	/**
	 * This method to be called once the UI is fully setup 
	 */
	public void translate() {
		new TranslationTask(this).execute();	
	}

	/**
	 * Returns the source key of the activity
	 */
	public String getTr8nSource() {
		return getClass().getName(); 	
	}

	/**
	 * Registers all sources referenced by the activity
	 */
	public void registerSources() {
		Tr8n.initSource(getTr8nSource());
	}
	
	/**
	 * By default every view will register its own source and all keys under it. 
	 * In case your view needs languages other than the current language, ensure that the languages are loaded.
	 * Use: Tr8n.initLanguage("ru")
	 */
	public void onTr8nBeforeTranslate() {
		registerSources();
	}
	
	/**
	 * The main method where all translations should happen
	 */
	public void onTr8nTranslate() {
		// TODO: go through the view hierarchy and translate all components 
	}
	
	/**
	 * When language changes, this message would be fired
	 */
    public void update(Observable observable, Object data) {
    	if (observable instanceof Session)
    		translate();	
    }
}
