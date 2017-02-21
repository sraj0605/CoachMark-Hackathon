package com.intuit.qbes.mobilescanner;

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;


/**
 * Created by ashah9 on 2/7/17.
 */

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class UPCFragmentTest {

    private UPCFragment upcfragment;
    private ProductInfoFragment fragment = new ProductInfoFragment();
    private LineItem lineitem;
    private ArrayList<String> serialnos1 = new ArrayList<String>();


    @Before
    public void setUp() throws Exception {

        create_dummy();
        upcfragment = new UPCFragment().newInstance(lineitem);
        //SupportFragmentTestUtil.startFragment(detailPicklistFragment1);
        SupportFragmentTestUtil.startFragment(upcfragment);

    }

    @Test
    public void test_Fragment_Launched()
    {
        Assert.assertNotNull(upcfragment);

    }

    @Test
    public void test_controllers()
    {
        TextView upc_header = (TextView)upcfragment.getView().findViewById(R.id.upc_scan_header);
        CustomEditText upc_value = (CustomEditText)upcfragment.getView().findViewById(R.id.upc_value);
        Button confirm = (Button)upcfragment.getView().findViewById(R.id.upc_confirm);

        Assert.assertNotNull(upc_header);
        Assert.assertNotNull(upc_value);
        Assert.assertNotNull(confirm);

    }

    @Test
    public void test_wrongUPC()
    {
        CustomEditText upc_value = (CustomEditText)upcfragment.getView().findViewById(R.id.upc_value);
        Button confirm = (Button)upcfragment.getView().findViewById(R.id.upc_confirm);
        TextView upc_errortext = (TextView) upcfragment.getView().findViewById(R.id.upc_error_text) ;
        View error_line = (View)upcfragment.getView().findViewById(R.id.UPCerrorline);

        upc_value.setText("89012389");
        Assert.assertNotNull(confirm);


        upcfragment.onClick(confirm);

        Assert.assertEquals(upc_errortext.getVisibility(),0);
        Assert.assertEquals(error_line.getVisibility(),0);


    }


    @Test
    public void test_correctUPC()
    {
        CustomEditText upc_value = (CustomEditText)upcfragment.getView().findViewById(R.id.upc_value);
        Button confirm = (Button)upcfragment.getView().findViewById(R.id.upc_confirm);
        TextView upc_errortext = (TextView) upcfragment.getView().findViewById(R.id.upc_error_text) ;
        View error_line = (View)upcfragment.getView().findViewById(R.id.UPCerrorline);


        upc_value.setText("8901238910005");
        Assert.assertNotNull(confirm);
        lineitem.setBarcodeEntered(upc_value.getText().toString());

        upcfragment.setTargetFragment(fragment,300);


        upcfragment.onClick(confirm);

        Assert.assertNotNull(upcfragment);


    }

@Test
public void test_dialogdismiss()
{

    upcfragment.Dismissdialog();

}

    @Test
    public void test_scan_data_received()
    {
        CustomEditText upc_value = (CustomEditText)upcfragment.getView().findViewById(R.id.upc_value);

        upcfragment.scanDataReceived("test");
        Assert.assertEquals(upc_value.getText().toString(),"test");

    }

    public void create_dummy()
    {
        lineitem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",10,0,"8901238910005","Rack 1",12,"custom",null,"true","true","false", Status.NotPicked);

    }

}
