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
        lineitem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false", Status.NotPicked);
        LineItems.add(0,lineitem);
        picklist = new Picklist(1, 1,1, "Picklist1",1,1,Status.NotPicked,1,"note1","show",1,"2017-01-10","2017-01-10",LineItems,"false");

    }

    @Test
    public void testPreConditions() {

        Assert.assertNotNull(db);
    }

    @Test
    public void test_addpicklist () throws Exception
    {

        db.addPickList(picklist);
        picklists = db.allPickLists();
        Assert.assertEquals(picklists.get(0), picklist);
    }

    @Test
    public void test_updatepicklist () throws Exception
    {

        Picklist testpicklist = new Picklist(1, 1,1, "PicklistTest",1,1,Status.NotPicked,1,"note1","show",1,"2017-01-10","2017-01-10",LineItems,"false");
        db.addPickList(picklist);
        db.updatePickList(testpicklist,1);
        picklists =  db.allPickLists();
        Assert.assertEquals(picklists.get(0).getName(), "PicklistTest");
    }

    @Test
    public void test_delete_picklist()
    {

        db.addPickList(picklist);
        db.deleteOnePicklist(picklist);
        picklists =  db.allPickLists();

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

}
