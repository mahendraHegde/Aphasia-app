package com.example.accer.aphasia_app;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mahendra on 6/2/2017.
 */

public  class Meta implements Serializable {
    int noOfQuestions;
    int day;
    int baselinePosition,trainingPosition, trainingSavedCounter;
    String startTime;
    String time1;
    String time2;
    Calendar startDate;
    Calendar lastDate;
    boolean todayTrainingOver,baselineOver;
   static Context ctx;
    Meta(Context c){
        ctx=c;
        noOfQuestions=0;
        day=0;
        baselinePosition=0;
        trainingPosition=-1;
        trainingSavedCounter=0;
        startTime="";
        time1="";
        time2="";
        todayTrainingOver=false;
        baselineOver=false;
        setCalendar();
    }
    private void setCalendar(){
        startDate=Calendar.getInstance();
        startDate.set(Calendar.YEAR,1700);
        lastDate=Calendar.getInstance();
        lastDate.set(Calendar.YEAR,1700);
    }

    public boolean isBaselineOver() {
        return baselineOver;
    }

    public void setBaselineOver(boolean baselineOver) {
        this.baselineOver = baselineOver;
    }

    public boolean isTodayTrainingOver() {
        return todayTrainingOver;
    }

    public void setTodayTrainingOver(boolean todayTrainingOver) {
        this.todayTrainingOver = todayTrainingOver;

    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getLastDate() {
        return lastDate;
    }

    public void setLastDate(Calendar lastDate) {
        this.lastDate = lastDate;
    }

    public int getTrainingSavedCounter() {
        return trainingSavedCounter;
    }

    public void setTrainingSavedCounter(int trainingSavedCounter) {
        this.trainingSavedCounter = trainingSavedCounter;
    }

    public int getTrainingPosition() {
        return trainingPosition;
    }

    public void setTrainingPosition(int trainingPosition) {
        this.trainingPosition = trainingPosition;
    }

    public int getBaselinePosition() {
        return baselinePosition;
    }

    public void setBaselinePosition(int baselinePosition) {
        this.baselinePosition = baselinePosition;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getNoOfQuestions() {
        return noOfQuestions;
    }

    public void setNoOfQuestions(int noOfQuestions) {
        this.noOfQuestions = noOfQuestions;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public void write(){
        String filename = "meta.ap";
        ObjectOutput out = null;
        new File(Environment.getExternalStorageDirectory()+File.separator+".apasia/").mkdir();
        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(Environment.getExternalStorageDirectory(),File.separator+".apasia")+File.separator+filename));
            out.writeObject(this);
            out.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
           // e.printStackTrace();
        }
    }

    public Meta read(){
        ObjectInputStream input;
        String filename = "meta.ap";
        Meta obj=null;
        try {
            input = new ObjectInputStream(new FileInputStream(new File(new File(Environment.getExternalStorageDirectory(),File.separator+".apasia")+File.separator+filename)));
            obj = (Meta) input.readObject();
            input.close();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    void deleteBackup(){
        File dir = new File(Environment.getExternalStorageDirectory()+"/.apasia/");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                if(new File(dir, children[i]).delete())
                    Toast.makeText(ctx,"deltd",Toast.LENGTH_SHORT).show();
            }
        }
    }
/*
    public void restoreDataBase(){
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data/package name/databases/database_name";
                String backupDBPath = "database_name";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getApplicationContext(), "Database Restored successfully", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
        }
    }*/




}

