package com.intuit.qbes.mobilescanner;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.intuit.qbes.mobilescanner.model.LineItem.Status.NOTPICKED;
import static org.junit.Assert.assertEquals;

/**
 * Created by ashah9 on 2/1/17.
 */
public class LineItemmethodsTest {


    LineItem lineItem = new LineItem();
    private ArrayList<String> serialnos1 = new ArrayList<String>();



    @Before
    public void setUp() {
        serialnos1.add("s1");

        lineItem.setRecNum(1);
        lineItem.setName("Redmi");
        lineItem.setDescription("pick it");
        lineItem.setBarcode("RedmiBarcode");
        lineItem.setBarcodeEntered("");
        lineItem.setIsSNO(1);
        lineItem.setUom("1");
        lineItem.setQtyNeeded(10.0);
        lineItem.setQtyToPick(5.0);
        lineItem.setQtyPicked(1);
        lineItem.setSalesOrderId(110);
        lineItem.setSNArr(serialnos1);


    }

    @Test
    public void TestLineitem() throws Exception {

        ArrayList<String>  test = new ArrayList<String>();
        test.add("s1");
        assertEquals(lineItem.getRecNum(), 1);
        assertEquals(lineItem.getName(), "Redmi");
        assertEquals(lineItem.getDescription(), "pick it");
        assertEquals(lineItem.getBarcode(), "RedmiBarcode");
        assertEquals(lineItem.getBarcodeEntered(), "");
        assertEquals(lineItem.getIsSNO().intValue(), 1);
        assertEquals(lineItem.getUom(), "1");
        assertEquals(String.valueOf(lineItem.getQtyNeeded()),"10.0");
        assertEquals(String.valueOf(lineItem.getQtyPicked()), "1.0");
        assertEquals(String.valueOf(lineItem.getQtyToPick()), "5.0");
        assertEquals(lineItem.getSalesOrderId(), 110);
        assertEquals(lineItem.getSNArr(), test);


    }

}
