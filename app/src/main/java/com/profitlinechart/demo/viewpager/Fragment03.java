package com.profitlinechart.demo.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.profitlinechart.demo.R;
import com.zengm.linechart.ProfitBean;
import com.zengm.linechart.ProfitLineChart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 创建人： zengming on 2017/9/18.
 * 功能：
 */

public class Fragment03 extends Fragment {

    private ProfitLineChart profitLineChart;
    private List<ProfitBean> data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment01, container, false);

        setData(20);

        profitLineChart = view.findViewById(R.id.profitLineChart);
        profitLineChart.setmViewPager((ViewPager) getActivity().findViewById(R.id.viewPager));
        profitLineChart.setData(data, new ProfitLineChart.OnSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.e("TAG", "你点击了" + data.get(position).getDate());
            }
        }, data.size()-2);
        return view;
    }


    private void setData(int count) {
        data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double d = 2000.56 + (new Random().nextInt(10) + 1) * 1000;
            BigDecimal bg = new BigDecimal(d);
            double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            data.add(new ProfitBean(f1, i > 9 ? "09/" + (i + 1) : "09/0" + (i + 1)));
            Log.e("0----", "" + data.get(i).getProfit());
        }
    }
}
