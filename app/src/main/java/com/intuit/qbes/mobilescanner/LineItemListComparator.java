package com.intuit.qbes.mobilescanner;

import com.intuit.qbes.mobilescanner.model.LineItem;

import java.util.Comparator;

/**
 * Created by ckumar5 on 11/01/17.
 */

public class LineItemListComparator implements Comparator<LineItem> {

    private SortFilterOption sortingBy;

    LineItemListComparator(SortFilterOption sortingBy)
    {
        this.sortingBy = sortingBy;
    }

    @Override
    public int compare(LineItem lineItem1, LineItem lineItem2) {
        switch(sortingBy) {
            case Items:
            {
                if(lineItem1.getName() != null && lineItem2.getName() != null)
                    return lineItem1.getName().toLowerCase().compareTo(lineItem2.getName().toLowerCase());
                return 0;
            }
            case Location:
            {
                if(lineItem1.getBin() != null && lineItem2.getBin() != null)
                    return lineItem1.getBin().toLowerCase().compareTo(lineItem2.getBin().toLowerCase());
                return 0;
            }
            case SalesOrder:
            {
                return Long.valueOf(lineItem1.getSalesOrderId()).compareTo(Long.valueOf(lineItem2.getSalesOrderId()));
            }
            case Status:
            {
                //Chandan - To be decided
                return lineItem1.getItemStatus().compareTo(lineItem2.getItemStatus()) ;
            }
            default:
            {
                throw new RuntimeException("Practically unreachable code,Sorting By option passed is not supported");
            }
        }

    }
}

