package com.chowdhuryelab.stopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button homeStopWatch, homeCountDown;
    EditText homeCountDownTimeH, homeCountDownTimeM,homeCountDownTimeS;
    long mTime=0;
    long hr, min, sec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeStopWatch = (Button) findViewById(R.id.homeStopwatch);
        homeCountDown = (Button) findViewById(R.id.homeCountDown);
        homeCountDownTimeH = (EditText) findViewById(R.id.homeCountDownTimeH);
        homeCountDownTimeM = (EditText) findViewById(R.id.homeCountDownTimeM);
        homeCountDownTimeS = (EditText) findViewById(R.id.homeCountDownTimeS);


        homeStopWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iStopwatch = new Intent(MainActivity.this, stopwatch.class);
                startActivity(iStopwatch);

            }
        });




        homeCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // mTime = Long.parseLong(homeCountDownTimeH.getText().toString())*3600000 +  Long.parseLong(homeCountDownTimeM.getText().toString())*60000 +  Long.parseLong(homeCountDownTimeS.getText().toString())*1000;

                try {
                    sec = Long.parseLong(String.valueOf(homeCountDownTimeS.getText()));
                    mTime =mTime+ sec*1000;
                }catch (NumberFormatException e) {}

                try {
                    min = Long.parseLong(String.valueOf(homeCountDownTimeM.getText()));
                    mTime =mTime+ min*60000;
                }catch (NumberFormatException e) {}
                try {
                    hr = Long.parseLong(String.valueOf(homeCountDownTimeH.getText()));
                    mTime = mTime + hr*3600000;
                }catch (NumberFormatException e) {}

                if(mTime !=0){
                    long countDownTime = mTime;
                    Intent iCountDown = new Intent(MainActivity.this, countdown.class);
                    iCountDown.putExtra("COUNTDOWN","" + countDownTime);
                    iCountDown.putExtra("S",sec);
                    iCountDown.putExtra("M",min);
                    iCountDown.putExtra("H",hr);
                    startActivity(iCountDown);
                    mTime =0;
                    homeCountDownTimeS.setText("");
                    homeCountDownTimeM.setText("");
                    homeCountDownTimeH.setText("");
                }
            }
        });

    }


}