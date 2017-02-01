package com.intuit.qbes.mobilescanner;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

/**
 * Created by pdixit on 10/6/16.
 */
public class DetailPicklistActivity extends SingleFrameActivity implements DetailPicklistFragment1.Callbacks {

    private static final String LOG_TAG = "DetailPicklistActivity";

    private static final int REQUEST_DETAIL_ITEM = 1;

    @Override
    protected Fragment createFragment()
    {
        Picklist picklist = (Picklist) getIntent().getParcelableExtra(DetailPicklistFragment1.EXTRA_PICKLIST);
        return DetailPicklistFragment1.newInstance(picklist);
    }

    @Override
    public void onLineItemSelected(LineItem selectedLineItem,String barcodeEntered) {
        Intent intent = new Intent(this, ProductInfoActivity.class);
        intent.putExtra(ProductInfoFragment.EXTRA_LINEITEM, selectedLineItem);
        intent.putExtra(ProductInfoFragment.BARCODE_ENTERED,barcodeEntered);
        startActivityForResult(intent, REQUEST_DETAIL_ITEM);
    }

    @Override
    public void onPicklistSaved(Integer responseCode, Picklist picklist) {

        if (responseCode != 201 && responseCode != 200)
        {
            Snackbar.make(mCoordinatorLayout, "Failed", Snackbar.LENGTH_LONG).show();
        }
        else
        {
            Intent data = new Intent();
            data.putExtra(DetailPicklistFragment1.EXTRA_PICKLIST, picklist);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onBarcodeReady() {
        Snackbar.make(mCoordinatorLayout, "Press Hard Scan Button to start scanning...", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_DETAIL_ITEM:
                FragmentManager fm = getSupportFragmentManager();

                DetailPicklistFragment1 fragment = (DetailPicklistFragment1) fm.findFragmentById(R.id.flContent);
                if (fragment != null)
                {
                    try
                    {
                        LineItem obj = (LineItem) data.getParcelableExtra(DetailItemFragment.EXTRA_LINEITEM);
                        fragment.updateLineItemAndItsView((LineItem) data.getParcelableExtra(DetailItemFragment.EXTRA_LINEITEM));
                        //To Do- second condition is hack will be done in product info fragment
                        if((obj.getBarcodeEntered().compareTo("") !=0) && (obj.getBarcode().compareTo(obj.getBarcodeEntered()) !=0))
                            fragment.scanDataReceived(obj.getBarcodeEntered());

                    }
                    catch (IllegalArgumentException ex)
                    {
                        Log.e(LOG_TAG, ex.toString());
                    }
                }
                Snackbar.make(mCoordinatorLayout, "Saved", Snackbar.LENGTH_LONG).show();
                break;

            default:
                break;

        }
    }
}