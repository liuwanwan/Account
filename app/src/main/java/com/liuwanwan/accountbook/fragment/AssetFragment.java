package com.liuwanwan.accountbook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.model.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AssetFragment extends Fragment implements View.OnClickListener {
    private View mRootView;
    private FrameLayout chartLayout;
    private ImageView ivSelectAsset, ivAssetVisable;
    private TextView tvAssetVisable,tvAsset, tvAssetNum;
    private Button btThansfer, btAddAsset;
    private boolean assetVisable = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = View.inflate(getContext(), R.layout.fragment_asset, null);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
        updateView();
        EventBus.getDefault().register(this);
    }

    private void initListener() {
        ivSelectAsset.setOnClickListener(this);
        ivAssetVisable.setOnClickListener(this);
        btThansfer.setOnClickListener(this);
        btAddAsset.setOnClickListener(this);
        chartLayout.setOnClickListener(this);
    }

    private void initView(View view) {
        ivSelectAsset = (ImageView) view.findViewById(R.id.iv_selectasset);
        ivAssetVisable = (ImageView) view.findViewById(R.id.iv_invisible);
        tvAssetVisable = (TextView) view.findViewById(R.id.tv_assetvisable);
        tvAsset = (TextView) view.findViewById(R.id.tv_asset);
        btThansfer = (Button) view.findViewById(R.id.bt_transfer);
        btAddAsset = (Button) view.findViewById(R.id.bt_addasset);
        tvAssetNum = (TextView) view.findViewById(R.id.tv_assetnum);
        chartLayout = (FrameLayout) view.findViewById(R.id.chartLayout);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent1(MessageEvent messageEvent) {
        switch (messageEvent.getMessage()) {
            case 3:
                //sectionAdapter.notifyDataSetChanged();
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

    private void updateView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_selectasset:
                break;
            case R.id.iv_invisible:
                if (assetVisable) {
                    tvAssetVisable.setVisibility(View.VISIBLE);
                    tvAsset.setVisibility(View.INVISIBLE);
                    ivAssetVisable.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_visibility));
                }else {
                    tvAssetVisable.setVisibility(View.INVISIBLE);
                    tvAsset.setVisibility(View.VISIBLE);
                    ivAssetVisable.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_invisibility));
                }
                assetVisable=!assetVisable;
                break;
            case R.id.chartLayout:
                break;
            case R.id.bt_transfer:
                break;
            case R.id.bt_addasset:
                break;
        }
    }
}