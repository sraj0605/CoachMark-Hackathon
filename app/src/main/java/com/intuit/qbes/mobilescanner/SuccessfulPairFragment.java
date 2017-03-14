package com.intuit.qbes.mobilescanner;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ashah9 on 2/28/17.
 */

public class SuccessfulPairFragment extends Fragment implements View.OnClickListener{

    private Button mNext;
    private TextView mCompanyName;
    private PairCompleteCallback mCallback;
    private DatabaseHandler db;

    public SuccessfulPairFragment()
    {

    }

    public static SuccessfulPairFragment newInstance()
    {
        SuccessfulPairFragment fragment = new SuccessfulPairFragment();
        return fragment;
    }

    public interface PairCompleteCallback{
        void onPairComplete();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = new DatabaseHandler(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_success_pair, container, false);
        mNext = (Button)view.findViewById(R.id.success_next);
        mCompanyName = (TextView)view.findViewById(R.id.success_text3);
        mNext.setOnClickListener(this);
        mCompanyName.setText("'" +db.getDetails().getCompanyName() + "'");
        return view;


    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.success_next:
            {
              if(mCallback!=null)
              {
                  mCallback.onPairComplete();
              }
              break;
            }
            default: break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (PairCompleteCallback) context;
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

}
