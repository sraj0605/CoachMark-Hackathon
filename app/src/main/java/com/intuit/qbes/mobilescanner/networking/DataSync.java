package com.intuit.qbes.mobilescanner.networking;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.intuit.qbes.mobilescanner.DatabaseHandler;
import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashah9 on 2/10/17.
 */

public class DataSync {

    private static final String updateTAG = "Update";
    private String fetchTAG  = "Fetch";
    private List<Picklist> mPicklists = new ArrayList<>();
    private ProgressDialog mProgressDialog;



    public void UpdatePicklist(Picklist mPicklist, Context context)
    {
        init_dialog(context);

        try {
            showDialog();
            String URL = "http://172.16.100.28:9999/api/v1/company/666667/tasks/35?merge=true";
         //   String URL = "https://posttestserver.com/post.php";
            final String picklistJSONStr = Picklist.JSONStringFromPicklist(mPicklist);

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    hideDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    hideDialog();
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

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
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

    public void FetchPicklists(final Context context)
    {
        init_dialog(context);

        try {
            showDialog();
            String URL = "http://172.16.100.28:9999/api/v1/company/666667/tasks/35";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            try {
                                mPicklists.add(Picklist.picklistFromJSON(response));
                                Picklist.StorePickList(mPicklists, context);

                            } catch (Exception e) {

                                Log.d("Error:", e.getMessage());

                            }
                            hideDialog();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Response", "Error: " + error.getMessage());
                    Toast.makeText(context,
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                    hideDialog();
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

    private void init_dialog(Context context)
    {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Updating Information...");
        mProgressDialog.setCancelable(false);
    }

    private void showDialog() {
        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

}
