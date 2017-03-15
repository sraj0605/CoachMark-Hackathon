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
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.intuit.qbes.mobilescanner.model.CompanyFileDetails;
import com.intuit.qbes.mobilescanner.networking.AppController;
import com.intuit.qbes.mobilescanner.networking.DataSync;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ashah9 on 2/27/17.
 */

public class DevicePairingService extends Service {


    private String pollTAG  = "Polling";
    private CompanyFileDetails cd = new CompanyFileDetails();
    private DatabaseHandler db;
    private String otp, device_id;
    private Intent mIntent;
    private Boolean mTimeOut = false;
    public static final String PREFS_NAME = "Service_Response";


    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        otp = intent.getStringExtra("otp");
        device_id = intent.getStringExtra("deviceId");
        PollService();
        mIntent = intent;
        return super.onStartCommand(intent, flags, startId);
    }



    public void PollService()
    {

        try {
            String URL = "http://172.16.100.28:9999/api/v1/device/pair";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                    cd = CompanyFileDetails.CompanyDetailsFromJSON(response);

                                    if(cd.getPairingStatus() == "5" && !mTimeOut) {
                                        db = new DatabaseHandler(getBaseContext());
                                        if (cd.getCompanyName() != null) {           //change logic once list of enum is available
                                            db.addCompanyFileDetails(cd);
                                            sendMessage("success");
                                        }

                                    }
                                else if(cd.getPairingStatus() == "4" && !mTimeOut)
                                    {
                                        sendMessage("deny");
                                    }
                                else if(!mTimeOut){
                                            PollService();
                                    }
                                    else {
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
                        NoInternetDialog(getBaseContext());

                    }
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("deviceId", "device1111");    //remove hardcode later
                    params.put("otp", otp );   // remove hardcode later

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


    public void NoInternetDialog(Context context)
    {
        final Dialog openDialog = new Dialog(context);
        openDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.connection_error_dialog);
        Button dialogCloseButton = (Button) openDialog.findViewById(R.id.NetowrkbtnOk);
        openDialog.setCancelable(false);
        dialogCloseButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {


                openDialog.dismiss();

            }

        });

        openDialog.show();
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
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        mTimeOut = true;
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
