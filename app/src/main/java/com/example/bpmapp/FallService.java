package com.example.bpmapp;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/*This activity constantly measures the acceleration based on the device's movement. Once it gets to a threshold
* it sends an alert to the user. In here is also implemented the notification which is used to let users know the detector is
* turned on*/

public class FallService extends Service implements SensorEventListener {

    private static final String CHANNEL_ID = "fall service";
    private static final String TAG = "";
    private Sensor accelerometer;
    private SensorManager sensorManager;
    private long previousTime;
    private boolean motionIsMin;
    private boolean motionIsMax;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Intent notificationIntent = new Intent(this, Fall.class);
        //set the intent to the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Fall Detector")
                .setContentText("Click if you want to deactivate")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_STICKY; //doesn't allow for the process to be killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this, accelerometer);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onSensorChanged(SensorEvent event) {
        int i = 0;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            double oX = event.values[0];
            double oY = event.values[1];
            double oZ = event.values[2];

            double accelerationReader = Math.sqrt(Math.pow(oX, 2)
                    + Math.pow(oY, 2)
                    + Math.pow(oZ, 2));
            previousTime = System.currentTimeMillis();
            Log.i(TAG, "oX : " + oX + " oY : " + oY + " oZ : " + oZ);
            if (accelerationReader <= 6.0) {
                motionIsMin = true;
                Log.i(TAG, "Min threshold");
            }

            if (motionIsMin) {
                i++;
                Log.i(TAG, " Acceleration : " + accelerationReader);
                if (accelerationReader >= 30) {
                    long llCurrentTime = System.currentTimeMillis();
                    long llTimeDiff = llCurrentTime - previousTime;
                    Log.i(TAG, "Time difference :" + llTimeDiff);
                    if (llTimeDiff >= 3) {
                        motionIsMax = true;
                        Log.i(TAG, "Max threshold");
                    }
                }

            }

            if (motionIsMin && motionIsMax) {
                Log.i(TAG, "FALL DETECTED!");
//                Toast.makeText(this, "FALL!", Toast.LENGTH_SHORT).show();
                Intent fallIntent = new Intent(getApplicationContext(), Emergency.class);
                fallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(fallIntent);
                i = 0;
                motionIsMin = false;
                motionIsMax = false;
                return;
            }

            if (i > 5) {
                i = 0;
                motionIsMin = false;
                motionIsMax = false;
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not used
    }
}
