package com.example.iceman.project.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iceman.project.R;
import com.example.iceman.project.model.ItemCurrentBalance;

import java.util.ArrayList;

/**
 * Created by iceman on 01/11/2016.
 */

public class RvCurrentBalanceAdapter extends RecyclerView.Adapter<RvCurrentBalanceAdapter.ViewHolder> {
    Context mContext;
    ArrayList<ItemCurrentBalance> mData;
    LayoutInflater mLayoutInflater;

    public RvCurrentBalanceAdapter(Context mContext, ArrayList<ItemCurrentBalance> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_current_balance, null);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvMoney.setText(mData.get(position).getMoney() + "");
        holder.tvName.setText(mData.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvMoney;


        public ViewHolder(View itemView) {
            super(itemView);
            tvMoney = (TextView) itemView.findViewById(R.id.tv_money_balance);
            tvName = (TextView) itemView.findViewById(R.id.tv_name_balance);
        }
    }
}
