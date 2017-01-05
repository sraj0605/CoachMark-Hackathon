package com.intuit.qbes.mobilescanner.barcode;

import android.os.Build;

/**
 * Created by ckumar5 on 26/12/16.
 */

public class BarcodeFactory {

    public String LOG_STR = "BarcodeFactory";
    public BarcodeScannerDevice getDevice()
    {
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.contains("Motorola Solutions"))
        {
            return new EMDKDevice();
        }
        return null;
    }
}
