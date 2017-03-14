package com.intuit.qbes.mobilescanner;

import android.content.Intent;
import android.os.Build;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;
import com.intuit.qbes.mobilescanner.model.Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by ashah9 on 2/8/17.
 */

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class SqliteDatabaseTest {

    private DatabaseHandler db;
    private MainActivity activity;
    private Picklist picklist;
    List<LineItem> LineItems = new LinkedList<LineItem>();
    private LineItem lineitem;
    private List <Picklist> picklists;
    private List<SerialLotNumber> serialLotNumbers = new ArrayList<>();
    private SerialLotNumber serialLotNumber;

    @Before
    public void setup() throws  Exception
    {
        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        activity = Robolectric.buildActivity(MainActivity.class)
                .withIntent(intent)
                .create()
                .start()
                .visible()
                .get();

        db = new DatabaseHandler(activity);
        SerialLotNumber serialLotNumber = new SerialLotNumber(1,1,1,"A123");
        serialLotNumbers.add(serialLotNumber);
        lineitem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false", Status.NotPicked);
        lineitem.setSerialLotNumbers(serialLotNumbers);
        LineItems.add(0,lineitem);
        picklist = new Picklist(1, 1,1, "Picklist1",1,1,Status.NotPicked,1,"note1","show",1,"2017-01-10",LineItems,"false");
        picklist.setLineitems(LineItems);


    }

    @Test
    public void testPreConditions() {

        Assert.assertNotNull(db);
    }

    @Test
    public void test_addpicklist () throws Exception
    {

        db.addPickList(picklist);
        picklists = db.allPickLists(1,"default");
        Assert.assertEquals(picklists.get(0), picklist);
    }

    @Test
    public void test_updatepicklist () throws Exception
    {

        Picklist testpicklist = new Picklist(1, 1,1, "PicklistTest",1,1,Status.NotPicked,1,"note1","show",1,"2017-01-10",LineItems,"false");
        db.addPickList(picklist);
        db.updatePickList(testpicklist,1);
        picklists =  db.allPickLists(1,"default");
        Assert.assertEquals(picklists.get(0).getName(), "PicklistTest");
    }

    @Test
    public void test_delete_picklist()
    {

        db.addPickList(picklist);
        db.deleteOnePicklist(picklist.getId());
        picklists =  db.allPickLists(1,"default");

        Assert.assertEquals(picklists.size(),0);
    }

    @Test
    public void test_picklist_exists()
    {
        db.addPickList(picklist);
        boolean test = db.PickListExists(1);
        Assert.assertTrue(test);
    }

    @Test
    public void test_additem () throws Exception
    {

        db.addLineItem(lineitem,1);
        LineItems = db.allLineItems(1);
        Assert.assertEquals(LineItems.get(0), lineitem);
    }

    @Test
    public void test_updatelineitems()
    {
        List<SerialLotNumber> serialnum = new ArrayList<>();
        SerialLotNumber obj = new SerialLotNumber(1,1,1,"ser1");
        SerialLotNumber obj1 = new SerialLotNumber(1,1,1,"ser2");
        serialnum.add(obj);
        serialnum.add(obj1);
        LineItem testlineitem = new LineItem(1,1,1,"TestItem","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10,0,"8901238910005","Rack 1",12,"custom",serialnum,"true","true","false",Status.NotPicked);
        db.addLineItem(testlineitem,1);

        db.updateLineItems(testlineitem,1);
        LineItems = db.allLineItems(1);

        Assert.assertEquals(LineItems.get(0).getItemName(),"TestItem");


    }

    @Test
    public void test_deleteitem()
    {

        db.addLineItem(lineitem,1);
        db.deleteLineItems(1);

        LineItems = db.allLineItems(1);

        Assert.assertEquals(LineItems.size(),0);

    }

    @Test
    public void test_addPicklistWithDetail()
    {
        db.addPickListWithDetail(picklist);
        List<Picklist> picklistFromDb = db.getPicklistWithDetail(picklist.getId(),"default");
        Assert.assertEquals(picklistFromDb.get(0).getName(),"Picklist1");
        db.deletePicklistWithDetails(picklist.getId());
        Assert.assertEquals(db.allPickLists(1,"default").size(),0);
    }

    @Test
    public void test_addPicklistAndItsChildInOneTransaction()
    {
        db.addPickListInBatch(picklist,true);
        List<Picklist> picklistFromDb = db.getPicklistWithDetail(picklist.getId(),"default");
        Assert.assertEquals(picklistFromDb.get(0).getName(),"Picklist1");
        Picklist picklistTest = new Picklist(1, 1,1, "Picklist2",1,1,Status.NotPicked,1,"note1","show",1,"2017-01-10",LineItems,"false");
        db.addPickListInBatch(picklistTest,false);
        List<Picklist> picklistFromDb1 = db.getPicklistWithDetail(picklistTest.getId(),"default");
        Assert.assertEquals(picklistFromDb1.get(0).getName(),"Picklist2");
        db.batchDeletePicklist(picklist);
        Assert.assertEquals(db.allPickLists(1,"default").size(),0);


    }

    @Test
    public void test_getItemCountForPicklist()
    {
        db.addPickListInBatch(picklist,true);
        int itemCount = db.getItemCountForPicklist(picklist.getId());
        Assert.assertEquals(itemCount,1);

    }

    @Test
    public void test_SYNCAPI()
    {
        db.storeLastSycTime("TestGuid");
        String lastSyncTime = db.getlastSyncedUTCTime("TestGuid");
        Assert.assertNotNull(lastSyncTime);
    }

    @Test
    public void test_getPickList()
    {
        db.addPickList(picklist);
        Picklist pickListFromDb = db.getPickList(1,picklist.getId());
        Assert.assertEquals(pickListFromDb.getName(),"Picklist1");
    }
}
