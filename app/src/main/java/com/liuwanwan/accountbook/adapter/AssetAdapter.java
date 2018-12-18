package com.liuwanwan.accountbook.adapter;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.model.AssetItem;
import com.liuwanwan.accountbook.model.ClassItem;
import com.liuwanwan.accountbook.utils.AssetDialogFragment;

import java.util.List;

public class AssetAdapter extends BaseAdapter {
    List<AssetItem> assetItemList;
    private FragmentManager fragmentManager;
    public AssetAdapter(FragmentManager fragmentManager,List<AssetItem> assetItemList){
        this.fragmentManager=fragmentManager;
        this.assetItemList=assetItemList;
    }
    @Override
    public int getCount() {
        return assetItemList.size();
    }

    @Override
    public AssetItem getItem(int i) {
        return assetItemList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final AssetItem assetItem=getItem(position);
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_gridview, null);
            holder = new ViewHolder();
            holder.btAssetName = (Button) convertView.findViewById(R.id.bt_assetclass);
            holder.tvAssetMoney = (TextView) convertView.findViewById(R.id.tv_assetmoney);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.btAssetName.setText(assetItem.assetItemName);
        holder.tvAssetMoney.setText("¥"+assetItem.assetItemMoney);
        holder.btAssetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssetDialogFragment assetDialogFragment = AssetDialogFragment.newInstance(assetItem.assetItemIndex);
                //AssetDialogFragment assetDialogFragment = AssetDialogFragment.newInstance(assetItem.assetItemName);
                //assetDialogFragment.setTargetFragment(fragmentManager, 0);
                //bottomDialogFragment.show(getChildFragmentManager(),"ShowDetaiRecord");//报错
                assetDialogFragment.show(fragmentManager,"ShowDetailAsset");
            }
        });
        return convertView;
    }
    static class ViewHolder {
        Button btAssetName;
        TextView tvAssetMoney;
    }
}
