package com.example.iceman.project.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.iceman.project.R;
import com.example.iceman.project.database.SQLiteDatabase;
import com.example.iceman.project.dialog.SimpleDialog;
import com.example.iceman.project.model.ItemCurrentBalance;
import com.example.iceman.project.utils.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner spAccount;
    RadioButton rdbSpend, rdbReceive;
    EditText edtMoneyAdd, edtReasonAdd;
    Button btnDate, btnTime, btnSave, btnSaveNClose;
    SQLiteDatabase mDatabase;
    ArrayList<ItemCurrentBalance> lstCurrentBalance;
    ArrayAdapter<ItemCurrentBalance> mAdapterCB;

    String dateSaveToDB = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        initControls();
        initEvents();
        getListCurrentBalance();
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

        mAdapterCB = new ArrayAdapter<ItemCurrentBalance>(AddActivity.this,
                android.R.layout.simple_spinner_item, lstCurrentBalance);

        mAdapterCB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccount.setAdapter(mAdapterCB);
    }

    private void initEvents() {
        btnTime.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnSaveNClose.setOnClickListener(this);


    }

    private void initControls() {

        rdbReceive = (RadioButton) findViewById(R.id.rdb_receive);
        rdbSpend = (RadioButton) findViewById(R.id.rdb_spend);
        rdbSpend.setChecked(true);

        edtMoneyAdd = (EditText) findViewById(R.id.edt_money_add);
        edtReasonAdd = (EditText) findViewById(R.id.edt_reason_add);

        btnDate = (Button) findViewById(R.id.btn_date_add);
        btnDate.setText(Common.getInstance().getCurrentDate(Common.DATE_SHOW));
        dateSaveToDB = Common.getInstance().getCurrentDate(Common.DATE_SAVE_TO_DB);

        btnTime = (Button) findViewById(R.id.btn_time_add);
        btnTime.setText(Common.getInstance().getCurrentTime());

        btnSave = (Button) findViewById(R.id.btn_save_new_trans);
        btnSaveNClose = (Button) findViewById(R.id.btn_save_and_close);
        mDatabase = SQLiteDatabase.getInstance(AddActivity.this);

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
            SimpleDialog.showDialog(AddActivity.this, "Cảnh báo", "Bạn chưa nhập số tiền!");
            edtMoneyAdd.requestFocus();
            return false;
        }

        if (edtReasonAdd.getText().toString().isEmpty()) {
            SimpleDialog.showDialog(AddActivity.this, "Cảnh báo", "Bạn chưa nhập lý do!");
            edtReasonAdd.requestFocus();
            return false;
        }

        Double money = Double.parseDouble(edtMoneyAdd.getText().toString());
        ItemCurrentBalance itemAcc = (ItemCurrentBalance) spAccount.getSelectedItem();
        if(money > itemAcc.getMoney() && rdbSpend.isChecked()){
            SimpleDialog.showDialog(AddActivity.this,"Cảnh báo","Số tiền bạn nhập phải bé hơn số dư tài khoản!");
            edtMoneyAdd.requestFocus();
            return false;
        }
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
            Toast.makeText(AddActivity.this, "Insert thành công", Toast.LENGTH_SHORT).show();
            edtMoneyAdd.setText("");
            edtReasonAdd.setText("");
            edtReasonAdd.requestFocus();
            btnDate.setText(Common.getInstance().getCurrentDate(Common.DATE_SHOW));
            btnTime.setText(Common.getInstance().getCurrentTime());

            Intent intent = new Intent(MainActivity.ACTION_ADD_CB_SUCCESS);
            sendBroadcast(intent);
        } else {
            Toast.makeText(AddActivity.this, "Insert thất bại", Toast.LENGTH_SHORT).show();
        }


    }

    private void sendBroadcastAddSuccess() {
        Intent intent = new Intent(ListTransactionsActivity.ACTION_ADD_TRANS_SUCCESS);
        sendBroadcast(intent);
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
                    finish();
                }
                break;
            case R.id.btn_date_add:
                new DatePickerDialog(AddActivity.this, onDateSetListener, year, month, day).show();
                break;
            case R.id.btn_time_add:
                new TimePickerDialog(AddActivity.this, onTimeSetListener, hour, minutes, true).show();
                break;
        }
    }
}
