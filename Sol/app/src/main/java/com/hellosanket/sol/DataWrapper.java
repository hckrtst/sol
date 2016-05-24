package com.hellosanket.sol;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Objects;

/**
 * Created by sanket on 5/23/16.
 */
public class DataWrapper {

    public static void saveInt(final Context context,
                       final String db,
                       final String key,
                       final int value) {
        SharedPreferences pref = context
                .getApplicationContext()
                .getSharedPreferences(db, context.MODE_PRIVATE);

        SharedPreferences.Editor ed = pref.edit();
        ed.putInt(key, value);
        ed.commit();
    }

    public static int readInt(final Context context,
                              final String db,
                              final String key,
                              final int default_value) {
        SharedPreferences pref = context
                .getApplicationContext()
                .getSharedPreferences(db, context.MODE_PRIVATE);
        return pref.getInt(key, default_value);
    }

    public static void saveString(final Context context,
                               final String db,
                               final String key,
                               final String value) {
        SharedPreferences pref = context
                .getApplicationContext()
                .getSharedPreferences(db, context.MODE_PRIVATE);

        SharedPreferences.Editor ed = pref.edit();
        ed.putString(key, value);
        ed.commit();
    }

    public static String readString(final Context context,
                              final String db,
                              final String key,
                              final String default_value) {
        SharedPreferences pref = context
                .getApplicationContext()
                .getSharedPreferences(db, context.MODE_PRIVATE);
        return pref.getString(key, default_value);
    }

    public static void savelong(final Context context,
                                  final String db,
                                  final String key,
                                  final long value) {
        SharedPreferences pref = context
                .getApplicationContext()
                .getSharedPreferences(db, context.MODE_PRIVATE);

        SharedPreferences.Editor ed = pref.edit();
        ed.putLong(key, value);
        ed.commit();
    }

    public static long readlong(final Context context,
                                    final String db,
                                    final String key,
                                    final long default_value) {
        SharedPreferences pref = context
                .getApplicationContext()
                .getSharedPreferences(db, context.MODE_PRIVATE);
        return pref.getLong(key, default_value);
    }


}
