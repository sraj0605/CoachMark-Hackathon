package com.intuit.qbes.mobilescanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by ashah9 on 2/28/17.
 */

public class DeviceDetails {

    private String deviceId;
    private String deviceName;
    private String otp;

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {return deviceName;}

    public String getOTP() {
        return otp;
    }

    public void setDeviceID(String deviceID) {this.deviceId= deviceID;}

    public void setDeviceName(String deviceName) {this.deviceName = deviceName;}

    public void setOTP(String otp) {this.otp = otp;}


    public DeviceDetails()
    {
        this.otp = "";
    }

    public DeviceDetails(String otp,
                         String deviceID,
                         String deviceName)
    {
        this.otp = otp;
        this.deviceId = deviceID;
        this.deviceName = deviceName;
    }


    public static String JSONStringFromDeviceDetails(DeviceDetails details)
    {
        GsonBuilder builder = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd");
        Gson gson = builder.create();
        String jsonString = gson.toJson(details);

        return jsonString;
    }


}
