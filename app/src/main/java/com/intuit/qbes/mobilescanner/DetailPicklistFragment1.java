package com.intuit.qbes.mobilescanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.barcode.BarcodeFactory;
import com.intuit.qbes.mobilescanner.barcode.BarcodeScannerDevice;
import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.networking.PicklistHttp;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.E;

/**
 * Created by ckumar5 on 08/01/17.
 */

public class DetailPicklistFragment1 extends Fragment implements BarcodeScannerDevice.ScanDataReceiver,View.OnClickListener,SortingDialog.SortingSelectionDialogListener {


    public static final String EXTRA_PICKLIST = "com.intuit.qbes.mobilescanner.picklist";
    private static final String LOG_STR = "DetailPicklistFragment";

    private RecyclerView mRecyclerView;
    private LineItemAdapter mAdapter = null;

    private Picklist mPicklist = null;
    private DetailPicklistFragment1.Callbacks mCallbacks;

    private BarcodeFactory mBarcodeFactory = null;
    private BarcodeScannerDevice mBarcodeScannerDevice = null;

    private RelativeLayout mLayoutForFilter;
    private RelativeLayout mLayoutForSort;
    private ImageView mReverseSortingOption;
    private TextView mSortOrderSelection;


    private ProgressDialog mProgressDialog;
    private SQLiteDatabaseLineItemHandler db;
    private List<LineItem> lineitems;


    public interface Callbacks {
        void onLineItemSelected(LineItem selectedLineItem);
        void onPicklistSaved(Integer responseCode, Picklist picklist);
        void onBarcodeReady();
    }

    public static DetailPicklistFragment1 newInstance(Picklist picklist)
    {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_PICKLIST, picklist);

        DetailPicklistFragment1 fragment = new DetailPicklistFragment1();
        fragment.setArguments(args);

