package com.liuwanwan.accountbook.activity;
import android.os.*;
import android.widget.*;
import android.support.v7.app.*;
import android.support.design.*;
import android.support.v7.widget.*;
import com.liuwanwan.accountbook.utils.*;
import com.liuwanwan.accountbook.recyclerviewadapter.*;
import java.util.*;
import com.liuwanwan.accountbook.db.*;
import org.litepal.*;
import com.liuwanwan.accountbook.model.*;
import android.graphics.*;
import android.content.*;
import com.liuwanwan.accountbook.R;
import org.greenrobot.eventbus.*;
import com.liuwanwan.accountbook.*;

public class AssetDetailActivity extends AppCompatActivity
{
    private TextView tvNetAssetName,tvNetAssetMoney, tvNetOut,tvNetIn;
	private RecyclerView recyclerView;
	private SectionedRecyclerViewAdapter sectionAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_detail);
        Intent intent=getIntent();
		String assetName=intent.getStringExtra("assetName");
		initView(assetName);
		updateView(assetName);
		EventBus.getDefault().register(this);
    }

	private void initView(String assetName)
	{
		tvNetAssetMoney = (TextView)findViewById(R.id.tv_netassetmoney);
		tvNetAssetName = (TextView)findViewById(R.id.tv_netassetname);
		tvNetAssetName.setText(assetName);
		tvNetOut = (TextView)findViewById(R.id.tv_culasset_out);
		tvNetIn = (TextView)findViewById(R.id.tv_culasset_in);
		recyclerView = (RecyclerView)findViewById(R.id.recyclerview_record);
	}

	private void updateView(String assetName)
	{
		double netIncome = 0;
		double netExpense = 0;
		
        List<Record> recordList = LitePal.where("account=?", assetName).order("recordedTime desc").find(Record.class);
        for (Record record : recordList)
		{
            if (assetName.equals(record.getAccount()))
                if (record.isIncome())
				{
                    netIncome += record.getMoney();
                }
				else
				{
                    netExpense += record.getMoney();
                }
        }
        tvNetIn.setText("￥" + netIncome);
        tvNetIn.setTextColor(Color.RED);
        tvNetOut.setText("￥" + netExpense);
        tvNetOut.setTextColor(Color.BLUE);
        double net = netIncome - netExpense;
        tvNetAssetMoney.setText("￥" + net);
        if (net >= 0)
            tvNetAssetMoney.setTextColor(Color.RED);
        else
            tvNetAssetMoney.setTextColor(Color.BLUE);
        List<String> recordedYearMonth = new ArrayList<>();
        for (Record r : recordList)
		{
            recordedYearMonth.add(r.getRecordedYearMonth());
        }
        Set<String> set = new LinkedHashSet<>();
        set.addAll(recordedYearMonth);
        recordedYearMonth.clear();
        recordedYearMonth.addAll(set);//去重
        sectionAdapter = new SectionedRecyclerViewAdapter();
        for (String s : recordedYearMonth)
		{
            double monthIncome = 0;
            double monthExpense = 0;
            List<RecordBean> recordBeanList = new ArrayList<>();
            if (s != null)
			{
                for (Record record : recordList)
				{
                    if (s.equals(record.getRecordedYearMonth()))
					{
                        recordBeanList.add(new RecordBean(record.isIncome(), record.getInOrOutType(), record.getMoney(), record.getRecordTime(), record.getRecordedTime(), record.getNote()));
                        if (record.isIncome())
						{
                            monthIncome += record.getMoney();
                        }
						else
						{
                            monthExpense += record.getMoney();
                        }
                    }
                }
            }

            ExpandableContactsSection expandableContactsSection = new ExpandableContactsSection(1, s, 																		monthIncome, monthExpense, recordBeanList);
            sectionAdapter.addSection(expandableContactsSection);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sectionAdapter);
    }
	@Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent1(MessageEvent messageEvent)
	{
        switch (messageEvent.getMessage())
		{
            case MyApplication.FLUSH_RECORD:
                sectionAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onDestroy()
	{
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
