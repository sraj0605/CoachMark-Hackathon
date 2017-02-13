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
                if(lineItem1.getItemName() != null && lineItem2.getItemName() != null)
                    return lineItem1.getItemName().toLowerCase().compareTo(lineItem2.getItemName().toLowerCase());
                return 0;
            }
            case Location:
            {
                if(lineItem1.getBinLocation() != null && lineItem2.getBinLocation() != null)
                    return lineItem1.getBinLocation().toLowerCase().compareTo(lineItem2.getBinLocation().toLowerCase());
                return 0;
            }
            case SalesOrder:
            {
                if(lineItem1.getDocNum() != null && lineItem2.getDocNum() != null)
                    return lineItem1.getDocNum().toLowerCase().compareTo(lineItem2.getDocNum().toLowerCase());
                return 0;
            }
            case Status:
            {
                //Chandan - To be decided
                return lineItem1.getmItemStatus().compareTo(lineItem2.getmItemStatus()) ;
            }
            default:
            {
                throw new RuntimeException("Practically unreachable code,Sorting By option passed is not supported");
            }
        }

    }
}

