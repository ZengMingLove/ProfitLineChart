package com.profitlinechart.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zengm.linechart.ProfitBean;
import com.zengm.linechart.ProfitLineChart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProfitLineChart profitLineChart;
    private ArrayList<ProfitBean> data;
    private Button btn01, btn02, btn03, btn04;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setData(2);
        initView();
    }

    private void initView() {
        profitLineChart = (ProfitLineChart) findViewById(R.id.profitLineChart);
        profitLineChart.setData(data, new ProfitLineChart.OnSelectListener() {
            @Override
            public void onSelect(int position) {
                Log.e("TAG", "你点击了" + data.get(position).getDate());
            }
        }, data.size()-2);

        btn01 = (Button) findViewById(R.id.btn_01);
        btn02 = (Button) findViewById(R.id.btn_02);
        btn03 = (Button) findViewById(R.id.btn_03);
        btn04 = (Button) findViewById(R.id.btn_04);

        btn01.setOnClickListener(this);
        btn02.setOnClickListener(this);
        btn03.setOnClickListener(this);
        btn04.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_01:
                setData(5);
                profitLineChart.setData(data, data.size()-2);
                break;
            case R.id.btn_02:
                setData(7);
                profitLineChart.setData(data, data.size()-2);
                break;
            case R.id.btn_03:
                setData(15);
                profitLineChart.setData(data, data.size()-2);
                break;
            case R.id.btn_04:
                setData(31);
                profitLineChart.setData(data, data.size()-2);
                break;
            default:
                break;
        }
    }
}
