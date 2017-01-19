package com.intuit.qbes.mobilescanner;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.Picklist;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ashah9 on 1/8/17.
 */

public class ProductInfoFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = "ProductInfoFragment";
    private View view;
    private EditText qty_picked;
    private TextView qty_to_pick;
    private double val;
    private ImageView increment;
    private ImageView decrement;
    private TextView  serialNo;
    private TextView  UPC_Note;
    private View  UPC_background;
    private EditText UPC_Value;
    private Button confirm;
    private boolean isInt;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onSerialNumberClicked();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    public ProductInfoFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_info, container, false);

        increment = (ImageView)view.findViewById(R.id.increase);

        decrement = (ImageView)view.findViewById(R.id.decrease);

        UPC_Value =   (EditText)view.findViewById(R.id.item_upc) ;

        confirm = (Button)view.findViewById(R.id.button_confirm);

        serialNo = (TextView) view.findViewById(R.id.item_SNO);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        qty_picked = (EditText)view.findViewById(R.id.qty_picked_value);

        increment.setOnClickListener(this);
        decrement.setOnClickListener(this);
        serialNo.setOnClickListener(this);

        getActivity().setTitle("LTree Tempered Glass");
        qty_picked.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(11,4)});

        qty_picked.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {


                if(s.length() == 0)
                {
                    qty_picked.setText("0");
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {




                }

        });


        return view;


    }

    @Override

    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.increase:
                qty_picked = (EditText)view.findViewById(R.id.qty_picked_value);
                qty_to_pick = (TextView)view.findViewById(R.id.item_qty);
                isInt  = isInteger(qty_picked.getText().toString());
                if(isInt)
                {
                    int val;
                    val = Integer.parseInt(qty_picked.getText().toString());
                    val = val + 1;
                    BigDecimal tempval = BigDecimal.valueOf(val);
                    if(isInteger(qty_to_pick.getText().toString()))
                    {
                        if(val <= Integer.parseInt(qty_to_pick.getText().toString()))
                            qty_picked.setText(String.valueOf(val));

                    }
                    else {
                        int comparison = tempval.compareTo(BigDecimal.valueOf(Double.parseDouble(qty_to_pick.getText().toString())));
                        if (comparison == -1 || comparison == 0) {
                            qty_picked.setText(String.valueOf(val));
                        }
                    }
                }
                else
                {
                    BigDecimal val;
                    val = BigDecimal.valueOf(Double.parseDouble(qty_picked.getText().toString()));
                    val = val.add(BigDecimal.valueOf(1));

                    int comparison;
                    if(isInteger(qty_to_pick.getText().toString()))
                    {
                        comparison = val.compareTo(BigDecimal.valueOf(Integer.parseInt(qty_to_pick.getText().toString())));
                        if( comparison == -1 || comparison == 0)
                            qty_picked.setText(String.valueOf(val));

                    }
                    else {
                        comparison = val.compareTo(BigDecimal.valueOf(Double.parseDouble(qty_to_pick.getText().toString())));
                        if (comparison == -1 || comparison == 0) {
                            qty_picked.setText(String.valueOf(val));
                        }
                    }
                }

                break;

            case R.id.decrease:
                qty_picked = (EditText)view.findViewById(R.id.qty_picked_value);
                isInt  = isInteger(qty_picked.getText().toString());
                if(isInt)
                {
                    int val;
                    val = Integer.parseInt(qty_picked.getText().toString());
                    val = val - 1;
                    if(val >= 0 )
                    {
                        qty_picked.setText(String.valueOf(val));
                    }
                }
                else
                {
                    BigDecimal val;
                    val = BigDecimal.valueOf(Double.parseDouble(qty_picked.getText().toString()));
                    val = val.subtract(BigDecimal.valueOf(1));
                    int comparison = val.compareTo(BigDecimal.valueOf(0));
                    if( comparison == 1 || comparison == 0)
                    {
                        qty_picked.setText(String.valueOf(val));
                    }
                }

                break;

            case R.id.item_SNO :
                    mCallbacks.onSerialNumberClicked();

                break;

        }

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




    public boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {}
        return false;
    }





    }

