package com.translationexchange.android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ababenko on 10/11/16.
 */

public class MTC {
    private String subject;
    private String action;
    @SerializedName("translation_key")
    private String translationKey;
    @SerializedName("target_locale")
    private String targetLocale;
    private String translation;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTargetLocale() {
        return targetLocale;
    }

    public void setTargetLocale(String targetLocale) {
        this.targetLocale = targetLocale;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
