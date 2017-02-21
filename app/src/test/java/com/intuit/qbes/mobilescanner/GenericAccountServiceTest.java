package com.intuit.qbes.mobilescanner;

/**
 * Created by ckumar5 on 13/02/17.
 */
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.Account.GenericAccountService;
import com.intuit.qbes.mobilescanner.model.LineItem;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;
import org.robolectric.util.ServiceController;

import java.util.ArrayList;




@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class GenericAccountServiceTest {

    private GenericAccountService service;
    private ServiceController<GenericAccountService> controller;

    @Before
    public void setUp() {
        controller = Robolectric.buildService(GenericAccountService.class);
        service = controller.attach().create().get();
    }
    @Test
    public void test_Start_GenericAccountService()
    {
        Intent intent = new Intent(RuntimeEnvironment.application, GenericAccountService.class);
        controller.withIntent(intent).startCommand(0, 0);
    }

    @After
    public void tearDown() {
        controller.destroy();
    }
}
