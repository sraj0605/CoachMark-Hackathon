package com.intuit.qbes.mobilescanner;

/**
 * Created by ashah9 on 12/12/16.
 */

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
 * Created by ashah9 on 12/1/16.
 */
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.intuit.qbes.mobilescanner.model.Picklist;
import com.symbol.emdk.barcode.ScannerConfig;

public class SQLiteDatabaseLineItemHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 21;
    private static final String DATABASE_NAME = "LineItemDBTest11.db";
    private static final String TABLE_NAME = "LineItemInfo";
    private static final String KEY_PICKLIST_ID = "picklist_id";
    private static final String KEY_RECNUM = "recnum";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESC = "desc";
    private static final String KEY_UOM = "uom";
    private static final String KEY_NEEDED = "needed";
    private static final String KEY_PICKED = "picked";
    private static final String KEY_TOPICK = "to_pick";
    private static final String KEY_BARCODE = "barcode";

    private static final String[] COLUMNS = { KEY_PICKLIST_ID, KEY_RECNUM, KEY_NAME,  KEY_DESC, KEY_UOM, KEY_NEEDED, KEY_PICKED,KEY_TOPICK,KEY_BARCODE  };

    public SQLiteDatabaseLineItemHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table LineItemInfo " +
                "(picklist_id long, recnum long,name text,desc text, uom text,needed real, picked real, to_pick real, barcode text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

      public void deleteLineItems(long recnum) {
          // Get reference to writable DB
          SQLiteDatabase db = this.getWritableDatabase();
          db.delete(TABLE_NAME, "recnum = ?",
                  new String[]{String.valueOf(recnum)});
          db.close();

      }


    public List<LineItem> allLineItems(long id) {

        List<LineItem> mLineItems = new LinkedList<LineItem>();
        String query = "SELECT  * FROM " + TABLE_NAME + " where " + "picklist_id" + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
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
        SQLiteDatabase db = this.getWritableDatabase();
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

        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

      public void updateLineItemWithQtyPicked(LineItem lineItem, double QtyPicked ) {
          SQLiteDatabase db = this.getWritableDatabase();
          ContentValues values = new ContentValues();
          values.put(KEY_PICKLIST_ID, lineItem.getPickListID());
          values.put(KEY_RECNUM, lineItem.getRecNum());
          values.put(KEY_NAME, lineItem.getName());
          values.put(KEY_DESC,lineItem.getDescription());
          values.put(KEY_UOM, lineItem.getUom());
          values.put(KEY_NEEDED, lineItem.getQtyNeeded());
          values.put(KEY_PICKED, QtyPicked);
          values.put(KEY_TOPICK, lineItem.getQtyToPick());
          values.put(KEY_BARCODE, lineItem.getBarcode());
          db.update(TABLE_NAME, // table
                  values, // column/value
                  "recnum = " + String.valueOf(lineItem.getRecNum()), null);

          db.close();

      }

    public void updateLineItems(LineItem lineItem, long id) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        db.update(TABLE_NAME, // table
                values, // column/value
                "recnum = " + String.valueOf(lineItem.getRecNum()), null);

        db.close();

    }
}
