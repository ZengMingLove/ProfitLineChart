package com.profitlinechart.demo.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.profitlinechart.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建人： zengming on 2017/9/18.
 * 功能：
 */

public class ViewPagerActivity extends FragmentActivity {

    private ViewPager viewPager;
    private List<Fragment> mFragments;
    private MyViewPagerAdapter myViewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        initData();
        initView();
    }

    private void initData() {
        mFragments = new ArrayList<>();
        mFragments.add(new Fragment01());
        mFragments.add(new Fragment02());
        mFragments.add(new Fragment03());
        mFragments.add(new FragmentConstraint());

        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(),mFragments);
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(4);
    }

}
