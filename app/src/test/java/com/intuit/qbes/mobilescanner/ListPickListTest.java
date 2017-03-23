package com.intuit.qbes.mobilescanner;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.Status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.robolectric.Shadows.shadowOf;

/**
 * Created by ashah9 on 2/7/17.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class ListPickListTest {

    private Picklist picklist = new Picklist();
    private ListPicklistFragment listpicklistFragment;
    @Before
    public void setUp() throws Exception {

        listpicklistFragment = ListPicklistFragment.newInstance(picklist);
        listpicklistFragment.createList(3);
        SupportFragmentTestUtil.startFragment(listpicklistFragment,MainActivity.class);
        //SupportFragmentTestUtil.startVisibleFragment(listpicklistFragment);
    }

    @Test
    public void test_Fragment_Launched()
    {
        Assert.assertNotNull(listpicklistFragment);

    }

    @Test

    public void test_controller()
    {
        try {
            RecyclerView recycleview = (RecyclerView) listpicklistFragment.getView().findViewById(R.id.list_picklist_rv);
            Assert.assertNotNull(recycleview);
            recycleview.measure(0, 0);
            recycleview.layout(0, 0, 100, 1000);
            View itemView = recycleview.findViewHolderForAdapterPosition(0).itemView;
            Assert.assertNotNull(itemView);

            TextView picklistname = (TextView) itemView.findViewById(R.id.picklist_name);
            TextView picklistnote = (TextView) itemView.findViewById(R.id.picklist_note);
            TextView picklistitems = (TextView) itemView.findViewById(R.id.picklist_total_items);

            Assert.assertNotNull(picklistname);
            Assert.assertNotNull(picklistnote);
            Assert.assertNotNull(picklistitems);

            Assert.assertEquals(picklistname.getText().toString(), "Order number: 8804");
            Assert.assertEquals(picklistnote.getText().toString(), "note1");
            Assert.assertEquals(picklistitems.getText().toString(), "0 item(s)");
            int count = recycleview.getAdapter().getItemCount();
            Assert.assertEquals(count, 3);
            itemView.performClick();
        }
        catch (Exception exp)
        {

        }


    }

    @Test
    public void test_refreshUI()
    {
        listpicklistFragment.updatePickList();
        listpicklistFragment.refreshUI();
        RecyclerView recycleview = (RecyclerView) listpicklistFragment.getView().findViewById(R.id.list_picklist_rv);
        Assert.assertNotNull(recycleview);
        recycleview.measure(0, 0);
        recycleview.layout(0, 0, 100, 1000);
        Assert.assertEquals(recycleview.getAdapter().getItemCount(),2);


    }

    @Test
    public void test_callback()
    {
        LineItem P1obj1 = new LineItem(1,1,1,"Anchor - 12x1","Anchor, 12x1 RedCap",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",35,0,"1258654257595","Rack 1",12,"custom",null,"false","false","false", Status.NotPicked);
        List<LineItem> lineItems = new ArrayList<>();
        lineItems.add(P1obj1);
        Picklist p1 = new Picklist(1, "1",1, "Order number: 8804",1,1, Status.NotPicked,1,"note1","show",1,"2017-01-10",lineItems,"false");
        listpicklistFragment.onClickCallback(p1);
    }

   /* @Test
    public void test_adapter()
    {
        try {
            RecyclerView recycleview = (RecyclerView) listpicklistFragment.getView().findViewById(R.id.list_picklist_rv);

            int count = recycleview.getAdapter().getItemCount();

            Assert.assertEquals(count, 3);
        }
        catch (Exception exp)
        {

        }
    }*/

    @Test
    public void test_createlist_dummy()
    {
        List<Picklist> result;
        result = listpicklistFragment.createList(3);
        Assert.assertEquals(result.size(), 3);
    }

    @After
    public void tearDown()
    {
        listpicklistFragment.onDestroy();
    }
}
