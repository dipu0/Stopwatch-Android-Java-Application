package com.chowdhuryelab.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class stopwatch extends AppCompatActivity {

    private final static String TAG = "stopwatch";
    TextView timeLapse=null, textViewTimer = null,textViewTimerS =null;
    ImageView playBtn, pauseBtn, stopBtn, timeLapseBtn;
    String hours, minutes, seconds, milliseconds;
    int lapCount=0;
boolean isRunning, isPaused, isStoped ;
    long mTime;
    private long saveIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        this.setTitle("StopWatch");

        textViewTimer = (TextView) findViewById(R.id.textViewTimer);
        textViewTimerS = (TextView) findViewById(R.id.textViewTimerS);
        timeLapse = (TextView) findViewById(R.id.timeLapse);

        playBtn=(ImageView)findViewById(R.id.playBtn) ;
        pauseBtn=(ImageView)findViewById(R.id.pauseBtn) ;
        stopBtn=(ImageView)findViewById(R.id.stopBtn) ;

        timeLapseBtn=(ImageView)findViewById(R.id.timeLapseBtn) ;

        Intent iStart = new Intent(stopwatch.this, StopwatchService.class);

        //restore save time
        if(savedInstanceState != null){
            mTime = savedInstanceState.getLong("RestoremTime");
            iStart.putExtra("pTime", mTime);
            startService(iStart);
        }

        // play button click listener
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iStart.putExtra("pTime", mTime);
                startService(iStart);
                //showing simple toast message to user
                Toast.makeText(stopwatch.this, "Started", Toast.LENGTH_SHORT).show();

                // hide the play and stop button
                playBtn.setVisibility(View.GONE);
                stopBtn.setVisibility(View.GONE);

                // show the pause  and time lapse button
                pauseBtn.setVisibility(View.VISIBLE);
                timeLapseBtn.setVisibility(View.VISIBLE);

                isRunning = true;
            }
        });

        // pause button click listener
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopService(iStart);

                //showing simple toast message to user
                Toast.makeText(stopwatch.this, "Paused", Toast.LENGTH_SHORT).show();

                // show the play  and stop  button
                playBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.VISIBLE);

                // hide the pause  and time lapse button
                timeLapseBtn.setVisibility(View.GONE);
                pauseBtn.setVisibility(View.GONE);

                isPaused = true;
            }
        });

        // stop  button click listener
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(iStart);
                mTime = 0;
                textViewTimer.setText("00:00:00");
                textViewTimerS.setText(".000");


                //showing simple toast message to user
                Toast.makeText(stopwatch.this, "Stoped", Toast.LENGTH_SHORT).show();

                timeLapse.setText("");

                // show the play
                playBtn.setVisibility(View.VISIBLE);

                // hide the pause , stop and time lapse button
                pauseBtn.setVisibility(View.GONE);
                stopBtn.setVisibility(View.GONE);
                timeLapseBtn.setVisibility(View.GONE);

                isStoped = true;
            }
        });

        // lap button click listener
        timeLapseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lapCount++;
                // calling timeLapse function


                if (lapCount >= 10) {
                    timeLapse.setText("Lap " + lapCount + "     >"+"hours: "+hours+ ", minutes: "+minutes+", seconds: "+seconds+ "\n" + timeLapse.getText());
                } else {
                    timeLapse.setText("Lap " + lapCount + "       >"+"hours: "+ hours + ", minutes: " + minutes + ", seconds: " + seconds + "\n" + timeLapse.getText());
                    Toast.makeText(stopwatch.this, "Lap " + lapCount, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    //Store mTime in on save instance state
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("RestoremTime",mTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(StopwatchService.STOPWATCH_Service));
    }

    @Override
    protected void onStop() {
        unregisterReceiver(br);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, StopwatchService.class));
        super.onDestroy();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            mTime = intent.getLongExtra("mTime",0);
            hours = intent.getStringExtra("hours");
            minutes = intent.getStringExtra("minutes");
            seconds = intent.getStringExtra("seconds");
            milliseconds = intent.getStringExtra("milliseconds");

            textViewTimer.setText(hours + ":" + minutes + ":" + seconds);
            textViewTimerS.setText("." + milliseconds);


            System.out.println("mTime:>>>>"+mTime +"\n"+ ">>>>>>>>>>hours: "+hours+ ", minutes: "+minutes+", seconds: "+seconds);
        }
    }

}