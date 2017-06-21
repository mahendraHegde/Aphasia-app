package com.example.accer.callender;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import static android.R.interpolator.linear;

public class Results extends AppCompatActivity implements View.OnClickListener, OnChartValueSelectedListener {

    LinearLayout linearLayout;
    LinearLayout.LayoutParams layoutParams;
    HorizontalScrollView horizontalScrollView;
    Button rightbtn, leftbtn;
    int i;
    private BarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
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

        // change the position of the y-labels
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        mChart.getAxisRight().setEnabled(false);

        XAxis xLabels = mChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
        xLabels.setDrawLabels(true);
        ArrayList<String> theDays=new ArrayList<>();
        for(int i=1;i<=30;i++)
        {
            theDays.add("Day"+i);
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

        for (int i = 0; i < 30; i++) {
            float val1 = (float) 2;//successful
            float val2 = (float) 2;//successful-unsuccessful

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

        layoutParams = new LinearLayout.LayoutParams(125, 125);
        layoutParams.setMargins(10, 10, 10, 10);
        for (i = 1; i <= 30; i++) {
            final Button btn = new Button(this);
            btn.setId(i);
            btn.setText("" + btn.getId());
            btn.setBackgroundResource(R.drawable.circle_button);
            linearLayout.addView(btn, layoutParams);
            //btn = ((Button) findViewById(btn.getId()));
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),
                            "Button clicked index = " + btn.getId(), Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
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
        //TODO Auto-generated method stub

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

