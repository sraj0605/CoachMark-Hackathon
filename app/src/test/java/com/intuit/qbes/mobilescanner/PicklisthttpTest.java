package com.intuit.qbes.mobilescanner;

import android.content.Intent;
import android.os.Build;

import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.networking.PicklistHttp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * Created by ashah9 on 2/7/17.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class PicklisthttpTest {

    private String urlspec = "https://www.google.com";

    PicklistHttp obj = new PicklistHttp();

    private Picklist mPickList = new Picklist();

    private DetailPicklistActivity detailPicklistActivity;

    byte[] test;

    @Before
    public void setup()
    {
        Intent intent = new Intent(RuntimeEnvironment.application, DetailPicklistActivity.class);
        intent.putExtra(DetailPicklistFragment1.EXTRA_PICKLIST, mPickList);
        detailPicklistActivity = Robolectric.buildActivity(DetailPicklistActivity.class)
                .withIntent(intent)
                .create()
                .start()
                .get();
    }
    @Test
    public void test_urlbyte()
    {
        try {
        test = obj.getUrlBytes(urlspec);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(test);
    }

    @Test
    public void test_puturl()
    {

        try {
            obj.putUrlString("%s/picklists/%d",MSUtils.getServerUrl(detailPicklistActivity),"1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

