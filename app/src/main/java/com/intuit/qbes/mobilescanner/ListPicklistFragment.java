package com.intuit.qbes.mobilescanner;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.model.Status;
import com.intuit.qbes.mobilescanner.networking.AppController;
import com.intuit.qbes.mobilescanner.networking.DataSync;
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;

import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.Intents.Insert.ACTION;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListPicklistFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListPicklistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListPicklistFragment extends Fragment implements PickingReceivingAdapter.AdapterCallback, DataSync.DataSyncCallback{

    private static final String LOG_TAG = "ListPicklistFragment";
    public static final String EXTRA_PICKLIST = "com.intuit.qbes.mobilescanner.picklist";

    private RecyclerView mRecyclerView;
    private Callbacks mCallbacks;
    private List<Picklist> mPicklists = null;
    private List<Picklist> dummyPicklists = null;
    private ProgressDialog mProgressDialog;
    private DatabaseHandler db;
    private DataSync dataSync = null;
    private List<LineItem> lineitems1 = null;
    private List<LineItem> lineitems2 = null;
    private List<LineItem> lineitems3 = null;

    private static final String fetchTAG = "Fetch";
    private List<SerialLotNumber> serialnos1 = new ArrayList<SerialLotNumber>();
    private List<SerialLotNumber> serialnos2 = new ArrayList<SerialLotNumber>();
    private List<SerialLotNumber> serialnos3 = new ArrayList<SerialLotNumber>();
    private List<SerialLotNumber> serialnos4 = new ArrayList<SerialLotNumber>();
    private List<SerialLotNumber> serialnos5 = new ArrayList<SerialLotNumber>();
    private PickingReceivingAdapter adapter = null;
	 @Override
    public void onFetchPicklist(List<Picklist> mPicklists) {
      //  List<Picklist> test = new ArrayList<>();
      //  test = mPicklists;
    }

    @Override
    public void onUpdatePicklist(Picklist mPicklist, Boolean isSync, Boolean isStale) {

    }

    @Override
    public void onCodeValidation(String response) {

    }


    public interface Callbacks {
        void onPickSelected(Picklist selectedPick);
    }

    @Override
    public void onClickCallback(Picklist picklist) {
        mCallbacks.onPickSelected(picklist);
    }

    public ListPicklistFragment() {
        // Required empty public constructor
    }
    public static ListPicklistFragment newInstance(Picklist picklist)
    {

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_PICKLIST, picklist);
        ListPicklistFragment fragment = new ListPicklistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiverWithSyncAdapter();
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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Read all picklists from DB

        DatabaseHandler db = new DatabaseHandler(getContext());
        if(mPicklists == null)
            mPicklists = db.allPickLists(1,"default");

        /*we can retry*/
        if(mPicklists != null)
        {
            for(int i = 0; i < mPicklists.size();i++)
            {
                mPicklists.get(i).setTotalitems(db.getItemCountForPicklist(mPicklists.get(i).getId()));
            }
        }
        //Set up adapter
        if(adapter == null)
         adapter = new PickingReceivingAdapter(mPicklists, this);
        mRecyclerView.setAdapter(adapter);
        //Trigger a refresh to sync adapter
        registerReceiverWithSyncAdapter();
        SyncUtils.TriggerRefresh();
    }

    public void refreshUI()
    {
        DatabaseHandler db = new DatabaseHandler(getContext());
        mPicklists = db.allPickLists(1,"default");
        if(mPicklists != null)
        {
            for(int i = 0; i < mPicklists.size();i++)
            {
                mPicklists.get(i).setTotalitems(db.getItemCountForPicklist(mPicklists.get(i).getId()));
            }
        }
        adapter.updatePicklist(mPicklists);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (Callbacks) context;
        }
        catch (ClassCastException exp)
        {
            exp.printStackTrace();
        }
        catch(Exception exp)
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
            AppController.getInstance().getRequestQueue().cancelAll(fetchTAG);
        }
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
               // fetchPicklists();
              //  dataSync = new DataSync();
              //  dataSync.FetchPicklists(getContext(), this);
                break;

            case android.R.id.home:
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    //Right now - it will be used only for Unit testing
    public  void UpdatePickList(List<Picklist> obj)
    {
        mPicklists.remove(2);
    }
