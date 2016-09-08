package com.translationexchange.android.model;

import android.text.format.DateUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.translationexchange.android.TmlAndroid;
import com.translationexchange.core.Utils;

import java.util.GregorianCalendar;

/**
 * Created by ababenko on 9/7/16.
 */
public class Auth {
    private String status;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("created_at")
    private long createdAt;
    private Project project;
    private Translator translator;

    public String getStatus() {
        return status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isExpired() {
        long now = GregorianCalendar.getInstance().getTimeInMillis();
        return (now - createdAt) >= 12 * DateUtils.HOUR_IN_MILLIS;
    }

    public Project getProject() {
        return project;
    }

    public Translator getTranslator() {
        return translator;
    }

    public static Auth getAuth() {
        String auth = (String) TmlAndroid.getCache().fetch("auth", Utils.buildMap());
        if (auth != null) {
            byte[] dataDecoded = Base64.decode(auth, Base64.DEFAULT);
            String s = new String(dataDecoded);
            Gson gson = new Gson();
            return gson.fromJson(s, Auth.class);
        }
        return null;
    }

    public static void saveAuth(String message) {
        byte[] dataDecoded = Base64.decode(message, Base64.DEFAULT);
        String s = new String(dataDecoded);
        Gson gson = new Gson();
        Auth auth = gson.fromJson(s, Auth.class);
        auth.createdAt = GregorianCalendar.getInstance().getTimeInMillis();
        message = gson.toJson(auth);
        byte[] bytes = Base64.encode(message.getBytes(), Base64.DEFAULT);
        TmlAndroid.getCache().store("auth", new String(bytes), Utils.buildMap());
    }

    public static void clear(){
        TmlAndroid.getCache().delete("auth", Utils.buildMap());
    }

    @Override
    public String toString() {
        return "Auth {" +
                "status='" + status + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", project=" + project +
                ", translator=" + translator +
                '}';
    }

    public static class Project {
        private String name;

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Project {" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static class Translator {
        private int id;
        @SerializedName("first_name")
        private String firstName;
        @SerializedName("last_name")
        private String lastName;
        @SerializedName("display_name")
        private String displayName;
        private String role;

        public int getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getRole() {
            return role;
        }

        @Override
        public String toString() {
            return "Translator {" +
                    "id=" + id +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", role='" + role + '\'' +
                    '}';
        }
    }
}
