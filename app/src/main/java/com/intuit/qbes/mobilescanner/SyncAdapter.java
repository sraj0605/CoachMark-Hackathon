package com.intuit.qbes.mobilescanner;

/**
 * Created by ckumar5 on 09/02/17.
 */

import com.intuit.qbes.mobilescanner.Account.GenericAccountService;
import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;
import com.intuit.qbes.mobilescanner.networking.DataSync;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;



import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

/**
 * Define a sync adapter for the app.
 *
 * This class is instantiated in {SyncService}, which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 *
 * The system calls onPerformSync() via an RPC call through the IBinder object supplied by
 * SyncService.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter implements DataSync.DataSyncCallback{
    public static final String TAG = "SyncAdapter";
    public static final String SYNC_STATUS = "SyncStatus";
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private boolean bDevicePairing = false;
    public Intent intent;
    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, parse it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run, and you don't have to set up a separate thread for them.
     .
     *
     * This is where we actually perform any work required to perform a sync.
     * {android.content.AbstractThreadedSyncAdapter} guarantees that this will be called on a non-UI thread,
     * so it is safe to peform blocking I/O here.
     *
     * The syncResult argument allows you to pass information back to the method that triggered
     * the sync.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");

        if (extras.getBoolean("MANUAL_SYNC") == true)//device pairing sync
            bDevicePairing = true;
        FetchAllPickList();

    }


    public void FetchAllPickList()
    {
        DataSync dataSync = new DataSync();
        dataSync.FetchPicklists(mContext,this);
    }

    @Override
    public void onFetchPicklist(List<Picklist> picklistsFromServer) {
            updateDevice(picklistsFromServer);

    }

    public void updateDevice(List<Picklist>picklistsFromServer)
    {
        DatabaseHandler db = new DatabaseHandler(mContext);
        for(int i =0;i < picklistsFromServer.size();i++)
        {
           Picklist picklistOnServer = picklistsFromServer.get(i);
            if(db.PickListExists(picklistOnServer.getId()))//Server Picklist already exist in device,delete it and add gain
            {
                db.deletePicklistWithDetails(picklistOnServer.getId());
            }
            db.addPickListWithDetail(picklistOnServer);
        }

        db = null;
        /* If it is a manual sync,there will be a listener,broadcast them*/
        if(bDevicePairing) {
            Intent i = new Intent(SYNC_STATUS);
            mContext.sendBroadcast(i);
            bDevicePairing = false;
        }
    }
}

