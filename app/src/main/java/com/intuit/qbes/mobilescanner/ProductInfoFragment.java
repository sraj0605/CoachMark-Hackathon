package com.intuit.qbes.mobilescanner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.barcode.BarcodeScannerDevice;
import com.intuit.qbes.mobilescanner.barcode.DeviceManager;
import com.intuit.qbes.mobilescanner.model.LineItem;
import com.intuit.qbes.mobilescanner.model.Status;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ashah9 on 1/8/17.
 */

public class ProductInfoFragment extends Fragment implements View.OnClickListener, TextWatcher,BarcodeScannerDevice.ScanDataReceiver {

    public static final String EXTRA_LINEITEM = "com.intuit.qbes.mobilescanner.lineitem";
    public static final String BARCODE_ENTERED = "BARCODE_ENTERED";
    private static final String LOG_TAG = "ProductInfoFragment";
    private View view;
    private LineItem mlineItem;
    private TextView mItemName;
    private TextView mItemDescription;
    private EditText mQty_picked;
    private TextView mQtyToPick;
    private TextView mLocation;
    private View error;
    private TextView mQty_picked_error;
    private TextView mQty_picked_label;
    private TextView mSalesOrder;

    private ImageView mIncrement;
    private ImageView mDecrement;
    private TextView mSerialNo;
    private TextView mSerialView;
    private TextView mUPC_ErrorText;
    private View mUPC_background;
    private TextView mUPC_Value;
    private TextView mUPC_Header;
    private TextView mSNO_Header;
    private TextView mLocationHeader;
    private ImageView mBin;
    private View mVDivider;
    private View mHDivider;
    private Button mConfirm;
    private boolean isInt;
    private Callbacks mCallbacks = null;
    private String mbarcodePassed;
    private static DeviceManager mDeviceManager = null;


    public interface Callbacks {
        void onSerialNumberClicked(LineItem lineitem);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mlineItem = (LineItem) getArguments().getParcelable(EXTRA_LINEITEM);
        mbarcodePassed = getArguments().getString(BARCODE_ENTERED);
        if (mlineItem == null && savedInstanceState != null)
        {
            mlineItem = (LineItem) savedInstanceState.getParcelable(EXTRA_LINEITEM);
            mbarcodePassed = savedInstanceState.getString(BARCODE_ENTERED);
        }
        
        setHasOptionsMenu(true);


    }

    public ProductInfoFragment()
    {

    }


    public static ProductInfoFragment newInstance(LineItem lineitem,String barcodePassed)
    {

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_LINEITEM, lineitem);
        args.putString(BARCODE_ENTERED,barcodePassed);
        ProductInfoFragment fragment = new ProductInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        
        view = inflater.inflate(R.layout.fragment_product_info, container, false);

        mIncrement = (ImageView)view.findViewById(R.id.increase);

        mDecrement = (ImageView)view.findViewById(R.id.decrease);

        mUPC_Value =   (TextView)view.findViewById(R.id.item_upc) ;

        mUPC_Header = (TextView)view.findViewById(R.id.UPC_code);

        mSNO_Header = (TextView)view.findViewById(R.id.SNO);

        mLocationHeader = (TextView)view.findViewById(R.id.location);

        mBin = (ImageView)view.findViewById(R.id.bin);

        mUPC_ErrorText = (TextView)view.findViewById(R.id.UPC_Errortext);

        mConfirm = (Button)view.findViewById(R.id.button_confirm);

        mSerialNo = (TextView) view.findViewById(R.id.item_SNO);

        mSerialView = (TextView)view.findViewById(R.id.item_ViewSNO);

        mVDivider = (View)view.findViewById(R.id.vdivider);

        mHDivider = (View)view.findViewById(R.id.divider3);

        error = (View)view.findViewById(R.id.errordivider);

        mQty_picked_error = (TextView)view.findViewById(R.id.qty_picked_error);
        mQty_picked_label = (TextView)view.findViewById(R.id.qty_picked);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mQty_picked = (EditText)view.findViewById(R.id.qty_picked_value);


