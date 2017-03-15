package com.intuit.qbes.mobilescanner;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.intuit.qbes.mobilescanner.model.Picklist;

import java.util.ArrayList;

/**
 * Created by ckumar5 on 04/02/17.
 */

public class ApplicationContentProvider extends ContentProvider{

    public static final String AUTHORITY = "com.intuit.qbes.mobilescanner.provider";//specific for our our app, will be specified in maninfed

    //Content URI
    public static final Uri CONTENT_URI_PICKLIST_TABLE = Uri.parse("content://" + AUTHORITY + "/PickListInfo");
    public static final Uri CONTENT_URI_LINEITEM_TABLE = Uri.parse("content://" + AUTHORITY + "/LineItemInfo");
    public static final Uri CONTENT_URI_SERIALLOTNUMBER_TABLE = Uri.parse("content://" + AUTHORITY + "/SerialNumberInfo");
    public static final Uri CONTENT_URI_TASK_TABLE = Uri.parse("content://" + AUTHORITY + "/TaskInfo");
    public static final Uri CONTENT_URI_SYNCINFO_TABLE = Uri.parse("content://" + AUTHORITY + "/SyncInfo");



    //Table Name
    private static final String PICKLIST_TABLE = "PickListInfo";
    private static final String LINEITEM_TABLE = "LineItemInfo";
    public static final String SERIALLOTNUMBER_TABLE = "SerialNumberInfo";
    public static final String SYNCINFO_TABLE = "SyncInfo";

    public static final int PICKLISTS = 1;
    public static final int LINEITEMS = 2;
    public static final int SERIALLOTNUMBERS = 3;
    public static final int SYNCINFO = 4;

    //DataBase Handler
    private DatabaseHandler mDbHandler = null;

