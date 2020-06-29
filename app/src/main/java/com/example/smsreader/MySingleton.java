package com.example.smsreader;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton extends Application {

    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private String TAG = this.getClass().getSimpleName();


    private MySingleton(){
        System.out.println(("======================MySingleton constructor() ============================="));
        try{
            //MySingleton.mCtx =getApplicationContext();
            System.out.println("############# context obj ########## "+mCtx);
            mRequestQueue = getRequestQueue();
        }catch (Exception e){
            System.out.println("############# eeeeeeeeeeeeeee ########## "+e.getMessage());
            e.printStackTrace();

        }

    }

    private MySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }


    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public static synchronized MySingleton getInstance() {
        System.out.println(("======================getInstance() ============================="));
        if (mInstance == null) {
            System.out.println(("==========null instense ============getInstance() ============================="));
            mInstance = new MySingleton();
        }
        System.out.println("=++++++++++++++++++++++++++++instence ++++++ "+mInstance);
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            Log.i(TAG,"Getting request queue "+mCtx.getApplicationContext());
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        Log.i(TAG,"Add request to request queue ");
        getRequestQueue().add(req);
    }
}
