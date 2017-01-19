package com.intuit.qbes.mobilescanner;

/**
 * Created by ckumar5 on 08/01/17.
 */

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

enum SortFilterOption
{
    Location,
    Items,
    Status,
    SalesOrder
};

public class SortingDialog extends DialogFragment implements View.OnClickListener{

    public TextView mSortByLocation;
    public TextView mSortByItems;
    public TextView mSortByStatus;
    public TextView mSortBySalesOrder;

    //Require Default Empty Constructor
    public SortingDialog()
    {

    }
    // Defines the listener interface
    public interface SortingSelectionDialogListener {
        void onSortingOptionSelection(SortFilterOption userSelection);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sorting_option1,container);
        mSortByLocation = (TextView) view.findViewById(R.id.sortByLocation);
        mSortByItems = (TextView) view.findViewById(R.id.sortByItems);
        mSortByStatus = (TextView) view.findViewById(R.id.sortByStatus);
        mSortBySalesOrder = (TextView) view.findViewById(R.id.sortBySalesOrder);
        mSortByLocation.setOnClickListener(this);
        mSortByItems.setOnClickListener(this);
        mSortByStatus.setOnClickListener(this);
        mSortBySalesOrder.setOnClickListener(this);
        return view;
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
        switch (view.getId())
        {
            case R.id.sortByLocation :
            {
                SortingSelectionDialogListener listener = (SortingSelectionDialogListener) getTargetFragment();
                listener.onSortingOptionSelection(SortFilterOption.Location);
                dismiss();
                break;
            }

            case R.id.sortByItems :
            {
                SortingSelectionDialogListener listener = (SortingSelectionDialogListener) getTargetFragment();
                listener.onSortingOptionSelection(SortFilterOption.Items);
                dismiss();
                break;
            }
            case R.id.sortByStatus :
            {
                SortingSelectionDialogListener listener = (SortingSelectionDialogListener) getTargetFragment();
                listener.onSortingOptionSelection(SortFilterOption.Status);
                dismiss();
                break;
            }
            case R.id.sortBySalesOrder :
            {
                SortingSelectionDialogListener listener = (SortingSelectionDialogListener) getTargetFragment();
                listener.onSortingOptionSelection(SortFilterOption.SalesOrder);
                dismiss();
                break;
            }
        }
    }
}
