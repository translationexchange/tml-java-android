package com.translationexchange.android.text;

import android.content.res.Resources;
import android.support.annotation.NonNull;

/**
 * Created by ababenko on 11/3/16.
 */

public class TmlResources extends Resources {
    /**
     * Create a new Resources object on top of an existing set of assets in an
     * AssetManager.
     */
    public TmlResources(Resources res) {
        super(res.getAssets(), res.getDisplayMetrics(), res.getConfiguration());
    }

    @NonNull
    @Override
    public String getString(int id) throws NotFoundException {
        return super.getString(id);
    }

    @NonNull
    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        return super.getString(id, formatArgs);
    }
}
