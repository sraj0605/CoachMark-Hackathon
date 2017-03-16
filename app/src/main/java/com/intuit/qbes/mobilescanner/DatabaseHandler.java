package com.intuit.qbes.mobilescanner;



import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.intuit.qbes.mobilescanner.model.CompanyFileDetails;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import com.intuit.qbes.mobilescanner.TableDetails;
import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;
import com.intuit.qbes.mobilescanner.model.Status;
import com.symbol.emdk.barcode.ScannerConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;


/**
 * Created by ckumar5 on 05/02/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 21;
    private static final String DATABASE_NAME = "MobileScanner.db";
    public static final String TABLE_PICKLISTINFO_NAME = "PickListInfo";
    public static final String TABLE_LINEITEMINFO_NAME = "LineItemInfo";
    public static final String TABLE_SERIALNUBERINFO = "SerialNumberInfo";
    public static final String TABLE_SYNCINFO= "SyncInfo";
    public static final String TABLE_COMPANYYFILEINFO_NAME = "CompanyFileInfo";


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
    private static final String KEY_SERIANUMBER = "showSerialNo";
    private static final String KEY_LOTNUMBER = "showLotNo";
    //Table -3
    private static final String KEY_LINEITEMID = "lineitemId";
    private static final String KEY_TYPE = "type";
    private static final String KEY_VALUE = "value";

    //Table-4
    private static final String KEY_GUID = "guid";
    private static final String KEY_LASTSYNCTIME = "lastSyncTime";


//Table -5

    private static final String KEY_REALMID = "companyId";
    private static final String KEY_DEVICEGUID = "extDeviceId";
    private static final String KEY_COMPANYNAME = "companyName";


    //Context object
    private ContentResolver myCR = null;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myCR = context.getContentResolver();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL("CREATE TABLE " + TableDetails.Tables.PICKLIST + " ("
                    + TableDetails.KEY_ID + " LONG PRIMARY KEY,"
                    + TableDetails.PickListInfo.KEY_COMPANYID + " LONG NOT NULL,"
                    + TableDetails.PickListInfo.KEY_TASKTYPE + " LONG NOT NULL,"
                    + TableDetails.PickListInfo.KEY_NAME + " TEXT NOT NULL,"
                    + TableDetails.PickListInfo.KEY_ASSIGNEDID + " LONG,"
                    + TableDetails.PickListInfo.KEY_CREATEDBYID + " LONG,"
                    + TableDetails.PickListInfo.KEY_STATUS + " INT NOT NULL,"
                    + TableDetails.PickListInfo.KEY_SITEID + " INT,"
                    + TableDetails.PickListInfo.KEY_NOTES + " TEXT,"
                    + TableDetails.PickListInfo.KEY_SHOWNOTES + " TEXT,"
                    + TableDetails.PickListInfo.KEY_SYNCTOKEN + " LONG,"
                    + TableDetails.PickListInfo.KEY_MODIFIEDTIMESTAMP + " TEXT "
                    + ")");
        }
        catch (SQLException exp)
        {
            exp.printStackTrace();
            Log.i("DatabaseHandler",exp.getMessage().toString());
        }
        try {
            db.execSQL("CREATE TABLE " + TableDetails.Tables.LINEITEM + " ("
                    + TableDetails.KEY_ID + " LONG PRIMARY KEY,"
                    + TableDetails.LineItemInfo.KEY_TASKID + " LONG NOT NULL,"
                    + TableDetails.LineItemInfo.KEY_EXTID + " LONG NOT NULL,"
                    + TableDetails.LineItemInfo.KEY_NAME_LINEITEM + " TEXT NOT NULL,"
                    + TableDetails.LineItemInfo.KEY_DESC + " TEXT,"
                    + TableDetails.LineItemInfo.KEY_LINEITEMPOS + " LONG,"
                    + TableDetails.LineItemInfo.KEY_DOCNUM + " TEXT,"
                    + TableDetails.LineItemInfo.KEY_TXNID + " LONG,"
                    + TableDetails.PickListInfo.KEY_NOTES + " TEXT,"
                    + TableDetails.PickListInfo.KEY_STATUS + " INT,"
                    + TableDetails.LineItemInfo.KEY_UOM + " TEXT,"
                    + TableDetails.LineItemInfo.KEY_TOPICK + " REAL,"
                    + TableDetails.LineItemInfo.KEY_PICKED + " REAL,"
                    + TableDetails.LineItemInfo.KEY_BARCODE + " TEXT,"
                    + TableDetails.LineItemInfo.KEY_BINLOCATION + " TEXT,"
                    + TableDetails.LineItemInfo.KEY_BINEXTID + " LONG,"
                    + TableDetails.LineItemInfo.KEY_CUSTOMFIELD + " TEXT,"
                    + TableDetails.LineItemInfo.KEY_SERIANUMBER + " BOOLEAN,"
                    + TableDetails.LineItemInfo.KEY_LOTNUMBER + " BOOLEAN "
                    + ")");
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
            Log.i("DatabaseHandler","LineItemInfo table Creation Failed");
        }
        try {
            db.execSQL("CREATE TABLE " + TableDetails.Tables.SERIALLOTNUMBER + " ("
                    + TableDetails.KEY_ID + " LONG,"
                    + TableDetails.SerialNumberInfo.KEY_LINEITEMID + " LONG NOT NULL,"
                    + TableDetails.SerialNumberInfo.KEY_TYPE + " LONG,"
                    + TableDetails.SerialNumberInfo.KEY_VALUE + " TEXT "
                    + ")");
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
            Log.i("DatabaseHandler","SerialNumberInfo Table creation failed");
        }
        try {
            db.execSQL("CREATE TABLE " + TableDetails.Tables.SYNCINFO + " ("
                    + TableDetails.SyncInfo.KEY_GUID + " LONG PRIMARY KEY,"
                    + TableDetails.SyncInfo.KEY_LASTSYNCTIME + " TEXT "
                    + ")");
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
            Log.i("DatabaseHandler","SyncInfo Table creation failed");
        }

        try {
            db.execSQL("CREATE TABLE " + TableDetails.Tables.COMPANYFILEINFO + " ("
                    + TableDetails.CompanyFileInfo.KEY_REALMID + " LONG PRIMARY KEY,"
                    + TableDetails.CompanyFileInfo.KEY_DEVICEGUID + " TEXT,"
                    + TableDetails.CompanyFileInfo.KEY_COMPANYNAME + " TEXT "
                    + ")");
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
            Log.i("DatabaseHandler","CompanyFileInfo Table creation failed");
        }

        /*db.execSQL("create table PickListInfo " +
                "(id long, companyId long,taskType long,name text,assigneeId long,createdById long,Status int,siteId long,notes text,showNotes text,syncToken long, modifiedTimestamp text)");

        //need to change
        db.execSQL("create table LineItemInfo " +
                "(id long, taskId long,extId long,itemName text,itemDesc text,lineitemPos long,docNum text,txnId long,notes text,status int, uom text,qtyToPick real, qtyPicked real,barcode text,binLocation text,binExtId long,customFields text,showSerialNo boolean, showLotNo boolean)");

        db.execSQL("create table SerialNumberInfo " +
                "(id long,lineitemId long,type long,value text)");

        db.execSQL("create table SyncInfo " +
                "(guid text,lastSyncTime text)");

        db.execSQL("create table CompanyFileInfo " +
                "(id long, companyId long, extDeviceId text, companyName text)");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
    }


    //This Method will delete One Specific Picklist from Database
    //It will not delete related line items and its details
    public void deleteOnePicklist(long id) {
        String selection = "id = ?";
        String [] selectionArgs = new String[] {String.valueOf(id)};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,selection,selectionArgs);

    }

    //This method will delete One specific Picklist and related line item and serial numbers
    public void deletePicklistWithDetails(long id)
    {
        try {
            deleteOnePicklist(id);
            List<LineItem> lineItems = allLineItems(id);
            if(lineItems != null && lineItems.size() > 0) {
                deleteLineItems(id);
                for (int i = 0; i < lineItems.size(); i++) {
                    LineItem lineItem = lineItems.get(i);
                    if(lineItem != null) {
                        List<SerialLotNumber> serialLotNumbers = lineItem.getSerialLotNumbers();
                        if (serialLotNumbers != null) {
                            for (int j = 0; j < serialLotNumbers.size(); j++) {
                                SerialLotNumber serialLotNumber = lineItem.getSerialLotNumbers().get(j);
                                deleteSerialNumber(serialLotNumber.getId(), serialLotNumber.getValue());
                                serialLotNumber = null;
                            }
                        }
                    }

                }
            }
        }
        catch (Exception exp)
        {

        }
    }

    //Get All picklist for given tasktype and company id
    //TO-DO - Query selection args will have company id,assignee id will change after device pairing flow
    public List<Picklist> allPickLists(long taskType,String companyID) {

        List<Picklist> mPickLists = new LinkedList<Picklist>();
        try {
            String selection = "taskType = ?";
            String [] selectionArgs = new String[] {String.valueOf(taskType)};
            Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,null,selection,selectionArgs,null,null);
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
                    mPickList.setStatus(Status.valueOf(cursor.getString(6)));
                    mPickList.setSiteId((cursor.getLong(7)));
                    mPickList.setNotes((cursor.getString(8)));
                    mPickList.setShowNotes(Boolean.valueOf(cursor.getString(9)));
                    mPickList.setSyncToken((cursor.getLong(10)));
                    //mPickList.setCreatedTimestamp(MSUtils.yyyyMMddFormat.parse((cursor.getString(11))));
                    mPickList.setModifiedTimestamp(MSUtils.yyyyMMddFormat.parse((cursor.getString(11))));
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

    //Get a picklist when its id is known
    public Picklist getPickList(long taskType,long id)
    {
        Picklist mPickList = null;
        try
        {
            String selection = "taskType=? and id=?";
            String [] selectionArgs = new String[] {String.valueOf(1),String.valueOf(id)};
            Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,null,selection,selectionArgs,null,null);
            if (cursor.moveToFirst()) {
                do {
                    mPickList = new Picklist();
                    mPickList.setId((cursor.getLong(0)));
                    mPickList.setCompanyId((cursor.getLong(1)));
                    mPickList.setTaskType((cursor.getLong(2)));
                    mPickList.setName((cursor.getString(3)));
                    mPickList.setAssigneeId((cursor.getLong(4)));
                    mPickList.setCreatedById((cursor.getLong(5)));
                    mPickList.setStatus(Status.valueOf(cursor.getString(6)));
                    mPickList.setSiteId((cursor.getLong(7)));
                    mPickList.setNotes((cursor.getString(8)));
                    mPickList.setShowNotes(Boolean.valueOf(cursor.getString(9)));
                    mPickList.setSyncToken((cursor.getLong(10)));
                    //mPickList.setCreatedTimestamp(MSUtils.yyyyMMddFormat.parse((cursor.getString(11))));
                    mPickList.setModifiedTimestamp(MSUtils.yyyyMMddFormat.parse((cursor.getString(11))));
                } while (cursor.moveToNext());
            }

        }
        catch (ParseException exp)
        {

        }
        return  mPickList;
    }
    //add one specific picklist,This will add only picklist in picklist info table
    public void addPickList(Picklist mPickList) {
        try {
            ContentValues values = new ContentValues();
            values.put(TableDetails.KEY_ID, mPickList.getId());
            values.put(TableDetails.PickListInfo.KEY_COMPANYID, mPickList.getCompanyId());
            values.put(TableDetails.PickListInfo.KEY_TASKTYPE, mPickList.getTaskType());
            values.put(TableDetails.PickListInfo.KEY_NAME, mPickList.getName());
            values.put(TableDetails.PickListInfo.KEY_ASSIGNEDID, mPickList.getAssigneeId());
            values.put(TableDetails.PickListInfo.KEY_CREATEDBYID, mPickList.getCreatedById());
            values.put(TableDetails.PickListInfo.KEY_STATUS, String.valueOf(mPickList.getStatus()));
            values.put(TableDetails.PickListInfo.KEY_SITEID, mPickList.getSiteId());
            values.put(TableDetails.PickListInfo.KEY_NOTES, mPickList.getNotes());
            values.put(TableDetails.PickListInfo.KEY_SHOWNOTES, String.valueOf(mPickList.isShowNotes()));
            values.put(TableDetails.PickListInfo.KEY_SYNCTOKEN, mPickList.getSyncToken());
           // values.put(KEY_CREATEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getCreatedTimestamp()));
            values.put(TableDetails.PickListInfo.KEY_MODIFIEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getModifiedTimestamp()));
            myCR.insert(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE, values);
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }

    //For a given picklist add picklist and its all childs,lineitem and serial number
    public void addPickListWithDetail(Picklist mPickList) {
        try
        {
            addPickList(mPickList);
            for(int i = 0;i < mPickList.getLineitems().size();i++)
            {
                LineItem lineItem = mPickList.getLineitems().get(i);
                addLineItem(lineItem,lineItem.getId());
                for(int j = 0;j< lineItem.getSerialLotNumbers().size();j++)
                {
                    SerialLotNumber serialLotNumber = lineItem.getSerialLotNumbers().get(j);
                    addSerialLotNumber(serialLotNumber);
                }
            }
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }

    //update specific picklist,will update only picklist not its childs
    public void updatePickList(Picklist mPickList, long recnum) {
        try {

            ContentValues values = new ContentValues();
            values.put(TableDetails.PickListInfo.KEY_ID, mPickList.getId());
            values.put(TableDetails.PickListInfo.KEY_COMPANYID, mPickList.getCompanyId());
            values.put(TableDetails.PickListInfo.KEY_TASKTYPE, mPickList.getTaskType());
            values.put(TableDetails.PickListInfo.KEY_NAME, mPickList.getName());
            values.put(TableDetails.PickListInfo.KEY_ASSIGNEDID, mPickList.getAssigneeId());
            values.put(TableDetails.PickListInfo.KEY_CREATEDBYID, mPickList.getCreatedById());
            values.put(TableDetails.PickListInfo.KEY_STATUS, String.valueOf(mPickList.getStatus()));
            values.put(TableDetails.PickListInfo.KEY_SITEID, mPickList.getSiteId());
            values.put(TableDetails.PickListInfo.KEY_NOTES, mPickList.getNotes());
            values.put(TableDetails.PickListInfo.KEY_SHOWNOTES, String.valueOf(mPickList.isShowNotes()));
            values.put(TableDetails.PickListInfo.KEY_SYNCTOKEN, mPickList.getSyncToken());
            //values.put(KEY_CREATEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getCreatedTimestamp()));
            values.put(TableDetails.PickListInfo.KEY_MODIFIEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getModifiedTimestamp()));

            String whereClause = "id = " + String.valueOf(mPickList.getId());

            myCR.update(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE, values, whereClause, null);
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }

    //For a given picklist id check whether any picklist exists in database
    public boolean PickListExists(long id)
    {
        String selection = "id=?";
        String [] selectionArgs = new String[] {String.valueOf(id)};
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,null,selection,selectionArgs,null,null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }


    //Line item related methods
    //delete one line item
    public void deleteLineItems(long id) {
        String selection = "taskId = ?";
        String [] selectionArgs = new String[]{String.valueOf(id)};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE,selection,selectionArgs);

    }

    //get all LineItems of a picklist
    public List<LineItem> allLineItems(long id) {

        List<LineItem> mLineItems = new LinkedList<LineItem>();
        String selection = "taskId = ?";
        String [] selectionArgs = new String[] {String.valueOf(id)};
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE,null,selection,selectionArgs,null,null);
        LineItem mLineItem = null;
        if(cursor != null) {
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
                        //mLineItem.setTxnDate(MSUtils.yyyyMMddFormat.parse((cursor.getString(8))));
                        //mLineItem.setTxnDate(MSUtils.yyyyMMddFormat.parse((cursor.getString(9))));
                        mLineItem.setNotes(cursor.getString(8));
                        mLineItem.setmItemStatus(Status.valueOf(cursor.getString(9)));
                        mLineItem.setUom(cursor.getString(10));
                        mLineItem.setQtyToPick(cursor.getDouble(11));
                        mLineItem.setQtyPicked(cursor.getDouble(12));
                        mLineItem.setBarcode(cursor.getString(13));
                        mLineItem.setBinLocation(cursor.getString(14));
                        mLineItem.setBinExtId(cursor.getLong(15));
						mLineItem.setCustomFields(cursor.getString(16));
                        String b = cursor.getString(17);
                        mLineItem.setShowSerialNo(Boolean.parseBoolean(cursor.getString(17)));
                        mLineItem.setShowLotNo(Boolean.parseBoolean(cursor.getString(18)));
                        mLineItems.add(mLineItem);
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        }
        else
            mLineItems = null;

        return mLineItems;
    }

    //add a line item,not its child
    public void addLineItem(LineItem lineItem, long id) {
        try {
            ContentValues values = new ContentValues();
            values.put(TableDetails.KEY_ID, lineItem.getId());
            values.put(TableDetails.LineItemInfo.KEY_TASKID, lineItem.getTaskId());
            values.put(TableDetails.LineItemInfo.KEY_EXTID, lineItem.getExtId());
            values.put(TableDetails.LineItemInfo.KEY_NAME_LINEITEM, lineItem.getItemName());
            values.put(TableDetails.LineItemInfo.KEY_DESC, lineItem.getItemDesc());
            values.put(TableDetails.LineItemInfo.KEY_LINEITEMPOS, lineItem.getLineitemPos());
            values.put(TableDetails.LineItemInfo.KEY_DOCNUM, lineItem.getDocNum());
            values.put(TableDetails.LineItemInfo.KEY_TXNID, lineItem.getTxnId());
            /*if(lineItem.getTxnDate() != null)
             values.put(KEY_TXNDATE, MSUtils.yyyyMMddFormat.format(lineItem.getTxnDate()));
            if(lineItem.getShipDate() != null)
            values.put(KEY_SHIPDATE, MSUtils.yyyyMMddFormat.format(lineItem.getShipDate()));*/
            values.put(TableDetails.PickListInfo.KEY_NOTES, lineItem.getNotes());
            values.put(TableDetails.PickListInfo.KEY_STATUS, String.valueOf(lineItem.getmItemStatus()));
            values.put(TableDetails.LineItemInfo.KEY_UOM, lineItem.getUom());
            values.put(TableDetails.LineItemInfo.KEY_TOPICK, lineItem.getQtyToPick());
            values.put(TableDetails.LineItemInfo.KEY_PICKED, lineItem.getQtyPicked());
            values.put(TableDetails.LineItemInfo.KEY_BARCODE, lineItem.getBarcode());
            values.put(TableDetails.LineItemInfo.KEY_BINLOCATION, lineItem.getBinLocation());
            values.put(TableDetails.LineItemInfo.KEY_BINEXTID, lineItem.getBinExtId());
            values.put(TableDetails.LineItemInfo.KEY_CUSTOMFIELD, lineItem.getCustomFields());
            values.put(TableDetails.LineItemInfo.KEY_SERIANUMBER,String.valueOf(lineItem.isShowSerialNo()));
            values.put(TableDetails.LineItemInfo.KEY_LOTNUMBER,String.valueOf(lineItem.isShowLotNo()));
            myCR.insert(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE, values);
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }

