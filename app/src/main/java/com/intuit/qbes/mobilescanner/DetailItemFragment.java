package com.intuit.qbes.mobilescanner;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailItemFragment extends Fragment {

    public static final String EXTRA_LINEITEM = "com.intuit.qbes.mobilescanner.lineitem";

    private LineItem mLineItem;

    private TextView mNameView;
    private TextView mDescView;
    private TextView mUOMView;
    private TextView mQtyNeededView;
    private TextView mQtyToPickView;
    private EditText mQtyPickedView;
    private SQLiteDatabaseLineItemHandler db;
    public DetailItemFragment() {
        // Required empty public constructor
    }

    public static DetailItemFragment newInstance(LineItem lineItem)
    {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_LINEITEM, lineItem);

        DetailItemFragment fragment = new DetailItemFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLineItem = (LineItem) getArguments().getParcelable(EXTRA_LINEITEM);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_item, container, false);
        
        mNameView = (TextView) view.findViewById(R.id.detail_item_name);
        mNameView.setText(mLineItem.getName());

        mDescView = (TextView) view.findViewById(R.id.detail_item_desc);
        mDescView.setText(mLineItem.getDescription());

        mUOMView = (TextView) view.findViewById(R.id.detail_item_uom);
        mUOMView.setText(String.format("U/M: %s", mLineItem.getUom()));

        mQtyNeededView = (TextView) view.findViewById(R.id.detail_item_qty_needed);
        mQtyNeededView.setText(String.format("Needed: %.2f", mLineItem.getQtyNeeded()));

        mQtyToPickView = (TextView) view.findViewById(R.id.detail_item_qty_topick);
        mQtyToPickView.setText(String.format("To Pick: %.2f", mLineItem.getQtyToPick()));

        mQtyPickedView = (EditText) view.findViewById(R.id.detail_item_qty_picked);
        mQtyPickedView.setText(Double.toString(mLineItem.getQtyPicked()));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_item, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save_item_detail:
                saveItemDetails();
                break;

            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(getActivity());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(getActivity(), intent);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void saveItemDetails()
    {
        //mLineItem.setQtyPicked(Double.parseDouble(mQtyPickedView.getText().toString()));
        db = new SQLiteDatabaseLineItemHandler(getActivity().getApplicationContext());
        db.updateLineItemWithQtyPicked(mLineItem,Double.parseDouble(mQtyPickedView.getText().toString()) );
        Intent data = new Intent();
        data.putExtra(EXTRA_LINEITEM, mLineItem);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }

}
