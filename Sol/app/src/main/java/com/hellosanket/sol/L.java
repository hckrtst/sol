package com.hellosanket.sol;

import android.util.Log;

/**
 * Created by sanket on 5/8/16.
 */
public final class L {
    final static boolean DBG = true;

    public static void d(String TAG, String msg) {
        //if (DBG) {
            Log.d("Sol: " + TAG, msg);
        //}
    }

    public static void w(String TAG, String msg) {
        Log.w("Sol: " +TAG, msg);
    }

    public static void e(String TAG, String msg) {
        Log.e("Sol: " +TAG, msg);
    }
}
