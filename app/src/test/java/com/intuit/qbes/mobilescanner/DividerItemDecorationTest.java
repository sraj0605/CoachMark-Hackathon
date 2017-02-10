package com.intuit.qbes.mobilescanner;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v7.widget.RecyclerView;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

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

import static com.intuit.qbes.mobilescanner.model.LineItem.Status.NOTPICKED;

/**
 * Created by ashah9 on 2/9/17.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class DividerItemDecorationTest {

    private DetailPicklistActivity detailPicklistActivity;
    private DetailPicklistFragment1 detailPicklistFragment1;
    private Picklist mPickList;
    private List<LineItem> mLineitems;
    private DividerItemDecoration obj;
    private Canvas c = new Canvas();



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
        mPickList = new Picklist(mLineitems, 1, "Picklist1", "1", "20160929", "20160929", 1);

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
        RecyclerView recycleview = (RecyclerView)detailPicklistFragment1.getView().findViewById(R.id.detail_picklist_rv2);
        obj.onDraw(c,recycleview);
    }
}
