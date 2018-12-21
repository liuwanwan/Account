package com.liuwanwan.accountbook.db;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class Record extends LitePalSupport implements Serializable {

    public boolean isIncome;
    public int inOrOutType;
    public double money;
    public String note;
    public String account;
    public long recordTime;
    public String recordedTime;
    public String recordedYear;
    public String recordedYearMonth;


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    public String getRecordedYearMonth() {
        return recordedYearMonth;
    }

    public void setRecordedYearMonth(String recordedYearMonth) {
        this.recordedYearMonth = recordedYearMonth;
    }

    public String getRecordedYear() {
        return recordedYear;
    }

    public void setRecordedYear(String recordedYear) {
        this.recordedYear = recordedYear;
    }

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
