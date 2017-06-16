    package com.example.accer.aphasia_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

    public class DailyReport extends AppCompatActivity implements View.OnTouchListener {

        LinearLayout topLinearLayout,leftLinearLayout,dataCountContainer,linearLayout;
        LinearLayout.LayoutParams topLayoutParams,leftLayoutParams,dataCountParams,layoutParams;
        RelativeLayout option;
        TextView days,question;

        ScrollView leftScroll,verticalScroll;
        HorizontalScrollView topScroll,horzScroll;
        int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);

        day=getIntent().getIntExtra("day",0);
        Toast.makeText(getApplicationContext(),""+day,Toast.LENGTH_LONG).show();

        leftScroll=(ScrollView)findViewById(R.id.leftScroll);
        verticalScroll=(ScrollView)findViewById(R.id.verticalScroll);
        topScroll=(HorizontalScrollView)findViewById(R.id.topScroll);
        horzScroll=(HorizontalScrollView)findViewById(R.id.horzScroll);
        question=(TextView)findViewById(R.id.question);

        days=(TextView)findViewById(R.id.days);
        leftScroll.setOnTouchListener(this);
        verticalScroll.setOnTouchListener(this);
        topScroll.setOnTouchListener(this);
        horzScroll.setOnTouchListener(this);

        topLinearLayout = (LinearLayout) findViewById(R.id.topImage);
        leftLinearLayout=(LinearLayout)findViewById(R.id.attempts);
        dataCountContainer=(LinearLayout)findViewById(R.id.count);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        ViewGroup.LayoutParams lp=days.getLayoutParams();
        lp.width= (int) (width*.15);
        lp.height= (int) (height*.20);
        days.setLayoutParams(lp);
        days.setTextSize((float) (width*.15/10));

        lp=topScroll.getLayoutParams();
        lp.width= (int) (width*.85);
        lp.height= (int) (height*.20);
        topScroll.setLayoutParams(lp);

        lp=leftScroll.getLayoutParams();
        lp.width= (int) (width*.15);
        lp.height= (int) (height*.60);
        leftScroll.setLayoutParams(lp);

        lp=verticalScroll.getLayoutParams();
        lp.width= (int) (width*.85);
        lp.height= (int) (height*.59);
        verticalScroll.setLayoutParams(lp);

        question.setTextSize((float) (width*0.15/8));

        topLayoutParams = new LinearLayout.LayoutParams(300,300);
        topLayoutParams.setMargins(10, 10, 0, 10);

        leftLayoutParams = new LinearLayout.LayoutParams(300, 300);
        leftLayoutParams.setMargins(0, 35 , 0, 0);

        dataCountParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 100);
        dataCountParams.setMargins(5, 10, 0,0);

        for (int i = 1; i <= 10; i++)//10 is no of images per day from db 3-10
        {
            final ImageView btn = new ImageView(this);
            btn.setBackgroundResource(R.drawable.train_1);
            topLinearLayout.addView(btn, topLayoutParams);
            lp=btn.getLayoutParams();
            lp.width=(int) (width*.1);
            lp.height=(int) (height*.15);
            btn.setLayoutParams(lp);
        }
        for (int i = 1; i <= 10; i++)//attempts per day
        {
            final TextView btn = new TextView(this);
            btn.setText("ಪ್ರಯತ್ನ " + i);
            btn.setGravity(5);
            btn.setTextSize((float) (width*.15/9));
            leftLinearLayout.addView(btn,leftLayoutParams);
            lp=btn.getLayoutParams();
            lp.width=(int) (width*.1);
            lp.height=(int) (height*.1);
            btn.setLayoutParams(lp);

            final LinearLayout layout=new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            for(int j=1;j<=10;j++)
            {
                final ImageView btn1=new ImageView(this);
                btn1.setBackgroundResource(R.drawable.train_1);
                layout.addView(btn1, topLayoutParams);
                lp=btn1.getLayoutParams();
                lp.width=(int) (width*.1);
                lp.height=(int) (height*.15);
                btn1.setLayoutParams(lp);
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
