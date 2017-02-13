package com.intuit.qbes.mobilescanner;

import android.os.Build;

import com.intuit.qbes.mobilescanner.model.LineItem;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.intuit.qbes.mobilescanner.model.LineItem.Status.NOTPICKED;

/**
 * Created by ashah9 on 2/7/17.
 */

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class LineItemComparatorTest {

    private SortFilterOption sortingBy;
    private LineItem l1;
    private LineItem l2;

    @Test
    public void test_compare()
    {
        create_dummy();
        LineItemListComparator obj = new LineItemListComparator(sortingBy);
        Assert.assertTrue(obj.compare(l1,l2) < 0 );

    }

    public void create_dummy()
    {
        sortingBy = SortFilterOption.Items;
        l1  = new LineItem(1,1,1,"A","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false",NOTPICKED);
        l2  = new LineItem(1,1,1,"B","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false",NOTPICKED);

    }
}
