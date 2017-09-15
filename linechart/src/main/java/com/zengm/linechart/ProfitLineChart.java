package com.zengm.linechart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.ArrayList;

/**
 * 创建人： zengming on 2017/9/14.
 * 功能：自定义收益折线图
 */

public class ProfitLineChart extends View {

    public static final String TAG = "ProfitLineChart";
    private static int DEFAULT_LINECOLOR = 0XFF21D1C1;    // 默认折线的颜色
    private static int DEFAULT_SPOTCOLOR = 0XFF449FFB;    // 默认选中点的颜色
    private static int DEFAULT_TEXTCOLOR = 0XFFA9A9A9;    // 默认文字的颜色

    private int defProfitSize = 13;
    private int defDateSize = 16;
    private int backgroundColor;  // view的背景色
    private int lineColor;  // 折线的颜色
    private int spotColor;  // 选中点的颜色
    private int profitSize = 0;   // 收益文字大小
    private int dateSize = 0;   // 日期文字大小
    private boolean isClick = false;  // view是否可以被点击，默认不能点击

    private int mViewHeight; //控件的最低高度
    private int minPointHeight;//折线最低点的高度,收益为0时也有个默认高度
    private int lineInterval; //一个日期的水平宽度，屏幕默认放5个日期
    private float pointRadius; //折线点的半径
    private double pointGap; //折线单位高度差

    private int leftPadding; //折线坐标图左边/右边留出来的偏移量
    private int bottomPadding; //折线坐标图上/下边留出来的偏移量

    private int viewHeight;
    private int viewWidth;
    private int screenWidth;
    private int screenHeight;

    private Paint linePaint;    //线画笔
    private Paint textPaint;    //文字画笔
    private Paint circlePaint;  //圆点画笔

    private ArrayList<ProfitBean> data = new ArrayList<>(); // 源数据
    private ArrayList<PointF> points = new ArrayList<>(); //折线拐点的集合
    // 源数据中的最高和最低收益
    private double maxProfit;
    private double minProfit;

    private VelocityTracker velocityTracker;
    private Scroller scroller;
    private ViewConfiguration viewConfiguration;

    public ProfitLineChart(Context context) {
        super(context, null);
        init(context, null);
    }

    public ProfitLineChart(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    public ProfitLineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        scroller = new Scroller(context);
        viewConfiguration = ViewConfiguration.get(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProfitLineChart);
        backgroundColor = a.getColor(R.styleable.ProfitLineChart_backgroundColor, Color.WHITE);
        lineColor = a.getColor(R.styleable.ProfitLineChart_lineColor, DEFAULT_LINECOLOR);
        spotColor = a.getColor(R.styleable.ProfitLineChart_spotColor, DEFAULT_SPOTCOLOR);
        profitSize = (int) a.getDimension(R.styleable.ProfitLineChart_profitSize, profitSize);
        dateSize =  (int) a.getDimension(R.styleable.ProfitLineChart_dateSize, dateSize);
        isClick = a.getBoolean(R.styleable.ProfitLineChart_isClick, isClick);
        a.recycle();

        setBackgroundColor(backgroundColor);

        initSize(context);

        initPaint(context);
    }

    /**
     * 唯一公开方法，用于设置元数据
     *
     * @param data
     */
    public void setData(ArrayList<ProfitBean> data) {
        setData(data, null);
    }
    public void setData(ArrayList<ProfitBean> data, OnSelectListener listener) {
        if (data == null || data.isEmpty()) {
            return;
        }
        this.data = data;
        this.listener = listener;
        points.clear();

        requestLayout();
        invalidate();
    }

    /**
     * 初始化数据
     */
    private void initSize(Context context) {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        Log.e(TAG, "screenWidth = " + screenWidth + " === screenHeight = " + screenHeight);

        mViewHeight = dp2px(context, 160);
        pointRadius = dp2pxF(context, 3f);
        lineInterval = screenWidth / 5;
        leftPadding = lineInterval / 5; // 默认为间隔的1/5
        bottomPadding = lineInterval / 5 * 2; // 默认为间隔的2/5
        minPointHeight = lineInterval / 5; // 默认为0时，点与日期间的高度差为间隔的1/5

        Log.e(TAG, "profitSize = " + profitSize + "   dateSize = " + dateSize);
        profitSize = profitSize == 0 ? (int) dp2pxF(context, defProfitSize) : profitSize;
        dateSize = dateSize == 0 ? (int) dp2pxF(context, defDateSize) : dateSize;
        Log.e(TAG, "profitSize = " + profitSize + "   dateSize = " + dateSize);
//        profitSize = (int) sp2pxF(context, profitSize);
//        dateSize = (int) sp2pxF(context, dateSize);
//        Log.e(TAG, "profitSize = " + profitSize + "   dateSize = " + dateSize);

        Log.e(TAG, "mViewHeight = " + mViewHeight + "   -lineInterval" + lineInterval);
    }

