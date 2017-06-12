package com.example.accer.aphasia_app;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    public static  String QUESTIONS_KEY="questions";
    long rowCount=0;
    ADB db;
    ObjectAnimator anim=null;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Meta meta;
    Calendar calendar;
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


        int last=meta.getLastDate().get(Calendar.DATE);
        int tod=Calendar.getInstance().get(Calendar.DATE);

        if(last!=tod&&meta.getLastDate().getTimeInMillis()<Calendar.getInstance().getTimeInMillis()){
            meta.setDay(meta.getDay()+1);
            meta.setTodayTrainingOver(false);
            meta.setFailedPics(null);
            meta.setDailyPicsOver(false);
            meta.setFailedLooping(false);
        }
        meta.write();
        if(meta.isTodayTrainingOver()){
            new AlertDialog.Builder(this)
                    .setTitle("over")
                    .setPositiveButton(""+meta.isTodayTrainingOver(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                        }
                    })
                    .show();
        }


        spChoice.setVisibility(View.GONE);

        int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        if(getIntent().getStringExtra("activity").contains("training"))
            training =true;



        if(training){
            if(meta.getNoOfQuestions()==0)
                spChoice.setVisibility(View.VISIBLE);

            txtIns.setText(getResources().getString(R.string.training_ins));
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
        else
            txtIns.setText(getResources().getString(R.string.baseline_ins));

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(training){
                    if(meta.getNoOfQuestions()!=0) {
                        int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    Instructions.this,
                                    PERMISSIONS_STORAGE,
                                    REQUEST_EXTERNAL_STORAGE
                            );
                        }else {
                            startActivity(new Intent(getApplicationContext(), training.class));
                            finish();
                        }
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
                    startActivity(new Intent(getApplicationContext(), BaselineTest.class));
                    finish();
                }
            }
        });
    }

  /*  public static void setNoOfQuestions(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getNoOfQuestions(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }*/

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
