package com.intuit.qbes.mobilescanner;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ckumar5 on 05/02/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 21;
    private static final String DATABASE_NAME = "MobileScanner.db";
    public static final String TABLE_PICKLISTINFO_NAME = "PickListInfo";
    public static final String TABLE_LINEITEMINFO_NAME = "LineItemInfo";

    //Table - 1
    private static final String KEY_ID = "id";
    private static final String KEY_COMPANYID = "companyId";
    private static final String KEY_TASKTYPE = "taskType";
    private static final String KEY_NAME = "name";
    private static final String KEY_ASSIGNEDID = "assigneeId";
    private static final String KEY_CREATEDBYID = "createdById";
    private static final String KEY_STATUS = "Status";
    private static final String KEY_SITEID = "siteId";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_SHOWNOTES = "showNotes";
    private static final String KEY_SYNCTOKEN = "syncToken";
    private static final String KEY_CREATEDTIMESTAMP = "createdTimestamp";
    private static final String KEY_MODIFIEDTIMESTAMP = "modifiedTimestamp";
    //private static final String[] COLUMNS = { KEY_ID, KEY_COMPANYID, KEY_STATUS,  KEY_ODATE, KEY_SDATE, KEY_CJOB};


//Table -2

    // "(id long, taskId long,extId long,itemName text,itemDesc text,lineitemPos long,docNum text,txnId long,txnDate text,shipDate text,notes text,status int, uom text,qtyToPick real, qtyPicked real,barcode text,binLocation text,binExtId long,customFields text,serialLotNumbers text)");

    private static final String KEY_TASKID = "taskId";
    private static final String KEY_EXTID = "extId";
    private static final String KEY_NAME_LINEITEM = "itemName";
    private static final String KEY_DESC = "itemDesc";
    private static final String KEY_LINEITEMPOS = "lineitemPos";
    private static final String KEY_DOCNUM = "docNum";
    private static final String KEY_TXNID = "txnId";
    private static final String KEY_TXNDATE = "txnDate";
    private static final String KEY_SHIPDATE = "shipDate";
    private static final String KEY_UOM = "uom";
    private static final String KEY_PICKED = "qtyPicked";
    private static final String KEY_TOPICK = "qtyToPick";
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_BINLOCATION = "binLocation";
    private static final String KEY_BINEXTID = "binExtId";
    private static final String KEY_CUSTOMFIELD = "customFields";
    private static final String KEY_SERIALOTNUMBBER = "serialLotNumbers";
    private static final String KEY_SERIANUMBER = "showSerialNo";
    private static final String KEY_LOTNUMBER = "showLotNo";


    //Context object
    private ContentResolver myCR = null;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myCR = context.getContentResolver();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table PickListInfo " +
                "(id long, companyId long,taskType long,name text,assigneeId long,createdById long,Status int,siteId long,notes text,showNotes text,syncToken long,createdTimestamp text, modifiedTimestamp text)");

        //need to change
        db.execSQL("create table LineItemInfo " +
                "(id long, taskId long,extId long,itemName text,itemDesc text,lineitemPos long,docNum text,txnId long,txnDate text,shipDate text,notes text,status int, uom text,qtyToPick real, qtyPicked real,barcode text,binLocation text,binExtId long,customFields text,serialLotNumbers text, showSerialNo boolean, showLotNo boolean)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
    }


    public void deleteOnePicklist(Picklist mPickList) {

        //String selection = "recnum = \" " + String.valueOf(mPickList.getRecnum()) + "\"";
        String selection = "id = ?";
        String [] selectionArgs = new String[] {String.valueOf(mPickList.getId())};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,selection,selectionArgs);

    }

    public List<Picklist> allPickLists() {

        List<Picklist> mPickLists = new LinkedList<Picklist>();
        try {
            String query = "SELECT  * FROM " + TABLE_PICKLISTINFO_NAME;
            Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE, null, query, null, null);
            Picklist mPickList = null;
            if (cursor.moveToFirst()) {
                do {
                    mPickList = new Picklist();
                    mPickList.setId((cursor.getLong(0)));
                    mPickList.setCompanyId((cursor.getLong(1)));
                    mPickList.setTaskType((cursor.getLong(2)));
                    mPickList.setName((cursor.getString(3)));
                    mPickList.setAssigneeId((cursor.getLong(4)));
                    mPickList.setCreatedById((cursor.getLong(5)));
                    mPickList.setStatus((cursor.getInt(6)));
                    mPickList.setSiteId((cursor.getLong(7)));
                    mPickList.setNotes((cursor.getString(8)));
                    mPickList.setShowNotes(Boolean.valueOf(cursor.getString(9)));
                    mPickList.setSyncToken((cursor.getLong(10)));
                    mPickList.setCreatedTimestamp(MSUtils.yyyyMMddFormat.parse((cursor.getString(11))));
                    mPickList.setModifiedTimestamp(MSUtils.yyyyMMddFormat.parse((cursor.getString(12))));
                    mPickLists.add(mPickList);
                } while (cursor.moveToNext());
            }
        }
        catch (ParseException exp)
        {
            exp.printStackTrace();
        }

        return mPickLists;
    }

    public void addPickList(Picklist mPickList) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, mPickList.getId());
            values.put(KEY_COMPANYID, mPickList.getCompanyId());
            values.put(KEY_TASKTYPE, mPickList.getTaskType());
            values.put(KEY_NAME, mPickList.getName());
            values.put(KEY_ASSIGNEDID, mPickList.getAssigneeId());
            values.put(KEY_CREATEDBYID, mPickList.getCreatedById());
            values.put(KEY_STATUS, mPickList.getStatus());
            values.put(KEY_SITEID, mPickList.getSiteId());
            values.put(KEY_NOTES, mPickList.getNotes());
            values.put(KEY_SHOWNOTES, String.valueOf(mPickList.isShowNotes()));
            values.put(KEY_SYNCTOKEN, mPickList.getSyncToken());
            values.put(KEY_CREATEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getCreatedTimestamp()));
            values.put(KEY_MODIFIEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getModifiedTimestamp()));
            myCR.insert(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE, values);
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }
    public void updatePickList(Picklist mPickList, long recnum) {
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_ID, mPickList.getId());
            values.put(KEY_COMPANYID, mPickList.getCompanyId());
            values.put(KEY_TASKTYPE, mPickList.getTaskType());
            values.put(KEY_NAME, mPickList.getName());
            values.put(KEY_ASSIGNEDID, mPickList.getAssigneeId());
            values.put(KEY_CREATEDBYID, mPickList.getCreatedById());
            values.put(KEY_STATUS, mPickList.getStatus());
            values.put(KEY_SITEID, mPickList.getSiteId());
            values.put(KEY_NOTES, mPickList.getNotes());
            values.put(KEY_SHOWNOTES, String.valueOf(mPickList.isShowNotes()));
            values.put(KEY_SYNCTOKEN, mPickList.getSyncToken());
            values.put(KEY_CREATEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getCreatedTimestamp()));
            values.put(KEY_MODIFIEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getModifiedTimestamp()));

            String whereClause = "id = " + String.valueOf(mPickList.getId());

            myCR.update(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE, values, whereClause, null);
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }
    public boolean PickListExists(long id)
    {
        String Query = "Select * from " + TABLE_PICKLISTINFO_NAME + " where " + "id" + " = " + id;
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,null,Query,null,null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }


    //Line item related methods
    public void deleteLineItems(long id) {
        String selection = "id = ?";
        String [] selectionArgs = new String[]{String.valueOf(id)};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE,selection,selectionArgs);

    }

    public List<LineItem> allLineItems(long id) {

        List<LineItem> mLineItems = new LinkedList<LineItem>();
        String query = "SELECT  * FROM " + TABLE_LINEITEMINFO_NAME + " where " + "taskId" + " = " + id;
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE,null,query,null,null);
        LineItem mLineItem = null;

        if (cursor.moveToFirst()) {
            do {
                try {
                    mLineItem = new LineItem();
                    mLineItem.setId(cursor.getLong(0));
                    mLineItem.setTaskId(cursor.getLong(1));
                    mLineItem.setExtId(cursor.getLong(2));
                    mLineItem.setItemName(cursor.getString(3));
                    mLineItem.setItemDesc(cursor.getString(4));
                    mLineItem.setLineitemPos(cursor.getLong(5));
                    mLineItem.setDocNum(cursor.getString(6));
                    mLineItem.setTxnId(cursor.getLong(7));
                    mLineItem.setTxnDate(MSUtils.yyyyMMddFormat.parse((cursor.getString(8))));
                    mLineItem.setTxnDate(MSUtils.yyyyMMddFormat.parse((cursor.getString(9))));
                    mLineItem.setNotes(cursor.getString(10));
                    mLineItem.setmItemStatus( LineItem.Status.valueOf(cursor.getString(11)));
                    mLineItem.setUom(cursor.getString(12));
                    mLineItem.setQtyToPick(cursor.getDouble(13));
                    mLineItem.setQtyPicked(cursor.getDouble(14));
                    mLineItem.setBarcode(cursor.getString(15));
                    mLineItem.setBinLocation(cursor.getString(16));
                    mLineItem.setBinExtId(cursor.getLong(17));
                    mLineItem.setCustomFields(cursor.getString(18));
                    String serialNumber = cursor.getString(19);
                    ArrayList<String> serialNumberArray = null;
                    if(serialNumber != null)
                        serialNumberArray = convertStringToArray(serialNumber);
                    mLineItem.setSerialLotNumbers(serialNumberArray);
                    mLineItem.setShowSerialNo(Boolean.parseBoolean(cursor.getString(20)));
                    mLineItem.setShowLotNo(Boolean.parseBoolean(cursor.getString(21)));
                    mLineItems.add(mLineItem);
                }
                catch (ParseException exp)
                {
                    exp.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        return mLineItems;
    }

    public static ArrayList <String> convertStringToArray(String str){
        String strSeparator = ",";
        ArrayList <String> arr = new ArrayList<>();
        String[] arr1 = str.split(strSeparator);
        for (int i = 0; i < arr1.length;i++)
        {
            arr.add(arr1[i]);
        }
        return arr;
    }

    public static String convertArrayToString(ArrayList <String> array){
        String str = "";
        String strSeparator = ",";
        for (int i = 0;i<array.size(); i++) {
            str = str+array.get(i);
            // Do not append comma at the end of last element
            if(i<array.size()-1){
                str = str+strSeparator;
            }
        }
        return str;
    }
    public void addLineItem(LineItem lineItem, long id) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, lineItem.getId());
            values.put(KEY_TASKID, lineItem.getTaskId());
            values.put(KEY_EXTID, lineItem.getExtId());
            values.put(KEY_NAME_LINEITEM, lineItem.getItemName());
            values.put(KEY_DESC, lineItem.getItemDesc());
            values.put(KEY_LINEITEMPOS, lineItem.getLineitemPos());
            values.put(KEY_DOCNUM, lineItem.getDocNum());
            values.put(KEY_TXNID, lineItem.getTxnId());
            values.put(KEY_TXNDATE, MSUtils.yyyyMMddFormat.format(lineItem.getTxnDate()));
            values.put(KEY_SHIPDATE, MSUtils.yyyyMMddFormat.format(lineItem.getShipDate()));
            values.put(KEY_NOTES, lineItem.getNotes());
            values.put(KEY_STATUS, String.valueOf(lineItem.getmItemStatus()));
            values.put(KEY_UOM, lineItem.getUom());
            values.put(KEY_TOPICK, lineItem.getQtyToPick());
            values.put(KEY_PICKED, lineItem.getQtyPicked());
            values.put(KEY_BARCODE, lineItem.getBarcode());
            values.put(KEY_BINLOCATION, lineItem.getBinLocation());
            values.put(KEY_BINEXTID, lineItem.getBinExtId());
            values.put(KEY_CUSTOMFIELD, lineItem.getCustomFields());
            String serialNumberList = null;
            if (lineItem.getSerialLotNumbers() != null)
                serialNumberList = convertArrayToString(lineItem.getSerialLotNumbers());
            values.put(KEY_SERIALOTNUMBBER, serialNumberList);
            values.put(KEY_SERIANUMBER,String.valueOf(lineItem.isShowSerialNo()));
            values.put(KEY_LOTNUMBER,String.valueOf(lineItem.isShowLotNo()));
            myCR.insert(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE, values);
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }


    public void updateLineItems(LineItem lineItem, long id) {
        try {
            ContentValues values = new ContentValues();

            values.put(KEY_ID, lineItem.getId());
            values.put(KEY_TASKID, lineItem.getTaskId());
            values.put(KEY_EXTID, lineItem.getExtId());
            values.put(KEY_NAME_LINEITEM, lineItem.getItemName());
            values.put(KEY_DESC, lineItem.getItemDesc());
            values.put(KEY_LINEITEMPOS, lineItem.getLineitemPos());
            values.put(KEY_DOCNUM, lineItem.getDocNum());
            values.put(KEY_TXNID, lineItem.getTxnId());
            values.put(KEY_TXNDATE, MSUtils.yyyyMMddFormat.format(lineItem.getTxnDate()));
            values.put(KEY_SHIPDATE, MSUtils.yyyyMMddFormat.format(lineItem.getShipDate()));
            values.put(KEY_NOTES, lineItem.getNotes());
            values.put(KEY_STATUS, String.valueOf(lineItem.getmItemStatus()));
            values.put(KEY_UOM, lineItem.getUom());
            values.put(KEY_TOPICK, lineItem.getQtyToPick());
            values.put(KEY_PICKED, lineItem.getQtyPicked());
            values.put(KEY_BARCODE, lineItem.getBarcode());
            values.put(KEY_BINLOCATION, lineItem.getBinLocation());
            values.put(KEY_BINEXTID, lineItem.getBinExtId());
            values.put(KEY_CUSTOMFIELD, lineItem.getCustomFields());
            String serialNumberList = null;
            if (lineItem.getSerialLotNumbers() != null)
                serialNumberList = convertArrayToString(lineItem.getSerialLotNumbers());
            values.put(KEY_SERIALOTNUMBBER, serialNumberList);
            values.put(KEY_SERIANUMBER,String.valueOf(lineItem.isShowSerialNo()));
            values.put(KEY_LOTNUMBER,String.valueOf(lineItem.isShowLotNo()));
            String whereClause = "id = " + String.valueOf(lineItem.getId());
            myCR.update(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE, values, whereClause, null);
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }

    }
}
