/*
 *  Copyright (c) 2014 Michael Berkovich, http://tr8nhub.com All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

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
