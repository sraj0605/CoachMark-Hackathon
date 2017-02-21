package com.intuit.qbes.mobilescanner;


import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.Status;

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

        picklist.setId(1);
        picklist.setCompanyId(1);
        picklist.setName("Picklist1");
        picklist.setTaskType(1);
        picklist.setAssigneeId(12);
        picklist.setCreatedById(12);
        picklist.setStatus(Status.NotPicked);
        picklist.setSiteId(1);
        picklist.setNotes("notes");
        picklist.setShowNotes(true);
        picklist.setSyncToken(1);
        picklist.setDeleted(true);
    }

    @Test
    public void TestPicklist() throws Exception {




    }



}