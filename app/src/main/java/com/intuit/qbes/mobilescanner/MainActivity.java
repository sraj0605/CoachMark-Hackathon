package com.intuit.qbes.mobilescanner;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.barcode.BarcodeScannerDevice;
import com.intuit.qbes.mobilescanner.barcode.DeviceManager;
import com.intuit.qbes.mobilescanner.model.CompanyFileDetails;
import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.networking.Foreground;

public class MainActivity extends AppCompatActivity
        implements ListPicklistFragment.Callbacks,Foreground.Listener{

    private static String LOG_TAG = "MainActivity";
    private static final int REQUEST_DETAIL_PICKLIST = 1;

    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private CoordinatorLayout mCoordinatorLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mPickerFName;
    private TextView mPickerLName;
    private CompanyFileDetails details = null;
    private long companyId;
    private static final int REQUEST_DETAIL_ITEM = 1;
    private int mSelectedMenuId;
    private DeviceManager mDevice = null;
    private static final int PAIRING_CODE = 2;
    private DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            db = new DatabaseHandler(getApplicationContext());

            if(DevicePairingCheck())
            {
                Intent intent = new Intent(this,DevicePairingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }


            setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationView = (NavigationView) findViewById(R.id.nvView);

        setupDrawerContent(mNavigationView);
        mDrawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(mDrawerToggle);

        //Setup header
        View navHeader = mNavigationView.getHeaderView(0);
        mPickerFName = (TextView) navHeader.findViewById(R.id.nav_header_tv1);
        mPickerLName = (TextView) navHeader.findViewById(R.id.nav_header_tv2);
        String tv1Text = "Hello,";
        String tv2Text = "John Doe!";
        mPickerFName.setText(tv1Text);
        mPickerLName.setText(tv2Text);

        mSelectedMenuId = R.id.nav_home_item;
        mDevice = DeviceManager.getDevice(getApplicationContext());
        Foreground obj = Foreground.init(getApplication());
        obj.addListener(this);
        SyncUtils.CreateSyncAccount(getApplicationContext());

    }

    @Override
    public void onBecameBackground() {
        if(mDevice!= null)
            mDevice.freeDeviceResource();
    }

    @Override
    public void onBecameForeground() {

    }

    @Override
    protected void onStart() {
        super.onStart();

        mNavigationView.getMenu().performIdentifierAction(mSelectedMenuId, 0);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDevice.freeDeviceResource();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPickSelected(Picklist selectedPick) {

        Intent intent = new Intent(this, DetailPicklistActivity.class);
        intent.putExtra(TaskPickListFragment.EXTRA_PICKLIST, selectedPick);
        startActivityForResult(intent, REQUEST_DETAIL_PICKLIST);

    }



    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar,
                R.string.drawer_open,  R.string.drawer_close);
    }

    private void selectDrawerItem(MenuItem menuItem) {

        presentFragmentForMenu(menuItem.getItemId());
        mDrawer.closeDrawers();
    }

    private void presentFragmentForMenu(int menuId)
    {
        mSelectedMenuId = menuId;
Class fragmentClass = null;
        switch(menuId) {
            case R.id.nav_home_item:
                fragmentClass = TabLayoutFragment.class;
                break;
            default:
                break;
        }

        /*
        Check if a fragment of the required class already exists with fragment manager
        and reuse it if found. Otherwise there will be issues where 2 instances of the
        same fragment are present and one is not attached to the activity
         */


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
        ft.replace(R.id.flContent, fragment, tag);
        if(!(fragmentClass == TabLayoutFragment.class)) {

            ft.addToBackStack(null);

            }
        ft.commit();

    }

   /* public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_DETAIL_PICKLIST:
                FragmentManager fm = getSupportFragmentManager();

                ListPicklistFragment fragment = (ListPicklistFragment) fm.findFragmentById(R.id.flContent);
                if (fragment != null)
                {
                    Picklist picklist = (Picklist) data.getParcelableExtra(DetailPicklistFragment.EXTRA_PICKLIST);
                    try
                    {
                        fragment.updatePicklist(picklist);
                    }
                    catch (IllegalArgumentException ex)
                    {
                        Log.e(LOG_TAG, ex.toString());
                    }
                    Snackbar.make(mCoordinatorLayout,
                            String.format("Picklist# %s updated", picklist.getNumber()),
                            Snackbar.LENGTH_LONG).show();

                }
                else
                {
                    Log.e(LOG_TAG, "Fragment not found");
                }

                break;
            default:
                break;
        }
    }*/


    private boolean DevicePairingCheck()
    {
        //check if realm_id, company_guid and company_name have been received or not
        //if yes return false else return true;
        details = db.getDetails();
        if(details != null) {
            companyId = details.getRealmID();
        }
        else
            companyId = -1;

        if(companyId == -1) {
            return true;
        }
        else {
            return false;
        }

    }
}
