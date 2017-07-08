package com.example.accer.aphasia_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class FollowUp extends AppCompatActivity implements View.OnClickListener {
    ImageView img;
    ImageButton btntick,btnuntic;
    RelativeLayout r1,r3;
    PowerManager pm;
    PowerManager.WakeLock wl;
    Button timer;
    final int interval=21;
    int position=0,threashold=100;
    ADB db;
    CountDownTimer countDownTimer;
    MediaPlayer player;
    ProgressBar progressBar;
    TextView txtProgress;
    Meta meta;
    String[] pics=null;
    boolean doubleBackToExitPressedOnce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_up);
        r1=(RelativeLayout)findViewById(R.id.relative_image);
        r3=(RelativeLayout)findViewById(R.id.relative_check);
        img=(ImageView)findViewById(R.id.img_training);
        btntick=(ImageButton)findViewById(R.id.btn_tick);
        btnuntic=(ImageButton)findViewById(R.id.btn_untick);
        timer=(Button)findViewById(R.id.btn_timer);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        txtProgress=(TextView)findViewById(R.id.txt_progress);
        db=new ADB(this);
        meta=new Meta(this);

        if(meta.read()!=null){
            meta=meta.read();
            position=meta.getBaselinePosition();
        }
        pics = db.getDataPics("valid", "1",("0,"+(Home.FOLLOW_UP_DAY*meta.getNoOfQuestions())));
        threashold=pics.length;


        if(meta.isDayTenFollowUpOver()){
            finish();
            Intent in=new Intent(getApplicationContext(),Home.class);
            startActivity(in);
        }
        countDownTimer=new CountDownTimer(interval*1000,1000) {
            @Override
            public void onTick(long l) {
                timer.setText(""+(l/1000));
                if((l/1000)<=5){
                    Animation animation=AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    timer.startAnimation(animation);
                }else
                    timer.clearAnimation();
                if((l/1000)==1) {
                    stopPlayer();
                    player = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
                    player.start();
                }

            }
            @Override
            public void onFinish() {
                db.updateDayTen(img.getTag().toString(),0);
                setImg();
            }
        };
        countDownTimer.start();

        setImg();


        btntick.setOnClickListener(this);
        btnuntic.setOnClickListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        ViewGroup.LayoutParams lp=r1.getLayoutParams();
        lp.width= (int) (width*.65);
        lp.height= (int) (height*.75);
        r1.setLayoutParams(lp);

        lp=r3.getLayoutParams();
        lp.width= (int) (width*.26);
        lp.height= (int) (height*.6);
        r3.setLayoutParams(lp);

        lp=btntick.getLayoutParams();
        lp.width= (int) (width*.15);
        lp.height= (int) (height*.15);
        btntick.setLayoutParams(lp);

        lp=btnuntic.getLayoutParams();
        lp.width= (int) (width*.15);
        lp.height= (int) (height*.15);
        btnuntic.setLayoutParams(lp);


        lp=progressBar.getLayoutParams();
        lp.width= (int) (width*.25);
        lp.height= (int) (height*.15);
        progressBar.setLayoutParams(lp);
        progressBar.invalidate();

    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.btn_tick :
                stopPlayer();
                player=MediaPlayer.create(this,R.raw.tick_sound);
                player.start();
                db.updateDayTen(img.getTag().toString(),1);
                setImg();
                break;

            case R.id.btn_untick :
                stopPlayer();
                player=MediaPlayer.create(this,R.raw.untick_sound);
                player.start();
                db.updateDayTen(img.getTag().toString(),0);
                setImg();
                break;
        }
    }
    void setImg(){
        if(position<threashold) {
            position++;
            txtProgress.setText(position+"/"+threashold);
            progressBar.setProgress((position*100)/threashold);
            r1.startAnimation(AnimationUtils.loadAnimation (getApplicationContext(),R.anim.fromright));
            countDownTimer.cancel();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                img.setImageDrawable(getDrawable(getResources().getIdentifier(pics[position-1], "drawable", getPackageName())));

            } else {
                img.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(pics[position-1], "drawable", getPackageName())));
            }
            img.setTag(pics[position-1]);
            countDownTimer.start();
        }else {
            meta.setDayTenFollowUpOver(true);
            meta.setBaselinePosition(0);
            meta.write();
            if(countDownTimer!=null)
                countDownTimer.cancel();
            finish();
            Intent i=new Intent(getApplicationContext(),Home.class);
            startActivity(i);

        }
    }
    void stopPlayer(){
        if(player!=null){
            player.stop();
            player.release();
            player=null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
        if(meta.isBaselineOver()) {
            meta.setBaselinePosition(position - 1);
            meta.write();
        }
        if(db!=null)
            db.close();
        if(countDownTimer!=null)
            countDownTimer.cancel();
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            stopPlayer();
            meta.backup();
            finish();
            Intent intent = new Intent(this,Home.class);
            startActivity(intent);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

}
