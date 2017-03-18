package com.intuit.qbes.mobilescanner;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.intuit.qbes.mobilescanner.model.CompanyFileDetails;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.networking.AppController;
import com.intuit.qbes.mobilescanner.networking.DataSync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ashah9 on 2/27/17.
 */

public class DevicePairingService extends Service{


    private String pollTAG  = "Polling";
    private CompanyFileDetails cd = new CompanyFileDetails();
    private DatabaseHandler db;
    private String otp, device_id;
    private Boolean mTimeOut = false;
    private DataSync dataSync;
    public static final String PREFS_NAME = "Service_Response";
    public static final String PAIRING_DETAILS = "Pairing_Details";
    public static final String MOBILE_TIMEOUT = "Mobile_Timeout";
    private DataSync tasks;




    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        SharedPreferences PairingDetails = getSharedPreferences(PAIRING_DETAILS, MODE_PRIVATE);
        otp = PairingDetails.getString("otp", "");
        device_id = PairingDetails.getString("device_id", "");
        dataSync = new DataSync();
        PollService();
        return super.onStartCommand(intent, flags, startId);
    }



    public void PollService()
    {

        SharedPreferences TimeOut = getSharedPreferences(MOBILE_TIMEOUT, MODE_PRIVATE);
        mTimeOut = TimeOut.getBoolean("isTimeOut", false);

        try {


            String URL = " https://alpha.prc.intuit.com/prc/v1/device/pair";
            //String URL = "http://172.16.100.28:9999/api/v1/device/pair";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                    cd = CompanyFileDetails.CompanyDetailsFromJSON(response);

                                    if(cd.getPairingStatus().compareTo("5") == 0 && !mTimeOut) {
                                        db = new DatabaseHandler(getBaseContext());
                                        if (cd.getCompanyName() != null) {           //change logic once list of enum is available
                                            db.addCompanyFileDetails(cd);
                                            sendMessage("success");
                                        }

                                    }
                                else if(cd.getPairingStatus().compareTo("4") == 0 && !mTimeOut)
                                    {
                                        sendMessage("deny");
                                    }
                                    else if(cd.getPairingStatus().compareTo("6") == 0 && !mTimeOut)
                                    {
                                        sendMessage("timeout");
                                    }
                                else if(!mTimeOut){
                                            PollService();
                                    }
                                    else if(mTimeOut){
                                     sendMessage("timeout");
                                    }

                                //stop service; if success save response to DB send success response to codefrag; else send timeout error; deny error

                            } catch (Exception e) {

                                Log.d("Error:", e.getMessage());

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Response", "Error: " + error.getMessage());
                    if (error instanceof NoConnectionError) {
                        //No Internet Error
                        sendMessage("NoInternet");

                    }
                    if (error instanceof TimeoutError)
                    {
                        sendMessage("ServiceError");

                    }
                /*    if (error instanceof ServerError)
                    {
                        ServerIssueDialog(getBaseContext());
                    }*/
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("deviceId", device_id);
                    params.put("otp", otp );

                    return params;
                }


            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    0,  // maxNumRetries = 0 means no retry
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            stringRequest.setTag(pollTAG);
            AppController.getInstance().addToRequestQueue(stringRequest);
        }
        catch(Exception e)
        {
            Log.d("Fetch Error:",e.getMessage());
        }
    }



    //send local broadcast to CodeEntryFragment

    private void sendMessage( String status) {

        //put IF check for success or time out error or deny

        Log.d("sender", "Broadcasting Message");
        Intent intent = new Intent("PollingResponse");
        intent.putExtra("message", status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        SaveServiceResponse(status);
        //stopSelf();

    }

    @Override
    public void onDestroy(){
       // Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
       // mTimeOut = true;
        if(AppController.getInstance().getRequestQueue()!=null)
        {
            AppController.getInstance().getRequestQueue().cancelAll(pollTAG);
        }
        super.onDestroy();
    }

    public void SaveServiceResponse(String response)
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Writing data to SharedPreferences
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Response", response);

        editor.commit();

    }



    

}
