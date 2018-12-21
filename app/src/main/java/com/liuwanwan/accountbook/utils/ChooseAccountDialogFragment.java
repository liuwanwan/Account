package com.liuwanwan.accountbook.utils;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwanwan.accountbook.MyApplication;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.activity.AddRecordActivity;
import com.liuwanwan.accountbook.adapter.AssetItemAdapter;
import com.liuwanwan.accountbook.adapter.AssetItemListAdapter;
import com.liuwanwan.accountbook.db.Asset;
import com.liuwanwan.accountbook.db.Record;
import com.liuwanwan.accountbook.model.AssetItem;
import com.liuwanwan.accountbook.model.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChooseAccountDialogFragment extends DialogFragment {
    private Button btOk, btEdit;
    private TextView tvDetailTitle,tvDetailType, tvDetailMoney, tvDetailTime, tvDetailAccount;
    private ImageView ivDetailType;
    private long recordTime = 0;
    private int fromflag = 0;
    private RecyclerView recyclerView;
    private AssetItemListAdapter assetItemListAdapter;

    public static ChooseAccountDialogFragment newInstance() {
        ChooseAccountDialogFragment bottomDialogFragment = new ChooseAccountDialogFragment();
        return bottomDialogFragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.BottomFragmentDialog);
        // 必须在setContentView之前调用。否则运行时报错。
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialogfragment__asset_choose, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_asset);
        initAssetList();
        setDialog(dialog, view);

        return dialog;
    }
    private void initAssetList() {
        List<Asset> assetList = LitePal.findAll(Asset.class);
        if (assetList.size()<1) {
            Toast.makeText(getContext(),"请先设置资产账户！",Toast.LENGTH_SHORT).show();
            return;
        }
        List<AssetItem> assetItemList = new ArrayList<>();
        for (Asset asset : assetList) {
                AssetItem assetItem = new AssetItem(asset.getType(), asset.getName(), asset.getMoney());
                assetItemList.add(assetItem);
        }
        assetItemListAdapter = new AssetItemListAdapter(assetItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(assetItemListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }
    private void setDialog(Dialog dialog, View view) {
        // 底部弹出的DialogFragment装载的View
        dialog.setContentView(view);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        // 设置底部弹出显示的DialogFragment窗口属性。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 1200; // 底部弹出的DialogFragment的高度，如果是MATCH_PARENT则铺满整个窗口
        window.setAttributes(params);
    }
}
