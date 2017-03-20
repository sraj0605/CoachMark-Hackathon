package com.intuit.qbes.mobilescanner;

/**
 * Created by ckumar5 on 09/02/17.
 */

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.intuit.qbes.mobilescanner.Account.GenericAccountService;
import com.intuit.qbes.mobilescanner.model.CompanyFileDetails;
import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;
import com.intuit.qbes.mobilescanner.networking.DataSync;
import com.intuit.qbes.mobilescanner.networking.PicklistHttp;

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
class SyncAdapter extends AbstractThreadedSyncAdapter{
    public static final String TAG = "SyncAdapter";
    public static final String SYNC_STATUS = "SyncStatus";
    public static final String url = "http://172.16.100.28:9999/api/v1/company/666667/tasks/";
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private boolean bManualSync = false;
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

        List<Picklist> picklists = null;

        if (extras.getBoolean("MANUAL_SYNC") == true)//device pairing sync
            bManualSync = true;

        //get the last sync utc time
        DatabaseHandler db = new DatabaseHandler(mContext);
        CompanyFileDetails companyFileDetails = db.getDetails();
        String companyId;
        if(companyFileDetails != null)
        {
            companyId = String.valueOf(db.getDetails().getRealmID());
            if(companyId == null)
                return;
        }
        else
        {
            Log.i(TAG,"Device is not yet paired");
            syncResult.stats.numIoExceptions++;
            return;
            //companyId = "3e76df0599af48ceba2b895540a7f782";
        }
        String lastSyncTime = null;//db.getlastSyncedUTCTime(companyId);

        //get the tasks which is modified after last sync time
        DataSync obj = new DataSync();

        try {
            String taskUrl = Utilities.constructURL(DataSync.taskURL,companyId);
            picklists = obj.getTasksSynchronously(Request.Method.GET,taskUrl,1,lastSyncTime);

        }
        catch (IOException exp)
        {
            Log.i(TAG,"Unable to reach server");
            syncResult.stats.numIoExceptions++;
        }

        if(picklists != null) {
            int size = picklists.size();
            Log.i(TAG,String.valueOf(size));

            if(updateDevice(picklists))
                db.storeLastSycTime(companyId);
        }
        else
        {
            //server has not given any delta or task
            if (bManualSync) {
                Log.i("SyncAdapter","calling refresh-1");
                Intent i = new Intent(SYNC_STATUS);
                i.putExtra("NEW_TASK_FOUND",false);
                mContext.sendBroadcast(i);
                bManualSync = false;
            }
        }

        Log.i(TAG, "Finished network synchronization");
    }



    public boolean updateDevice(List<Picklist>picklistsFromServer)
    {
        boolean bret = false;
        try {
            DatabaseHandler db = new DatabaseHandler(mContext);
            for (int i = 0; i < picklistsFromServer.size(); i++) {
                Picklist picklistOnServer = picklistsFromServer.get(i);

                if (db.PickListExists(picklistOnServer.getId())) {//Server Picklist already exist in device,delete it and add gain

                    db.batchDeletePicklist(picklistOnServer);
                }


                db.addPickListInBatch(picklistOnServer, true);

                bret = true;
            }
            db = null;
        /* If it is a manual sync,there will be a listener,broadcast them*/
            if (bManualSync) {
                Log.i("SyncAdapter","calling refresh");
                Intent i = new Intent(SYNC_STATUS);
                i.putExtra("NEW_TASK_FOUND",true);
                mContext.sendBroadcast(i);
                bManualSync = false;
            }
        }
        catch (Exception exp)
        {
            bret = false;
        }
        return bret;
    }

}

