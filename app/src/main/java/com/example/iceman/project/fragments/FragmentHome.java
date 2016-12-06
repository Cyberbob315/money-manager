package com.example.iceman.project.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.iceman.project.R;
import com.example.iceman.project.adapter.RvCurrentBalanceAdapter;
import com.example.iceman.project.database.SQLiteDatabase;
import com.example.iceman.project.interfaces.ShowFragmentListener;
import com.example.iceman.project.model.ItemCurrentBalance;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment implements View.OnClickListener {

    public static final String TAG = FragmentHome.class.getName();
    public static final String ACTION_ADD_CB_SUCCESS = "com.example.iceman.project.ACTION_ADD_CB_SUCCESS";

    private ShowFragmentListener mCallback;

    Button btnAdd;
    Button btnStatistic;
    Button btnManage;
    Button btnInfo;
    TextView tvNoti;
    //    LinearLayout llList;
    RecyclerView rvCurrentBalance;
    RvCurrentBalanceAdapter mCurrentBlAdapter;
    ArrayList<ItemCurrentBalance> lstItemCurrentBalance;
    SQLiteDatabase mDatabase;
    View view;


    public FragmentHome() {
        // Required empty public constructor
    }


    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (ShowFragmentListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        registerBroadcastAddNewCB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_main, container, false);
        initControl();
        initEvents();
//        addDynamicLayout();
        showDataCurrentBalance();
        if(lstItemCurrentBalance.isEmpty()){
            tvNoti.setText(getString(R.string.noti));
            tvNoti.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void showDataCurrentBalance() {
        lstItemCurrentBalance = new ArrayList<>();

        String sql = "select * from " + SQLiteDatabase.TBL_CURRENT_BALANCE;
        Cursor result = mDatabase.rawQuery(sql);
        if (result != null && result.moveToFirst()) {
            do {
                int id = result.getInt(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_ID));
                String name = result.getString(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_NAME));
                Double money = result.getDouble(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_MONEY));
                ItemCurrentBalance item = new ItemCurrentBalance(id, name, money);
                lstItemCurrentBalance.add(item);
            } while (result.moveToNext());
        }


        mCurrentBlAdapter = new RvCurrentBalanceAdapter(getActivity(), lstItemCurrentBalance);
        rvCurrentBalance.setAdapter(mCurrentBlAdapter);
    }

    private void initEvents() {
        btnAdd.setOnClickListener(this);
        btnStatistic.setOnClickListener(this);
        btnManage.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
    }

//    private void addDynamicLayout() {
//        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
//
//
//        for (int i = 0; i < 4; i++) {
//            View myView = layoutInflater.inflate(R.layout.item_current_balance, null);
//            TextView tvName = (TextView) myView.findViewById(R.id.tv_name);
//            TextView tvBalance = (TextView) myView.findViewById(R.id.tv_so_tien);
//
//            tvName.setText("Tên " + (i + 1));
//            tvBalance.setText("Số tiền " + (i + 1) * 100000);
//
//            llList.addView(myView);
//        }
//    }

    private void initControl() {
        tvNoti = (TextView) view.findViewById(R.id.tv_noti);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnStatistic = (Button) view.findViewById(R.id.btnStatistic);
        btnManage = (Button) view.findViewById(R.id.btnManage);
        btnInfo = (Button) view.findViewById(R.id.btnInfo);
//        llList = (LinearLayout) findViewById(R.id.ll_ds);
        rvCurrentBalance = (RecyclerView) view.findViewById(R.id.rv_currentbalance);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvCurrentBalance.setLayoutManager(layoutManager);

        mDatabase = SQLiteDatabase.getInstance(getContext());
    }

    BroadcastReceiver broadcastReceiverAddNewCB = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showDataCurrentBalance();
            if(lstItemCurrentBalance.isEmpty()){
                tvNoti.setText("Chưa có tài khoản nào,hãy nhấn vào quản lý tài khoản để thêm mới");
                tvNoti.setVisibility(View.VISIBLE);
            }else{
                tvNoti.setVisibility(View.GONE);
            }

        }
    };

    private void registerBroadcastAddNewCB() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ADD_CB_SUCCESS);
        getContext().registerReceiver(broadcastReceiverAddNewCB, filter);
    }

    private void unregisterBroadCastAddNewCB() {
        getContext().unregisterReceiver(broadcastReceiverAddNewCB);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                mCallback.onShowAddTransactionFragment();
                break;
            case R.id.btnStatistic:
                mCallback.onListTransactionFragment();
                break;
            case R.id.btnManage:

                mCallback.onShowManagementFragment();
                break;
            case R.id.btnInfo:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadCastAddNewCB();
    }
}
