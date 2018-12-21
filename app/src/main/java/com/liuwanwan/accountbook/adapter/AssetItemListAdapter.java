package com.liuwanwan.accountbook.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwanwan.accountbook.MyApplication;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.model.AssetItem;
import com.liuwanwan.accountbook.model.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AssetItemListAdapter extends RecyclerView.Adapter<AssetItemListAdapter.ViewHolder> {
    private List<AssetItem> assetItemList;
    private int mposition = -1;

    public AssetItemListAdapter(List<AssetItem> assetItemList) {
        this.assetItemList = assetItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asset_list, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.chooseAssetName=holder.tvAssetItemName.getText().toString();
                //将点击的位置传出去
                mposition = holder.getAdapterPosition();
                //在点击监听里最好写入setVisibility(View.VISIBLE);这样可以避免效果会闪
                holder.ivChoose.setVisibility(View.VISIBLE);
                //刷新界面 notify 通知Data 数据set设置Changed变化
                //在这里运行notifyDataSetChanged 会导致下面的onBindViewHolder 重新加载一遍
                notifyDataSetChanged();
                EventBus.getDefault().post(new MessageEvent(MyApplication.CHOOSE_ASSET));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssetItem assetItem = assetItemList.get(position);
        holder.tvAssetItemName.setText(assetItem.assetItemName);
        holder.tvAssetMoney.setText("¥" + assetItem.assetItemMoney);
         /*
        onBindViewHolder 方法可能是在class里for添加了其他视图
        引入mposition与当前的position判断，判断在点击的位置上显示打勾图片，在其他位置上不显示打勾
         */
        if (position == mposition) {
            holder.ivChoose.setVisibility(View.VISIBLE);
        } else {
            holder.ivChoose.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (assetItemList != null)
            return assetItemList.size();
        else
            return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView ivAssetType;
        TextView tvAssetItemName;
        TextView tvAssetMoney;
        ImageView ivChoose;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvAssetItemName = (TextView) itemView.findViewById(R.id.tv_assetitemname);
            tvAssetMoney = (TextView) itemView.findViewById(R.id.tv_assetitemmoney);
            ivChoose = (ImageView) itemView.findViewById(R.id.iv_assetitemchoose);
        }
    }

}
