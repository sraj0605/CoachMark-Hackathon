package com.intuit.qbes.mobilescanner;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.DeviceDetails;
import com.intuit.qbes.mobilescanner.model.Picklist;
import com.intuit.qbes.mobilescanner.networking.AppController;
import com.intuit.qbes.mobilescanner.networking.DataSync;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ashah9 on 2/20/17.
 */

public class CodeEntryFragment extends Fragment implements DataSync.DataSyncCallback, View.OnClickListener, TextWatcher{

    private EditText mCode;
    private Button mPair;
    private View  mErrorLine;
    private View  Line;
    private TextView mErrorText;
    private String DevicePairingCode;
    private DataSync dataSync = null;
    private DeviceDetails details = new DeviceDetails();
    private String device_id,device_name;
    private String deviceDetailsJSON;
    private String validateTAG  = "Validate";
    private ProgressDialog mProgressDialog;
    private PairSuccessCallback mCallback = null;
    private Handler handler;
    private Runnable runnable;
    private long mLastClickTime = 0;
    public static final String PREFS_NAME = "Service_Response";
    public static final String PAIRING_DETAILS = "Pairing_Details";
    public static final String MOBILE_TIMEOUT = "Mobile_Timeout";
    private Intent ServiceIntent;
    private SharedPreferences settings;
    private DatabaseHandler db;


    public  CodeEntryFragment()
    {

    }

    public static CodeEntryFragment newInstance()
    {
        CodeEntryFragment fragment = new CodeEntryFragment();
        return fragment;
    }

    public interface PairSuccessCallback
    {
        void onPairSuccess();
        void onTimeOut();
        void onDeny();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_code_entry, container, false);
        SetControllers(view);
        mCode.setGravity(Gravity.CENTER_HORIZONTAL);
        mCode.addTextChangedListener(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        mPair.setOnClickListener(this);
        return view;


    }

    public void SetControllers(View view)
    {
        mCode = (EditText)view.findViewById(R.id.Code);

        mPair =  (Button)view.findViewById(R.id.PairButton);
        mErrorLine = (View)view.findViewById(R.id.DevPairErrorline);
        Line = (View)view.findViewById(R.id.DevPairline);
        mErrorText = (TextView)view.findViewById(R.id.DevPairErrorText);


    }
    @Override
    public void onFetchPicklist(List<Picklist> mPicklists) {

        db = new DatabaseHandler(getContext());

        for(int i = 0; i < mPicklists.size() ; i++)
        {
            db.addPickListInBatch(mPicklists.get(i),true);
        }

        if(mPicklists.size() > 0)
        db.storeLastSycTime(db.getDetails().getRealmID());

        if(mCallback !=null) {
            mCallback.onPairSuccess();
        }

        dismissDialog();

    }

    @Override
    public void onUpdatePicklist(Picklist mPicklist, Boolean isSync, Boolean isStale) {

    }

    @Override
    public void onCodeValidation(String response) {


        if(response.compareTo("200") == 0)
        {
            Intent mIntent = new Intent(getContext(), DevicePairingService.class);
            ServiceIntent = mIntent;
            SaveDPdetails(DevicePairingCode,device_id);

            ServiceTimeOut(mIntent);

            getActivity().startService(mIntent);
           // showDialog();
        }
        else if(response.compareTo("NotValid") == 0)
        {
            SetErrorScreen();
            dismissDialog();
        }
        else if(response.compareTo("NoInternet") == 0)
        {
            NoInternetDialog(getContext());
            dismissDialog();


        }
        else if(response.compareTo("ServiceIssue") == 0)
        {
            ServerIssueDialog(getContext());
            dismissDialog();
        }
    }

