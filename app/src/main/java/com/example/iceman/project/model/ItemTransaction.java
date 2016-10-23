package com.example.iceman.project.model;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by iceman on 20/10/2016.
 */

public class ItemTransaction implements Serializable {

    public static final String TRANS_TYPE_SPEND = "Khoản chi";
    public static final String TRANS_TYPE_RECEIVE = "Khoản thu";

    private int id;
    private String date;
    private String content;
    private double money;
    private double accBalance;
    private String account;
    private int transType;

    public ItemTransaction(int id, String date, String content, double money, double accBalance, String account, int transType) {
        this.id = id;
        this.date = date;
        this.content = content;
        this.money = money;
        this.accBalance = accBalance;
        this.account = account;
        this.transType = transType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getAccBalance() {
        return accBalance;
    }

    public void setAccBalance(double accBalance) {
        this.accBalance = accBalance;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }


}
