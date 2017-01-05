package com.intuit.qbes.mobilescanner;

import android.support.v4.app.Fragment;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;

/**
 * Created by pdixit on 10/7/16.
 */
public class DetailItemActivity extends SingleFrameActivity {

    @Override
    protected Fragment createFragment()
    {
        LineItem lineItem = (LineItem) getIntent().getParcelableExtra(DetailItemFragment.EXTRA_LINEITEM);
        return DetailItemFragment.newInstance(lineItem);
    }
}
