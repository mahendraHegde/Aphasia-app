package com.example.accer.aphasia_app;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Instructions extends AppCompatActivity {

    TextView txtIns;
    Button btnstart;
    boolean training =false;
    Button spChoice;
    final int max=10;
    List<String> list;
    ArrayAdapter<String> adapter;
    ADB db;
    ObjectAnimator anim=null;
    Meta meta;
    Calendar calendar;
    String resource;
    SpannableString spannableString;
    int index;
    Drawable d;
    ImageSpan span;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        txtIns=(TextView)findViewById(R.id.txt_instucrions);
        btnstart=(Button)findViewById(R.id.btn_start);
        list=new ArrayList<String>();
        spChoice=(Button)findViewById(R.id.spinner_choice);
        db=new ADB(this);
        calendar=Calendar.getInstance();
        meta=new Meta(this);
        if(meta.read()!=null){
            meta=meta.read();
        }
        if(getIntent().getStringExtra("activity").contains("training"))
            training =true;


        int last=meta.getLastDate().get(Calendar.DATE);
        int tod=Calendar.getInstance().get(Calendar.DATE);
        spChoice.setVisibility(View.GONE);


        if(training){
            if(meta.getNoOfQuestions()<=0)
                spChoice.setVisibility(View.VISIBLE);

            if(last!=tod&&meta.getLastDate().getTimeInMillis()<Calendar.getInstance().getTimeInMillis()){
                meta.setMaxGivenTimeElapsed(0);
                meta.setTodayTrainingOver(false);
                meta.setFailedPics(null);
                meta.setDailyPicsOver(false);
                meta.setFailedLooping(false);
                if(!db.checkForYesterdayTest(meta.getDay()))
                    meta.setDay(meta.getDay()+1);
            }

            meta.write();
            if(meta.isTodayTrainingOver()){
                new AlertDialog.Builder(this)
                        .setTitle("ಪೂರ್ಣಗೊಂಡಿದೆ")
                        .setMessage("ಇಂದಿನ ತರಬೇತಿ ಮುಗಿದಿದೆ. ನಾಳೆ ಮತ್ತೆ ಮರಳಿ ಬನ್ನಿ")
                        .setPositiveButton("ಸರಿ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                Intent intent = new Intent(getApplicationContext(),Home.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }


            resource=new String(getResources().getString(R.string.training_ins));
            spannableString= new SpannableString(resource);

             d = null;
            index=resource.indexOf("✔");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                d = getDrawable(R.drawable.tick_small);
            }else {
                d = getResources().getDrawable(R.drawable.tick_small);
            }
            d.setBounds(0, 0, d.getIntrinsicWidth()/10, d.getIntrinsicHeight()/10);
            span= new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
            spannableString.setSpan(span, index, index+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);



            index=resource.indexOf("@");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                d = getDrawable(R.drawable.hint);
            }else {
                d = getResources().getDrawable(R.drawable.hint);
            }
            d.setBounds(0, 0, d.getIntrinsicWidth()/2, d.getIntrinsicHeight()/2);
            span= new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
            spannableString.setSpan(span, index, index+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);




            txtIns.setText(spannableString);

            for(int i=3;i<=max;i++){
                list.add(i+"  ಪ್ರಶ್ನೆಗಳಿಗೆ ");
            }
            adapter=new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(anim!=null)
                        anim.cancel();
                      new AlertDialog.Builder(Instructions.this)
                    .setTitle("ದಿನಕ್ಕೆ ಎಷ್ಟು ಪ್ರಶ್ನೆಗಳಿಗೆ ಉತ್ತರಿಸಲು ಇಚ್ಛಿಸುತ್ತೀರಿ?(ಒಂದು ಬಾರಿ ಆಯ್ಕೆ)")
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            spChoice.setText(adapter.getItem(which)+" ▼");
                            meta.setNoOfQuestions(which+3);
                            meta.write();
                            dialog.dismiss();
                        }
                    }).create().show();
                }
            });

        }
        else {
            resource=new String(getResources().getString(R.string.baseline_ins));
            spannableString=new SpannableString(resource);

            index=resource.indexOf("✔");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                d = getDrawable(R.drawable.tick_small);
            }else {
                d = getResources().getDrawable(R.drawable.tick_small);
            }
            d.setBounds(0, 0, d.getIntrinsicWidth()/10, d.getIntrinsicHeight()/10);
            span= new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
            spannableString.setSpan(span, index, index+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);


            index=resource.indexOf("@");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                d = getDrawable(R.drawable.untick_small);
            }else {
                d = getResources().getDrawable(R.drawable.untick_small);
            }
            d.setBounds(0, 0, d.getIntrinsicWidth()/2, d.getIntrinsicHeight()/2);
            span= new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
            spannableString.setSpan(span, index, index+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);


            txtIns.setText(spannableString);
        }

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(training){
                    if(meta.getNoOfQuestions()>0) {
                            startActivity(new Intent(getApplicationContext(), training.class));
                            finish();
                        }
                    else {
                        spChoice.setVisibility(View.VISIBLE);
                        anim=ObjectAnimator.ofPropertyValuesHolder(spChoice,
                                PropertyValuesHolder.ofFloat("scaleX",1.2f),
                                PropertyValuesHolder.ofFloat("scaleY",1.2f));
                        anim.setDuration(1000);
                        anim.setRepeatCount(ObjectAnimator.INFINITE);
                        anim.setRepeatMode(ObjectAnimator.REVERSE);
                        anim.start();
                    }
                }else {
                    finish();
                    startActivity(new Intent(getApplicationContext(), BaselineTest.class));
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db!=null)
            db.close();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}
