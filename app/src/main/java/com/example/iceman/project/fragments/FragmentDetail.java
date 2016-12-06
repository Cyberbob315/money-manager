package com.example.iceman.project.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iceman.project.R;
import com.example.iceman.project.activity.ListTransactionsActivity;
import com.example.iceman.project.database.SQLiteDatabase;
import com.example.iceman.project.model.ItemCurrentBalance;
import com.example.iceman.project.utils.Common;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDetail extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = FragmentDetail.class.getName();
    public static final String KEY_ID_TRANS = "key_id_trans";


    View view;

    TextView tvDate, tvContent, tvMoney, tvBalance;
    SQLiteDatabase mDatabase;


    public FragmentDetail() {
        // Required empty public constructor
    }


    public static FragmentDetail newInstance(int itemTrans) {
        FragmentDetail fragment = new FragmentDetail();
        Bundle args = new Bundle();
        args.putInt(KEY_ID_TRANS, itemTrans);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_transaction_detail, container, false);
        initControls();
        if (getArguments() != null) {
            int idTrans = getArguments().getInt(KEY_ID_TRANS);
            Log.d(TAG,idTrans+"");
            showTransDetail(idTrans);
        }
        getAndShowTransData();
        return view;
    }

    private void getAndShowTransData() {
//        Intent intent = getActivity().getIntent();
//        if (intent != null) {
//            int transId = intent.getExtras().getInt(ListTransactionsActivity.KEY_TRANS);
//            showTransDetail(transId);
//        }
    }

    private ItemCurrentBalance getItemCB(int id) {
        ItemCurrentBalance item = null;
        String sql = "select * from " + SQLiteDatabase.TBL_CURRENT_BALANCE
                + " where " + SQLiteDatabase.TBL_CB_COLUMN_ID + " = " + id;
        Cursor result = mDatabase.rawQuery(sql);
        if (result != null && result.moveToFirst()) {
            int idCB = result.getInt(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_ID));
            String nameCB = result.getString(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_NAME));
            Double moneyCB = result.getDouble(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_MONEY));
            item = new ItemCurrentBalance(idCB, nameCB, moneyCB);
        }


        return item;
    }

    private void showTransDetail(int transId) {
        String sql = "select * from " + SQLiteDatabase.TBL_TRANSACTION +
                " where " + SQLiteDatabase.TBL_TRANS_COLUMN_ID + " = " + transId;
        Cursor result = mDatabase.rawQuery(sql);
        if (result != null && result.moveToFirst()) {
            String content = result.getString(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_CONTENT));
            String date = result.getString(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_DATE));
            date = Common.getInstance().formatDate(date, Common.DATE_SAVE_TO_DB, Common.DATE_SHOW);
            Double money = result.getDouble(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_MONEY));
            int idCB = result.getInt(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_ID_TBL_CB));
            ItemCurrentBalance itemCB = getItemCB(idCB);
            Double balance = itemCB.getMoney();

            tvContent.setText(content);
            tvDate.setText(date);
            tvMoney.setText("" + money);
            tvBalance.setText("" + balance);
        }

    }

    private void initControls() {
        tvDate = (TextView) view.findViewById(R.id.tv_date_detail);
        tvContent = (TextView) view.findViewById(R.id.tv_content_detail);
        tvMoney = (TextView) view.findViewById(R.id.tv_money_detail);
        tvBalance = (TextView) view.findViewById(R.id.tv_balance_detail);
        mDatabase = SQLiteDatabase.getInstance(getActivity());
    }


}
