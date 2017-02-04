package com.intuit.qbes.mobilescanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.barcode.BarcodeScannerDevice;
import com.intuit.qbes.mobilescanner.barcode.DeviceManager;
import com.intuit.qbes.mobilescanner.model.LineItem;

import java.util.ArrayList;
import java.util.List;

import static com.intuit.qbes.mobilescanner.R.drawable.plus;

/**
 * Created by ashah9 on 1/18/17.
 */

public class SerialNumberFragment extends Fragment implements View.OnClickListener,BarcodeScannerDevice.ScanDataReceiver {

    private RecyclerView mRecyclerView;
    private SerialNumberAdapter mySerialNumberAdapter;
    private EditText mSerialNumberAdded;
    private ImageView mAddSerialNo;
    private TextView  mAdded;
    private TextView mAddedValue;
    private TextView mItemName;
    private LineItem lineitem;
    private String Qty;
    private Button mConfirm;
    private DeviceManager mDeviceManager = null;
    boolean bSerialNumberLimitDialog = false;
  //  private ArrayList<String> test = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


    }

    public static SerialNumberFragment newInstance(LineItem lineitem) {

        SerialNumberFragment fragment = new SerialNumberFragment();
        Bundle args = new Bundle();
        args.putParcelable("lineitem", lineitem);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_serialnumber, container, false);
        Bundle args = getArguments();
        lineitem = args.getParcelable("lineitem");
        mRecyclerView = (RecyclerView) view.findViewById(R.id.serialnumber_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mConfirm = (Button)view.findViewById(R.id.serialno_confirm);
        mAddSerialNo = (ImageView) view.findViewById(R.id.add_serialno);
        mAddSerialNo.setOnClickListener(this);
        mAdded = (TextView) view.findViewById(R.id.serialno_added);
        mAddedValue = (TextView)view.findViewById(R.id.serialno_added_value);
        mItemName = (TextView)view.findViewById(R.id.serialno_item_value);
        mSerialNumberAdded = (EditText) view.findViewById(R.id.AddSno);
        mConfirm.setOnClickListener(this);

        getActivity().setTitle("Add Serial Numbers");

        return view;


    }
    @Override
    public void onStart() {
        super.onStart();

        mItemName.setText(lineitem.getName());
       if(noDecimal(lineitem.getQtyToPick())) {
            Qty = String.valueOf((int)lineitem.getQtyToPick());

            if (lineitem.getSNArr().size() == 0) {
                //mAdded.setText("To be added: " + Qty);
                mAddedValue.setText(Qty);
            } else {
                mAdded.setText("ADDED");// + " " + lineitem.getSNArr().size() + "/" + Qty);
                mAddedValue.setText(lineitem.getSNArr().size() + "/" + Qty);
            }
        }
        else
        {
            Qty = String.valueOf(lineitem.getQtyToPick());

            if (lineitem.getSNArr().size() == 0) {
               // mAdded.setText("To be added: " + Qty);
                mAddedValue.setText(Qty);
            } else {
                mAdded.setText("ADDED");// + " " + lineitem.getSNArr().size() + "/" + Qty);
                mAddedValue.setText(lineitem.getSNArr().size() + "/" + Qty);
            }
        }

        mySerialNumberAdapter = new SerialNumberAdapter(lineitem);

        mRecyclerView.setAdapter(mySerialNumberAdapter);

        //scanner integration
        mDeviceManager = DeviceManager.getDevice(getContext());
        mDeviceManager.registerForCallback(this);
        bSerialNumberLimitDialog = false;
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.add_serialno:

                int position = mySerialNumberAdapter.getItemCount();
                String SerialNumber = mSerialNumberAdded.getText().toString();
                if(mySerialNumberAdapter.getSerialnoList().size() < (int)Double.parseDouble(Qty)) {
                    if (!SerialNumber.equals("")) {
                        mySerialNumberAdapter.add(position, SerialNumber);
                        int count = position + 1;
                        mAdded.setText("ADDED");// + " " + count + "/" + Qty);
                        mAddedValue.setText(count + "/" + Qty);
                        mSerialNumberAdded.getText().clear();

                    }
                }
                else
                {
                    if(!bSerialNumberLimitDialog)
                        SerialNoLimitDialog();

                }
                break;

            case R.id.serialno_confirm:

                if(mySerialNumberAdapter.getSerialnoList().size() < (int)Double.parseDouble(Qty))
                {
                    int value = (int)Double.parseDouble(Qty) - mySerialNumberAdapter.getSerialnoList().size();
                    SerialNoConfirmDialog(value);
                }
                else
                {
                    toProductInfo();
                }
        }


    }

    public void toProductInfo() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            lineitem.setSNArr(mySerialNumberAdapter.getSerialnoList());
            getFragmentManager().popBackStack();

        }
    }

    public void SerialNoLimitDialog()
    {
        new AlertDialog.Builder(getContext())
                .setMessage("Quantity picked cannot exceed quantity to be picked").setCancelable(false)

                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mSerialNumberAdded.getText().clear();
                        bSerialNumberLimitDialog = false;
                    }
                })

                .show();

        bSerialNumberLimitDialog = true;
    }

    public void SerialNoConfirmDialog(int toAdd)
    {
        new AlertDialog.Builder(getContext())
                .setMessage("You need to add" + " " + toAdd + " " + "more serial number(s)").setCancelable(false)

                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       toProductInfo();
                    }
                })
                .show();
    }


    public class SerialNumberAdapter extends RecyclerView.Adapter<SerialNumberAdapter.ViewHolder> {

        ArrayList serialNOList = new ArrayList();

        public SerialNumberAdapter(LineItem lineItem){
           // this.serialNOList = new ArrayList<String>();
         //   this.serialNOList = serialnos;
            this.serialNOList = lineItem.getSNArr();

        }

        @Override
        public SerialNumberAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.serialnumber_row, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindSerialNumber(serialNOList.get(position));

        }



        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView minus;
             TextView serialNO;

            public ViewHolder(View itemView) {
                super(itemView);
                minus = (ImageView) itemView.findViewById(R.id.remove_serialno);
                serialNO = (TextView)itemView.findViewById(R.id.serialnumber_text) ;

                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(getContext())
                                .setMessage("Are you sure you want to remove this serial number?")
                                .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        int position = getAdapterPosition();
                                        try {
                                            int count = getItemCount();
                                            serialNOList.remove(position);
                                            notifyItemRemoved(position);
                                            if (count > 1) {
                                                count--;
                                                mAdded.setText("ADDED");// + " " + count + "/" + Qty);
                                                mAddedValue.setText(count + "/" + Qty);
                                            } else {
                                                mAdded.setText("TO BE ADDED");// + Qty);
                                                mAddedValue.setText(Qty);
                                            }

                                        } catch (ArrayIndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        }                                    }
                                })
                                .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();


                    }
                });


            }


            public void bindSerialNumber(Object sno)
            {
                serialNO.setText(sno.toString());
            }
        }
        public void add(int location, String iName){
            try {
                serialNOList.add(location, iName);
                notifyItemInserted(location);
            }
            catch(ArrayIndexOutOfBoundsException e)
            {e.printStackTrace();}
        }



        @Override
        public int getItemCount() {
            return serialNOList.size();
        }


        public ArrayList<String> getSerialnoList()
        {
            return serialNOList;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:
                toProductInfo();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    public boolean noDecimal(double val)
    {
        if(val%1 == 0)
            return true;
        else
            return false;
    }
    //scanner integration
    @Override
    public void scanDataReceived(String sData) {
        final String sSerialNumber = sData;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mSerialNumberAdded.setText(sSerialNumber);
                onClick(mAddSerialNo);
            }
        });

    }
}







