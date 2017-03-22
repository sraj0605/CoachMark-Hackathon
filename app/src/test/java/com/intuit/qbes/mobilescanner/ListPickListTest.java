package com.intuit.qbes.mobilescanner;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.Picklist;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;
import org.junit.runner.RunWith;

import java.util.List;

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
            listpicklistFragment.refreshUI();
        }
        catch (Exception exp)
        {

        }


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
