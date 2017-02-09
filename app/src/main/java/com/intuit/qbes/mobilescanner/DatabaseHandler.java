package com.intuit.qbes.mobilescanner;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

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
    private static final String KEY_RECNUM = "recnum";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_STATUS = "status";
    private static final String KEY_ODATE = "OrderDate";
    private static final String KEY_SDATE = "ShipDate";
    private static final String KEY_CJOB = "CustomerJob";
    private static final String[] COLUMNS = { KEY_NUMBER, KEY_RECNUM, KEY_STATUS,  KEY_ODATE, KEY_SDATE, KEY_CJOB};
//Table -2
    private static final String KEY_PICKLIST_ID = "picklist_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESC = "desc";
    private static final String KEY_UOM = "uom";
    private static final String KEY_NEEDED = "needed";
    private static final String KEY_PICKED = "picked";
    private static final String KEY_TOPICK = "to_pick";
    private static final String KEY_BARCODE = "barcode";

    //Context object
    private ContentResolver myCR = null;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myCR = context.getContentResolver();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table PickListInfo " +
                "(recnum long, number integer,status integer,OrderDate text, ShipDate text,CustomerJob text)");

        db.execSQL("create table LineItemInfo " +
                "(picklist_id long, recnum long,name text,desc text, uom text,needed real, picked real, to_pick real, barcode text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
    }


    public void deleteOnePicklist(Picklist mPickList) {

        //String selection = "recnum = \" " + String.valueOf(mPickList.getRecnum()) + "\"";
        String selection = "recnum = ?";
        String [] selectionArgs = new String[] {String.valueOf(mPickList.getRecnum())};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,selection,selectionArgs);

    }

    public List<Picklist> allPickLists() {

        List<Picklist> mPickLists = new LinkedList<Picklist>();
        String query = "SELECT  * FROM " + TABLE_PICKLISTINFO_NAME;
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,null,query,null,null);
        Picklist mPickList = null;
        if (cursor.moveToFirst()) {
            do {
                mPickList = new Picklist();
                mPickList.setRecnum((cursor.getLong(0)));
                mPickList.setNumber((cursor.getString(1)));
                mPickList.setStatus(cursor.getInt(2));
                mPickList.setOrderDate((cursor.getString(3)));
                mPickList.setShipDate(cursor.getString(4));
                mPickList.setName(cursor.getString(5));
                mPickLists.add(mPickList);
            } while (cursor.moveToNext());
        }

        return mPickLists;
    }

    public void addPickList(Picklist mPickList) {
        ContentValues values = new ContentValues();
        values.put(KEY_RECNUM, mPickList.getRecnum());
        values.put(KEY_NUMBER, mPickList.getNumber());
        values.put(KEY_STATUS, mPickList.getStatus());
        values.put(KEY_ODATE,MSUtils.yyyyMMddFormat.format((mPickList.getOrderDate())));
        values.put(KEY_SDATE, MSUtils.yyyyMMddFormat.format(mPickList.getShipDate()));
        values.put(KEY_CJOB, mPickList.getName());

        myCR.insert(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,values);
    }
    public void updatePickList(Picklist mPickList, long recnum) {

        ContentValues values = new ContentValues();

        values.put(KEY_RECNUM, mPickList.getRecnum());
        values.put(KEY_NUMBER, mPickList.getNumber());
        values.put(KEY_STATUS, mPickList.getStatus());
        values.put(KEY_ODATE, MSUtils.yyyyMMddFormat.format((mPickList.getOrderDate())));
        values.put(KEY_SDATE, MSUtils.yyyyMMddFormat.format((mPickList.getOrderDate())));
        values.put(KEY_CJOB, mPickList.getName());

        String whereClause = "recnum = " + String.valueOf(mPickList.getRecnum());

        myCR.update(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE, values, whereClause, null);
    }
    public boolean PickListExists(long recnum)
    {
        String Query = "Select * from " + TABLE_PICKLISTINFO_NAME + " where " + "recnum" + " = " + recnum;
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_PICKLIST_TABLE,null,Query,null,null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }


    //Line item related methods
    public void deleteLineItems(long recnum) {
        String selection = "recnum = ?";
        String [] selectionArgs = new String[]{String.valueOf(recnum)};
        myCR.delete(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE,selection,selectionArgs);

    }

    public List<LineItem> allLineItems(long id) {

        List<LineItem> mLineItems = new LinkedList<LineItem>();
        String query = "SELECT  * FROM " + TABLE_LINEITEMINFO_NAME + " where " + "picklist_id" + " = " + id;
        Cursor cursor = myCR.query(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE,null,query,null,null);
        LineItem mLineItem = null;

        if (cursor.moveToFirst()) {
            do {
                mLineItem = new LineItem();
                mLineItem.setPickListID(cursor.getLong(0));
                mLineItem.setRecNum((cursor.getLong(1)));
                mLineItem.setName((cursor.getString(2)));
                mLineItem.setDescription(cursor.getString(3));
                mLineItem.setUom((cursor.getString(4)));
                mLineItem.setQtyNeeded(cursor.getDouble(5));
                mLineItem.setQtyPicked(cursor.getDouble(6));
                mLineItem.setQtyToPick(cursor.getDouble(7));
                mLineItem.setBarcode(cursor.getString(8));
                mLineItems.add(mLineItem);
            } while (cursor.moveToNext());
        }

        return mLineItems;
    }

    public void addLineItem(LineItem lineItem, long id) {

        ContentValues values = new ContentValues();

        lineItem.setPickListID(id);

        values.put(KEY_PICKLIST_ID, id);
        values.put(KEY_RECNUM, lineItem.getRecNum());
        values.put(KEY_NAME, lineItem.getName());
        values.put(KEY_DESC,lineItem.getDescription());
        values.put(KEY_UOM, lineItem.getUom());
        values.put(KEY_NEEDED, lineItem.getQtyNeeded());
        values.put(KEY_PICKED, lineItem.getQtyPicked());
        values.put(KEY_TOPICK, lineItem.getQtyToPick());
        values.put(KEY_BARCODE, lineItem.getBarcode());

        myCR.insert(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE,values);
    }


    public void updateLineItems(LineItem lineItem, long id) {

        ContentValues values = new ContentValues();

        lineItem.setPickListID(id);
        values.put(KEY_PICKLIST_ID, id);
        values.put(KEY_RECNUM, lineItem.getRecNum());
        values.put(KEY_NAME, lineItem.getName());
        values.put(KEY_DESC,lineItem.getDescription());
        values.put(KEY_UOM, lineItem.getUom());
        values.put(KEY_NEEDED, lineItem.getQtyNeeded());
        values.put(KEY_PICKED, lineItem.getQtyPicked());
        values.put(KEY_TOPICK, lineItem.getQtyToPick());
        values.put(KEY_BARCODE, lineItem.getBarcode());
        String whereClause = "recnum = " + String.valueOf(lineItem.getRecNum());
        myCR.update(ApplicationContentProvider.CONTENT_URI_LINEITEM_TABLE,values,whereClause,null);

    }
}
