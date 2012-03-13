/**
 * 
 */

package com.calvin.app.shakemute;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;

/**
 * @author Calvin.Lee<lihao921@gmail.com> @ Tue Mar 13 14:38:21 CST 2012
 *
 */
public class ShakeMuteService extends Service {

    private AudioManager mAudioManager;

    private SensorManager mSensorManager;

    private SensorListener mSensorListener = new SensorListener();

    private Sensor mSensor;

    private long mLastUpdate = -1;

    private static final int SHAKE_THRESHOLD = 1000;

    public void onCreate() {

        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mSensor == null) {
            Utility.logd("Your device does not have a accelerometer sensor!");
            stopSelf();
            return;
        }

        boolean accelSupported = mSensorManager.registerListener(mSensorListener, mSensor,
                SensorManager.SENSOR_DELAY_GAME);
        if (!accelSupported) {
            Utility.logd("Register accelerometer sensor monitor failed!");
            stopSelf();
        }

        Utility.logd("Flip service started");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class SensorListener implements SensorEventListener {
        float x, y, z, lastX, lastY, lastZ;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - mLastUpdate) > 100) {
                long diffTime = (curTime - mLastUpdate);
                mLastUpdate = curTime;

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    // TODO;
                    int ringerMode = mAudioManager.getRingerMode();
                    if (ringerMode != AudioManager.RINGER_MODE_SILENT
                            || ringerMode != AudioManager.RINGER_MODE_VIBRATE) {
                        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                        // Backup the original ringer mode setup
                        getSharedPreferences(Utility.CONF_FILE, Context.MODE_PRIVATE).edit()
                                .putInt(Utility.KEY_SAVED_RINGER_MODE, ringerMode).commit();
                        Utility.logd("Shaking, set ringer mode to silent");
                    }
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onDestroy() {
        Utility.logd("Flip service destroyed");
        if (mSensor != null) {
            mSensorManager.unregisterListener(mSensorListener, mSensor);
        }
        super.onDestroy();
    }

}
