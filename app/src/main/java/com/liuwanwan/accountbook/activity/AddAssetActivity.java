package com.liuwanwan.accountbook.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liuwanwan.accountbook.MyApplication;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.db.Asset;
import com.liuwanwan.accountbook.model.MessageEvent;
import com.liuwanwan.accountbook.utils.MutiRadioGroup;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.List;

public class AddAssetActivity extends AppCompatActivity {
    private Button btOK;
    private EditText etNewAssetName, etNewAssetMoney;
    private String editAssetItemName = "";
    private int flag = 0, newAssetType = 0;
    private MutiRadioGroup mutiRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);
        Bundle bundle = getIntent().getExtras();
        flag = bundle.getInt("AddAsset", 0);//可能出错
        editAssetItemName = bundle.getString("AssetItemName");
        btOK = (Button) findViewById(R.id.bt_ok);
        mutiRadioGroup = (MutiRadioGroup) findViewById(R.id.rg_assettype);
        etNewAssetName = (EditText) findViewById(R.id.et_newassetname);
        etNewAssetMoney = (EditText) findViewById(R.id.et_newassetmoney);
        if (flag == 1) {//编辑AssetItem
            List<Asset> assetList = LitePal.where("name=?", editAssetItemName).find(Asset.class);
            Asset asset = assetList.get(0);
            etNewAssetName.setText(asset.getName());
            etNewAssetMoney.setText(asset.getMoney() + "");
            int editAssetItemId = 0;
            switch (asset.getType()) {
                case 0:
                    editAssetItemId = R.id.rb_cash;
                    break;
                case 1:
                    editAssetItemId = R.id.rb_deposit;
                    break;
                case 2:
                    editAssetItemId = R.id.rb_stored;
                    break;
                case 3:
                    editAssetItemId = R.id.rb_ecard;
                    break;
                case 4:
                    editAssetItemId = R.id.rb_invest;
                    break;
                case 5:
                    editAssetItemId = R.id.rb_financial;
                    break;
            }
            mutiRadioGroup.setCheckWithoutNotif(editAssetItemId);
        }
        mutiRadioGroup.setOnCheckedChangeListener(new MutiRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MutiRadioGroup group, int checkedId) {
                if (flag == 0)//修改模式下不可改变账户大类
                    switch (checkedId) {
                        case R.id.rb_cash:
                            newAssetType = 0;
                            break;
                        case R.id.rb_deposit:
                            newAssetType = 1;
                            break;
                        case R.id.rb_stored:
                            newAssetType = 2;
                            break;
                        case R.id.rb_ecard:
                            newAssetType = 3;
                            break;
                        case R.id.rb_invest:
                            newAssetType = 4;
                            break;
                        case R.id.rb_financial:
                            newAssetType = 5;
                            break;
                    }
            }
        });
        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    Asset asset = new Asset();
                    String s = etNewAssetMoney.getText().toString();
                    asset.setMoney(Double.parseDouble(s));
                    if (flag == 0) {
                        asset.setType(newAssetType);
                        asset.setName(etNewAssetName.getText().toString());
                        asset.save();
                        EventBus.getDefault().post(new MessageEvent(MyApplication.ADD_ASSET));
                    } else {
                        asset.updateAll("name=?", editAssetItemName);
                        EventBus.getDefault().post(new MessageEvent(MyApplication.DEL_EDIT_ASSET));
                    }
                    finish();
                } else {
                    Toast.makeText(AddAssetActivity.this, "输入有误！请重新输入！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean checkInput() {
        String name = etNewAssetName.getText().toString();
        List<Asset> assetList = LitePal.where("name=?", name).find(Asset.class);
        int len = assetList.size();//len==1表示只有一个
        String tempMoney = etNewAssetMoney.getText().toString();
        if (len == flag && name != null && name.length() > 0 && tempMoney != null && newAssetType >= 0 && newAssetType <= 5)
            return true;
        else
            return false;
    }

}

