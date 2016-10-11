package com.translationexchange.android.utils;

import android.content.Context;
import android.util.TypedValue;

public class ViewUtils {

    public static int convertPixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px * scaledDensity);
    }

    public static int convertDpToPixels(float dp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static int convertSpToPixels(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static int convertDpToSp(float dp, Context context) {
        int sp = (int) (convertDpToPixels(dp, context) / (float) convertSpToPixels(dp, context));
        return sp;
    }
}
