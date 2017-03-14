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
import com.intuit.qbes.mobilescanner.model.SerialLotNumber;
import com.intuit.qbes.mobilescanner.model.Status;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ashah9 on 1/8/17.
 */

public class ProductInfoFragment extends Fragment implements View.OnClickListener, TextWatcher,BarcodeScannerDevice.ScanDataReceiver {

    public static final String EXTRA_LINEITEM = "com.intuit.qbes.mobilescanner.lineitem";
    public static final String BARCODE_ENTERED = "BARCODE_ENTERED";
    public static final String IS_SCANNED = "SCANNED_DATA";
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
    private static DeviceManager mDeviceManager = null;
    private boolean mQuantityMismatchDialogThrown = false;
    private String mBarCodeScanned = "";
    private boolean isSerialNumberAssociated = false;

    public interface Callbacks {
        void onSerialNumberClicked(LineItem lineitem);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mlineItem = (LineItem) getArguments().getParcelable(EXTRA_LINEITEM);
        if (mlineItem == null && savedInstanceState != null)
        {
            mlineItem = (LineItem) savedInstanceState.getParcelable(EXTRA_LINEITEM);
        }
        updateSerialLotnumberForItem();

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
        mUPC_background = (View)view.findViewById(R.id.UPC_Error);
        mItemName = (TextView)view.findViewById(R.id.item_Name);
        mItemDescription = (TextView)view.findViewById(R.id.item_description);
        mLocation = (TextView)view.findViewById(R.id.item_location);
        mSalesOrder = (TextView)view.findViewById(R.id.item_salesorder);
        mQtyToPick = (TextView)view.findViewById(R.id.item_qty);

        return view;


    }
    @Override
    public void onStart() {

        super.onStart();

        setUpUI();

        fillUIControls();

        setListeners();

        init_barcode();
    }

    public void updateSerialLotnumberForItem()
    {
        if(mlineItem.isShowSerialNo() == true && mlineItem.getSerialLotNumbers() != null && mlineItem.getSerialLotNumbers().size()==0) {
            DatabaseHandler db = new DatabaseHandler(getContext());
            List<SerialLotNumber> serialLotNumbers = db.allSerialLotNumbers(mlineItem.getId());
            mlineItem.setSerialLotNumbers(serialLotNumbers);
        }
    }

