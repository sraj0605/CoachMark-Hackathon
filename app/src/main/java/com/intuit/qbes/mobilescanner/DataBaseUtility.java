package com.intuit.qbes.mobilescanner;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;
import com.intuit.qbes.mobilescanner.model.Status;
import com.intuit.qbes.mobilescanner.model.Task;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ckumar5 on 18/02/17.
 */

public class DataBaseUtility extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 21;
    private static final String DATABASE_NAME = "MobileScanner.db";
    public static final String TABLE_LINEITEMINFO_NAME = "LineItemInfo";
    public static final String TABLE_SERIALNUBERINFO = "SerialNumberInfo";
    public static final String TABLE_TASKINFO_NAME = "TasksInfo";

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


    //Context object
    private ContentResolver myCR = null;

    public DataBaseUtility(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myCR = context.getContentResolver();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table TasksInfo " +
                "(id long, companyId long,taskType long,name text,assigneeId long,createdById long,Status int,siteId long,notes text,showNotes text,syncToken long,createdTimestamp text, modifiedTimestamp text)");

        //need to change
        db.execSQL("create table LineItemInfo " +
                "(id long, taskId long,extId long,itemName text,itemDesc text,lineitemPos long,docNum text,txnId long,txnDate text,shipDate text,notes text,status int, uom text,qtyToPick real, qtyPicked real,barcode text,binLocation text,binExtId long,customFields text,serialLotNumbers text, showSerialNo boolean, showLotNo boolean)");

        db.execSQL("create table SerialNumberInfo " +
                "(id long,lineitemId long,type long,value text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
    }

    public void deleteTask(Task task)
    {
        String selection = "id = ?";
        String [] selectionArgs = new String[] {String.valueOf(task.getId())};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,selection,selectionArgs);
    }

    public List<Task> getTasks(long taskType) {

        List<Task> tasks = new LinkedList<Task>();

        try {
            String selection = "taskType = ?";
            String [] selectionArgs = new String[] {String.valueOf(taskType)};
            Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,null,selection,selectionArgs,null,null);
            Task task = null;
            if (cursor.moveToFirst()) {
                do {
                    task = new Task();
                    task.setId((cursor.getLong(0)));
                    task.setCompanyId((cursor.getLong(1)));
                    task.setTaskType((cursor.getLong(2)));
                    task.setName((cursor.getString(3)));
                    task.setAssigneeId((cursor.getLong(4)));
                    task.setCreatedById((cursor.getLong(5)));
                    task.setStatus(Status.valueOf(cursor.getString(6)));
                    task.setSiteId((cursor.getLong(7)));
                    task.setNotes((cursor.getString(8)));
                    task.setShowNotes(Boolean.valueOf(cursor.getString(9)));
                    task.setSyncToken((cursor.getLong(10)));
                    task.setCreatedTimestamp(MSUtils.yyyyMMddFormat.parse((cursor.getString(11))));
                    task.setModifiedTimestamp(MSUtils.yyyyMMddFormat.parse((cursor.getString(12))));
                    tasks.add(task);
                } while (cursor.moveToNext());
            }
        }
        catch (ParseException exp)
        {
            exp.printStackTrace();
        }

        return tasks;
    }

    public void addTask(Task task) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, task.getId());
            values.put(KEY_COMPANYID, task.getCompanyId());
            values.put(KEY_TASKTYPE, task.getTaskType());
            values.put(KEY_NAME, task.getName());
            values.put(KEY_ASSIGNEDID, task.getAssigneeId());
            values.put(KEY_CREATEDBYID, task.getCreatedById());
            values.put(KEY_STATUS, String.valueOf(task.getStatus()));
            values.put(KEY_SITEID, task.getSiteId());
            values.put(KEY_NOTES, task.getNotes());
            values.put(KEY_SHOWNOTES, String.valueOf(task.isShowNotes()));
            values.put(KEY_SYNCTOKEN, task.getSyncToken());
            values.put(KEY_CREATEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(task.getCreatedTimestamp()));
            values.put(KEY_MODIFIEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(task.getModifiedTimestamp()));
            myCR.insert(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE, values);
            //Add Line item
            if(task.getLineitems()!= null && task.getLineitems().size() > 0)
            {
                for (int i = 0; i<task.getLineitems().size();i++)
                {
                    LineItem lineItem = new LineItem();
                    lineItem = task.getLineitems().get(i);
                    addLineItem(lineItem,lineItem.getId());
                    //Add serial lot number
                    if(lineItem.getSerialLotNumbers() !=null && lineItem.getSerialLotNumbers().size() > 0) {
                        for (int j = 0; j < lineItem.getSerialLotNumbers().size(); j++) {
                            SerialLotNumber serialLotNumber = new SerialLotNumber();
                            serialLotNumber = lineItem.getSerialLotNumbers().get(j);
                            addSerialLotNumber(serialLotNumber);
                        }
                    }

                }
            }
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }


    public void UpdateTask(Task task) {
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_ID, task.getId());
            values.put(KEY_COMPANYID, task.getCompanyId());
            values.put(KEY_TASKTYPE, task.getTaskType());
            values.put(KEY_NAME, task.getName());
            values.put(KEY_ASSIGNEDID, task.getAssigneeId());
            values.put(KEY_CREATEDBYID, task.getCreatedById());
            values.put(KEY_STATUS, String.valueOf(task.getStatus()));
            values.put(KEY_SITEID, task.getSiteId());
            values.put(KEY_NOTES, task.getNotes());
            values.put(KEY_SHOWNOTES, String.valueOf(task.isShowNotes()));
            values.put(KEY_SYNCTOKEN, task.getSyncToken());
            values.put(KEY_CREATEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(task.getCreatedTimestamp()));
            values.put(KEY_MODIFIEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(task.getModifiedTimestamp()));

            String whereClause = "id = " + String.valueOf(task.getId());

            myCR.update(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE, values, whereClause, null);
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }

    public boolean taskExists(long id)
    {
        String Query = "Select * from " + TABLE_TASKINFO_NAME + " where " + "id" + " = " + id;
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
                    mLineItem.setTxnDate(MSUtils.yyyyMMddFormat.parse((cursor.getString(8))));
                    mLineItem.setTxnDate(MSUtils.yyyyMMddFormat.parse((cursor.getString(9))));
                    mLineItem.setNotes(cursor.getString(10));
                    mLineItem.setmItemStatus(Status.valueOf(cursor.getString(11)));
                    mLineItem.setUom(cursor.getString(12));
                    mLineItem.setQtyToPick(cursor.getDouble(13));
                    mLineItem.setQtyPicked(cursor.getDouble(14));
                    mLineItem.setBarcode(cursor.getString(15));
                    mLineItem.setBinLocation(cursor.getString(16));
                    mLineItem.setBinExtId(cursor.getLong(17));
                    mLineItem.setCustomFields(cursor.getString(18));
                    mLineItem.setShowSerialNo(Boolean.parseBoolean(cursor.getString(20)));
                    mLineItem.setShowLotNo(Boolean.parseBoolean(cursor.getString(21)));
                    mLineItems.add(mLineItem);
                } catch (ParseException exp) {
                    exp.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
    }
    else
     mLineItems = null;

    return mLineItems;
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

    public  void addSerialLotNumber(SerialLotNumber serialLotNumber)
    {
        try {

            ContentValues values = new ContentValues();
            values.put(KEY_ID, serialLotNumber.getId());
            values.put(KEY_LINEITEMID, serialLotNumber.getLineitemId());
            values.put(KEY_TYPE, serialLotNumber.getType());
            values.put(KEY_VALUE, serialLotNumber.getValue());
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }

    public List<SerialLotNumber> allSerialLotNumbers(long id) {

        List<SerialLotNumber> serialLotNumbers = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_SERIALNUBERINFO + " where " + "id" + " = " + id;
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_SERIALLOTNUMBER_TABLE, null, query, null, null);

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

    public void deleteSerialNumber(long id,String SerialNumber)
    {
        String selection = "id=? and value=?";
        String [] selectionArgs = new String[] {String.valueOf(id),String.valueOf(SerialNumber)};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_SERIALLOTNUMBER_TABLE,selection,selectionArgs);

    }
}
