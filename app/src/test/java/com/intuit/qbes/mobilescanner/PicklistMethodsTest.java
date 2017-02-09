package com.intuit.qbes.mobilescanner;


import com.intuit.qbes.mobilescanner.model.Picklist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(JUnit4.class)


public class PicklistMethodsTest {
   Picklist picklist = new Picklist();



    @Before
    public void setUp() {

        picklist.setTotalItems(100);
        picklist.setRecnum(1);
        picklist.setName("Picklist1");
        picklist.setNumber("1");
        picklist.setOrderDate("20160929");
        picklist.setShipDate("20160929");
        picklist.setStatus(1);
        picklist.setNote("TestNote");
        picklist.setTotalItems(100);

    }

    @Test
    public void TestPicklist() throws Exception {

        assertEquals(picklist.getNote(), "TestNote");
        assertEquals(picklist.getTotalItems(), 100);
        assertEquals(picklist.getRecnum(), 1);
        assertEquals(picklist.getName(), "Picklist1");
        assertEquals(picklist.getNumber(), "1");
        assertEquals(picklist.getOrderDate().toString(), "Thu Sep 29 00:00:00 IST 2016");
        assertEquals(picklist.getShipDate().toString(), "Thu Sep 29 00:00:00 IST 2016");
        assertEquals(picklist.getStatus(), 1);


    }



}