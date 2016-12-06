package com.example.iceman.project.fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iceman.project.R;
import com.example.iceman.project.activity.ListTransactionsActivity;
import com.example.iceman.project.activity.MainActivity;
import com.example.iceman.project.adapter.RvManagementAdapter;
import com.example.iceman.project.database.SQLiteDatabase;
import com.example.iceman.project.dialog.SimpleDialog;
import com.example.iceman.project.model.ItemCurrentBalance;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentManagement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentManagement extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = FragmentManagement.class.getName();

    private String mParam1;
    private String mParam2;

    View view;

    //    LinearLayout llList;
    EditText edtAddManageName;
    EditText edtMoney;
    Button btnAdd;
    RecyclerView rvManagement;
    ArrayList<ItemCurrentBalance> lstCurrentBalance;
    RvManagementAdapter mManageAdapter;
    SQLiteDatabase mDatabase;

    public FragmentManagement() {
        // Required empty public constructor
    }

    public static FragmentManagement newInstance() {
        FragmentManagement fragment = new FragmentManagement();
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
        view = inflater.inflate(R.layout.activity_management, container, false);
        initControls();
        initEvents();
//        addDynamicLayout();
        initListViewData();
        return view;
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
        mManageAdapter = new RvManagementAdapter(getContext(), lstCurrentBalance);
        rvManagement.setAdapter(mManageAdapter);
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
                    Toast.makeText(getContext(), "Insert thành công", Toast.LENGTH_SHORT).show();
                    edtMoney.setText("");
                    edtAddManageName.setText("");
                    edtAddManageName.requestFocus();
                    initListViewData();
                } else {
                    Toast.makeText(getContext(), "Insert thất bại", Toast.LENGTH_SHORT).show();
                }
                Intent itent = new Intent(MainActivity.ACTION_ADD_CB_SUCCESS);
                Intent itent1 = new Intent(ListTransactionsActivity.ACTION_ACCOUNT_CHANGE);
                getContext().sendBroadcast(itent);
                getContext().sendBroadcast(itent1);
            }
        });
    }

    private boolean validate(){
        if(edtAddManageName.getText().toString().trim().isEmpty()){
            SimpleDialog.showDialog(getContext(),"Cảnh báo","Bạn chưa nhập tên tài khoản");
            edtAddManageName.requestFocus();
            return true;
        }
        if(edtMoney.getText().toString().trim().isEmpty()){
            SimpleDialog.showDialog(getContext(),"Cảnh báo","Bạn chưa nhập số tiền");
            edtMoney.requestFocus();
            return true;
        }
        return false;
    }

    private void initControls() {
//        llList = (LinearLayout) findViewById(R.id.ll_manage);
        edtAddManageName = (EditText) view.findViewById(R.id.edt_add_manage);
        edtMoney = (EditText) view.findViewById(R.id.edt_money);

        btnAdd = (Button) view.findViewById(R.id.btn_add_manage);
        rvManagement = (RecyclerView) view.findViewById(R.id.rv_manage);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvManagement.setLayoutManager(layoutManager);

        mDatabase = SQLiteDatabase.getInstance(getContext());
    }

}
