package com.intuit.qbes.mobilescanner;

import com.intuit.qbes.mobilescanner.model.LineItem;

import org.junit.Test;

import java.util.List;

import static com.intuit.qbes.mobilescanner.model.LineItem.Status.NOTPICKED;

/**
 * Created by ashah9 on 2/8/17.
 */

public class SortitemListTest {

    private List<LineItem> mLineItemList;
    private SortFilterOption sortingOption;
    private LineItemListComparator lineItemListComparator = null;

    public void create_dummy()
    {
        LineItem lineItem = new LineItem();
        lineItem  = new LineItem(1, "Redmi2", "pick it", "8901238910005", "",1,"1", 10.2, 0, 3, "abc", "_",110,NOTPICKED,null);
        sortingOption = SortFilterOption.Items;
        mLineItemList.add(0,lineItem);
    }

    @Test
    public void test_SortItemList()
    {
        SortItemList objSorting = new SortItemList(mLineItemList,sortingOption);
        objSorting.performSort();
    }

}
