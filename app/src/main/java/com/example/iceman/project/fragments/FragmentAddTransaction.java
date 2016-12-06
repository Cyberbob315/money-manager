package com.example.iceman.project.fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.iceman.project.R;
import com.example.iceman.project.activity.ListTransactionsActivity;
import com.example.iceman.project.activity.MainActivity;
import com.example.iceman.project.adapter.CurrentBalanceDropDownAdapter;
import com.example.iceman.project.database.SQLiteDatabase;
import com.example.iceman.project.dialog.SimpleDialog;
import com.example.iceman.project.interfaces.OnViewHolderClicked;
import com.example.iceman.project.model.ItemCurrentBalance;
import com.example.iceman.project.utils.Common;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAddTransaction#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAddTransaction extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = FragmentAddTransaction.class.getName();

    Spinner spAccount;
    RadioButton rdbSpend, rdbReceive;
    EditText edtMoneyAdd, edtReasonAdd;
    Button btnDate, btnTime, btnSave, btnSaveNClose;
    SQLiteDatabase mDatabase;
    ArrayList<ItemCurrentBalance> lstCurrentBalance;

    PopupWindow popupWindowsItemCB;
    CurrentBalanceDropDownAdapter popupAdapter;
    RecyclerView rvItemCBDropDown;

    RelativeLayout rlShowList;
    TextView tvItemCb;
    int selectedItem;

    String dateSaveToDB = "";

    View view;

    public FragmentAddTransaction() {
        // Required empty public constructor
    }


    public static FragmentAddTransaction newInstance() {
        FragmentAddTransaction fragment = new FragmentAddTransaction();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_add_transaction, container, false);
        initControls();
        initEvents();
        getListCurrentBalance();
        initPopupWindowsItem();
        return view;

    }

    private void getListCurrentBalance() {
        String sql = "select * from " + SQLiteDatabase.TBL_CURRENT_BALANCE;
        Cursor result = mDatabase.rawQuery(sql);
        lstCurrentBalance = new ArrayList<>();
        if (result != null && result.moveToFirst()) {
            do {

                int id = result.getInt(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_ID));
                String name = result.getString(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_NAME));
                String money = result.getString(result.getColumnIndex(SQLiteDatabase.TBL_CB_COLUMN_MONEY));
                ItemCurrentBalance item = new ItemCurrentBalance(id, name, Double.parseDouble(money));
                lstCurrentBalance.add(item);

            } while (result.moveToNext());
        }
        popupAdapter = new CurrentBalanceDropDownAdapter(getActivity(), lstCurrentBalance, onItemSpinnerClicked);
        rvItemCBDropDown.setAdapter(popupAdapter);
    }

    private void initEvents() {
        btnTime.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnSaveNClose.setOnClickListener(this);

        rlShowList.setOnClickListener(this);
    }

    private void initControls() {
        rlShowList = (RelativeLayout) view.findViewById(R.id.rl_spinner);
        tvItemCb = (TextView) view.findViewById(R.id.tv_item_cb_drop_down);
        rvItemCBDropDown = new RecyclerView(getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvItemCBDropDown.setLayoutManager(layoutManager);

        rdbReceive = (RadioButton) view.findViewById(R.id.rdb_receive);
        rdbSpend = (RadioButton) view.findViewById(R.id.rdb_spend);
        rdbSpend.setChecked(true);

        edtMoneyAdd = (EditText) view.findViewById(R.id.edt_money_add);
        edtReasonAdd = (EditText) view.findViewById(R.id.edt_reason_add);

        btnDate = (Button) view.findViewById(R.id.btn_date_add);
        btnDate.setText(Common.getInstance().getCurrentDate(Common.DATE_SHOW));
        dateSaveToDB = Common.getInstance().getCurrentDate(Common.DATE_SAVE_TO_DB);

        btnTime = (Button) view.findViewById(R.id.btn_time_add);
        btnTime.setText(Common.getInstance().getCurrentTime());

        btnSave = (Button) view.findViewById(R.id.btn_save_new_trans);
        btnSaveNClose = (Button) view.findViewById(R.id.btn_save_and_close);
        mDatabase = SQLiteDatabase.getInstance(getContext());

    }

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            btnDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            dateSaveToDB = Common.getInstance().formatDate(btnDate.getText().toString(),
                    Common.DATE_SHOW, Common.DATE_SAVE_TO_DB);
        }
    };

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            btnTime.setText(hourOfDay + ":" + minute);
        }
    };

    private boolean validate() {
        if (edtMoneyAdd.getText().toString().isEmpty()) {
            SimpleDialog.showDialog(getContext(), "Cảnh báo", "Bạn chưa nhập số tiền!");
            edtMoneyAdd.requestFocus();
            return false;
        }

        if (edtReasonAdd.getText().toString().isEmpty()) {
            SimpleDialog.showDialog(getContext(), "Cảnh báo", "Bạn chưa nhập lý do!");
            edtReasonAdd.requestFocus();
            return false;
        }

        Double money = Double.parseDouble(edtMoneyAdd.getText().toString());
//        ItemCurrentBalance itemAcc = (ItemCurrentBalance) spAccount.getSelectedItem();
//        if (money > itemAcc.getMoney() && rdbSpend.isChecked()) {
//            SimpleDialog.showDialog(getContext(), "Cảnh báo", "Số tiền bạn nhập phải bé hơn số dư tài khoản!");
//            edtMoneyAdd.requestFocus();
//            return false;
//        }
        return true;
    }

    private void saveDataToDatabase() {
        ContentValues valuesTrans = new ContentValues();
        ContentValues valuesCB = new ContentValues();
        ItemCurrentBalance selectedItem = (ItemCurrentBalance) spAccount.getSelectedItem();
        int transType;
        Double currentMoney = selectedItem.getMoney();

        if (rdbSpend.isChecked()) {
            transType = 0;
            selectedItem.setMoney(currentMoney - Double.parseDouble(edtMoneyAdd.getText().toString()));
        } else {
            transType = 1;
            selectedItem.setMoney(currentMoney + Double.parseDouble(edtMoneyAdd.getText().toString()));
        }

        valuesCB.put(SQLiteDatabase.TBL_CB_COLUMN_NAME, selectedItem.getName());
        valuesCB.put(SQLiteDatabase.TBL_CB_COLUMN_MONEY, selectedItem.getMoney());
        mDatabase.updateRecord(SQLiteDatabase.TBL_CURRENT_BALANCE, valuesCB,
                SQLiteDatabase.TBL_CB_COLUMN_ID, new String[]{selectedItem.getId() + ""});


        valuesTrans.put(SQLiteDatabase.TBL_TRANS_COLUMN_CONTENT, edtReasonAdd.getText().toString());
        valuesTrans.put(SQLiteDatabase.TBL_TRANS_COLUMN_MONEY, edtMoneyAdd.getText().toString());
        valuesTrans.put(SQLiteDatabase.TBL_TRANS_COLUMN_DATE, dateSaveToDB);
        valuesTrans.put(SQLiteDatabase.TBL_TRANS_COLUMN_ID_TBL_CB, selectedItem.getId());
        valuesTrans.put(SQLiteDatabase.TBL_TRANS_COLUMN_TRANS_TYPE, transType);
        long isSuccess = mDatabase.insertRecord(SQLiteDatabase.TBL_TRANSACTION, valuesTrans);
        if (isSuccess > 0) {
            Toast.makeText(getContext(), "Insert thành công", Toast.LENGTH_SHORT).show();
            edtMoneyAdd.setText("");
            edtReasonAdd.setText("");
            edtReasonAdd.requestFocus();
            btnDate.setText(Common.getInstance().getCurrentDate(Common.DATE_SHOW));
            btnTime.setText(Common.getInstance().getCurrentTime());

            Intent intent = new Intent(MainActivity.ACTION_ADD_CB_SUCCESS);
            getContext().sendBroadcast(intent);
        } else {
            Toast.makeText(getContext(), "Insert thất bại", Toast.LENGTH_SHORT).show();
        }


    }

