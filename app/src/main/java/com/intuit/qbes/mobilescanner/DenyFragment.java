package com.intuit.qbes.mobilescanner;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by ashah9 on 2/28/17.
 */

public class DenyFragment extends Fragment implements View.OnClickListener{

    private Button mTryAgain;
    private DenyTryAgainCallback mCallback;
    public DenyFragment()
    {

    }

    public static DenyFragment newInstance()
    {
        DenyFragment fragment = new DenyFragment();
        return fragment;
    }

    public interface DenyTryAgainCallback{
        void onDenyTryAgain();
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
        View view = inflater.inflate(R.layout.fragment_deny, container, false);
        mTryAgain = (Button)view.findViewById(R.id.deny_tryagain);
        mTryAgain.setOnClickListener(this);
        return view;


    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.deny_tryagain:
            {
                if(mCallback!=null)
                {
                    mCallback.onDenyTryAgain();
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
            mCallback = (DenyTryAgainCallback) context;
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
