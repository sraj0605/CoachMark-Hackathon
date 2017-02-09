package com.intuit.qbes.mobilescanner;

import android.content.Intent;
import android.os.Build;

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

public class SettingsActivityTest {

    private SettingsActivity settingsActivity;

    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(RuntimeEnvironment.application, SettingsActivity.class);
        settingsActivity = Robolectric.buildActivity(SettingsActivity.class)
                .withIntent(intent)
                .create()
                .start()
                .visible()
                .get();

    }

    @Test
    public void test_Activity_Launched()
    {
        Assert.assertNotNull(settingsActivity);

    }

}
