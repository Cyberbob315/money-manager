package com.example.iceman.project.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.iceman.project.R;
import com.example.iceman.project.adapter.ManagementAdapter;
import com.example.iceman.project.database.SQLiteDatabase;
import com.example.iceman.project.dialog.SimpleDialog;
import com.example.iceman.project.model.ItemCurrentBalance;

import java.util.ArrayList;

public class ManagementActivity extends AppCompatActivity {
    //    LinearLayout llList;
    EditText edtAddManageName;
    EditText edtMoney;
    Button btnAdd;
    ListView lvManagement;
    ArrayList<ItemCurrentBalance> lstCurrentBalance;
    ManagementAdapter mManageAdapter;
    SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        initControls();
        initEvents();
//        addDynamicLayout();
        initListViewData();
    }

    private void initListViewData() {
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
        mManageAdapter = new ManagementAdapter(ManagementActivity.this, lstCurrentBalance);
        lvManagement.setAdapter(mManageAdapter);
    }

//    private void addDynamicLayout() {
//        LayoutInflater layoutInflater = LayoutInflater.from(ManagementActivity.this);
//
//
//        for (int i = 0; i < 3; i++) {
//            View myView = layoutInflater.inflate(R.layout.item_manage, null);
//            TextView tvType = (TextView) myView.findViewById(R.id.tv_type);
//            TextView tvMoney = (TextView) myView.findViewById(R.id.tv_money_manage);
//            Button btnDelete = (Button) myView.findViewById(R.id.btn_delete);
//
//            tvType.setText("Loại " + (i + 1));
//            tvMoney.setText("" + 100000 * (i + 1));
//
//            llList.addView(myView);
//        }
//
//    }

    private void initEvents() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    return;
                }
                String name = edtAddManageName.getText().toString();
                Double money = Double.parseDouble(edtMoney.getText().toString());
                ContentValues values = new ContentValues();
                values.put(SQLiteDatabase.TBL_CB_COLUMN_NAME, name);
                values.put(SQLiteDatabase.TBL_CB_COLUMN_MONEY, money);
                long isSuccess = mDatabase.insertRecord(SQLiteDatabase.TBL_CURRENT_BALANCE, values);
                if (isSuccess > 0) {
                    Toast.makeText(ManagementActivity.this, "Insert thành công", Toast.LENGTH_SHORT).show();
                    edtMoney.setText("");
                    edtAddManageName.setText("");
                    edtAddManageName.requestFocus();
                    initListViewData();
                } else {
                    Toast.makeText(ManagementActivity.this, "Insert thất bại", Toast.LENGTH_SHORT).show();
                }
                Intent itent = new Intent(MainActivity.ACTION_ADD_CB_SUCCESS);
                Intent itent1 = new Intent(ListTransactionsActivity.ACTION_ACCOUNT_CHANGE);
                sendBroadcast(itent);
                sendBroadcast(itent1);
            }
        });
    }

    private boolean validate(){
        if(edtAddManageName.getText().toString().trim().isEmpty()){
            SimpleDialog.showDialog(ManagementActivity.this,"Cảnh báo","Bạn chưa nhập tên tài khoản");
            edtAddManageName.requestFocus();
            return true;
        }
        if(edtMoney.getText().toString().trim().isEmpty()){
            SimpleDialog.showDialog(ManagementActivity.this,"Cảnh báo","Bạn chưa nhập số tiền");
            edtMoney.requestFocus();
            return true;
        }
        return false;
    }

    private void initControls() {
//        llList = (LinearLayout) findViewById(R.id.ll_manage);
        edtAddManageName = (EditText) findViewById(R.id.edt_add_manage);
        edtMoney = (EditText) findViewById(R.id.edt_money);

        btnAdd = (Button) findViewById(R.id.btn_add_manage);
        lvManagement = (ListView) findViewById(R.id.lv_manage);
        mDatabase = SQLiteDatabase.getInstance(ManagementActivity.this);
    }


}
