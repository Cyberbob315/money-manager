package com.example.iceman.project.utils;

import com.example.iceman.project.model.ItemTransaction;

import java.util.Comparator;

/**
 * Created by iceman on 23/10/2016.
 */

public class ItemTransComparator implements Comparator<ItemTransaction> {
    public enum Order {DateTime, Content, Money}

    private Order sortingBy = Order.DateTime;

    @Override
    public int compare(ItemTransaction o1, ItemTransaction o2) {
        switch (sortingBy){
            case DateTime:return o1.getDate().compareToIgnoreCase(o2.getDate());
            case Content: return o1.getContent().compareTo(o2.getContent());
            case Money:
                if(o1.getMoney()>o2.getMoney()){
                    return 1;
                }else if(o1.getMoney() == o2.getMoney()){
                    return 0;
                }else {
                    return -1;
                }
        }
        throw new RuntimeException("Practically unreachable code, can't be thrown");
    }

    public void setSortingBy(Order sortBy) {
        this.sortingBy = sortBy;
    }
}
