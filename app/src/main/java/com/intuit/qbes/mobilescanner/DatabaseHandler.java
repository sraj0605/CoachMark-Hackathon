package com.intuit.qbes.mobilescanner;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;
import com.intuit.qbes.mobilescanner.model.Status;

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
    public static final String TABLE_SERIALNUBERINFO = "SerialNumberInfo";

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

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myCR = context.getContentResolver();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table PickListInfo " +
                "(id long, companyId long,taskType long,name text,assigneeId long,createdById long,Status int,siteId long,notes text,showNotes text,syncToken long, modifiedTimestamp text)");

        //need to change
        db.execSQL("create table LineItemInfo " +
                "(id long, taskId long,extId long,itemName text,itemDesc text,lineitemPos long,docNum text,txnId long,notes text,status int, uom text,qtyToPick real, qtyPicked real,barcode text,binLocation text,binExtId long,customFields text,serialLotNumbers text, showSerialNo boolean, showLotNo boolean)");

        db.execSQL("create table SerialNumberInfo " +
                "(id long,lineitemId long,type long,value text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
    }


    public void deleteOnePicklist(long id) {

        //String selection = "recnum = \" " + String.valueOf(mPickList.getRecnum()) + "\"";
        String selection = "id = ?";
        String [] selectionArgs = new String[] {String.valueOf(id)};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,selection,selectionArgs);

    }

    public void deletePicklistWithDetails(long id)
    {
        try {
            deleteOnePicklist(id);
            List<LineItem> lineItems = allLineItems(id);
            if(lineItems != null && lineItems.size() > 0) {
                deleteLineItems(id);
                for (int i = 0; i < lineItems.size(); i++) {
                    LineItem lineItem = lineItems.get(i);
                    List<SerialLotNumber> serialLotNumbers = lineItem.getSerialLotNumbers();
                    if(serialLotNumbers != null)
                    {
                        for(int j= 0; j <serialLotNumbers.size();j++ )
                        {
                            SerialLotNumber serialLotNumber = lineItem.getSerialLotNumbers().get(j);
                            deleteSerialNumber(serialLotNumber.getId(), serialLotNumber.getValue());
                            serialLotNumber = null;
                        }
                    }

                }
            }
        }
        catch (Exception exp)
        {

        }
    }

    public List<Picklist> allPickLists() {

        List<Picklist> mPickLists = new LinkedList<Picklist>();
        try {
            /*String query = "SELECT  * FROM " + TABLE_PICKLISTINFO_NAME;
            Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE, null, query, null, null);*/
            String selection = "taskType = ?";
            String [] selectionArgs = new String[] {String.valueOf(1)};
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

    public Picklist getPickList(long id)
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

    public void addPickList(Picklist mPickList) {
        try {
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
           // values.put(KEY_CREATEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getCreatedTimestamp()));
            values.put(KEY_MODIFIEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getModifiedTimestamp()));
            myCR.insert(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE, values);
        }
        catch (IllegalArgumentException exp)
        {
            exp.printStackTrace();
        }
    }

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
    public void updatePickList(Picklist mPickList, long recnum) {
        try {

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
            //values.put(KEY_CREATEDTIMESTAMP, MSUtils.yyyyMMddFormat.format(mPickList.getCreatedTimestamp()));
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
    public void deleteLineItems(long id) {
        String selection = "id = ?";
        String [] selectionArgs = new String[]{String.valueOf(id)};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE,selection,selectionArgs);

    }

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
            /*if(lineItem.getTxnDate() != null)
             values.put(KEY_TXNDATE, MSUtils.yyyyMMddFormat.format(lineItem.getTxnDate()));
            if(lineItem.getShipDate() != null)
            values.put(KEY_SHIPDATE, MSUtils.yyyyMMddFormat.format(lineItem.getShipDate()));*/
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
            //values.put(KEY_TXNDATE, MSUtils.yyyyMMddFormat.format(lineItem.getTxnDate()));
            //values.put(KEY_SHIPDATE, MSUtils.yyyyMMddFormat.format(lineItem.getShipDate()));
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
        String selection = "id = ?";
        String [] selectionArgs = new String[] {String.valueOf(id)};
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

    public void deleteSerialNumber(long id,String SerialNumber)
    {
        String selection = "id=? and value=?";
        String [] selectionArgs = new String[] {String.valueOf(id),String.valueOf(SerialNumber)};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_SERIALLOTNUMBER_TABLE,selection,selectionArgs);

    }

    public void addPickListInBatch(Picklist mPickList,boolean badd)
    {
        //Picklist Table
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
                            else
                                ops.add(
                                        ContentProviderOperation.newUpdate(ApplicationContentProvider.CONTENT_URI_SERIALLOTNUMBER_TABLE)
                                                .withValues(values2)
                                                .build());
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

    public List<Picklist> getPicklistWithDetail()
    {
        List<Picklist> picklists = null;
        picklists = allPickLists();
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
                .withSelection("id=?",new String[] {String.valueOf(picklist.getId())})
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
                                .withSelection("id=?",new String[] {String.valueOf(serialLotNumber.getId())})
                                .build()
                        );
                    }
                }

                }

        }


    }

}
