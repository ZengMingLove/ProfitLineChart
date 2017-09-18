package com.zengm.linechart;

/**
 * 创建人： zengming on 2017/9/14.
 * 功能：日期及对应收益
 */

public class ProfitBean {

    private double profit;
    private String date;

    public ProfitBean(double profit, String date) {
        this.profit = profit;
        this.date = date;
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
}
