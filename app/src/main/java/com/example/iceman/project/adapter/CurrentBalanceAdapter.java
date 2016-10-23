package com.example.iceman.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.iceman.project.R;
import com.example.iceman.project.model.ItemCurrentBalance;

import java.util.ArrayList;

/**
 * Created by iceman on 18/10/2016.
 */

public class CurrentBalanceAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<ItemCurrentBalance> mData;
    LayoutInflater mLayoutInflater;

    public CurrentBalanceAdapter(Context mContext, ArrayList<ItemCurrentBalance> mData) {
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
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_current_balance,null);
            viewHolder.tvMoney = (TextView) convertView.findViewById(R.id.tv_money_balance);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name_balance);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ItemCurrentBalance item = mData.get(position);
        viewHolder.tvName.setText(item.getName());
        viewHolder.tvMoney.setText(item.getMoney()+"");

        return convertView;
    }

    private static class ViewHolder {
        TextView tvName;
        TextView tvMoney;
    }
}