//Dummy as of now
    public List<Picklist> createList(int size) {

        List<Picklist> result = new ArrayList<Picklist>();
        if (lineitems1 == null || lineitems2 == null || lineitems3 == null) {
            lineitems1 = new ArrayList<LineItem>();
            lineitems2 = new ArrayList<LineItem>();
            lineitems3 = new ArrayList<LineItem>();

        }
        //cahdan -start - only for testing
        LineItem P1obj1 = new LineItem(1,1,1,"Anchor - 12x1","Anchor, 12x1 RedCap",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",35,0,"1258654257595","Rack 1",12,"custom",serialnos1,"false","false","false", Status.NotPicked);
        LineItem P1obj2 = new LineItem(2,2,2,"Base - BL","Anchor Base, Black",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",3,0,"8901057310062","Rack 1",12,"custom",serialnos1,"true","true","false",Status.NotPicked);
        LineItem P1obj3 = new LineItem(3,3,3,"Connectors","UR - Connectors",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",4,0,"8901361302449","Rack 1",12,"custom",serialnos1,"true","true","false",Status.NotPicked);
        LineItem P1obj4 = new LineItem(4,4,4,"Drum - AL","Drum, 4, Aluminum",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",2,0,"QB:010335866099","Rack 1",12,"custom",serialnos1,"true","true","false",Status.NotPicked);
        LineItem P1obj5 = new LineItem(5,5,5,"Edge - CL","Leading Edge,Clear",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",12,0,"QB:0103358660132","Rack 1",12,"custom",serialnos1,"true","true","false",Status.NotPicked);
        LineItem P1obj6 = new LineItem(5,5,5,"Grommet 4","Grommet, #4 Brass",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",50,0,"QB:010335866078","Rack 1",12,"custom",serialnos1,"true","true","false",Status.NotPicked);
        LineItem P1obj7 = new LineItem(5,5,5,"Hook","Retainer Hook, Manual Strap",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",2,0,"QB:0103358660142","Rack 1",12,"custom",serialnos1,"true","true","false",Status.NotPicked);
        LineItem P2obj1 = new LineItem(1,1,1,"1500 - PM","POWER MANUAL 1500",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",3,0,"QB:0103358660135","Rack 1",12,"custom",serialnos1,"false","false","false", Status.NotPicked);
        LineItem P2obj2 = new LineItem(2,2,2,"Cord - 12G","Electrical Cord, 12 Gauge",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",3,0,"QB:010335866082","Rack 1",12,"custom",serialnos1,"true","true","false",Status.NotPicked);
        LineItem P2obj3 = new LineItem(3,3,3,"Pool Covers:Cover - AQ","Pool Cover, Aqua",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",2,0,"QB:0103358660122","Rack 1",12,"custom",serialnos1,"true","true","false",Status.NotPicked);
        LineItem P2obj4 = new LineItem(3,3,3,"Pool Covers:Cover - LB","Pool Cover, Light Blue",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",2,0,"QB:0103358660121","Rack 1",12,"custom",serialnos1,"true","true","false",Status.NotPicked);
        LineItem P3obj1 = new LineItem(1,1,1,"Pumps:Pump 1800","Rule Pump 1800",1,"sales-1",1,"2017-01-10","2017-01-10","note1","ea",1,0,"QB:0103358660128","Rack 1",12,"custom",serialnos1,"false","false","false", Status.NotPicked);
        lineitems1.add(P1obj1);
        lineitems1.add(P1obj2);
        lineitems1.add(P1obj3);
        lineitems1.add(P1obj4);
        lineitems1.add(P1obj5);
        lineitems1.add(P1obj6);
        lineitems1.add(P1obj7);
        lineitems2.add(P2obj1);
        lineitems2.add(P2obj2);
        lineitems2.add(P2obj3);
        lineitems2.add(P2obj4);
        lineitems3.add(P3obj1);
        Picklist p1 = new Picklist(1, "1",1, "Order number: 8804",1,1,Status.NotPicked,1,"note1","show",1,"2017-01-10",lineitems1,"false");
        Picklist p2 = new Picklist(2, "1",1, "Order number: 8795",1,1,Status.NotPicked,1,"note1","show",1,"2017-01-10",lineitems2,"false");
        Picklist p3 = new Picklist(3, "1",1, "Order number: 8792",1,1,Status.NotPicked,1,"note1","show",1,"2017-01-10",lineitems3,"false");
        p1.setTotalitems(lineitems1.size());
        result.add(p1);
        p2.setTotalitems(lineitems2.size());
        result.add(p2);
        p3.setTotalitems(lineitems3.size());
        result.add(p3);
        mPicklists = new ArrayList<>();
        mPicklists.add(p1);
        mPicklists.add(p2);
        mPicklists.add(p3);
        return result;
    }
    //Listen for syncadapter
    public class SyncReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getContext().unregisterReceiver(this);
            refreshUI();
        }
    }

    private void registerReceiverWithSyncAdapter()
    {
        SyncReceiver myReceiver = new SyncReceiver();
        IntentFilter i = new IntentFilter(SyncAdapter.SYNC_STATUS);
        getContext().registerReceiver(myReceiver,i);
    }
}
