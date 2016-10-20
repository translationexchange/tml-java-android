package com.translationexchange.android.text;

import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.translationexchange.android.TmlAndroid;
import com.translationexchange.android.logger.Logger;

import java.lang.ref.WeakReference;

/**
 * Created by ababenko on 10/6/16.
 */

class TmlTextWatcher implements TextWatcher {

    private WeakReference<TextView> textView;
    private Logger logger;

    TmlTextWatcher(TextView textView, Logger logger) {
        this.textView = new WeakReference<>(textView);
        this.logger = logger;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            TextView view = textView.get();
            if (view != null) {
                logger.debug("onTextChanged set new", s.toString());
                if (!(s instanceof Spannable)) {
                    view.removeTextChangedListener(this);
                    view.setText(TmlAndroid.translate(s.toString()));
                    view.addTextChangedListener(this);
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
