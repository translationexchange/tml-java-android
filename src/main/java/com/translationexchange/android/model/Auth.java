package com.translationexchange.android.model;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.translationexchange.android.TmlAndroid;
import com.translationexchange.core.Utils;

/**
 * Created by ababenko on 9/7/16.
 */
public class Auth {
    private String status;
    @SerializedName("access_token")
    private String accessToken;
    private Project project;
    private Translator translator;

    public String getStatus() {
        return status;
    }

    public String getAccessToken() {
        return accessToken;
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

    public static void saveAuth(String auth) {
        TmlAndroid.getCache().store("auth", auth, Utils.buildMap());
    }

    @Override
    public String toString() {
        return "Auth {" +
                "status='" + status + '\'' +
                ", accessToken='" + accessToken + '\'' +
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
