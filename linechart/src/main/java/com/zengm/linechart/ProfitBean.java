package com.zengm.linechart;

/**
 * 创建人： zengming on 2017/9/14.
 * 功能：日期及对应收益
 */

public class ProfitBean {

    private double profit;
    private String date;
    private boolean isSelected; // 表示对应的日期是否被选中，非必须值

    public ProfitBean(double profit, String date) {
        this(profit, date, false);
    }

    public ProfitBean(double profit, String date, boolean isSelected) {
        this.profit = profit;
        this.date = date;
        this.isSelected = isSelected;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
