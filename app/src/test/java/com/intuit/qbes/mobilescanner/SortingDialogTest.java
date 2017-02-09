package com.intuit.qbes.mobilescanner;

import android.os.Build;
import android.view.View;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;


/**
 * Created by ashah9 on 2/8/17.
 */

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class SortingDialogTest {

    private SortingDialog sortdialog;
    private DetailPicklistFragment1 fragment = new DetailPicklistFragment1();

    @Before
    public void setUp() throws Exception {

       sortdialog = new SortingDialog();
        sortdialog.setTargetFragment(fragment,300);

        SupportFragmentTestUtil.startFragment(sortdialog);

    }

    @Test
    public void test_Fragment_Launched()
    {
        Assert.assertNotNull(sortdialog);

    }

    @Test
    public void test_controllers()
    {
        View view = sortdialog.getView();
       TextView mSortByLocation = (TextView) view.findViewById(R.id.sortByLocation);
        TextView mSortByItems = (TextView) view.findViewById(R.id.sortByItems);
        TextView mSortByStatus = (TextView) view.findViewById(R.id.sortByStatus);
        TextView mSortBySalesOrder = (TextView) view.findViewById(R.id.sortBySalesOrder);

        Assert.assertNotNull(mSortByLocation);
        Assert.assertNotNull(mSortByItems);
        Assert.assertNotNull(mSortByStatus);
        Assert.assertNotNull(mSortBySalesOrder);


    }

    @Test
    public void test_onclick()
    {
        View view = sortdialog.getView();
        TextView mSortByLocation = (TextView) view.findViewById(R.id.sortByLocation);
        sortdialog.onClick(mSortByLocation);
    }

}
