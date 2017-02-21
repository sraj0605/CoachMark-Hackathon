package com.intuit.qbes.mobilescanner;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.intuit.qbes.mobilescanner.model.Picklist;

/**
 * Created by ckumar5 on 04/02/17.
 */

public class ApplicationContentProvider extends ContentProvider{

    public static final String AUTHORITY = "ourContentProviderAuthorities";//specific for our our app, will be specified in maninfed

    //Content URI
    public static final Uri CONTENT_URI_PICKLIST_TABLE = Uri.parse("content://" + AUTHORITY + "/PickListInfo");
    public static final Uri CONTENT_URI_LINEITEM_TABLE = Uri.parse("content://" + AUTHORITY + "/LineItemInfo");
    public static final Uri CONTENT_URI_SERIALLOTNUMBER_TABLE = Uri.parse("content://" + AUTHORITY + "/SerialNumberInfo");

    //Table Name
    private static final String PICKLIST_TABLE = "PickListInfo";
    private static final String LINEITEM_TABLE = "LineItemInfo";
    public static final String SERIALLOTNUMBER_TABLE = "SerialNumberInfo";

    public static final int PICKLISTS = 1;
    public static final int LINEITEMS = 2;
    public static final int SERIALLOTNUMBERS = 3;

    //DataBase Handler
    private DatabaseHandler mDbHandler = null;

    //URI Matcher
    private static final UriMatcher sURIMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sURIMatcher.addURI(AUTHORITY, PICKLIST_TABLE,PICKLISTS);
        sURIMatcher.addURI(AUTHORITY, LINEITEM_TABLE,LINEITEMS);
        sURIMatcher.addURI(AUTHORITY, SERIALLOTNUMBER_TABLE,SERIALLOTNUMBERS);
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
                    rowsUpdated = sqlDB.update(mDbHandler.TABLE_SERIALNUBERINFO,
                            values,
                            selection,
                            selectionArgs);

                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
        }
        catch (SQLiteException exp )
        {
            Log.e("ContentProvider",exp.getMessage().toString());
        }
        return rowsUpdated;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = Integer.parseInt(getType(uri));
        long id = 0;
        Uri  ret = null;
        try {
            SQLiteDatabase sqlDB = mDbHandler.getWritableDatabase();
            switch (uriType) {

                case PICKLISTS:
                    id = sqlDB.insert(mDbHandler.TABLE_PICKLISTINFO_NAME, null, values);
                    ret = Uri.parse(PICKLIST_TABLE + "/" + id);
                    break;

                case LINEITEMS:
                    id = sqlDB.insertOrThrow(mDbHandler.TABLE_LINEITEMINFO_NAME, null, values);
                    ret = Uri.parse(LINEITEM_TABLE + "/" + id);
                    break;
                case SERIALLOTNUMBERS:
                    id = sqlDB.insert(mDbHandler.TABLE_SERIALNUBERINFO, null, values);
                    ret = Uri.parse(SERIALLOTNUMBER_TABLE + "/" + id);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        catch (SQLiteException exp)
        {
            Log.e("ContentProvider",exp.getMessage().toString());
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {

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
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
        }
        catch (SQLiteException exp)
        {
            Log.e("ContentProvider",exp.getMessage().toString());
        }
        return rowsDeleted;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

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
                    cursor = sqlDB.query(mDbHandler.TABLE_LINEITEMINFO_NAME,null,selection,selectionArgs,null,null,null);
            }
            //cursor = sqlDB.rawQuery(selection, null);
            //cursor = sqlDB.query(mDbHandler.TABLE_PICKLISTINFO_NAME,null,selection,selectionArgs,null,null,null);
        }
        catch (SQLiteException exp)
        {
            Log.e("ContentProvider",exp.getMessage().toString());
        }
        return cursor;
    }

}
