package com.translationexchange.android.text;

import android.view.View;

import com.translationexchange.android.R;
import com.translationexchange.android.TmlAndroid;
import com.translationexchange.android.activities.MobileTranslationCenterActivity;
import com.translationexchange.android.model.Auth;
import com.translationexchange.android.utils.PreferenceUtil;
import com.translationexchange.core.tools.Tools;

/**
 * Created by ababenko on 10/10/16.
 */

public class TmlLongClickListener implements View.OnLongClickListener {
    @Override
    public boolean onLongClick(View view) {
        if (TmlAndroid.getAuth() != null && TmlAndroid.getAuth().isInlineMode() && TmlAndroid.getAndroidApplication() != null && TmlAndroid.getAndroidApplication().getTools() != null) {
            Object o = view.getTag(R.id.tml_key_hash_id);
            if (o != null) {
                Tools tools = TmlAndroid.getAndroidApplication().getTools();
                Auth auth = TmlAndroid.getAuth();
                String keyHash = o.toString();
                TmlAndroid.getLogger().debug("Long_Click", "Key hash - " + keyHash);
                String url = tools.getMobileTranslationCenterKey().replace("{translation_key}", keyHash).replace("{access_token}", auth.getAccessToken()).replace("{locale}", PreferenceUtil.getCurrentLocation(view.getContext()).getLanguage());
                MobileTranslationCenterActivity.translate(view.getContext(), url);
                return true;
            }
        }
        return false;
    }
}
