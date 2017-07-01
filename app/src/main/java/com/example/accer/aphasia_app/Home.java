package com.example.accer.aphasia_app;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {
    Button btnProgress,btnTraining;
    Boolean doubleBackToExitPressedOnce=false;
    Meta meta;
    public static int FOLLOW_UP_DAY=10;
    ADB db;
    boolean isFollowUp=false;
    Button overallProgress;
    Button btnSendReports;
    public static final String TRANSACTION_URL="synctransactions.php";
    int sentCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnProgress=(Button) findViewById(R.id.btn_progress);
        btnTraining=(Button)findViewById(R.id.btn_training);
        overallProgress=(Button)findViewById(R.id.btn_overall) ;
        btnSendReports=(Button)findViewById(R.id.btn_send_reports);
        meta=new Meta(this);
        db=new ADB(this);
        if(meta.read()!=null)
            meta=meta.read();


        btnProgress.setText("ಪ್ರಗತಿ\n");

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

        lp=btnSendReports.getLayoutParams();
        lp.width= (int) (width*.20);
        lp.height= (int) (height*.29);
        btnSendReports.setLayoutParams(lp);

        if(meta.getFollowUpDay()<=0)
            askFollowUp();
        else
            FOLLOW_UP_DAY=meta.getFollowUpDay();

        if(meta.getDay()+1==FOLLOW_UP_DAY&&!meta.isDayTenFollowUpOver()&&meta.isTodayTrainingOver()){
            isFollowUp=true;
        }

        if(meta.isBaselineOver()) {
            if(isFollowUp){
                btnTraining.setText("ಅನುಸರಣಾ ಪರೀಕ್ಷೆ\n");
            }else {
                btnTraining.setText("ತರಬೇತಿ\n");
                if((meta.getDay()>0&&db.getDataPics("valid", "1",(meta.getDay()*meta.getNoOfQuestions())+","+meta.getNoOfQuestions()).length<=0)||(db.getDataPics("valid","1").length<=0)){
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
            btnTraining.setText("ಬೇಸ್ಲೈನ್ ಟೆಸ್ಟ್\n");
        }

        if((meta.isTherapyOver()==false)&&(meta.getDay()>0&&db.getDataPics("valid", "1",(meta.getDay()*meta.getNoOfQuestions())+","+meta.getNoOfQuestions()).length<=0)||(db.getDataPics("valid","1").length<=0)){
            meta.setTherapyOver(true);
        }

        overallProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int arr[]=db.getSuccessFailTransactionArray();
                if(arr[1]<=0){
                    Snackbar.make(findViewById(R.id.constraint_home),"ತರಬೇತಿ ಇನ್ನೂ ಪ್ರಾರಂಭಿಸಿಲ್ಲ.",Snackbar.LENGTH_LONG).show();
                }else {
                    Intent i=new Intent(getApplicationContext(), OverallReport.class);
                    startActivity(i);
                }
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
                    }else {
                        in.putExtra("activity", "training");
                        if(meta.isTherapyOver()){
                            new AlertDialog.Builder(Home.this)
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
                            in=new Intent(getApplicationContext(),Home.class);
                        }
                    }
                }else {
                        in.putExtra("activity", "baseline");
                }
                startActivity(in);

            }
        });


        btnSendReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendReports();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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


    void askFollowUp(){
        List<String>list=new ArrayList<String>();
        for(int i=5;i<=30;i=i+5){
            list.add("On Day "+i);
        }

       ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        new AlertDialog.Builder(Home.this)
                .setTitle("please select follow up test day for your study(Speech Therapist Only)")
                .setCancelable(false)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                       askFollowUp();
                    }
                })
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        meta.setFollowUpDay((which+1)*5);
                        meta.write();
                        dialog.dismiss();
                    }
                }).create().show();
    }


    void sendReports() throws JSONException {
        Map<String, String> params = new HashMap<>();
        if(meta.isTherapyOver())
            params.put("over","1");
        params.put("patient_id",meta.getPatientId());
        params.put("transaction",getTrans());
        if(getFollowUp().length()>20&&!meta.isFollowUpSent())
             params.put("followup",getFollowUp());
        if(sentCount>0||(getFollowUp().length()>20&&!meta.isFollowUpSent())) {
            final GetVolleyResponse response = new GetVolleyResponse(Home.this);
            response.getResponse(MainActivity.SERVER_URL + TRANSACTION_URL, params, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        if(jsonArray!=null&&jsonArray.length()>0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code").toLowerCase();
                            if (code.contains("success")) {
                                meta.setCountOfTransactionsSent(meta.getCountOfTransactionsSent() + sentCount);
                                if (getFollowUp().length() > 20 && !meta.isFollowUpSent()) {
                                    meta.setFollowUpSent(true);
                                }
                                meta.write();

                                new AlertDialog.Builder(Home.this)
                                        .setTitle("ಕಳುಹಿಸಲಾಗಿದೆ")
                                        .setMessage("ನಿಮ್ಮ ತರಬೇತಿ ವರದಿಯನ್ನು ವೈದ್ಯರಿಗೆ ಕಳುಹಿಸಲಾಗಿದೆ.\n" +
                                                "ಧನ್ಯವಾದಗಳು ...")
                                        .show();

                            } else {
                                new AlertDialog.Builder(Home.this)
                                        .setTitle("ಕಳುಹಿಸಲಾಗಿದೆ")
                                        .setMessage("ವರದಿ ಈಗಾಗಲೇ ಕಳುಹಿಸಲಾಗಿದೆ\n" +
                                                "ಧನ್ಯವಾದಗಳು ...")
                                        .show();
                            }
                        }else {
                            new AlertDialog.Builder(Home.this)
                                    .setTitle("ಕಳುಹಿಸಲಾಗಿದೆ")
                                    .setMessage("ವರದಿ ಈಗಾಗಲೇ ಕಳುಹಿಸಲಾಗಿದೆ\n" +
                                            "ಧನ್ಯವಾದಗಳು ...")
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }else{
            new AlertDialog.Builder(Home.this)
                    .setTitle("ಕಳುಹಿಸಲಾಗಿದೆ")
                    .setMessage("ವರದಿ ಈಗಾಗಲೇ ಕಳುಹಿಸಲಾಗಿದೆ\n" +
                            "ಧನ್ಯವಾದಗಳು ...")
                    .show();
        }

          /*new AlertDialog.Builder(Home.this)
                .setMessage(""+getFollowUp())
                .show(); */
    }
    String getTrans() throws JSONException {
        ArrayList<ADB.Transactions> list;
        JSONArray array=new JSONArray();
        JSONObject obj=new JSONObject();
        list=db.getTransactions();
        sentCount=list.size();
        for (int i=0;i<list.size();i++){
            ADB.Transactions t=list.get(i);
            JSONObject tempObj=new JSONObject();
            tempObj.put("type",t.getType());
            tempObj.put("attempt_id",t.getAttempt_id());
            tempObj.put("pic_id",t.getPic_id());
            tempObj.put("cue1",t.getCue1());
            tempObj.put("cue2",t.getCue2());
            tempObj.put("cue3",t.getCue3());
            tempObj.put("cue4",t.getCue4());
            tempObj.put("time",t.getTime());
            tempObj.put("day",t.getDay());
            tempObj.put("date",t.getDate());


            array.put(tempObj);
        }
        obj.put("transactions",array);
        return obj.toString();
    }


    String getFollowUp() throws JSONException {
        ArrayList<ADB.Record> list;
        JSONArray array=new JSONArray();
        JSONObject obj=new JSONObject();
        list=db.getFollowUp();
        for (int i=0;i<list.size();i++){
            ADB.Record t=list.get(i);
            JSONObject tempObj=new JSONObject();
            tempObj.put("pic_id",t.getId());
            tempObj.put("value",t.getDayten());

            array.put(tempObj);
        }
        obj.put("followup",array);
        return obj.toString();
    }

}
