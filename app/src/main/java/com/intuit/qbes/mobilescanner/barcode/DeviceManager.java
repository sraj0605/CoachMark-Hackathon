package com.intuit.qbes.mobilescanner.barcode;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.util.Log;

/**
 * Created by ckumar5 on 20/01/17.
 */
public class DeviceManager{

    private static DeviceManager mInstance = null;

    private  static BarcodeFactory mFactory = null;

    private  static BarcodeScannerDevice mDevice  = null;

    private DeviceManager(Context context) {
        setupDeviceForScan(context);
    }

    public static synchronized DeviceManager getDevice(Context context) {

        if(mInstance == null)
        {
            mInstance = new DeviceManager(context);
        }


        return  mInstance;
    }

    private  void setupDeviceForScan(Context context)
    {
        if(mFactory == null)
            mFactory = new BarcodeFactory();

        mDevice = mFactory.getDevice();

        if(mDevice != null)
            mDevice.initializeLibraryResource(context);

        //mFactory = null;

    }

    public void registerForCallback(BarcodeScannerDevice.ScanDataReceiver obj)
    {
        if(mDevice != null)
            mDevice.registerForCallback(obj);
    }

    public void unRegisterDeviceFromCallback(BarcodeScannerDevice.ScanDataReceiver obj)
    {
        if(mDevice != null)
            mDevice.unRegisterDeviceFromCallback(obj);

    }

    public void freeDeviceResource()
    {
        if(mDevice != null) {
            mDevice.releaseDevice();
            mInstance = null;
            mDevice = null;
        }

    }

    public void releaseScanner()
    {
        if(mDevice != null) {
            mDevice.releaseScanner();
        }
    }
}
