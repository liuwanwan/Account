package com.liuwanwan.accountbook.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.model.AssetItem;
import com.liuwanwan.accountbook.utils.SlideLayout;

import java.util.List;

public class AssetItemAdapter extends RecyclerView.Adapter<AssetItemAdapter.ViewHolder> implements View.OnClickListener {
    List<AssetItem> assetItemList;
    public AssetItemAdapter(List<AssetItem> assetItemList){
        this.assetItemList=assetItemList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asset, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvAssetItemDel.setTag(position);
        holder.tvAssetItemName.setTag(position);
        holder.tvAssetMoney.setTag(position);
        holder.tvAssetItemEdit.setTag(position);

        AssetItem assetItem=assetItemList.get(position);
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
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    /** item里面有多个控件可以点击 */
    public enum ViewName {
        DEL_BUTTON,
        EDIT_BUTTON
    }
    public interface OnRecyclerViewItemClickListener {
        void onClick(View view, ViewName viewName, int position);
    }
    @Override
    public void onClick(View v) {
        //注意这里使用getTag方法获取数据
        int position = (int) v.getTag();
            switch (v.getId()){
                case R.id.tv_slidedel:
                    mOnItemClickListener.onClick(v, ViewName.DEL_BUTTON, position);
                    break;
                case R.id.tv_slideedit:
                    mOnItemClickListener.onClick(v, ViewName.EDIT_BUTTON, position);
                    break;
            }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        SlideLayout slideLayout;
        ImageView ivAssetType;
        TextView tvAssetItemName;
        TextView tvAssetMoney;
        TextView tvAssetItemDel;
        TextView tvAssetItemEdit;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            slideLayout=(SlideLayout)itemView.findViewById(R.id.rootView);
            tvAssetItemName = (TextView) itemView.findViewById(R.id.tv_assetitemname);
            tvAssetMoney = (TextView) itemView.findViewById(R.id.tv_assetitemmoney);
            tvAssetItemDel = (TextView) itemView.findViewById(R.id.tv_slidedel);
            tvAssetItemEdit = (TextView) itemView.findViewById(R.id.tv_slideedit);
            tvAssetItemDel.setOnClickListener(AssetItemAdapter.this);
            tvAssetItemEdit.setOnClickListener(AssetItemAdapter.this);
        }
    }

}
