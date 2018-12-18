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
import android.widget.TextView;
import android.widget.Toast;

import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.activity.BudgetActivity;
import com.liuwanwan.accountbook.activity.AddRecordActivity;
import com.liuwanwan.accountbook.db.Record;
import com.liuwanwan.accountbook.model.MessageEvent;
import com.liuwanwan.accountbook.model.RecordBean;
import com.liuwanwan.accountbook.recyclerviewadapter.SectionedRecyclerViewAdapter;
import com.liuwanwan.accountbook.utils.ExpandableContactsSection;

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
                Intent intent = new Intent(getContext(), AddRecordActivity.class);
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

            ExpandableContactsSection expandableContactsSection = new ExpandableContactsSection(RecordFragment.this, tempMonth + "月" + tempDay + "日", dayIncome, dayExpense, recordBeanList);
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
    public void onMessageEvent1(MessageEvent messageEvent) {
        switch (messageEvent.getMessage()) {
            case 1:
                updateView();
                break;
            case 2:
                sectionAdapter.notifyDataSetChanged();
                break;
        }
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
}