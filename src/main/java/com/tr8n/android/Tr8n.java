package com.tr8n.android;

import java.util.Map;

import com.tr8n.core.TranslationKey;
import com.tr8n.android.tokenizers.SpannableStringTokenizer;

import android.text.Spannable;

public class Tr8n extends com.tr8n.core.Tr8n {

   public static void init(String key, String secret, String host) {
	   com.tr8n.core.Tr8n.init(key, secret, host);
	   com.tr8n.core.Tr8n.getConfig().addTokenizerClass(TranslationKey.DEFAULT_TOKENIZERS_STYLED, SpannableStringTokenizer.class.getName());
   }
	
   public static Spannable translateSpannableString(String label) {
       return translateSpannableString(label, "");
   }

   public static Spannable translateSpannableString(String label, String description) {
       return translateSpannableString(label, description, null);
   }

   public static Spannable translateSpannableString(String label, String description, Map<String, Object> tokens) {
       return translateSpannableString(label, description, tokens, null);
   }
   
   public static Spannable translateSpannableString(String label, Map<String, Object> tokens) {
       return translateSpannableString(label, null, tokens, null);
   }

   public static Spannable translateSpannableString(String label, Map<String, Object> tokens, Map<String, Object> options) {
       return translateSpannableString(label, null, tokens, options);
   }	

   public static Spannable translateSpannableString(String label, String description, Map<String, Object> tokens, Map<String, Object> options) {
       return (Spannable) com.tr8n.core.Tr8n.getSession().translateStyledString(label, tokens, options);
   }
   
}
