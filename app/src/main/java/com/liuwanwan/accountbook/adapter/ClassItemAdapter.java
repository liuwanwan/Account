package com.liuwanwan.accountbook.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.model.ClassItem;
import com.liuwanwan.accountbook.model.IconBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ClassItemAdapter extends BaseAdapter {
    private List<ClassItem> mData;

    public ClassItemAdapter(List<ClassItem> data) {
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public ClassItem getItem(int position) {
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
            convertView = View.inflate(parent.getContext(), R.layout.item_class, null);
            holder = new ViewHolder();
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tvWeek = (TextView) convertView.findViewById(R.id.tv_week);
            holder.tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ClassItem bean = getItem(position);
        holder.tvDate.setText(bean.date);
        String y=bean.date.substring(0,4);
        String m=bean.date.substring(4,6);
        String d=bean.date.substring(6,8);
        holder.tvWeek.setText(getWeekday(y+"-"+m+"-"+d));
        holder.tvMoney.setText(""+bean.money);

        return convertView;
    }
    //	实现给定某日期，判断是星期几
    public static String getWeekday(String date){//必须yyyy-MM-dd
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdw = new SimpleDateFormat("E");
        Date d = null;
        try {
            d = sd.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdw.format(d);
    }

    static class ViewHolder {
        TextView tvDate;
        TextView tvWeek;
        TextView tvMoney;
    }

}
