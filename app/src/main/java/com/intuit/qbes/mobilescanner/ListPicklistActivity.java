package com.intuit.qbes.mobilescanner;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

/**
 * Created by pdixit on 10/5/16.
 */
public class ListPicklistActivity extends SingleFrameActivity implements ListPicklistFragment.Callbacks {

    private static final int REQUEST_DETAIL_PICKLIST = 1;
    private static final String LOG_TAG = "ListPicklistActivity";



    @Override
    protected Fragment createFragment()
    {

        return new ListPicklistFragment();
    }

    @Override
    public void onPickSelected(Picklist selectedPick) {
        Intent intent = new Intent(this, DetailPicklistActivity.class);
        intent.putExtra(DetailPicklistFragment1.EXTRA_PICKLIST, selectedPick);
        startActivityForResult(intent, REQUEST_DETAIL_PICKLIST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
    }
}
