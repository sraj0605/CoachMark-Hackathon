package com.intuit.qbes.mobilescanner;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.Picklist;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.List;

/**
 * Created by ashah9 on 2/7/17.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class ListPicklistFragmentTest {

    private Picklist picklist = new Picklist();
    private ListPicklistFragment listpicklistFragment;

    @Before
    public void setUp() throws Exception {


        listpicklistFragment = ListPicklistFragment.newInstance(picklist);
        //SupportFragmentTestUtil.startFragment(detailPicklistFragment1);
        SupportFragmentTestUtil.startVisibleFragment(listpicklistFragment);

    }

    @Test
    public void test_Fragment_Launched()
    {
        Assert.assertNotNull(listpicklistFragment);

    }

    @Test
    public void test_controller()
    {
        RecyclerView recycleview = (RecyclerView)listpicklistFragment.getView().findViewById(R.id.list_picklist_rv);
        Assert.assertNotNull(recycleview);
        recycleview.measure(0,0);
        recycleview.layout(0,0,100,1000);
        View itemView = recycleview.findViewHolderForAdapterPosition(0).itemView;
        Assert.assertNotNull(itemView);

        TextView picklistname = (TextView)itemView.findViewById(R.id.picklist_name);
        TextView picklistnote = (TextView)itemView.findViewById(R.id.picklist_note);
        TextView picklistitems = (TextView)itemView.findViewById(R.id.picklist_total_items);

        Assert.assertNotNull(picklistname);
        Assert.assertNotNull(picklistnote);
        Assert.assertNotNull(picklistitems);

       Assert.assertEquals(picklistname.getText().toString(),"Picklist1");
        Assert.assertEquals(picklistnote.getText().toString(),"Note: 1");
        Assert.assertEquals(picklistitems.getText().toString(),"35 items");


    }

    @Test
    public void test_adapter()
    {
        RecyclerView recycleview = (RecyclerView)listpicklistFragment.getView().findViewById(R.id.list_picklist_rv);

        int count = recycleview.getAdapter().getItemCount();

        Assert.assertEquals(count,5);
    }

    @Test
    public void test_createlist_dummy()
    {
        List<Picklist> result;
        result = listpicklistFragment.createList(5);
        Assert.assertEquals(result.size(), 5);
    }
}
