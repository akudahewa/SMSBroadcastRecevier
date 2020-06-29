package com.example.smsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private SMSLog broadcastReceiver = null;
    int SMS_PERMISSION_REQ_CODE_SUBMIT = 101;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int receiveSMSPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS);
        Log.i(TAG, "Permission - Receive SMS :" + receiveSMSPermission);

        if (receiveSMSPermission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Requesting permissions for RECEIVE_SMS");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS},
                    SMS_PERMISSION_REQ_CODE_SUBMIT);
        }

        int sendSMSPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS);
        Log.i(TAG,"Permission - Send SMS"+sendSMSPermission);

        if (sendSMSPermission  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }

        Intent backgroundService = new Intent(getApplicationContext(), MyService.class);
        System.out.println("SDK" +Build.VERSION.SDK_INT);
        System.out.println("BUILD "+Build.VERSION_CODES);
        startService(backgroundService);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.i(TAG,"Start Foregound .......");
//            startForegroundService(backgroundService);
//        } else {
//            Log.i(TAG,"Start Background  .......");
//            startService(backgroundService);
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver !=null){
            Log.i(TAG,"Unregistering broadcastReceiver onDestroy");
            unregisterReceiver(broadcastReceiver);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(broadcastReceiver!=null){
            Log.i(TAG,"Unregistering broadcastReceiver onPause");
            unregisterReceiver(broadcastReceiver);
        }


    }

}
