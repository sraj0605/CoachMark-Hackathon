package com.intuit.qbes.mobilescanner;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;
import com.intuit.qbes.mobilescanner.model.Status;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;



import static org.junit.Assert.assertEquals;

/**
 * Created by ashah9 on 2/1/17.
 */
public class LineItemmethodsTest {


    LineItem lineItem = new LineItem();
    private List<SerialLotNumber> serialnos1 = new ArrayList<SerialLotNumber>();



    @Before
    public void setUp() {

        SerialLotNumber test = new SerialLotNumber(1,1,1,"s1");
        serialnos1.add(test);
        lineItem.setId(1);
        lineItem.setTaskId(2);
        lineItem.setExtId(3);
        lineItem.setItemName("Redmi");
        lineItem.setItemDesc("pick it");
        lineItem.setLineitemPos(1);
        lineItem.setDocNum("a123");
        lineItem.setTxnId(123);
        lineItem.setNotes("notes");
        lineItem.setUom("ea");
        lineItem.setQtyToPick(5.0);
        lineItem.setQtyPicked(1);
        lineItem.setBarcode("RedmiBarcode");
        lineItem.setBinLocation("rack");
        lineItem.setBinExtId(1);
        lineItem.setCustomFields("custom");
        lineItem.setBarcodeEntered("");
        lineItem.setShowSerialNo(true);
        lineItem.setShowLotNo(true);
        lineItem.setSerialLotNumbers(serialnos1);
        lineItem.setDeleted(true);
        lineItem.setmItemStatus(Status.Picked);
    }

    @Test
    public void TestLineitem() throws Exception {

        ArrayList<String>  test = new ArrayList<String>();
        test.add("s1");
        assertEquals(lineItem.getId(), 1);
        assertEquals(lineItem.getTaskId(), 2);
        assertEquals(lineItem.getExtId(), 3);
        assertEquals(lineItem.getItemName(), "Redmi");
        assertEquals(lineItem.getItemDesc(), "pick it");
        assertEquals(lineItem.getLineitemPos(), 1);
        assertEquals(lineItem.getDocNum(), "a123");
        assertEquals(lineItem.getTxnId(), 123);
        assertEquals(lineItem.getNotes(), "notes");
        assertEquals(lineItem.getUom(), "ea");
        assertEquals(lineItem.getBinLocation(), "rack");
        assertEquals(lineItem.getBinExtId(), 1);
        assertEquals(lineItem.getCustomFields(),"custom");
        assertEquals(lineItem.getBarcode(), "RedmiBarcode");
        assertEquals(lineItem.getBarcodeEntered(), "");
        assertEquals(String.valueOf(lineItem.getQtyPicked()), "1.0");
        assertEquals(String.valueOf(lineItem.getQtyToPick()), "5.0");
        assertEquals(lineItem.getSerialLotNumbers().get(0).getValue().toString(), "s1");

    }

}
