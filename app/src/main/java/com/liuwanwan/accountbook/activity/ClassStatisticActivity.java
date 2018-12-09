package com.liuwanwan.accountbook.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.liuwanwan.accountbook.MyApplication;
import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.adapter.ClassItemAdapter;
import com.liuwanwan.accountbook.db.Record;
import com.liuwanwan.accountbook.model.ClassItem;

import java.util.ArrayList;
import java.util.List;


public class ClassStatisticActivity extends AppCompatActivity {
    private ImageView ivClass;
    private TextView tvClassName, tvClassMoney;
    private ListView listView;
    private ClassItemAdapter classItemAdapter;
private List<Record> classStatisticList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        ivClass = (ImageView) findViewById(R.id.iv_class);
        tvClassName = (TextView) findViewById(R.id.tv_classname);
        tvClassMoney = (TextView) findViewById(R.id.tv_classmoney);
        classStatisticList = (List<Record>) getIntent().getSerializableExtra("classStatisticList");
        Record r = classStatisticList.get(0);
        int classId = 0;
        String className = "";

        if (r.isIncome) {
            classId = MyApplication.incomeIds[r.getInOrOutType()];
            className = MyApplication.incomeTypes[r.getInOrOutType()];
        } else {
            classId = MyApplication.expenseIds[r.getInOrOutType()];
            className = MyApplication.expenseTypes[r.getInOrOutType()];
        }

        ivClass.setImageResource(classId);
        tvClassName.setText(className);
        listView = (ListView) findViewById(R.id.listview_class);
        updateClassStatistic();
    }

    private void updateClassStatistic() {
        List<ClassItem> classItemList = new ArrayList<>();
        double classMoney = 0;
        for (Record record : classStatisticList) {
            ClassItem classItem = new ClassItem(record.getRecordedTime(), record.getMoney(), record.getRecordTime());
            classItemList.add(classItem);
            classMoney += record.getMoney();
        }
        tvClassMoney.setText("¥" + classMoney);
        classItemAdapter = new ClassItemAdapter(getSupportFragmentManager(),classItemList);
        listView.setAdapter(classItemAdapter);
    }
}
