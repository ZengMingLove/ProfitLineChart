# 自定义折线图
    采用自定义View实现的收益折线图

## 截图
![Alt text](https://github.com/ZengMingLove/ProfitLineChart/raw/master/screenshot/Screenshot01.png)
![Alt text](https://github.com/ZengMingLove/ProfitLineChart/raw/master/screenshot/Screenshot02.png)

## 使用
#### XMl
自定义的属性都是非必填的

    <com.zengm.linechart.ProfitLineChart
        android:id="@+id/profitLineChart"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        app:profitSize="13dp"
        app:dateSize="16dp"
        app:lineColor="#9933FF"
        app:isClick="true"/>
        
#### item点击监听
不是使用view的OnClickListener，可以下载demo查看

    profitLineChart.setData(data, new ProfitLineChart.OnSelectListener() {
        @Override
        public void onSelect(int position) {
        
        }
    });
    
#### 嵌套ViewPager使用，解决不能滑动的问题
需要使用ProfitLineChart内部的setmViewPager

    profitLineChart.setmViewPager((ViewPager) getActivity().findViewById(R.id.viewPager));