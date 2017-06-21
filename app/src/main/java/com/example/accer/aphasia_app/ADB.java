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
                "valid INTEGER DEFAULT 1" +
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
        ContentValues values=new ContentValues();
        values.put("type",type);
        values.put("attempt_id",attempt);
        values.put("pic_id",pic_id);
        values.put("cue1",cue1);
        values.put("cue2",cue2);
        values.put("cue3",cue3);
        values.put("cue4",cue4);
        values.put("time",time);
        try {
            db.insertOrThrow(TABLE_TRANSACTIONS,null,values);
        }catch (SQLiteConstraintException ex){
            db.execSQL("update "+TABLE_TRANSACTIONS+" set cue1=cue1+"+cue1+ ",cue2=cue2+"+cue2+",cue3=cue3+"+cue3+",cue4=cue4+"+cue4+",time=? where type="+type+" and attempt_id="+attempt+" and pic_id="+pic_id,new String[]{time});
        }finally {
            db.close();

        }
    }

    public  int getSuccessfulPicsCount(int day){
        try {
            int noOfQuestions=new Meta(ctx).read().getNoOfQuestions();
            SQLiteDatabase db=this.getReadableDatabase();
            String limit=(day*noOfQuestions)+","+noOfQuestions;
            Cursor cursor=db.rawQuery("select count(pic_id) from "+TABLE_TRANSACTIONS +" where type=? and cue4=? and attempt_id=? and pic_id in(select id from "+TABLE_DATA+" where valid=? limit "+limit+") ",new String[]{"0","0","1","1"});
            cursor.moveToFirst();
            return cursor.getInt(0);
        }catch (Exception e){
            Toast.makeText(ctx,"Training has not started yet..",Toast.LENGTH_LONG).show();
        }
       return 0;

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

    public String query(String q){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.query(TABLE_DATA,new String[]{q},null,null,null,null,null);
        String result=null;
        if(cursor.moveToFirst()){
            result=cursor.getString(0);
        }
        return result;
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
}