    /**
     * 初始化画笔
     */
    private void initPaint(Context c) {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(dp2px(c, 1));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(DEFAULT_TEXTCOLOR);
        textPaint.setTextAlign(Paint.Align.CENTER);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStrokeWidth(dp2pxF(c, 1));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        Log.e(TAG, "mViewHeight = " + mViewHeight + "heightSize = " + heightSize);
        if (heightMode == MeasureSpec.EXACTLY) {
            viewHeight = Math.max(heightSize, mViewHeight);
        } else {
            viewHeight = mViewHeight;
        }

        int totalWidth = 0; // 总宽度
        if (data.size() > 0) {
            totalWidth = lineInterval * data.size();
        }
        viewWidth = Math.max(screenWidth, totalWidth);

        setMeasuredDimension(viewWidth, viewHeight);
        calculatePontGap();
        Log.e(TAG, "viewHeight = " + viewHeight + ";viewWidth = " + viewWidth);
    }

    /**
     * view大小发生改变时调用
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initSize(getContext());
        calculatePontGap();
    }

    /**
     * 计算折线高度差
     */
    private void calculatePontGap() {
        double lastMaxProfit = -100000000;    // 当收益在-1亿以上时，计算不会出错
        double lastMinProfit = 100000000;     // 当收益在1亿以下时，计算不会出错
        for (ProfitBean bean : data) {
            if (bean.getProfit() > lastMaxProfit) {
                maxProfit = bean.getProfit();
                lastMaxProfit = bean.getProfit();
            }
            if (bean.getProfit() < lastMinProfit) {
                minProfit = bean.getProfit();
                lastMinProfit = bean.getProfit();
            }
        }

        double gap = maxProfit * 1.0d;
        gap = (gap == 0 ? 1.0d : gap);

        pointGap = (viewHeight - minPointHeight - bottomPadding - bottomPadding * 1.5) / gap;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (viewWidth > screenWidth) {
            scrollTo(viewWidth - screenWidth, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data.isEmpty()) {
            return;
        }

        drawAxis(canvas);

        drawLinesAndPoints(canvas);

        drawProfit(canvas);
    }

    /**
     * 画时间轴
     * @param canvas  画布
     */
    private void drawAxis(Canvas canvas) {
        canvas.save();

//        linePaint.setColor(DEFAULT_TEXTCOLOR);
//        linePaint.setStrokeWidth(dp2px(getContext(), 1));
//        canvas.drawLine(leftPadding, viewHeight - bottomPadding, viewWidth - leftPadding, viewHeight - bottomPadding, linePaint);

        float centerY = viewHeight - bottomPadding;
        float centerX;

        for (int i = 0; i < data.size(); i++) {
            String date = data.get(i).getDate();
            centerX = lineInterval / 2 + (i * lineInterval);

            if (isClick && data.get(i).isSelected()) {
                textPaint.setTextSize(dateSize + 1f); //字体放大一丢丢
                textPaint.setColor(Color.BLACK);
                Paint.FontMetrics fm1 = textPaint.getFontMetrics();
                canvas.drawText(date, 0, date.length(), centerX, centerY - (fm1.ascent + fm1.descent) / 2, textPaint);
            } else {
                textPaint.setTextSize(dateSize);
                textPaint.setColor(DEFAULT_TEXTCOLOR);
                Paint.FontMetrics fm = textPaint.getFontMetrics();
                canvas.drawText(date, 0, date.length(), centerX, centerY - (fm.ascent + fm.descent) / 2, textPaint);
            }
        }
        canvas.restore();
    }

    /**
     * 画折线和它拐点的圆
     * @param canvas  画布
     */
    private void drawLinesAndPoints(Canvas canvas) {
        canvas.save();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(dp2px(getContext(), 2));
        linePaint.setStyle(Paint.Style.STROKE);

        // 绘制折线
        Path linePath = new Path();
        points.clear();
        int baseHeight = bottomPadding + minPointHeight;
        float centerX;
        float centerY;
        for (int i = 0; i < data.size(); i++) {
            double profit = data.get(i).getProfit();
            Log.e(TAG, "profit = " + profit);
            centerY = (float) (viewHeight - (baseHeight + profit * pointGap));
            centerX = lineInterval / 2 + (i * lineInterval);
            points.add(new PointF(centerX, centerY));
            if (i == 0) {
                linePath.moveTo(centerX, centerY);
            } else {
                linePath.lineTo(centerX, centerY);
            }
        }
        canvas.drawPath(linePath, linePaint); // 画出折线

        //接下来画折线拐点的圆
//        coolCircle(canvas);
        normalCircle(canvas);

        canvas.restore();
    }

    /**
     * 内置两种拐点圆形
     * 酷炫
     * 正常
     * @param canvas  画布
     */
    private void coolCircle(Canvas canvas) {
        float x, y;
        for (int i = 0; i < points.size(); i++) {
            x = points.get(i).x;
            y = points.get(i).y;

            // 先画一个颜色与背景颜色相同的实心圆覆盖掉折线拐点
            circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            circlePaint.setColor(backgroundColor);
            canvas.drawCircle(x, y, pointRadius + dp2pxF(getContext(), 1), circlePaint);

            // 再画出正常的空心圆
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setColor(lineColor);
            canvas.drawCircle(x, y, pointRadius, circlePaint);
        }
    }
    private void normalCircle(Canvas canvas) {
        float x, y;
        for (int i = 0; i < points.size(); i++) {
            x = points.get(i).x;
            y = points.get(i).y;

            if (isClick && data.get(i).isSelected()) {
                // 先画一个颜色与背景颜色相同的实心圆覆盖掉折线拐点
                circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                circlePaint.setColor(backgroundColor);
                canvas.drawCircle(x, y, pointRadius + dp2pxF(getContext(), 3), circlePaint);

                // 再画出小一点的蓝色的实心圆
                circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                circlePaint.setColor(spotColor);
                canvas.drawCircle(x, y, pointRadius + dp2pxF(getContext(), 1), circlePaint);
            } else {
                // 画一个颜色与折线颜色相同的实心圆覆盖掉折线拐点
                circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                circlePaint.setColor(lineColor);
                canvas.drawCircle(x, y, pointRadius, circlePaint);
            }
        }
    }

    /**
     * 画收益描述
     * @param canvas
     */
    private void drawProfit(Canvas canvas) {
        canvas.save();

        float centerX;
        float centerY;
        for (int i = 0; i < points.size(); i++) {
            centerX = points.get(i).x;
            centerY = points.get(i).y - dp2pxF(getContext(), 15);

            if (isClick && data.get(i).isSelected()) {
                textPaint.setTextSize(profitSize + 2); //字体放大一丢丢
                textPaint.setColor(spotColor);
                Paint.FontMetrics fm = textPaint.getFontMetrics();
                canvas.drawText(String.valueOf(data.get(i).getProfit()), centerX, centerY - (fm.ascent + fm.descent) / 2, textPaint);
            } else {
                textPaint.setTextSize(profitSize);
                textPaint.setColor(DEFAULT_TEXTCOLOR);
                Paint.FontMetrics fm = textPaint.getFontMetrics();
                canvas.drawText(String.valueOf(data.get(i).getProfit()), centerX, centerY - (fm.ascent + fm.descent) / 2, textPaint);
            }
        }
        canvas.restore();
    }

    // 分别记录上次滑动的坐标
    private int mLastX = 0;
    // 记录事件按下是的坐标，用于判断是否是点击事件
    int mDownX = 0;
    int mDownY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                mDownX = mLastX = x;
                mDownY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                int deltaX = mLastX - x;
                if (getScrollX() + deltaX < 0) { // 越界恢复
                    scrollTo(0, 0);
                    return true;
                } else if (getScrollX() + deltaX > viewWidth - screenWidth){
                    scrollTo(viewWidth - screenWidth, 0);
                    return true;
                }
                scrollBy(deltaX, 0);
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(1000);
                int xVelocity = (int) velocityTracker.getXVelocity();

                if (Math.abs(xVelocity) > viewConfiguration.getScaledMinimumFlingVelocity()) { // 滑动速度可被判定为抛动
                    //在快速滑动松开的基础上开始惯性滚动，滚动距离取决于fling的初速度
                    scroller.fling(getScrollX(), 0, -xVelocity, 0, 0, viewWidth - screenWidth, 0, 0);
                    invalidate();
                }

                Log.e(TAG, "mDownX = " + mDownX + "  mDownY = " + mDownY + "\n" + "x = " + x + "  y = " + y + "\n" + "getScrollX = " + getScrollX());
                // 判断手指抬起的点的坐标与按下的坐标是否一致 (在某个范围内)
//                if (isClick && mDownX == x && mDownY == y) {
                if (isClick && Math.abs(mDownX - x) < 20 && Math.abs(mDownY - y) < 20) {
                    calculatePosition(getScrollX() + (int) event.getRawX(), mDownY);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    /**
     * 计算点击事件位于那个区域
     * @param clickX   点击位置相对于view的最左边的距离
     * @param clickY   点击位置的Y坐标
     */
    private void calculatePosition(int clickX, int clickY) {
        if (clickY > bottomPadding * 1.5) {
            int position = clickX / lineInterval;
            Log.e(TAG, "position = " + position);

            for (int i = 0; i < data.size(); i++) {
                if (i == position) {
                    data.get(i).setSelected(true);
                } else {
                    data.get(i).setSelected(false);
                }
            }
            invalidate();

            // 点击事件回调
            if (listener != null) {
                listener.onSelect(position);
            }
        }
    }

    //定义一个接口对象listerner
    private OnSelectListener listener;
    //获得接口对象的方法。
    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }
    //定义一个监听点击事件接口
    public interface  OnSelectListener {
        void onSelect(int position); // position从0开始
    }

    //工具类
    public static int dp2px(Context c, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context c, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, c.getResources().getDisplayMetrics());
    }

    public static float dp2pxF(Context c, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static float sp2pxF(Context c, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, c.getResources().getDisplayMetrics());
    }

}
