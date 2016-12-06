package com.example.iceman.project.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.iceman.project.R;
import com.example.iceman.project.activity.MainActivity;
import com.example.iceman.project.database.SQLiteDatabase;
import com.example.iceman.project.model.ItemCurrentBalance;

import java.util.ArrayList;

/**
 * Created by iceman on 01/11/2016.
 */

public class RvManagementAdapter extends RecyclerView.Adapter<RvManagementAdapter.ViewHolder> {
    Context mContext;
    ArrayList<ItemCurrentBalance> mData;
    LayoutInflater mLayoutInflater;
    SQLiteDatabase mDatabase;

    public RvManagementAdapter(Context mContext, ArrayList<ItemCurrentBalance> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDatabase = SQLiteDatabase.getInstance(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_manage, null);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemCurrentBalance item = mData.get(position);
        holder.tvMoney.setText(item.getMoney() + "");
        holder.tvName.setText(item.getName());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog(item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvMoney;
        Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_type);
            tvMoney = (TextView) itemView.findViewById(R.id.tv_money_manage);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
        }
    }

    public void confirmDialog(ItemCurrentBalance item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Cảnh báo");
        builder.setMessage("Bạn có chắc chắn muốn xóa " + item.getName() + " ?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.deleteRecord(SQLiteDatabase.TBL_CURRENT_BALANCE,
                        SQLiteDatabase.TBL_CB_COLUMN_ID, new String[]{item.getId() + ""});
                Intent intent = new Intent(MainActivity.ACTION_ADD_CB_SUCCESS);
                mContext.sendBroadcast(intent);
                mData.remove(item);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}
