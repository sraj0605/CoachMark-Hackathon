package com.intuit.qbes.mobilescanner;

/**
 * Created by ckumar5 on 03/02/17.
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
public class DetailPickListFragment1Test {

    private DetailPicklistActivity detailPicklistActivity;
    private DetailPicklistFragment1 detailPicklistFragment1;
    private List<LineItem> mLineitems;
    private LineItem mLineItem;
    private Picklist mPickList;
    @Before
    public void setUp() throws Exception {
        createDummyModel();
        Intent intent = new Intent(RuntimeEnvironment.application, DetailPicklistActivity.class);
        intent.putExtra(DetailPicklistFragment1.EXTRA_PICKLIST, mPickList);
        detailPicklistActivity = Robolectric.buildActivity(DetailPicklistActivity.class)
                .withIntent(intent)
                .create()
                .start()
                .get();
        detailPicklistFragment1 = DetailPicklistFragment1.newInstance(mPickList);
        //SupportFragmentTestUtil.startFragment(detailPicklistFragment1);
        SupportFragmentTestUtil.startVisibleFragment(detailPicklistFragment1);

    }

    public void createDummyModel()
    {
        mLineitems = new ArrayList<>();
        mLineItem  = new LineItem(1, "Redmi2", "pick it", "8901238910005", "",1,"1", 10.2, 1, 10.2, "abc", "_",110,NOTPICKED,null);
        mLineitems.add(mLineItem);
        mPickList = new Picklist(mLineitems, 1, "Picklist1", "1", "20160929", "20160929", 1);

    }

    @Test
    public void test_ActivityAndFragment_Launched()
    {
        Assert.assertNotNull(detailPicklistActivity);
        Assert.assertNotNull(detailPicklistFragment1);


    }

    @Test
    public void test_UI_Control_Validation_Against_Model()
    {
        RecyclerView recycleview = (RecyclerView)detailPicklistFragment1.getView().findViewById(R.id.detail_picklist_rv2);
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
                mLineItem.getName().toString().equals(itemName.getText().toString()));
        assertTrue("Description Nmae contains incorrect text",
                mLineItem.getDescription().toString().equals(itemDescription.getText().toString()));

        String locationFormat = String.format("Bin No : %s",mLineItem.getBin().toString());
        String salesOrderFormat = String.format("Sales Order: %d",mLineItem.getSalesOrderId());
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
        TextView sortby = (TextView) detailPicklistFragment1.getView().findViewById(R.id.sortby);
        junit.framework.Assert.assertNotNull(sortby);
    }



}
