package com.intuit.qbes.mobilescanner;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.intuit.qbes.mobilescanner.model.LineItem;

/**
 * Created by ashah9 on 2/19/17.
 */

public class GetStartedFragment extends Fragment implements View.OnClickListener{

    private Button mGetStarted;
    private GetStartedCallback mCallback = null;

    public  GetStartedFragment()
    {

    }

    public static GetStartedFragment newInstance()
    {
        GetStartedFragment fragment = new GetStartedFragment();
        return fragment;
    }

    public interface GetStartedCallback
    {
        void onGetStartedClick();
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

        View view = inflater.inflate(R.layout.fragment_get_started, container, false);

        mGetStarted = (Button)view.findViewById(R.id.GetStartedButton);
        mGetStarted.setOnClickListener(this);

        return view;

    }

    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.GetStartedButton :
            {
                if(mCallback !=null) {
                    mCallback.onGetStartedClick();
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (GetStartedFragment.GetStartedCallback) context;
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
