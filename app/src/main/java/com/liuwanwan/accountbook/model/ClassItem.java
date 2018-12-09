package com.liuwanwan.accountbook.model;

/**
 * Created by Judy on 2017/2/16.
 */
public class ClassItem {
    public String date;
    public double money;
    public long recordTime;
    public ClassItem(String date, double money,long recordTime) {
        this.date = date;
        this.money = money;
        this.recordTime=recordTime;
    }
}
