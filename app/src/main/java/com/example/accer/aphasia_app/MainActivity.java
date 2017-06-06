package com.example.accer.aphasia_app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.util.Calendar;

import android.os.Environment;
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
        db=new ADB(this);
        c=Calendar.getInstance();
        if(db.getDataRowCount()<=0) {
            Toast.makeText(getApplicationContext(),db.getDataRowCount()+"",Toast.LENGTH_SHORT).show();
            for (int i = 1; i <= totalNoOfPics; i++) {
                db.addData("train_" + i);
            }
        }
      //  backup();
        meta=new Meta(this);
       // meta.deleteBackup();



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
        }else {

             permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission!=PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(
                            this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
            }else {
                startActivity(new Intent(this, BaselineTest.class));
            }

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
                     startActivity(new Intent(this, BaselineTest.class));
                 break;

         }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db!=null)
            db.close();
    }

    void backup(){
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data/"+getPackageName()+"/databases/"+ADB.DATABASE_NAME;
                String backupDBPath = ADB.DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getApplicationContext(), "Backup is successful to SD card", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
        }
    }
}
