package com.intuit.qbes.mobilescanner;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intuit.qbes.mobilescanner.model.Picklist;

import java.util.List;


/**
 * Created by ashah9 on 1/15/17.
 */


public class PickingReceivingAdapter extends RecyclerView.Adapter<PickingReceivingAdapter.PickingReceivingViewHolder> {

    private List<Picklist> pickingdata;
    private AdapterCallback mAdapterCallback;
    private String TAG = "PickingReceivingAdapter";

    public PickingReceivingAdapter(List<Picklist> pickingdata, AdapterCallback callback) {
        this.pickingdata = pickingdata;
        this.mAdapterCallback = callback;
    }

    public interface AdapterCallback {
        void onClickCallback(Picklist picklist);
    }


    public void updatePicklist(List<Picklist> pickingdata)
    {
        this.pickingdata = pickingdata;
        notifyDataSetChanged();
    }

    @Override
    public PickingReceivingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.userdashboard_layout, viewGroup, false);

        return new PickingReceivingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PickingReceivingViewHolder pickingViewHolder, int i) {
        Picklist pi = pickingdata.get(i);
        pickingViewHolder.mPicklistName.setText(pi.getName());
        pickingViewHolder.mPicklistItems.setText(String.valueOf(pi.getTotalitems()) + " " + "item(s)");
        pickingViewHolder.mPicklistNote.setText(pi.getNotes());

        pickingViewHolder.bindPickList(pi);
    }



    @Override
    public int getItemCount() {
        return pickingdata.size();
    }

    public class PickingReceivingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected TextView mPicklistName;
        protected TextView mPicklistItems;
        protected TextView mPicklistNote;
        protected Picklist mPick;



        public PickingReceivingViewHolder(View v) {
            super(v);
            mPicklistName  =  (TextView) v.findViewById(R.id.picklist_name);
            mPicklistItems = (TextView)  v.findViewById(R.id.picklist_total_items);
            mPicklistNote = (TextView)  v.findViewById(R.id.picklist_note);

            v.setOnClickListener(this);
        }

        public void bindPickList(Picklist pList)
        {
            mPick = pList;
        }

        @Override
        public void onClick(View view) {
            mAdapterCallback.onClickCallback(mPick);

        }
    }

}

