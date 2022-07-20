package com.chowdhuryelab.stopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class countdown extends AppCompatActivity {

    private final static String TAG = "countdown";
    TextView textViewTimer = null,textViewTimerS =null;
    ImageView playBtn, pauseBtn, stopBtn;
    long mTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);
        this.setTitle("CountDown");

        textViewTimer = (TextView) findViewById(R.id.textViewTimer);
        textViewTimerS = (TextView) findViewById(R.id.textViewTimerS);

        playBtn = (ImageView) findViewById(R.id.playBtn);
        pauseBtn = (ImageView) findViewById(R.id.pauseBtn);
        stopBtn = (ImageView) findViewById(R.id.stopBtn);

        Intent iCount = getIntent();
        Bundle b = iCount.getExtras();

        if (b != null) {
            String Count = (String) b.get("COUNTDOWN");
            mTime = Long.parseLong(Count);

            //textViewTimer.setText(b.get("H")+":"+b.get("M")+":"+b.get("S"));
            textViewTimer.setText(GUITimer(mTime));
        }

        Intent iStart = new Intent(countdown.this, CountdownService.class);

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
                Toast.makeText(countdown.this, "Started", Toast.LENGTH_SHORT).show();

                // hide the play button
                playBtn.setVisibility(View.GONE);

                // show the pause  and stop lapse button
                pauseBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.VISIBLE);
            }
        });

        // pause button click listener
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopService(iStart);

                //showing simple toast message to user
                Toast.makeText(countdown.this, "Paused", Toast.LENGTH_SHORT).show();

                // show the play  and stop  button
                playBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.VISIBLE);
                // hide the play button
                pauseBtn.setVisibility(View.GONE);

            }
        });

        // stop  button click listener
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(iStart);
                mTime = 0;
                textViewTimer.setText("00:00:00");
                textViewTimerS.setText("000");


                //showing simple toast message to user
                Toast.makeText(countdown.this, "Stoped", Toast.LENGTH_SHORT).show();

                // show the play
                playBtn.setVisibility(View.VISIBLE);

                // hide the pause , stop and time lapse button
                pauseBtn.setVisibility(View.GONE);
                stopBtn.setVisibility(View.GONE);

                Intent stopC = new Intent(countdown.this,MainActivity.class);
                startActivity(stopC);
                finish();
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
        registerReceiver(br, new IntentFilter(CountdownService.Countdown_Service));
    }

    @Override
    protected void onStop() {
        unregisterReceiver(br);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, CountdownService.class));
        super.onDestroy();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            mTime = intent.getLongExtra("mTime",0);
            String hours = intent.getStringExtra("hours");
            String minutes = intent.getStringExtra("minutes");
            String seconds = intent.getStringExtra("seconds");
            String milliseconds = intent.getStringExtra("milliseconds");

            textViewTimer.setText(hours + ":" + minutes + ":" + seconds);
            textViewTimerS.setText("."+milliseconds);

            if(mTime==0)
                finish();

            System.out.println("mTime:>>>>"+mTime +"\n"+ ">>>>>>>>>>hours: "+hours+ ", minutes: "+minutes+", seconds: "+seconds);
        }
    }

    private String GUITimer (long time){
        long secs = (long) (time / 1000);
        long mins = (long)((time/1000)/60);
        long hrs = (long)(((time/1000)/60)/60);
        mTime = (long) time;

        // Convert the seconds to String
        secs = secs % 60;
        String seconds=String.valueOf(secs);
        if(secs == 0){
            seconds = "00";
        }
        if(secs <10 && secs > 0){
            seconds = "0"+seconds;
        }

        // Convert the minutes
        mins = mins % 60;
        String minutes=String.valueOf(mins);
        if(mins == 0){
            minutes = "00";
        }
        if(mins <10 && mins > 0){
            minutes = "0"+minutes;
        }

        //Convert the hours
        String hours=String.valueOf(hrs);
        if(hrs == 0){
            hours = "00";
        }
        if(hrs <10 && hrs > 0){
            hours = "0"+hours;
        }

        //Convert millisecconds
        String milliseconds = String.valueOf((long)time);
        if(milliseconds.length()==2){
            milliseconds = "0"+milliseconds;
        }
        else if(milliseconds.length()<=1){
            milliseconds = "00";
        }
        else milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-1);

        return hours+ ":"+minutes+":"+seconds;
    }//end of UpdateTimer Function
}