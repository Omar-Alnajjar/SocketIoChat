package com.omi.socketiochat.main_activity.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by omar on 2/24/18.
 */

public class UserPref {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String USERNAME = "username";

    public UserPref(Context context) {
        prefs = context.getSharedPreferences("chat_user_pref", MODE_PRIVATE);
        editor = prefs.edit();
    }



    public void setUsername(String username){
        editor.putString(USERNAME, username);
        editor.commit();
    }
    public String getUsername(){
        return prefs.getString(USERNAME, null);
    }
}
