package com.liuwanwan.accountbook.db;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class Asset extends LitePalSupport implements Serializable {

    public int type;
    public String name;
    public double money;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

}
