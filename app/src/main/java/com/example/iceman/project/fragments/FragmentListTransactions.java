package com.example.iceman.project.fragments;


import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.iceman.project.R;
import com.example.iceman.project.adapter.RvTransactionAdapter;
import com.example.iceman.project.database.SQLiteDatabase;
import com.example.iceman.project.dialog.SimpleDialog;
import com.example.iceman.project.interfaces.ShowFragmentListener;
import com.example.iceman.project.model.ItemCurrentBalance;
import com.example.iceman.project.model.ItemTransaction;
import com.example.iceman.project.utils.Common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentListTransactions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentListTransactions extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = FragmentListTransactions.class.getName();

    ShowFragmentListener mCallBack;


    View view;
    public static final String ACTION_ADD_TRANS_SUCCESS = "com.example.iceman.project.ACTION_ADD_TRANS_SUCCESS";
    public static final String ACTION_ACCOUNT_CHANGE = "com.example.iceman.project.ACTION_ACCOUNT_CHANGE";
    public static final String KEY_TRANS = "key_trans";

    Button btnDateStart;
    Button btnDateEnd;
    Spinner spAccount;
    Spinner spTransType;
    RecyclerView rvTransaction;
    ProgressBar pbLoadList;

    SQLiteDatabase mDatabase;
    ArrayList<ItemTransaction> lstTransList;
    ArrayList<ItemCurrentBalance> lstAccount;
    ArrayList<String> lstType;

    RvTransactionAdapter mAdapterTrans;
    ArrayAdapter<ItemCurrentBalance> adapterAccount;

    int selectedTransItem;


    public FragmentListTransactions() {
        // Required empty public constructor
    }


    public static FragmentListTransactions newInstance() {
        FragmentListTransactions fragment = new FragmentListTransactions();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallBack = (ShowFragmentListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        registerBroadcastAddTrans();
        registerBroadcastAccChange();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_list_transactions, container, false);
        initControls();
        initEvents();

        getSpinnerListAccount();
        initSpinnerTransType();
        new AsyncTaskGetListTransaction().execute();
        return view;
    }

    private ArrayList<ItemTransaction> getListItemTransaction() {
        String sql = "select * from " + SQLiteDatabase.TBL_TRANSACTION
                + " order by date(" + SQLiteDatabase.TBL_TRANS_COLUMN_DATE + ") ASC;";
        lstTransList = getListItemTransaction(sql);
        return lstTransList;

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
                    getListItemTransaction();
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
                    getListItemTransaction();
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

//        rvTransaction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d(TAG,lstTransList.get(position).getId()+"");
//                mCallBack.onShowDetailFragment(lstTransList.get(position).getId());
//
//            }
//        });
//
//        rvTransaction.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                selectedTransItem = position;
//                return false;
//            }
//        });
    }

    private void initSpinnerTransType() {
        lstType = new ArrayList<>();
        lstType.add("Tất cả");
        lstType.add(ItemTransaction.TRANS_TYPE_SPEND);
        lstType.add(ItemTransaction.TRANS_TYPE_RECEIVE);

        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(getActivity(),
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

        adapterAccount = new ArrayAdapter<ItemCurrentBalance>(getActivity(),
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
        btnDateStart = (Button) view.findViewById(R.id.btn_date_start);
        btnDateEnd = (Button) view.findViewById(R.id.btn_date_end);
        spAccount = (Spinner) view.findViewById(R.id.sp_account_list_trans);
        spTransType = (Spinner) view.findViewById(R.id.sp_trans_type);
        rvTransaction = (RecyclerView) view.findViewById(R.id.rv_trans_item);
        pbLoadList = (ProgressBar) view.findViewById(R.id.pb);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvTransaction.setLayoutManager(layoutManager);

        mDatabase = SQLiteDatabase.getInstance(getActivity());
        btnDateStart.setText(Common.getInstance().getCurrentDate(Common.DATE_SHOW));
        btnDateEnd.setText(Common.getInstance().getCurrentDate(Common.DATE_SHOW));
        registerForContextMenu(rvTransaction);
    }

    private void refreshList() {
        String dateStart = Common.getInstance().formatDate(btnDateStart.getText().toString(),
                Common.DATE_SHOW, Common.DATE_SAVE_TO_DB);
        String dateEnd = Common.getInstance().formatDate(btnDateEnd.getText().toString(),
                Common.DATE_SHOW, Common.DATE_SAVE_TO_DB);
        String sql = "select * from " + SQLiteDatabase.TBL_TRANSACTION + " where date(" + SQLiteDatabase.TBL_TRANS_COLUMN_DATE + ")"
                + " between date('" + dateStart + "') and date('" + dateEnd + "') " +
                "order by date(+" + SQLiteDatabase.TBL_TRANS_COLUMN_DATE + ") ASC;";
        Log.d("SQL: ", sql);
        lstTransList = getListItemTransaction(sql);
        mAdapterTrans = new RvTransactionAdapter(getActivity(), lstTransList);
        rvTransaction.setAdapter(mAdapterTrans);
    }


    DatePickerDialog.OnDateSetListener onDateSetListenerStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            if (view.isShown()) {

                btnDateStart.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                Date dateStart = Common.getInstance().parseStr2Date(btnDateStart.getText().toString(), Common.DATE_SHOW);
                Date dateEnd = Common.getInstance().parseStr2Date(btnDateEnd.getText().toString(), Common.DATE_SHOW);

                if (dateStart.compareTo(dateEnd) > 0) {
                    SimpleDialog.showDialog(getActivity(), "Cảnh báo", "Khoảng ngày không hợp lệ");
                }
                Log.d("Thobg bao", "test");
                refreshList();
            }
        }
    };
    DatePickerDialog.OnDateSetListener onDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            if (view.isShown()) {
                btnDateEnd.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                Date dateStart = Common.getInstance().parseStr2Date(btnDateStart.getText().toString(), Common.DATE_SHOW);
                Date dateEnd = Common.getInstance().parseStr2Date(btnDateEnd.getText().toString(), Common.DATE_SHOW);

                if (dateStart.compareTo(dateEnd) > 0) {
                    SimpleDialog.showDialog(getActivity(), "Cảnh báo", "Khoảng ngày không hợp lệ");
                }
                refreshList();
            }
        }
    };

    BroadcastReceiver broadcastReceiverAddTransSucess = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getListItemTransaction();
        }
    };

    private void registerBroadcastAddTrans() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ADD_TRANS_SUCCESS);
        getActivity().registerReceiver(broadcastReceiverAddTransSucess, filter);
    }

    private void unregisterBroadcastAddTrans() {
        getActivity().unregisterReceiver(broadcastReceiverAddTransSucess);
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
        getActivity().registerReceiver(broadcastReceiverAccountChange, filter);
    }

    private void unregisterBroadcastAccChange() {
        getActivity().unregisterReceiver(broadcastReceiverAccountChange);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcastAddTrans();
        unregisterBroadcastAccChange();
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        switch (v.getId()) {
            case R.id.btn_date_start:
                new DatePickerDialog(getContext(), onDateSetListenerStart, year, month, day).show();
                break;
            case R.id.btn_date_end:
                new DatePickerDialog(getContext(), onDateSetListenerEnd, year, month, day).show();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.item_trans_context_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_copy:
                break;
            case R.id.item_delete:
                confirmDialog(lstTransList.get(selectedTransItem));
                break;
            case R.id.item_forward:
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void confirmDialog(ItemTransaction item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cảnh báo");
        builder.setMessage("Bạn có chắc chắn muốn xóa ?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.deleteRecord(SQLiteDatabase.TBL_TRANSACTION,
                        SQLiteDatabase.TBL_TRANS_COLUMN_ID, new String[]{item.getId() + ""});
                lstTransList.remove(item);
                mAdapterTrans.notifyDataSetChanged();
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

    private class AsyncTaskGetListTransaction extends AsyncTask<Void, Void, ArrayList<ItemTransaction>> {

        @Override
        protected void onPreExecute() {
            pbLoadList.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ItemTransaction> doInBackground(Void... params) {

            return getListItemTransaction();
        }

        @Override
        protected void onPostExecute(ArrayList<ItemTransaction> aVoid) {
            mAdapterTrans = new RvTransactionAdapter(getActivity(), aVoid);
            rvTransaction.setAdapter(mAdapterTrans);
            pbLoadList.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }

}
