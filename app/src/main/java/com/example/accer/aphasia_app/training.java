package com.example.accer.aphasia_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class training extends AppCompatActivity implements View.OnClickListener{

    static int picAttemptArray[];
    ImageView img;
    ImageButton btntick;
    RelativeLayout r1,r3;
    ConstraintLayout r2;
    PowerManager pm;
    PowerManager.WakeLock wl;
    int position=-1;
     int interval=5,savedCtr=1;
    int threashold=0;
    static  MediaPlayer player;
    String pics[];
    ADB db;
    Handler handler=null;
    Runnable handlerRunnable=null;
    boolean doubleBackToExitPressedOnce=false;
    Meta meta;
    Calendar todayDate,lastDate;

    Button btnc1,btnc2,btnc3,btnc4;
    int cue1=0,cue2=0,cue3=0,cue4=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_training);
        r1 = (RelativeLayout) findViewById(R.id.relative_image);
        r2 = (ConstraintLayout) findViewById(R.id.relative_cue);
        r3 = (RelativeLayout) findViewById(R.id.relative_check);
        img = (ImageView) findViewById(R.id.img_training);
        btntick = (ImageButton) findViewById(R.id.btn_tick);
        btnc1 = (Button) findViewById(R.id.btn_cue1);
        btnc2 = (Button) findViewById(R.id.btn_cue2);
        btnc3 = (Button) findViewById(R.id.btn_cue3);
        btnc4 = (Button) findViewById(R.id.btn_cue4);
        meta=new Meta(this);
        todayDate=Calendar.getInstance();
        db = new ADB(this);


        handler = new Handler();



        btntick.setOnClickListener(this);
        btnc1.setOnClickListener(this);
        btnc2.setOnClickListener(this);
        btnc3.setOnClickListener(this);
        btnc4.setOnClickListener(this);

        pics = db.getDataPics("valid","1");

        if(meta.read()!=null){
            meta=meta.read();
            position=(meta.getDay()*meta.getNoOfQuestions())-1;
            if(meta.getLastDate().get(Calendar.DATE)==todayDate.get(Calendar.DATE)
                    &&meta.getLastDate().get(Calendar.MONTH)==todayDate.get(Calendar.MONTH)
                    &&meta.getLastDate().get(Calendar.YEAR)==todayDate.get(Calendar.YEAR))
                 position=meta.getTrainingPosition();
            threashold =position+meta.getNoOfQuestions()+1>pics.length?pics.length:position+meta.getNoOfQuestions()+1;
            savedCtr=meta.getTrainingSavedCounter();
        }
        if(meta.isTodayTrainingOver()){
            finish();
        }
        lastDate=meta.getLastDate();

        setImg();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        ViewGroup.LayoutParams lp = r1.getLayoutParams();
        lp.width = (int) (width * .5);
        lp.height = (int) (height * .8);
        r1.setLayoutParams(lp);


        lp = r2.getLayoutParams();
        lp.width = (int) (width * .4);
        lp.height = (int) (height * .5);
        r2.setLayoutParams(lp);

        lp = r3.getLayoutParams();
        lp.width = (int) (width * .35);
        lp.height = (int) (height * .25);
        r3.setLayoutParams(lp);


        lp = btnc1.getLayoutParams();
        lp.width = (int) (width * .35 * .45);
        lp.height = (int) (height * .25 * .65);
        btnc1.setLayoutParams(lp);
        btnc1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width * .35 * .085));

        lp = btnc2.getLayoutParams();
        lp.width = (int) (width * .35 * .45);
        lp.height = (int) (height * .25 * .65);
        btnc2.setLayoutParams(lp);
        btnc2.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width * .35 * .085));

        lp = btnc3.getLayoutParams();
        lp.width = (int) (width * .35 * .45);
        lp.height = (int) (height * .25 * .65);
        btnc3.setLayoutParams(lp);
        btnc3.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width * .35 * .085));

        lp = btnc4.getLayoutParams();
        lp.width = (int) (width * .35 * .45);
        lp.height = (int) (height * .25 * .65);
        btnc4.setLayoutParams(lp);
        btnc4.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width * .35 * .085));


        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
        wl.acquire(interval * 1000);
    }

  public void startTimer(){
      btnc1.setEnabled(false);
      btnc2.setEnabled(false);
      btnc3.setEnabled(false);
      btnc4.setEnabled(false);
          handlerRunnable=new Runnable() {
              int ctr=1;
              @Override
              public void run() {
                  if(player!=null&&player.isPlaying()) {
                      handler.removeCallbacks(handlerRunnable);
                      handler.postDelayed(this, (interval*1000)+player.getDuration());

                      if(ctr==4)
                          btnc4.setEnabled(true);
                      return;
                  }

                  stopPlayer();
                  player = MediaPlayer.create(getApplicationContext(), getResources().getIdentifier(img.getTag() + "_" + ctr, "raw", getPackageName()));
                  player.start();
                  player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                      @Override
                      public void onCompletion(MediaPlayer mediaPlayer) {
                          switch (ctr-1){
                              case 1:
                                  cue1=1;
                                  cue2=0;
                                  cue3=0;
                                  cue4=0;
                                  break;

                              case 2:
                                  cue1=0;
                                  cue2=1;
                                  cue3=0;
                                  cue4=0;
                                  break;

                              case 3:
                                  cue1=0;
                                  cue2=0;
                                  cue3=1;
                                  cue4=0;
                                  break;

                              case 4:
                                  cue1=0;
                                  cue2=0;
                                  cue3=0;
                                  cue4=1;
                                  break;
                          }
                          db.addTransaction(picAttemptArray[1]+1,picAttemptArray[0],cue1,cue2,cue3,cue4);
                      }
                  });
                  ctr++;
                  if (ctr <4)
                      handler.postDelayed(this, (interval * 1000)+player.getDuration());
                 else if(ctr==4) {
                      handler.postDelayed(this, (1000 * 30)+player.getDuration());
                      new Handler().postDelayed(new Runnable() {
                          @Override
                          public void run() {
                                  btnc1.setEnabled(true);
                                  btnc2.setEnabled(true);
                                  btnc3.setEnabled(true);
                          }
                      },10000);
                      new Handler().postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              btnc4.setEnabled(true);
                          }
                      },1000*30);

                  }
                  else if(position<threashold-1) {
                      new Handler().postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              if(player!=null&&player.isPlaying()){
                                  new Handler().postDelayed(this,6000);
                                  btntick.setEnabled(false);
                                  return;
                              }
                              btntick.setEnabled(true);
                              setImg();
                          }
                      },2000);
                  }
              }
          };
          handler.postDelayed(handlerRunnable,interval*1000);
  }
    @Override
    protected void onDestroy() {
        if(wl.isHeld())
            wl.release();
        if(handler!=null)
            handler.removeCallbacks(handlerRunnable);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
      int id=view.getId();
        switch (id){
            case R.id.btn_tick :
                stopPlayer();
                player=MediaPlayer.create(this,R.raw.tick_sound);
                player.start();
                setImg();
                break;
            case R.id.btn_cue1 :
                db.addTransaction(picAttemptArray[1]+1,picAttemptArray[0],1,0,0,0);
                stopPlayer();
                player = MediaPlayer.create(getApplicationContext(),getResources().getIdentifier(img.getTag()+"_1","raw",getPackageName()));
                player.start();
                break;

            case R.id.btn_cue2 :
                db.addTransaction(picAttemptArray[1]+1,picAttemptArray[0],0,1,0,0);
                stopPlayer();
                player = MediaPlayer.create(getApplicationContext(),getResources().getIdentifier(img.getTag()+"_2","raw",getPackageName()));
                player.start();

                break;
            case R.id.btn_cue3 :
                db.addTransaction(picAttemptArray[1]+1,picAttemptArray[0],0,0,1,0);
                stopPlayer();
                player = MediaPlayer.create(getApplicationContext(),getResources().getIdentifier(img.getTag()+"_3","raw",getPackageName()));
                player.start();
                break;
            case R.id.btn_cue4 :
                db.addTransaction(picAttemptArray[1]+1,picAttemptArray[0],0,0,0,1);
                stopPlayer();
                player = MediaPlayer.create(getApplicationContext(),getResources().getIdentifier(img.getTag()+"_4","raw",getPackageName()));
                player.start();
                break;
        }
    }



    void setImg(){
        position++;
        if(position<threashold&&position<pics.length) {
            picAttemptArray=db.getLastAttemptFromTransaction(pics[position]);
            if(picAttemptArray[0]==0)
                Toast.makeText(getApplicationContext(),"Oooops.",Toast.LENGTH_SHORT).show();
            r1.startAnimation(AnimationUtils.loadAnimation (getApplicationContext(),R.anim.fromright));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                img.setImageDrawable(getDrawable(getResources().getIdentifier(pics[position], "drawable", getPackageName())));

            } else {
                img.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(pics[position], "drawable", getPackageName())));
            }
            img.setTag(pics[position]);
            if(handler!=null) {
                handler.removeCallbacks(handlerRunnable);
                handler.removeCallbacksAndMessages(null);
            }
            startTimer();
        }else {
            meta.setTodayTrainingOver(true);
            meta.setLastDate(todayDate);
            meta.write();
            Intent i=new Intent(getApplicationContext(),Instructions.class);
            i.putExtra("activity","training");
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
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            stopPlayer();
            if(handler!=null) {
                handler.removeCallbacks(handlerRunnable);
                handler.removeCallbacksAndMessages(null);
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
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

    @Override
    protected void onStop() {
        super.onStop();
        if(wl.isHeld())
            wl.release();
        if(handler!=null) {
            handler.removeCallbacks(handlerRunnable);
            handler.removeCallbacksAndMessages(null);
        }

        todayDate=Calendar.getInstance();
        int last=lastDate.get(Calendar.DATE);
        int tod=todayDate.get(Calendar.DATE);

        if(player!=null)
            player.stop();


        meta.setTrainingPosition(position-1);
        if(last!=tod&&lastDate.getTimeInMillis()<todayDate.getTimeInMillis()){
            meta.setDay(meta.getDay()+1);
        }
        meta.setLastDate(todayDate);
        //meta.setTrainingSavedCounter(ctr-1);
        //Toast.makeText(getApplicationContext(),ctr,Toast.LENGTH_LONG).show();
        meta.write();
    }
}
