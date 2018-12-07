package com.liuwanwan.accountbook.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwanwan.accountbook.MyApplication;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.adapter.ItemTableRecordAdapter;
import com.liuwanwan.accountbook.db.Record;
import com.liuwanwan.accountbook.model.RecordBean;
import com.liuwanwan.accountbook.utils.ColumnChartView;
import com.liuwanwan.accountbook.utils.PieChartView;
import com.liuwanwan.accountbook.utils.ScrollDateView;
import com.liuwanwan.accountbook.utils.YearPickerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableFragment extends Fragment implements View.OnClickListener, YearPickerView.OnSelectListener, CompoundButton.OnCheckedChangeListener, ScrollDateView.OnWheelItemSelectedListener
{
    private View mRootView;
    private TextView mTvExpense,mTvExpenseTitle,mTvNet,mTvNetTitle,mTvIncome,mTvIncomeTitle;
	private int clickIndex=0;
	private double mIncome = 0, mExpense = 0, mNet = 0;
    private HashMap<String, Double> mExHm;
    private HashMap<String, Double> mInHm;
    private PieChartView pieChartView;
	private ColumnChartView columnChartView;
    private ScrollDateView scrollDateView;
    private YearPickerView numberPicker;
    private CheckBox checkBox;
    private int currentYear;
    private String currentMonth;
    private RecyclerView recordRecyclerView;
    private ItemTableRecordAdapter itemTableRecordAdapter;
	private LinearLayout llayoutExpense,llayoutNet,llayoutIncome;
	private FrameLayout chartLayout;
	private boolean changeChart=true;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
        if (mRootView == null)
		{
            mRootView = View.inflate(getContext(), R.layout.fragment_table, null);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
        super.onViewCreated(view, savedInstanceState);
        initView(view);
		initListener();
        showChart();
		EventBus.getDefault().register(this);
    }

	private void initListener()
	{
		llayoutExpense.setOnClickListener(this);
		//llayoutNet.setOnClickListener(this);
		llayoutIncome.setOnClickListener(this);
		checkBox.setOnCheckedChangeListener(this);
        scrollDateView.setOnWheelItemSelectedListener(this);
        numberPicker.setOnSelectListener(this); //值变化监听事件
        chartLayout.setOnClickListener(this);
	}

    private void initView(View view)
	{
        numberPicker = (YearPickerView) view.findViewById(R.id.pick_year);
        scrollDateView = (ScrollDateView) view.findViewById(R.id.scroll_date);
        pieChartView = (PieChartView) view.findViewById(R.id.pie_chart);
		columnChartView = (ColumnChartView) view.findViewById(R.id.column_chart);
        checkBox = (CheckBox) view.findViewById(R.id.cb_only_year);

		llayoutExpense = (LinearLayout)view.findViewById(R.id.llayout_expense);
		mTvExpense = (TextView) view.findViewById(R.id.tv_expense);
		mTvExpenseTitle = (TextView) view.findViewById(R.id.tv_expense_title);
		mTvExpenseTitle.setTextColor(Color.RED);
		mTvExpense.setTextColor(Color.RED);
		
		llayoutNet = (LinearLayout)view.findViewById(R.id.llayout_net);
		mTvNet = (TextView) view.findViewById(R.id.tv_net);
		mTvNetTitle = (TextView) view.findViewById(R.id.tv_net_title);

		llayoutIncome = (LinearLayout)view.findViewById(R.id.llayout_income);
		mTvIncome = (TextView) view.findViewById(R.id.tv_income);
		mTvIncomeTitle = (TextView) view.findViewById(R.id.tv_income_title);

		recordRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_item_record);

		chartLayout=(FrameLayout) view.findViewById(R.id.chartLayout);
        List<String> monthItmes = new ArrayList<>();
        for (int i = 1; i <= 12; i++)
		{
            monthItmes.add(i + "月");
        }
        scrollDateView.setItems(monthItmes);

        ArrayList<String> yearList = new ArrayList<>();
        for (int i = 2017; i < 2100; i++)
            yearList.add(i + "");
        numberPicker.setData(yearList);
        numberPicker.setDefault(1);
        currentYear = 2018;

        Calendar c = Calendar.getInstance();
        int m = c.get(Calendar.MONTH);
		scrollDateView.smoothSelectIndex(m - 3);
        currentMonth = m + 1 + "";
        if (m + 1 < 10)
            currentMonth = "0" + (m + 1);
    }

    private void showChart()
	{
		LinkedHashMap map = new LinkedHashMap<String, Float>();
		map = getChartData();
        if (changeChart)
		{
            pieChartView.setVisibility(View.VISIBLE);
            columnChartView.setVisibility(View.INVISIBLE);
            updatePieChart(map);
        }else
		{
            pieChartView.setVisibility(View.INVISIBLE);
            columnChartView.setVisibility(View.VISIBLE);
			updateColumnChart(map);
		}
		mTvExpense.setText("¥" + mExpense);
        mTvNet.setText("¥" + mNet);
		mTvIncome.setText("¥" + mIncome);
        runLayoutAnimation();
    }

    private void runLayoutAnimation()
	{
        final LayoutAnimationController controller =
			AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_recyclerview_animation_from_right);

        recordRecyclerView.setLayoutAnimation(controller);
        itemTableRecordAdapter.notifyDataSetChanged();
        recordRecyclerView.scheduleLayoutAnimation();
    }

    private void updatePieChart(LinkedHashMap<String, Float> map)
	{
        pieChartView.setDataMap(map);
        pieChartView.startDraw();
    }
	private void updateColumnChart(LinkedHashMap<String, Float> map)
	{
        columnChartView.setDataMap(map);
		columnChartView.startDraw();
    }
    private LinkedHashMap<String, Float> getChartData()
	{
        List<Record> recordList = LitePal.order("recordedTime desc").find(Record.class);
        List<Record> recordAccountList = new ArrayList<>();
        if (scrollDateView.getVisibility() == View.VISIBLE)//统计选择的月
        {
            for (Record record : recordList)
			{//只统计选择的年月，默认为当前月
                if (record.getRecordedTime().substring(0, 6).equals(currentYear + currentMonth))
				{
                    recordAccountList.add(record);
                }
            }
        }
		else
		{//统计选择的年
            for (Record record : recordList)
			{//只统计选择的年，默认为当前年
                if (record.getRecordedTime().substring(0, 4).equals(currentYear + ""))
				{
                    recordAccountList.add(record);
                }
            }
        }
        mExpense = 0;
        mIncome = 0;
        mExHm = new HashMap<>();
        mInHm = new HashMap<>();
        for (Record record : recordAccountList)
		{
            if (!record.isIncome())
			{
                String key = MyApplication.expenseTypes[record.getInOrOutType()];
                double value;
                if (mExHm.get(key) == null)
				{
                    value = record.getMoney();
                }
				else
				{
                    value = mExHm.get(key).doubleValue() + record.getMoney();
                }
                mExpense+= record.getMoney();
                mExHm.put(key, value);
            }
			else
			{
                String key = MyApplication.incomeTypes[record.getInOrOutType()];
                double value;
                if (mInHm.get(key) == null)
				{
                    value = record.getMoney();
                }
				else
				{
                    value = mInHm.get(key).doubleValue() + record.getMoney();
                }
                mIncome+= record.getMoney();
                mInHm.put(key, value);
            }
        }
        mNet = mIncome - mExpense;

		List<RecordBean> recordBeanList = new ArrayList<>();
        HashMap<String, Double> hs;
        int idLenth;
        boolean isIncome;
        if (clickIndex==0)//支出被点击
		{
            hs = mExHm;
            idLenth = MyApplication.expenseIds.length;
            isIncome = false;
        }
		else
		{
            hs = mInHm;
            idLenth = MyApplication.incomeIds.length;
            isIncome = true;
        }
        List<Map.Entry<String, Double>> listData = new ArrayList<>(hs.entrySet());
        Collections.sort(listData, new Comparator<Map.Entry<String, Double>>() {
				@Override
				public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)
				{
					if (o2.getValue() > o1.getValue())
						return 1;
					else
						return -1;
				}
			});//排序
        double sum = 0.0;
        int temp = listData.size();
        for (int i = 0; i < temp; i++)
		{
            sum += listData.get(i).getValue();
            for (int j = 0; j < idLenth; j++)
			{
                String s;
                if (isIncome)
				{
                    s = MyApplication.incomeTypes[j];
                }
				else
				{
                    s = MyApplication.expenseTypes[j];
                }
                if (listData.get(i).getKey().equals(s))
				{
                    recordBeanList.add(new RecordBean(isIncome, j, listData.get(i).getValue()));
                }
            }
        }
		itemTableRecordAdapter = new ItemTableRecordAdapter(recordBeanList);
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recordRecyclerView.setAdapter(itemTableRecordAdapter);

		LinkedHashMap kindsMap = new LinkedHashMap<String, Float>();
        for (int i = 0; i < temp; i++)
		{
            String type = listData.get(i).getKey();
            double data = listData.get(i).getValue();
            kindsMap.put(type, (float) data);
        }

		return kindsMap;
    }

    @Override
    public void onClick(View v)
	{
        switch (v.getId())
		{
            case R.id.chartLayout:
                changeChart=!changeChart;
                showChart();
                break;
			case R.id.llayout_expense:
				clickIndex=0;
				showChart();
				
				//mTvNetTitle.setTextColor(Color.GRAY);
				//mTvNet.setTextColor(Color.BLACK);
				mTvExpenseTitle.setTextColor(Color.RED);
				mTvExpense.setTextColor(Color.RED);
				mTvIncomeTitle.setTextColor(Color.GRAY);
				mTvIncome.setTextColor(Color.BLACK);
				break;
			case R.id.llayout_income:
				clickIndex=1;
				showChart();
				
				//mTvNetTitle.setTextColor(Color.GRAY);
				//mTvNet.setTextColor(Color.BLACK);
				mTvExpenseTitle.setTextColor(Color.GRAY);
				mTvExpense.setTextColor(Color.BLACK);
				mTvIncomeTitle.setTextColor(Color.RED);
				mTvIncome.setTextColor(Color.RED);
				break;
            case R.id.llayout_net:
                clickIndex=1;
                showChart();

                //mTvNetTitle.setTextColor(Color.GRAY);
                //mTvNet.setTextColor(Color.BLACK);
                mTvExpenseTitle.setTextColor(Color.GRAY);
                mTvExpense.setTextColor(Color.BLACK);
                mTvIncomeTitle.setTextColor(Color.RED);
                mTvIncome.setTextColor(Color.RED);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(List<Record> data)
	{
        showChart();
    }

    @Override
    public void onDestroyView()
	{
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void endSelect(int id, final String text)
	{
        if (!numberPicker.isScrolling())
            getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run()
					{
						currentYear = Integer.parseInt(numberPicker.getSelectedText());
						Toast.makeText(getContext(), currentYear + currentMonth, Toast.LENGTH_SHORT).show();
						showChart();
					}
				});
    }

    @Override
    public void selecting(int id, String text)
	{

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b)
	{
        if (checkBox.isChecked())
		{
            scrollDateView.setVisibility(View.INVISIBLE);
        }
		else
            scrollDateView.setVisibility(View.VISIBLE);
        showChart();
    }

    @Override
    public void onWheelItemChanged(ScrollDateView wheelView, int position)
	{

    }

    @Override
    public void onWheelItemSelected(ScrollDateView wheelView, int position)
	{
        currentMonth = wheelView.getItems().get(position);
        currentMonth = currentMonth.substring(0, currentMonth.lastIndexOf("月"));
        if (Integer.parseInt(currentMonth) < 10)
		{
            currentMonth = "0" + currentMonth;
        }
        Toast.makeText(getContext(), currentYear + currentMonth, Toast.LENGTH_SHORT).show();
        showChart();
    }

}
