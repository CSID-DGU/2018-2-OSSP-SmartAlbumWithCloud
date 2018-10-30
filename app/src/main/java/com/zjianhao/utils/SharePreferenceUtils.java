package com.zjianhao.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zjianhao on 16-6-15.
 */
public class SharePreferenceUtils {

    public static void save(Context context,String name,String key,String value){
        SharedPreferences preferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public static void save(Context context,String name,String key,boolean value){
        SharedPreferences preferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,value);
        editor.commit();

    }
    public static String getValue(Context context,String name,String key){
        SharedPreferences preferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        return preferences.getString(key,null);
    }
}
