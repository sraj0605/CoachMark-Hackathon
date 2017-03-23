package com.intuit.qbes.mobilescanner;

import android.os.Build;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.AccessibilityChecks;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

/**
 * Created by ckumar5 on 23/03/17.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class GetStartedFragmentTest {

    private GetStartedFragment getStartedFragment = GetStartedFragment.newInstance();

    @Before
    public void setUp() throws Exception {
        SupportFragmentTestUtil.startFragment(getStartedFragment);
    }

    @Test
    public void test_fragment_launched()
    {
        Assert.assertNotNull(getStartedFragment);
    }

    @After
    public void tearDown()
    {
        getStartedFragment.onDestroy();
    }

}