        mIncrement.setOnClickListener(this);
        mDecrement.setOnClickListener(this);
        mSerialNo.setOnClickListener(this);
        mSerialView.setOnClickListener(this);
        mConfirm.setOnClickListener(this);

        mQty_picked.addTextChangedListener(this);

       // mUPC_Value.addTextChangedListener(this);

        mUPC_Value.setOnClickListener(this);

        return view;


    }
    @Override
    public void onStart() {
        super.onStart();
        //chandan - There can be case when serial number aarray will be null hence putting null check
        if(mlineItem.getSerialLotNumbers() != null && mlineItem.getBarcodeEntered()!= null) {
            if (mlineItem.getSerialLotNumbers().size() > 0 && mlineItem.getBarcodeEntered().isEmpty())
                showUPCDialog();
        }

        mUPC_background = (View)view.findViewById(R.id.UPC_Error);
        mQty_picked.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(11, 4)});

        if(mlineItem.getSerialLotNumbers() != null && mlineItem.getSerialLotNumbers().size() == 0) {
            mSerialView.setVisibility(view.GONE);
        }
        else {
            mSerialNo.setVisibility(view.GONE);
            mSerialView.setPaintFlags(mSerialView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
        if(mlineItem.getBarcode() != null && mlineItem.getBarcode().isEmpty()) {
            mUPC_Header.setVisibility(view.GONE);
            mUPC_Value.setVisibility(view.GONE);
        }

        if(mlineItem.isShowSerialNo() == false) {
            mSNO_Header.setVisibility(view.GONE);
            mSerialNo.setVisibility(view.GONE);
            mSerialView.setVisibility(view.GONE);
            mVDivider.setVisibility(view.GONE);

        }
        if(mlineItem.isShowSerialNo() == false && (mlineItem.getBarcode().isEmpty())) {
            mHDivider.setVisibility(view.GONE);
        }
        if(mbarcodePassed.compareTo("") != 0)
        {
            mUPC_Value.setText(mbarcodePassed);
            if(mlineItem.isShowSerialNo() == true) {

                mQty_picked.setText(String.valueOf(mlineItem.getSerialLotNumbers().size()));
            }

            else {
                if (noDecimal(mlineItem.getQtyPicked())) {

                    mQty_picked.setText(String.valueOf((int) mlineItem.getQtyPicked()));
                }
                else
                {
                    mQty_picked.setText(String.valueOf(mlineItem.getQtyPicked()));
                }
            }
            onClick(mIncrement);
        }
        setControllers(mlineItem, view);
        mDeviceManager = DeviceManager.getDevice(getContext());
        mDeviceManager.unRegisterDeviceFromCallback(this);
        mDeviceManager.registerForCallback(this);
    }

    public void setControllers(LineItem lineitem, View view)
    {

        mItemName = (TextView)view.findViewById(R.id.item_Name);
        mItemDescription = (TextView)view.findViewById(R.id.item_description);
        mLocation = (TextView)view.findViewById(R.id.item_location);
        mSalesOrder = (TextView)view.findViewById(R.id.item_salesorder);
        mQtyToPick = (TextView)view.findViewById(R.id.item_qty);


        mItemName.setText(lineitem.getItemName().toString());
        mItemDescription.setText(lineitem.getItemDesc().toString());
        mLocation.setText(lineitem.getBinLocation().toString());
        if(lineitem.getBarcode().equals(lineitem.getBarcodeEntered()))
          mUPC_Value.setText(lineitem.getBarcode());

        //  mSalesOrder.setText(lineitem.getBin().toString());   Dummy for now

        if(noDecimal(lineitem.getQtyToPick())){

            mQtyToPick.setText((String.valueOf((int)lineitem.getQtyToPick())));
        }
        else
        {
            mQtyToPick.setText(String.valueOf(lineitem.getQtyToPick()));
        }

        if(lineitem.isShowSerialNo() == true) {

            mQty_picked.setText(String.valueOf(lineitem.getSerialLotNumbers().size()));
        }

        else {
            if (noDecimal(lineitem.getQtyPicked())) {

            mQty_picked.setText(String.valueOf((int) lineitem.getQtyPicked()));

        }
        else {
            mQty_picked.setText(String.valueOf(lineitem.getQtyPicked()));

            }
        }

        getActivity().setTitle(mItemName.getText().toString());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        if(mQty_picked.getText().hashCode() == editable.hashCode()) {

            if (editable.length() == 0) {
                mQty_picked.setText("0");
            }

            try {
                if (editable.charAt(0) == '.') {
                    mQty_picked.setText("0.");
                }
            }
            catch(Exception e)
            {

            }
            if (Double.parseDouble(mQty_picked.getText().toString()) > mlineItem.getQtyToPick()) {

                error.setVisibility(view.VISIBLE);
                mQty_picked_error.setVisibility(view.VISIBLE);
                mQty_picked_label.setVisibility(View.GONE);


            } else {
                mQty_picked_error.setVisibility(view.GONE);
                error.setVisibility(view.GONE);
                mQty_picked_label.setVisibility(View.VISIBLE);

            }
            if(!mlineItem.getBarcode().isEmpty()) {

                if (mUPC_Value.getText().toString().isEmpty() && Double.parseDouble(mQty_picked.getText().toString()) == 0 && mlineItem.getSerialLotNumbers().size() == 0) {
                    mUPC_background.setVisibility(view.GONE);
                    mUPC_ErrorText.setVisibility(view.GONE);
                } else if (mUPC_Value.getText().toString().isEmpty() && (Double.parseDouble(mQty_picked.getText().toString()) > 0 || (mlineItem.getSerialLotNumbers().size() > 0))) {
                    mUPC_background.setVisibility(view.VISIBLE);
                    mUPC_ErrorText.setVisibility(view.VISIBLE);

                }
            }
           if(mlineItem.isShowSerialNo() == true) {
            mlineItem.setQtyPicked(mlineItem.getSerialLotNumbers().size());
           }
            else {
            mlineItem.setQtyPicked(Double.parseDouble(mQty_picked.getText().toString()));
            }

        }


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.increase:
                mQty_picked = (EditText)view.findViewById(R.id.qty_picked_value);
                mQtyToPick = (TextView)view.findViewById(R.id.item_qty);
                isInt  = isInteger(mQty_picked.getText().toString());
                if(isInt)
                {
                    int val;
                    val = Integer.parseInt(mQty_picked.getText().toString());
                    val = val + 1;
                    BigDecimal tempval = BigDecimal.valueOf(val);
                    if(isInteger(mQtyToPick.getText().toString()))
                    {
                        if(val <= Integer.parseInt(mQtyToPick.getText().toString())) {
                            mQty_picked.setText(String.valueOf(val));
                            mlineItem.setQtyPicked(Double.parseDouble(String.valueOf(val)));
                        }

                    }
                    else {
                        int comparison = tempval.compareTo(BigDecimal.valueOf(Double.parseDouble(mQtyToPick.getText().toString())));
                        if (comparison == -1 || comparison == 0) {
                            mQty_picked.setText(String.valueOf(val));
                            mlineItem.setQtyPicked(Double.parseDouble(String.valueOf(val)));
                        }
                    }
                }
                else
                {
                    BigDecimal val;
                    val = BigDecimal.valueOf(Double.parseDouble(mQty_picked.getText().toString()));
                    val = val.add(BigDecimal.valueOf(1));

                    int comparison;
                    if(isInteger(mQtyToPick.getText().toString()))
                    {
                        comparison = val.compareTo(BigDecimal.valueOf(Integer.parseInt(mQtyToPick.getText().toString())));
                        if( comparison == -1 || comparison == 0) {
                            mQty_picked.setText(String.valueOf(val));
                            mlineItem.setQtyPicked(Double.parseDouble(String.valueOf(val)));
                        }


                    }
                    else {
                        comparison = val.compareTo(BigDecimal.valueOf(Double.parseDouble(mQtyToPick.getText().toString())));
                        if (comparison == -1 || comparison == 0) {
                            mQty_picked.setText(String.valueOf(val));
                            mlineItem.setQtyPicked(Double.parseDouble(String.valueOf(val)));
                        }
                    }
                }

                break;

            case R.id.decrease:
                mQty_picked = (EditText)view.findViewById(R.id.qty_picked_value);
                isInt  = isInteger(mQty_picked.getText().toString());
                if(isInt)
                {
                    int val;
                    val = Integer.parseInt(mQty_picked.getText().toString());
                    val = val - 1;
                    if(val >= 0 )
                    {
                        mQty_picked.setText(String.valueOf(val));
                        mlineItem.setQtyPicked(Double.parseDouble(String.valueOf(val)));
                    }
                }
                else
                {
                    BigDecimal val;
                    val = BigDecimal.valueOf(Double.parseDouble(mQty_picked.getText().toString()));
                    val = val.subtract(BigDecimal.valueOf(1));
                    int comparison = val.compareTo(BigDecimal.valueOf(0));
                    if( comparison == 1 || comparison == 0)
                    {
                        mQty_picked.setText(String.valueOf(val));
                        mlineItem.setQtyPicked(Double.parseDouble(String.valueOf(val)));

                    }
                }

                break;

            case R.id.item_SNO :
            case R.id.item_ViewSNO:
                if(mCallbacks !=null)
                    mCallbacks.onSerialNumberClicked(mlineItem);
                break;
            case R.id.button_confirm :

                updateItemStatus();
                GotoDetailPicklist();
                break;
            case R.id.item_upc :

                showUPCDialog();

                break;

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

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:
                updateItemStatus();

                GotoDetailPicklist();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }



    public void GotoDetailPicklist()
    {
        if(!mlineItem.getBarcode().isEmpty()) {
            if(mlineItem.isShowSerialNo() == false) {
                if (!(((Double.parseDouble(mQty_picked.getText().toString()) > 0) || (mlineItem.getSerialLotNumbers().size() > 0)) && (mUPC_Value.getText().toString().isEmpty())) && !(Double.parseDouble(mQty_picked.getText().toString()) > mlineItem.getQtyToPick())) {

                    Intent data = new Intent();
                    data.putExtra(EXTRA_LINEITEM, mlineItem);
                    getActivity().setResult(Activity.RESULT_OK, data);
                    getActivity().finish();
                }
            }
            else
            {
                if (!(((Double.parseDouble(mQty_picked.getText().toString()) > 0) || (mlineItem.getSerialLotNumbers().size() > 0)) && (mUPC_Value.getText().toString().isEmpty())) && !(Double.parseDouble(mQty_picked.getText().toString()) > mlineItem.getQtyToPick()) && !(Double.parseDouble(mQty_picked.getText().toString()) != mlineItem.getSerialLotNumbers().size()) ) {

                    Intent data = new Intent();
                    data.putExtra(EXTRA_LINEITEM, mlineItem);
                    getActivity().setResult(Activity.RESULT_OK, data);
                    getActivity().finish();
                }
                else if((Double.parseDouble(mQty_picked.getText().toString()) != mlineItem.getSerialLotNumbers().size()))
                {

                    QntyMismatchDialog();
                }
                else
                {

                }
            }
        }
        else if(mlineItem.getBarcode().isEmpty())
        {
            if(mlineItem.isShowSerialNo() == false) {
                if (!(Double.parseDouble(mQty_picked.getText().toString()) > mlineItem.getQtyToPick())) {

                    Intent data = new Intent();
                    data.putExtra(EXTRA_LINEITEM, mlineItem);
                    getActivity().setResult(Activity.RESULT_OK, data);
                    getActivity().finish();
                }
            }
            else
            {
                if (!(Double.parseDouble(mQty_picked.getText().toString()) > mlineItem.getQtyToPick()) && !(Double.parseDouble(mQty_picked.getText().toString()) != mlineItem.getSerialLotNumbers().size()) ) {

                    Intent data = new Intent();
                    data.putExtra(EXTRA_LINEITEM, mlineItem);
                    getActivity().setResult(Activity.RESULT_OK, data);
                    getActivity().finish();
                }
                else if((Double.parseDouble(mQty_picked.getText().toString()) != mlineItem.getSerialLotNumbers().size()))
                {
                 
                    QntyMismatchDialog();
                }
                else
                {

                }
            }
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_LINEITEM, mlineItem);
        super.onSaveInstanceState(outState);
    }


    public boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {}
        return false;
    }

