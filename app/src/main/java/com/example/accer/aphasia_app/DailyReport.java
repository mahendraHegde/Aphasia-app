    package com.example.accer.aphasia_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

    public class DailyReport extends AppCompatActivity implements View.OnTouchListener {

        LinearLayout topLinearLayout,leftLinearLayout,dataCountContainer,linearLayout;
        LinearLayout.LayoutParams topLayoutParams,leftLayoutParams,dataCountParams,layoutParams;

        ScrollView leftScroll,verticalScroll;
        HorizontalScrollView topScroll,horzScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);

        leftScroll=(ScrollView)findViewById(R.id.leftScroll);
        verticalScroll=(ScrollView)findViewById(R.id.verticalScroll);
        topScroll=(HorizontalScrollView)findViewById(R.id.topScroll);
        horzScroll=(HorizontalScrollView)findViewById(R.id.horzScroll);

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

        for (int i = 1; i <= 10; i++)//10 is no of images per day from db 3-10
        {
            final ImageView btn = new ImageView(this);
            btn.setBackgroundResource(R.drawable.train_1);
            topLinearLayout.addView(btn, topLayoutParams);
        }

        for (int i = 1; i <= 10; i++)//attempts per day
        {
            final TextView btn = new TextView(this);
            btn.setText("ಪ್ರಯತ್ನ " + i);
            btn.setTextSize(16);
            btn.setGravity(5);
            leftLinearLayout.addView(btn, leftLayoutParams);

            final LinearLayout layout=new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            //layout.setBackgroundResource(R.drawable.circle_button);
            for(int j=1;j<=10;j++)
            {
                final Button btn1=new Button(this);
                btn1.setBackgroundResource(R.drawable.train_1);
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
