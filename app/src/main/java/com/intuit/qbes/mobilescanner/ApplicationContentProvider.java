package com.intuit.qbes.mobilescanner;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.intuit.qbes.mobilescanner.model.Picklist;

/**
 * Created by ckumar5 on 04/02/17.
 */

public class ApplicationContentProvider extends ContentProvider{

    public static final String AUTHORITY = "ourContentProviderAuthorities";//specific for our our app, will be specified in maninfed

    //Content URI
    public static final Uri CONTENT_URI_PICKLIST_TABLE = Uri.parse("content://" + AUTHORITY + "/PickListInfo");
    public static final Uri CONTENT_URI_LINEITEM_TABLE = Uri.parse("content://" + AUTHORITY + "/LineItemInfo");

    //Table Name
    private static final String PICKLIST_TABLE = "PickListInfo";
    private static final String LINEITEM_TABLE = "LineItemInfo";

    public static final int PICKLISTS = 1;
    public static final int LINEITEMS = 2;

    //DataBase Handler
    private DatabaseHandler mDbHandler = null;

    //URI Matcher
    private static final UriMatcher sURIMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sURIMatcher.addURI(AUTHORITY, PICKLIST_TABLE,PICKLISTS);
        sURIMatcher.addURI(AUTHORITY, LINEITEM_TABLE,LINEITEMS);
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
        SQLiteDatabase sqlDB = mDbHandler.getWritableDatabase();
        int rowsUpdated = 0;
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
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = Integer.parseInt(getType(uri));
        SQLiteDatabase sqlDB = mDbHandler.getWritableDatabase();
        long id = 0;
        Uri  ret;
        switch (uriType) {

            case PICKLISTS:
                id = sqlDB.insert(mDbHandler.TABLE_PICKLISTINFO_NAME, null, values);
                ret = Uri.parse(PICKLIST_TABLE + "/" + id);
                break;

            case LINEITEMS:
                id = sqlDB.insert(mDbHandler.TABLE_LINEITEMINFO_NAME, null, values);
                ret = Uri.parse(LINEITEM_TABLE + "/" + id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
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
        SQLiteDatabase sqlDB = mDbHandler.getWritableDatabase();
        int rowsDeleted = 0;
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
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //chandan - need to change
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDbHandler.getReadableDatabase();
        Cursor cursor = null;
        cursor = sqlDB.rawQuery(selection,null);
        return cursor;
    }

}
