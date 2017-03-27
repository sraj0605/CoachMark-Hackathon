package com.intuit.qbes.mobilescanner;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intuit.qbes.mobilescanner.barcode.BarcodeFactory;
import com.intuit.qbes.mobilescanner.barcode.BarcodeScannerDevice;
import com.intuit.qbes.mobilescanner.barcode.DeviceManager;
import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;
import com.intuit.qbes.mobilescanner.model.Status;
import com.intuit.qbes.mobilescanner.networking.AppController;
import com.intuit.qbes.mobilescanner.networking.DataSync;
import com.intuit.qbes.mobilescanner.networking.PicklistHttp;

import org.json.JSONException;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import static java.lang.Math.E;

/**
 * Created by ckumar5 on 08/01/17.
 */

public class TaskPickListFragment extends Fragment implements View.OnClickListener,SortingDialog.SortingSelectionDialogListener,BarcodeScannerDevice.ScanDataReceiver,DataSync.DataSyncCallback{


    public static final String EXTRA_PICKLIST = "com.intuit.qbes.mobilescanner.picklist";
    private static final String LOG_STR = "TaskPickListFragment";

    private RecyclerView mRecyclerView;
    private LineItemAdapter mAdapter = null;
    private static final String updateTAG = "Update";
    private ProgressDialog mProgressDialog;
    private Picklist mPicklist = null;
    private TaskPickListFragment.Callbacks mCallbacks;


    private DataSync dataSync = null;


    private DeviceManager mDeviceManager = null;

    //private RelativeLayout mLayoutForFilter;
    private RelativeLayout mLayoutForSort;
    private ImageView mReverseSortingOption;
    private Button mSync;
    private ImageButton mSyncTick;
    private Button mComplete;
    private TextView mSortOrderSelection;
    private boolean mMessageThrown = false;

    private DatabaseHandler db = null;
    private List<LineItem> lineitems = null;
  

    @Override
    public void onFetchPicklist(List<Picklist> mPicklists) {

    }

    @Override
    public void onUpdatePicklist(Picklist Picklist, Boolean isSync, Boolean isStale, String error) {
        if(db == null)
            db =new DatabaseHandler(getContext());
        if(!isStale) {
            if (isSync) {


                mPicklist = Picklist;
                db.addPickListInBatch(mPicklist, false);
                updateSyncButton();

            } else {

                db.batchDeletePicklist(mPicklist);
                mCallbacks.onPicklistComplete();
            }
        }
        else if(isStale && error.compareTo("") == 0)
        {
            StaleDataDialog(getContext());
        }
        else if(isStale && error.compareTo("Network")==0)
        {

        }
        else if(isStale && error.compareTo("Timeout")==0)
        {

        }
        dismissDialog();
    }

    @Override
    public void onCodeValidation(String response) {

    }

