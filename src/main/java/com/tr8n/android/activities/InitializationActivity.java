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
 
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import com.tr8n.android.interfaces.Initializable;
import com.tr8n.android.tasks.InitializationTask;
import com.tr8n.android.tokenizers.SpannableStringTokenizer;
import com.tr8n.core.Language;
import com.tr8n.core.Tr8n;
import com.tr8n.core.Utils;
 
@SuppressLint("Registered")
public abstract class InitializationActivity extends Activity implements Initializable {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new InitializationTask(this).execute();
    }

    /**
     * Override this method to configure anything in Tr8n before it is initialized
     */
    public void onTr8nBeforeInit() {
    	Tr8n.getConfig().setCache(Utils.buildMap(
	        "enabled", 	true,
	    	"class", 	"com.tr8n.android.cache.FileCache",
	    	"cache_dir", getFilesDir()
         ));
    	
        Tr8n.getConfig().addTokenizerClass("styled", SpannableStringTokenizer.class.getName());
    }
      
    /**
     * Called during initialization
     */
    public void onTr8nInit() {
    	Tr8n.init();
    	
    	String locale = Locale.getDefault().toString();
    	Tr8n.getLogger().debug("System locale: " + locale);
    	locale = locale.replaceAll("_", "-");
    	
    	// TODO: map android locales to tr8nhub
    	locale = locale.split("-")[0];

    	if (!Tr8n.getApplication().isSupportedLocale(locale))
    		return;

    	Language language = Tr8n.getApplication().getLanguage(locale);
    	if (language != null && language.isLoaded())
    		Tr8n.switchLanguage(language);
    }
    
    /**
     * Called after initialization thread is finished
     */
    public void onTr8nAfterInit() {
      finish();
    }
    
}