    @Override
    public void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    @Override
     public void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("PollingResponse"));

        settings = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String value = settings.getString("Response", "");

        if(value!=null && value != "")
            HandleServiceResponse(value);

        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        if(ServiceIntent!=null)
        getActivity().stopService(ServiceIntent);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(
                mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.PairButton:
            {
                if (SystemClock.elapsedRealtime() - mLastClickTime > 3000) {
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (!(mCode.getText().toString().isEmpty()) && !(mCode.getText().toString().length() < 4))

                    {
                        DevicePairingCode = mCode.getText().toString();
                        details.setOTP(DevicePairingCode);
                        GetDeviceNameID();
                        deviceDetailsJSON = DeviceDetails.JSONStringFromDeviceDetails(details);
                        dataSync = new DataSync();
                        dataSync.ValidateDevicePairing(deviceDetailsJSON, getContext(), this);
                        showDialog();

                        break;
                    } else {
                        SetErrorScreen();

                    }
                }
            }
            default:
                break;

        }
    }

    public void SetErrorScreen()
    {
        mErrorLine.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
        Line.setVisibility(View.GONE);


    }

    public void RemoveErrorScreen()
    {
        mErrorLine.setVisibility(View.GONE);
        mErrorText.setVisibility(View.GONE);
        Line.setVisibility(View.VISIBLE);


    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {



    }

    @Override
    public void afterTextChanged(Editable editable) {

        if(mCode.getText().hashCode() == editable.hashCode())
        {
            RemoveErrorScreen();
        }



    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");

            if(message!=null && message != "")
            HandleServiceResponse(message);

        }



    };

    public void GetDeviceNameID()
    {
      device_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

          device_name = android.os.Build.MODEL;

          details.setDeviceID(device_id);
          details.setDeviceName(device_name);



    }

    @Override
    public void onStop() {
        super.onStop();
        if(AppController.getInstance().getRequestQueue()!=null)
        {
            AppController.getInstance().getRequestQueue().cancelAll(validateTAG);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (PairSuccessCallback) context;
        }
        catch (Exception exp)
        {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public void showDialog()
    {
        mProgressDialog = ProgressDialog.show(getActivity(),
                "Please wait, we're working on it!", "");
    }

    public void dismissDialog()
    {
        mProgressDialog.dismiss();
    }

    public void ServiceTimeOut(final Intent sIntent)
    {
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                //getActivity().stopService(sIntent);
                MobileTimeOut(true);
            }
        };
        handler.postDelayed(runnable, 10*60*1000);

    }

    public void HandleServiceResponse(String response)
    {
        if(response.compareTo("success") == 0)
        {
            dataSync = new DataSync();
           dataSync.FetchPicklists(getContext(),this);

        }
        else if(response.compareTo("timeout") == 0)
        {
            if(mCallback !=null) {
                mCallback.onTimeOut();
            }
            MobileTimeOut(false);
            dismissDialog();

        }
        else if(response.compareTo("deny") == 0)
        {
            if(mCallback !=null) {
                mCallback.onDeny();
            }
            dismissDialog();

        }
        else if(response.compareTo("NoInternet") == 0)
        {
            NoInternetDialog(getContext());
            dismissDialog();

        }
        else if(response.compareTo("ServiceError") == 0)
        {
            ServerIssueDialog(getContext());
            dismissDialog();

        }
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(
                mMessageReceiver);
        handler.removeCallbacks(runnable);

        //Reset shared pref
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Response", "");
        editor.commit();
    }

    public void NoInternetDialog(Context context)
    {
        final Dialog openDialog = new Dialog(context);
        openDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.connection_error_dialog);
        Button dialogCloseButton = (Button) openDialog.findViewById(R.id.NetowrkbtnOk);
        openDialog.setCancelable(false);
        dialogCloseButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {


                openDialog.dismiss();

            }

        });

        openDialog.show();
    }

    public void ServerIssueDialog(Context context)
    {
        final Dialog openDialog = new Dialog(context);
        openDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.serverissue_dialog);
        Button dialogCloseButton = (Button) openDialog.findViewById(R.id.ServerIssuebtnOk);
        dialogCloseButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {


                openDialog.dismiss();

            }

        });

        openDialog.show();
    }

    public void SaveDPdetails(String otp, String device_id)
    {
        SharedPreferences settings = getActivity().getSharedPreferences(PAIRING_DETAILS, MODE_PRIVATE);

        // Writing data to SharedPreferences
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("otp", otp);
        editor.putString("device_id", device_id);
        editor.commit();

    }


    public void MobileTimeOut(Boolean TimeOut)
    {
        SharedPreferences settings = getActivity().getSharedPreferences(MOBILE_TIMEOUT, MODE_PRIVATE);

        // Writing data to SharedPreferences
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isTimeOut", TimeOut);
        editor.commit();

    }
}



