package com.example.iceman.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.iceman.project.R;
import com.example.iceman.project.model.ItemTransaction;

import java.util.ArrayList;

/**
 * Created by iceman on 21/10/2016.
 */

public class TransactionAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<ItemTransaction> mData;
    LayoutInflater mLayoutInflater;

    public TransactionAdapter(Context mContext, ArrayList<ItemTransaction> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_transaction, null);
            viewHolder = new ViewHolder();

            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_date_trans);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content_trans);
            viewHolder.tvMoney = (TextView) convertView.findViewById(R.id.tv_money_trans);
            viewHolder.tvBalance = (TextView) convertView.findViewById(R.id.tv_balance_trans);
            viewHolder.tvAccount = (TextView) convertView.findViewById(R.id.tv_account_trans);
            viewHolder.tvTransType = (TextView) convertView.findViewById(R.id.tv_trans_type);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ItemTransaction item = mData.get(position);

        viewHolder.tvDate.setText(item.getDate());
        viewHolder.tvContent.setText(item.getContent());
        viewHolder.tvMoney.setText(item.getMoney() + "");
        viewHolder.tvBalance.setText(item.getAccBalance() + "");
        viewHolder.tvAccount.setText(item.getAccount());
        int type = item.getTransType();
        if(type == 0){
            viewHolder.tvTransType.setText(ItemTransaction.TRANS_TYPE_SPEND);
        }else{
            viewHolder.tvTransType.setText(ItemTransaction.TRANS_TYPE_RECEIVE);
        }



        return convertView;
    }

    private static class ViewHolder {
        TextView tvDate;
        TextView tvContent;
        TextView tvMoney;
        TextView tvBalance;
        TextView tvAccount;
        TextView tvTransType;
    }
}
