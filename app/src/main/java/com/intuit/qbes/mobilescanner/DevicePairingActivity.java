package com.intuit.qbes.mobilescanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.intuit.qbes.mobilescanner.model.CompanyFileDetails;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.networking.DataSync;

import java.util.List;

public class DevicePairingActivity extends AppCompatActivity implements GetStartedFragment.GetStartedCallback, CodeEntryFragment.PairSuccessCallback, SuccessfulPairFragment.PairCompleteCallback, TimeoutFragment.TryAgainCallback, DenyFragment.DenyTryAgainCallback{

    private static String LOG_TAG = "DevicePairingActivity";
    private Class fragmentClass = null;
    private Fragment fragment = null;
    private DatabaseHandler db;
    private DataSync tasks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            requestWindowFeature(Window.FEATURE_NO_TITLE);

            setContentView(R.layout.activity_device_pairing);

            LaunchGetStart();



    }




    public void LaunchGetStart()
    {

        fragmentClass = GetStartedFragment.class;
        String tag = fragmentClass.getCanonicalName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentByTag(tag);
        try {
            if (fragment == null) {
                //fragment = (Fragment) fragmentClass.newInstance();
                fragment = GetStartedFragment.newInstance();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flDevicePairing, fragment, tag);
        // ft.addToBackStack(null);
        ft.commit();
    }

    public void LaunchCodeEntry()
    {

        fragmentClass = CodeEntryFragment.class;
        String tag = fragmentClass.getCanonicalName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentByTag(tag);
        try {
            if (fragment == null) {
                //fragment = (Fragment) fragmentClass.newInstance();
                fragment = CodeEntryFragment.newInstance();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flDevicePairing, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void TimeOutScreen()
    {

        fragmentClass = TimeoutFragment.class;
        String tag = fragmentClass.getCanonicalName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentByTag(tag);
        try {
            if (fragment == null) {
                //fragment = (Fragment) fragmentClass.newInstance();
                fragment = TimeoutFragment.newInstance();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flDevicePairing, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void PairSuccess()
    {

        fragmentClass = SuccessfulPairFragment.class;
        String tag = fragmentClass.getCanonicalName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentByTag(tag);
        try {
            if (fragment == null) {
                //fragment = (Fragment) fragmentClass.newInstance();
                fragment = SuccessfulPairFragment.newInstance();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flDevicePairing, fragment, tag);
       // ft.addToBackStack(null);
        ft.commit();
    }

    public void DenyScreen()
    {

        fragmentClass = DenyFragment.class;
        String tag = fragmentClass.getCanonicalName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentByTag(tag);
        try {
            if (fragment == null) {
                //fragment = (Fragment) fragmentClass.newInstance();
                fragment = DenyFragment.newInstance();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flDevicePairing, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onGetStartedClick() {

        LaunchCodeEntry();

    }


    @Override
    public void onPairSuccess() {

        PairSuccess();


    }

    @Override
    public void onTimeOut() {

        TimeOutScreen();
    }

    @Override
    public void onDeny() {

        DenyScreen();
    }

    @Override
    public void onPairComplete() {

        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public void onTryAgain() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();


    }

    @Override
    public void onDenyTryAgain() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }




}
