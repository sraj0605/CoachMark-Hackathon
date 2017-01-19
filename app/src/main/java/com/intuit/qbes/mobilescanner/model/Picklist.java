package com.intuit.qbes.mobilescanner.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.intuit.qbes.mobilescanner.MSUtils;
import com.intuit.qbes.mobilescanner.SQLiteDatabaseHandler;
import com.intuit.qbes.mobilescanner.SQLiteDatabaseLineItemHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by pdixit on 10/3/16.
 */
public class Picklist implements Parcelable {

    private static final String LOG_TAG = "Picklist";
    private static final String JSON_TAG_RECNUM = "_id";
    private static final String JSON_TAG_NUMBER = "num";
    private static final String JSON_TAG_STATUS = "status";
    private static final String JSON_TAG_DATE = "date";
    private static final String JSON_TAG_SHIPDATE = "shipdate";
    private static final String JSON_TAG_CUSTJOB = "customerjob";
    private static final String JSON_TAG_ITEMARR = "items";
    private static SQLiteDatabaseHandler db;
    private static SQLiteDatabaseLineItemHandler dbLineItem;

    private long mRecnum;
    private String mNumber;
    private int mStatus; //change to enum later
    private Date mOrderDate;
    private Date mShipDate;
    private String mName;
    private String mNote;
    private int mTotalItems;
    private List<LineItem> mLines;
    public Picklist() {
        mRecnum = -1;
        mLines = new ArrayList<LineItem>();
        mStatus = 0;
    }

