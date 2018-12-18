package com.liuwanwan.accountbook.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.db.Asset;
import com.liuwanwan.accountbook.model.MessageEvent;

import org.greenrobot.eventbus.EventBus;

public class AddAssetActivity extends AppCompatActivity {
    private Button btOK;
    private EditText etNewAssetType, etNewAssetName, etNewAssetMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);
        btOK = (Button) findViewById(R.id.bt_ok);
        etNewAssetType = (EditText) findViewById(R.id.et_newassettype);
        etNewAssetName = (EditText) findViewById(R.id.et_newassetname);
        etNewAssetMoney = (EditText) findViewById(R.id.et_newassetmoney);

        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    Asset asset = new Asset();
                    String t = etNewAssetType.getText().toString();
                    asset.setType(Integer.parseInt(t));
                    asset.setName(etNewAssetName.getText().toString());
                    String s = etNewAssetMoney.getText().toString();
                    asset.setMoney(Double.parseDouble(s));
                    asset.save();
                    EventBus.getDefault().post(new MessageEvent(10));
                    finish();
                } else {
                    Toast.makeText(AddAssetActivity.this, "输入有误！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean checkInput() {
        String name = etNewAssetName.getText().toString();
        String tempMoney = etNewAssetMoney.getText().toString();
        String tempType = etNewAssetType.getText().toString();
        if (name != null && name.length() > 0 && tempMoney != null && Double.parseDouble(tempMoney) > 0 && tempType != null && Integer.parseInt(tempType) >= 0 && Integer.parseInt(tempType) <= 5)
            return true;
        else
            return false;
    }
}

