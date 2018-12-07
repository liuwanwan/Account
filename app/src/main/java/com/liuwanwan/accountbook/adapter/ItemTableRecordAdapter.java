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
import com.liuwanwan.accountbook.model.RecordBean;

import java.math.BigDecimal;
import java.util.List;

public class ItemTableRecordAdapter extends RecyclerView.Adapter<ItemTableRecordAdapter.ViewHolder> {
   private List<RecordBean>  list;
   public ItemTableRecordAdapter(List<RecordBean> list){
       this.list=list;
   }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_table, parent,false);
            final ViewHolder holder = new ViewHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=holder.getAdapterPosition();
                    RecordBean recordBean=list.get(position);
                    Toast.makeText(v.getContext(),recordBean.type+"+"+recordBean.money,Toast.LENGTH_SHORT).show();
                }
            });
            return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecordBean recordBean = list.get(position);
        if (recordBean.isIncome) {
            holder.imagetype.setImageResource(MyApplication.incomeIds[recordBean.type]);
            holder.tvType.setText(MyApplication.incomeTypes[recordBean.type]);
        }
        else {
            holder.imagetype.setImageResource(MyApplication.expenseIds[recordBean.type]);
            holder.tvType.setText(MyApplication.expenseTypes[recordBean.type]);
        }
        holder.tvMoney.setText(recordBean.money+"");
        double sum=0;
        for (RecordBean bean:list){
            sum+=bean.money;
        }//四舍五入保留1位小数，0.5进一，小于0.5不进一
        holder.tvRate.setText(new BigDecimal(recordBean.money / sum * 100).setScale(1, BigDecimal.ROUND_HALF_UP) + "%");
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
       View itemView;
        ImageView imagetype;
        TextView tvType;
        TextView tvRate;
        TextView tvMoney;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            imagetype = (ImageView) itemView.findViewById(R.id.imgItem);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            tvRate = (TextView) itemView.findViewById(R.id.tvRate);
            tvMoney = (TextView) itemView.findViewById(R.id.tvMoney);
        }
    }
}
