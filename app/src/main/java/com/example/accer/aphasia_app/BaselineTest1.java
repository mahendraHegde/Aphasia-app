package com.example.accer.aphasia_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BaselineTest1 extends AppCompatActivity {

   TextView tx1,tx2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baseline_test1);
        tx1=(TextView)findViewById(R.id.txt1);
        //tx2=(TextView)findViewById(R.id.txt2);
        tx1.setText("hello");
        //tx2.setText("hello");
    }
}
