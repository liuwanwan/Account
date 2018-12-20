package com.liuwanwan.accountbook.utils;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuwanwan.accountbook.MyApplication;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.activity.AddRecordActivity;
import com.liuwanwan.accountbook.db.Record;
import com.liuwanwan.accountbook.model.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.List;

public class BottomDialogFragment extends DialogFragment implements View.OnClickListener {
    private Button btDelete, btEdit;
    private TextView tvDetailTitle,tvDetailType, tvDetailMoney, tvDetailTime, tvDetailAccount;
    private ImageView ivDetailType;
    private long recordTime = 0;
    private int fromflag = 0;

    public static BottomDialogFragment newInstance(long time, int flag) {
        BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("ShowDetaiRecord", time);
        bundle.putInt("flag", flag);
        bottomDialogFragment.setArguments(bundle);
        return bottomDialogFragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            recordTime = getArguments().getLong("ShowDetaiRecord");
            fromflag = getArguments().getInt("flag");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.BottomFragmentDialog);
        // 必须在setContentView之前调用。否则运行时报错。
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialogfragment__record_detail, null);
        btDelete = (Button) view.findViewById(R.id.bt_delete);
        btEdit = (Button) view.findViewById(R.id.bt_edit);

        ivDetailType = (ImageView) view.findViewById(R.id.iv_detailtype);
        tvDetailTitle=(TextView)view.findViewById(R.id.tv_detailtitle);
        tvDetailType = (TextView) view.findViewById(R.id.tv_detailname);
        tvDetailMoney = (TextView) view.findViewById(R.id.tv_detailmoney);
        tvDetailTime = (TextView) view.findViewById(R.id.tv_detailtime);
        tvDetailAccount = (TextView) view.findViewById(R.id.tv_detailaccount);
        List<Record> recordList = LitePal.where("recordTime=?", recordTime + "").find(Record.class);
        Record record = recordList.get(0);
        int id;
        String type;
        if (record.isIncome) {
            id = MyApplication.incomeIds[record.getInOrOutType()];
            type = MyApplication.incomeTypes[record.getInOrOutType()];
        } else {
            id = MyApplication.expenseIds[record.getInOrOutType()];
            type = MyApplication.expenseTypes[record.getInOrOutType()];
        }
        ivDetailType.setImageResource(id);
        tvDetailType.setText(type);
        tvDetailMoney.setText("¥" + record.getMoney());
        switch (fromflag) {
            case 1:
                tvDetailTitle.setVisibility(View.INVISIBLE);
                btDelete.setVisibility(View.VISIBLE);
                btDelete.setEnabled(true);
                btDelete.setOnClickListener(this);
                btEdit.setVisibility(View.VISIBLE);
                btEdit.setEnabled(true);
                btEdit.setOnClickListener(this);
                break;
            case 2://统计页面的详情，只能查看不能修改
                tvDetailTitle.setVisibility(View.VISIBLE);
                btDelete.setVisibility(View.INVISIBLE);
                btDelete.setEnabled(false);
                btEdit.setVisibility(View.INVISIBLE);
                btEdit.setEnabled(false);
                break;
        }

        //时间戳转化为Sting或Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(record.getRecordTime());
        tvDetailTime.setText(d);

        // 底部弹出的DialogFragment装载的View
        dialog.setContentView(view);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        // 设置底部弹出显示的DialogFragment窗口属性。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 1000; // 底部弹出的DialogFragment的高度，如果是MATCH_PARENT则铺满整个窗口
        window.setAttributes(params);

        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_delete:
                LitePal.deleteAll(Record.class, "recordTime=?", recordTime + "");
                break;
            case R.id.bt_edit:
                Intent intent = new Intent(getContext(), AddRecordActivity.class);
                intent.putExtra("time", recordTime);
                startActivity(intent);
                break;
        }
        // 发布事件
        EventBus.getDefault().post(new MessageEvent(MyApplication.ADD_DEL_RECORD));
        dismiss();
    }
}
