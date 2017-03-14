package com.intuit.qbes.mobilescanner;

import java.math.BigDecimal;

/**
 * Created by ckumar5 on 28/02/17.
 */

public class Utilities{

    //Method will check whether quantity can get increneted or not,if yes it will increment quantitypicked
    public static  String checkAndIncrementQuantity(String qtyToPick,String qtyPicked)
    {
        boolean isInt  = isInteger(qtyPicked);
        if(isInt) {
            int val;
            val = Integer.parseInt(qtyPicked);
            val = val + 1;
            BigDecimal tempval = BigDecimal.valueOf(val);
            if (isInteger(qtyToPick)) {
                if (val <= Integer.parseInt(qtyToPick)) {
                    qtyPicked = String.valueOf(val);
                }

            } else {
                int comparison = tempval.compareTo(BigDecimal.valueOf(Double.parseDouble(qtyToPick)));
                if (comparison == -1 || comparison == 0) {
                    qtyPicked = String.valueOf(val);
                }
            }

        }
        else
        {
            BigDecimal val;
            val = BigDecimal.valueOf(Double.parseDouble(qtyPicked));
            val = val.add(BigDecimal.valueOf(1));

            int comparison;
            if(isInteger(qtyToPick))
            {
                comparison = val.compareTo(BigDecimal.valueOf(Integer.parseInt(qtyToPick)));
                if( comparison == -1 || comparison == 0) {
                    qtyPicked = String.valueOf(val);
                }


            }
            else {
                comparison = val.compareTo(BigDecimal.valueOf(Double.parseDouble(qtyToPick)));
                if (comparison == -1 || comparison == 0) {
                    qtyPicked = String.valueOf(val);
                }
            }
        }
        return  qtyPicked;
    }
    public static  String incrementQuantity(String qty)
    {
        boolean isInt  = isInteger(qty);
        if(isInt) {
            int val;
            val = Integer.parseInt(qty);
            val = val + 1;
            qty = String.valueOf(val);

        }
        else
        {
            BigDecimal val;
            val = BigDecimal.valueOf(Double.parseDouble(qty));
            val = val.add(BigDecimal.valueOf(1));
            qty = String.valueOf(val);
        }
        return  qty;
    }
    public static String decrementQuantity(String qty)
    {
        boolean isInt  = isInteger(qty);
        if(isInt)
        {
            int val;
            val = Integer.parseInt(qty);
            val = val - 1;
            if(val >= 0 )
            {
                qty = String.valueOf(val);
            }
        }
        else
        {
            BigDecimal val;
            val = BigDecimal.valueOf(Double.parseDouble(qty));
            val = val.subtract(BigDecimal.valueOf(1));
            int comparison = val.compareTo(BigDecimal.valueOf(0));
            if( comparison == 1 || comparison == 0)
            {
                qty = String.valueOf(val);

            }
        }

        return qty;
    }

    public static boolean isInteger(String val)
    {
        try {
            Integer.parseInt(val);
            return true;
        } catch (NumberFormatException nfe) {}
        return false;
    }

    public static boolean noDecimal(double qty)
    {
        if(qty%1 == 0)
            return true;
        else
            return false;
    }
}
