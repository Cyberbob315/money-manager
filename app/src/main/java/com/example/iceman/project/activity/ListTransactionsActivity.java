package com.example.iceman.project.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.iceman.project.R;
import com.example.iceman.project.adapter.TransactionAdapter;
import com.example.iceman.project.database.SQLiteDatabase;
import com.example.iceman.project.model.ItemCurrentBalance;
import com.example.iceman.project.model.ItemTransaction;
import com.example.iceman.project.utils.Common;
import com.example.iceman.project.utils.ItemTransComparator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ListTransactionsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String ACTION_ADD_TRANS_SUCCESS = "com.example.iceman.project.ACTION_ADD_TRANS_SUCCESS";
    public static final String ACTION_ACCOUNT_CHANGE = "com.example.iceman.project.ACTION_ACCOUNT_CHANGE";

    Button btnDateStart;
    Button btnDateEnd;
    Spinner spAccount;
    Spinner spTransType;
    ListView lvTransaction;

    SQLiteDatabase mDatabase;
    ArrayList<ItemTransaction> lstTransList;
    ArrayList<ItemCurrentBalance> lstAccount;
    ArrayList<String> lstType;

    TransactionAdapter mAdapterTrans;
    ArrayAdapter<ItemCurrentBalance> adapterAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transactions);

        initControls();
        initEvents();
        showListItemTransaction();
        getSpinnerListAccount();
        initSpinnerTransType();
        registerBroadcastAddTrans();
        registerBroadcastAccChange();
    }

    private void showListItemTransaction() {
        String sql = "select * from " + SQLiteDatabase.TBL_TRANSACTION
                + " order by date(" + SQLiteDatabase.TBL_TRANS_COLUMN_DATE + ") ASC;";
        lstTransList = getListItemTransaction(sql);
        mAdapterTrans = new TransactionAdapter(ListTransactionsActivity.this, lstTransList);
        lvTransaction.setAdapter(mAdapterTrans);
    }

    private void initEvents() {
        btnDateStart.setOnClickListener(this);
        btnDateEnd.setOnClickListener(this);
        spAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedAccPos = spAccount.getSelectedItemPosition();
                ItemCurrentBalance selectedAccObj = (ItemCurrentBalance) spAccount.getSelectedItem();
                int selectedTransType = spTransType.getSelectedItemPosition();
                if (selectedAccPos == 0 && selectedTransType == 0) {
                    showListItemTransaction();
                } else {
                    if (selectedTransType == 0) {
                        getCustomListItemTrans(selectedAccObj.getId(), -1);
                    } else {
                        getCustomListItemTrans(selectedAccObj.getId(), selectedTransType - 1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spTransType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedAccPos = spAccount.getSelectedItemPosition();
                ItemCurrentBalance selectedAccObj = (ItemCurrentBalance) spAccount.getSelectedItem();
                int selectedTransType = spTransType.getSelectedItemPosition();
                if (selectedAccPos == 0 && selectedTransType == 0) {
                    showListItemTransaction();
                } else {
                    if (selectedAccPos == 0) {
                        getCustomListItemTrans(-1, selectedTransType - 1);
                    } else {
                        getCustomListItemTrans(selectedAccObj.getId(), selectedTransType - 1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSpinnerTransType() {
        lstType = new ArrayList<>();
        lstType.add("Tất cả");
        lstType.add(ItemTransaction.TRANS_TYPE_SPEND);
        lstType.add(ItemTransaction.TRANS_TYPE_RECEIVE);

        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(ListTransactionsActivity.this,
                android.R.layout.simple_spinner_item, lstType);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spTransType.setAdapter(adapterType);
    }

    private void getSpinnerListAccount() {

        lstAccount = new ArrayList<>();
        String sql = "select * from " + SQLiteDatabase.TBL_CURRENT_BALANCE;
        Cursor result = mDatabase.rawQuery(sql);
        ItemCurrentBalance itemAll = new ItemCurrentBalance(-1, "Tất cả", -1);

        lstAccount.add(itemAll);
        if (result != null && result.moveToFirst()) {
            do {
                int idCB = result.getInt(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_ID));
                String nameCB = result.getString(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_NAME));
                Double moneyCB = result.getDouble(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_MONEY));
                ItemCurrentBalance item = new ItemCurrentBalance(idCB, nameCB, moneyCB);

                lstAccount.add(item);

            } while (result.moveToNext());
        }

        adapterAccount = new ArrayAdapter<ItemCurrentBalance>(ListTransactionsActivity.this,
                android.R.layout.simple_spinner_item, lstAccount);
        adapterAccount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccount.setAdapter(adapterAccount);
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

    private ArrayList<ItemTransaction> getListItemTransaction(String sql) {
        Cursor result = mDatabase.rawQuery(sql);
        ArrayList list = new ArrayList<>();
        if (result != null && result.moveToFirst()) {
            do {
                int id = result.getInt(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_ID));
                String date = result.getString(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_DATE));
                date = Common.getInstance().formatDate(date, Common.DATE_SAVE_TO_DB, Common.DATE_SHOW);
                String content = result.getString(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_CONTENT));
                Double money = result.getDouble(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_MONEY));
                int transType = result.getInt(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_TRANS_TYPE));
                int idCurrentBalance = result.getInt(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_ID_TBL_CB));
                ItemCurrentBalance itemCB = getItemCB(idCurrentBalance);

                Double moneyCB = itemCB.getMoney();
                String nameCB = itemCB.getName();
//                Double moneyCB = 10.0;
//                String nameCB = "null";

                ItemTransaction itemTrans = new ItemTransaction(id, date, content, money, moneyCB, nameCB, transType);
                list.add(itemTrans);
            } while (result.moveToNext());
        }

        return list;
    }

    private void initControls() {
        btnDateStart = (Button) findViewById(R.id.btn_date_start);
        btnDateEnd = (Button) findViewById(R.id.btn_date_end);
        spAccount = (Spinner) findViewById(R.id.sp_account_list_trans);
        spTransType = (Spinner) findViewById(R.id.sp_trans_type);
        lvTransaction = (ListView) findViewById(R.id.lv_trans_item);
        mDatabase = SQLiteDatabase.getInstance(ListTransactionsActivity.this);
        btnDateStart.setText(Common.getInstance().getCurrentDate(Common.DATE_SHOW));
        btnDateEnd.setText(Common.getInstance().getCurrentDate(Common.DATE_SHOW));
    }

    private void refreshList() {
        String dateStart = Common.getInstance().formatDate(btnDateStart.getText().toString(),
                Common.DATE_SHOW, Common.DATE_SAVE_TO_DB);
        String dateEnd = Common.getInstance().formatDate(btnDateEnd.getText().toString(),
                Common.DATE_SHOW, Common.DATE_SAVE_TO_DB);
        String sql = "select * from " + SQLiteDatabase.TBL_TRANSACTION + " where date(" + SQLiteDatabase.TBL_TRANS_COLUMN_DATE + ")"
                + " between date('" + dateStart + "') and date('" + dateEnd + "') " +
                "order by date(+"+SQLiteDatabase.TBL_TRANS_COLUMN_DATE+") ASC;";
        Log.d("SQL: ",sql);
        lstTransList = getListItemTransaction(sql);
        mAdapterTrans = new TransactionAdapter(ListTransactionsActivity.this, lstTransList);
        lvTransaction.setAdapter(mAdapterTrans);
    }


    DatePickerDialog.OnDateSetListener onDateSetListenerStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            btnDateStart.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            refreshList();
        }
    };
    DatePickerDialog.OnDateSetListener onDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            btnDateEnd.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            refreshList();
        }
    };

    BroadcastReceiver broadcastReceiverAddTransSucess = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showListItemTransaction();
        }
    };

    private void registerBroadcastAddTrans() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ADD_TRANS_SUCCESS);
        registerReceiver(broadcastReceiverAddTransSucess, filter);
    }

    private void unregisterBroadcastAddTrans() {
        unregisterReceiver(broadcastReceiverAddTransSucess);
    }

    BroadcastReceiver broadcastReceiverAccountChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getSpinnerListAccount();
        }
    };

    private void registerBroadcastAccChange() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ACCOUNT_CHANGE);
        registerReceiver(broadcastReceiverAccountChange, filter);
    }

    private void unregisterBroadcastAccChange() {
        unregisterReceiver(broadcastReceiverAccountChange);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastAddTrans();
        unregisterBroadcastAccChange();
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        switch (v.getId()) {
            case R.id.btn_date_start:
                new DatePickerDialog(ListTransactionsActivity.this, onDateSetListenerStart, year, month + 1, day).show();
                break;
            case R.id.btn_date_end:
                new DatePickerDialog(ListTransactionsActivity.this, onDateSetListenerEnd, year, month + 1, day).show();
                break;
        }
    }

    private void getCustomListItemTrans(int itemCBId, int transType) {
        String sql = "select * from " + SQLiteDatabase.TBL_TRANSACTION + " where 1=1 ";
        if (itemCBId >= 0) {
            sql += " and " + SQLiteDatabase.TBL_TRANS_COLUMN_ID_TBL_CB + " = " + itemCBId;
        }
        if (transType >= 0) {
            sql += " and " + SQLiteDatabase.TBL_TRANS_COLUMN_TRANS_TYPE + " = " + transType;
        }
        Cursor result = mDatabase.rawQuery(sql);
        lstTransList.clear();
        if (result != null && result.moveToFirst()) {
            do {
                int id = result.getInt(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_ID));
                String date = result.getString(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_DATE));
                String content = result.getString(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_CONTENT));
                Double money = result.getDouble(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_MONEY));
                int mtransType = result.getInt(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_TRANS_TYPE));
                int idCurrentBalance = result.getInt(result.getColumnIndex(SQLiteDatabase.TBL_TRANS_COLUMN_ID_TBL_CB));
                ItemCurrentBalance itemCB = getItemCB(idCurrentBalance);

                Double moneyCB = itemCB.getMoney();
                String nameCB = itemCB.getName();
//                Double moneyCB = 10.0;
//                String nameCB = "null";

                ItemTransaction itemTrans = new ItemTransaction(id, date, content, money, moneyCB, nameCB, mtransType);
                lstTransList.add(itemTrans);
            } while (result.moveToNext());
        }
        mAdapterTrans.notifyDataSetChanged();
    }

}
