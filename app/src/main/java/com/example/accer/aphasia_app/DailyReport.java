package com.example.accer.aphasia_app;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

    public class DailyReport extends AppCompatActivity implements View.OnTouchListener {

        LinearLayout topLinearLayout,leftLinearLayout,dataCountContainer,linearLayout;
        LinearLayout.LayoutParams topLayoutParams,leftLayoutParams,dataCountParams,layoutParams;

        ScrollView leftScroll,verticalScroll;
        HorizontalScrollView topScroll,horzScroll;



        int day;
        Meta meta;
        String pics[]=null;
        ADB db=null;
        Button btnRetry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);

        day=getIntent().getIntExtra("day",0);
        day--;

        leftScroll=(ScrollView)findViewById(R.id.leftScroll);
        verticalScroll=(ScrollView)findViewById(R.id.verticalScroll);
        topScroll=(HorizontalScrollView)findViewById(R.id.topScroll);
        horzScroll=(HorizontalScrollView)findViewById(R.id.horzScroll);
        btnRetry=(Button)findViewById(R.id.btn_yes);

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

        leftScroll.setOnTouchListener(this);
        verticalScroll.setOnTouchListener(this);
        topScroll.setOnTouchListener(this);
        horzScroll.setOnTouchListener(this);


        topLinearLayout = (LinearLayout) findViewById(R.id.topImage);
        leftLinearLayout=(LinearLayout)findViewById(R.id.attempts);
        dataCountContainer=(LinearLayout)findViewById(R.id.count);

        topLayoutParams = new LinearLayout.LayoutParams(200, 200);
        topLayoutParams.setMargins(10, 10, 10, 10);

        leftLayoutParams = new LinearLayout.LayoutParams(200, 100);
        leftLayoutParams.setMargins(10, 40, 10, 10);

        dataCountParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 100);
        dataCountParams.setMargins(10, 40, 10, 10);

        layoutParams = new LinearLayout.LayoutParams(200, 50);
        layoutParams.setMargins(10, 10, 10, 10);

        pics = db.getDataPics("valid", "1",(day*meta.getNoOfQuestions())+","+meta.getNoOfQuestions());

        for (int i = 1; i <=pics.length ; i++)//10 is no of images per day from db 3-10
        {
            final ImageView btn = new ImageView(this);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn.setImageDrawable(getDrawable(getResources().getIdentifier(pics[i-1], "drawable", getPackageName())));

            } else {
                btn.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(pics[i-1], "drawable", getPackageName())));
            }
            topLinearLayout.addView(btn, topLayoutParams);
        }


        for (int i = 1; i <= db.getMaxAttemptsOfDay(day); i++)//attempts per day
        {
            final TextView btn = new TextView(this);
            btn.setText("ಪ್ರಯತ್ನ " + i);
            btn.setTextSize(16);
            btn.setGravity(5);
            leftLinearLayout.addView(btn, leftLayoutParams);

            final LinearLayout layout=new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            //layout.setBackgroundResource(R.drawable.circle_button);
            for(int j=1;j<=pics.length;j++)
            {
                final ImageView btn1=new ImageView(this);
                btn1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                int success=db.isTransactionSucess(pics[j-1],i);
                switch (success){
                    default:
                        btn1.setImageResource(R.drawable.dash_small);break;
                    case 0:
                        btn1.setImageResource(R.drawable.untick_small);break;
                    case 1:
                        btn1.setImageResource(R.drawable.tick_small);break;
                }
                layout.addView(btn1, topLayoutParams);
            }
            dataCountContainer.addView(layout,dataCountParams);
        }
    }
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int id=view.getId();
            switch(id)
            {
                case R.id.leftScroll:verticalScroll.scrollTo(view.getScrollX(),view.getScrollY());
                    break;
                case R.id.verticalScroll:leftScroll.scrollTo(view.getScrollX(),view.getScrollY());
                    break;
                case R.id.topScroll:horzScroll.scrollTo(view.getScrollX(),view.getScrollY());
                    break;
                case R.id.horzScroll:topScroll.scrollTo(view.getScrollX(),view.getScrollY());
                    break;
            }
            return false;
        }
}
