package com.translationexchange.android.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.translationexchange.android.TmlAndroid;
import com.translationexchange.android.activities.MobileTranslationCenterActivity;
import com.translationexchange.android.model.Auth;
import com.translationexchange.core.TranslationKey;
import com.translationexchange.core.tools.Tools;

/**
 * Created by ababenko on 9/8/16.
 */
public class ViewUtils {
    public static void findViews(View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
//                TmlAndroid.getLogger().debug("Find_ViewGroup", vg.getClass().getSimpleName());
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    findViews(child);
                }
            } else if (v instanceof TextView) {
//                if (TmlAndroid.getAndroidApplication() != null && TmlAndroid.getAndroidApplication().getAuth() != null && TmlAndroid.getAndroidApplication().getAuth().isInlineMode() && TmlAndroid.getAndroidApplication().getTools() != null) {
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (TmlAndroid.getAndroidApplication() != null && TmlAndroid.getAndroidApplication().getAuth() != null && TmlAndroid.getAndroidApplication().getAuth().isInlineMode() && TmlAndroid.getAndroidApplication().getTools() != null) {
                            String text = ((TextView) view).getText().toString();
                            Tools tools = TmlAndroid.getAndroidApplication().getTools();
                            Auth auth = TmlAndroid.getAndroidApplication().getAuth();
                            String keyHash = TranslationKey.generateKey(text);
                            String url = tools.getMobileTranslationCenterKey().replace("{translation_key}", keyHash).replace("{access_token}", auth.getAccessToken()).replace("{locale}", PreferenceUtil.getCurrentLocation(view.getContext()).getLanguage());
                            MobileTranslationCenterActivity.translate(view.getContext(), url);
                            return true;
                        }
                        return false;
                    }
                });
//                }
//                TmlAndroid.getLogger().debug("Find_TextView", ((TextView) v).getText().toString() + "\t" + "-" + "\t" + v.getClass().getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
