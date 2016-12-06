package com.example.iceman.project.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.iceman.project.R;
import com.example.iceman.project.fragments.FragmentAddTransaction;
import com.example.iceman.project.fragments.FragmentDetail;
import com.example.iceman.project.fragments.FragmentHome;
import com.example.iceman.project.fragments.FragmentListTransactions;
import com.example.iceman.project.fragments.FragmentManagement;
import com.example.iceman.project.interfaces.ShowFragmentListener;

public class HostActivity extends AppCompatActivity implements ShowFragmentListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        FragmentHome fragmentHome = FragmentHome.newInstance();
        showFragment(fragmentHome, FragmentHome.TAG);

    }

    private void showFragment(Fragment fragment, String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.add(R.id.activity_host, fragment, tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onShowManagementFragment() {
        FragmentManagement fragmentManagement = FragmentManagement.newInstance();
        showFragment(fragmentManagement, FragmentManagement.TAG);
    }

    @Override
    public void onShowAddTransactionFragment() {
        FragmentAddTransaction fragmentAddTransaction = FragmentAddTransaction.newInstance();
        showFragment(fragmentAddTransaction, FragmentAddTransaction.TAG);
    }

    @Override
    public void onListTransactionFragment() {
        FragmentListTransactions fragmentListTransactions = FragmentListTransactions.newInstance();
        showFragment(fragmentListTransactions, FragmentListTransactions.TAG);
    }

    @Override
    public void onShowDetailFragment(int idTrans) {
        FragmentDetail fragmentDetail = FragmentDetail.newInstance(idTrans);
        showFragment(fragmentDetail, FragmentDetail.TAG);
    }
}
