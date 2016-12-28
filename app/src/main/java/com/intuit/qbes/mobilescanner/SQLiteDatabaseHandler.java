package com.intuit.qbes.mobilescanner;

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

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 21;
    private static final String DATABASE_NAME = "PickListDBTest11.db";
    private static final String TABLE_NAME = "PickListInfo";
    private static final String KEY_RECNUM = "recnum";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_STATUS = "status";
    private static final String KEY_ODATE = "OrderDate";
    private static final String KEY_SDATE = "ShipDate";
    private static final String KEY_CJOB = "CustomerJob";
    private static final String[] COLUMNS = { KEY_NUMBER, KEY_RECNUM, KEY_STATUS,  KEY_ODATE, KEY_SDATE, KEY_CJOB};

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table PickListInfo " +
                "(recnum long, number integer,status integer,OrderDate text, ShipDate text,CustomerJob text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void deleteOnePicklist(Picklist mPickList) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "recnum = ?", new String[] {String.valueOf(mPickList.getRecnum())} );
        db.close();

    }

/*    public Picklist getPickList(long recnum) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " recnum = ?", // c. selections
                new String[] { String.valueOf(recnum) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Picklist mPickList = new Picklist();
        mPickList.setNumber((cursor.getString(0)));
        mPickList.setRecnum((cursor.getLong(1)));
        mPickList.setStatus((cursor.getInt(2)));
        mPickList.setOrderDate(cursor.getString(3));
        mPickList.setShipDate(cursor.getString(3));
        mPickList.setName(cursor.getString(4));

        return mPickList;
    }
*/
    public List<Picklist> allPickLists() {

        List<Picklist> mPickLists = new LinkedList<Picklist>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
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
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RECNUM, mPickList.getRecnum());
        values.put(KEY_NUMBER, mPickList.getNumber());
        values.put(KEY_STATUS, mPickList.getStatus());
        values.put(KEY_ODATE,MSUtils.yyyyMMddFormat.format((mPickList.getOrderDate())));
        values.put(KEY_SDATE, MSUtils.yyyyMMddFormat.format(mPickList.getShipDate()));
        values.put(KEY_CJOB, mPickList.getName());

        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public void updatePickList(Picklist mPickList, long recnum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RECNUM, mPickList.getRecnum());
        values.put(KEY_NUMBER, mPickList.getNumber());
        values.put(KEY_STATUS, mPickList.getStatus());
        values.put(KEY_ODATE, MSUtils.yyyyMMddFormat.format((mPickList.getOrderDate())));
        values.put(KEY_SDATE, MSUtils.yyyyMMddFormat.format((mPickList.getOrderDate())));
        values.put(KEY_CJOB, mPickList.getName());

        db.update(TABLE_NAME, // table
                values, // column/value
                "recnum = " + String.valueOf(mPickList.getRecnum()), null);

        db.close();

    }

public boolean PickListExists(long recnum)
{
    SQLiteDatabase db = this.getWritableDatabase();
    String Query = "Select * from " + TABLE_NAME + " where " + "recnum" + " = " + recnum;
    Cursor cursor = db.rawQuery(Query, null);
    if(cursor.getCount() <= 0){
        cursor.close();
        return false;
    }
    cursor.close();
    return true;

}
}
