package com.chowdhuryelab.stopwatch;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class CountdownService extends Service {

    private final static String TAG = "CountdownService";
    public static final String Countdown_Service = "com.chowdhuryelab.stopwatch.countdown";

    private long startTime, passedTime, mTime;

    private final int RefreshRate = 100;

    private String  milliseconds, seconds, minutes, hours;
    private long secs,mins,hrs;

    private boolean running = false;

    Intent ibroadCast = new Intent(Countdown_Service);
    private Handler mHandler = new Handler();

    private PowerManager.WakeLock wl = null;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "OnCreate Starting...");

        PowerManager pm = (PowerManager)getApplicationContext().getSystemService(
                getApplicationContext().POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();

        if (running) {

            startTime = passedTime;
        }
        else {

            startTime = System.currentTimeMillis()+passedTime;

        }

        mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "OnStartCommand Starting...");

        Bundle b = intent.getExtras();
        if(b != null) {
            Long cx = b.getLong("pTime");
            try {
                passedTime = cx;
            } catch (NumberFormatException ex){}


        }

        startTime = System.currentTimeMillis()+passedTime;

        return START_STICKY;
    }


    public void onStop() {
        Log.i(TAG, "onStop()");
    }
    public void onPause() {
        Log.i(TAG, "onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service Destroyed...");
        //wl.release();
        System.out.println("Stop TIme: "+passedTime);
        mHandler.removeCallbacks(startTimer);
        running = true;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Runnable startTimer = new Runnable() {
        @Override
        public void run() {
            passedTime = startTime - System.currentTimeMillis();
            if(passedTime > 0){
                updateTimer(passedTime);
            }
            else {
                updateTimer(0);
                playSound();
                stopSelf();

            }
            mHandler.postDelayed(this, RefreshRate);

        }
    };

    public void playSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTimer (float time){
        secs = (long)(time/1000);
        mins = (long)((time/1000)/60);
        hrs = (long)(((time/1000)/60)/60);
        mTime = (long) time;

        // Convert the seconds to String
        secs = secs % 60;
        seconds=String.valueOf(secs);
        if(secs == 0){
            seconds = "00";
        }
        if(secs <10 && secs > 0){
            seconds = "0"+seconds;
        }

        // Convert the minutes
        mins = mins % 60;
        minutes=String.valueOf(mins);
        if(mins == 0){
            minutes = "00";
        }
        if(mins <10 && mins > 0){
            minutes = "0"+minutes;
        }

        //Convert the hours
        hours=String.valueOf(hrs);
        if(hrs == 0){
            hours = "00";
        }
        if(hrs <10 && hrs > 0){
            hours = "0"+hours;
        }

        //Convert millisecconds
        milliseconds = String.valueOf((long)time);
        if(milliseconds.length()==2){
            milliseconds = "0"+milliseconds;
        }
        else if(milliseconds.length()<=1){
            milliseconds = "00";
        }
        else milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-0);

        ibroadCast.putExtra("mTime",mTime);
        ibroadCast.putExtra("hours", hours);
        ibroadCast.putExtra("minutes", minutes);
        ibroadCast.putExtra("seconds", seconds);
        ibroadCast.putExtra("milliseconds", milliseconds);
        sendBroadcast(ibroadCast); // Sending BroadCast

    }//end of UpdateTimer Function
}