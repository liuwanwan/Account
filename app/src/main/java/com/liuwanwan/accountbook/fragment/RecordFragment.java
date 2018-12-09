package com.liuwanwan.accountbook.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwanwan.accountbook.MyApplication;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.activity.BudgetActivity;
import com.liuwanwan.accountbook.activity.WriteActivity;
import com.liuwanwan.accountbook.db.Record;
import com.liuwanwan.accountbook.model.MessageEvent;
import com.liuwanwan.accountbook.model.RecordBean;
import com.liuwanwan.accountbook.recyclerviewadapter.SectionParameters;
import com.liuwanwan.accountbook.recyclerviewadapter.SectionedRecyclerViewAdapter;
import com.liuwanwan.accountbook.recyclerviewadapter.StatelessSection;
import com.liuwanwan.accountbook.utils.BottomDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RecordFragment extends Fragment {
    private View mRootView;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private FloatingActionButton fbWrite;
    private RecyclerView recyclerView;
    private TextView tvCurrentMonth;
    private TextView tvCurrentMonthIn;
    private TextView tvCurrentMonthOut;
    private TextView tvCurrentMonthNet;
    private Button btBudget;
    private boolean refresh = false;
    double monthIncome = 0;
    double monthExpense = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = View.inflate(getContext(), R.layout.fragment_record, null);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //EventBus.getDefault().register(this);
        tvCurrentMonth = (TextView) view.findViewById(R.id.tvCurrentMonth);
        tvCurrentMonthIn = (TextView) view.findViewById(R.id.tvCurrentIn);
        tvCurrentMonthOut = (TextView) view.findViewById(R.id.tvCurrentOut);
        tvCurrentMonthNet = (TextView) view.findViewById(R.id.tvCurrentNet);
        btBudget = (Button) view.findViewById(R.id.bt_budget);
        fbWrite = (FloatingActionButton) view.findViewById(R.id.fb_write);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_record);
        fbWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WriteActivity.class);
                startActivity(intent);
            }
        });
        btBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置预算
                Toast.makeText(getActivity(), "设置预算", Toast.LENGTH_SHORT).show();
                Intent BudgetActivityIntent = new Intent(getContext(), BudgetActivity.class);
                startActivityForResult(BudgetActivityIntent, 1);
            }
        });
        updateView();
        runLayoutAnimation();
        EventBus.getDefault().register(this);
    }

    private void updateView() {
        monthIncome = 0;
        monthExpense = 0;
        Calendar c = Calendar.getInstance();
        String condition = c.get(Calendar.YEAR) + "" + (c.get(Calendar.MONTH) + 1);
        tvCurrentMonth.setText((c.get(Calendar.MONTH) + 1) + "");
        List<Record> recordList = LitePal.order("recordedTime desc").find(Record.class);
        for (Record record : recordList) {
            if (condition.equals(record.getRecordedYearMonth()))
                if (record.isIncome()) {
                    monthIncome += record.getMoney();
                } else {
                    monthExpense += record.getMoney();
                }
        }
        tvCurrentMonthIn.setText("￥" + monthIncome);
        tvCurrentMonthIn.setTextColor(Color.RED);
        tvCurrentMonthOut.setText("￥" + monthExpense);
        tvCurrentMonthOut.setTextColor(Color.BLUE);
        double net = monthIncome - monthExpense;
        tvCurrentMonthNet.setText("￥" + net);
        if (net >= 0)
            tvCurrentMonthNet.setTextColor(Color.RED);
        else
            tvCurrentMonthNet.setTextColor(Color.BLUE);
        List<String> recordedDates = new ArrayList<>();
        for (Record r : recordList) {
            recordedDates.add(r.getRecordedTime());
        }
        Set<String> set = new LinkedHashSet<>();
        set.addAll(recordedDates);
        recordedDates.clear();
        recordedDates.addAll(set);//去重
        sectionAdapter = new SectionedRecyclerViewAdapter();
        for (String s : recordedDates) {
            double dayIncome = 0;
            double dayExpense = 0;
            List<RecordBean> recordBeanList = new ArrayList<>();
            if (s != null) {
                for (Record record : recordList) {
                    if (s.equals(record.getRecordedTime())) {
                        recordBeanList.add(new RecordBean(record.isIncome(), record.getInOrOutType(), record.getMoney(), record.getRecordTime(), record.getNote()));
                        if (record.isIncome()) {
                            dayIncome += record.getMoney();
                        } else {
                            dayExpense += record.getMoney();
                        }
                    }
                }
            }
            String tempMonth = s.substring(4, 6);
            String tempDay = s.substring(6, 8);

            ExpandableContactsSection expandableContactsSection = new ExpandableContactsSection(tempMonth + "月" + tempDay + "日", dayIncome, dayExpense, recordBeanList);
            sectionAdapter.addSection(expandableContactsSection);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);
        EventBus.getDefault().post(recordList);
    }

    private void runLayoutAnimation() {
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_recyclerview_animation_from_right);

        recyclerView.setLayoutAnimation(controller);
        sectionAdapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        refresh = messageEvent.getMessage();
        updateView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void onResume() {
        super.onResume();
        updateView();
    }

    /* public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         switch (requestCode) {
             case 1:
                 if (resultCode == Activity.RESULT_OK && data != null) {//获取从DialogFragmentB中传递的mB2A
                     Bundle bundle = data.getExtras();
                     if (bundle != null) {
                         Object object = bundle.get("OK");
                         if (object instanceof Integer) {
                             //mB2A = (Integer) object;
                             updateView();
                         }
                     }
                 }
                 break;
             default:
                 break;
         }
     }*/
    public class ExpandableContactsSection extends StatelessSection {
        String title;
        double income;
        double expense;
        List<RecordBean> list;
        boolean expanded = true;

        public ExpandableContactsSection(String title, double income, double expense, List<RecordBean> list) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.item_record)
                    .headerResourceId(R.layout.item_header)
                    .build());
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
            BottomDialogFragment bottomDialogFragment = BottomDialogFragment.newInstance(time,1);
            bottomDialogFragment.setTargetFragment(RecordFragment.this, 0);
            //bottomDialogFragment.show(getChildFragmentManager(),"ShowDetaiRecord");//报错
            bottomDialogFragment.show(getFragmentManager(), "ShowDetaiRecord");
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
                    sectionAdapter.notifyDataSetChanged();
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

}