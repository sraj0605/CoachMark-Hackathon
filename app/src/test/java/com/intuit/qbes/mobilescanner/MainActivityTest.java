package com.intuit.qbes.mobilescanner;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.design.widget.NavigationView;


import com.intuit.qbes.mobilescanner.model.Picklist;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by ashah9 on 2/7/17.
 */

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class MainActivityTest {

    private Picklist picklist = new Picklist();
    private MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        mainActivity = Robolectric.buildActivity(MainActivity.class)
                .withIntent(intent)
                .create()
                .start()
                .visible()
                .get();

    }

    @Test
    public void test_Activity_Launched()
    {
        Assert.assertNotNull(mainActivity);

    }

    @Test
    public void test_onpickselect()
    {
        mainActivity.onPickSelected(picklist);
    }

    @Test
    public void test_navigationdrawer()
    {
        NavigationView nv = (NavigationView)mainActivity.findViewById(R.id.nvView);
        int count = nv.getMenu().size();
        Assert.assertEquals(count,3);

        Assert.assertEquals(nv.getMenu().getItem(0).getTitle(),"Home");
        Assert.assertEquals(nv.getMenu().getItem(1).getTitle(),"Settings");
        Assert.assertEquals(nv.getMenu().getItem(2).getTitle(),"Switch User");

        Configuration newConfig = new Configuration();

        mainActivity.onConfigurationChanged(newConfig);

    }


}