//    private void insert(){
//        for(int i =1;i<200;i++) {
//            ContentValues valuesTrans = new ContentValues();
//            ContentValues valuesCB = new ContentValues();
//            ItemCurrentBalance selectedItem = (ItemCurrentBalance) spAccount.getSelectedItem();
//            int transType;
//            Double currentMoney = selectedItem.getMoney();
//
//            if (rdbSpend.isChecked()) {
//                transType = 0;
//                selectedItem.setMoney(currentMoney - 100 * i);
//            } else {
//                transType = 1;
//                selectedItem.setMoney(currentMoney + 100 * i);
//            }
//
//            valuesCB.put(SQLiteDatabase.TBL_CB_COLUMN_NAME, selectedItem.getName() + i);
//            valuesCB.put(SQLiteDatabase.TBL_CB_COLUMN_MONEY, selectedItem.getMoney());
//            mDatabase.updateRecord(SQLiteDatabase.TBL_CURRENT_BALANCE, valuesCB,
//                    SQLiteDatabase.TBL_CB_COLUMN_ID, new String[]{selectedItem.getId() + ""});
//
//
//            valuesTrans.put(SQLiteDatabase.TBL_TRANS_COLUMN_CONTENT, edtReasonAdd.getText().toString() + i);
//            valuesTrans.put(SQLiteDatabase.TBL_TRANS_COLUMN_MONEY, edtMoneyAdd.getText().toString() + i);
//            valuesTrans.put(SQLiteDatabase.TBL_TRANS_COLUMN_DATE, dateSaveToDB);
//            valuesTrans.put(SQLiteDatabase.TBL_TRANS_COLUMN_ID_TBL_CB, selectedItem.getId());
//            valuesTrans.put(SQLiteDatabase.TBL_TRANS_COLUMN_TRANS_TYPE, transType);
//            mDatabase.insertRecord(SQLiteDatabase.TBL_TRANSACTION, valuesTrans);
//        }
//    }

    private void sendBroadcastAddSuccess() {
        Intent intent = new Intent(ListTransactionsActivity.ACTION_ADD_TRANS_SUCCESS);
        getContext().sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        switch (v.getId()) {
            case R.id.btn_save_new_trans:
                if (validate()) {
                    saveDataToDatabase();
                    sendBroadcastAddSuccess();
                }
                break;
            case R.id.btn_save_and_close:
                if (validate()) {
                    saveDataToDatabase();
                    sendBroadcastAddSuccess();
                    getActivity().onBackPressed();
                }
                break;
            case R.id.btn_date_add:
                new DatePickerDialog(getContext(), onDateSetListener, year, month, day).show();
                break;
            case R.id.btn_time_add:
                new TimePickerDialog(getContext(), onTimeSetListener, hour, minutes, true).show();
                break;
            case R.id.rl_spinner:
                popupWindowsItemCB.showAsDropDown(rlShowList, 0, 0);
                break;
        }
    }

    private void initPopupWindowsItem() {
        popupWindowsItemCB = new PopupWindow(getContext());

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                popupWindowsItemCB.setWidth(rlShowList.getWidth());
            }
        });
        popupWindowsItemCB.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindowsItemCB.setContentView(rvItemCBDropDown);

    }

    OnViewHolderClicked onItemSpinnerClicked = new OnViewHolderClicked() {
        @Override
        public void sendData(String name, int id) {
            tvItemCb.setText(name);
            selectedItem = id;
            Log.d("name", id + "");
            popupWindowsItemCB.dismiss();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        popupWindowsItemCB.dismiss();
    }
}
