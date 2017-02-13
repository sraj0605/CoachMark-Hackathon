package com.intuit.qbes.mobilescanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.text.SimpleDateFormat;

/**
 * Created by pdixit on 10/3/16.
 */
public class MSUtils {

    public static SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat MMddyyyyFormat = new SimpleDateFormat("MM/dd/yyyy");

    public static String getServerUrl(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String serverPref = sharedPref.getString("pref_server", "sbg-qbes-pps.azurewebsites.net");
        String portPref = sharedPref.getString("pref_port", "");

        if (TextUtils.isEmpty(serverPref))
        {
            serverPref = "sbg-qbes-pps.azurewebsites.net";
        }

        return String.format("http://%s:%s", serverPref, portPref);
    }
}
