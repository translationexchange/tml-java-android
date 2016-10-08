package com.translationexchange.android.text;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

public class TmlContextWrapper extends ContextWrapper {

    private TmlLayoutInflater mInflater;

    public static ContextWrapper wrap(Context base) {
        return new TmlContextWrapper(base);
    }

    TmlContextWrapper(Context base) {
        super(base);
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mInflater == null) {
                mInflater = new TmlLayoutInflater(LayoutInflater.from(getBaseContext()), this, false);
            }
            return mInflater;
        }
        return super.getSystemService(name);
    }
}
