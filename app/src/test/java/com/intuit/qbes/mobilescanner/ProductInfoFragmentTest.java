package com.intuit.qbes.mobilescanner;

/**
 * Created by ckumar5 on 05/02/17.
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;
import com.intuit.qbes.mobilescanner.model.Status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
//import org.robolectric.shadows.support.v4.Shadows;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static com.intuit.qbes.mobilescanner.ProductInfoFragment.EXTRA_LINEITEM;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.util.FragmentTestUtil.startFragment;
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class ProductInfoFragmentTest{

    private ProductInfoActivity productInfoActivity;
    private ProductInfoFragment productInfoFragment;
    private LineItem mLineItem;
    private List<SerialLotNumber> serialnos1 = new ArrayList<SerialLotNumber>();
    ImageView mIncrement;
    EditText mQty_picked;
    ImageView mDecrement;
   // TextView mSerialNo;
    View view;

    @Before
    public void setUp() throws Exception {



    }
    public void createDummyModel(LineItem lineItem)
    {
        mLineItem  = lineItem;//new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);

    }

    public void startActivity(LineItem lineItem)
    {
        createDummyModel(lineItem);
        Intent intent = new Intent(RuntimeEnvironment.application, ProductInfoActivity.class);
        intent.putExtra(ProductInfoFragment.EXTRA_LINEITEM, mLineItem);
        intent.putExtra(ProductInfoFragment.BARCODE_ENTERED,"dummy");
        productInfoActivity = Robolectric.buildActivity(ProductInfoActivity.class)
                .withIntent(intent)
                .create()
                .start()
                .visible()
                .get();
        productInfoFragment = productInfoFragment.newInstance(mLineItem,"dummy");
        SupportFragmentTestUtil.startFragment(productInfoFragment);
        View view = productInfoFragment.getView();
        mIncrement = (ImageView)view.findViewById(R.id.increase);
        mDecrement = (ImageView)view.findViewById(R.id.decrease);
        mQty_picked = (EditText)view.findViewById(R.id.qty_picked_value);
    }

    @Test
    public void test_ActivityAndFragment_Launched()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        Assert.assertNotNull(productInfoActivity);
        Assert.assertNotNull(productInfoFragment);


    }
    @Test
    public void test_UI_Control_Validation_Against_Model()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        view = productInfoFragment.getView();
        TextView mUPC_Value =   (TextView)view.findViewById(R.id.item_upc) ;
        TextView mUPC_Header = (TextView)view.findViewById(R.id.UPC_code);
        TextView mSNO_Header = (TextView)view.findViewById(R.id.SNO);
        TextView mLocationHeader = (TextView)view.findViewById(R.id.location);
        ImageView mBin = (ImageView)view.findViewById(R.id.bin);
        TextView mUPC_ErrorText = (TextView)view.findViewById(R.id.UPC_Errortext);
        Button mConfirm = (Button)view.findViewById(R.id.button_confirm);
        TextView mSerialView = (TextView)view.findViewById(R.id.item_ViewSNO);
        View mVDivider = (View)view.findViewById(R.id.vdivider);
        View mHDivider = (View)view.findViewById(R.id.divider3);
        View error = (View)view.findViewById(R.id.errordivider);
        TextView mQty_picked_error = (TextView)view.findViewById(R.id.qty_picked_error);
        TextView mQty_picked_label = (TextView)view.findViewById(R.id.qty_picked);
        TextView mQtytoBePicked = (TextView) view.findViewById(R.id.item_qty);
        TextView mItemName = (TextView) view.findViewById(R.id.item_Name);
        TextView mItemDesc = (TextView) view.findViewById(R.id.item_description);
        TextView mItemLocation = (TextView) view.findViewById(R.id.item_location);
        TextView mItemSalesOrder = (TextView) view.findViewById(R.id.item_salesorder);

        assertNotNull(view);
        assertNotNull(mIncrement);
        assertNotNull(mDecrement);
        assertNotNull(mUPC_Value);
        assertNotNull(mUPC_Header);
        assertNotNull(mSNO_Header);
        assertNotNull(mLocationHeader);
        assertNotNull(mBin);
        assertNotNull(mUPC_ErrorText);
        assertNotNull(mConfirm);
       // assertNotNull(mSerialNo);
        assertNotNull(mSerialView);
        assertNotNull(mVDivider);
        assertNotNull(mHDivider);
        assertNotNull(error);
        assertNotNull(mQty_picked_error);
        assertNotNull(mQty_picked_label);
        assertNotNull(mQty_picked);

        assertTrue("UPC incorrect text",
                "dummy".equals(mUPC_Value.getText().toString()));
        assertTrue("qunatity to pick contains incorrect text",
                String.valueOf(mLineItem.getQtyToPick()).equals(mQtytoBePicked.getText().toString()));

        assertTrue("qunatity to pick contains incorrect text",
                String.valueOf(mLineItem.getQtyToPick()).equals(mQtytoBePicked.getText().toString()));

        assertTrue("Item Name contains incorrect text",
                mLineItem.getItemName().equals(mItemName.getText().toString()));

        assertTrue("Description contains incorrect text",
                mLineItem.getItemDesc().equals(mItemDesc.getText().toString()));

        assertTrue("Location contains incorrect text",
                mLineItem.getBinLocation().equals(mItemLocation.getText().toString()));

        //To - do sales order


    }

    @Test
    public void test_serialNumber_list_view_model() throws NullPointerException
    {
        //show serial number is true but list does not have any serial number
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        view = productInfoFragment.getView();
        TextView mSerialView = (TextView)view.findViewById(R.id.item_ViewSNO);
        TextView mSNO_Header = (TextView)view.findViewById(R.id.SNO);
        int serialViewVisibilty = mSerialView.getVisibility();
        int serialHeaderVisibilty = mSNO_Header.getVisibility();
        assertTrue("Serial number view is shown even if list does not have any serial number",serialViewVisibilty == 8);
        assertTrue("Serial number header is not shown when serial number is enabled",serialHeaderVisibilty == 0);
    }
    @Test
    public void test_serialNumber_list_view_model1() throws NullPointerException
    {
        //show serial number is true but list does not have any serial number
        List<SerialLotNumber> serialLotNumbers = new ArrayList<>();
        SerialLotNumber obj = new SerialLotNumber(1,1,1,"A6315");
        serialLotNumbers.add(obj);
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialLotNumbers,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        view = productInfoFragment.getView();
        TextView mSerialView = (TextView)view.findViewById(R.id.item_ViewSNO);
        TextView mSNO_Header = (TextView)view.findViewById(R.id.SNO);
        int serialViewVisibilty = mSerialView.getVisibility();
        int serialHeaderVisibilty = mSNO_Header.getVisibility();
        assertTrue("Serial number view is shown even if list does not have any serial number",serialViewVisibilty == 0);
        assertTrue("Serial number header is not shown when serial number is enabled",serialHeaderVisibilty == 0);
    }

    @Test
    public void test_serialNumber_list_view_model2() throws NullPointerException
    {
        //show serial number is true but list does not have any serial number
        List<SerialLotNumber> serialLotNumbers = new ArrayList<>();
        SerialLotNumber obj = new SerialLotNumber(1,1,1,"A6315");
        serialLotNumbers.add(obj);
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialLotNumbers,"false","false","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        view = productInfoFragment.getView();
        TextView mSerialView = (TextView)view.findViewById(R.id.item_ViewSNO);
        TextView mSNO_Header = (TextView)view.findViewById(R.id.SNO);
        int serialViewVisibilty = mSerialView.getVisibility();
        int serialHeaderVisibilty = mSNO_Header.getVisibility();
        assertTrue("Serial number view is shown even if list does not have any serial number",serialViewVisibilty == 8);
        assertTrue("Serial number header is not shown when serial number is enabled",serialHeaderVisibilty == 8);
    }

    @Test
    public void test_increment_button_functionality()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        mIncrement.performClick();
        assertTrue("quantity not incremented",String.valueOf(mLineItem.getQtyPicked()).equals("1.0"));

    }

    @Test
    public void test_decrement_button_functionality()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        mDecrement.performClick();
        assertTrue("quantity not incremented",String.valueOf(mLineItem.getQtyPicked()).equals("0.0"));


    }

    @Test
    public void test_serial_number_click_functionality()
    {
        List<SerialLotNumber> serialLotNumbers = new ArrayList<>();
        SerialLotNumber obj = new SerialLotNumber(1,1,1,"A6315");
        serialLotNumbers.add(obj);
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialLotNumbers,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        view = productInfoFragment.getView();
        TextView mSerialNumberView = (TextView)view.findViewById(R.id.item_SNO);
        mSerialNumberView.performClick();

    }

    @Test
    public void test_prodinfotosno()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        productInfoActivity.onSerialNumberClicked(mLineItem);

    }

    @Test
    public void test_confirm()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        view = productInfoFragment.getView();

        Button confirm = (Button)view.findViewById(R.id.button_confirm);

        confirm.performClick();

    }


    @Test
    public void test_updatetatusAsPicked()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        mLineItem.setQtyPicked(5.0);
        mLineItem.setQtyToPick(5.0);
        productInfoFragment.updateItemStatus();
        Assert.assertEquals(mLineItem.getmItemStatus(),Status.Picked);

    }
    @Test
    public void test_updatetatusAsNotPicked()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        mLineItem.setQtyPicked(0);
        mLineItem.setQtyToPick(5.0);
        productInfoFragment.updateItemStatus();
        Assert.assertEquals(mLineItem.getmItemStatus(),Status.NotPicked);

    }
    @Test
    public void test_updatetatusAsNotPartialPicked()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        mLineItem.setQtyPicked(2.0);
        mLineItem.setQtyToPick(5.0);
        productInfoFragment.updateItemStatus();
        Assert.assertEquals(mLineItem.getmItemStatus(),Status.PartiallyPicked);

    }
    @Test
    public void integer_decimal_functionality_test()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        double val = 10.2;
       boolean result =  Utilities.noDecimal(10.2);
        Assert.assertFalse(result);

        String qty = "10";
        boolean isInt = Utilities.isInteger(qty);
        Assert.assertTrue(isInt);
    }

    @Test
    public void test_onactivityresult()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        int reqcode = 300;
        int rescode = Activity.RESULT_OK;
        Intent data = new Intent();
        data.putExtra("lineitem", mLineItem);
        productInfoFragment.onActivityResult(reqcode,rescode,data);
    }

    @Test
    public void test_hideQuantityPickedWarning_behaviour()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        productInfoFragment.hideQuantityPickedWarning();
        view = productInfoFragment.getView();
        TextView mQty_picked_error = (TextView)view.findViewById(R.id.qty_picked_error);
        TextView mQty_picked_label = (TextView)view.findViewById(R.id.qty_picked);
        assertTrue("quantity picker error is not hidden",mQty_picked_error.getVisibility() == View.GONE);
    }

    @Test
    public void test_showQuantityPickedWarning_method()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        productInfoFragment.showQuantityPickedWarning();
        view = productInfoFragment.getView();
        TextView mQty_picked_error = (TextView)view.findViewById(R.id.qty_picked_error);
        TextView mQty_picked_label = (TextView)view.findViewById(R.id.qty_picked);
        assertTrue("quantity picker error is not hidden",mQty_picked_error.getVisibility() == View.VISIBLE);
    }

    @Test
    public void test_showHideBarcodeErrorHeader_method()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        productInfoFragment.showHideBarcodeErrorHeader();
        view = productInfoFragment.getView();
        View mUPC_background = (View)view.findViewById(R.id.UPC_Error);
        TextView mUPC_ErrorText = (TextView)view.findViewById(R.id.UPC_Errortext);
        assertTrue("showHideBarcodeErrorHeader method not working as expected",mUPC_background.getVisibility() == View.GONE);
        assertTrue("showHideBarcodeErrorHeader method not working as expected",mUPC_ErrorText.getVisibility() == View.GONE);

    }

    @Test
    public void test_showHideBarcodeErrorHeader_method1()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,1.2,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("");
        startActivity(mLineItem);
        productInfoFragment.showHideBarcodeErrorHeader();
        view = productInfoFragment.getView();
        View mUPC_background = (View)view.findViewById(R.id.UPC_Error);
        TextView mUPC_ErrorText = (TextView)view.findViewById(R.id.UPC_Errortext);
        assertTrue("showHideBarcodeErrorHeader method not working as expected",mUPC_background.getVisibility() == View.VISIBLE);
        assertTrue("showHideBarcodeErrorHeader method not working as expected",mUPC_ErrorText.getVisibility() == View.VISIBLE);

    }

    @Test
    public void test_UPC_ErrorCheck_method()
    {
        //IF BARCODE IS  ENTERED AND QUANTITY IS PICKED ERROR SHOULD NOT BE SHOWN
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,1.2,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("dummy");
        startActivity(mLineItem);
        productInfoFragment.UPC_ErrorCheck();
        view = productInfoFragment.getView();
        View mUPC_background = (View)view.findViewById(R.id.UPC_Error);
        TextView mUPC_ErrorText = (TextView)view.findViewById(R.id.UPC_Errortext);
        assertTrue("UPC_ErrorCheck method not working as expected",mUPC_background.getVisibility() == View.GONE);
        assertTrue("UPC_ErrorCheck method not working as expected",mUPC_ErrorText.getVisibility() == View.GONE);

    }

    @Test
    public void test_UPC_ErrorCheck_method1()
    {
        //IF BARCODE IS NOT ENTERED AND QUANTITY IS PICKED ERROR SHOULD BE SHOWN
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,1.2,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("");
        startActivity(mLineItem);
        productInfoFragment.UPC_ErrorCheck();
        view = productInfoFragment.getView();
        View mUPC_background = (View)view.findViewById(R.id.UPC_Error);
        TextView mUPC_ErrorText = (TextView)view.findViewById(R.id.UPC_Errortext);
        assertTrue("UPC_ErrorCheck method not working as expected",mUPC_background.getVisibility() == View.VISIBLE);
        assertTrue("UPC_ErrorCheck method not working as expected",mUPC_ErrorText.getVisibility() == View.VISIBLE);

    }

    @Test
    public void test_correct_scanDataReceived()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,1.2,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("8901238910005");
        startActivity(mLineItem);
        productInfoFragment.scanDataReceived("8901238910005");
        assertTrue("quantity not incremented on scan",String.valueOf(mLineItem.getQtyPicked()).equals("2.2"));

    }

    @Test
    public void test_wrong_scanDataReceived()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,1.2,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("8901238910005");
        startActivity(mLineItem);
        productInfoFragment.scanDataReceived("ABCD");
        assertTrue("quantity  incremented on wrong scan",String.valueOf(mLineItem.getQtyPicked()).equals("1.2"));

    }

    @Test
    public void test_onBackPressed()
    {
        //should show error - serial number and quantity not matching
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,1.2,"8901238910005","Rack 1",12,"custom",serialnos1,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("8901238910005");
        startActivity(mLineItem);
        productInfoFragment.onBackPressed();
    }

    @Test
    public void test_onBackPressed1()
    {
        //should not show error - serial number and quantity not matching
        List<SerialLotNumber> serialLotNumbers = new ArrayList<>();
        SerialLotNumber obj = new SerialLotNumber(1,1,1,"A6315");
        serialLotNumbers.add(obj);
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,1.2,"8901238910005","Rack 1",12,"custom",serialLotNumbers,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("8901238910005");
        startActivity(mLineItem);
        productInfoFragment.onBackPressed();
    }

    @Test

    public void test_upcdialog_shown()
    {
        try {

            List<SerialLotNumber> serialLotNumbers = new ArrayList<>();
            SerialLotNumber obj = new SerialLotNumber(1,1,1,"A6315");
            serialLotNumbers.add(obj);
            mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,1.2,"8901238910005","Rack 1",12,"custom",serialLotNumbers,"true","true","false", Status.NotPicked);
            mLineItem.setBarcodeEntered("ABCD");
            startActivity(mLineItem);

        }
        catch (Exception exp)
        {

        }
    }

    @Test
    public void test_onSerialNumberClicked()
    {
        List<SerialLotNumber> serialLotNumbers = new ArrayList<>();
        SerialLotNumber obj = new SerialLotNumber(1,1,1,"A6315");
        SerialLotNumber obj1 = new SerialLotNumber(1,1,1,"A6315");
        serialLotNumbers.add(obj);
        serialLotNumbers.add(obj1);
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",2,0,"8901238910005","Rack 1",12,"custom",serialLotNumbers,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("8901238910005");
        startActivity(mLineItem);
        productInfoActivity.onSerialNumberClicked(mLineItem);
        Class fragmentClass = null;
        fragmentClass = SerialNumberFragment.class;
        String tag = fragmentClass.getCanonicalName();
        SerialNumberFragment serialNumberFragment =(SerialNumberFragment) productInfoActivity.getSupportFragmentManager().findFragmentByTag(tag);
        Assert.assertNotNull(serialNumberFragment);
        Button confirm = (Button)serialNumberFragment.getView().findViewById(R.id.serialno_confirm);
        confirm.performClick();
        fragmentClass = ProductInfoFragment.class;
        tag = fragmentClass.getCanonicalName();
        productInfoFragment = (ProductInfoFragment) productInfoActivity.getSupportFragmentManager().findFragmentByTag(tag);
        Assert.assertNotNull(productInfoFragment);
    }

    @Test
    public void test_onProductInfoActivityBackPress()
    {
        List<SerialLotNumber> serialLotNumbers = new ArrayList<>();
        SerialLotNumber obj = new SerialLotNumber(1,1,1,"A6315");
        SerialLotNumber obj1 = new SerialLotNumber(1,1,1,"A6315");
        serialLotNumbers.add(obj);
        serialLotNumbers.add(obj1);
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",2,2,"8901238910005","Rack 1",12,"custom",serialLotNumbers,"true","true","false", Status.NotPicked);
        mLineItem.setBarcodeEntered("8901238910005");
        startActivity(mLineItem);
        productInfoActivity.onBackPressed();
        Assert.assertEquals(productInfoActivity.isFinishing(),true);

    }

    @Test
    public void test_noBarcodeModel1()
    {
        mLineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",2,2,"","Rack 1",12,"custom",null,"false","false","false", Status.NotPicked);
        //mLineItem.setBarcodeEntered("8901238910005");
        startActivity(mLineItem);
        productInfoActivity.onBackPressed();
        Assert.assertEquals(productInfoActivity.isFinishing(),true);
    }

    @After
    public void tearDown()
    {
        productInfoFragment.onDestroy();
    }
}