    //URI Matcher
    private static final UriMatcher sURIMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sURIMatcher.addURI(AUTHORITY, PICKLIST_TABLE,PICKLISTS);
        sURIMatcher.addURI(AUTHORITY, LINEITEM_TABLE,LINEITEMS);
        sURIMatcher.addURI(AUTHORITY, SERIALLOTNUMBER_TABLE,SERIALLOTNUMBERS);
        sURIMatcher.addURI(AUTHORITY, SYNCINFO_TABLE,SYNCINFO);
    }
    @Nullable
    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        return String.valueOf(uriType);
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = Integer.parseInt(getType(uri));
        int rowsUpdated = 0;
        try {
            SQLiteDatabase sqlDB = mDbHandler.getWritableDatabase();
            switch (uriType) {
                case PICKLISTS:
                    rowsUpdated = sqlDB.update(mDbHandler.TABLE_PICKLISTINFO_NAME,
                            values,
                            selection,
                            selectionArgs);
                    break;

                case LINEITEMS:
                    rowsUpdated = sqlDB.update(mDbHandler.TABLE_LINEITEMINFO_NAME,
                            values,
                            selection,
                            selectionArgs);
                    break;
                case SERIALLOTNUMBERS:
                    /*rowsUpdated = sqlDB.update(mDbHandler.TABLE_SERIALNUBERINFO,
                            values,
                            selection,
                            selectionArgs);*/
                    /*As of now lets keep all as insert*/
                    /*We have made sure that only new serial number will come here hence inserting*/
                    long rowInserted = sqlDB.insertOrThrow(mDbHandler.TABLE_SERIALNUBERINFO, null, values);
                    break;
                case SYNCINFO:
                    rowsUpdated = sqlDB.update(mDbHandler.TABLE_SYNCINFO,
                            values,
                            selection,
                            selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            //getContext().getContentResolver().notifyChange(uri, null);
        }
        catch (SQLiteException exp )
        {
            Log.e("ContentProvider",exp.getMessage().toString());
            throw new SQLiteException(exp.getMessage().toString());
        }
        return rowsUpdated;
    }

    @Nullable
    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {
        int uriType = Integer.parseInt(getType(uri));
        long id = 0;
        Uri  ret = null;
        try {
            SQLiteDatabase sqlDB = mDbHandler.getWritableDatabase();
            switch (uriType) {

                case PICKLISTS:
                    id = sqlDB.insertOrThrow(mDbHandler.TABLE_PICKLISTINFO_NAME, null, values);
                    ret = Uri.parse(PICKLIST_TABLE + "/" + id);
                    break;

                case LINEITEMS:
                    id = sqlDB.insertOrThrow(mDbHandler.TABLE_LINEITEMINFO_NAME, null, values);
                    ret = Uri.parse(LINEITEM_TABLE + "/" + id);
                    break;
                case SERIALLOTNUMBERS:
                    id = sqlDB.insertOrThrow(mDbHandler.TABLE_SERIALNUBERINFO, null, values);
                    ret = Uri.parse(SERIALLOTNUMBER_TABLE + "/" + id);
                    break;
                case SYNCINFO:
                    id = sqlDB.insertOrThrow(mDbHandler.TABLE_SYNCINFO, null, values);
                    ret = Uri.parse(SYNCINFO_TABLE + "/" + id);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        catch (SQLiteException exp)
        {
            Log.e("ContentProvider",exp.getMessage().toString());
            throw new SQLiteException(exp.getMessage().toString());
        }

        return ret;
    }

    @Override
    public boolean onCreate() {

        mDbHandler = new DatabaseHandler(getContext());

        if(mDbHandler !=null)
            return true;

        return false;
    }

    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = Integer.parseInt(getType(uri));
        int rowsDeleted = 0;
        try {
            SQLiteDatabase sqlDB = mDbHandler.getWritableDatabase();
            switch (uriType) {
                case PICKLISTS:

                    rowsDeleted = sqlDB.delete(mDbHandler.TABLE_PICKLISTINFO_NAME,
                            selection,
                            selectionArgs);
                    break;
                case LINEITEMS:
                    rowsDeleted = sqlDB.delete(mDbHandler.TABLE_LINEITEMINFO_NAME,
                            selection,
                            selectionArgs);
                    break;
                case SERIALLOTNUMBERS:
                    rowsDeleted = sqlDB.delete(mDbHandler.TABLE_SERIALNUBERINFO,
                            selection,
                            selectionArgs);
                    break;
                case SYNCINFO:
                    rowsDeleted = sqlDB.delete(mDbHandler.TABLE_SYNCINFO,
                            selection,
                            selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            //getContext().getContentResolver().notifyChange(uri, null);
        }
        catch (SQLiteException exp)
        {
            Log.e("ContentProvider",exp.getMessage().toString());
            throw new SQLiteException(exp.getMessage().toString());
        }
        return rowsDeleted;
    }

    @Nullable
    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //chandan - need to change
        Cursor cursor = null;
        int uriType = sURIMatcher.match(uri);
        try {

            SQLiteDatabase sqlDB = mDbHandler.getReadableDatabase();
            switch (uriType) {
                case PICKLISTS:
                    cursor = sqlDB.query(mDbHandler.TABLE_PICKLISTINFO_NAME,null,selection,selectionArgs,null,null,null);
                    break;
                case LINEITEMS:
                    cursor = sqlDB.query(mDbHandler.TABLE_LINEITEMINFO_NAME,null,selection,selectionArgs,null,null,null);
                    break;
                case SERIALLOTNUMBERS:
                    cursor = sqlDB.query(mDbHandler.TABLE_SERIALNUBERINFO,null,selection,selectionArgs,null,null,null);
                    break;
                case SYNCINFO:
                    cursor = sqlDB.query(mDbHandler.TABLE_SYNCINFO,null,selection,selectionArgs,null,null,null);
            }
        }
        catch (SQLiteException exp)
        {
            Log.e("ContentProvider",exp.getMessage().toString());
            throw new SQLiteException(exp.getMessage().toString());
        }
        return cursor;
    }



    @NonNull
    @Override
    public synchronized ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        ContentProviderResult[] results = null;
        SQLiteDatabase sqlDB = mDbHandler.getWritableDatabase();
        sqlDB.beginTransaction();
        try
        {
            //results = super.applyBatch(operations);
            final int numOperations = operations.size();
            //final ContentProviderResult[]
                    results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            sqlDB.setTransactionSuccessful();//Commit the transaction
            return results;
        }
        catch (SQLiteException exp)
        {
            exp.printStackTrace();
        }
        finally {

            sqlDB.endTransaction();
        }
    return results;
    }

}
