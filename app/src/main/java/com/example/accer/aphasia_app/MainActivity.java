package com.example.accer.aphasia_app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.Calendar;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ADB db=null;
    final static  int totalNoOfPics=24;
    Meta meta;
    Calendar c;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int permission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermission();
    }

    void dateMgmnt(){
        db=new ADB(this);
        c=Calendar.getInstance();
        meta=new Meta(this);
        if(db.getDataRowCount()<=0) {
            meta.deleteBackup();
            for (int i = 1; i <= totalNoOfPics; i++) {
                db.addData("train_" + i);
            }
        }


        if(meta.read()!=null){
            meta=meta.read();
        }
        if(meta.getLastDate().getTimeInMillis()>c.getTimeInMillis()){
            new AlertDialog.Builder(this)
                    .setTitle("ದಿನಾಂಕ/ಸಮಯ ದೋಷ")
                    .setMessage("ದಯವಿಟ್ಟು ಪ್ರಸ್ತುತ ದಿನಾಂಕದ ಸಮಯಕ್ಕೆ ನಿಮ್ಮ ಮೊಬೈಲ್ ಸಮಯ ಮತ್ತು ದಿನಾಂಕವನ್ನು ಹೊಂದಿಸಿ")
                    .setPositiveButton("ಸರಿ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            finish();
                        }
                    })
                    .show();
        }else{
            //startActivity(new Intent(this, Home.class));
            startActivity(new Intent(this, OverallReport.class));


        }
    }
    void askPermission(){

            permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission!=PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }else {
                dateMgmnt();
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         switch (requestCode){
             case REQUEST_EXTERNAL_STORAGE:
                 permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                 if(permission!=PackageManager.PERMISSION_GRANTED){
                     new AlertDialog.Builder(this)
                             .setTitle("ಅನುಮತಿ ನಿರಾಕರಿಸಲಾಗಿದೆ")
                             .setMessage("ಅಪ್ಲಿಕೇಶನ್ಗೆ ಕೆಲಸ ಮಾಡಲು ಸಂಗ್ರಹಣೆ ಅನುಮತಿ ಅಗತ್ಯವಿದೆ...")
                             .setPositiveButton("ಸರಿ", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                     android.os.Process.killProcess(android.os.Process.myPid());
                                 }
                             })
                             .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                 @Override
                                 public void onCancel(DialogInterface dialogInterface) {
                                     android.os.Process.killProcess(android.os.Process.myPid());
                                 }
                             })
                             .show();

                 }else
                     dateMgmnt();
                 break;
         }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            askPermission();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            askPermission();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db!=null)
            db.close();
    }
}
