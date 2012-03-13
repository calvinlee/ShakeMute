/**
 * 
 */

package com.calvin.app.shakemute;

import android.util.Log;

/**
 * @author calvin
 */
public class Utility {
    public static boolean sDebug = true;

    public static final String KEY_SAVED_RINGER_MODE = "ringer_mode";

    public static final String ACTION_MUTE_RINGING = "com.calvin.intent.action.MUTE_RINGING";

    public static final String CONF_FILE = "pref.conf";

    private static final String TAG = "ShakeMute";

    public static void logd(String msg) {
        if (sDebug) {
            Log.d(TAG, msg);
        }
    }
}
