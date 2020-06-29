package com.example.smsreader;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.smsreader.exception.BookingApiException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiClient {

    private String endPoint ="http://192.168.1.5:3000/";
    //"http://127.0.0.1:3000/sendsms";
    //"http://dummy.restapiexample.com/api/v1/employees";
    private static final String APPOINMENT_NO = "appoinmentNo";
    private static final String TIME_SLOT="time";
    private static final String DOCTOR = "doctor";
    private static final String DISPENSARY = "dispensary";
    private static final String REFERENCE_NO = "referenceNo";

    private static  final String PENDING = "pending";
    private static  final String DONE = "done";
    private static  final String ERROR = "error";
    private String TAG =this.getClass().getSimpleName();
    private int nextAppoinmentNo=0;
    private String timeSlot="";
    private String doctor="";
    private String dispensary="";




    public void setNextAppoinmentNumber(final Context context, final String source, final String code) throws JSONException  {

        Log.i(TAG,"Invoke REST API ..."+endPoint);
        makeRecord(context,source,code,PENDING,null);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, endPoint.concat("booking"), requestObj(code), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG,"Response from API :"+response);
                        try {
                            nextAppoinmentNo = response.getInt(APPOINMENT_NO);
                            timeSlot = response.getString(TIME_SLOT);
                            if(nextAppoinmentNo >0){
                                Log.i(TAG,"Before smsManager get default :"+nextAppoinmentNo);
                                SmsManager smsManager = SmsManager.getDefault();
                                try{
                                    smsManager.sendTextMessage(source, null,
                                            generateSMS(response.getString(REFERENCE_NO),response.getInt(APPOINMENT_NO),response.getString(TIME_SLOT),
                                                    response.getString(DOCTOR),response.getString(DISPENSARY)),
                                            null, null);
                                    makeRecord(context,source,code,DONE,null);
                                }catch (Exception e){
                                    makeRecord(context,source,code,ERROR,e.getMessage());
                                    Log.e(TAG,"Error occur while sending the sms : "+e.getMessage());
                                    e.printStackTrace();
                                }

                            }
                        } catch (Exception e) {
                            makeRecord(context,source,code,ERROR,e.getMessage());
                            Log.e(TAG,"Error occur while fetch api :"+e.getMessage());

                        }
                    }
                },new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        makeRecord(context,source,code,ERROR,error.getMessage());
                        Log.e(TAG,"Error occur while fetching data : "+error.getMessage());
                    }
                });

        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    private JSONObject requestObj(String code) throws JSONException{
        JSONObject req = new JSONObject();
        req.put("code",code);
        return req;
    }

    private void makeRecord(Context context,String source,String code,String status,String error){
        Log.i(TAG, "Make record to keep track");
        final JSONObject reqObj = new JSONObject();
        try{
            reqObj.put("source",source);
            reqObj.put("code",code);
            reqObj.put("status",status);
            reqObj.put("error",error);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        JsonObjectRequest record = new JsonObjectRequest(Request.Method.POST, endPoint.concat("record"), reqObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG,"Record write Success "+response);
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e(TAG,"Error occur while make record "+error.getMessage());
            }
        });

        MySingleton.getInstance(context).addToRequestQueue(record);

    }

    private String generateSMS(String referenceNo,int appoinmentNo,String time,String doctor,String dispensary){
        StringBuilder sb = new StringBuilder();
        sb.append("Ref No: "+referenceNo+", ");
        sb.append(doctor+", ");
        sb.append("No: "+appoinmentNo+", ");
        sb.append(time+" , ");
        sb.append(dispensary);
        return sb.toString();
    }

//    public void post(Context context,String source,String code) throws BookingApiException {
//        Log.i(TAG,"Invorking REST API ...");
//        Map<String,String> m = new HashMap<String, String>();
//        m.put("source",source);
//        m.put("code",code);
//        m.put("error","Error");
//
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.POST, endPoint, new JSONObject(), new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i(TAG,"Response from REST API :"+response);
//                        try {
//                            nextAppoinmentNo = 8;// response.getInt(KEY_EMPLOYEE_ID);
//                        } catch (Exception e) {
//                            Log.e(TAG,"Error occur while fetch api");
//                            Log.e(TAG,e.getMessage());
//
//                        }
//                    }
//                },new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG,"Error "+error.getMessage());
//                        Log.e(TAG,error.getMessage());
//                    }
//                });
//        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
//        if(nextAppoinmentNo !=0){
//            //return nextAppoinmentNo;
//        }
//        Log.i(TAG,"Sending Error .......");
//        throw new BookingApiException("Api Error");
//    }


    public int accessEndPoint(Context context){
        Integer employeeId =0;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, endPoint, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("!!!!!!!!!!!!!!!!!!! "+response );
                            //Parse the JSON response
                            Integer employeeId = response.getInt(APPOINMENT_NO);
                            System.out.println("MMMMMMMMMMMMMMMMMM "+employeeId);
                            try {

                             //   SmsManager sms = SmsManager.getDefault(); // using android SmsManager sms.sendTextMessage(phone_Num, null, send_msg, null, null); // adding number and text
                             //   sms.sendTextMessage("5554", null, "Test msg", null, null);

                            } catch (Exception e) {


                                e.printStackTrace();

                            }
//                            String name = response.getString(KEY_NAME);
//                            String dob = response.getString(KEY_DOB);
//                            String designation = response.getString(KEY_DESIGNATION);
//                            String contactNumber = response.getString(KEY_CONTACT_NUMBER);
//                            String email = response.getString(KEY_EMAIL);
//                            String salary = response.getString(KEY_SALARY);
//
//                            //Create String out of the Parsed JSON
//                            StringBuilder textViewData = new StringBuilder().append("Employee Id: ")
//                                    .append(employeeId.toString()).append(NEW_LINE);
//                            textViewData.append("Name: ").append(name).append(NEW_LINE);
//                            textViewData.append("Date of Birth: ").append(dob).append(NEW_LINE);
//                            textViewData.append("Designation: ").append(designation).append(NEW_LINE);
//                            textViewData.append("Contact Number: ").append(contactNumber).append(NEW_LINE);
//                            textViewData.append("Email: ").append(email).append(NEW_LINE);
//                            textViewData.append("Salary: ").append(salary).append(NEW_LINE);
//
//                            //Populate textView with the response
//                            mTxtDisplay.setText(textViewData.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
        return employeeId;

    }
}



