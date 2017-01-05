package com.intuit.qbes.mobilescanner;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public abstract class SingleFrameActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    protected CoordinatorLayout mCoordinatorLayout;

    protected abstract Fragment createFragment();

    protected int getLayoutResId()
    {
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_cl);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.flContent);

        if (fragment == null)
        {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.flContent, fragment).commit();
        }
    }
}
