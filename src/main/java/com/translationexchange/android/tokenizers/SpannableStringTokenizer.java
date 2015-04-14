/**
 * Copyright (c) 2015 Translation Exchange, Inc. All rights reserved.
 *
 *  _______                  _       _   _             ______          _
 * |__   __|                | |     | | (_)           |  ____|        | |
 *    | |_ __ __ _ _ __  ___| | __ _| |_ _  ___  _ __ | |__  __  _____| |__   __ _ _ __   __ _  ___
 *    | | '__/ _` | '_ \/ __| |/ _` | __| |/ _ \| '_ \|  __| \ \/ / __| '_ \ / _` | '_ \ / _` |/ _ \
 *    | | | | (_| | | | \__ \ | (_| | |_| | (_) | | | | |____ >  < (__| | | | (_| | | | | (_| |  __/
 *    |_|_|  \__,_|_| |_|___/_|\__,_|\__|_|\___/|_| |_|______/_/\_\___|_| |_|\__,_|_| |_|\__, |\___|
 *                                                                                        __/ |
 *                                                                                       |___/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.translationexchange.android.tokenizers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.DrawableMarginSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.EasyEditSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.IconMarginSpan;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.LineHeightSpan;
//import android.text.style.LocaleSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.QuoteSpan;
import android.text.style.RasterizerSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuggestionSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TabStopSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.text.style.WrapTogetherSpan;

import com.translationexchange.core.tokenizers.StyledTokenizer;

public class SpannableStringTokenizer extends StyledTokenizer {

    /**
     *
     * @param label
     */
    public SpannableStringTokenizer(String label) {
        this(label, null);
    }

    /**
     *
     * @param label
     * @param allowedTokenNames
     */
    public SpannableStringTokenizer(String label, List<String> allowedTokenNames) {
        super(label, allowedTokenNames);
    }

    protected Object createStyledString(String label) {
    	return new SpannableString(label);
    }
    
	protected void applyStyles(Object styledString, Map<String, Object> styles, List<Map<String, Object>> ranges) {
    	Spannable spannable = (Spannable) styledString;

    	Iterator<Map.Entry<String, Object>> entries = styles.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();

            String styleName = (String) entry.getKey();
            Object styleAttributes = entry.getValue();

            for (Map<String, Object> range : ranges) {
                Integer start = (Integer) range.get(ATTRIBUTE_RANGE_ORIGIN);
                Integer end = start + (Integer) range.get(ATTRIBUTE_RANGE_LENGTH);
 
                if (styleName.equals("size")) {
                	addAbsoluteSizeSpan(spannable, styleAttributes, start, end);
                } else if (styleName.equals("alignment")) {
                	addAlignmentSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("background") || styleName.equals("background-color")) {
	            	addBackgroundColorSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("bullet")) {
	            	addBulletSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("clickable")) {
	            	addClickableSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("drawable-margin")) {
	            	addDrawableMarginSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("dynamic-drawable")) {
	            	addDynamicDrawableSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("easy-edit")) {
	            	addEasyEditSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("color") || styleName.equals("foreground") || styleName.equals("foreground-color")) {
	            	addForegroundColorSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("icon-margin")) {
	            	addIconMarginSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("image")) {
	            	addImageSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("leading-margin")) {
	            	addLeadingMarginSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("line-background")) {
	            	addLineBackgroundSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("line-height")) {
	            	addLineHeightSpan(spannable, styleAttributes, start, end);
//	            } else if (styleName.equals("locale")) {
//	            	addLocaleSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("mask-filter")) {
	            	addMaskFilterSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("metric-affecting")) {
	            	addMetricAffectingSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("quote")) {
	            	addQuoteSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("rasterized")) {
	            	addRasterizerSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("relative-size")) {
	            	addRelativeSizeSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("replacement")) {
	            	addReplacementSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("scale-x")) {
	            	addScaleXSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("strikethrough")) {
	            	addStrikethroughSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("style")) {
                	addStyleSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("subscript")) {
                	addSubscriptSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("suggestion")) {
                	addSuggestionSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("superscript")) {
                	addSuperscriptSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("tab-stop")) {
                	addTabStopSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("text-appearance")) {
                	addTextAppearanceSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("typeface")) {
                	addTypefaceSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("underline")) {
                	addUnderlineSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("url")) {
                	addUrlSpan(spannable, styleAttributes, start, end);
	            } else if (styleName.equals("wrap-together")) {
                	addWrapTogetherSpan(spannable, styleAttributes, start, end);
                }
            }

        }
    }

    protected void addMaskFilterSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((MaskFilterSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addMetricAffectingSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((MetricAffectingSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
        
    protected void addQuoteSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((QuoteSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addRasterizerSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((RasterizerSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addRelativeSizeSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((RelativeSizeSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addReplacementSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((ReplacementSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addScaleXSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((ScaleXSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addStrikethroughSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	if (style instanceof StrikethroughSpan) {
        	spannable.setSpan((StrikethroughSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        	return; 
    	}

    	spannable.setSpan(new StrikethroughSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addSubscriptSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	if (style instanceof SubscriptSpan) {
        	spannable.setSpan((SubscriptSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        	return; 
    	}

    	spannable.setSpan((SubscriptSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addSuggestionSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((SuggestionSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addSuperscriptSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	if (style instanceof SuperscriptSpan) {
        	spannable.setSpan((SuperscriptSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        	return; 
    	}
    	spannable.setSpan(new SuperscriptSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addTabStopSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((TabStopSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addTextAppearanceSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((TextAppearanceSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addUnderlineSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	if (style instanceof UnderlineSpan) {
        	spannable.setSpan((UnderlineSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        	return; 
    	}
    	
    	spannable.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addUrlSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((URLSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addWrapTogetherSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((WrapTogetherSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
//    protected void addLocaleSpan(Spannable spannable, Object style, Integer start, Integer end) {
//    	spannable.setSpan((LocaleSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//	}
    
    protected void addLineHeightSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((LineHeightSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addLineBackgroundSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((LineBackgroundSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addLeadingMarginSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((LeadingMarginSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addEasyEditSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((EasyEditSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addDynamicDrawableSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((DynamicDrawableSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addAlignmentSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((AlignmentSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addImageSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((ImageSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
    protected void addIconMarginSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((IconMarginSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
        
	protected void addDrawableMarginSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((DrawableMarginSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
    
	protected void addClickableSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan((ClickableSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addBulletSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	if (style instanceof BulletSpan) {
        	spannable.setSpan((BulletSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        	return; 
    	}
    	
    	if (style instanceof Map) {
        	int gapWidth = BulletSpan.STANDARD_GAP_WIDTH;
        	int color = Color.BLACK;
    		Map<String, Object> attrs = (Map) style;
    		if (attrs.get("gap-width") != null)
    			gapWidth = (Integer) attrs.get("gap-width");
    		if (attrs.get("color") != null)
    			color = getColor(attrs.get("color"));
        	spannable.setSpan(new BulletSpan(gapWidth, color), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    	}
    }
     
    protected void addForegroundColorSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan(new ForegroundColorSpan(getColor(style)), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    protected void addBackgroundColorSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan(new BackgroundColorSpan(getColor(style)), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }
    
    protected int getColor(Object color) {
    	if (color instanceof Integer)
    		return (Integer) color;

    	if (color.toString().startsWith("#"))
    		return Color.parseColor(color.toString());
    	
    	if (color.equals("red")) return Color.RED;
    	if (color.equals("green")) return Color.GREEN;
    	if (color.equals("white")) return Color.WHITE;
    	if (color.equals("light-gray")) return Color.LTGRAY;
    	if (color.equals("gray")) return Color.GRAY;
    	if (color.equals("dark-gray")) return Color.DKGRAY;
    	if (color.equals("black")) return Color.BLACK;
    	if (color.equals("yellow")) return Color.YELLOW;
    	if (color.equals("magenta")) return Color.MAGENTA;
    	if (color.equals("cyan")) return Color.CYAN;
    	if (color.equals("blue")) return Color.BLUE;
    	if (color.equals("black")) return Color.BLACK;
    	if (color.equals("transparent")) return Color.TRANSPARENT;
    	
    	return Color.BLACK;
    }
        
    protected void addAbsoluteSizeSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	spannable.setSpan(new AbsoluteSizeSpan((Integer) style), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }
    
    protected void addTypefaceSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	if (style instanceof TypefaceSpan) {
        	spannable.setSpan((TypefaceSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        	return; 
    	}
    	
    	if (style instanceof String) {
    		spannable.setSpan(new TypefaceSpan((String) style), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    	}
    }
    
    protected void addStyleSpan(Spannable spannable, Object style, Integer start, Integer end) {
    	if (style instanceof StyleSpan) {
        	spannable.setSpan((StyleSpan) style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        	return; 
    	}

    	if (style instanceof String) {
	    	spannable.setSpan(new StyleSpan(getTypeface((String) style)), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    	}
    }
    
    private int getTypeface(String style) {
    	if (style.equals("normal")) return Typeface.NORMAL;
    	if (style.equals("bold")) return Typeface.BOLD;
    	if (style.equals("italic")) return Typeface.ITALIC;
    	if (style.equals("bold_italic")) return Typeface.BOLD_ITALIC;
    	    	
    	return Typeface.NORMAL;
    }
    
}

