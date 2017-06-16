package com.example.accer.aphasia_app;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class Home extends AppCompatActivity {
    Button btnProgress,btnTraining;
    Boolean doubleBackToExitPressedOnce=false;
    Meta meta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnProgress=(Button) findViewById(R.id.btn_progress);
        btnTraining=(Button)findViewById(R.id.btn_training);
        meta=new Meta(this);
        if(meta.read()!=null)
            meta=meta.read();

        if(meta.isBaselineOver()) {
            btnTraining.setText("ತರಬೇತಿ");
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
        lp.height= (int) (height*.25);
        btnProgress.setLayoutParams(lp);


        lp=btnTraining.getLayoutParams();
        lp.width= (int) (width*.30);
        lp.height= (int) (height*.25);
        btnTraining.setLayoutParams(lp);

       /* finish();
        startActivity(new Intent(getApplicationContext(),Progress.class));
*/
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
