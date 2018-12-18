package com.liuwanwan.accountbook.model;

public class AssetItem {
    public int assetItemIndex;
    public String assetItemName;
    public double assetItemMoney;
    public AssetItem(int assetItemIndex,String assetItemName,double assetItemMoney){
        this.assetItemIndex=assetItemIndex;
        this.assetItemName=assetItemName;
        this.assetItemMoney=assetItemMoney;
    }
}
