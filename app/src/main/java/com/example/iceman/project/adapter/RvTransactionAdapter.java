package com.example.iceman.project.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iceman.project.R;
import com.example.iceman.project.activity.HostActivity;
import com.example.iceman.project.model.ItemTransaction;

import java.util.ArrayList;

/**
 * Created by iceman on 01/11/2016.
 */

public class RvTransactionAdapter extends RecyclerView.Adapter<RvTransactionAdapter.ViewHolder> {
    Context mContext;
    ArrayList<ItemTransaction> mData;
    LayoutInflater mLayoutInflater;

    public RvTransactionAdapter(Context mContext, ArrayList<ItemTransaction> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_transaction, null);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemTransaction item = mData.get(position);
        holder.tvDate.setText(item.getDate());
        holder.tvContent.setText(item.getContent());
        holder.tvMoney.setText(item.getMoney() + "");
        holder.tvBalance.setText(item.getAccBalance() + "");
        holder.tvAccount.setText(item.getAccount());
        holder.id = item.getId();

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDate;
        TextView tvContent;
        TextView tvMoney;
        TextView tvBalance;
        TextView tvAccount;
        TextView tvTransType;
        int id;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date_trans);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content_trans);
            tvMoney = (TextView) itemView.findViewById(R.id.tv_money_trans);
            tvBalance = (TextView) itemView.findViewById(R.id.tv_balance_trans);
            tvAccount = (TextView) itemView.findViewById(R.id.tv_account_trans);
            tvTransType = (TextView) itemView.findViewById(R.id.tv_trans_type);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ((HostActivity)mContext).onShowDetailFragment(id);
        }
    }
}
