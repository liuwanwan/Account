package com.liuwanwan.accountbook.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.model.IconBean;

import java.util.List;

public class IconAdapter extends BaseAdapter {
    private List<IconBean> mData;

    public IconAdapter(List<IconBean> data) {
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public IconBean getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_icon, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        IconBean bean = getItem(position);
        holder.iv.setImageResource(bean.resId);
        holder.tv.setText(bean.type);
        if (bean.selected) {
            holder.iv.setBackgroundResource(R.drawable.shape_quan_pink);
            //convertView.setBackgroundResource(R.drawable.shape_quan_pink);
        } else {
            holder.iv.setBackgroundResource(0);
            //convertView.setBackgroundResource(0);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView iv;
        TextView tv;
    }

}
