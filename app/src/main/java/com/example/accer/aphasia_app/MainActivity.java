package com.example.accer.aphasia_app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    AlertDialog alert;
    LayoutInflater inflater;
    View v;
    EditText patId;
    Button btnLogin;

    public static final String SERVER_URL="http://10.0.2.2:8081/Aphasia-web/";
    public static String LOGIN_URL="patientlogin.php";
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
           //login();
            startActivity(new Intent(this, Home.class));
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


    void login(){
        if(meta.getPatientId()==null) {
            if (alert == null)
                alert = getBuilder().create();
            alert.setCanceledOnTouchOutside(false);
            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    MainActivity.this.finish();
                }
            });
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    MainActivity.this.finish();
                }
            });
            alert.show();
        }
        else
            startActivity(new Intent(this, Home.class));

    }

    @NonNull
    private AlertDialog.Builder getBuilder() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        inflater = MainActivity.this.getLayoutInflater();
        if (v == null) {
            v = inflater.inflate(R.layout.login, null, false);
        } else {
            ((ViewGroup) v.getParent()).removeView(v);
        }
        alert.setView(v);

        patId = (EditText) v.findViewById(R.id.patient_id);
        btnLogin = (Button) v.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!patId.getText().toString().isEmpty()){
                    Map<String, String> params = new HashMap<>();
                    params.put("patient_id", patId.getText().toString());
                    final GetVolleyResponse response = new GetVolleyResponse(MainActivity.this);
                    response.getResponse(SERVER_URL + LOGIN_URL, params, new VolleyCallback() {
                        @Override
                        public void onSuccessResponse(String result) {
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(result);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String code = jsonObject.getString("code").toLowerCase();
                                if (code.contains("success")) {
                                    meta.setPatientId(patId.getText().toString());
                                    meta.write();
                                    MainActivity.this.finish();
                                    startActivity(new Intent(MainActivity.this, Home.class));

                                } else {
                                    Snackbar snack = Snackbar.make(v, "Inavlid Patient Id", Snackbar.LENGTH_LONG);
                                    snack.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else {
                    Snackbar snack = Snackbar.make(v, "Please provide Patient Id", Snackbar.LENGTH_LONG);
                    snack.show();
                }
            }
        });
        return alert;
    }

}
