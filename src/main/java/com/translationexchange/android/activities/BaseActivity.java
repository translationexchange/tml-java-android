package com.translationexchange.android.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.translationexchange.android.interfaces.TmlAnnotation;
import com.translationexchange.android.text.TmlContextWrapper;

/**
 * Created by ababenko on 9/8/16.
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @TmlAnnotation
    public void initUi() {
    }

    ;

    public void enableBackButton(boolean isHomeButtonEnabled) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeButtonEnabled(isHomeButtonEnabled);
            actionBar.setDisplayHomeAsUpEnabled(isHomeButtonEnabled);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(TmlContextWrapper.wrap(base));
    }
}
