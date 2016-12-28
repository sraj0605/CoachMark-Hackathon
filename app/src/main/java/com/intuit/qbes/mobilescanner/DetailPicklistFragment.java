package com.intuit.qbes.mobilescanner;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.barcode.BarcodeReader;
import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.networking.PicklistHttp;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailPicklistFragment extends Fragment implements DataListener, BarcodeReader.BarcodeReaderReady {

    public static final String EXTRA_PICKLIST = "com.intuit.qbes.mobilescanner.picklist";
    private static final String LOG_STR = "DetailPicklistFragment";

    private TextView mNumber;
    private ImageView mStatus;
    private TextView mOrderDate;
    private TextView mShipDate;
    private TextView mName;
    private RecyclerView mRecyclerView;

    private Picklist mPicklist;

    private Callbacks mCallbacks;

    private BarcodeReader mBarcodeReader = null;

    private ProgressDialog mProgressDialog;
    private SQLiteDatabaseLineItemHandler db;
    private List<LineItem> lineitems;
    public interface Callbacks {
        void onLineItemSelected(LineItem selectedLineItem);
        void onPicklistSaved(Integer responseCode, Picklist picklist);
        void onBarcodeReady();
    }

    public static DetailPicklistFragment newInstance(Picklist picklist)
    {
            Bundle args = new Bundle();
        args.putParcelable(EXTRA_PICKLIST, picklist);

        DetailPicklistFragment fragment = new DetailPicklistFragment();
        fragment.setArguments(args);

        return fragment;
    }


    public DetailPicklistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPicklist = (Picklist) getArguments().getParcelable(EXTRA_PICKLIST);
        if (mPicklist == null && savedInstanceState != null)
        {
            mPicklist = (Picklist) savedInstanceState.getParcelable(EXTRA_PICKLIST);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_picklist, container, false);

        mNumber = (TextView) view.findViewById(R.id.detail_picklist_num);


        mStatus = (ImageView) view.findViewById(R.id.detail_picklist_status);


        mOrderDate = (TextView) view.findViewById(R.id.detail_picklist_date);


        mShipDate = (TextView) view.findViewById(R.id.detail_picklist_ship_date);


        mName = (TextView) view.findViewById(R.id.detail_picklist_name);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.detail_picklist_rv);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                com.intuit.qbes.mobilescanner.DividerItemDecoration(getActivity(), com.intuit.qbes.mobilescanner.DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_picklist, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save_picklist:
                savePicklist();
                break;

            case R.id.action_reset_barcode_picklist:
                initializeBarcode();
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

    @Override
    public void onStart() {
        super.onStart();

        mNumber.setText(String.format("SO# %s", mPicklist.getNumber()));

        if (mPicklist.getStatus() == 1)
        {
            mStatus.setImageResource(R.drawable.ic_assignment_black_18dp);
        }
        else
        {
            mStatus.setImageResource(R.drawable.ic_assignment_turned_in_black_18dp);
        }

        mOrderDate.setText(MSUtils.MMddyyyyFormat.format(mPicklist.getOrderDate()));
        mShipDate.setText(MSUtils.MMddyyyyFormat.format(mPicklist.getShipDate()));
        mName.setText(mPicklist.getName());
        db = new SQLiteDatabaseLineItemHandler(getActivity().getApplicationContext());
        lineitems = db.allLineItems(mPicklist.getRecnum());

        mRecyclerView.setAdapter(new LineItemAdapter(lineitems));
        if (mBarcodeReader == null)
        {
            mBarcodeReader = new BarcodeReader();
            mBarcodeReader.registerBarcodeReaderReady(this);
            mBarcodeReader.initializeBarcodeReader(getActivity());
        }
    }

    private void initializeBarcode()
    {
        if (mBarcodeReader != null)
        {
            mBarcodeReader.unregisterDataListener(this);
            mBarcodeReader.close();
            mBarcodeReader = null;
        }

        mBarcodeReader = new BarcodeReader();
        mBarcodeReader.registerBarcodeReaderReady(this);
        mBarcodeReader.initializeBarcodeReader(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        mBarcodeReader.unregisterDataListener(this);
        mBarcodeReader.close();
        mBarcodeReader = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_PICKLIST, mPicklist);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        new AsyncDataUpdate().execute(scanDataCollection);
    }

    @Override
    public void onBarcodeReaderReady() {
        mBarcodeReader.registerDataListener(this);
        if (mCallbacks != null)
        {
            mCallbacks.onBarcodeReady();
        }
    }

    public void updateLineItem(LineItem lineItem)
    {
        if (lineItem == null)
        {
            throw new IllegalArgumentException("Lineitem is null");
        }

        int idx = mPicklist.getLines().indexOf(lineItem);

        if (idx == -1)
        {
            throw new IllegalArgumentException("Lineitem is not found in picklist");
        }

        mPicklist.getLines().get(idx).setQtyPicked(lineItem.getQtyPicked());
    }

    private void savePicklist()
    {
        mProgressDialog = ProgressDialog.show(getActivity(), "", "Updating Picklist");
        new UpdatePicklistTask().execute(mPicklist);
    }

    private class LineItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mItemName;
        private TextView mItemDesc;
        private TextView mQtyNeeded;
        private TextView mQtyToPick;
        private TextView mQtyPicked;

        private LineItem mItem;

        public LineItemHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            mItemName = (TextView) itemView.findViewById(R.id.item_name);
            mItemDesc = (TextView) itemView.findViewById(R.id.item_desc);
            mQtyNeeded = (TextView) itemView.findViewById(R.id.item_qty_needed);
            mQtyToPick = (TextView) itemView.findViewById(R.id.item_qty_topick);
            mQtyPicked = (TextView) itemView.findViewById(R.id.item_qty_picked);
        }

        public void bindLineItem(LineItem item)
        {
            mItem = item;

            mItemName.setText(item.getName());
            mItemDesc.setText(item.getDescription());
            mQtyNeeded.setText(String.format("Needed: %s %s", item.getQtyNeeded(), item.getUom()));
            mQtyToPick.setText(String.format("To Pick: %s %s", item.getQtyToPick(), item.getUom()));
            mQtyPicked.setText(String.format("Picked: %s %s", item.getQtyPicked(), item.getUom()));
        }


        @Override
        public void onClick(View v) {
            if (mCallbacks != null)
            {
                mCallbacks.onLineItemSelected(mItem);
            }
        }
    }

    private class LineItemAdapter extends RecyclerView.Adapter<LineItemHolder> {

        private List<LineItem> mLineItems;

        public LineItemAdapter(List<LineItem> lineItems)
        {
            mLineItems = lineItems;
        }

        @Override
        public void onBindViewHolder(LineItemHolder holder, int position) {
            holder.bindLineItem(mLineItems.get(position));

        }

        @Override
        public LineItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.list_item_detail_picklist, parent, false);

            LineItemHolder viewHolder = new LineItemHolder(view);
            return viewHolder;
        }

        @Override
        public int getItemCount() {
            return mLineItems.size();
        }
    }

    private class AsyncDataUpdate extends
            AsyncTask<ScanDataCollection, Void, Integer> {

        @Override
        protected Integer doInBackground(ScanDataCollection... params) {

            int idx = -1;

            try
            {
                mBarcodeReader.executeRead();
                String dataStr = "";
                ScanDataCollection scanDataCollection = params[0];

                if (scanDataCollection != null
                        && scanDataCollection.getResult() == ScannerResults.SUCCESS) {
                    ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection
                            .getScanData();

                    // Iterate through scanned data and prepare the statusStr
                    for (ScanDataCollection.ScanData data : scanData) {
                        // Get the scanned data
                        dataStr = data.getData();
                        // Get the type of label being scanned
                        //ScanDataCollection.LabelType labelType = data.getLabelType();
                        // Concatenate barcode data and label type
                        //statusStr = barcodeData + " " + labelType;
                    }
                }

                for(idx = 0; idx < mPicklist.getLines().size(); idx++)
                {
                    LineItem lineItem = mPicklist.getLines().get(idx);
                    if (lineItem.getBarcode().equals(dataStr))
                    {
                        lineItem.setQtyPicked(lineItem.getQtyPicked() + 1);
                        break;
                    }
                }

                if (idx == mPicklist.getLines().size())
                {
                    idx = -1;
                }
            }
            catch (ScannerException ex)
            {
                Log.e(LOG_STR, "Failed to process data");
                Log.e(LOG_STR, ex.toString());
            }

            return idx;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mRecyclerView.getAdapter().notifyItemChanged(integer);
            mRecyclerView.scrollToPosition(integer);
        }
    }

    private class UpdatePicklistTask extends AsyncTask<Picklist, Void, Integer> {

        @Override
        protected Integer doInBackground(Picklist... params) {

            Picklist picklist = params[0];
            int responseCode = -1;

            try {
                String result = "";
                picklist.setStatus(2);
                String picklistJSONStr = picklist.toJSON().toString();
                responseCode = new PicklistHttp().
                        putUrlString(String.format("%s/picklists/%d",
                                MSUtils.getServerUrl(getActivity()),
                                picklist.getRecnum()), picklistJSONStr, result);

                if (responseCode != 201 && responseCode != 200)
                {
                    Log.e(LOG_STR, result);
                }
            }
            catch (JSONException ex)
            {
                Log.e(LOG_STR, "Failed to update picklist");
                Log.e(LOG_STR, ex.toString());
            }
            catch (IOException ioe)
            {
                Log.e(LOG_STR, "Failed to update picklist");
                Log.e(LOG_STR, ioe.toString());
            }

            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer response) {
            super.onPostExecute(response);
            if (mCallbacks != null)
            {
                mProgressDialog.dismiss();
                mCallbacks.onPicklistSaved(response, mPicklist);
            }
        }
    }

}
