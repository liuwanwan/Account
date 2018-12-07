package com.liuwanwan.accountbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.liuwanwan.accountbook.model.HomePagerInfo;

import java.util.List;

public class HomePagerAdapter extends FragmentStatePagerAdapter {
    private List<HomePagerInfo> mData;
    public HomePagerAdapter(FragmentManager fm, List<HomePagerInfo> data) {
        super(fm);
        this.mData = data;
    }

    @Override
    public Fragment getItem(int position) {
        return mData.get(position).fragment;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mData.get(position).title;
    }

}
