package com.liuwanwan.accountbook.model;

public class RecordBean {
    public boolean isIncome;
    public int type;
    public double money;
    public long recordTime;
    public String note;
    public RecordBean(boolean isIncome,int type, double money) {
        this.isIncome=isIncome;
        this.type = type;
        this.money = money;
    }
    public RecordBean(boolean isIncome,int type, double money,long recordTime,String note) {
        this.isIncome=isIncome;
        this.type = type;
        this.money = money;
        this.recordTime=recordTime;
        this.note=note;
    }
}
