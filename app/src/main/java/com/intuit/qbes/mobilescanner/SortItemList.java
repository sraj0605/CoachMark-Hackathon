package com.intuit.qbes.mobilescanner;

import com.intuit.qbes.mobilescanner.model.LineItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ckumar5 on 11/01/17.
 */

public class SortItemList {

    private List<LineItem> mLineItemList;

    private SortFilterOption sortingOption;

    private LineItemListComparator lineItemListComparator = null;

    public SortItemList(List<LineItem> lineItemList,SortFilterOption sortingOption)
    {
        this.mLineItemList = lineItemList;
        this.sortingOption = sortingOption;

    }

    public void performShort()
    {
        try {
            this.lineItemListComparator = new LineItemListComparator(sortingOption);
            Collections.sort(mLineItemList, lineItemListComparator);
        }
        catch (RuntimeException exception)
        {

        }
    }

}
