package com.liuwanwan.accountbook.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwanwan.accountbook.MyApplication;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.adapter.IconAdapter;
import com.liuwanwan.accountbook.db.Record;
import com.liuwanwan.accountbook.model.IconBean;
import com.liuwanwan.accountbook.model.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddRecordActivity extends AppCompatActivity {
    private Button mBtTitleExpense;
    private Button mBtTitleIncome;
    private Button mBtRecordDate;
    private TextView mTvIsExpense;
    private EditText mEtMoney;
    private GridView mGlWrite;
    private List<IconBean> mData;
    private List<IconBean> mExpenseData;
    private List<IconBean> mIncomeData;
    private IconAdapter mIconAdapter;
    private int prePostion;
    private LinearLayout mLlInput;
    private EditText mEtDes;
    private long time;
    private String recordedDate;
    private FloatingActionButton fbDone;
    private String editY, editM, editD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        time = getIntent().getLongExtra("time", 0);
        mBtTitleExpense = (Button) findViewById(R.id.bt_title_expense);
        mBtTitleIncome = (Button) findViewById(R.id.bt_title_income);
        mTvIsExpense = (TextView) findViewById(R.id.tv_is_expense);
        mEtMoney = (EditText) findViewById(R.id.et_money);
        mGlWrite = (GridView) findViewById(R.id.gl_write);
        fbDone = (FloatingActionButton) findViewById(R.id.fb_done);
        mLlInput = (LinearLayout) findViewById(R.id.ll_input);
        mBtRecordDate = (Button) findViewById(R.id.bt_recorddate);
        mEtDes = (EditText) findViewById(R.id.et_des);
        Calendar c = Calendar.getInstance();
        recordedDate = getCurrentDate();
        //tvRecordDate.setText((c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
        initList();
        mBtTitleExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.get(prePostion).selected = false;
                mBtTitleExpense.setEnabled(false);
                mBtTitleIncome.setEnabled(true);
                mTvIsExpense.setText("花钱");
                mData.clear();
                mData.addAll(mExpenseData);
                mIconAdapter.notifyDataSetChanged();
                //mLlInput.setBackgroundResource(R.color.pink);
                //mEtMoney.setBackgroundResource(R.color.pink);
            }
        });
        mBtTitleIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.get(prePostion).selected = false;
                mBtTitleExpense.setEnabled(true);
                mBtTitleIncome.setEnabled(false);
                mTvIsExpense.setText("赚钱");
                mData.clear();
                mData.addAll(mIncomeData);
                mIconAdapter.notifyDataSetChanged();
                //mLlInput.setBackgroundResource(R.color.orange);
                //mEtMoney.setBackgroundResource(R.color.orange);
            }
        });
        mIconAdapter = new IconAdapter(mData);
        mGlWrite.setAdapter(mIconAdapter);
        mGlWrite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mData.get(prePostion).selected = false;
                mData.get(position).selected = true;
                prePostion = position;
                mIconAdapter.notifyDataSetChanged();
            }
        });
        fbDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRecord();
            }
        });

        mEtMoney.setSelection(mEtMoney.getText().length());//将光标移至文字末尾
        mEtMoney.requestFocus();
        //打开软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        mEtMoney.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                int textViewId = textView.getId();
                if (textViewId == mEtDes.getId()) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        return true;
                    }
                }
                if (textViewId == mEtMoney.getId()) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        mEtMoney.clearFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        return true;
                    }
                }
                return false;
            }
        });
        mBtRecordDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                new DatePickerDialog(AddRecordActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        if (y < c.get(Calendar.YEAR) || (y == c.get(Calendar.YEAR) && m < c.get(Calendar.MONTH)) || (y == c.get(Calendar.YEAR) && m == c.get(Calendar.MONTH) && d <= c.get(Calendar.DAY_OF_MONTH)))//选择的是过去或现在
                            mBtRecordDate.setText((m + 1) + "-" + d);
                        else//不能给未来记账
                        {
                            Toast.makeText(AddRecordActivity.this, "不能给未来记账！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String mm = (m+1) + "";
                        String dd = d + "";
                        if (m<9)
                            mm = "0" + (m + 1);
                        if (d < 10)
                            dd = "0" + d;
                        editY=y+"";
                        editM=mm;
                        editD=dd;
                        recordedDate = y + mm + dd;
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        initData();

    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        String mm =(m+1) + "";
        String dd = d + "";
        if (m<9)
            mm = "0" + (m + 1);
        if (d < 10)
            dd = "0" + d;
        return c.get(Calendar.YEAR) + mm + dd;
    }

    private void initData() {
        if (time != 0) {
            Record record = LitePal.where("recordTime=?", time + "").findFirst(Record.class);
            if (record.isIncome) {
                mBtTitleIncome.performClick();
            } else {
                mBtTitleExpense.performClick();
            }
            prePostion = record.getInOrOutType();
            mEtDes.setText(record.getNote());
            mEtMoney.setText(record.getMoney() + "");
            String temp = record.getRecordedTime();
            editY = temp.substring(0, 4);
            editM = temp.substring(4, 6);
            editD = temp.substring(6, 8);
            mBtRecordDate.setText(editM + "-" + editD);
            Calendar c = Calendar.getInstance();
            recordedDate = c.get(Calendar.YEAR) + temp.substring(4, 8);
        }
    }

    private void initList() {
        mData = new ArrayList<>();
        mExpenseData = new ArrayList<>();
        mIncomeData = new ArrayList<>();
        int m = MyApplication.expenseIds.length;
        int n = MyApplication.incomeIds.length;
        for (int i = 0; i < m; i++) {
            mExpenseData.add(new IconBean(MyApplication.expenseIds[i], MyApplication.expenseTypes[i], false));
        }
        for (int i = 0; i < n; i++) {
            mIncomeData.add(new IconBean(MyApplication.incomeIds[i], MyApplication.incomeTypes[i], false));
        }
        mData.addAll(mExpenseData);
    }

    private void changeRecord() {
        boolean isIncome = mBtTitleExpense.isEnabled();
        int type = prePostion;
        String qian = mEtMoney.getText().toString();
        if (TextUtils.isEmpty(qian)) {
            Toast.makeText(getApplicationContext(), "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        double money = Double.parseDouble(qian);
        if (money == 0) {
            Toast.makeText(getApplicationContext(), "请输入非0金额", Toast.LENGTH_SHORT).show();
            return;
        }
        String des = mEtDes.getText().toString();
        Calendar calender = Calendar.getInstance();
        Record record = new Record();
        record.setIncome(isIncome);
        record.setInOrOutType(type);
        record.setMoney(money);
        record.setNote(des);

        if (time == 0) {//新记录，增加
            String tempYear = recordedDate.substring(0, 4);
            String tempMonth=recordedDate.substring(4, 6);
            String tempDay = recordedDate.substring(6, 8);
            record.setRecordedTime(tempYear + tempMonth + tempDay);
            record.setRecordedYear(tempYear);
            record.setRecordedYearMonth(tempYear + tempMonth);
            record.setRecordTime(calender.getTime().getTime());
            record.save();
        } else {//老记录，更新
            record.setRecordedTime(editY + editM + editD);
            record.setRecordedYear(editY);
            record.setRecordedYearMonth(editY + editM);
            record.setRecordTime(calender.getTime().getTime());
            record.updateAll("recordTime=?", time + "");
        }
        EventBus.getDefault().post(new MessageEvent(1));
        finish();
    }
}
