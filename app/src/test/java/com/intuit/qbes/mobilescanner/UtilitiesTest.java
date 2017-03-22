package com.intuit.qbes.mobilescanner;

/**
 * Created by ckumar5 on 11/03/17.
 */
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Status;
import com.intuit.qbes.mobilescanner.networking.DataSync;

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
public class UtilitiesTest {

    @Test
    public void test_checkAndIncrementQuantity() {
        String qtyPicked = Utilities.checkAndIncrementQuantity("10", "5");
        assertTrue("checkAndIncrementQuantity - method failed", qtyPicked.equals("6"));
        qtyPicked = Utilities.checkAndIncrementQuantity("10", "10");
        assertTrue("checkAndIncrementQuantity - method failed", qtyPicked.equals("10"));
        qtyPicked = Utilities.checkAndIncrementQuantity("5.2", "3");
        assertTrue("checkAndIncrementQuantity - method failed", qtyPicked.equals("4"));
        qtyPicked = Utilities.checkAndIncrementQuantity("5.2", "3.2");
        assertTrue("checkAndIncrementQuantity - method failed", qtyPicked.equals("4.2"));
        qtyPicked = Utilities.checkAndIncrementQuantity("5.2", "4.3");
        assertTrue("checkAndIncrementQuantity - method failed", qtyPicked.equals("4.3"));
        qtyPicked = Utilities.checkAndIncrementQuantity("10", "4.3");
        assertTrue("checkAndIncrementQuantity - method failed", qtyPicked.equals("5.3"));
    }

    @Test
    public void test_IncrementQuantity() {
        String incrementedValue = Utilities.incrementQuantity("1");
        assertTrue("IncrementQuantity - method failed", incrementedValue.equals("2"));
        incrementedValue = Utilities.incrementQuantity("1.1");
        assertTrue("IncrementQuantity - method failed", incrementedValue.equals("2.1"));

    }

    @Test
    public void test_DecrementQuantity() {
        String incrementedValue = Utilities.decrementQuantity("10");
        assertTrue("IncrementQuantity - method failed", incrementedValue.equals("9"));
        incrementedValue = Utilities.decrementQuantity("10.1");
        assertTrue("IncrementQuantity - method failed", incrementedValue.equals("9.1"));

    }
    @Test
    public void test_isInteger()
    {
        boolean isInteger = Utilities.isInteger("1.2");
        assertTrue("isInteger - method failed", isInteger == false);
        isInteger = Utilities.isInteger("5");
        assertTrue("isInteger - method failed", isInteger == true);

    }

    @Test
    public void test_noDecimal()
    {
        boolean isDecimal = Utilities.noDecimal(1.2);
        assertTrue("noDecimal - method failed", isDecimal == false);
        isDecimal = Utilities.noDecimal(5);
        assertTrue("noDecimal - method failed", isDecimal == true);
    }

    @Test
    public void test_constructURL()
    {
        String actual = Utilities.constructURL(DataSync.taskURL,"1234");
        String expectedURL = DataSync.taskURL.concat("1234/").concat("tasks");
        assertTrue("construct url failed",expectedURL.equals(actual));

    }
}
