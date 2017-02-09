package com.intuit.qbes.mobilescanner;

import android.os.Build;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

/**
 * Created by ashah9 on 2/7/17.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class TablayoutFragmentTest {


    private TabLayoutFragment tablayoutFragment;

    @Before
    public void setUp() throws Exception {


        tablayoutFragment = new TabLayoutFragment();

        SupportFragmentTestUtil.startVisibleFragment(tablayoutFragment);

    }

    @Test
    public void test_Fragment_Launched()
    {
        Assert.assertNotNull(tablayoutFragment);


    }
}
