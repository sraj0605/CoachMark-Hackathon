package com.intuit.qbes.mobilescanner;

/**
 * Created by ckumar5 on 03/02/17.
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.Shadows;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;
//import org.robolectric.shadows.support.v4.Shadows;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;
import org.robolectric.util.ActivityController;
import org.robolectric.util.FragmentTestUtil;

import java.util.ArrayList;
import java.util.List;

import static com.intuit.qbes.mobilescanner.ProductInfoFragment.EXTRA_LINEITEM;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import static org.robolectric.util.FragmentTestUtil.startFragment;
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class TaskPickListFragmentTest {

    private DetailPicklistActivity detailPicklistActivity;
    private TaskPickListFragment taskPickListFragment;
    private List<LineItem> mLineitems;
    private LineItem mLineItem;
    private Picklist mPickList;
    @Before
    public void setUp() throws Exception {
        createDummyModel();
        Intent intent = new Intent(RuntimeEnvironment.application, DetailPicklistActivity.class);
        intent.putExtra(TaskPickListFragment.EXTRA_PICKLIST, mPickList);
        detailPicklistActivity = Robolectric.buildActivity(DetailPicklistActivity.class)
                .withIntent(intent)
                .create()
                .start()
                .get();
        taskPickListFragment = TaskPickListFragment.newInstance(mPickList);
        //SupportFragmentTestUtil.startFragment(detailPicklistFragment1);
        SupportFragmentTestUtil.startVisibleFragment(taskPickListFragment);

    }

    public void createDummyModel()
    {
        mLineitems = new ArrayList<>();
        mLineItem = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false",Status.NotPicked);
        mLineitems.add(mLineItem);
        mPickList = new Picklist(1, 1,1, "Picklist1",1,1,Status.NotPicked,1,"note1","show",1,"2017-01-10",mLineitems,"false");

    }

    @Test
    public void test_ActivityAndFragment_Launched()
    {
        Assert.assertNotNull(detailPicklistActivity);
        Assert.assertNotNull(taskPickListFragment);


    }

    @Test
    public void test_UI_Control_Validation_Against_Model()
    {
        RecyclerView recycleview = (RecyclerView)taskPickListFragment.getView().findViewById(R.id.detail_picklist_rv2);
        Assert.assertNotNull(recycleview);
        recycleview.measure(0,0);
        recycleview.layout(0,0,100,1000);
        View itemView = recycleview.findViewHolderForAdapterPosition(0).itemView;
        Assert.assertNotNull(itemView);

        TextView itemName = (TextView) itemView.findViewById(R.id.itemName);
        TextView itemDescription = (TextView) itemView.findViewById(R.id.desc);
        TextView locationName = (TextView) itemView.findViewById(R.id.locationName);
        TextView salesOrder = (TextView) itemView.findViewById(R.id.salesOrderName);
        TextView quantityToPick = (TextView) itemView.findViewById(R.id.quantity);

        Assert.assertNotNull(itemName);
        Assert.assertNotNull(itemDescription);
        Assert.assertNotNull(locationName);
        Assert.assertNotNull(salesOrder);
        Assert.assertNotNull(quantityToPick);
        assertTrue("Item Name contains incorrect text",
                mLineItem.getItemName().toString().equals(itemName.getText().toString()));
        assertTrue("Description Nmae contains incorrect text",
                mLineItem.getItemDesc().toString().equals(itemDescription.getText().toString()));

        String locationFormat = String.format("Bin No : %s",mLineItem.getBinLocation().toString());
        String salesOrderFormat = String.format("Sales Order: %s",mLineItem.getDocNum());
        String  qtyTopickFormat = String.format("Qty : %s", mLineItem.getQtyToPick());
        String  qtyTopickFormat1 = quantityToPick.getText().toString();
        assertTrue(" Location contains incorrect text",
                locationFormat.equals(locationName.getText().toString()));
        assertTrue(" Sales Order contains incorrect text",
                salesOrderFormat.equals(salesOrder.getText().toString()));
        assertTrue(" qunatity to pick contains incorrect text",
                qtyTopickFormat.equals(quantityToPick.getText().toString()));
    }

    @Test
    public void test_sort_button_click()
    {
        TextView sortby = (TextView) taskPickListFragment.getView().findViewById(R.id.sortby);
        junit.framework.Assert.assertNotNull(sortby);

        TextView mSortOrderSelection = (TextView)taskPickListFragment.getView().findViewById(R.id.sortbyselection);
        mSortOrderSelection.performClick();

    }

@Test
    public void test_onitemselect()
    {
        String barcode ="8901238910005";
       detailPicklistActivity.onLineItemSelected(mLineItem,true);
    }


    @Test
    public void test_onpicksaved()
    {
        Integer code = 200;
        detailPicklistActivity.onPicklistSaved(code,mPickList);
    }

    @Test
    public void test_onbarcodeready()
    {
        detailPicklistActivity.onBarcodeReady();
    }

    @Test
    public void test_onactivityresult()
    {
        int reqcode = 1;
        int rescode = Activity.RESULT_OK;
        Intent data = new Intent();
        data.putExtra(EXTRA_LINEITEM, mLineItem);
        detailPicklistActivity.onActivityResult(reqcode,rescode,data);
    }

@Test
    public void test_msutils()
    {
        MSUtils.getServerUrl(detailPicklistActivity);
    }

    @Test
    public void test_updatelineitem()
    {
       LineItem testlineitem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false", Status.NotPicked);

        taskPickListFragment.updateLineItem(testlineitem);

        taskPickListFragment.updateViewAtPosition(0);
    }

    @Test
    public void app_back()
    {

        MenuItem menuItem = new RoboMenuItem(R.id.home);
        taskPickListFragment.onOptionsItemSelected(menuItem);
        Assert.assertEquals(taskPickListFragment.getFragmentManager().getBackStackEntryCount(),0);

    }

    @Test
    public void test_sort_selection()
    {
        taskPickListFragment.onSortingOptionSelection(SortFilterOption.Items);
    }

    @Test
    public void test_scandata_rec()
    {
        taskPickListFragment.scanDataReceived("yes");
    }

}
