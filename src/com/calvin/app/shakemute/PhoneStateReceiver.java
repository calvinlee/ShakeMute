/**
 * 
 */

package com.calvin.app.shakemute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;

/**
 * @author Calvin.Lee<lihao921@gmail.com> @ Tue Mar 13 14:34:11 CST 2012
 */
public class PhoneStateReceiver extends BroadcastReceiver {
    private Intent mServiceIntent = new Intent(Utility.ACTION_MUTE_RINGING);

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
            AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);

            Bundle bundle = intent.getExtras();
            int state = bundle.getInt(TelephonyManager.EXTRA_STATE);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Utility.logd("CALL_STATE_RINGING");
                    context.startService(mServiceIntent);
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Utility.logd("CALL_STATE_OFFHOOK");
                case TelephonyManager.CALL_STATE_IDLE:
                    SharedPreferences pref = context.getSharedPreferences(Utility.CONF_FILE,
                            Context.MODE_PRIVATE);
                    int savedMode = pref.getInt(Utility.KEY_SAVED_RINGER_MODE, -1);
                    if (savedMode != -1) {
                        // recover the original ringer mode setup
                        audioManager.setRingerMode(savedMode);
                        pref.edit().putInt(Utility.KEY_SAVED_RINGER_MODE, -1).commit();
                        context.stopService(mServiceIntent);
                        Utility.logd("recover ringer mode to " + savedMode);
                    }
                    Utility.logd("CALL_STATE_IDLE");
                    break;
            }
        }
    }
}
