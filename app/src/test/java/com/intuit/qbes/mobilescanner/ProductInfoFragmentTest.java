package com.intuit.qbes.mobilescanner;

/**
 * Created by ckumar5 on 05/02/17.
 */
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
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
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static com.intuit.qbes.mobilescanner.model.LineItem.Status.NOTAVAILABLE;
import static com.intuit.qbes.mobilescanner.model.LineItem.Status.NOTPICKED;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import static org.robolectric.util.FragmentTestUtil.startFragment;
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class ProductInfoFragmentTest{

    private ProductInfoActivity productInfoActivity;
    private ProductInfoFragment productInfoFragment;
    private LineItem mLineItem;
    ImageView mIncrement;
    EditText mQty_picked;
    ImageView mDecrement;
    TextView mSerialNo;
    View view;

    @Before
    public void setUp() throws Exception {
        createDummyModel();
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
        SupportFragmentTestUtil.startVisibleFragment(productInfoFragment);
        View view = productInfoFragment.getView();
        mIncrement = (ImageView)view.findViewById(R.id.increase);
        mDecrement = (ImageView)view.findViewById(R.id.decrease);
        mQty_picked = (EditText)view.findViewById(R.id.qty_picked_value);
        mSerialNo = (TextView) view.findViewById(R.id.item_SNO);

    }
    public void createDummyModel()
    {
        mLineItem  = new LineItem(1, "Redmi2", "pick it", "8901238910005", "",1,"1", 10.2, 1.2, 10.2, "abc", "_",110,NOTPICKED,null);

    }

    @Test
    public void test_ActivityAndFragment_Launched()
    {
        Assert.assertNotNull(productInfoActivity);
        Assert.assertNotNull(productInfoFragment);


    }
    @Test
    public void test_UI_Control_Validation_Against_Model()
    {
        view = productInfoFragment.getView();
        EditText mUPC_Value =   (EditText)view.findViewById(R.id.item_upc) ;
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
        assertNotNull(mSerialNo);
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
                mLineItem.getName().equals(mItemName.getText().toString()));

        assertTrue("Description contains incorrect text",
                mLineItem.getDescription().equals(mItemDesc.getText().toString()));

        assertTrue("Location contains incorrect text",
                mLineItem.getBin().equals(mItemLocation.getText().toString()));

        //To - do sales order


    }

    @Test
    public void test_increment_button_functionality()
    {
        mIncrement.performClick();

    }

    @Test
    public void test_decrement_button_functionality()
    {
        mDecrement.performClick();

    }

}
