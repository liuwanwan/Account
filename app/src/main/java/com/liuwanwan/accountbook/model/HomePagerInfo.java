package com.liuwanwan.accountbook.model;

import android.support.v4.app.Fragment;
public class HomePagerInfo {
    public String title;
    public Fragment fragment;

    public HomePagerInfo(String title, Fragment fragment) {
        this.title = title;
        this.fragment = fragment;
    }
}
