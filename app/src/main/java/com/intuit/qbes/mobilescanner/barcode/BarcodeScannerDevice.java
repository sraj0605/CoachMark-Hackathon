package com.intuit.qbes.mobilescanner.barcode;

import android.content.Context;

/**
 * Created by ckumar5 on 26/12/16.
 */

public interface BarcodeScannerDevice {
    Boolean initializeLibraryResource(Context context);

    public interface ScanDataReceiver
    {
        void scanDataReceived(String sData);
    }

    public void registerForCallback(ScanDataReceiver obj);

    public void unRegisterDeviceFromCallback(ScanDataReceiver obj);

    public void releaseDevice();

    public void releaseScanner();
}
