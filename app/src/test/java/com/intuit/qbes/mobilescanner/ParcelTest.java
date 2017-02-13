package com.intuit.qbes.mobilescanner;

import android.os.Parcel;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static com.intuit.qbes.mobilescanner.model.LineItem.Status.NOTPICKED;
import static org.junit.Assert.assertEquals;

/**
 * Created by ashah9 on 2/3/17.
 */



/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
//@RunWith(JUnit4.class)
@Config(constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)

public class ParcelTest {
    Picklist picklist;
    private ArrayList<LineItem> lineitems = new ArrayList<LineItem>();
    private ArrayList<String> serialnos1 = new ArrayList<String>();
    LineItem lineItem;
    private String resultpicklist;


    @Before
    public void setUp() {
        LineItem obj1 = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false",NOTPICKED);
        lineitems.add(obj1);
        picklist = new Picklist(1, 1,1, "Picklist1",1,1,1,1,"note1","show",1,"2017-01-10","2017-01-10",lineitems,"false");
        picklist.setNotes("TestNote");
        picklist.setTotalitems(100);
        lineItem = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false",NOTPICKED);
        resultpicklist = "[{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8793\",\"shipdate\":\"20211115\",\"status\":1}]";


    }



    @Test
    public void ParcelPicklist() throws Exception {

        Parcel parcel = Parcel.obtain();
        picklist.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Picklist ParcelOutputPiklist = Picklist.CREATOR.createFromParcel(parcel);
        assertEquals(picklist, ParcelOutputPiklist);


    }

    @Test
    public void ParcelLineItem() throws Exception {

        Parcel parcel = Parcel.obtain();
        lineItem.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        LineItem ParcelOutputPiklist = LineItem.CREATOR.createFromParcel(parcel);
        assertEquals(lineItem,ParcelOutputPiklist);


    }


}