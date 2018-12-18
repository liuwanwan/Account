package com.liuwanwan.accountbook.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.db.Asset;
import com.liuwanwan.accountbook.db.Record;
import com.liuwanwan.accountbook.model.AssetItem;
import com.liuwanwan.accountbook.model.MessageEvent;
import com.liuwanwan.accountbook.model.RecordBean;
import com.liuwanwan.accountbook.utils.SlideLayout;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetItemAdapter extends RecyclerView.Adapter<AssetItemAdapter.ViewHolder> {
    private FragmentManager fragmentManager;
    String name;
    //double value[];
    List<AssetItem> assetItemList=new ArrayList<>();
    public AssetItemAdapter(FragmentManager fragmentManager,List<AssetItem> assetItemList){
        this.fragmentManager=fragmentManager;
        this.assetItemList=assetItemList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asset, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.tvAssetItemDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAssetItem();
            }
        });
        holder.tvAssetItemEdit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(view.getContext(),""+holder.tvAssetItemEdit.getText(),Toast.LENGTH_SHORT).show();

            }
        });
        return holder;
    }

    private void deleteAssetItem() {
        List<Asset> assetList = LitePal.findAll(Asset.class);
        for (Asset asset:assetList){
            if (asset.getName().equals(name)){
                LitePal.deleteAll(Asset.class, "name=?",name);
                EventBus.getDefault().post(new MessageEvent(10));
                return;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssetItem assetItem=assetItemList.get(position);
        name=assetItem.assetItemName;
        holder.tvAssetItemName.setText(assetItem.assetItemName);
        holder.tvAssetMoney.setText("¥"+assetItem.assetItemMoney);
        holder.tvAssetItemDel.setText("删除");
        holder.tvAssetItemEdit.setText("修改");
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (assetItemList!=null)
            return assetItemList.size();
        else
            return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView ivAssetType;
        TextView tvAssetItemName;
        TextView tvAssetMoney;
        TextView tvAssetItemDel;
        TextView tvAssetItemEdit;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvAssetItemName = (TextView) itemView.findViewById(R.id.tv_assetitemname);
            tvAssetMoney = (TextView) itemView.findViewById(R.id.tv_assetitemmoney);
            tvAssetItemDel = (TextView) itemView.findViewById(R.id.tv_slidedel);
            tvAssetItemEdit = (TextView) itemView.findViewById(R.id.tv_slideedit);
        }
    }
}
