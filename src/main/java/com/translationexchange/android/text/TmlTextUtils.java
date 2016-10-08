package com.translationexchange.android.text;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;

final class TmlTextUtils {

    private static final int[] ANDROID_ATTR_TEXT_APPEARANCE = new int[]{android.R.attr.textAppearance};

    /**
     * Tries to pull the Custom Attribute directly from the TextView.
     *
     * @param context     Activity Context
     * @param attrs       View Attributes
     * @param attributeId if -1 returns null.
     * @return null if attribute is not defined or added to View
     */
    static String pullFontPathFromView(Context context, AttributeSet attrs, int[] attributeId) {
        if (attributeId == null || attrs == null)
            return null;

        final String attributeName;
        try {
            attributeName = context.getResources().getResourceEntryName(attributeId[0]);
        } catch (Resources.NotFoundException e) {
            // invalid attribute ID
            return null;
        }

        final int stringResourceId = attrs.getAttributeResourceValue(null, attributeName, -1);
        return stringResourceId > 0 ? context.getString(stringResourceId) : attrs.getAttributeValue(null, attributeName);
    }

    /**
     * Tries to pull the Font Path from the View Style as this is the next decendent after being
     * defined in the View's xml.
     *
     * @param context     Activity Activity Context
     * @param attrs       View Attributes
     * @param attributeId if -1 returns null.
     * @return null if attribute is not defined or found in the Style
     */
    static String pullFontPathFromStyle(Context context, AttributeSet attrs, int[] attributeId) {
        if (attributeId == null || attrs == null)
            return null;
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, attributeId);
        if (typedArray != null) {
            try {
                // First defined attribute
                String fontFromAttribute = typedArray.getString(0);
                if (!TextUtils.isEmpty(fontFromAttribute)) {
                    return fontFromAttribute;
                }
            } catch (Exception ignore) {
                // Failed for some reason.
            } finally {
                typedArray.recycle();
            }
        }
        return null;
    }

    /**
     * Tries to pull the Font Path from the Text Appearance.
     *
     * @param context     Activity Context
     * @param attrs       View Attributes
     * @param attributeId if -1 returns null.
     * @return returns null if attribute is not defined or if no TextAppearance is found.
     */
    static String pullFontPathFromTextAppearance(final Context context, AttributeSet attrs, int[] attributeId) {
        if (attributeId == null || attrs == null) {
            return null;
        }

        int textAppearanceId = -1;
        final TypedArray typedArrayAttr = context.obtainStyledAttributes(attrs, ANDROID_ATTR_TEXT_APPEARANCE);
        if (typedArrayAttr != null) {
            try {
                textAppearanceId = typedArrayAttr.getResourceId(0, -1);
            } catch (Exception ignored) {
                // Failed for some reason
                return null;
            } finally {
                typedArrayAttr.recycle();
            }
        }

        final TypedArray textAppearanceAttrs = context.obtainStyledAttributes(textAppearanceId, attributeId);
        if (textAppearanceAttrs != null) {
            try {
                return textAppearanceAttrs.getString(0);
            } catch (Exception ignore) {
                // Failed for some reason.
                return null;
            } finally {
                textAppearanceAttrs.recycle();
            }
        }
        return null;
    }

    private static Boolean sToolbarCheck = null;

    /**
     * See if the user has added appcompat-v7, this is done at runtime, so we only check once.
     *
     * @return true if the v7.Toolbar is on the classpath
     */
    static boolean canCheckForV7Toolbar() {
        if (sToolbarCheck == null) {
            try {
                Class.forName("android.support.v7.widget.Toolbar");
                sToolbarCheck = Boolean.TRUE;
            } catch (ClassNotFoundException e) {
                sToolbarCheck = Boolean.FALSE;
            }
        }
        return sToolbarCheck;
    }

    private TmlTextUtils() {
    }

}
