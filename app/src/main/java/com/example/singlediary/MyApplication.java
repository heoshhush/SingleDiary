package com.example.singlediary;

import android.app.Application;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }

    public static interface OnResponseListener{
        public void processResponse(int requestCode, int responseCode, String response);
    }

    public static void send(int requestMethod, String url, final int requestCode, final OnResponseListener onResponseListener, final Map<String, String> params){

        StringRequest stringRequest = new StringRequest(
                requestMethod,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response for " + requestCode + " -> " + response);

                        if (onResponseListener != null) {
                            onResponseListener.processResponse(requestCode, 200, response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error for " + requestCode + " -> " + error.getMessage());
                        if(onResponseListener != null){
                            onResponseListener.processResponse(requestCode, 400, error.getMessage());
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if(MyApplication.requestQueue != null){
            MyApplication.requestQueue.add(stringRequest);
            Log.d(TAG, "Request sent : " + requestCode);
            Log.d(TAG, "Request url : " + url);
        } else {
            Log.d(TAG, "Request queue is null.");
        }
    }
}
