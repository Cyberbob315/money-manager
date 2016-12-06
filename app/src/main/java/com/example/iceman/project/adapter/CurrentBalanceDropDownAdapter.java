package com.example.iceman.project.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iceman.project.R;
import com.example.iceman.project.interfaces.OnViewHolderClicked;
import com.example.iceman.project.model.ItemCurrentBalance;

import java.util.ArrayList;

/**
 * Created by iceman on 01/11/2016.
 */

public class CurrentBalanceDropDownAdapter extends RecyclerView.Adapter<CurrentBalanceDropDownAdapter.ViewHolder> {

    Context mContext;
    ArrayList<ItemCurrentBalance> mData;
    LayoutInflater mLayoutInflater;
    OnViewHolderClicked clicked;

    public CurrentBalanceDropDownAdapter(Context mContext, ArrayList<ItemCurrentBalance> mData, OnViewHolderClicked onClicked) {
        this.mContext = mContext;
        this.mData = mData;
        mLayoutInflater = LayoutInflater.from(mContext);
        clicked = onClicked;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_cb_drop_down, null);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvItemCB.setText(mData.get(position).getName());
        holder.id = mData.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvItemCB;
         int id;

        public ViewHolder(View itemView) {
            super(itemView);
            tvItemCB = (TextView) itemView.findViewById(R.id.tv_cb_drop_down);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clicked.sendData(tvItemCB.getText().toString(), id);
        }
    }

}
