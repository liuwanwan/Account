package com.liuwanwan.accountbook;

import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {
    public static final int[] expenseIds = {R.drawable.ic_cost_type_0,R.drawable.ic_cost_type_1,R.drawable.ic_cost_type_2,
            R.drawable.ic_cost_type_3,R.drawable.ic_cost_type_4,R.drawable.ic_cost_type_5,R.drawable.ic_cost_type_6,
            R.drawable.ic_cost_type_7,R.drawable.ic_cost_type_8,R.drawable.ic_cost_type_10,
            R.drawable.ic_cost_type_11,R.drawable.ic_cost_type_12,R.drawable.ic_cost_type_13,R.drawable.ic_cost_type_14,
            R.drawable.ic_cost_type_custom};
    public static final String[] expenseTypes = {"支出","餐饮","交通",
            "购物","学习","医疗","水电",
            "住房","母婴","丽人",
            "日用", "数码", "娱乐", "维修","自定义"};

    public static final int[] incomeIds = {R.drawable.ic_income_type_0,R.drawable.ic_income_type_1, R.drawable.ic_income_type_2, R.drawable.ic_income_type_3,
            R.drawable.ic_income_type_4, R.drawable.ic_income_type_5, R.drawable.ic_income_type_6, R.drawable.ic_cost_type_custom};
    public static final String[] incomeTypes = {"收入","工资","零花钱","外快","投资","意外收入","红包","自定义"};
    public static final int ADD_DEL_RECORD=1;
    public static final int FLUSH_RECORD=2;
    public static final int DEL_EDIT_ASSET=3;
    public static final int ADD_ASSET=4;
    public void onCreate() {
        super.onCreate();
    }
}
