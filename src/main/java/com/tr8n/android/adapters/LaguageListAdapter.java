package com.tr8n.android.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tr8n.core.Language;

public class LaguageListAdapter extends BaseAdapter {
	
	private Context context;
	private List<Language> languages;

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	public LaguageListAdapter(Context context){
		this(context, new ArrayList<Language>());
	}
	
	public LaguageListAdapter(Context context, List<Language> languages){
		this.context = context;
		this.languages = languages;
	}

	public int getCount() {
		return languages.size();
	}

	public Object getItem(int position) {		
		return languages.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LinearLayout layout = new LinearLayout(context);
	        layout.setGravity(Gravity.CENTER);
	        layout.setOrientation(LinearLayout.VERTICAL);
	        layout.setBackgroundColor(Color.WHITE);
	        
	        TextView languageName = new TextView(context);
	        languageName.setPadding(10, 30, 10, 30);
	        languageName.setId(123456);
	        layout.addView(languageName, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        
            convertView = layout;
        }
         
		Language language = languages.get(position);
		
        TextView languageName = (TextView) convertView.findViewById(123456);
        languageName.setText(language.getEnglishName());

        return convertView;
	}

}
