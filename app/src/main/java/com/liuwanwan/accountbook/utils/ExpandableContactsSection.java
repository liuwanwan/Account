package com.liuwanwan.accountbook.utils;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuwanwan.accountbook.MyApplication;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.model.MessageEvent;
import com.liuwanwan.accountbook.model.RecordBean;
import com.liuwanwan.accountbook.recyclerviewadapter.SectionParameters;
import com.liuwanwan.accountbook.recyclerviewadapter.StatelessSection;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ExpandableContactsSection extends StatelessSection {
    String title;
    double income;
    double expense;
    List<RecordBean> list;
    boolean expanded = true;
    Fragment fragment;

    public ExpandableContactsSection(Fragment fragment, String title, double income, double expense, List<RecordBean> list) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_record)
                .headerResourceId(R.layout.item_header)
                .build());
        this.fragment = fragment;
        this.title = title;
        this.income = income;
        this.expense = expense;
        this.list = list;
    }

    @Override
    public int getContentItemsTotal() {
        return expanded ? list.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;
        final RecordBean recordBean = list.get(position);
        int type = recordBean.type;
        String typeName;
        int typeId;
        if (recordBean.isIncome) {
            typeName = MyApplication.incomeTypes[type];
            typeId = MyApplication.incomeIds[type];
            itemHolder.tvMoney.setText("+" + recordBean.money);
            itemHolder.tvMoney.setTextColor(Color.RED);
        } else {
            typeName = MyApplication.expenseTypes[type];
            typeId = MyApplication.expenseIds[type];
            itemHolder.tvMoney.setText("-" + recordBean.money);
            itemHolder.tvMoney.setTextColor(Color.BLUE);
        }
        itemHolder.tvType.setText(typeName);
        itemHolder.tvNote.setText(recordBean.note);
        itemHolder.imgItem.setImageResource(typeId);
        itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDetail(recordBean.recordTime);
            }
        });
            /*itemHolder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long time = recordBean.recordTime;

                    operateRecord(time, position);
                    return true;
                }
            });*/
    }

    private void recordDetail(long time) {
        BottomDialogFragment bottomDialogFragment = BottomDialogFragment.newInstance(time, 1);
        bottomDialogFragment.setTargetFragment(fragment, 0);
        //bottomDialogFragment.show(getChildFragmentManager(),"ShowDetaiRecord");//报错
        bottomDialogFragment.show(fragment.getFragmentManager(), "ShowDetaiRecord");
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.tvTitle.setText(title);
        headerHolder.tvNet.setText("收" + income + " 支" + expense + " 余" + (income - expense));
        headerHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded = !expanded;
                headerHolder.imgArrow.setImageResource(
                        expanded ? R.drawable.ic_keyboard_arrow_up_black_18dp : R.drawable.ic_keyboard_arrow_down_black_18dp
                );
                // 发布事件
                EventBus.getDefault().post(new MessageEvent(MyApplication.FLUSH_RECORD));
            }
        });
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final TextView tvTitle;
        private final TextView tvNet;
        private final ImageView imgArrow;

        HeaderViewHolder(View view) {
            super(view);

            rootView = view;
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvNet = (TextView) view.findViewById(R.id.tvNet);
            imgArrow = (ImageView) view.findViewById(R.id.imgArrow);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final ImageView imgItem;
        private final TextView tvType;
        private final TextView tvNote;
        private final TextView tvMoney;

        ItemViewHolder(View view) {
            super(view);

            rootView = view;
            imgItem = (ImageView) view.findViewById(R.id.imgItem);
            tvType = (TextView) view.findViewById(R.id.tvType);
            tvNote = (TextView) view.findViewById(R.id.tvNote);
            tvMoney = (TextView) view.findViewById(R.id.tvMoney);
        }
    }
}