    public void updateSyncButton()
    {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
               mSyncTick.setVisibility(View.GONE);
                mSync.setVisibility(View.VISIBLE);
            }
        }, 2000);

        mSyncTick.setVisibility(View.VISIBLE);
        //QBWG-44263 added by Sunder Raj
        Toast.makeText(getActivity(),getString(R.string.sync_complete),
                Toast.LENGTH_SHORT).show();
        mSync.setVisibility(View.GONE);


    }

    public interface Callbacks {
        void onLineItemSelected(LineItem selectedLineItem,boolean bScanned);
        void onPicklistSaved(Integer responseCode, Picklist picklist);
        void onBarcodeReady();
        void onPicklistComplete();
    }

    public static TaskPickListFragment newInstance(Picklist picklist)
    {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_PICKLIST, picklist);

        TaskPickListFragment fragment = new TaskPickListFragment();

        fragment.setArguments(args);
        return fragment;
    }

    public TaskPickListFragment() {
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
        //@Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_picklist2, container, false);

        //@Set up Recycle view and its holder
        setUpRecycleView(view);

        //@Set up necessary control listener
        setUpControlListenerForFragment(view);

        return view;
    }

    //Set up Recycle view and its holder
    public void setUpRecycleView(View view)
    {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.detail_picklist_rv2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                com.intuit.qbes.mobilescanner.DividerItemDecoration(getActivity(), com.intuit.qbes.mobilescanner.DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

    }
    //@Set up necessary control listener
    public void setUpControlListenerForFragment(View view)
    {
        mLayoutForSort = (RelativeLayout) view.findViewById(R.id.sortlayout);
        mReverseSortingOption = (ImageView) view.findViewById(R.id.sorticon);
        mSortOrderSelection = (TextView)view.findViewById(R.id.sortbyselection);
        mSync = (Button)view.findViewById(R.id.update_sync);
        mSyncTick = (ImageButton)view.findViewById(R.id.img_update_sync);
        mComplete = (Button)view.findViewById(R.id.update_complete);
        //mLayoutForFilter = (RelativeLayout)view.findViewById(R.id.filter);
        //chandan - is there any chances of above variables getting null,dont think so
        mComplete.setOnClickListener(this);
        mSync.setOnClickListener(this);
        mLayoutForSort.setOnClickListener(this);
        mReverseSortingOption.setOnClickListener(this);
        mSortOrderSelection.setOnClickListener(this);
        //mLayoutForFilter.setOnClickListener(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_detail_picklist, menu);
        //super.onCreateOptionsMenu(menu, inflater);

    }
//right now no use
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save_picklist:
                //savePicklist();-chandan
                break;

            case R.id.action_reset_barcode_picklist:
                //initializeBarcode();-chandan
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
    }*/

    @Override
    public void onStart() {
        super.onStart();

        //@Check whether Picklist is already having line items
        if(lineitems == null)
            lineitems = mPicklist.getLineitems();

        //@If picklist does not have line item read line items from db,for picklist,assign lineitems to picklist object for future use
        if(lineitems == null || lineitems.size() == 0) {
            db = new DatabaseHandler(getContext());
            lineitems = db.allLineItems(mPicklist.getId());
            mPicklist.setLineitems(lineitems);
        }

        //@if still we dont find line items there is no meaning of setting up UI Views,hence skipping

        if(lineitems != null) {

            //@Initialize recycle view adapter
            if (mAdapter == null)
                mAdapter = new LineItemAdapter(lineitems);

            mRecyclerView.setAdapter(mAdapter);

            //@get Scanner device and register for call back,such that scanned data will be received in this object's method
            mDeviceManager = DeviceManager.getDevice(getContext());
            mDeviceManager.unRegisterDeviceFromCallback(this);
            mDeviceManager.registerForCallback(this);
        }

    }
    //@callback from sorting Dialog fragment
    @Override
    public void onSortingOptionSelection(SortFilterOption userSelection) {
        if(mSortOrderSelection!=null && mAdapter!=null) {
            switch (userSelection) {

                case Items:
                    mSortOrderSelection.setText("Items");
                    break;
                case Location:
                    mSortOrderSelection.setText("Locations");
                    break;
                case SalesOrder:
                    mSortOrderSelection.setText("SalesOrder");
                    break;
                case Status:
                    mSortOrderSelection.setText("Status");
                    break;

            }

            mAdapter.sortDataSetByUserOption(userSelection, false);
        }
    }