public boolean noDecimal(double val)
{
    if(val%1 == 0)
        return true;
    else
        return false;
}
  public  void onBackPressed()
    {

        updateItemStatus();

        GotoDetailPicklist();
    }
//scanner Integration
    @Override
    public void scanDataReceived(String sData) {
        final String sUPCData = sData;
        if(mlineItem.getBarcode().compareTo(sData) == 0)
        {//We have to run this  on ui thread - ASync task also can be used
             new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mUPC_Value.setText(sUPCData);
                    mlineItem.setBarcodeEntered(sUPCData);
                    onClick(mIncrement);

                }
            });
        }
        else
        {
            //Scanned item is not matching current item
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mlineItem.setBarcodeEntered(sUPCData);
                    onClick(mConfirm);
                }
            });
        }

    }
//chandan - status related code
    public void updateItemStatus()
    {
        Double qtyPicked = mlineItem.getQtyPicked();
        Double qtyToPick = mlineItem.getQtyToPick();
        Status status  = getItemStatus(qtyPicked,qtyToPick);
        mlineItem.setmItemStatus(status);

    }

    public Status getItemStatus(Double qtyPicked, Double qtyToPick)
    {
        Status status = Status.Open;
        if(Double.compare(qtyPicked,qtyToPick) == 0)
        {
            status = Status.Picked;
        }

        else if(Double.compare(qtyPicked,0) > 0 && Double.compare(qtyPicked,qtyToPick) < 0)
        {
            status = Status.PartiallyPicked;
        }
        else if(Double.compare(qtyPicked,0) == 0)
        {
            status = Status.NotPicked;
        }
        else
        {
            //check
        }

        return status;
    }

    private void showUPCDialog() {
        FragmentManager fm = getFragmentManager();
        UPCFragment UPCFragment = new UPCFragment().newInstance(mlineItem);
        UPCFragment.setTargetFragment(ProductInfoFragment.this,300);
        UPCFragment.show(fm, "fragment_edit_name");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 300:
                if (resultCode == Activity.RESULT_OK) {
                    mlineItem = data.getParcelableExtra("lineitem");
                    if(!mlineItem.getBarcodeEntered().isEmpty())
                        mUPC_Value.setText(mlineItem.getBarcodeEntered());
                         UPC_ErrorCheck();
                }
                break;
        }
    }

    public void UPC_ErrorCheck()
    {
        if(!mlineItem.getBarcode().isEmpty()) {

            if (!mUPC_Value.getText().toString().isEmpty()) {
                mUPC_background.setVisibility(view.GONE);
                mUPC_ErrorText.setVisibility(view.GONE);

            } else if (mUPC_Value.getText().toString().isEmpty() && ((Double.parseDouble(mQty_picked.getText().toString()) > 0) || (mlineItem.getSerialLotNumbers().size() > 0))) {
                mUPC_background.setVisibility(view.VISIBLE);
                mUPC_ErrorText.setVisibility(view.VISIBLE);

            }
        }
        mlineItem.setBarcodeEntered(mUPC_Value.getText().toString());
    }

    public void QntyMismatchDialog()
    {
        final Dialog openDialog = new Dialog(getContext());
        openDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.quantitysno_mismatch_dialog);
        Button dialogCloseButton = (Button) openDialog.findViewById(R.id.MismatchbtnOk);
        dialogCloseButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {


                openDialog.dismiss();

            }

        });

        openDialog.show();
    }
}

