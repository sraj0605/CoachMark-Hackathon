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

public class TimeoutFragment extends Fragment implements View.OnClickListener{

    private Button mTryAgain;
    private TryAgainCallback mCallback;
    public TimeoutFragment()
    {

    }

    public static TimeoutFragment newInstance()
    {
        TimeoutFragment fragment = new TimeoutFragment();
        return fragment;
    }

    public interface TryAgainCallback{
        void onTryAgain();
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
        View view = inflater.inflate(R.layout.fragment_timeout, container, false);
        mTryAgain = (Button)view.findViewById(R.id.timeout_tryagain);
        mTryAgain.setOnClickListener(this);
        return view;


    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.timeout_tryagain:
            {
                if(mCallback!=null)
                {
                    mCallback.onTryAgain();
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
            mCallback = (TryAgainCallback) context;
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
