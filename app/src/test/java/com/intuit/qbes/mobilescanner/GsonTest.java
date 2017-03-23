package com.intuit.qbes.mobilescanner;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.Status;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckumar5 on 23/03/17.
 */

public class GsonTest {

    String str = "[{\"createdTimestamp\":\"2017-03-15 16:36:43\",\"lastModified\":\"2017-03-17 03:37:42\",\"id\":32,\"companyId\":\"666667\",\"taskType\":1,\"name\":\"picklist666667\",\"assigneeId\":14,\"createdById\":1,\"createdOn\":null,\"sortBy\":0,\"status\":100,\"siteId\":11,\"notes\":\"picklist-notes\",\"showNotes\":true,\"syncToken\":31,\"lineitems\":[{\"id\":44,\"taskId\":32,\"extId\":5000,\"itemName\":\"IPhone\",\"itemDesc\":\"IPhone 7\",\"lineitemPos\":1,\"docNum\":\"A-232\",\"txnId\":980980,\"txnDate\":\"2017-01-10\",\"shipDate\":\"2017-01-17\",\"notes\":\"lineitem-notes\",\"status\":9,\"uom\":\"ea\",\"qtyToPick\":109.0,\"qtyPicked\":0.0,\"barcode\":\"A659313\",\"binLocation\":\"Bin1\",\"binExtId\":5000,\"customFields\":null,\"serialLotNumbers\":[],\"deleted\":false,\"showSerialNo\":true,\"showLotNo\":false}]},{\"createdTimestamp\":\"2017-03-15 17:23:55\",\"lastModified\":\"2017-03-17 03:39:07\",\"id\":35,\"companyId\":\"666667\",\"taskType\":1,\"name\":\"picklist666668\",\"assigneeId\":15,\"createdById\":1,\"createdOn\":null,\"sortBy\":0,\"status\":3,\"siteId\":11,\"notes\":\"picklist-notes\",\"showNotes\":true,\"syncToken\":14,\"lineitems\":[{\"id\":47,\"taskId\":35,\"extId\":5001,\"itemName\":\"IPhone\",\"itemDesc\":\"IPhone 7\",\"lineitemPos\":1,\"docNum\":\"A-232\",\"txnId\":980980,\"txnDate\":\"2017-01-10\",\"shipDate\":\"2017-01-17\",\"notes\":\"lineitem-notes\",\"status\":5,\"uom\":\"ea\",\"qtyToPick\":109.0,\"qtyPicked\":4.0,\"barcode\":\"A659313\",\"binLocation\":\"Bin1\",\"binExtId\":5001,\"customFields\":null,\"serialLotNumbers\":[],\"deleted\":false,\"showSerialNo\":true,\"showLotNo\":false}]}]";
    String str1 = "{\"createdTimestamp\":\"2017-03-15 16:36:43\",\"lastModified\":\"2017-03-17 03:37:42\",\"id\":32,\"companyId\":\"666667\",\"taskType\":1,\"name\":\"picklist666667\",\"assigneeId\":14,\"createdById\":1,\"createdOn\":null,\"sortBy\":0,\"status\":100,\"siteId\":11,\"notes\":\"picklist-notes\",\"showNotes\":true,\"syncToken\":31,\"lineitems\":[{\"id\":44,\"taskId\":32,\"extId\":5000,\"itemName\":\"IPhone\",\"itemDesc\":\"IPhone 7\",\"lineitemPos\":1,\"docNum\":\"A-232\",\"txnId\":980980,\"txnDate\":\"2017-01-10\",\"shipDate\":\"2017-01-17\",\"notes\":\"lineitem-notes\",\"status\":9,\"uom\":\"ea\",\"qtyToPick\":109.0,\"qtyPicked\":0.0,\"barcode\":\"A659313\",\"binLocation\":\"Bin1\",\"binExtId\":5000,\"customFields\":null,\"serialLotNumbers\":[],\"deleted\":false,\"showSerialNo\":true,\"showLotNo\":false}]}";


    @Test
    public void picklistFromJSON()
    {
        List<Picklist> picklists = Picklist.picklistsFromJSON(str);
        Assert.assertEquals(picklists.size(),2);
    }

    @Test
    public void picklistFromjson()
    {
        Picklist picklist = null;
        picklist = Picklist.picklistFromJSON(str1);
        Assert.assertNotNull(picklist);
    }

    @Test
    public void jsonFromPickList()
    {
        Picklist picklist = new Picklist(1, "1",1, "Picklist1",1,1, Status.NotPicked,1,"note1","show",1,"2017-01-10",null,"false");
        String strTest = "";
        strTest = Picklist.JSONStringFromPicklist(picklist);
        Assert.assertNotEquals(strTest,"");

    }

    @Test
    public void jsonFromPickLists()
    {
        Picklist picklist = new Picklist(1, "1",1, "Picklist1",1,1, Status.NotPicked,1,"note1","show",1,"2017-01-10",null,"false");
        List<Picklist> picklists = new ArrayList<>();
        picklists.add(picklist);
        String strTest = "";
        strTest = Picklist.JSONStringArrayFromPicklistArray(picklists);
        Assert.assertNotEquals(strTest,"");

    }

    @Test
    public void jsonFromLineItems()
    {
        List<LineItem>lineitems = new ArrayList<>();
        LineItem lineItem = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false",Status.NotPicked);
        String strTest = "";
        strTest = LineItem.JSONStringArrayFromPicklistArray(lineitems);
        Assert.assertNotEquals(strTest,"");

    }

    @Test
    public void jsonFromLineItem()
    {
        LineItem lineItem = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10.2,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false",Status.NotPicked);
        String strTest = "";
        strTest = LineItem.JSONStringFromLineitem(lineItem);
        Assert.assertNotEquals(strTest,"");

    }

}
