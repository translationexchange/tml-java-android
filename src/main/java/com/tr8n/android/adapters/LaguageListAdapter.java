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