    public Picklist(List<LineItem> lines,
                    long recnum,
                    String name,
                    String number,
                    String orderDate,
                    String shipDate,
                    int status) {
        try {
            mLines = lines;
            mName = name;
            mNumber = number;
            mOrderDate = MSUtils.yyyyMMddFormat.parse(orderDate);
            mRecnum = recnum;
            mShipDate = MSUtils.yyyyMMddFormat.parse(shipDate);
            mStatus = status;
        }
        catch (ParseException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    public Picklist(Parcel in)
    {
        mRecnum = in.readLong();
        mNumber = in.readString();
        mStatus = in.readInt();
        try {
            mOrderDate = MSUtils.yyyyMMddFormat.parse(in.readString());
            mShipDate = MSUtils.yyyyMMddFormat.parse(in.readString());
        }
        catch (ParseException e) {
            Log.e(LOG_TAG, e.toString());
        }
        mName = in.readString();
        //chandan - commenting as of now,to find why it is taking time
        //mLines = new ArrayList<LineItem>(Arrays.asList(in.createTypedArray(LineItem.CREATOR)));
    }

    public List<LineItem> getLines() {
        return mLines;
    }

    public String getName() {
        return mName;
    }

    public String getNumber() {
        return mNumber;
    }

    public Date getOrderDate(){

            return mOrderDate;

    }

    public long getRecnum() {
        return mRecnum;
    }

    public Date getShipDate() {
        return mShipDate;
    }

    public String getNote() { return mNote ;}

    public int getTotalItems() { return mTotalItems ;}

    public int getStatus() {
        return mStatus;
    }



    public void setName(String name) {
        this.mName = name;
    }

    public void setNumber(String number) {
        this.mNumber = number;
    }

    public void setOrderDate(String OrderDate) {
       try {
            this.mOrderDate = MSUtils.yyyyMMddFormat.parse(OrderDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setRecnum(long recnum) {
        this.mRecnum = recnum;
    }

    public void setShipDate(String ShipDate) {
        try {
            this.mShipDate = MSUtils.yyyyMMddFormat.parse(ShipDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
   public void setNote(String note)
   {
       this.mNote = note;
   }

   public void setTotalItems(int item_total)
   {
       this.mTotalItems = item_total;
   }

    public void setStatus(int status)
    {
        mStatus = status;
    }

    public static List<Picklist> picklistsFromJSON(String plJsonStr, Context context )
    {
        List<Picklist> plArr = new ArrayList<Picklist>();
        try
        {
            JSONArray plJsonArr = new JSONArray(plJsonStr);
            for (int i = 0; i < plJsonArr.length(); i++)
            {
                JSONObject jsonPl = plJsonArr.getJSONObject(i);
                plArr.add(new Picklist(LineItem.lineItemsFromJSON(jsonPl.getJSONArray(JSON_TAG_ITEMARR)),
                        jsonPl.getLong(JSON_TAG_RECNUM),
                        jsonPl.getString(JSON_TAG_CUSTJOB),
                        jsonPl.getString(JSON_TAG_NUMBER),
                        jsonPl.getString(JSON_TAG_DATE),
                        jsonPl.getString(JSON_TAG_SHIPDATE),
                        jsonPl.optInt(JSON_TAG_STATUS)));
            }
        }
        catch (JSONException ex)
        {
            Log.e(LOG_TAG, ex.toString());
        }
        return plArr;
    }



    public JSONObject toJSON() throws JSONException
    {
        JSONObject picklistJSON = new JSONObject();

        picklistJSON.put(JSON_TAG_RECNUM, getRecnum());
        picklistJSON.put(JSON_TAG_CUSTJOB, getName());
        picklistJSON.put(JSON_TAG_NUMBER, getNumber());
        picklistJSON.put(JSON_TAG_DATE, MSUtils.yyyyMMddFormat.format(getOrderDate()));
        picklistJSON.put(JSON_TAG_SHIPDATE, MSUtils.yyyyMMddFormat.format(getShipDate()));
        picklistJSON.put(JSON_TAG_STATUS, getStatus());

        JSONArray lineItemsArray = new JSONArray();
        for (LineItem line : mLines)
        {
            lineItemsArray.put(line.toJSON());
        }

        picklistJSON.put(JSON_TAG_ITEMARR, lineItemsArray);

        return picklistJSON;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeInt(getTotalItems());
            String test = getNote();
            Date test2;
            test2 = getOrderDate();
            Long lg = getRecnum();

            dest.writeString(getNote());
            dest.writeLong(getRecnum());
            dest.writeString(getNumber());
            dest.writeInt(getStatus());
            dest.writeString(MSUtils.yyyyMMddFormat.format(getOrderDate()));
            dest.writeString(MSUtils.yyyyMMddFormat.format(getShipDate()));

        }
        catch (Exception exp)
        {

        }
      //  dest.writeTypedArray(getLines().toArray(new LineItem[getLines().size()]), 0);
    }

    public static final Parcelable.Creator<Picklist> CREATOR = new Parcelable.Creator<Picklist>() {
        @Override
        public Picklist createFromParcel(Parcel source) {
            return new Picklist(source);
        }

        @Override
        public Picklist[] newArray(int size) {
            return new Picklist[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        boolean ret = false;

        if (o instanceof Picklist)
        {
            Picklist rhs = (Picklist) o;
            ret = rhs.mRecnum == this.mRecnum;
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int)this.mRecnum;
        return hash;
    }

    public static void StorePickList(List<Picklist> picklists, Context context)
    {
        db = new SQLiteDatabaseHandler(context);
        for(int i =0 ; i<picklists.size() ; i++)
        {
            if(db.PickListExists(picklists.get(i).getRecnum())) {
                db.updatePickList(picklists.get(i), picklists.get(i).getRecnum());
                UpdateLineItems(picklists.get(i).getLines(),picklists.get(i).getRecnum(),context);
            }
            else
            {
                db.addPickList(picklists.get(i));
                StoreLineItem(picklists.get(i).getLines(), picklists.get(i).getRecnum(), context);
            }
        }
    }

    public static void StoreLineItem(List<LineItem> lineitems, long id, Context context)
    {
        dbLineItem = new SQLiteDatabaseLineItemHandler(context);
        for(int i =0 ; i<lineitems.size() ; i++)
        {
                dbLineItem.addLineItem(lineitems.get(i), id);
        }

    }

    public static void UpdateLineItems(List<LineItem> lineitems, long id,  Context context)
    {
        dbLineItem = new SQLiteDatabaseLineItemHandler(context);
        for(int i =0 ; i<lineitems.size() ; i++)
        {
            dbLineItem.updateLineItems(lineitems.get(i), id);
        }
    }

    public void DeletePicklists(List<Picklist> picklists, Context context)
    {
        db = new SQLiteDatabaseHandler(context);
        for(int i=0; i<picklists.size();i++)
        {
            DeleteLineItems(picklists.get(i).getRecnum(),context);
            db.deleteOnePicklist(picklists.get(i));
        }
    }

    public void DeleteLineItems(long recnum, Context context)
    {
        dbLineItem = new SQLiteDatabaseLineItemHandler(context);
        dbLineItem.deleteLineItems(recnum);
    }

}
