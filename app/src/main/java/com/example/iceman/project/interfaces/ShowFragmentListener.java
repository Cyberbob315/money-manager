package com.example.iceman.project.interfaces;

/**
 * Created by iceman on 30/10/2016.
 */

public interface ShowFragmentListener {

    void onShowManagementFragment();
    void onShowAddTransactionFragment();
    void onListTransactionFragment();
    void onShowDetailFragment(int id);

}
