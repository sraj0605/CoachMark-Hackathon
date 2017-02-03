package com.intuit.qbes.mobilescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

import java.util.ArrayList;
import java.util.List;

public class ProductInfoActivity extends AppCompatActivity implements ProductInfoFragment.Callbacks {

    private static String LOG_TAG = "ProductInfoActivity";
    private Class fragmentClass = null;
    private Fragment fragment = null;
    private ProductInfoFragment ProductFragment = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LineItem lineitem = (LineItem) getIntent().getParcelableExtra(ProductInfoFragment.EXTRA_LINEITEM);
        String barcodePassed = getIntent().getStringExtra(ProductInfoFragment.BARCODE_ENTERED);
     	ProductFragment = new ProductInfoFragment();
        fragmentClass = ProductInfoFragment.class;
        String tag = fragmentClass.getCanonicalName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentByTag(tag);
        try {
            if (fragment == null) {
                //fragment = (Fragment) fragmentClass.newInstance();
                fragment = ProductInfoFragment.newInstance(lineitem,barcodePassed);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flProdInfo, fragment, tag);
        // ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    public void onSerialNumberClicked(LineItem lineitem) {

        fragmentClass = SerialNumberFragment.class;
        String tag = fragmentClass.getCanonicalName();


        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        try {
            if (fragment == null) {
                //fragment = (Fragment) fragmentClass.newInstance();
                fragment = SerialNumberFragment.newInstance(lineitem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flProdInfo, fragment, tag);

        ft.addToBackStack(null);

        ft.commit();
    }


    @Override
    public void onBackPressed() {


        fragmentClass = ProductInfoFragment.class;
        String tag = fragmentClass.getCanonicalName();
        FragmentManager fragmentManager = getSupportFragmentManager();

        ProductInfoFragment fragment = (ProductInfoFragment)fragmentManager.findFragmentByTag(tag);
        if(fragment!=null && fragment.isVisible()) {
            fragment.onBackPressed();
        }
        else
        {
            super.onBackPressed();
        }

    }
}
