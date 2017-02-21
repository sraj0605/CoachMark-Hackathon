package com.intuit.qbes.mobilescanner.barcode;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;
import java.util.ArrayList;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.StatusData;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.BarcodeManager.DeviceIdentifier;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.ScanDataCollection.ScanData;
import com.symbol.emdk.barcode.ScanDataCollection.LabelType;
import android.content.Context;
import com.symbol.emdk.barcode.Scanner.TriggerType;
import android.os.AsyncTask;
import java.util.ArrayList;
import android.os.Trace;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ckumar5 on 23/12/16.
 */

public class EMDKDevice implements BarcodeScannerDevice,EMDKManager.EMDKListener,Scanner.StatusListener,Scanner.DataListener,BarcodeManager.ScannerConnectionListener{

    public String LOG_STR = "EMDKDevice";
    //Client Interface which will receive Scanned Data
    private BarcodeScannerDevice.ScanDataReceiver scanDataRecieverObj = null;

    // Declare a variable to store EMDKManager object
    private EMDKManager emdkManager = null;

    // Declare a variable to store Barcode Manager object
    private BarcodeManager barcodeManager = null;

    // Declare a variable to hold scanner device to scan
    private Scanner scanner = null;

    //clear cache after 50 scan
    int dataLength = 0;

    //Final Scanned data which will be broadcasted to registered client
    String scanData = "";

    //Application context which will be set by client
    public Context context = null;