    public void setUpUI()
    {
        if(mlineItem.getSerialLotNumbers()!= null && mlineItem.getSerialLotNumbers().size() > 0)
            isSerialNumberAssociated = true;
        else
            isSerialNumberAssociated = false;

        upcDialogSetup();

        setupSerialNumberView();

        mQty_picked.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(11, 4)});

        if(mlineItem.isShowSerialNo() == false && (mlineItem.getBarcode().isEmpty())) {
            mHDivider.setVisibility(view.GONE);
        }
    }
    public void fillUIControls()
    {

        mItemName.setText(mlineItem.getItemName().toString());
        mItemDescription.setText(mlineItem.getItemDesc().toString());
        mLocation.setText(mlineItem.getBinLocation().toString());

        if(mlineItem.getBarcodeEntered().compareTo("") !=0)
            mUPC_Value.setText(mlineItem.getBarcodeEntered());

        fillQunatity();

        getActivity().setTitle(mItemName.getText().toString());
    }

    public void setListeners()
    {
        mIncrement.setOnClickListener(this);
        mDecrement.setOnClickListener(this);
        mSerialNo.setOnClickListener(this);
        mSerialView.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mQty_picked.addTextChangedListener(this);
        mUPC_Value.setOnClickListener(this);
    }
    public void upcDialogSetup()
    {
        if(mlineItem.getBarcode() != null && mlineItem.getBarcode().isEmpty()) {
            mUPC_Header.setVisibility(view.GONE);
            mUPC_Value.setVisibility(view.GONE);
        }
        else
        {
            if (isSerialNumberAssociated && (mlineItem.getBarcodeEntered().compareTo("") == 0))
                showUPCDialog();

        }
    }

    public void setupSerialNumberView()
    {
        if(mlineItem.isShowSerialNo() == false) {
            mSNO_Header.setVisibility(view.GONE);
            mSerialNo.setVisibility(view.GONE);
            mSerialView.setVisibility(view.GONE);
            mVDivider.setVisibility(view.GONE);

        }
        else
        {
            if(!isSerialNumberAssociated) {
                mSerialView.setVisibility(view.GONE);
            }
            else {
                mSerialNo.setVisibility(view.GONE);
                mSerialView.setPaintFlags(mSerialView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
        }
    }
    public void fillQunatity()
    {
        if(Utilities.noDecimal(mlineItem.getQtyToPick())){

            mQtyToPick.setText((String.valueOf((int)mlineItem.getQtyToPick())));
        }
        else
        {
            mQtyToPick.setText(String.valueOf(mlineItem.getQtyToPick()));
        }
        if (Utilities.noDecimal(mlineItem.getQtyPicked())) {

            mQty_picked.setText(String.valueOf((int) mlineItem.getQtyPicked()));

        }
        else {
            mQty_picked.setText(String.valueOf(mlineItem.getQtyPicked()));

        }
        validateQuantityPickedAgainstQuantityToPick(mQty_picked.getText().toString());
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
            validateQuantityPickedAgainstQuantityToPick(mQty_picked.getText().toString());

            if(mlineItem.isShowSerialNo() == true) {
            mlineItem.setQtyPicked(mlineItem.getSerialLotNumbers().size());
           }
            else {
            mlineItem.setQtyPicked(Double.parseDouble(mQty_picked.getText().toString()));
            }

            showHideBarcodeErrorHeader();
        }


    }


    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.increase:
                mQty_picked = (EditText)view.findViewById(R.id.qty_picked_value);
                mQtyToPick = (TextView)view.findViewById(R.id.item_qty);
                //String quantityPicked = Utilities.checkAndIncrementQuantity(mQtyToPick.getText().toString(),mQty_picked.getText().toString());
                String quantityPicked = Utilities.incrementQuantity(mQty_picked.getText().toString());
                validateQuantityPickedAgainstQuantityToPick(quantityPicked);
                mQty_picked.setText(String.valueOf(quantityPicked));
                mlineItem.setQtyPicked(Double.parseDouble(String.valueOf(quantityPicked)));
                break;

            case R.id.decrease:
                mQty_picked = (EditText)view.findViewById(R.id.qty_picked_value);
                String val = Utilities.decrementQuantity(mQty_picked.getText().toString());
                validateQuantityPickedAgainstQuantityToPick(val);
                mQty_picked.setText(String.valueOf(val));
                mlineItem.setQtyPicked(Double.parseDouble(String.valueOf(val)));
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

                    finishActivity();
                }
            }
            else
            {
                if (!(((Double.parseDouble(mQty_picked.getText().toString()) > 0) || (mlineItem.getSerialLotNumbers().size() > 0)) && (mUPC_Value.getText().toString().isEmpty())) && !(Double.parseDouble(mQty_picked.getText().toString()) > mlineItem.getQtyToPick()) && !(Double.parseDouble(mQty_picked.getText().toString()) != mlineItem.getSerialLotNumbers().size()) ) {

                    finishActivity();
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

                    finishActivity();
                }
            }
            else
            {
                if (!(Double.parseDouble(mQty_picked.getText().toString()) > mlineItem.getQtyToPick()) && !(Double.parseDouble(mQty_picked.getText().toString()) != mlineItem.getSerialLotNumbers().size()) ) {

                    finishActivity();
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

    public void finishActivity()
    {
        Intent data = new Intent();
        data.putExtra(EXTRA_LINEITEM, mlineItem);
        data.putExtra(BARCODE_ENTERED,mlineItem.getBarcodetoReturn());
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_LINEITEM, mlineItem);
        super.onSaveInstanceState(outState);
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
                    showHideBarcodeErrorHeader();
                    mlineItem.setBarcodetoReturn(sUPCData);
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
                    mlineItem.setBarcodetoReturn(sUPCData);
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

        UPCFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                init_barcode();
                String barcodeEntered = mlineItem.getBarcodeEntered().toString();
                if(!barcodeEntered.isEmpty()) {
                    mUPC_Value.setText(barcodeEntered);
                    mlineItem.setBarcodetoReturn(barcodeEntered);
                }
                UPC_ErrorCheck();
            }
        });
    }



    public void UPC_ErrorCheck()
    {
        if(!mlineItem.getBarcode().isEmpty()) {

            boolean isUpcTextEmpty = mUPC_Value.getText().toString().isEmpty();

            if (!isUpcTextEmpty) {
                mUPC_background.setVisibility(view.GONE);
                mUPC_ErrorText.setVisibility(view.GONE);

            } else if (isUpcTextEmpty && ((Double.parseDouble(mQty_picked.getText().toString()) > 0) || isSerialNumberAssociated)) {
                mUPC_background.setVisibility(view.VISIBLE);
                mUPC_ErrorText.setVisibility(view.VISIBLE);

            }
        }
        mlineItem.setBarcodeEntered(mUPC_Value.getText().toString());
    }

    public void QntyMismatchDialog()
    {
        if(!mQuantityMismatchDialogThrown) {
            final Dialog openDialog = new Dialog(getContext());
            openDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            openDialog.setContentView(R.layout.quantitysno_mismatch_dialog);
            Button dialogCloseButton = (Button) openDialog.findViewById(R.id.MismatchbtnOk);
            dialogCloseButton.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    mQuantityMismatchDialogThrown = false;
                    openDialog.dismiss();

                }

            });

            openDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mQuantityMismatchDialogThrown = false;
                }
            });

            mQuantityMismatchDialogThrown = true;
            openDialog.show();
        }
    }

    public void validateQuantityPickedAgainstQuantityToPick(String qtyPicked)
    {
        if (Double.parseDouble(qtyPicked) > mlineItem.getQtyToPick()) {

            showQuantityPickedWarning();


        } else {
            hideQuantityPickedWarning();

        }


    }
    public void showHideBarcodeErrorHeader()
    {
        if(!mlineItem.getBarcode().isEmpty()) {

            boolean isUPCTextEmpty = mUPC_Value.getText().toString().isEmpty();
            boolean isAnyQTYPicked = false;
            if(Double.parseDouble(mQty_picked.getText().toString()) > 0)
                isAnyQTYPicked = true;
            if ((isUPCTextEmpty && !isAnyQTYPicked && !isSerialNumberAssociated) || !isUPCTextEmpty ) {
                mUPC_background.setVisibility(view.GONE);
                mUPC_ErrorText.setVisibility(view.GONE);
            }
            else if (isUPCTextEmpty && (isAnyQTYPicked || (isSerialNumberAssociated))) {
                mUPC_background.setVisibility(view.VISIBLE);
                mUPC_ErrorText.setVisibility(view.VISIBLE);

            }
        }
    }
    public void showQuantityPickedWarning()
    {
        error.setVisibility(view.VISIBLE);
        mQty_picked_error.setVisibility(view.VISIBLE);
        mQty_picked_label.setVisibility(View.GONE);
    }

    public void hideQuantityPickedWarning()
    {
        mQty_picked_error.setVisibility(view.GONE);
        error.setVisibility(view.GONE);
        mQty_picked_label.setVisibility(View.VISIBLE);
    }

    public void init_barcode()
    {
        mDeviceManager = DeviceManager.getDevice(getContext());
        mDeviceManager.unRegisterDeviceFromCallback(this);
        mDeviceManager.registerForCallback(this);
    }
}

