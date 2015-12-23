package com.buthmathearo.articlemanagement.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Buth Mathearo on 12/3/2015.
 */
public class UserLogin {

    private int id;
    private String username;
    private String password;
    private String role;
    private boolean isLogin = false;
    private boolean isRemember = false;
    private Context mContext;
    private final String sharedPrefFileName = "user_login";

    // KEY
    public static final String USER_ID = "USER_ID";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String ROLE = "ROLE";
    public static final String IS_LOGIN = "IS_LOGIN";
    public static final String IS_REMEMBER = "IS_REMEMBER";

    // Default Constructor
    public UserLogin(Context ctx) {
        mContext = ctx;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isRemember() {
        return isRemember;
    }

    public void setRemember(boolean remember) {
        isRemember = remember;
    }

    // Create Shared Preference file based on giving File name.
    public void writeToSharedPrefFile() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(USER_ID, getId());
        editor.putString(USERNAME, getUsername());
        editor.putString(PASSWORD, getPassword());
        editor.putString(ROLE, getRole());
        editor.putBoolean(IS_LOGIN, isLogin());
        editor.putBoolean(IS_REMEMBER, isRemember());
        editor.commit();
    }

    public void readFromSharedPrefFile() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        id = sharedPref.getInt(USER_ID, 0);
        username = sharedPref.getString(USERNAME, null);
        password = sharedPref.getString(PASSWORD, null);
        role = sharedPref.getString(ROLE, null);
        isRemember = sharedPref.getBoolean(IS_REMEMBER, false);
        isLogin = sharedPref.getBoolean(IS_LOGIN, false);
    }

    public void clear() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }


}
