package com.example.accer.aphasia_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DailyReport extends AppCompatActivity implements View.OnTouchListener {

    TextView titleText,days;
    RadioGroup options;
    RadioButton rdb1,rdb2,rdb3;
    HorizontalScrollView topLinearLayout,horzScroll;
    ScrollView leftLinearLayout,dataCountContainer;
    LinearLayout.LayoutParams topLayoutParams,leftLayoutParams,dataCountParams,layoutParams;
    RelativeLayout choice;
    LinearLayout topLinear,leftLinear,dataLinear;


    int day;
    Meta meta;
    String pics[]=null;
    ADB db=null;
    Button btnRetry;
    ViewGroup.LayoutParams lp;
    int width,height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);

        day=getIntent().getIntExtra("day",0);
        day--;


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        titleText=(TextView)findViewById(R.id.titleText);
        days=(TextView)findViewById(R.id.days);
        options=(RadioGroup)findViewById(R.id.options);
        rdb1=(RadioButton)findViewById(R.id.rdb1);
        rdb2=(RadioButton)findViewById(R.id.rdb2);
        rdb3=(RadioButton)findViewById(R.id.rdb3);
        topLinearLayout=(HorizontalScrollView) findViewById(R.id.topScroll);
        leftLinearLayout=(ScrollView) findViewById(R.id.leftScroll);
        dataCountContainer=(ScrollView)findViewById(R.id.verticalScroll);
        horzScroll=(HorizontalScrollView)findViewById(R.id.horzScroll);
        choice=(RelativeLayout)findViewById(R.id.choice);
        topLinear=(LinearLayout)findViewById(R.id.topImage);
        leftLinear=(LinearLayout)findViewById(R.id.attempts);
        dataLinear=(LinearLayout)findViewById(R.id.count);
        btnRetry=(Button)findViewById(R.id.btn_yes);

        rdb1.setChecked(true);

        options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rdb1 :
                        Toast.makeText(getApplicationContext(),"s",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });


        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(getApplicationContext(),Attempt.class);
                in.putExtra("day",day+1);
                startActivity(in);
            }
        });

        db=new ADB(this);
        meta=new Meta(this);
        if(meta.read()!=null)
            meta=meta.read();


        topLinearLayout.setOnTouchListener(this);
        leftLinearLayout.setOnTouchListener(this);
        dataCountContainer.setOnTouchListener(this);
        horzScroll.setOnTouchListener(this);

        lp=titleText.getLayoutParams();
        lp.width= (int) (LinearLayout.LayoutParams.MATCH_PARENT);
        lp.height= (int) (height*.1);
        titleText.setLayoutParams(lp);

        lp=options.getLayoutParams();
        lp.width= (int) (LinearLayout.LayoutParams.MATCH_PARENT);
        lp.height= (int) (height*.1);
        options.setLayoutParams(lp);

        lp=options.getLayoutParams();
        lp.width= (int) (LinearLayout.LayoutParams.MATCH_PARENT);
        lp.height= (int) (height*.1);
        options.setLayoutParams(lp);

        lp=topLinearLayout.getLayoutParams();
        lp.width= (int) (width*.85);
        lp.height= (int) (height*.15);
        topLinearLayout.setLayoutParams(lp);

        lp=days.getLayoutParams();
        lp.width= (int) (width*.15);
        lp.height= (int) (height*.15);
        days.setLayoutParams(lp);

        lp=leftLinearLayout.getLayoutParams();
        lp.width= (int) (width*.15);
        lp.height= (int) (height*.47);
        leftLinearLayout.setLayoutParams(lp);

        lp=dataCountContainer.getLayoutParams();
        lp.width= (int) (width*.85);
        lp.height= (int) (height*.47);
        dataCountContainer.setLayoutParams(lp);

        lp=choice.getLayoutParams();
        lp.width= (int) (LinearLayout.LayoutParams.MATCH_PARENT);
        lp.height= (int) (height*.15);
        choice.setLayoutParams(lp);

        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP,23);
        rdb1.setTextSize(TypedValue.COMPLEX_UNIT_SP,23);
        rdb2.setTextSize(TypedValue.COMPLEX_UNIT_SP,23);
        rdb3.setTextSize(TypedValue.COMPLEX_UNIT_SP,23);

        topLayoutParams = new LinearLayout.LayoutParams(300,300);
        topLayoutParams.setMargins(10, 10, 0, 10);

        leftLayoutParams = new LinearLayout.LayoutParams(300, 300);
        leftLayoutParams.setMargins(0, 40 , 0, 0);

        dataCountParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 100);
        dataCountParams.setMargins(0, 10, 0,0);
        setPics();

    }

    void setPics(){
        pics = db.getDataPics("valid", "1",(day*meta.getNoOfQuestions())+","+meta.getNoOfQuestions());
        for (int i = 1; i <= pics.length; i++)// no of images per day from db 3-10
        {
            final ImageView btn = new ImageView(this);
            btn.setBackgroundResource(R.drawable.train_1);
            topLinear.addView(btn, topLayoutParams);
            lp=btn.getLayoutParams();
            lp.width=(int) (width*.1);
            lp.height=(int) (height*.14);
            btn.setLayoutParams(lp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn.setImageDrawable(getDrawable(getResources().getIdentifier(pics[i-1], "drawable", getPackageName())));

            } else {
                btn.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(pics[i-1], "drawable", getPackageName())));
            }
        }

        for (int i = 1; i <=pics.length; i++)//attempts per day
        {
            final TextView btn = new TextView(this);
            btn.setText("ಪ್ರಯತ್ನ " + i);
            leftLinear.addView(btn,leftLayoutParams);
            lp=btn.getLayoutParams();
            lp.width=(int) (width*.1);
            lp.height=(int) (height*.1);
            btn.setLayoutParams(lp);

            final LinearLayout layout=new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            for(int j=1;j<=db.getMaxAttemptsOfDay(day);j++)
            {
                final ImageView btn1=new ImageView(this);
                btn1.setBackgroundResource(R.drawable.train_1);
                layout.addView(btn1, topLayoutParams);
                lp=btn1.getLayoutParams();
                lp.width=(int) (width*.1);
                lp.height=(int) (height*.12);
                btn1.setLayoutParams(lp);
            }
            dataLinear.addView(layout,dataCountParams);
            lp=layout.getLayoutParams();
            lp.width=(int) (LinearLayout.LayoutParams.MATCH_PARENT);
            lp.height=(int) (height*.15);
            layout.setLayoutParams(lp);
        }

    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int id=view.getId();
        switch(id)
        {
            case R.id.leftScroll:dataCountContainer.scrollTo(view.getScrollX(),view.getScrollY());
                break;
            case R.id.verticalScroll:leftLinearLayout.scrollTo(view.getScrollX(),view.getScrollY());
                break;
            case R.id.topScroll:horzScroll.scrollTo(view.getScrollX(),view.getScrollY());
                break;
            case R.id.horzScroll:topLinearLayout.scrollTo(view.getScrollX(),view.getScrollY());
                break;
        }
        return false;
    }
}
