package com.intuit.qbes.mobilescanner.networking;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.intuit.qbes.mobilescanner.DatabaseHandler;
import com.intuit.qbes.mobilescanner.ListPicklistFragment;
import com.intuit.qbes.mobilescanner.R;
import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;

/**
 * Created by ashah9 on 2/10/17.
 */

public class DataSync {

    private static final String updateTAG = "Update";
    private String fetchTAG  = "Fetch";
    private List<Picklist> mPicklists = new ArrayList<>();
    private DataSyncCallback mCallback;



    public interface DataSyncCallback {
        void onFetchPicklist(List<Picklist> mPicklists);
    }


    public void UpdatePicklist(Picklist mPicklist, final Context context)
    {

        try {
            String URL = "http://172.16.100.28:9999/api/v1/company/666667/tasks/98";
            final String picklistJSONStr = Picklist.JSONStringFromPicklist(mPicklist);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());

                    if (error instanceof NoConnectionError) {
                        //No Internet Error
                        NoInternetDialog(context);

                    }
                    if(error.networkResponse != null)
                    {
                        if (error.networkResponse.statusCode == 409) {
                            StaleDataDialog(context);

                        }
                    }
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return picklistJSONStr == null ? null : picklistJSONStr.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", picklistJSONStr, "utf-8");
                        return null;
                    }
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    0,  // maxNumRetries = 0 means no retry
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            stringRequest.setTag(updateTAG);
            AppController.getInstance().addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void FetchPicklists(final Context context, final DataSyncCallback callback)
    {

        try {
            String URL = "http://172.16.100.28:9999/api/v1/company/666667/tasks/98";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                mPicklists.add(Picklist.picklistFromJSON(response));
                                mCallback = callback;
                                mCallback.onFetchPicklist(mPicklists);

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
                        NoInternetDialog(context);

                    }
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    0,  // maxNumRetries = 0 means no retry
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            stringRequest.setTag(fetchTAG);
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
        dialogCloseButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {


                openDialog.dismiss();

            }

        });

        openDialog.show();
    }

    public void StaleDataDialog(Context context)
    {
        final Dialog openDialog = new Dialog(context);
        openDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.staledata_dialog);
        Button dialogCloseButton = (Button) openDialog.findViewById(R.id.StalebtnOk);
        dialogCloseButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {


                openDialog.dismiss();

            }

        });

        openDialog.show();
    }

    public List<Picklist> getTasksSynchronously(int method, String url) {
        List<Picklist> picklists = null;
        String response = null;
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(method, url,future,future);
        AppController.getInstance().addToRequestQueue(request);

        try {
            response = future.get(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if(response != null) {
                Log.e("SyncAdapter", response);
                picklists = Picklist.picklistsFromJSON(response);
            }
        } catch (InterruptedException e) {
            Log.e("Retrieve  interrupted.", e.getMessage());
        } catch (ExecutionException e) {
            Log.e("Retrieve  failed.", e.getMessage());

        } catch (TimeoutException e) {
            Log.e("Retrieve  timed out.", e.getMessage());

        }
        return  picklists;
    }
}