///@Filter - in future we can enable this
   /* @Override
    public void onFilterOptionSelection(SortFilterOption userSelection) {
        mAdapter.filterDatasetByUserOption(userSelection,"Item1");
    }*/

    //@callback from scanner device library
    @Override
    public void scanDataReceived(String sData) {
        //Creating a dummy Line Item object,-1 is important as equal of will validate based on -1
        LineItem lineItem = new LineItem(-1,sData);

        if(lineitems.contains(lineItem))
        {
            if(!mMessageThrown) {
                lineItem = lineitems.get(lineitems.indexOf(lineItem));
                lineItem.setBarcodeEntered(sData);
                if (mCallbacks != null)
                    mCallbacks.onLineItemSelected(lineItem, true);
            }
        }
        else
        {
            //chandan- scanned barcode is not available in list
            //sunder - Added Code for - QBWG-41184
            if(!mMessageThrown) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        final Dialog openDialog = new Dialog(getContext());
                        openDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        openDialog.setContentView(R.layout.custom_dialog);
                        ImageView dialogImage = (ImageView) openDialog.findViewById(R.id.alert);
                        TextView dialogTextContent = (TextView) openDialog.findViewById(R.id.select_right_item);
                        TextView dialogTextContent2 = (TextView) openDialog.findViewById(R.id.item_not_in_picklist);
                        Button dialogCloseButton = (Button) openDialog.findViewById(R.id.dismiss_dialog);
                        dialogCloseButton.setOnClickListener(new View.OnClickListener() {

                            @Override

                            public void onClick(View v) {

                                mMessageThrown = false;
                                openDialog.dismiss();

                            }

                        });
                        mMessageThrown = true;
                        openDialog.show();

                    }


                });
            }

        }

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        try {

            mCallbacks = (Callbacks) context;
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(AppController.getInstance().getRequestQueue()!=null)
        {
            AppController.getInstance().getRequestQueue().cancelAll(updateTAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_PICKLIST, mPicklist);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.sortlayout:
            case R.id.sortbyselection:
            {
                FragmentManager fm = getFragmentManager();
                SortingDialog sortDialog = new SortingDialog();
                sortDialog.setTargetFragment(TaskPickListFragment.this,300);
                sortDialog.show(fm,"sort by");
                break;
            }
            case R.id.sorticon:
            {
                mAdapter.sortDataSetByUserOption(SortFilterOption.Status,true);
                break;
            }

            case R.id.update_sync:
            {
                mPicklist.setStatus(Status.PickinProgress);
                savePicklist(mPicklist, true, String.valueOf(mPicklist.getId()));
                break;
            }
            case R.id.update_complete:
            {
                CompleteList_Dialog();
                break;
            }
            /*case R.id.filter:
            {
                FragmentManager fm = getFragmentManager();
                FilterDialog filterDialog = new FilterDialog();
                filterDialog.setTargetFragment(DetailPicklistFragment1.this,300);
                filterDialog.show(fm,"sort by");
                break;
            }*/


        }

    }
    public void updateLineItemAndItsView(LineItem lineItem)
    {
        try
        {
            int lineItemPosition = updateLineItem(lineItem);
            updateViewAtPosition(lineItemPosition);
            DatabaseHandler db = new DatabaseHandler(getContext());
            db.addPickListInBatch(mPicklist,false);
        }
        catch (Exception exp)
        {
            Log.e(LOG_STR,exp.getMessage());
        }
    }

    public int updateLineItem(LineItem lineItem)
    {
        if (lineItem == null)
        {
            throw new IllegalArgumentException("Lineitem is null");
        }

        int idx = mPicklist.getLineitems().indexOf(lineItem);

        if (idx == -1)
        {
            throw new IllegalArgumentException("Lineitem is not found in picklist");
        }
        mPicklist.getLineitems().set(idx,lineItem);
        //mPicklist.getLines().get(idx).setQtyPicked(lineItem.getQtyPicked());
        return idx;
    }

    public void updateViewAtPosition(int position)
    {
        mAdapter.updateViewAtPosition(position);
        /*@if requirement is like not picked should come first,comment above line and uncomment below one*/
        //mAdapter.sortDataSetByUserOption(SortFilterOption.Status,false);
    }

    private void savePicklist(Picklist picklist, Boolean isSync, String id)
    {

        dataSync = new DataSync();
        dataSync.UpdatePicklist(picklist,getContext(), this, isSync, id); // remove dummy later
        showDialog();

    }
    private class LineItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mItemName;
        private TextView mItemDesc;
        private TextView mLocation;
        private TextView mSalesOrder;
        private TextView mQtyToPick;
        private ImageView mPickOrNonPickImage;
        private LineItem mItem;

        public LineItemHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            mItemName = (TextView) itemView.findViewById(R.id.itemName);
            mItemDesc = (TextView) itemView.findViewById(R.id.desc);
            mLocation = (TextView) itemView.findViewById(R.id.locationName);
            mSalesOrder = (TextView) itemView.findViewById(R.id.salesOrderName);
            mQtyToPick = (TextView) itemView.findViewById(R.id.quantity);
            mPickOrNonPickImage = (ImageView)itemView.findViewById(R.id.pickImage);
        }

        public void bindLineItem(LineItem item)
        {
            mItem = item;
            mItemName.setText(item.getItemName());
            mItemDesc.setText(item.getItemDesc());
            mLocation.setText(String.format("Bin No : %s",item.getBinLocation()));
            mSalesOrder.setText(String.format("Sales Order: %s",item.getDocNum()));
            if(Utilities.isInteger(String.valueOf(item.getQtyToPick())))
            {
                long qtyTopick = (long) item.getQtyToPick();
                mQtyToPick.setText(String.format("Qty : %s", qtyTopick));
            }
            else
                mQtyToPick.setText(String.format("Qty : %s", (item.getQtyToPick())));

            if(item.getmItemStatus() == Status.Picked)
                mPickOrNonPickImage.setImageResource(R.drawable.ic_picked_test);
            else if(item.getmItemStatus() == Status.NotPicked)
                mPickOrNonPickImage.setImageResource(R.drawable.ic_notpicked_test);
            else if(item.getmItemStatus() == Status.PartiallyPicked)
                mPickOrNonPickImage.setImageResource(R.mipmap.ic_partialpicked);
            else
                mPickOrNonPickImage.setImageResource(R.drawable.ic_notpicked_test);

        }

        @Override
        public void onClick(View v) {
            if (mCallbacks != null)
            {
                mCallbacks.onLineItemSelected(mItem,false);
            }
        }
    }
    //Recycle View Adpter
    private class LineItemAdapter extends RecyclerView.Adapter<LineItemHolder> /*implements Filterable*/{

        //Member Varaibles
        private List<LineItem> mLineItems = Collections.emptyList();;
        private List<LineItem> originalLineItems = Collections.emptyList();
        private SortFilterOption filterOption;
        private SortFilterOption sortOption;

        public LineItemAdapter(List<LineItem> lineItems)
        {

            mLineItems = lineItems;//This is to keep track of last sort opton done on dataset,which will be helpful in removing filter
            // originalLineItems = lineItems;//This is only for filter
        }

        @Override
        public void onBindViewHolder(LineItemHolder holder, int position) {
            holder.bindLineItem(mLineItems.get(position));
        }

        @Override
        public LineItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();

            LayoutInflater inflater = LayoutInflater.from(context);
            //chandan - To Do
            View view = inflater.inflate(R.layout.list_item_list_picklist2, parent, false);

            LineItemHolder viewHolder = new LineItemHolder(view);

            return viewHolder;
        }

        public void sortDataSetByUserOption(SortFilterOption sortOption,boolean bReverseList)
        {
            this.sortOption = sortOption;
            if (bReverseList)
                Collections.reverse(mLineItems);
            else {
                SortItemList objSorting = new SortItemList(mLineItems,sortOption);
                objSorting.performSort();
                objSorting = null;
            }
            notifyDataSetChanged();//Notify Layout manager that dataset has changed to re render
        }

       /* public void filterDatasetByUserOption(SortFilterOption filterOption,String strToFilter)
        {
            this.filterOption = filterOption;
            getFilter().filter(strToFilter);
        }
        public void RemoveFilter(SortFilterOption sortingOption)
        {
            mLineItems = originalLineItems;
            sortDataSetByUserOption(sortOption,false);
        }
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    List<LineItem> filteredResults = null;
                    if (charSequence.length() == 0) {
                        filteredResults = originalLineItems;
                    } else {
                        //provide your custom logic
                        filteredResults = getFilteredResults(charSequence.toString().toLowerCase(),filterOption);
                    }
                    FilterResults results = new FilterResults();
                    results.values = filteredResults;
                    return results;
                }
                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mLineItems = (List<LineItem>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
        protected List<LineItem> getFilteredResults(String constraint,SortFilterOption sortingOption) {
            List<LineItem> results = new ArrayList<>();
            switch (sortingOption)
            {
                case Items:
                {
                    for (LineItem item : originalLineItems) {
                        if (item.getItemName().toLowerCase().contains(constraint)) {
                            results.add(item);
                        }
                    }
                }
                case Location:
                {
                    for (LineItem item : originalLineItems) {
                        if (item.getBinLocation().toLowerCase().contains(constraint)) {
                            results.add(item);
                        }
                    }
                }
                case SalesOrder:
                {
                    for (LineItem item : originalLineItems) {
                        if (item.getItemDesc().toLowerCase().contains(constraint)) {
                            results.add(item);
                        }
                    }
                }
            }
            return results;
        }*/

        @Override
        public int getItemCount() {
            return mLineItems.size();
        }
        //Method to update specific view in view holder
        public void updateViewAtPosition(int nPosition)
        {
            notifyItemChanged(nPosition);
        }
    }

    /*private class UpdatePicklistTask extends AsyncTask<Picklist, Void, Integer> {
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
                                picklist.getId()), picklistJSONStr, result);
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
    }*/

    public void CompleteList_Dialog()
    {

        new AlertDialog.Builder(getContext())
                .setMessage("Are you sure you want to complete this picklist?")
                .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Check if any item is not completely picked and complete or show discrepency screen accordingly.
                        Status picklistStatus = Status.PartiallyPicked;
                        for(int i = 0; i < mPicklist.getLineitems().size();i++)
                        {
                            if(mPicklist.getLineitems().get(i).getmItemStatus() != Status.Picked)
                                break;
                            else
                            {
                                if(i == mPicklist.getLineitems().size() - 1)
                                    picklistStatus = Status.Picked;
                            }

                        }
                        mPicklist.setStatus(picklistStatus);
                        savePicklist(mPicklist, false, String.valueOf(mPicklist.getId()));

                    }
                })
                .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();

    }

    public void showDialog()
    {
        mProgressDialog = ProgressDialog.show(getActivity(),
                "Syncing Data", "");
    }

    public void dismissDialog()
    {
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    public void StaleDataDialog(Context context)
    {
        final Dialog openDialog = new Dialog(context);
        openDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.staledata_dialog);
        openDialog.setCancelable(false);
        Button dialogCloseButton = (Button) openDialog.findViewById(R.id.StalebtnOk);
        dialogCloseButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {


                Intent mainactivity = new Intent(getActivity(), MainActivity.class);
                mainactivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainactivity);
                getActivity().finish();


            }

        });

        openDialog.show();
    }


   
}
