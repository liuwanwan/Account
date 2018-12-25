package com.liuwanwan.accountbook.activity;
import android.support.v7.app.*;
import android.os.*;
import com.liuwanwan.accountbook.R;
import android.widget.*;
import android.view.*;
import java.text.*;
import com.liuwanwan.accountbook.utils.*;
import org.greenrobot.eventbus.*;
import com.liuwanwan.accountbook.model.*;
import com.liuwanwan.accountbook.*;
import java.util.*;
import com.liuwanwan.accountbook.db.*;
import org.litepal.*;
public class TransferAssetActivity extends AppCompatActivity implements View.OnClickListener
{
	private RelativeLayout layoutOut,layoutIn;
	private TextView tvResMoney,tvOut,tvIn;
	private EditText etTransferMoney;
	private Button btOk,btHistory;
	private boolean outChoose=false,inChoose=false;
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
		initView();
		initListener();
		EventBus.getDefault().register(this);
	}

	private void initView()
	{
		layoutOut = (RelativeLayout)findViewById(R.id.layout_out);
		layoutIn = (RelativeLayout)findViewById(R.id.layout_in);
		tvOut = (TextView)findViewById(R.id.tv_out);
		tvIn = (TextView)findViewById(R.id.tv_in);
		tvResMoney = (TextView)findViewById(R.id.tv_re);
		etTransferMoney = (EditText)findViewById(R.id.et_transfermoney);
		btHistory = (Button)findViewById(R.id.bt_history);
		btOk = (Button)findViewById(R.id.bt_ok);
	}

	private void initListener()
	{
		layoutOut.setOnClickListener(this);
		layoutIn.setOnClickListener(this);
		btHistory.setOnClickListener(this);
		btOk.setOnClickListener(this);
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.layout_out:
				ChooseAccountDialogFragment chooseOutAccountDialogFragment = ChooseAccountDialogFragment.newInstance(MyApplication.CHOOSE_ASSET_OUT);
				//bottomDialogFragment.setTargetFragment(getSupportFragment(), 0);
				//bottomDialogFragment.show(getChildFragmentManager(),"ShowDetaiRecord");//报错
				chooseOutAccountDialogFragment.show(getSupportFragmentManager(), "ChooseAccount");
				outChoose = true;
				break;
			case R.id.layout_in:
				ChooseAccountDialogFragment chooseInAccountDialogFragment = ChooseAccountDialogFragment.newInstance(MyApplication.CHOOSE_ASSET_IN);
				//bottomDialogFragment.setTargetFragment(getSupportFragment(), 0);
				//bottomDialogFragment.show(getChildFragmentManager(),"ShowDetaiRecord");//报错
				chooseInAccountDialogFragment.show(getSupportFragmentManager(), "ChooseAccount");
				inChoose = true;
				break;
			case R.id.bt_history:
				break;
			case R.id.bt_ok:
				transferMoney();
				break;
		}
	}

	private void transferMoney()
	{
		String transferMoneyString=etTransferMoney.getText().toString();
		double transferMoney=0;
		if (transferMoneyString != null && transferMoneyString.length() > 0)
		{
			transferMoney = Double.parseDouble(transferMoneyString);
			if (transferMoney > 0 && outChoose && inChoose)
			{
				String outAsset=tvOut.getText().toString();
				String inAsset=tvIn.getText().toString();
				List<Asset> assetList1 = LitePal.where("name=?", outAsset).find(Asset.class);
				boolean go=true;
				if (assetList1 != null && assetList1.size() > 0)
				{
					Asset asset1=assetList1.get(0);
					if (asset1.getMoney() > transferMoney)
					{
						asset1.setMoney(asset1.getMoney() - transferMoney);
						asset1.updateAll("name=?", outAsset);}
					else
				  	{
						go = false;
						Toast.makeText(TransferAssetActivity.this, "转出金额必须小于转出账户金额！", Toast.LENGTH_SHORT).show();
					}
				}
				List<Asset> assetList2 = LitePal.where("name=?", inAsset).find(Asset.class);
				if (go && assetList2 != null && assetList2.size() > 0)
				{
					Asset asset=assetList2.get(0);
					asset.setMoney(asset.getMoney() + transferMoney);
					asset.updateAll("name=?", inAsset);
				}
				EventBus.getDefault().post(new MessageEvent(MyApplication.TRANSFER_ASSET));
				finish();
			}
			else
				Toast.makeText(TransferAssetActivity.this, "转出错误！", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(TransferAssetActivity.this, "转出金额错误！", Toast.LENGTH_SHORT).show();

	}
	@Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent1(MessageEvent messageEvent)
	{
        switch (messageEvent.getMessage())
		{
            case MyApplication.CHOOSE_ASSET_OUT:
                tvOut.setText(MyApplication.chooseAssetName);
				tvResMoney.setText("(余额￥" + MyApplication.chooseAssetMoneyString + ")");
                break;
			case MyApplication.CHOOSE_ASSET_IN:
                tvIn.setText(MyApplication.chooseAssetName);
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
