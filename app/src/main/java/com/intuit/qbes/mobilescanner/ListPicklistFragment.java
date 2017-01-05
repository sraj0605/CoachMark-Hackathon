package com.intuit.qbes.mobilescanner;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.networking.PicklistHttp;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListPicklistFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListPicklistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListPicklistFragment extends Fragment{

    private static final String LOG_TAG = "ListPicklistFragment";

    private RecyclerView mRecyclerView;
    private Callbacks mCallbacks;
    private List<Picklist> mPicklists;
    private ProgressDialog mProgressDialog;
    private SQLiteDatabaseHandler db;

    public interface Callbacks {
        void onPickSelected(Picklist selectedPick);
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
        RecyclerView.ItemDecoration itemDecoration = new
                com.intuit.qbes.mobilescanner.DividerItemDecoration(getActivity(), com.intuit.qbes.mobilescanner.DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        return view;


    }

    private void setupAdapter()
    {
        if (isAdded())
        {
            if (mPicklists == null)
            {
                mPicklists = new ArrayList<Picklist>();
            }
            mRecyclerView.setAdapter(new PicklistAdapter());
        }
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

    private class ListPicklistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNumber;
        private ImageView mStatus;
        private TextView mDate;
        private TextView mName;

        private Picklist mPick;

        public ListPicklistHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            mNumber = (TextView) itemView.findViewById(R.id.list_picklist_num);
            mStatus = (ImageView) itemView.findViewById(R.id.list_picklist_status);
            mDate = (TextView) itemView.findViewById(R.id.list_picklist_date);
            mName = (TextView) itemView.findViewById(R.id.list_picklist_name);
        }

        public void bindPickList(com.intuit.qbes.mobilescanner.model.Picklist pList)
        {
            mPick = pList;
            String dateStr = "";

            //ToDo: Change this to make display date's format as per device's locale settings
            dateStr = MSUtils.MMddyyyyFormat.format((Date)pList.getOrderDate());

            mDate.setText(dateStr);
            mNumber.setText(pList.getNumber());
            mName.setText(pList.getName());

            if (pList.getStatus() == 1)
            {
                mStatus.setImageResource(R.drawable.ic_assignment_black_24dp);
            }
            else
            {
                mStatus.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
            }
        }

        @Override
        public void onClick(View v) {
            if (mCallbacks != null) {
                mCallbacks.onPickSelected(mPick);
            }
        }

    }

    private class PicklistAdapter extends RecyclerView.Adapter<ListPicklistHolder> {

        public PicklistAdapter() {
        }

        @Override
        public void onBindViewHolder(ListPicklistHolder holder, int position) {
                holder.bindPickList(mPicklists.get(position));

        }

        @Override
        public ListPicklistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.list_item_list_picklist, parent, false);

            ListPicklistHolder viewHolder = new ListPicklistHolder(view);
            return viewHolder;
        }

        @Override
        public int getItemCount() {
            return mPicklists.size();
        }
    }

    private class FetchPicklistTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try
            {

                String urlStr = String.format("%s/picklists", MSUtils.getServerUrl(getActivity()));

                String result = new PicklistHttp().
                        getUrlString(urlStr);

//                String result = "[{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8793\",\"shipdate\":\"20211115\",\"status\":1}]";

//                String result = "[{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8793\",\"shipdate\":\"20211115\",\"status\":1},{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8794\",\"shipdate\":\"20211115\",\"status\":1},{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8795\",\"shipdate\":\"20211115\",\"status\":1},{\"_id\":26021,\"customerjob\":\"Dunning's Pool Depot, Inc.:Las Wages Store # 554\",\"date\":\"2021115\",\"items\":[{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"},{\"Needed\":5,\"_id\":26024,\"barcode\":\"6921734900210\",\"desc\":\"POWER MANUAL 1500\",\"name\":\"1500-PM\",\"to_pick\":0,\"uom\":\"ea\"},{\"Needed\":8,\"_id\":26023,\"barcode\":\"QB:0103358660139\",\"desc\":\"Pool Cover, Forest Green\",\"name\":\"Pool Covers:Cover -FG\",\"to_pick\":8,\"uom\":\"ea\"}],\"num\":\"8796\",\"shipdate\":\"20211115\",\"status\":1}]";
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
            setupAdapter();
        }


    }

}
