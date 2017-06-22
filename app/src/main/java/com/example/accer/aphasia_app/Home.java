package com.example.accer.aphasia_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class Home extends AppCompatActivity {
    Button btnProgress,btnTraining;
    Boolean doubleBackToExitPressedOnce=false;
    Meta meta;
    public static int FOLLOW_UP_DAY=10;
    ADB db;
    boolean isFollowUp=false;
    Button overallProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnProgress=(Button) findViewById(R.id.btn_progress);
        btnTraining=(Button)findViewById(R.id.btn_training);
        overallProgress=(Button)findViewById(R.id.btn_overall) ;
        meta=new Meta(this);
        db=new ADB(this);
        if(meta.read()!=null)
            meta=meta.read();


        if(meta.getLastDate().get(Calendar.DATE)!=Calendar.getInstance().get(Calendar.DATE)&&meta.getLastDate().getTimeInMillis()< Calendar.getInstance().getTimeInMillis()&&!db.checkForYesterdayTest(meta.getDay())&&meta.getDay()+1==FOLLOW_UP_DAY&&!meta.isDayTenFollowUpOver()){
            isFollowUp=true;
        }

        if(meta.isBaselineOver()) {
            if(isFollowUp){
                btnTraining.setText("ಅನುಸರಣಾ ಪರೀಕ್ಷೆ");
            }else {
                btnTraining.setText("ತರಬೇತಿ");
                if(db.getDataPics("valid", "1",(meta.getDay()*meta.getNoOfQuestions())+","+meta.getNoOfQuestions()).length<=0){
                    new AlertDialog.Builder(this)
                            .setTitle("ಪೂರ್ಣಗೊಂಡಿದೆ")
                            .setMessage("ನಿಮ್ಮ ತರಬೇತಿ ಯಶಸ್ವಿಯಾಗಿ ಪೂರ್ಣಗೊಂಡಿದೆ.\n" +
                                    "ನೀವು ಗುಣಮುಖರಾಗಿದ್ದೀರಿ ಎಂದು ಭಾವಿಸುತ್ತೇವೆ.\n" +
                                    "ಧನ್ಯವಾದಗಳು ..")
                            .setPositiveButton("ಸರಿ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
            }
        }else {
            btnTraining.setText("ಬೇಸ್ಲೈನ್ ಟೆಸ್ಟ್");
        }
        btnProgress.setText("ಪ್ರಗತಿ");



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        ViewGroup.LayoutParams lp=btnProgress.getLayoutParams();
        lp.width= (int) (width*.30);
        lp.height= (int) (height*.30);
        btnProgress.setLayoutParams(lp);


        lp=btnTraining.getLayoutParams();
        lp.width= (int) (width*.30);
        lp.height= (int) (height*.35);
        btnTraining.setLayoutParams(lp);


        lp=overallProgress.getLayoutParams();
        lp.width= (int) (width*.20);
        lp.height= (int) (height*.29);
        overallProgress.setLayoutParams(lp);

        overallProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),OverallReport.class));
            }
        });

        btnProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(),Progress.class));
            }
        });

        btnTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent in=new Intent(getApplicationContext(),Instructions.class);
                if(meta.isBaselineOver()) {
                    if(isFollowUp){
                        in.putExtra("activity", "followup");
                    }else
                     in.putExtra("activity", "training");
                }else {
                        in.putExtra("activity", "baseline");
                }
                startActivity(in);

            }
        });


    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
