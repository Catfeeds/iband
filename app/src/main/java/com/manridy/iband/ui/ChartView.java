package com.manridy.iband.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.manridy.iband.bean.SleepModel;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义图表
 * Created by Administrator on 2016/9/19.
 */
public class ChartView extends View {
    private static final String TAG = "ChartView";
    private Paint mPaint;
    private Context mContext;

    private onChartItemSelectListener chartItemSelectListener;
    private int[] mColors;
    private List<Item> itemList = new ArrayList<>();
    private List<Float> indexList = new ArrayList<>();
    private int _height;
    private int _width;
    private int max;
    private int position = -1;
    private int actionState =-1;
    private Handler handler = new Handler();

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(dp2px(context,1));
        mPaint.setAntiAlias(true);//抗锯齿
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left = 0,top = 0,right =0,bottom = _height;
        for (int i = 0; i < indexList.size(); i++) {
            int color = getTypeColor(itemList.get(i),mColors);
            color = i == position ? Color.parseColor("#de673ab7") : color;
            mPaint.setColor(color);
            left = right;
            right = indexList.get(i);
            canvas.drawRect(left,top,right,bottom,mPaint);
//            Log.d(TAG, "onDraw() called with: left = [" + left + "]"+ " top = [" + top + "]"+ " right = [" + right + "]"+ " bottom = [" + bottom + "]");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        _height = getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec);
        _width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        setMeasuredDimension(_width,_height);
//        Log.d(TAG, "onMeasure() called with: _height = [" + _height + "], _width = [" + _width + "]");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                actionState = MotionEvent.ACTION_DOWN;
                position = getPosition(x,indexList);
                if (chartItemSelectListener != null) {
                    chartItemSelectListener.onItemSelect(position);
                }
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                actionState = MotionEvent.ACTION_MOVE;
                position = getPosition(x,indexList);
                handler.removeCallbacks(autoRunnable);
                handler.postDelayed(autoRunnable,3000);
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_UP:
                actionState = MotionEvent.ACTION_UP;
                handler.removeCallbacks(autoRunnable);
                handler.postDelayed(autoRunnable,1000);
                return true;

        }
        return super.onTouchEvent(event);
    }

    Runnable autoRunnable = new Runnable() {
        @Override
        public void run() {
            if (actionState == MotionEvent.ACTION_DOWN) return;
            position = -1;
            invaliDate();
            if (chartItemSelectListener != null) {
                chartItemSelectListener.onNoSelect();
            }
        }
    };


    public ChartView setChartData(int[] colors, List<SleepModel> sleepList) {
        this.itemList = new ArrayList<>();
        for (SleepModel sleepModel : sleepList) {
            itemList.add(new Item(sleepModel));
        }
        this.mColors = colors;
        this.max = getMax(itemList);
        this.indexList =getIndexList(max,_width,itemList);
        return this;
    }

    public void setOnChartItemSelectListener(onChartItemSelectListener chartItemSelectListener) {
        this.chartItemSelectListener = chartItemSelectListener;
    }

    public void invaliDate(){
        requestLayout();
        invalidate();
    }

    private int getMax(List<Item> itemList){
        int sum = 0;
        for (int i = 0; i < itemList.size(); i++) {
            sum += itemList.get(i)._value;
        }
        return sum;
    }

    private List<Float> getIndexList(int max ,int width,List<Item> itemList){
        List<Float> indexs = new ArrayList<>();
        float index = 0;
        for (int i = 0; i < itemList.size(); i++) {
            float value = itemList.get(i)._value;
            index = (index + (width) * (value / max));
            indexs.add(index);
        }
        return indexs;
    }

    private int getTypeColor(Item item, int[] colors){
        int color = Color.BLUE;
        if (colors.length >= item._type-1) {
            color = colors[item._type-1];
        }
        return color;
    }

    private int getPosition(float x,List<Float> indexList){
        for (int i = 0; i < indexList.size(); i++) {
            if (x < indexList.get(i)) {
                return i;
            }
        }
        return 0;
    }

    public class Item{
        public static final int DEEP = 1;
        public static final int LIGHT = 2;
        public static final int SOBER = 3;

        public int _type;
        public int _value;

        public Item(SleepModel sleepModel){
            this._type = sleepModel.getSleepDataType();
            if (_type == DEEP) {
                this._value = sleepModel.getSleepDeep();
            }else if (_type == LIGHT){
                this._value = sleepModel.getSleepLight();
            }else if (_type == SOBER){
                this._value = sleepModel.getSleepAwake();
            }
        }
    }

    public interface onChartItemSelectListener{
        void onItemSelect(int position);
        void onNoSelect();
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dp(Context context,float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * px转sp
     *
     * @param pxValue px值
     * @return sp值
     */
    public static int px2sp(Context context,float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context,float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * sp转px
     *
     * @param spValue sp值
     * @return px值
     */
    public static int sp2px(Context context,float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}


