package com.intuit.qbes.mobilescanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.networking.PicklistHttp;
import com.symbol.emdk.barcode.ScannerConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListPicklistFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListPicklistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListPicklistFragment extends Fragment implements PickingReceivingAdapter.AdapterCallback{

    private static final String LOG_TAG = "ListPicklistFragment";

    private RecyclerView mRecyclerView;
    private Callbacks mCallbacks;
    private List<Picklist> mPicklists;
    private ProgressDialog mProgressDialog;
    private SQLiteDatabaseHandler db;

    public interface Callbacks {
        void onPickSelected(Picklist selectedPick);
    }

    @Override
    public void onClickCallback(Picklist picklist) {
        db = new SQLiteDatabaseHandler(getActivity().getApplicationContext());
        mPicklists =  db.allPickLists();
        mCallbacks.onPickSelected(mPicklists.get(0));
       // mCallbacks.onPickSelected(picklist);
    }

    public ListPicklistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchPicklists();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_picklist, container, false);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_picklist_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        PickingReceivingAdapter pa = new PickingReceivingAdapter(createList(5), this);
        mRecyclerView.setAdapter(pa);

        return view;


    }

    private void fetchPicklists()
    {
        mProgressDialog = ProgressDialog.show(getActivity(),
                "Fetching Picklists", "Working real hard on it", true);
        new FetchPicklistTask().execute();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_picklist, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            case R.id.action_sync:
                fetchPicklists();
                break;

            case android.R.id.home:
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void updatePicklist(Picklist picklist)
    {
        if (picklist == null)
        {
            throw new IllegalArgumentException("Picklist is null");
        }

        int idx = mPicklists.indexOf(picklist);

        if (idx == -1)
        {
            throw new IllegalArgumentException("Picklist is not found");
        }

        mPicklists.set(idx, picklist);
        mRecyclerView.getAdapter().notifyItemChanged(idx);
        mRecyclerView.scrollToPosition(idx);
    }



    private class FetchPicklistTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try
            {

                String urlStr = String.format("%s/picklists", MSUtils.getServerUrl(getActivity()));
                //chandan- commenting for alpha

                /*String result = new PicklistHttp().
                        getUrlString(urlStr);*/

//                String result = "[{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8793\",\"shipdate\":\"20211115\",\"status\":1}]";

                String result = "[{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8793\",\"shipdate\":\"20211115\",\"status\":1},{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8794\",\"shipdate\":\"20211115\",\"status\":1},{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8795\",\"shipdate\":\"20211115\",\"status\":1},{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8796\",\"shipdate\":\"20211115\",\"status\":1}]";
                Log.d(LOG_TAG, "Fetch URL: " + urlStr);
                Log.i(LOG_TAG, "Fetch contents of URL: " + result);

                mPicklists = Picklist.picklistsFromJSON(result, getActivity().getApplicationContext());
                Picklist.StorePickList(mPicklists, getContext());

                db = new SQLiteDatabaseHandler(getActivity().getApplicationContext());
                mPicklists =  db.allPickLists();
            }
            catch (Exception ioe)
            {
                Log.e(LOG_TAG, "Failed to fetch URL: ", ioe);
                db = new SQLiteDatabaseHandler(getActivity().getApplicationContext());
                mPicklists =  db.allPickLists();
            }
//            catch (IOException ioe)
//            {
//                Log.e(LOG_TAG, "Failed to fetch URL: ", ioe);
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressDialog.dismiss();
          //  setupAdapter();
        }


    }


//Dummy as of now
    private List<Picklist> createList(int size) {

        List<Picklist> result = new ArrayList<Picklist>();
        for (int i=1; i <= size; i++) {
            Picklist pi = new Picklist();
            pi.setName("Picklist" + " " + i);
            pi.setTotalItems(35*i);
            pi.setNote("Note:" +" " + i);

            result.add(pi);

        }

        return result;
    }

 /*   public class PickingAdapter extends RecyclerView.Adapter<PickingAdapter.PickingViewHolder> {

        private List<Picklist> pickingdata;

        public PickingAdapter(List<Picklist> pickingdata) {
            this.pickingdata = pickingdata;
        }

        @Override
        public PickingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.userdashboard_layout, viewGroup, false);

            return new PickingViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PickingViewHolder pickingViewHolder, int i) {
            Picklist pi = pickingdata.get(i);
            pickingViewHolder.mPicklistName.setText(pi.getName());
            pickingViewHolder.mPicklistItems.setText(String.valueOf(pi.getTotalItems()) + " " + "items");
            pickingViewHolder.mPicklistNote.setText(pi.getNote());

            pickingViewHolder.bindPickList(pi);
        }



        @Override
        public int getItemCount() {
            return pickingdata.size();
        }

        public class PickingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            protected TextView mPicklistName;
            protected TextView mPicklistItems;
            protected TextView mPicklistNote;
            protected Picklist mPick;



            public PickingViewHolder(View v) {
                super(v);
                mPicklistName  =  (TextView) v.findViewById(R.id.picklist_name);
                mPicklistItems = (TextView)  v.findViewById(R.id.picklist_total_items);
                mPicklistNote = (TextView)  v.findViewById(R.id.picklist_note);

                v.setOnClickListener(this);
            }

            public void bindPickList(Picklist pList)
            {
                mPick = pList;
            }

            @Override
            public void onClick(View view) {

                if (mCallbacks != null) {
                    mCallbacks.onPickSelected(mPick);
                }
            }
        }

    }*/
}
