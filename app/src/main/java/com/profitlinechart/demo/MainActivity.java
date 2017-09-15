package com.profitlinechart.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zengm.linechart.ProfitBean;
import com.zengm.linechart.ProfitLineChart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ProfitLineChart profitLineChart;
    private ArrayList<ProfitBean> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setData();

        profitLineChart = (ProfitLineChart) findViewById(R.id.profitLineChart);
        profitLineChart.setData(data, new ProfitLineChart.OnSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.e("TAG", "你点击了" + data.get(position).getDate());
            }
        });
    }

    private void setData() {
        data = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            double d = 2000.56 + (new Random().nextInt(10) + 1) * 1000;
            BigDecimal bg = new BigDecimal(d);
            double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            data.add(new ProfitBean(f1, i >9 ? "09/" + (i + 1) : "09/0" + (i + 1)));
            Log.e("0----", "" + data.get(i).getProfit());
        }
        data.get(8).setSelected(true);
    }
}
