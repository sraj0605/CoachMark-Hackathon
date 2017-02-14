package com.intuit.qbes.mobilescanner.model;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.intuit.qbes.mobilescanner.MSUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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

    private long id;
    private long taskId;
    private  long extId;
    private String itemName;
    private String itemDesc;
    private long lineitemPos;
    private String docNum;
    private long txnId;
    private Date txnDate;
    private Date shipDate;
    private String notes;
    public enum Status
    {
        @SerializedName("0")
        NOTAVAILABLE,
        @SerializedName("1")
        NOTPICKED,
        @SerializedName("2")
        PARTIALPICKED,
        @SerializedName("3")
        PICKED

    }
    private String uom;
    private double qtyToPick;
    private double qtyPicked;
    private  String barcode;
    private String binLocation;
    private  long binExtId;
    private  String customFields;
    private ArrayList<String> serialLotNumbers = null;
    private boolean deleted;
    private boolean showSerialNo;
    private  boolean showLotNo;
    @SerializedName("status")
    private Status mItemStatus;
    private transient String barcodeEntered;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getExtId() {
        return extId;
    }

    public void setExtId(long extId) {
        this.extId = extId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public long getLineitemPos() {
        return lineitemPos;
    }

    public void setLineitemPos(long lineitemPos) {
        this.lineitemPos = lineitemPos;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public long getTxnId() {
        return txnId;
    }

    public void setTxnId(long txnId) {
        this.txnId = txnId;
    }

    public Date getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(Date txnDate) {
        this.txnDate = txnDate;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public double getQtyToPick() {
        return qtyToPick;
    }

    public void setQtyToPick(double qtyToPick) {
        this.qtyToPick = qtyToPick;
    }

    public double getQtyPicked() {
        return qtyPicked;
    }

    public void setQtyPicked(double qtyPicked) {
        this.qtyPicked = qtyPicked;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBinLocation() {
        return binLocation;
    }

    public void setBinLocation(String binLocation) {
        this.binLocation = binLocation;
    }

    public long getBinExtId() {
        return binExtId;
    }

    public void setBinExtId(long binExtId) {
        this.binExtId = binExtId;
    }

    public String getCustomFields() {
        return customFields;
    }

    public void setCustomFields(String customFields) {
        this.customFields = customFields;
    }

    public ArrayList<String> getSerialLotNumbers() {
        return serialLotNumbers;
    }

    public void setSerialLotNumbers(ArrayList<String> serialLotNumbers) {
        this.serialLotNumbers = serialLotNumbers;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isShowSerialNo() {
        return showSerialNo;
    }

    public void setShowSerialNo(boolean showSerialNo) {
        this.showSerialNo = showSerialNo;
    }

    public boolean isShowLotNo() {
        return showLotNo;
    }

    public void setShowLotNo(boolean showLotNo) {
        this.showLotNo = showLotNo;
    }

    public Status getmItemStatus() {
        return mItemStatus;
    }

    public void setmItemStatus(Status mItemStatus) {
        this.mItemStatus = mItemStatus;
    }
    public String getBarcodeEntered() {
        return barcodeEntered;
    }

    public void setBarcodeEntered(String barcodeEntered) {
        this.barcodeEntered = barcodeEntered;
    }



    public LineItem()
    {
        this.id = -1;
    }
    public LineItem(long id,String barcode)
    {
        this.id = -1;
        this.barcode = barcode;

    }
    public LineItem(long id,
                    long taskId,
                    long extId,
                    String itemName,
                    String itemDesc,
                    long lineitemPos,
                    String docNum,
                    long txnId,
                    String txnDate,
                    String shipDate,
                    String notes,
                    String uom,
                    double qtyToPick,
                    double qtyPicked,
                    String barcode,
                    String binLocation,
                    long binExtId,
                    String customFields,
                    ArrayList<String> serialLotNumbers,
                    String deleted,
                    String showSerialNo,
                    String showLotNo,
                    Status mItemStatus

    )
    {
        try {
            this.id = id;
            this.taskId = taskId;
            this.extId = extId;
            this.itemName = itemName;
            this.itemDesc = itemDesc;
            this.lineitemPos = lineitemPos;
            this.docNum = docNum;
            this.txnId = txnId;
            this.txnDate = MSUtils.yyyyMMddFormat.parse(txnDate);
            this.shipDate = MSUtils.yyyyMMddFormat.parse(shipDate);
            this.notes = notes;
            this.uom = uom;
            this.qtyToPick = qtyToPick;
            this.qtyPicked = qtyPicked;
            this.barcode = barcode;
            this.binLocation = binLocation;
            this.binExtId = binExtId;
            this.customFields = customFields;
            this.serialLotNumbers = serialLotNumbers;
            this.deleted = Boolean.valueOf(deleted);
            this.showSerialNo = Boolean.valueOf(showSerialNo);
            this.showLotNo = Boolean.valueOf(showLotNo);
            this.mItemStatus = mItemStatus;
            this.barcodeEntered = "";
        }
        catch (ParseException exp)
        {
            exp.printStackTrace();
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }


    public LineItem(Parcel in) {
        try
        {
            id = in.readLong();
            taskId = in.readLong();
            extId = in.readLong();
            itemName = in.readString();
            itemDesc = in.readString();
            lineitemPos = in.readLong();
            docNum = in.readString();
            txnId = in.readLong();
            txnDate = (java.util.Date) in.readSerializable();
            shipDate = (java.util.Date) in.readSerializable();
            notes = in.readString();
            uom = in.readString();
            qtyToPick = in.readDouble();
            qtyPicked = in.readDouble();
            barcode = in.readString();
            binLocation = in.readString();
            binExtId = in.readLong();
            customFields = in.readString();
            serialLotNumbers = (ArrayList<String>) in.readSerializable();
            deleted = Boolean.valueOf(in.readString());
            showSerialNo = Boolean.valueOf(in.readString());
            showLotNo = Boolean.valueOf(in.readString());
            mItemStatus = Status.valueOf(in.readString());
            barcodeEntered = in.readString();
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }
    }

    public static LineItem  lineItemFromJSON(String jsonLineItems)
    {
        try {
            GsonBuilder builder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd");
            Gson gson = builder.create();
            LineItem objList = gson.fromJson(jsonLineItems, LineItem.class);
            return objList;
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }

        return  null;
    }

    public static List<LineItem> lineItemsFromJSON(String jsonLineItems)
    {
        List<LineItem> lineItems = new ArrayList<LineItem>();
        try {

            GsonBuilder builder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd");;
            Gson gson = builder.create();
            LineItem[] objList = gson.fromJson(jsonLineItems, LineItem[].class);
            for (int i = 0; i < objList.length; i++) {
                lineItems.add(objList[i]);
            }

        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
        return lineItems;
    }
    public String JSONStringFromLineitem(LineItem lineItem)
    {
        GsonBuilder builder = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd");
        Gson gson = builder.create();
        String jsonString = gson.toJson(lineItem);

        return jsonString;
    }

    public String JSONStringArrayFromPicklistArray(List<LineItem> lineItems)
    {
        GsonBuilder builder = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd");
        Gson gson = builder.create();
        String jsonString = gson.toJson(lineItems);

        return jsonString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeLong(getId());
            dest.writeLong(getTaskId());
            dest.writeLong(getExtId());
            dest.writeString(getItemName());
            dest.writeString(getItemDesc());
            dest.writeLong(getLineitemPos());
            dest.writeString(getDocNum());
            dest.writeLong(getTxnId());
            dest.writeSerializable(getTxnDate());
            dest.writeSerializable(getShipDate());
            dest.writeString(getNotes());
            dest.writeString(getUom());
            dest.writeDouble(getQtyToPick());
            dest.writeDouble(getQtyPicked());
            dest.writeString(getBarcode());
            dest.writeString(getBinLocation());
            dest.writeLong(getExtId());
            dest.writeString(getCustomFields());
            dest.writeSerializable(getSerialLotNumbers());
            dest.writeString(String.valueOf(isDeleted()));
            dest.writeString(String.valueOf(isShowSerialNo()));
            dest.writeString(String.valueOf(isShowLotNo()));
            dest.writeString((mItemStatus == null) ? "" : mItemStatus.name());
            dest.writeString(String.valueOf(getBarcodeEntered()));
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }


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
        int test = 0;

        if (o instanceof LineItem)
        {
            LineItem rhs = (LineItem) o;
            //ret = rhs.mRecNum == this.mRecNum;
            if(this.getId() == -1) { //object of dummy barcode which will have recnum as -1
                test = (rhs.getBarcode().toLowerCase().compareTo(this.getBarcode().toLowerCase()));
                if (test == 0)
                    ret = true;
            }
            else
            {
                //barcode and location can be null from qb
                //TO D0 - next we will add target id
                ret = (rhs.extId == this.extId);

            }
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int)this.id;
        return hash;
    }
}
