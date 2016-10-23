package com.example.iceman.project.model;

/**
 * Created by iceman on 18/10/2016.
 */

public class ItemCurrentBalance {
    private int id;
    private String name;
    private double money;

    public ItemCurrentBalance() {
    }


    public ItemCurrentBalance(int id, String name, double money) {
        this.id = id;
        this.name = name;
        this.money = money;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
