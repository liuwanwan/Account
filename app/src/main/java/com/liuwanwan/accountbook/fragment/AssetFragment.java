package com.liuwanwan.accountbook.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuwanwan.accountbook.MyApplication;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.activity.AddAssetActivity;
import com.liuwanwan.accountbook.adapter.AssetAdapter;
import com.liuwanwan.accountbook.db.Asset;
import com.liuwanwan.accountbook.model.AssetItem;
import com.liuwanwan.accountbook.model.MessageEvent;
import com.liuwanwan.accountbook.utils.ColumnChartView;
import com.liuwanwan.accountbook.utils.PieChartView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import com.liuwanwan.accountbook.activity.*;

public class AssetFragment extends Fragment implements View.OnClickListener
{
    private View mRootView;
    private FrameLayout chartLayout;
    private ImageView ivSelectAsset, ivAssetVisable;
    private TextView tvAssetVisable, tvAssetTotal, tvAssetStatistic, tvAssetItem;
    private Button btTransfer, btAddAssetItem, btAssetStructure, btAssetTrend;
    private boolean assetVisable = true;
    private boolean assetTableType = true;
    private GridView gridView;
    private List<AssetItem> assetItemList = new ArrayList<>();
    private AssetAdapter adapter;
    private PieChartView pieChartView;
    private ColumnChartView columnChartView;
    private boolean changeChart = true;
    private static String name[] = {"现", "银", "储", "电", "投", "理"};
    private double money[] = {0, 0, 0, 0, 0, 0};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
        if (mRootView == null)
		{
            mRootView = View.inflate(getContext(), R.layout.fragment_asset, null);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
        updateView();
        EventBus.getDefault().register(this);
    }

    private void showChart()
	{
		if (assetVisable)
		{
			LinkedHashMap map = getChartData();
			if (assetTableType)
			{//资产结构
				if (changeChart)
				{
					pieChartView.setVisibility(View.VISIBLE);
					columnChartView.setVisibility(View.INVISIBLE);
					updatePieChart(map);
				}
				else
				{
					pieChartView.setVisibility(View.INVISIBLE);
					columnChartView.setVisibility(View.VISIBLE);
					updateColumnChart(map);
				}
			}
			else
			{//资产趋势
				pieChartView.setVisibility(View.INVISIBLE);
				columnChartView.setVisibility(View.INVISIBLE);
			}
		}
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
        List<Asset> assetList = LitePal.order("money desc").find(Asset.class);
        LinkedHashMap kindsMap = new LinkedHashMap<String, Float>();
        for (Asset asset : assetList)
		{
            kindsMap.put(asset.getName(), (float) asset.getMoney());
        }

        return kindsMap;
    }

    private void initListener()
	{
        ivSelectAsset.setOnClickListener(this);
        ivAssetVisable.setOnClickListener(this);
        btTransfer.setOnClickListener(this);
        btAssetStructure.setOnClickListener(this);
        btAssetTrend.setOnClickListener(this);
        btAddAssetItem.setOnClickListener(this);
        chartLayout.setOnClickListener(this);
    }

    private void initView(View view)
	{
        ivSelectAsset = (ImageView) view.findViewById(R.id.iv_selectasset);
        ivAssetVisable = (ImageView) view.findViewById(R.id.iv_invisible);
        tvAssetVisable = (TextView) view.findViewById(R.id.tv_assetvisable);
        tvAssetTotal = (TextView) view.findViewById(R.id.tv_assettotal);
        btTransfer = (Button) view.findViewById(R.id.bt_transfer);
        btAddAssetItem = (Button) view.findViewById(R.id.bt_addassetitem);
        btAssetStructure = (Button) view.findViewById(R.id.bt_assetstructure);
        btAssetTrend = (Button) view.findViewById(R.id.bt_assettrend);
        tvAssetStatistic = (TextView) view.findViewById(R.id.tv_assetstatistic);
        tvAssetItem = (TextView) view.findViewById(R.id.tv_assetitem);
        chartLayout = (FrameLayout) view.findViewById(R.id.chartLayout);
        pieChartView = (PieChartView) view.findViewById(R.id.pie_chart);
        columnChartView = (ColumnChartView) view.findViewById(R.id.column_chart);
        gridView = (GridView) view.findViewById(R.id.gridview_account);
        updateView();
        adapter = new AssetAdapter(getFragmentManager(), assetItemList);
        gridView.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent1(MessageEvent messageEvent)
	{
        switch (messageEvent.getMessage())
		{
            case MyApplication.DEL_EDIT_ASSET:
            case MyApplication.TRANSFER_ASSET:
            case MyApplication.ADD_ASSET:
                updateView();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onDestroyView()
	{
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void onResume()
	{
        super.onResume();
        updateView();
    }

    private void updateView()
	{
        List<Asset> assetList = LitePal.findAll(Asset.class);
        double total = 0;
        for (Asset asset : assetList)
		{
            total += asset.getMoney();
        }
        tvAssetTotal.setText("¥" + total);
        int Len = name.length;
        for (int i = 0; i < Len; i++)
            money[i] = 0;
        for (int i = 0; i < Len; i++)
		{
            for (Asset asset : assetList)
			{
                if (asset.getType() == i)
                    money[i] += asset.getMoney();
            }
        }
        assetItemList.clear();
        for (int j = 0; j < Len; j++)
		{
            AssetItem assetItem = new AssetItem(j, name[j], money[j]);
            assetItemList.add(assetItem);
        }
        showChart();
    }

    @Override
    public void onClick(View v)
	{
        switch (v.getId())
		{
            case R.id.iv_selectasset:
                break;
            case R.id.iv_invisible:
                assetVisable = !assetVisable;
                if (assetVisable)
				{//显示
                    tvAssetVisable.setVisibility(View.INVISIBLE);
                    tvAssetTotal.setVisibility(View.VISIBLE);
                    ivAssetVisable.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_invisibility));
                    tvAssetItem.setVisibility(View.INVISIBLE);
                    tvAssetStatistic.setVisibility(View.INVISIBLE);
                    pieChartView.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.VISIBLE);
                }
				else
				{
                    tvAssetVisable.setVisibility(View.VISIBLE);
                    tvAssetTotal.setVisibility(View.INVISIBLE);
                    ivAssetVisable.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_visibility));
                    tvAssetItem.setVisibility(View.VISIBLE);
                    tvAssetStatistic.setVisibility(View.VISIBLE);
                    tvAssetStatistic.setText("哈，我隐身了啦啦啦~");
                    pieChartView.setVisibility(View.INVISIBLE);
                    gridView.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.chartLayout:
                changeChart = !changeChart;
                break;
            case R.id.bt_transfer:
				Intent transferIntent = new Intent(getContext(), TransferAssetActivity.class);
                startActivity(transferIntent);
                break;
            case R.id.bt_addassetitem:
                Intent addIntent = new Intent(getContext(), AddAssetActivity.class);
                addIntent.putExtra("AddAsset", 0);
                addIntent.putExtra("AssetItemName", "");
                startActivity(addIntent);
                break;
            case R.id.bt_assetstructure:
                assetTableType = true;
                btAssetStructure.setTextColor(Color.RED);
                btAssetTrend.setTextColor(Color.BLACK);
                break;
            case R.id.bt_assettrend:
                assetTableType = false;
                btAssetStructure.setTextColor(Color.BLACK);
                btAssetTrend.setTextColor(Color.RED);
                break;
        }
        showChart();
    }
}
