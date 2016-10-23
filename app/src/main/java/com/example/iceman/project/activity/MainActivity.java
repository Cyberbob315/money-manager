package com.example.iceman.project.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.iceman.project.R;
import com.example.iceman.project.adapter.CurrentBalanceAdapter;
import com.example.iceman.project.database.SQLiteDatabase;
import com.example.iceman.project.model.ItemCurrentBalance;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String ACTION_ADD_CB_SUCCESS = "com.example.iceman.project.ACTION_ADD_CB_SUCCESS";

    Button btnAdd;
    Button btnStatistic;
    Button btnManage;
    Button btnInfo;
    //    LinearLayout llList;
    ListView lvCurrentBalance;
    CurrentBalanceAdapter mCurrentBlAdapter;
    ArrayList<ItemCurrentBalance> lstItemCurrentBalance;
    SQLiteDatabase mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControl();
        initEvents();
//        addDynamicLayout();
        showDataCurrentBalance();
        registerBroadcastAddNewCB();

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
                ItemCurrentBalance item = new ItemCurrentBalance(id,name,money);
                lstItemCurrentBalance.add(item);
            } while (result.moveToNext());
        }


        mCurrentBlAdapter = new CurrentBalanceAdapter(MainActivity.this, lstItemCurrentBalance);
        lvCurrentBalance.setAdapter(mCurrentBlAdapter);
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
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnStatistic = (Button) findViewById(R.id.btnStatistic);
        btnManage = (Button) findViewById(R.id.btnManage);
        btnInfo = (Button) findViewById(R.id.btnInfo);
//        llList = (LinearLayout) findViewById(R.id.ll_ds);
        lvCurrentBalance = (ListView) findViewById(R.id.lv_current_balance);
        mDatabase = SQLiteDatabase.getInstance(MainActivity.this);
    }

    BroadcastReceiver broadcastReceiverAddNewCB = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showDataCurrentBalance();
        }
    };

    private void registerBroadcastAddNewCB() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ADD_CB_SUCCESS);
        registerReceiver(broadcastReceiverAddNewCB, filter);
    }

    private void unregisterBroadCastAddNewCB() {
        unregisterReceiver(broadcastReceiverAddNewCB);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
                break;
            case R.id.btnStatistic:
                Intent intent2 = new Intent(MainActivity.this, ListTransactionsActivity.class);
                startActivity(intent2);
                break;
            case R.id.btnManage:
                Intent intent1 = new Intent(MainActivity.this, ManagementActivity.class);
                startActivity(intent1);
                break;
            case R.id.btnInfo:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadCastAddNewCB();
    }
}
