package com.intuit.qbes.mobilescanner;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.Picklist;

public class MainActivity extends AppCompatActivity
        implements ListPicklistFragment.Callbacks {

    private static String LOG_TAG = "MainActivity";
    private static final int REQUEST_DETAIL_PICKLIST = 1;

    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private CoordinatorLayout mCoordinatorLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mHeaderTextView1;
    private TextView mHeaderTextView2;

    private int mSelectedMenuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mHeaderTextView1 = (TextView) navHeader.findViewById(R.id.nav_header_tv1);
        mHeaderTextView2 = (TextView) navHeader.findViewById(R.id.nav_header_tv2);
        String tv1Text = "Hello,";
        String tv2Text = "John Doe!";
        mHeaderTextView1.setText(tv1Text);
        mHeaderTextView2.setText(tv2Text);

        mSelectedMenuId = R.id.nav_home_item;
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
        intent.putExtra(DetailPicklistFragment.EXTRA_PICKLIST, selectedPick);
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
        Fragment fragment = null;
        mSelectedMenuId = menuId;

        Class fragmentClass = null;
        switch(menuId) {
            case R.id.nav_home_item:
                fragmentClass = ListPicklistFragment.class;
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
        fragment = fragmentManager.findFragmentByTag(tag);
        try {
            if (fragment == null) {
                fragment = (Fragment) fragmentClass.newInstance();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContent, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();
    }
}
