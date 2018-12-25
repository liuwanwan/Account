package com.liuwanwan.accountbook.utils;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
//mport android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuwanwan.accountbook.MyApplication;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.activity.AddAssetActivity;
import com.liuwanwan.accountbook.adapter.AssetItemAdapter;
import com.liuwanwan.accountbook.db.Asset;
import com.liuwanwan.accountbook.model.AssetItem;
import com.liuwanwan.accountbook.model.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import android.app.*;
import android.content.*;
import android.widget.*;
import com.liuwanwan.accountbook.db.*;
import com.liuwanwan.accountbook.activity.*;

public class AssetDialogFragment extends DialogFragment {
    private TextView tvAssetClassTitle, tvAssetMoney;
    private ImageView ivDetailType;
    private int assetClassIndex;
    private RecyclerView recyclerView;
    private AssetItemAdapter assetItemAdapter;
    private List<AssetItem> assetItemList = new ArrayList<>();
    private String[] assetClassName={"现金","银行卡","储值卡","电子钱包","投资","理财"};
    public static AssetDialogFragment newInstance(int index) {
        AssetDialogFragment assetDialogFragment = new AssetDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("ShowDetailAsset", index);
        assetDialogFragment.setArguments(bundle);
        return assetDialogFragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            assetClassIndex = getArguments().getInt("ShowDetailAsset");
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.BottomFragmentDialog);
        // 必须在setContentView之前调用。否则运行时报错。
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialogfragment__asset_detail, null);
        tvAssetClassTitle = (TextView) view.findViewById(R.id.tv_assetclasstitle);
        tvAssetMoney = (TextView) view.findViewById(R.id.tv_assetclassmoney);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_asset);

        initAsset();
        setDialog(dialog, view);
        return dialog;
    }

    private void initAsset() {
        tvAssetClassTitle.setText(assetClassName[assetClassIndex]);
        updateAssetClassMoney();
        assetItemAdapter = new AssetItemAdapter(assetItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(assetItemAdapter);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).build());
        assetItemAdapter.setOnItemClickListener(new AssetItemAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, AssetItemAdapter.ViewName viewName, int position) {
                if(view instanceof TextView){
					SlideLayout slideLayout=(SlideLayout) (view.getParent().getParent());
					TextView tvAssetItemName=(TextView) slideLayout.findViewById(R.id.tv_assetitemname);
					switch (viewName){
						case DEL_BUTTON:
							deleteAssetItem(tvAssetItemName.getText().toString());
							slideLayout.smoothCloseSlide();
							break;
						case EDIT_BUTTON:
							editAssetItem(tvAssetItemName.getText().toString());
							slideLayout.smoothCloseSlide();
							break;
					}
				}else{
					SlideLayout slideLayout=(SlideLayout) (view.getParent());
					TextView tvAssetItemName=(TextView) slideLayout.findViewById(R.id.tv_assetitemname);
					switch (viewName){
						case ITEM:
							//showAssetDetal(tvAssetItemName.getText().toString());
							Intent intent = new Intent(getContext(), AssetDetailActivity.class);
							intent.putExtra("assetName", tvAssetItemName.getText());
							startActivity(intent);
							slideLayout.smoothCloseSlide();
							break;
					}
				}
            }
        });
    }
	private void showAssetDetal(String accountName){
		List<Record> recordList=LitePal.where("account=?",accountName).find(Record.class);
		List<Asset> assetList=LitePal.where("name=?",accountName).find(Asset.class);
		Asset asset=assetList.get(0);
		
		String nextFuc="\t\t\t\t1.完善资产账户收支明细功能\n"+
			"\t\t\t\t2.完善数据同步功能\n"+
			"\t\t\t\t3.完善支出、收入流水和资产的月度、年度趋势图\n"+
			"\t\t\t\t4.完善预算设置";
		AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();//创建对话框
        dialog.setIcon(R.mipmap.ic_launcher);//设置对话框icon
        dialog.setTitle("下一步开发计划");//设置对话框标题
        dialog.setMessage(nextFuc);//设置文字显示内容
        //分别设置三个button
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();//关闭对话框
				}
			});
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL,"点我试试", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { }
			});
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();//关闭对话框
				}
			});
        dialog.show();//显示对话框
	}
    private void editAssetItem(String clickName) {
        List<Asset> assetList = LitePal.where("name=?",clickName).find(Asset.class);
        Asset asset=assetList.get(0);
        Intent intent = new Intent(getContext(), AddAssetActivity.class);
        intent.putExtra("AddAsset",1);
        intent.putExtra("AssetItemName",asset.getName());
        getContext().startActivity(intent);
    }
    private void deleteAssetItem(String clickName) {
        LitePal.deleteAll(Asset.class, "name=?",clickName);
        EventBus.getDefault().post(new MessageEvent(MyApplication.DEL_EDIT_ASSET));
    }
    private void updateAssetClassMoney() {
        List<Asset> assetList = LitePal.findAll(Asset.class);

        assetItemList.clear();
        for (Asset asset : assetList) {
            if (asset.getType() == assetClassIndex) {
                AssetItem assetItem = new AssetItem(assetClassIndex, asset.getName(), asset.getMoney());
                assetItemList.add(assetItem);
            }
        }
        double sum = 0;
        for (AssetItem assetItem : assetItemList) {
            sum += assetItem.assetItemMoney;
        }
        tvAssetMoney.setText("¥" + sum);

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent1(MessageEvent messageEvent) {
        switch (messageEvent.getMessage()) {
            case MyApplication.DEL_EDIT_ASSET:
                updateAssetClassMoney();
                assetItemAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
