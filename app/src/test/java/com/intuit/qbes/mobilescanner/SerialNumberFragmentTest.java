package com.intuit.qbes.mobilescanner;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;
import com.intuit.qbes.mobilescanner.model.Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by ashah9 on 2/6/17.
 */

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)

public class SerialNumberFragmentTest {

    private SerialNumberFragment serialnoFragment;
    private LineItem mlineitem;
    private SerialNumberFragment.SerialNumberAdapter obj;
    private List<SerialLotNumber> serialnos1 = new ArrayList<SerialLotNumber>();
    private Class fragmentClass = null;


    @Before
    public void setUp() throws Exception {

        createDummyModel();
        serialnoFragment = SerialNumberFragment.newInstance(mlineitem);
        //SupportFragmentTestUtil.startFragment(detailPicklistFragment1);
        SupportFragmentTestUtil.startVisibleFragment(serialnoFragment);

    }

    public void createDummyModel()
    {
        mlineitem  = new LineItem(1,1,1,"Redmi","pick it",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",1,0,"8901238910005","Rack 1",4,"custom",serialnos1,"true","true","false", Status.NotPicked);

    }

    @Test
    public void test_Fragment_Launched()
    {
        Assert.assertNotNull(serialnoFragment);


    }

    @Test
    public void test_UI_Control_Validation_Against_Model()
    {
        RecyclerView recycleview = (RecyclerView)serialnoFragment.getView().findViewById(R.id.serialnumber_rv);
        Assert.assertNotNull(recycleview);

        TextView item_header = (TextView)serialnoFragment.getView().findViewById(R.id.serialno_item);
        TextView item_name = (TextView)serialnoFragment.getView().findViewById(R.id.serialno_item_value);
        TextView sno_added = (TextView)serialnoFragment.getView().findViewById(R.id.serialno_added);
        TextView sno_added_value = (TextView)serialnoFragment.getView().findViewById(R.id.serialno_added_value);
        EditText sno_enter = (EditText)serialnoFragment.getView().findViewById(R.id.AddSno);
        ImageView plus = (ImageView)serialnoFragment.getView().findViewById(R.id.add_serialno);

        Assert.assertNotNull(item_header);
        Assert.assertNotNull(item_name);
        Assert.assertNotNull(sno_added);
        Assert.assertNotNull(sno_added_value);
        Assert.assertNotNull(sno_enter);
        Assert.assertNotNull(plus);


        Assert.assertEquals(mlineitem.getItemName(), item_name.getText().toString());
        int n = (int)mlineitem.getQtyToPick();
        int n1 = Integer.parseInt(sno_added_value.getText().toString());
        Assert.assertEquals((int)mlineitem.getQtyToPick(), Integer.parseInt(sno_added_value.getText().toString()));





    }

    @Test
    public void add_sno()
    {
        EditText sno_enter = (EditText)serialnoFragment.getView().findViewById(R.id.AddSno);
        ImageView plus = (ImageView)serialnoFragment.getView().findViewById(R.id.add_serialno);

        sno_enter.setText("TestSno");
        serialnoFragment.onClick(plus);

        RecyclerView recycleview = (RecyclerView)serialnoFragment.getView().findViewById(R.id.serialnumber_rv);

        recycleview.measure(0,0);
        recycleview.layout(0,0,100,1000);
        View itemView = recycleview.findViewHolderForAdapterPosition(0).itemView;
        Assert.assertNotNull(itemView);


        TextView sno_text = (TextView)itemView.findViewById(R.id.serialnumber_text);
        ImageView cancel = (ImageView)itemView.findViewById(R.id.remove_serialno);


        Assert.assertNotNull(sno_text);
        Assert.assertNotNull(cancel);

        Assert.assertEquals(recycleview.getAdapter().getItemCount(),1);


    }

    @Test
    public void extra_sno()
    {
        EditText sno_enter = (EditText)serialnoFragment.getView().findViewById(R.id.AddSno);
        ImageView plus = (ImageView)serialnoFragment.getView().findViewById(R.id.add_serialno);

        sno_enter.setText("TestSno");
        serialnoFragment.onClick(plus);
        sno_enter.setText("TestSno");
        serialnoFragment.onClick(plus);
        sno_enter.setText("TestSno");
        serialnoFragment.onClick(plus);
        sno_enter.setText("TestSno");
        serialnoFragment.onClick(plus);


        Assert.assertFalse(!serialnoFragment.bSerialNumberLimitDialog);


    }

    @Test
    public void app_back()
    {
        fragmentClass = SerialNumberFragment.class;
        String tag = fragmentClass.getCanonicalName();
        MenuItem menuItem = new RoboMenuItem(R.id.home);
        serialnoFragment.onOptionsItemSelected(menuItem);
        Assert.assertEquals(serialnoFragment.getFragmentManager().getBackStackEntryCount(),0);

    }

    @Test
    public void test_confirm()
    {
        EditText sno_enter = (EditText)serialnoFragment.getView().findViewById(R.id.AddSno);
        ImageView plus = (ImageView)serialnoFragment.getView().findViewById(R.id.add_serialno);

        sno_enter.setText("TestSno");
        serialnoFragment.onClick(plus);
        sno_enter.setText("TestSno");
        serialnoFragment.onClick(plus);


        Button confirm = (Button)serialnoFragment.getView().findViewById(R.id.serialno_confirm);
        confirm.performClick();

    }

    @Test


    public void test_click_minus_functionality()
    {
        EditText sno_enter = (EditText)serialnoFragment.getView().findViewById(R.id.AddSno);
        ImageView plus = (ImageView)serialnoFragment.getView().findViewById(R.id.add_serialno);

        sno_enter.setText("TestSno");
        serialnoFragment.onClick(plus);

        ImageView minus = (ImageView) serialnoFragment.getView().findViewById(R.id.remove_serialno);
        minus.performClick();

    }
}
