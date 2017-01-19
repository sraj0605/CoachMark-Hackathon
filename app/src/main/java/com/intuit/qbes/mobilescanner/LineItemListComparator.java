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
                    return lineItem1.getName().compareTo(lineItem2.getName());
                return 0;
            }
            case Location:
            {
                if(lineItem1.getBin() != null && lineItem2.getBin() != null)
                    return lineItem1.getBin().compareTo(lineItem2.getBin());
                return 0;
            }
            case SalesOrder:
            {
                //Chandan - To be decided
                if(lineItem1.getDescription() != null && lineItem2.getDescription() != null)
                    return lineItem1.getDescription().compareTo(lineItem2.getDescription());//chandan - to do sales order
                return 0;
            }
            case Status:
            {
                //Chandan - To be decided
                if(lineItem1.getDescription() != null && lineItem2.getDescription() != null)
                    return  lineItem1.getDescription().compareTo(lineItem2.getDescription());//chandan - to do
                return 0;
            }
        }
        throw new RuntimeException("Practically unreachable code,Sorting By option passed is not supported");
    }


}

