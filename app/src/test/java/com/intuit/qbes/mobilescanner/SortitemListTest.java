package com.intuit.qbes.mobilescanner;

import com.intuit.qbes.mobilescanner.model.LineItem;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.intuit.qbes.mobilescanner.model.LineItem.Status.NOTPICKED;
import static junit.framework.Assert.assertTrue;

/**
 * Created by ashah9 on 2/8/17.
 */

public class SortitemListTest {

    private List<LineItem> mLineItemList;
    private SortFilterOption sortingOption;
    private LineItemListComparator lineItemListComparator = null;

    public void create_dummy()
    {
        mLineItemList = new ArrayList<>();
        LineItem lineItem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false",NOTPICKED);
        LineItem lineItem1  = new LineItem(1,1,1,"Phone","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false",NOTPICKED);
        sortingOption = SortFilterOption.Items;
        mLineItemList.add(lineItem);
        mLineItemList.add(lineItem1);
    }

    @Test
    public void test_SortItemList()
    {
        create_dummy();
        SortItemList objSorting = new SortItemList(mLineItemList,sortingOption);
        objSorting.performSort();
        assertTrue("Sorting went wrong",mLineItemList.get(0).getItemName().toString().equals("Phone"));
    }

}
