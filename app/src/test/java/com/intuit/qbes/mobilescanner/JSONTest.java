package com.intuit.qbes.mobilescanner;

import android.util.Log;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by ashah9 on 2/2/17.
 */


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(JUnit4.class)

public class JSONTest {

    private List<Picklist> mPicklists = null;
    private String resultpicklist;
    private String resultpicklisttoJSON;
    private String picklistfromJSON;



    @Before
    public void setUp() {

        resultpicklist = "[{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8793\",\"shipdate\":\"20211115\",\"status\":1}]";
        resultpicklisttoJSON = "{\"date\":\"20211105\",\"num\":\"8793\",\"_id\":26021,\"items\":[{\"uom\":\"ea\",\"picked\":0,\"name\":\"1500-PM\",\"_id\":26024,\"barcode\":\"6921734900210\",\"Needed\":5,\"to_pick\":0,\"desc\":\"POWER MANUAL 1500\"},{\"uom\":\"ea\",\"picked\":0,\"name\":\"Pool Covers:Cover -FG\",\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"Needed\":8,\"to_pick\":8,\"desc\":\"Pool Cover, Forest Green\"}],\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"shipdate\":\"20211115\",\"status\":1}";
        try {
            mPicklists = Picklist.picklistsFromJSON(resultpicklist);
        }
        catch (Exception exp)
        {
            Log.d("",exp.getMessage().toString());
        }

    }




    @Test
    public void ConvertFrom_TestJSON()
    {
        assertEquals(mPicklists.get(0).getRecnum(),26021);
        assertEquals(mPicklists.get(0).getShipDate().toString(),"Mon Nov 15 00:00:00 IST 2021");
        assertEquals(mPicklists.get(0).getOrderDate().toString(),"Fri Nov 05 00:00:00 IST 2021");
        assertEquals(mPicklists.get(0).getStatus(),1);
        assertEquals(mPicklists.get(0).getNumber(),"8793");
        assertEquals(mPicklists.get(0).getName(),"Dunning's Pool Depot, Inc.:Las Wages Store # 554");
        assertEquals(mPicklists.get(0).getLines().get(0).getName(),"1500-PM");
        assertEquals(mPicklists.get(0).getLines().get(0).getDescription(),"POWER MANUAL 1500");
        assertEquals(String.valueOf(mPicklists.get(0).getLines().get(0).getQtyNeeded()),"5.0");
        assertEquals(mPicklists.get(0).getLines().get(0).getRecNum(),26024);
        assertEquals(mPicklists.get(0).getLines().get(0).getBarcode(),"6921734900210");
        assertEquals(mPicklists.get(0).getLines().get(0).getUom(),"ea");
        assertEquals(String.valueOf(mPicklists.get(0).getLines().get(0).getQtyToPick()),"0.0");


    }

    @Test
    public void PicklistConvertTo_TestJSON()
    {
        try {
            picklistfromJSON = mPicklists.get(0).toJSON().toString();
            assertEquals(picklistfromJSON,resultpicklisttoJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}




