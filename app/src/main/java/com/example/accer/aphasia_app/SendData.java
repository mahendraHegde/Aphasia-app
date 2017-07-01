package com.example.accer.aphasia_app;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mahendra on 6/27/2017.
 */

public class SendData extends IntentService {
    Meta meta;
    long sentCount=0;
    ADB db;
    public SendData() {
        super(SendData.class.getName());
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            db=new ADB(getApplicationContext());
            meta=new Meta(getApplicationContext());
            if(meta.read()!=null)
                meta=meta.read();
            sendReports();
        }catch (Exception e){

        }

    }

    void sendReports() throws JSONException {
        Map<String, String> params = new HashMap<>();
        params.put("patient_id",meta.getPatientId());
        params.put("transaction",getTrans());
        if(getFollowUp().length()>20&&!meta.isFollowUpSent())
            params.put("followup",getFollowUp());
        if(sentCount>0||(getFollowUp().length()>20&&!meta.isFollowUpSent())) {
            final GetVolleyResponse response = new GetVolleyResponse(SendData.this);
            response.getResponseService(MainActivity.SERVER_URL +Home.TRANSACTION_URL, params, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        if(jsonArray.length()>0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code").toLowerCase();
                            if (code.contains("success")) {
                                meta.setCountOfTransactionsSent(meta.getCountOfTransactionsSent() + sentCount);
                                if (getFollowUp().length() > 20 && !meta.isFollowUpSent()) {
                                    meta.setFollowUpSent(true);
                                }
                                meta.write();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    }
    String getTrans() throws JSONException {
        ArrayList<ADB.Transactions> list;
        JSONArray array=new JSONArray();
        JSONObject obj=new JSONObject();
        list=db.getTransactions();
        sentCount=list.size();
        for (int i=0;i<list.size();i++){
            ADB.Transactions t=list.get(i);
            JSONObject tempObj=new JSONObject();
            tempObj.put("type",t.getType());
            tempObj.put("attempt_id",t.getAttempt_id());
            tempObj.put("pic_id",t.getPic_id());
            tempObj.put("cue1",t.getCue1());
            tempObj.put("cue2",t.getCue2());
            tempObj.put("cue3",t.getCue3());
            tempObj.put("cue4",t.getCue4());
            tempObj.put("time",t.getTime());
            tempObj.put("day",t.getDay());
            tempObj.put("date",t.getDate());


            array.put(tempObj);
        }
        obj.put("transactions",array);
        return obj.toString();
    }


    String getFollowUp() throws JSONException {
        ArrayList<ADB.Record> list;
        JSONArray array=new JSONArray();
        JSONObject obj=new JSONObject();
        list=db.getFollowUp();
        for (int i=0;i<list.size();i++){
            ADB.Record t=list.get(i);
            JSONObject tempObj=new JSONObject();
            tempObj.put("pic_id",t.getId());
            tempObj.put("value",t.getDayten());

            array.put(tempObj);
        }
        obj.put("followup",array);
        return obj.toString();
    }


}
