package com.intuit.qbes.mobilescanner;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.BuildConfig;
import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class TimeOutFragmentTest {

    private TimeoutFragment mTimeOutFragment = TimeoutFragment.newInstance();

    @Before
    public void setUp() throws Exception {
        SupportFragmentTestUtil.startFragment(mTimeOutFragment);
    }

    @Test
    public void test_fragment_launched()
    {
        Assert.assertNotNull(mTimeOutFragment);
    }

    @Test
    public void test_view()
    {
        View view = mTimeOutFragment.getView();
        Assert.assertNotNull(view);
        Button mTryAgain = (Button) view.findViewById(R.id.timeout_tryagain);
        Assert.assertNotNull(mTryAgain);
    }

    @Test
    public void test_tryAgain_button()
    {
        View view = mTimeOutFragment.getView();
        Button mTryAgain = (Button) view.findViewById(R.id.timeout_tryagain);
        mTryAgain.performClick();
    }

    @After
    public void tearDown()
    {
        mTimeOutFragment.onDestroy();
    }
}
