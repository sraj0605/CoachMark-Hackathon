package com.intuit.qbes.mobilescanner;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashah9 on 1/18/17.
 */

public class SerialNumberFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
   // private SerialNumberAdapter mySerialNumberAdapter;
    private EditText mSerialNumberAdded;
    private ImageView mAddSerialNo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    public SerialNumberFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_serialnumber, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.serialnumber_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);


        mAddSerialNo = (ImageView) view.findViewById(R.id.add_serialno);
        mAddSerialNo.setOnClickListener(this);
        mSerialNumberAdded = (EditText)view.findViewById(R.id.AddSno);

        getActivity().setTitle("Add Serial Numbers");


        return view;


    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {

            case R.id.add_serialno:

                String SerialNumber = mSerialNumberAdded.getText().toString();

                break;
                }


        }
        }







