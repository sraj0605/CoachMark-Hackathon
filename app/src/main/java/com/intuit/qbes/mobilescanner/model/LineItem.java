package com.intuit.qbes.mobilescanner.model;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pdixit on 10/3/16.
 */
public class LineItem implements Parcelable {

    private static final String LOG_TAG = "LineItem";
    private static final String JSON_TAG_RECNUM = "_id";
    private static final String JSON_TAG_NAME = "name";
    private static final String JSON_TAG_DESC = "desc";
    private static final String JSON_TAG_UOM = "uom";
    private static final String JSON_TAG_NEEDED = "Needed";
    private static final String JSON_TAG_PICKED = "picked";
    private static final String JSON_TAG_TOPICK = "to_pick";
    private static final String JSON_TAG_BARCODE = "barcode";
    private static final String TAG_ID = "picklist_id";

    private long mRecNum;
    private long mPicklistID;
    private String mName;
    private String mDescription;
    private String mBarcode;

    private String mUom;
    private double mQtyNeeded;
    private double mQtyToPick;
    private double mQtyPicked;

    private String mSite;
    private String mBin;

    private String[] mSNArr;
    public LineItem()
    {
        mRecNum = -1;
    }
    public LineItem(long recNum,
                    String name,
                    String description,
                    String barcode,
                    String uom,
                    double qtyNeeded,
                    double qtyPicked,
                    double qtyToPick,
                    String site,
                    String bin,
                    ArrayList<String> SNArr)
    {
        mBarcode = barcode;
        mBin = bin;
        mDescription = description;
        mName = name;
        mQtyNeeded = qtyNeeded;
        mQtyPicked = qtyPicked;
        mQtyToPick = qtyToPick;
        mRecNum = recNum;
        mSite = site;

        if (SNArr != null) {
            mSNArr = (String[]) SNArr.toArray();
        }
        else
        {
            mSNArr = null;
        }
        mUom = uom;
    }

    public LineItem(String jsonStr)
    {
        try
        {
            JSONObject jsonItem = new JSONObject(jsonStr);
            mRecNum = jsonItem.getLong(JSON_TAG_RECNUM);
            mName = jsonItem.getString(JSON_TAG_NAME);
            mDescription = jsonItem.getString(JSON_TAG_DESC);
            mBarcode = jsonItem.getString(JSON_TAG_BARCODE);
            mUom = jsonItem.getString(JSON_TAG_UOM);
            mQtyNeeded = jsonItem.getDouble(JSON_TAG_NEEDED);
            mQtyToPick = jsonItem.getDouble(JSON_TAG_TOPICK);
            mQtyPicked = jsonItem.getDouble(JSON_TAG_PICKED);
        }
        catch (Exception ex)
        {
            Log.e(LOG_TAG, ex.toString());
        }
    }


    public LineItem(Parcel in)
    {
        mPicklistID = in.readLong();
        mRecNum = in.readLong();
        mName = in.readString();
        mDescription = in.readString();
        mBarcode = in.readString();
        mUom = in.readString();
        mQtyNeeded = in.readDouble();
        mQtyToPick = in.readDouble();
        mQtyPicked = in.readDouble();
        mSite = in.readString();
        mBin = in.readString();
        //in.readStringArray(mSNArr);
    }

    public String getBarcode() {
        return mBarcode;
    }

    public String getBin() {
        return mBin;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getName() {
        return mName;
    }

    public double getQtyNeeded() {
        return mQtyNeeded;
    }

    public double getQtyPicked() {
        return mQtyPicked;
    }

    public double getQtyToPick() {
        return mQtyToPick;
    }

    public long getRecNum() {
        return mRecNum;
    }

    public String getSite() {
        return mSite;
    }

    public String[] getSNArr() {
        return mSNArr;
    }

    public String getUom() {
        return mUom;
    }

    public long getPickListID() {
        return mPicklistID;
    }


    public void setBarcode(String barcode) {
        mBarcode = barcode;
    }
    public void setDescription(String decsription) {
        mDescription = decsription;
    }
    public void setName(String name) {
        mName = name;
    }
    public void setQtyNeeded(Double qtyNeeded) {
        mQtyNeeded = qtyNeeded;
    }

    public void setQtyToPick(Double qtyToPick) {
        mQtyToPick = qtyToPick;
    }
    public void setRecNum(long recnum) {
        mRecNum = recnum;
    }
    public void setUom(String uom) {
        mUom = uom;
    }

    public void setPickListID(long id) { mPicklistID = id ;}


    public void setQtyPicked(double qtyPicked) {
        mQtyPicked = qtyPicked;
    }

    public static List<LineItem> lineItemsFromJSON(JSONArray jsonLineItems)
    {
        List<LineItem> lineItems = new ArrayList<LineItem>();
        try {
            for (int i = 0; i < jsonLineItems.length(); i++)
            {
                JSONObject jsonItem = jsonLineItems.getJSONObject(i);
                lineItems.add(new LineItem( jsonItem.getLong(JSON_TAG_RECNUM),
                        jsonItem.getString(JSON_TAG_NAME),
                        jsonItem.optString(JSON_TAG_DESC),
                        jsonItem.getString(JSON_TAG_BARCODE),
                        jsonItem.optString(JSON_TAG_UOM),
                        jsonItem.optDouble(JSON_TAG_NEEDED, 0.0),
                        jsonItem.optDouble(JSON_TAG_PICKED, 0.0),
                        jsonItem.getDouble(JSON_TAG_TOPICK),
                        null, null, null));
            }
        }
        catch (JSONException ex)
        {
            Log.e(LOG_TAG, ex.toString());
        }

        return lineItems;

    }

    public static List<LineItem> lineItemsFromJSONStr(String jsonStr)
    {
        JSONArray lineItems = null;
        try
        {
            lineItems = new JSONArray(jsonStr);
        }
        catch (JSONException ex)
        {
            Log.e(LOG_TAG, ex.toString());
        }

        return lineItemsFromJSON(lineItems);
    }

    public JSONObject toJSON() throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(JSON_TAG_RECNUM, getRecNum());
        json.put(JSON_TAG_NAME, getName());
        json.put(JSON_TAG_DESC, getDescription());
        json.put(JSON_TAG_BARCODE, getBarcode());
        json.put(JSON_TAG_UOM, getUom());
        json.put(JSON_TAG_NEEDED, getQtyNeeded());
        json.put(JSON_TAG_PICKED, getQtyPicked());
        json.put(JSON_TAG_TOPICK, getQtyToPick());

        return json;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getPickListID());
        dest.writeLong(getRecNum());
        dest.writeString(getName());
        dest.writeString(getDescription());
        dest.writeString(getBarcode());
        dest.writeString(getUom());
        dest.writeDouble(getQtyNeeded());
        dest.writeDouble(getQtyToPick());
        dest.writeDouble(getQtyPicked());
        dest.writeString(getSite());
        dest.writeString(getBin());
        //dest.writeStringArray(mSNArr);
    }

    public static final Parcelable.Creator<LineItem> CREATOR = new Parcelable.Creator<LineItem>() {
        @Override
        public LineItem createFromParcel(Parcel source) {
            return new LineItem(source);
        }

        @Override
        public LineItem[] newArray(int size) {
            return new LineItem[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        boolean ret = false;

        if (o instanceof LineItem)
        {
            LineItem rhs = (LineItem) o;
            ret = rhs.mRecNum == this.mRecNum;
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int)this.mRecNum;
        return hash;
    }
}
