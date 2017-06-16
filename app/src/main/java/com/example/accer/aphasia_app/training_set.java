package com.example.accer.aphasia_app;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class training_set extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    Button tickButton,untickButton;
    Button cue1,cue2,cue3,cue4;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_set);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        tickButton=(Button)findViewById(R.id.tick);
        tickButton.setOnClickListener(this);
        untickButton=(Button)findViewById(R.id.untick);

        cue1=(Button)findViewById(R.id.cue1);
        cue2=(Button)findViewById(R.id.cue2);
        cue3=(Button)findViewById(R.id.cue3);
        cue4=(Button)findViewById(R.id.cue4);

        cue1.setOnClickListener(this);
        cue2.setOnClickListener(this);
        cue3.setOnClickListener(this);
        cue4.setOnClickListener(this);

        untickButton.setOnClickListener(this);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tick:MediaPlayer tickPlayer=MediaPlayer.create(this,R.raw.tick_sound);
                tickPlayer.start();
                break;
            case R.id.untick:MediaPlayer untickPlayer=MediaPlayer.create(this, R.raw.untick_sound);
                untickPlayer.start();
                break;
            case R.id.cue1:MediaPlayer.create(this,R.raw.sound1).start();
                break;
            case R.id.cue2:MediaPlayer.create(this,R.raw.sound2).start();
                break;
            case R.id.cue3:MediaPlayer.create(this,R.raw.sound3).start();
                break;
            case R.id.cue4:MediaPlayer.create(this,R.raw.sound4).start();
        }
        new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                String str="" + millisUntilFinished / 1000;
            }

            public void onFinish() {

            }
        }.start();
    }
}