        return fragment;
    }


    public DetailPicklistFragment1() {
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
        View view = inflater.inflate(R.layout.fragment_detail_picklist2, container, false);

        //Set up Recycle view and its holder
        setUpRecycleView(view);

        //Set up necessary control listener
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
    //Set up necessary control listener
    public void setUpControlListenerForFragment(View view)
    {
        mLayoutForSort = (RelativeLayout) view.findViewById(R.id.sortlayout);
        mReverseSortingOption = (ImageView) view.findViewById(R.id.sorticon);
        mSortOrderSelection = (TextView)view.findViewById(R.id.sortbyselection);
        //mLayoutForFilter = (RelativeLayout)view.findViewById(R.id.filter);
        //chandan - is there any chances of above variables getting null,dont think so
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

    @Override
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
    }

    @Override
    public void onStart() {
        super.onStart();
        db = new SQLiteDatabaseLineItemHandler(getActivity().getApplicationContext());
        lineitems = db.allLineItems(mPicklist.getRecnum());
        //cahdan -start - only for testing
        LineItem obj1 = new LineItem(1,"Redmi","pick it","aaa","1",10,1,2,"abc","_",null);
        LineItem obj2 = new LineItem(1,"Iphone","hardware","aaa","1",10,1,2,"def","def_123",null);
        LineItem obj3 = new LineItem(1,"Motorola","harware","aaa","1",10,1,2,"ghi","def_12",null);
        LineItem obj4 = new LineItem(1,"Zebra","awesome phone","aaa","1",10,1,2,"wow","jkl",null);
        LineItem obj5 = new LineItem(1,"1 plus 3","struggling phone","aaa","1",10,1,2,"mno","wer",null);
        lineitems.add(obj1);
        lineitems.add(obj2);
        lineitems.add(obj3);
        lineitems.add(obj4);
        lineitems.add(obj5);
        //chandan - end

        if(mAdapter ==  null)
            mAdapter = new LineItemAdapter(lineitems);

        mRecyclerView.setAdapter(mAdapter);

        if (mBarcodeScannerDevice == null)
        {
            mBarcodeFactory = new BarcodeFactory();
            mBarcodeScannerDevice = mBarcodeFactory.getDevice();
            if(mBarcodeScannerDevice != null)
            {
                mBarcodeScannerDevice.registerForCallback(this);
                mBarcodeScannerDevice.initializeLibraryResource(getActivity());
            }
            mBarcodeFactory = null;
        }
    }
    //callback from sort Dialog fragment
    @Override
    public void onSortingOptionSelection(SortFilterOption userSelection) {

        switch (userSelection)
        {
            case Items: mSortOrderSelection.setText("Items");break;
            case Location:mSortOrderSelection.setText("Locations");break;
            case SalesOrder:mSortOrderSelection.setText("SalesOrder");break;
            case Status:mSortOrderSelection.setText("Status");break;

        }
        mAdapter.sortDataSetByUserOption(userSelection,false);
    }

   /* @Override
    public void onFilterOptionSelection(SortFilterOption userSelection) {
        mAdapter.filterDatasetByUserOption(userSelection,"Item1");
    }*/

    //callback from scanner device library
    @Override
    public void scanDataReceived(String sData) {
        new AsyncDataUpdate().execute(sData);

    }

    private void initializeBarcode()
    {
        if (mBarcodeScannerDevice != null)
        {
            mBarcodeScannerDevice.releaseDevice();
            mBarcodeFactory = null;
        }
        mBarcodeFactory = new BarcodeFactory();
        mBarcodeScannerDevice = mBarcodeFactory.getDevice();
        if(mBarcodeScannerDevice != null)
        {
            mBarcodeScannerDevice.registerForCallback(this);
            mBarcodeScannerDevice.initializeLibraryResource(getActivity());
        }
        mBarcodeFactory = null;
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
        //mBarcodeReader.unregisterDataListener(this);
        //mBarcodeReader.close();
        //mBarcodeReader = null;
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
                sortDialog.setTargetFragment(DetailPicklistFragment1.this,300);
                sortDialog.show(fm,"sort by");
                break;
            }
            case R.id.sorticon:
            {
                mAdapter.sortDataSetByUserOption(SortFilterOption.Status,true);
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
            mItemName.setText(item.getName());
            mItemDesc.setText(item.getDescription());
            mLocation.setText(String.format("Bin No : %s",item.getBin()));
            mSalesOrder.setText(String.format("Sales Order: %s",item.getPickListID()));
            mQtyToPick.setText(String.format("Qty : %s",item.getQtyToPick()));

        }

        @Override
        public void onClick(View v) {
            if (mCallbacks != null)
            {
                mCallbacks.onLineItemSelected(mItem);
            }
        }


    }
    //Recycle View Adpter
    private class LineItemAdapter extends RecyclerView.Adapter<LineItemHolder> implements Filterable{

        private List<LineItem> mLineItems = Collections.emptyList();;

        private List<LineItem> originalLineItems = Collections.emptyList();

        private SortFilterOption filterOption;

        private SortFilterOption sortOption;

        public LineItemAdapter(List<LineItem> lineItems)
        {

            mLineItems = lineItems;
            originalLineItems = lineItems;
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
                objSorting.performShort();
                objSorting = null;
            }
            notifyDataSetChanged();//Notify Layout manager that dataset has changed to re render
        }

        public void filterDatasetByUserOption(SortFilterOption sortOption,String strToFilter)
        {
            this.filterOption = sortOption;
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
                        filteredResults = getFilteredResults(charSequence.toString().toLowerCase(),sortOption);
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
                        if (item.getName().toLowerCase().contains(constraint)) {
                            results.add(item);
                        }
                    }

                }
                case Location:
                {
                    for (LineItem item : originalLineItems) {
                        if (item.getBin().toLowerCase().contains(constraint)) {
                            results.add(item);
                        }
                    }
                }
                case SalesOrder:
                {
                    for (LineItem item : originalLineItems) {
                        if (item.getDescription().toLowerCase().contains(constraint)) {
                            results.add(item);
                        }
                    }
                }
            }
            return results;
        }

        @Override
        public int getItemCount() {
            return mLineItems.size();
        }
    }

    //process scanned data
    private class AsyncDataUpdate extends
            AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            int idx = -1;
            String dataStr = params[0].toString();
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
                Log.e("Barcode not found",dataStr.toString());
                idx = -1;
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


