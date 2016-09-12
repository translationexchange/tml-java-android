package com.translationexchange.android.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.translationexchange.android.TmlAndroid;

/**
 * Created by ababenko on 9/8/16.
 */
public class ViewUtils {
    public static void findViews(View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                TmlAndroid.getLogger().debug("Find_ViewGroup", vg.getClass().getSimpleName());
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    findViews(child);
                }
            } else if (v instanceof TextView) {
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        TmlAndroid.getLogger().debug("Find_TextView", ((TextView) view).getText().toString());
                        return true;
                    }
                });
                TmlAndroid.getLogger().debug("Find_TextView", ((TextView) v).getText().toString() + "\t" + "-" + "\t" + v.getClass().getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
