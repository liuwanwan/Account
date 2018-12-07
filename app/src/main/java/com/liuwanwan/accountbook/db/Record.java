package com.liuwanwan.accountbook.db;

import org.litepal.crud.LitePalSupport;

public class Record extends LitePalSupport {

    public boolean isIncome;
    public int inOrOutType;
    public double money;
    public String note;
    public long recordTime;
    public String recordedTime;
    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }
    public String getRecordedTime() {
        return recordedTime;
    }

    public void setRecordedTime(String recordedTime) {
        this.recordedTime = recordedTime;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getInOrOutType() {
        return inOrOutType;
    }

    public void setInOrOutType(int inOrOutType) {
        this.inOrOutType = inOrOutType;
    }
}
