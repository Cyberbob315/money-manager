package com.example.iceman.project.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.iceman.project.R;
import com.example.iceman.project.database.SQLiteDatabase;
import com.example.iceman.project.model.ItemCurrentBalance;
import com.example.iceman.project.model.ItemTransaction;
import com.example.iceman.project.utils.Common;

public class TransactionDetailActivity extends AppCompatActivity {
    TextView tvDate, tvContent, tvMoney, tvBalance;
    SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        initControls();
        getAndShowTransData();
    }

    private void getAndShowTransData() {
        Intent intent = getIntent();
        if (intent != null) {
            int transId = intent.getExtras().getInt(ListTransactionsActivity.KEY_TRANS);
            showTransDetail(transId);
        }
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
        tvDate = (TextView) findViewById(R.id.tv_date_detail);
        tvContent = (TextView) findViewById(R.id.tv_content_detail);
        tvMoney = (TextView) findViewById(R.id.tv_money_detail);
        tvBalance = (TextView) findViewById(R.id.tv_balance_detail);
        mDatabase = SQLiteDatabase.getInstance(this);
    }
}
