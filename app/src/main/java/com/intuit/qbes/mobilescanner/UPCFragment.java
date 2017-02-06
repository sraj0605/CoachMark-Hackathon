
package com.intuit.qbes.mobilescanner;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.barcode.BarcodeScannerDevice;
import com.intuit.qbes.mobilescanner.barcode.DeviceManager;
import com.intuit.qbes.mobilescanner.model.LineItem;
// ...

public class UPCFragment extends DialogFragment implements TextWatcher,BarcodeScannerDevice.ScanDataReceiver, View.OnClickListener{

private EditText mUPC_entered;
    private LineItem mLineItem;
    private Drawable mError;
    private Drawable mTickmark;
    private View mUPC_errorline;
    private View mUPC_line;
    private Button mUpc_confirm;
    private TextView mUpc_error_text;
    private DeviceManager mDeviceManager = null;


    public UPCFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public UPCFragment newInstance(LineItem lineItem) {
        UPCFragment frag = new UPCFragment();
        Bundle args = new Bundle();
        args.putParcelable("lineitem", lineItem);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upc, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLineItem = (LineItem)getArguments().getParcelable("lineitem");

        // Get field from view
        mUPC_entered = (EditText)view.findViewById(R.id.upc_value) ;

        mUPC_entered.addTextChangedListener(this);

        mError = ContextCompat.getDrawable(getActivity(), R.drawable.exclamation);
        mError.setBounds(0, 0, mError.getIntrinsicWidth(), mError.getIntrinsicHeight());

        mTickmark = ContextCompat.getDrawable(getActivity(), R.drawable.tickmark);
        mTickmark.setBounds(0, 0, mTickmark.getIntrinsicWidth(), mTickmark.getIntrinsicHeight());

        mUpc_confirm = (Button)view.findViewById(R.id.upc_confirm);
        mUpc_confirm.setOnClickListener(this);

        mUPC_errorline = (View)view.findViewById(R.id.UPCerrorline);
        mUPC_line = (View)view.findViewById(R.id.UPCline);

        mUpc_error_text = (TextView)view.findViewById(R.id.upc_error_text);


        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //Scanner Integration
        mDeviceManager = DeviceManager.getDevice(getContext());
        mDeviceManager.unRegisterDeviceFromCallback(this);
        mDeviceManager.registerForCallback(this);



    }



    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
       mUPC_line.setVisibility(this.getView().VISIBLE);
        mUPC_errorline.setVisibility(this.getView().GONE);
        mUpc_error_text.setVisibility(this.getView().GONE);
        mUPC_entered.setError("",null);
    }


    @Override
    public void scanDataReceived(String sData) {
        final String sBarcode = sData;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mUPC_entered.setText(sBarcode);
                onClick(mUpc_confirm);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.upc_confirm:

            if (mUPC_entered.getText().toString().compareTo(mLineItem.getBarcode()) != 0 && !mUPC_entered.getText().toString().isEmpty()) {

                mUPC_entered.setError("", mError);
                mUPC_errorline.setVisibility(view.VISIBLE);
                mUPC_line.setVisibility(view.GONE);
                mUpc_error_text.setVisibility(view.VISIBLE);

            } else if (mUPC_entered.getText().toString().compareTo(mLineItem.getBarcode()) == 0 && !mUPC_entered.getText().toString().isEmpty()) {
                mUPC_line.setVisibility(view.VISIBLE);
                mUPC_errorline.setVisibility(view.GONE);
                mUpc_error_text.setVisibility(view.GONE);
                mUPC_entered.setError("", mTickmark);
                mLineItem.setBarcodeEntered(mUPC_entered.getText().toString());
                Intent i = new Intent().putExtra("lineitem", mLineItem);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);

                //this.dismiss();

                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Dismissdialog();
                    }
                };

                handler.postDelayed(runnable, 1000);

            } else if (mUPC_entered.getText().toString().isEmpty()) {
                mUPC_entered.setError("", mError);
                mUPC_errorline.setVisibility(view.VISIBLE);
                mUPC_line.setVisibility(view.GONE);
                mUpc_error_text.setVisibility(view.VISIBLE);

            }

                break;
        }
    }

   public void Dismissdialog()
   {
       this.dismiss();

   }

}