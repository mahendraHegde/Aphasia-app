package com.example.accer.aphasia_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Mahendra on 5/23/2017.
 */

public class ADB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    public final static String DATABASE_NAME="APHASIA";
    private static final String TABLE_DATA="DATA";
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF  EXISTS "+TABLE_DATA);
        onCreate(db);
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



    public void deleteData(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_DATA,"id=?",new String[]{String.valueOf(id)});
        db.close();
    }
    public void deleteAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DROP TABLE IF  EXISTS "+TABLE_DATA);
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