//update one line item,not its child
    public void updateLineItems(LineItem lineItem, long id) {
        try {
            ContentValues values = new ContentValues();

            values.put(KEY_ID, lineItem.getId());
            values.put(TableDetails.LineItemInfo.KEY_TASKID, lineItem.getTaskId());
            values.put(TableDetails.LineItemInfo.KEY_EXTID, lineItem.getExtId());
            values.put(TableDetails.LineItemInfo.KEY_NAME_LINEITEM, lineItem.getItemName());
            values.put(TableDetails.LineItemInfo.KEY_DESC, lineItem.getItemDesc());
            values.put(TableDetails.LineItemInfo.KEY_LINEITEMPOS, lineItem.getLineitemPos());
            values.put(TableDetails.LineItemInfo.KEY_DOCNUM, lineItem.getDocNum());
            values.put(TableDetails.LineItemInfo.KEY_TXNID, lineItem.getTxnId());
            //values.put(KEY_TXNDATE, MSUtils.yyyyMMddFormat.format(lineItem.getTxnDate()));
            //values.put(KEY_SHIPDATE, MSUtils.yyyyMMddFormat.format(lineItem.getShipDate()));
            values.put(TableDetails.PickListInfo.KEY_NOTES, lineItem.getNotes());
            values.put(TableDetails.PickListInfo.KEY_STATUS, String.valueOf(lineItem.getmItemStatus()));
            values.put(TableDetails.LineItemInfo.KEY_UOM, lineItem.getUom());
            values.put(TableDetails.LineItemInfo.KEY_TOPICK, lineItem.getQtyToPick());
            values.put(TableDetails.LineItemInfo.KEY_PICKED, lineItem.getQtyPicked());
            values.put(TableDetails.LineItemInfo.KEY_BARCODE, lineItem.getBarcode());
            values.put(TableDetails.LineItemInfo.KEY_BINLOCATION, lineItem.getBinLocation());
            values.put(TableDetails.LineItemInfo.KEY_BINEXTID, lineItem.getBinExtId());
            values.put(TableDetails.LineItemInfo.KEY_CUSTOMFIELD, lineItem.getCustomFields());
            values.put(TableDetails.LineItemInfo.KEY_SERIANUMBER,String.valueOf(lineItem.isShowSerialNo()));
            values.put(TableDetails.LineItemInfo.KEY_LOTNUMBER,String.valueOf(lineItem.isShowLotNo()));
            String whereClause = "id = " + String.valueOf(lineItem.getId());
            myCR.update(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE, values, whereClause, null);
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }

    }

    //add one serial lot number
    public  void addSerialLotNumber(SerialLotNumber serialLotNumber)
    {
        try {

            ContentValues values = new ContentValues();
            values.put(TableDetails.KEY_ID, serialLotNumber.getId());
            values.put(TableDetails.SerialNumberInfo.KEY_LINEITEMID, serialLotNumber.getLineitemId());
            values.put(TableDetails.SerialNumberInfo.KEY_TYPE, serialLotNumber.getType());
            values.put(TableDetails.SerialNumberInfo.KEY_VALUE, serialLotNumber.getValue());

        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }

    //get all serial lot numbers for a line item
    public List<SerialLotNumber> allSerialLotNumbers(long lineitemId) {

        List<SerialLotNumber> serialLotNumbers = new ArrayList<>();
        String selection = "lineitemId = ?";
        String [] selectionArgs = new String[] {String.valueOf(lineitemId)};
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_SERIALLOTNUMBER_TABLE,null,selection,selectionArgs,null,null);
        SerialLotNumber serialLotNumber = null;
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    serialLotNumber = new SerialLotNumber();
                    serialLotNumber.setId((cursor.getLong(0)));
                    serialLotNumber.setLineitemId((cursor.getLong(1)));
                    serialLotNumber.setType((cursor.getInt(2)));
                    serialLotNumber.setValue((cursor.getString(3)));
                    serialLotNumbers.add(serialLotNumber);
                } while (cursor.moveToNext());
            }
        }
        else
            serialLotNumber = null;

        return serialLotNumbers;
    }

    //delete one serial lot number
    public void deleteSerialNumber(long id,String SerialNumber)
    {
        String selection = "lineitemId=? and value=?";
        String [] selectionArgs = new String[] {String.valueOf(id),String.valueOf(SerialNumber)};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_SERIALLOTNUMBER_TABLE,selection,selectionArgs);

    }

    //add one picklist and its childs in one transaction
    public void addPickListInBatch(Picklist mPickList,boolean badd)
    {
        //Picklist Table
        String selection = "id = ?";
        ContentValues values = new ContentValues();
        values.put(KEY_ID, mPickList.getId());
        values.put(KEY_COMPANYID, mPickList.getCompanyId());
        values.put(KEY_TASKTYPE, mPickList.getTaskType());
        values.put(KEY_NAME, mPickList.getName());
        values.put(KEY_ASSIGNEDID, mPickList.getAssigneeId());
        values.put(KEY_CREATEDBYID, mPickList.getCreatedById());
        values.put(KEY_STATUS, String.valueOf(mPickList.getStatus()));
        values.put(KEY_SITEID, mPickList.getSiteId());
        values.put(KEY_NOTES, mPickList.getNotes());
        values.put(KEY_SHOWNOTES, String.valueOf(mPickList.isShowNotes()));
        values.put(KEY_SYNCTOKEN, mPickList.getSyncToken());
        values.put(KEY_MODIFIEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getModifiedTimestamp()));

        try {

            ArrayList<ContentProviderOperation> ops =
                    new ArrayList<ContentProviderOperation>();
            if(badd)
                   ops.add(
                     ContentProviderOperation.newInsert(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE)
                            .withValues(values)
                            .build());
            else
                ops.add(
                        ContentProviderOperation.newUpdate(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE)
                                .withValues(values)
                                .withSelection("id=?",new String[] {String.valueOf(mPickList.getId())})
                                .build());
            if(mPickList.getLineitems() != null) {
                for (int i = 0; i < mPickList.getLineitems().size();i++)
                {
                    //Lineitem table
                    LineItem lineItem = mPickList.getLineitems().get(i);
                    ContentValues values1 = new ContentValues();
                    values1.put(KEY_ID, lineItem.getId());
                    values1.put(KEY_TASKID, lineItem.getTaskId());
                    values1.put(KEY_EXTID, lineItem.getExtId());
                    values1.put(KEY_NAME_LINEITEM, lineItem.getItemName());
                    values1.put(KEY_DESC, lineItem.getItemDesc());
                    values1.put(KEY_LINEITEMPOS, lineItem.getLineitemPos());
                    values1.put(KEY_DOCNUM, lineItem.getDocNum());
                    values1.put(KEY_TXNID, lineItem.getTxnId());
                    //values1.put(KEY_TXNDATE, MSUtils.yyyyMMddFormat.format(lineItem.getTxnDate()));
                    //values1.put(KEY_SHIPDATE, MSUtils.yyyyMMddFormat.format(lineItem.getShipDate()));
                    values1.put(KEY_NOTES, lineItem.getNotes());
                    values1.put(KEY_STATUS, String.valueOf(lineItem.getmItemStatus()));
                    values1.put(KEY_UOM, lineItem.getUom());
                    values1.put(KEY_TOPICK, lineItem.getQtyToPick());
                    values1.put(KEY_PICKED, lineItem.getQtyPicked());
                    values1.put(KEY_BARCODE, lineItem.getBarcode());
                    values1.put(KEY_BINLOCATION, lineItem.getBinLocation());
                    values1.put(KEY_BINEXTID, lineItem.getBinExtId());
                    values1.put(KEY_CUSTOMFIELD, lineItem.getCustomFields());
                    values1.put(KEY_SERIANUMBER,String.valueOf(lineItem.isShowSerialNo()));
                    values1.put(KEY_LOTNUMBER,String.valueOf(lineItem.isShowLotNo()));
                    if(badd)
                        ops.add(
                            ContentProviderOperation.newInsert(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE)
                                    .withValues(values1)
                                    .build());
                    else
                        ops.add(
                                ContentProviderOperation.newUpdate(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE)
                                        .withValues(values1)
                                        .withSelection("id=?",new String[] {String.valueOf(lineItem.getId())})
                                        .build());
                    if(lineItem.getSerialLotNumbers()!= null) {
                        for (int j = 0; j < lineItem.getSerialLotNumbers().size(); j++) {
                            //Serial number table
                            SerialLotNumber serialLotNumber = lineItem.getSerialLotNumbers().get(j);
                            ContentValues values2 = new ContentValues();
                            values2.put(KEY_ID, serialLotNumber.getId());
                            values2.put(KEY_LINEITEMID, serialLotNumber.getLineitemId());
                            values2.put(KEY_TYPE, serialLotNumber.getType());
                            values2.put(KEY_VALUE, serialLotNumber.getValue());
                            if(badd)
                                ops.add(
                                    ContentProviderOperation.newInsert(ApplicationContentProvider.CONTENT_URI_SERIALLOTNUMBER_TABLE)
                                            .withValues(values2)
                                            .build());
                            else {

                                /*
                                1.Check if serial number already exist,dont add in query */
                                if(!isSerialNumberExist(serialLotNumber.getLineitemId(),serialLotNumber.getValue())) {
                                    ops.add(
                                            ContentProviderOperation.newUpdate(ApplicationContentProvider.CONTENT_URI_SERIALLOTNUMBER_TABLE)
                                                    .withValues(values2)
                                                    .withSelection("lineitemId=? and value=?", new String[]{String.valueOf(serialLotNumber.getLineitemId()), serialLotNumber.getValue()})
                                                    .build());
                                }
                            }
                        }
                    }
                }

            }
            myCR.applyBatch(ApplicationContentProvider.AUTHORITY,ops);
        }
        catch (RemoteException exp)
        {
            exp.printStackTrace();
        }
        catch (OperationApplicationException exp)
        {
            exp.printStackTrace();
        }


    }
    //get one picklist and its childs for a companyid
    public List<Picklist> getPicklistWithDetail(long taskType,String companyId)
    {
        List<Picklist> picklists = null;
        picklists = allPickLists(taskType,companyId);
        if(picklists != null)
        {
            for(int i = 0; i < picklists.size();i++) {
                List<LineItem>  lineItems = allLineItems(picklists.get(i).getId());
                if(lineItems != null) {
                    picklists.get(i).setLineitems(lineItems);
                    for (int j = 0; j < lineItems.size(); j++) {
                        List<SerialLotNumber> serialLotNumbers = allSerialLotNumbers(lineItems.get(j).getId());
                        if (serialLotNumbers != null)
                            lineItems.get(j).setSerialLotNumbers(serialLotNumbers);

                    }
                }
            }
        }
        return  picklists;

    }

    //delete one picklist and its childs in one transaction
    public void batchDeletePicklist(Picklist picklist)
    {
        String selection = "id = ?";
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newDelete(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE)
                .withSelection("id=?",new String[] {String.valueOf(picklist.getId())})
                .build()
        );

        ops.add(ContentProviderOperation.newDelete(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE)
                .withSelection("taskId=?",new String[] {String.valueOf(picklist.getId())})
                .build()
        );
        if(picklist.getLineitems()!= null)
        {
            List<LineItem> lineItems = picklist.getLineitems();
            for(int i = 0; i < lineItems.size();i++)
            {
                LineItem lineItem = lineItems.get(i);
                List<SerialLotNumber> serialLotNumbers = lineItem.getSerialLotNumbers();
                if(serialLotNumbers != null)
                {
                    for(int j = 0; j < serialLotNumbers.size();j++)
                    {
                        SerialLotNumber serialLotNumber = serialLotNumbers.get(j);
                        ops.add(ContentProviderOperation.newDelete(ApplicationContentProvider.CONTENT_URI_SERIALLOTNUMBER_TABLE)
                                .withSelection("lineitemId=? and value=?",new String[] {String.valueOf(serialLotNumber.getLineitemId()),serialLotNumber.getValue()})
                                .build()
                        );
                    }
                }

                }

        }
        try {
            myCR.applyBatch(ApplicationContentProvider.AUTHORITY, ops);
        }
        catch (OperationApplicationException exp)
        {

        }
        catch (RemoteException exp)
        {

        }

    }

    //STORE last sync time for a company guid provided by sync adapter
    public void storeLastSycTime(String guid)
    {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            final String utcTime = sdf.format(new Date());
            ContentValues values = new ContentValues();
            values.put(KEY_GUID, guid);
            values.put(KEY_LASTSYNCTIME, utcTime);
            Log.i("SyncAdapter",utcTime);
            String whereClause = "guid = " + String.valueOf(guid);
            if(syncTimeExistsforCompanyId(guid))
                    myCR.update(ApplicationContentProvider.CONTENT_URI_SYNCINFO_TABLE, values,whereClause,null);
            else
                myCR.insert(ApplicationContentProvider.CONTENT_URI_SYNCINFO_TABLE,values);
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }


    }

    public boolean syncTimeExistsforCompanyId(String guid)
    {
        String selection = "guid = ?";
        String utcTime = null;
        String [] selectionArgs = new String[] {guid};
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_SYNCINFO_TABLE,null,selection,selectionArgs,null,null);
        if(cursor != null && cursor.getCount() > 0)
        {
           return true;

        }

        return false;
    }
    //get last sync time
    public String getlastSyncedUTCTime(String guid)
    {
        String selection = "guid = ?";
        String utcTime = null;
        String [] selectionArgs = new String[] {guid};
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_SYNCINFO_TABLE,null,selection,selectionArgs,null,null);
        if(cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            String guid1 = cursor.getString(0);
            utcTime = cursor.getString(1);

        }

        return utcTime;
    }

    //get nuber of line items in a picklist
    public int getItemCountForPicklist(long id)
    {
        int count = 0;
        try {
            String selection = "taskId = ?";
            String[] selectionArgs = new String[]{String.valueOf(id)};
            Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE, null, selection, selectionArgs, null, null);
            if(cursor != null)
                count = cursor.getCount();
        }
        catch (IllegalArgumentException exp)
        {

        }
        return count;
    }
    public void addCompanyFileDetails(CompanyFileDetails details)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        try
        {
            ContentValues values = new ContentValues();

            values.put(KEY_REALMID, details.getRealmID());
            values.put(KEY_DEVICEGUID, details.getDeviceGUID());
            values.put(KEY_COMPANYNAME, details.getCompanyName());

            db.insert(TABLE_COMPANYYFILEINFO_NAME,null, values);
            db.close();
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }


    }

    public CompanyFileDetails getDetails() {
        CompanyFileDetails obj = new CompanyFileDetails();

        String query = "SELECT  * FROM " + TABLE_COMPANYYFILEINFO_NAME;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        //Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_COMPANYFILE_TABLE, null, query, null, null);
        if (cursor.moveToFirst()) {
            do {

                obj.setRealmID((cursor.getLong(0)));
                obj.setDeviceGUID((cursor.getString(1)));
                obj.setCompanyName(cursor.getString(2));

            } while (cursor.moveToNext());
        }

        return obj;
    }
    public boolean isSerialNumberExist(long lineitemId,String value)
    {
        String selection = "lineitemId=? and value=?";
        String [] selectionArgs = new String[] {String.valueOf(lineitemId),value};
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_SERIALLOTNUMBER_TABLE,null,selection,selectionArgs,null,null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
