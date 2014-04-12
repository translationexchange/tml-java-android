package com.tr8n.android.fragments;

import java.util.Observable;
import java.util.Observer;

import android.app.Fragment;
import android.os.Bundle;

import com.tr8n.android.interfaces.Translatable;
import com.tr8n.android.tasks.TranslationTask;
import com.tr8n.core.Tr8n;

public class Tr8nFragment extends Fragment implements Translatable, Observer {

	public Tr8nFragment(){}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        Tr8n.addObserver(this);
        translate();
	}	
	
	/**
	 * This method to be called once the UI is fully setup 
	 */
	public void translate() {
		new TranslationTask(this).execute();	
	}
	
	public void update(Observable observable, Object data) {
		translate();
	}

	public String getTr8nSource() {
		return getClass().getName(); 	
	}

	public void registerSources() {
		Tr8n.initSource(getTr8nSource());		
	}
	
	public void onTr8nBeforeTranslate() {
		registerSources();
	}

	public void onTr8nTranslate() {
		// TODO: go through the view hierarchy and translate all components 
	}

}
