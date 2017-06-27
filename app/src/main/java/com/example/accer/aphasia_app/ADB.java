package com.example.accer.aphasia_app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.TabLayout;
import android.support.transition.Transition;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Mahendra on 5/23/2017.
 */

public class ADB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    public final static String DATABASE_NAME="APHASIA";
    private static final String TABLE_DATA="DATA";
    private static final String TABLE_TRANSACTIONS="TRANSACTIONS";

    Context ctx;



    public ADB(Context ctx){
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx=ctx;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createData="create table  "+TABLE_DATA+" ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
                "pic TEXT UNIQUE NOT NULL," +
                "valid INTEGER DEFAULT 1," +
                "dayten INTEGER DEFAULT -1" +
                ")";
        db.execSQL(createData);

        String createTransactions="create table  "+TABLE_TRANSACTIONS+" ( " +
                "type INTEGER, "+
                "attempt_id INTEGER, "+
                "pic_id INTEGER ," +
                "cue1 INTGER DEFAULT 0," +
                "cue2 INTGER DEFAULT 0," +
                "cue3 INTGER DEFAULT 0," +
                "cue4 INTGER DEFAULT 0," +
                "time TEXT," +
                "day Integer DEFAULT 0," +
                "PRIMARY KEY (type,attempt_id,pic_id)"+
                ")";
       // db.execSQL("PRAGMA foreign_keys=ON");
        db.execSQL(createTransactions);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF  EXISTS "+TABLE_DATA);
        db.execSQL("DROP TABLE IF  EXISTS "+TABLE_TRANSACTIONS);
        onCreate(db);
    }

    public int[] getLastAttemptFromTransaction(String pic,int type){
        SQLiteDatabase db=this.getReadableDatabase();
        int []arr=new int[2];
        Cursor cursor=db.query(TABLE_DATA,new String[] {"id"},"pic=?",new String[]{pic},null,null,null,null);
        if(cursor.moveToFirst())
            arr[0]=Integer.parseInt(cursor.getString(0));
        else
            arr[0]=0;
        Cursor c=db.query(TABLE_TRANSACTIONS,new String[]{"MAX(attempt_id)"},"pic_id=? ",new String[]{cursor.getString(0)},null,null,null,null);
        if(c.moveToFirst()&&c.getString(0)!=null)
          arr[1]=Integer.parseInt(c.getString(0));
        else
            arr[1]=0;
        return arr;
    }

    public void addTransaction(int type,int attempt,int pic_id,int cue1,int cue2,int cue3,int cue4,String time){
        SQLiteDatabase db=this.getWritableDatabase();
        int day=new Meta(ctx).read().getDay();
        day++;
        ContentValues values=new ContentValues();
        values.put("type",type);
        values.put("attempt_id",attempt);
        values.put("pic_id",pic_id);
        values.put("cue1",cue1);
        values.put("cue2",cue2);
        values.put("cue3",cue3);
        values.put("cue4",cue4);
        values.put("time",time);
        values.put("day",day);
        try {
            db.insertOrThrow(TABLE_TRANSACTIONS,null,values);
        }catch (SQLiteConstraintException ex){
            db.execSQL("update "+TABLE_TRANSACTIONS+" set cue1=cue1+"+cue1+ ",cue2=cue2+"+cue2+",cue3=cue3+"+cue3+",cue4=cue4+"+cue4+",time=?,day=? where type="+type+" and attempt_id="+attempt+" and pic_id="+pic_id,new String[]{time,""+day});
        }finally {
            db.close();

        }
    }

    public  int getSuccessfulPicsCount(int day){
        SQLiteDatabase db=this.getReadableDatabase();
        try {
            int noOfQuestions=new Meta(ctx).read().getNoOfQuestions();

            String limit=(day*noOfQuestions)+","+noOfQuestions;
            Cursor cursor=db.rawQuery("select count(pic_id) from "+TABLE_TRANSACTIONS +" where type=? and cue4=? and attempt_id=? and pic_id in(select id from "+TABLE_DATA+" where valid=? limit "+limit+") ",new String[]{"0","0","1","1"});
            cursor.moveToFirst();
            return cursor.getInt(0);
        }catch (Exception e){
            Toast.makeText(ctx,"Training has not started yet..",Toast.LENGTH_LONG).show();
        }finally {
            db.close();
        }
       return 0;

    }

    public int[] getSuccessFailTransactionArray(){
        int arr[]={0,0};
        SQLiteDatabase db=this.getReadableDatabase();
        try{
            Cursor cursor=db.query(TABLE_TRANSACTIONS,new String[]{"count(*)"},"cue4=?",new String[]{"1"},null,null,null,null);
            if(cursor.moveToFirst()){
                arr[0]=cursor.getInt(0);
            }else {
                arr[0]=0;
            }
            cursor=db.query(TABLE_TRANSACTIONS,new String[]{"count(*)"},null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                arr[1]=cursor.getInt(0);
            }else {
                arr[1]=0;
            }
        }catch (Exception e){
            return arr;
        }finally {
            db.close();
        }
        return arr;
    }



    public boolean checkForYesterdayTest(int day){
        int noOfQuestions=new Meta(ctx).read().getNoOfQuestions();
        String limit=(day*noOfQuestions)+","+noOfQuestions;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select count(*) from "+TABLE_TRANSACTIONS +" where  pic_id in(select id from "+TABLE_DATA+" where valid=? limit "+limit+") ",new String[]{"1"});
        if(cursor.moveToFirst()){
            if(cursor.getInt(0)>0) {
                return false;
            }
        }
        return true;

    }

    public void addData(String pic){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("pic",pic);
        try {
            db.insertOrThrow(TABLE_DATA,null,values);
        }
        catch (SQLiteConstraintException ex){

        }
        finally {
            db.close();
        }
    }

    public boolean updateValid(String pic,int valid){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("valid",valid);
        return  db.update(TABLE_DATA,values,"pic=?",new String[]{pic})>0;
    }
    public boolean updateDayTen(String pic,int dayten){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("dayten",dayten);
        return  db.update(TABLE_DATA,values,"pic=?",new String[]{pic})>0;
    }

    public ArrayList<Collect> getData(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_DATA,new String[] {"id","pic","valid"},null,null,null,null,null);
        ArrayList<Collect> list=new ArrayList<Collect>();
        if(cursor.moveToFirst()){
            do{
                list.add(new Collect(Integer.parseInt(cursor.getString(0)),cursor.getString(1),Integer.parseInt(cursor.getString(2))));
            }while(cursor.moveToNext());
        }
        return list;
    }



    public ArrayList<Collect> getData(String field,String val){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_DATA,new String[] {"id","pic","valid"},field+"="+val,null,null,null,null,null);
        ArrayList<Collect> list=new ArrayList<Collect>();
        if(cursor.moveToFirst()){
            do{
                list.add(new Collect(Integer.parseInt(cursor.getString(0)),cursor.getString(1),Integer.parseInt(cursor.getString(2))));
            }while(cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public ArrayList<Record> getFollowUp(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select id,dayten from "+TABLE_DATA+" where dayten=? or dayten=?",new String[]{"1","0"});
        ArrayList<Record> list=new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                list.add(new Record(cursor.getInt(1),cursor.getInt(0)));
            }while(cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public ArrayList<Transactions>getTransactions(){
        ArrayList<Transactions>list=new ArrayList<Transactions>();
        SQLiteDatabase db=this.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, TABLE_TRANSACTIONS);
        long sentCount=new Meta(ctx).read().getCountOfTransactionsSent();
        String limit=sentCount+","+(cnt-sentCount);
        try {

            Cursor c=db.query(TABLE_TRANSACTIONS,new String[]{"type","attempt_id","pic_id","cue1","cue2","cue3","cue4","time","day"},null,null,null,null,null,limit);
            if(c.moveToFirst()){

                do{
                    list.add(new Transactions(c.getInt(0),c.getInt(1),c.getInt(2),c.getInt(3),c.getInt(4),c.getInt(5),c.getInt(6),c.getString(7),c.getInt(8)));
                }while (c.moveToNext());

            }else
                return list;

        }catch (Exception e){
            return list;
        }finally {
            db.close();
        }
        return list;

    }
    public String[] getDataPics(String field,String val){
        int i=0;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_DATA,new String[]{"pic"},field+"="+val,null,null,null,"id ASC",null);

        String pics[]=new String[cursor.getCount()];

        if(cursor.moveToFirst()){
            do {
                pics[i++] = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        db.close();
        return  pics;
    }


    public String[] getDataPics(String field,String val,String limit){
        int i=0;
        SQLiteDatabase db=this.getReadableDatabase();
       // Toast.makeText(ctx,"limit : "+limit,Toast.LENGTH_SHORT).show();
        Cursor cursor=db.query(TABLE_DATA,new String[]{"pic"},field+"="+val,null,null,null,"id ASC",limit);
        String pics[]=new String[cursor.getCount()];

        if(cursor.moveToFirst()){
            do {
                pics[i++] = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        db.close();
        return  pics;
    }

    public int isTransactionSuccessFull(int attempt,String pic,int type){
        Cursor cursor=null;
        SQLiteDatabase db=this.getReadableDatabase();
        if(type>=0&&type<=2){
            cursor=db.rawQuery("select cue4 from "+TABLE_TRANSACTIONS +" where attempt_id=? and type=?  and  pic_id in(select id from "+TABLE_DATA+" where pic=?) ",new String[]{""+attempt,""+type,pic});
        }else
            cursor=db.rawQuery("select cue4 from "+TABLE_TRANSACTIONS +" where attempt_id=?  and  pic_id in(select id from "+TABLE_DATA+" where pic=?) ",new String[]{""+attempt,pic});
        if(cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return -1;
    }
    public int getMaxAttemptsOfDay(int day,int type){
        int noOfQuestions=new Meta(ctx).read().getNoOfQuestions();
        Cursor cursor=null;
        SQLiteDatabase db=this.getReadableDatabase();
        String limit=(day*noOfQuestions)+","+noOfQuestions;
        if(type>=0&&type<=2){
            cursor=db.rawQuery("select count(attempt_id) from "+TABLE_TRANSACTIONS +" where type=? and  pic_id in(select id from "+TABLE_DATA+" where valid=?  limit "+limit+")",new String[]{""+type,"1"});

        }else
            cursor=db.rawQuery("select max(attempt_id) from "+TABLE_TRANSACTIONS +" where  pic_id in(select id from "+TABLE_DATA+" where valid=? limit "+limit+") ",new String[]{"1"});
        if(cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return 0;

    }

    public void deleteData(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_DATA,"id=?",new String[]{String.valueOf(id)});
        db.close();
    }
    public void deleteAllDataAndTransactions(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DROP TABLE IF  EXISTS "+TABLE_DATA);
        db.execSQL("DROP TABLE IF  EXISTS "+TABLE_TRANSACTIONS);

        onCreate(db);
    }
    public long getDataRowCount(){
        SQLiteDatabase db=this.getReadableDatabase();
        return  DatabaseUtils.queryNumEntries(db,TABLE_DATA);
    }



    public class  Collect{
        int id,valid;
        String pic;


        public Collect(int id, String pic,int valid) {
            this.id = id;
            this.pic=pic;
            this.valid=valid;
        }

        public int getId() {
            return id;
        }

        public String getPic() {
            return pic;
        }


        public int getValid() {
            return valid;
        }
    }

    class Transactions{
        private int type,attempt_id,pic_id,cue1,cue2,cue3,cue4,day;
        private String time;
        Transactions(int type,int attempt_id,int pic_id,int cue1,int cue2,int cue3,int cue4,String time,int day){
            this.type=type;
            this.attempt_id=attempt_id;
            this.pic_id=pic_id;
            this.cue1=cue1;
            this.cue2=cue2;
            this.cue3=cue3;
            this.cue4=cue4;
            this.time=time;
            this.day=day;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getAttempt_id() {
            return attempt_id;
        }

        public void setAttempt_id(int attempt_id) {
            this.attempt_id = attempt_id;
        }

        public int getPic_id() {
            return pic_id;
        }

        public void setPic_id(int pic_id) {
            this.pic_id = pic_id;
        }

        public int getCue1() {
            return cue1;
        }

        public void setCue1(int cue1) {
            this.cue1 = cue1;
        }

        public int getCue2() {
            return cue2;
        }

        public void setCue2(int cue2) {
            this.cue2 = cue2;
        }

        public int getCue3() {
            return cue3;
        }

        public void setCue3(int cue3) {
            this.cue3 = cue3;
        }

        public int getCue4() {
            return cue4;
        }

        public void setCue4(int cue4) {
            this.cue4 = cue4;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
    class Record{
        int dayten,id;
        Record(int dayten,int id){
            this.dayten=dayten;
            this.id=id;
        }

        public int getDayten() {
            return dayten;
        }

        public int getId() {
            return id;
        }
    }
}
