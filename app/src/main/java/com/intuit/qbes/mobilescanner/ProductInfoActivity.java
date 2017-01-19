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

import com.intuit.qbes.mobilescanner.model.Picklist;

public class ProductInfoActivity extends AppCompatActivity implements ProductInfoFragment.Callbacks {

    private static String LOG_TAG = "ProductInfoActivity";
    private Class fragmentClass = null;
    private Fragment fragment = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();


        fragmentClass = ProductInfoFragment.class;
        String tag = fragmentClass.getCanonicalName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentByTag(tag);

        try {
            if (fragment == null) {
                //fragment = (Fragment) fragmentClass.newInstance();
                 fragment = new ProductInfoFragment();

            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flProdInfo, fragment, tag);
       // ft.addToBackStack(fragment.getClass().getName());
        ft.commit();

    }

    @Override
    public void onSerialNumberClicked() {

        fragmentClass = SerialNumberFragment.class;
        String tag = fragmentClass.getCanonicalName();

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        try {
            if (fragment == null) {
                fragment = (Fragment) fragmentClass.newInstance();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction   ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flProdInfo, fragment, tag);
        if(!(fragmentClass == TabLayoutFragment.class)) {

            ft.addToBackStack(null);

        }
        ft.commit();
    }
}
