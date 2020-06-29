package com.example.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.smsreader.exception.BookingApiException;

public class SMSLog extends BroadcastReceiver {
    //private BlockingDeque<String> queue = new LinkedBlockingDeque>String>();

    private String TAG = this.getClass().getSimpleName();

    public SMSLog(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Recevied a sms to target device");
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msgFrom= null;
            String msgBody;
            ApiClient apiClient = new ApiClient();

            if (bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msgFrom = msgs[i].getOriginatingAddress();
                        msgBody = msgs[i].getMessageBody();
                        Log.i(TAG,"RECEIVED SOURCE :"+msgFrom+" RECEVIED MSG :"+msgBody);
                        if(msgBody.length() < 7){
                            apiClient.setNextAppoinmentNumber(context,msgFrom,msgBody);
                        }

                    }
                }
                catch (Exception ex){
                    Log.e(TAG,"Could not make a booking for : "+msgFrom);
                }
            }
        }

    }

}
