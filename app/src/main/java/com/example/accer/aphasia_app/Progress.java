package com.example.accer.aphasia_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.jar.Attributes;

public class Progress extends AppCompatActivity implements View.OnClickListener, OnChartValueSelectedListener {

    LinearLayout linearLayout;
    RelativeLayout topScrollButton;
    LinearLayout.LayoutParams layoutParams,topScrollParams;
    HorizontalScrollView horizontalScrollView;
    ViewGroup.LayoutParams lp;
    Button rightbtn, leftbtn;
    int i;
    private BarChart mChart;
    int valid,invalid;
    Meta meta;
    ADB db;
    HorizontalScrollView container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        meta=new Meta(this);
        if(meta.read()!=null){
            meta=meta.read();
        }
        db=new ADB(this);

        linearLayout = (LinearLayout) findViewById(R.id.dayContainer);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.scroller);

        mChart = (BarChart) findViewById(R.id.monthlyGraph);
        mChart.setOnChartValueSelectedListener(this);

        mChart.getDescription().setEnabled(false);

        mChart.setMaxVisibleValueCount(30);

        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(false);
        mChart.setHighlightFullBarEnabled(false);
        mChart.getXAxis().setTextSize(20f);
        mChart.getAxisLeft().setTextSize(15f);

        // change the position of the y-labels
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        mChart.getAxisRight().setEnabled(false);

        XAxis xLabels = mChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
        xLabels.setDrawLabels(true);
        final ArrayList<String> theDays=new ArrayList<>();
        for(int i=1;i<=30;i++)
        {
            theDays.add("ದಿನ"+i);
        }

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i <30; i++) {
            valid=0;
            invalid=0;
            if(i<=meta.getDay()) {
                valid = db.getSuccessfulPicsCount(i);
                invalid = db.getDataPics("valid","1",(i*meta.getNoOfQuestions())+","+meta.getNoOfQuestions()).length - valid;
            }
            float val1 = (float) valid;//successful
            float val2 = (float) invalid;//successful-unsuccessful

            yVals1.add(new BarEntry(
                    i,
                    new float[]{val1, val2}));
        }        BarDataSet set1;
        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(theDays));

        set1 = new BarDataSet(yVals1, ":  Monthly Report");
        set1.setDrawIcons(false);
        set1.setColors(getColors());
        set1.setStackLabels(new String[]{"Successful", "Unsuccessful"});

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        BarData data = new BarData(dataSets);
        data.setValueTextColor(Color.WHITE);
        mChart.setScaleMinima((float) 30f/10f, 1f);
        mChart.setData(data);
        mChart.setVisibleYRange(0,10, YAxis.AxisDependency.LEFT);

        mChart.setFitBars(true);
        mChart.invalidate();
        mChart.animateY(3000);
        // mChart.setDrawLegend(false);

        rightbtn = (Button) findViewById(R.id.back);
        leftbtn = (Button) findViewById(R.id.next);
        rightbtn.setOnClickListener(this);
        leftbtn.setOnClickListener(this);

        //added
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        //added

        layoutParams = new LinearLayout.LayoutParams(125, 125);
        layoutParams.setMargins(10, 10, 10, 10);
        for (i = 1; i <= 30; i++) {
            final Button btn = new Button(this);
            btn.setId(i);
            btn.setText("" + btn.getId());
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            btn.setTypeface(null, Typeface.BOLD);
            if(i>meta.getDay()+1) {
                btn.setEnabled(false);
                btn.setBackgroundResource(R.drawable.circle_button);
            }
            else {
                btn.setBackgroundResource(R.drawable.over_tick);
            }
            linearLayout.addView(btn, layoutParams);
            lp=btn.getLayoutParams();
            lp.width= (int) (width*.08);
            lp.height= (int) (height*.13);
            btn.setLayoutParams(lp);

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent in=new Intent(getApplicationContext(),DailyReport.class);
                    //Intent in=new Intent(getApplicationContext(),Attempt.class);
                    in.putExtra("day",btn.getId());
                    startActivity(in);
                }
            });
        }
        //i edited

        TextView t = (TextView)findViewById(R.id.textview);
        lp=t.getLayoutParams();
        lp.width= (int) (width*.05);
        lp.height= (int) (height*.58);
        t.setLayoutParams(lp);
        t.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (height/16));

        TextView t1=(TextView)findViewById(R.id.textview12);
        t1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (height/16));


        topScrollButton=(RelativeLayout)findViewById(R.id.topScrollButtons);
        lp=topScrollButton.getLayoutParams();
        lp.width= (int) (LinearLayout.LayoutParams.MATCH_PARENT);
        lp.height= (int) (height*.15);
        topScrollButton.setLayoutParams(lp);

        lp=rightbtn.getLayoutParams();
        lp.width= (int) (width*.09);
        lp.height= (int) (LinearLayout.LayoutParams.MATCH_PARENT);
        rightbtn.setLayoutParams(lp);

        lp=leftbtn.getLayoutParams();
        lp.width= (int) (width*.09);
        lp.height= (int) (LinearLayout.LayoutParams.MATCH_PARENT);
        leftbtn.setLayoutParams(lp);

        container=(HorizontalScrollView)findViewById(R.id.scroller);
        lp=container.getLayoutParams();
        lp.width= (int) (width*.82);
        lp.height= (int) (LinearLayout.LayoutParams.MATCH_PARENT);
        container.setLayoutParams(lp);

        lp=mChart.getLayoutParams();
        lp.width= (int) (width*0.9);
        lp.height= (int) (height*.58);
        mChart.setLayoutParams(lp);

        /*lp=t.getLayoutParams();
        lp.width= (int) (height*.65);
        lp.height= (int) (width*.1);
        t.setLayoutParams(lp);*/

        //ends
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.back:
                horizontalScrollView.smoothScrollTo((int) horizontalScrollView.getScrollX() - 500, (int) horizontalScrollView.getScrollY());
                break;
            case R.id.next:
                horizontalScrollView.smoothScrollTo((int) horizontalScrollView.getScrollX() + 500, (int) horizontalScrollView.getScrollY());
                break;
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        BarEntry entry = (BarEntry) e;

        if (entry.getYVals() != null)
            Log.i("VAL SELECTED", "Value: " + entry.getYVals()[h.getStackIndex()]);
        else
            Log.i("VAL SELECTED", "Value: " + entry.getY());
    }

    @Override
    public void onNothingSelected() {
        // TODO Auto-generated method stub

    }

    private int[] getColors() {

        int stacksize = 2;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < colors.length; i++) {
            colors[i] = ColorTemplate.MATERIAL_COLORS[i];
        }

        return colors;
    }
}
