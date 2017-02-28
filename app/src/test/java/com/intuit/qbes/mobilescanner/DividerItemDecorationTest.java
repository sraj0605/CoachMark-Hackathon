package com.intuit.qbes.mobilescanner;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v7.widget.RecyclerView;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ashah9 on 2/9/17.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class DividerItemDecorationTest {

    private DetailPicklistActivity detailPicklistActivity;
    private TaskPickListFragment testPickListFragment;
    private Picklist mPickList;
    private List<LineItem> mLineitems;
    private DividerItemDecoration obj;
    private Canvas c = new Canvas();



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
        testPickListFragment = TaskPickListFragment.newInstance(mPickList);
        //SupportFragmentTestUtil.startFragment(detailPicklistFragment1);
        SupportFragmentTestUtil.startVisibleFragment(testPickListFragment);

    }

    public void createDummyModel()
    {
        mLineitems = new ArrayList<>();
        mPickList = new Picklist(1, 1,1, "Picklist1",1,1, Status.NotPicked,1,"note1","show",1,"2017-01-10",mLineitems,"false");

    }

    @Test
    public void test_item_decoration()
    {
        obj = new DividerItemDecoration(detailPicklistActivity,com.intuit.qbes.mobilescanner.DividerItemDecoration.VERTICAL_LIST);
    }

    @Test
    public void test_onDraw()
    {
        obj = new DividerItemDecoration(detailPicklistActivity,com.intuit.qbes.mobilescanner.DividerItemDecoration.VERTICAL_LIST);
        RecyclerView recycleview = (RecyclerView)testPickListFragment.getView().findViewById(R.id.detail_picklist_rv2);
        obj.onDraw(c,recycleview);
    }
}
