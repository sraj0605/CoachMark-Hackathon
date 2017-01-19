package com.intuit.qbes.mobilescanner;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by ckumar5 on 10/01/17.
 */

/*public class FilterDialog extends DialogFragment implements View.OnClickListener{

    public FilterDialog(){};

    private TextView mfilterByLocation;

    // Defines the listener interface
    public interface FilterDialogListener {
        void onFilterOptionSelection(SortFilterOption userSelection);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_filter_option,container);
        mfilterByLocation = (TextView) view.findViewById(R.id.filterbystatus);
        mfilterByLocation.setOnClickListener(this);
        return  view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View view) {

        FilterDialog.FilterDialogListener listener = (FilterDialog.FilterDialogListener) getTargetFragment();
        listener.onFilterOptionSelection(SortFilterOption.Items);
        dismiss();

    }
}*/