    public EMDKDevice()
    {
        //Putting a default constructor
    }
    //Barcode Interface method
    public Boolean initializeLibraryResource(Context context) {

        Boolean libraryInitialized = true;
        this.context = context;
        // The EMDKManager object will be created and returned in the callback.
        EMDKResults results = EMDKManager.getEMDKManager(
                context, this);
        // Check the return status of getEMDKManager and update the status Text
        // View accordingly
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            libraryInitialized = false;
        }
        return libraryInitialized;
    }
    //Barcode Interface method - Register a client
    public void registerForCallback(ScanDataReceiver obj)
    {
        scanDataRecieverObj = obj;
    }

    @Override
    public void unRegisterDeviceFromCallback(ScanDataReceiver obj) {
        //To - do
        if (obj instanceof EMDKDevice.ScanDataReceiver)
        {
            scanDataRecieverObj = null;
        }
    }

    //Barcode Interface method - Release EMDK resource
    public void releaseDevice()
    {
        if (scanner != null)
        {
            try {
                scanner.cancelRead();
                scanner.disable();
                scanner.removeStatusListener(this);
                scanner.removeDataListener(this);
                scanner = null;
            }
            catch (ScannerException ex)
            {
                Log.e(LOG_STR, "Failed disable scanner");
                Log.e(LOG_STR, ex.toString());
            }
        }
        barcodeManager.removeConnectionListener(this);
        //Release the EMDKmanager on Application exit.
        if (emdkManager != null) {
            emdkManager.release(EMDKManager.FEATURE_TYPE.BARCODE);
            emdkManager = null;
        }

    }

    @Override
    public void releaseScanner() {
        if (scanner != null)
        {
            try {
                scanner.cancelRead();
                scanner.disable();

                scanner.removeStatusListener(this);
                scanner.removeDataListener(this);

            }
            catch (ScannerException ex)
            {
                Log.e(LOG_STR, "Failed disable scanner");
                Log.e(LOG_STR, ex.toString());
            }
        }
        scanner = null;
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;

        try {
            // Call this method to enable Scanner and its listeners
            initializeScanner();
        } catch (ScannerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClosed() {
        if(this.emdkManager != null)
        {
            this.emdkManager.release();
            this.emdkManager = null;
        }

    }

    @Override
    public void onConnectionChange(ScannerInfo scannerInfo, BarcodeManager.ConnectionState connectionState) {

        Log.e("chandan","on connection listener");

    }

    @Override
    public void onStatus(StatusData statusData) {
        new AsyncStatusUpdate().execute(statusData);
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        new AsyncDataUpdate().execute(scanDataCollection);

    }
    // Method to initialize and enable Scanner and its listeners
    private void initializeScanner() throws ScannerException {
        if (scanner == null) {
            // Get the Barcode Manager object
            barcodeManager = (BarcodeManager) this.emdkManager
                    .getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
            barcodeManager.addConnectionListener(this);
            // Get default scanner defined on the device
            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
            // Add data and status listeners
            scanner.addDataListener(this);
            scanner.addStatusListener(this);
            // Hard trigger. When this mode is set, the user has to manually
            // press the trigger on the device after issuing the read call.
            scanner.triggerType = Scanner.TriggerType.HARD;
            // Enable the scanner
            try {
                scanner.enable();
            }
            catch (ScannerException exp)
            {
                Log.e("chandan",exp.getMessage());
            }
            // Starts an asynchronous Scan. The method will not turn ON the
            // scanner. It will, however, put the scanner in a state in which
            // the scanner can be turned ON either by pressing a hardware
            // trigger or can be turned ON automatically.
            scanner.read();
        }
    }
    // AsyncTask that configures the scanned data on background
// thread and updated the result on UI thread with scanned data and type of
// label
    private class AsyncDataUpdate extends
            AsyncTask<ScanDataCollection, Void, String> {

        @Override
        protected String doInBackground(ScanDataCollection... params) {

            // Status string that contains both barcode data and type of barcode
            // that is being scanned
            String statusStr = "";

            try {

                // Starts an asynchronous Scan. The method will not turn ON the
                // scanner. It will, however, put the scanner in a state in
                // which
                // the scanner can be turned ON either by pressing a hardware
                // trigger or can be turned ON automatically.
                if(!scanner.isReadPending())
                {
                    scanner.read();
                }
                ScanDataCollection scanDataCollection = params[0];

                // The ScanDataCollection object gives scanning result and the
                // collection of ScanData. So check the data and its status
                if (scanDataCollection != null
                        && scanDataCollection.getResult() == ScannerResults.SUCCESS) {

                    ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection
                            .getScanData();

                    // Iterate through scanned data and prepare the statusStr
                    for (ScanDataCollection.ScanData data : scanData) {
                        // Get the scanned data
                        String a = data.getData();
                        if(scanDataRecieverObj != null)
                        {
                            scanDataRecieverObj.scanDataReceived(a.toString());
                        }
                        else {
                            Log.e("LOG_STR","listener for scan data is null");
                        }
                        Log.e(LOG_STR, a.toString());
                        // Get the type of label being scanned
                        //LabelType labelType = data.getLabelType();
                        // Concatenate barcode data and label type
                        //statusStr = barcodeData + " " + labelType;
                    }
                }

            } catch (ScannerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // Return result to populate on UI thread
            return statusStr;
        }

        @Override
        protected void onPostExecute(String result) {
            // Update the dataView EditText on UI thread with barcode data and
            // its label type
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }


    }
    // AsyncTask that configures the current state of scanner on background
// thread and updates the result on UI thread
    private class AsyncStatusUpdate extends AsyncTask<StatusData, Void, String> {

        @Override
        protected String doInBackground(StatusData... params) {
            String statusStr = "";
            // Get the current state of scanner in background
            try {
                StatusData statusData = params[0];
                StatusData.ScannerStates state = statusData.getState();

                // Different states of Scanner
                switch (state) {
                    // Scanner is IDLE
                    case IDLE:
                        statusStr = "The scanner enabled and its idle";
                        scanner.read();
                        break;
                    // Scanner is SCANNING
                    case SCANNING:
                        statusStr = "Scanning..";
                        break;
                    // Scanner is waiting for trigger press
                    case WAITING:
                        statusStr = "Waiting for trigger press..";
                        break;
                    // Scanner is not enabled
                    case DISABLED:
                        statusStr = "Scanner is not enabled";
                        break;
                    default:
                        break;
                }
            }
            catch(ScannerException e)
            {
                Log.e(LOG_STR, "scaanner status exception");
            }

            // Return result to populate on UI thread
            Log.e(LOG_STR, statusStr.toString());
            return statusStr;
        }

        @Override
        protected void onPostExecute(String result) {
            // Update the status text view on UI thread with current scanner
            // state
            //statusTextView.setText(result);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }


    }
}
