package com.liuwanwan.accountbook.activity;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.liuwanwan.accountbook.R;
import com.liuwanwan.accountbook.adapter.HomePagerAdapter;
import com.liuwanwan.accountbook.fragment.AssetFragment;
import com.liuwanwan.accountbook.fragment.MoreFragment;
import com.liuwanwan.accountbook.fragment.RecordFragment;
import com.liuwanwan.accountbook.fragment.TableFragment;
import com.liuwanwan.accountbook.model.HomePagerInfo;

import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager mVpHome;
    private TabLayout mTabLayoutHome;

    private final String[] TITLES = {"记账", "报表", "资产", "更多"};
    private final int[] ICONS = {R.mipmap.record, R.mipmap.table, R.mipmap.fund, R.mipmap.more};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVpHome = (ViewPager) findViewById(R.id.vp_home);
        mTabLayoutHome = (TabLayout) findViewById(R.id.tab_layout_home);
        mVpHome.setOffscreenPageLimit(4);
        initViewPager();
        Connector.getDatabase();
    }

    private void initViewPager() {
        final List<HomePagerInfo> data = new ArrayList<>();
        data.add(new HomePagerInfo(TITLES[0], new RecordFragment()));
        data.add(new HomePagerInfo(TITLES[1], new TableFragment()));
        data.add(new HomePagerInfo(TITLES[2], new AssetFragment()));
        data.add(new HomePagerInfo(TITLES[3], new MoreFragment()));
        HomePagerAdapter homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), data);
        mVpHome.setAdapter(homePagerAdapter);
        mTabLayoutHome.setupWithViewPager(mVpHome);
        int normalColor = Color.parseColor("#8C8C8C");
        int selectColor = Color.parseColor("#000000");
        mTabLayoutHome.setTabTextColors(normalColor, Color.RED);
        mTabLayoutHome.setSelectedTabIndicatorColor(selectColor);
        for (int i = 0; i < 4; i++) {
            mTabLayoutHome.getTabAt(i).setIcon(ICONS[i]);
        }
    }
    private long firtTime=0;
    public void onBackPressed(){
        long seconTime=System.currentTimeMillis();
        if (seconTime-firtTime>2000){
            Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
            firtTime=seconTime;
        }else {
            System.exit(0);
        }
    }
}
