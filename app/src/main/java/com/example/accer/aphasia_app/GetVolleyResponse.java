package com.example.accer.aphasia_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by Mahendra on 2/26/2017.
 */
public class GetVolleyResponse {
    ProgressDialog progress;
    Context ctx;
    AlertDialog.Builder alert;
    GetVolleyResponse(Context ctx)
    {
        this.ctx=ctx;
        progress=new ProgressDialog(this.ctx);
        progress.setMessage("ದಯಮಾಡಿ ನಿರೀಕ್ಷಿಸಿ...");
        alert=new AlertDialog.Builder(this.ctx);
    }
    public void getResponse(String url, final Map params, final VolleyCallback callback)
    {
        progress.show();
        StringRequest stringRequest=new StringRequest( Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                String message = null;
                if (error instanceof NetworkError) {
                    message ="ಇಂಟರ್ನೆಟ್ಗೆ ಸಂಪರ್ಕಿಸಲು ಸಾಧ್ಯವಿಲ್ಲ ... ದಯವಿಟ್ಟು ನಿಮ್ಮ ಸಂಪರ್ಕವನ್ನು ಪರಿಶೀಲಿಸಿ!";
                } else if (error instanceof ServerError) {
                    message = "ಸರ್ವರ್ ಕಂಡುಬಂದಿಲ್ಲ. ಸ್ವಲ್ಪ ಸಮಯದ ನಂತರ ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ!";
                } else if (error instanceof AuthFailureError) {
                    message = "ಇಂಟರ್ನೆಟ್ಗೆ ಸಂಪರ್ಕಿಸಲು ಸಾಧ್ಯವಿಲ್ಲ ... ದಯವಿಟ್ಟು ನಿಮ್ಮ ಸಂಪರ್ಕವನ್ನು ಪರಿಶೀಲಿಸಿ!";
                } else if (error instanceof ParseError) {
                    message = "ಪಾರ್ಸಿಂಗ್ ದೋಷ! ಸ್ವಲ್ಪ ಸಮಯದ ನಂತರ ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ!";
                } else if (error instanceof NoConnectionError) {
                    message = "ಇಂಟರ್ನೆಟ್ಗೆ ಸಂಪರ್ಕಿಸಲು ಸಾಧ್ಯವಿಲ್ಲ ... ದಯವಿಟ್ಟು ನಿಮ್ಮ ಸಂಪರ್ಕವನ್ನು ಪರಿಶೀಲಿಸಿ!";
                } else if (error instanceof TimeoutError) {
                    message = "ಸಂಪರ್ಕ ಸಮಯ ಮೀರಿದೆ! ದಯವಿಟ್ಟು ನಿಮ್ಮ ಇಂಟರ್ನೆಟ್ ಸಂಪರ್ಕವನ್ನು ಪರಿಶೀಲಿಸಿ.";
                }
                alert.setTitle("ERROR").setMessage(message)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {

                            }
                        })
                        .show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        MySingleton.getInstance(ctx).addToRequestQueue(stringRequest);
    }





    public void getResponseService(String url, final Map params, final VolleyCallback callback)
    {
        StringRequest stringRequest=new StringRequest( Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        MySingleton.getInstance(ctx).addToRequestQueue(stringRequest);
    }

}