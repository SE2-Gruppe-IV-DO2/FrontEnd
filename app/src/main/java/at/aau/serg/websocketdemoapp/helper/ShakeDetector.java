package at.aau.serg.websocketdemoapp.helper;

import static android.content.Context.SENSOR_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 2.5f;
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 1000;
    private long mLastShakeTime;
    private SensorManager mSensorMgr;
    private Sensor accelerometer;
    private final Activity activity;
    private static final String TAG = "SHAKE_DETECTOR";
    private final ShakeListener listener;

    public ShakeDetector(Activity activity, ShakeListener listener){
        this.activity = activity;
        this.listener = listener;
        init();
        startListening();
    }

    private void init(){
        mSensorMgr = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        assert mSensorMgr!=null;
        accelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

                if (acceleration > SHAKE_THRESHOLD) {
                    mLastShakeTime = curTime;
                    listener.onShake();
                    Log.d(TAG, "Shake, Rattle, and Roll");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void stopListening(){
        mSensorMgr.unregisterListener(this);
    }
    public void startListening(){
        if (accelerometer != null) {
            mSensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void setShakeThreshold(float threshold){
        threshold = SHAKE_THRESHOLD;
    }
    public void setMinTimeBetweenShakesMillisecs(int time){
        time = MIN_TIME_BETWEEN_SHAKES_MILLISECS;
    }
}
