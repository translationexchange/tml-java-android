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

package com.tr8n.android;

import java.util.Map;

import com.tr8n.core.TranslationKey;
import com.tr8n.android.tokenizers.SpannableStringTokenizer;

import android.text.Spannable;

public class Tr8n extends com.tr8n.core.Tr8n {

	/**
	 * Initializes the SDK
	 * @param key
	 * @param secret
	 * @param host
	 */
	public static void init(String key, String secret, String host) {
		com.tr8n.core.Tr8n.init(key, secret, host);
		com.tr8n.core.Tr8n.getConfig().addTokenizerClass(
				TranslationKey.DEFAULT_TOKENIZERS_STYLED,
				SpannableStringTokenizer.class.getName());
	}

	/**
	 * Translates the SpannableString 
	 * @param label
	 * @return
	 */
	public static Spannable translateSpannableString(String label) {
		return translateSpannableString(label, "");
	}

	/**
	 * Translates the SpannableString
	 * @param label
	 * @param description
	 * @return
	 */
	public static Spannable translateSpannableString(String label,
			String description) {
		return translateSpannableString(label, description, null);
	}

	/**
	 * Translates the SpannableString
	 * @param label
	 * @param description
	 * @param tokens
	 * @return
	 */
	public static Spannable translateSpannableString(String label,
			String description, Map<String, Object> tokens) {
		return translateSpannableString(label, description, tokens, null);
	}

	/**
	 * Translates the SpannableString
	 * @param label
	 * @param tokens
	 * @return
	 */
	public static Spannable translateSpannableString(String label,
			Map<String, Object> tokens) {
		return translateSpannableString(label, null, tokens, null);
	}

	/**
	 * Translates the SpannableString
	 * @param label
	 * @param tokens
	 * @param options
	 * @return
	 */
	public static Spannable translateSpannableString(String label,
			Map<String, Object> tokens, Map<String, Object> options) {
		return translateSpannableString(label, null, tokens, options);
	}
	
	/**
	 * Translates the SpannableString
	 * @param label
	 * @param description
	 * @param tokens
	 * @param options
	 * @return
	 */
	public static Spannable translateSpannableString(String label,
			String description, Map<String, Object> tokens,
			Map<String, Object> options) {
		return (Spannable) com.tr8n.core.Tr8n.getSession()
				.translateStyledString(label, tokens, options);
	}

}
