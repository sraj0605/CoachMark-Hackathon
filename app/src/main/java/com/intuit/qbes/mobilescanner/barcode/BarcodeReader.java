package com.intuit.qbes.mobilescanner.barcode;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.StatusData;

/**
 * Created by pdixit on 10/11/16.
 */
public class BarcodeReader implements EMDKManager.EMDKListener, Scanner.StatusListener {

    private static final String LOG_STR = "BarcodeReader";

    // Declare a variable to store EMDKManager object
    private EMDKManager mEMDKManager = null;

    // Declare a variable to store Barcode Manager object
    private BarcodeManager mBarcodeManager = null;

    // Declare a variable to hold scanner device to scan
    private Scanner mScanner = null;

    private BarcodeReaderReady mBarcodeReaderReady;

    public interface BarcodeReaderReady {
        void onBarcodeReaderReady();
    }

    public void initializeBarcodeReader(Context context)
    {
        // The EMDKManager object will be created and returned in the callback.
        EMDKResults results = EMDKManager.getEMDKManager(
                context, this);

        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Log.e(LOG_STR, "EMDKManager Request Failed");
        }
    }

    @Override
    public void onClosed() {
        if (this.mEMDKManager != null) {

            this.mEMDKManager.release();
            this.mEMDKManager = null;
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        mEMDKManager = emdkManager;

        if (mScanner == null) {
            mBarcodeManager = (BarcodeManager) this.mEMDKManager
                    .getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

            // Get default scanner defined on the device
            mScanner = mBarcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
            mScanner.addStatusListener(this);
            mScanner.triggerType = Scanner.TriggerType.HARD;
            try
            {
                mScanner.enable();
            }
            catch (ScannerException ex)
            {
                Log.e(LOG_STR, "Failed to enable scanner");
                Log.e(LOG_STR, ex.toString());
            }
        }

        if (mBarcodeReaderReady != null)
        {
            mBarcodeReaderReady.onBarcodeReaderReady();
        }
    }

    @Override
    public void onStatus(StatusData statusData) {
        new AsyncStatusUpdate().execute(statusData);
    }

    public void executeRead() throws ScannerException
    {
        mScanner.read();
    }

    public void registerBarcodeReaderReady(BarcodeReaderReady listener)
    {
        mBarcodeReaderReady = listener;
    }

    public void registerDataListener(Scanner.DataListener listener)
    {
        try {
            mScanner.addDataListener(listener);
            mScanner.read();
        }
        catch (ScannerException ex)
        {
            Log.e(LOG_STR, "Failed add data listener");
            Log.e(LOG_STR, ex.toString());
        }
    }

    public void unregisterDataListener(Scanner.DataListener listener)
    {
        mScanner.removeDataListener(listener);
    }

    public void close()
    {
        if (mScanner != null)
        {
            try {
                mScanner.removeStatusListener(this);
                mScanner.disable();
                mScanner = null;
            }
            catch (ScannerException ex)
            {
                Log.e(LOG_STR, "Failed disable scanner");
                Log.e(LOG_STR, ex.toString());
            }
        }

        if (mEMDKManager != null)
        {
            mEMDKManager.release();
            mEMDKManager = null;
        }
    }

    private class AsyncStatusUpdate extends AsyncTask<StatusData, Void, String> {

        @Override
        protected String doInBackground(StatusData... params) {
            String statusStr = "";
            // Get the current state of scanner in background
            StatusData statusData = params[0];
            StatusData.ScannerStates state = statusData.getState();
            // Different states of Scanner
            switch (state) {
                // Scanner is IDLE
                case IDLE:
                    statusStr = "The scanner enabled and its idle";
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

            // Return result to populate on UI thread
            return statusStr;
        }

        @Override
        protected void onPostExecute(String result) {
            // Update the status text view on UI thread with current scanner
            // state
            Log.i(LOG_STR, result);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
