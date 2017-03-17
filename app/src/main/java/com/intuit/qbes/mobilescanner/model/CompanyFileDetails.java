package com.intuit.qbes.mobilescanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by ashah9 on 2/28/17.
 */

public class CompanyFileDetails {

    private String companyId;
    private String extDeviceId;
    private String companyName;
    private String pairingStatus;

    public String getRealmID() {
        return companyId;
    }

    public  String getDeviceGUID() {return extDeviceId;}

    public String getCompanyName() {
        return companyName;
    }

    public String getPairingStatus() { return pairingStatus; }

    public void setRealmID(String realmID) {this.companyId = realmID;}

    public void setDeviceGUID(String deviceGUID) {this.extDeviceId = deviceGUID;}

    public void setCompanyName(String companyName) {this.companyName = companyName;}

    public void setPairingStatus(String pairingStatus) {this.pairingStatus = pairingStatus;}


    public CompanyFileDetails()
    {
        this.companyId = "-1";
    }

    public CompanyFileDetails(String realmID,
                         String deviceGUID,
                         String companyName,
                         String pairingStatus)
    {
        this.companyId = realmID;
        this.extDeviceId = deviceGUID;
        this.companyName = companyName;
        this.pairingStatus = pairingStatus;
    }


    public static CompanyFileDetails CompanyDetailsFromJSON(String cdJSONstr)
    {
        GsonBuilder builder = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd");
        Gson gson = builder.create();
        CompanyFileDetails objList = gson.fromJson(cdJSONstr, CompanyFileDetails.class);
        return objList;
    }


}
