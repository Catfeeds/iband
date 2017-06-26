package com.manridy.iband.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


import com.manridy.iband.R;



/**
 * 自定义圆环
 * Created by Administrator on 2016/9/19.
 */
public class CircularView extends View {
    // Properties
    private float progress = 0;
    private float strokeWidth ;
    private float backgroundStrokeWidth ;
    private int progressColor ;
    private int backgroundColor ;
    private int textColor ;
    private int hintColor ;
    private String text;
    private String title;
    private String state;
    private String unit;

    // Object used to draw
    private int startAngle = -90;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private Paint titlePaint;
    private Paint textPaint;
    private Paint statePaint;
    private Paint unitPaint;
    private Paint linePaint;
    private Context mContext;
    private Bitmap bitmap;

    private float titleSize;
    private float textSize ;
    private float stateSize;
    private float unitSize;

    private int height;
    private int width;
    private float progress2;
    private float stroke;

    public CircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircularView,0,0);
        //value
        progress = typedArray.getFloat(R.styleable.CircularView_cv_progress,progress);
        stroke = typedArray.getFloat(R.styleable.CircularView_cv_stroke_width,10);
        //strokeWidth
        //color
        progressColor = typedArray.getInt(R.styleable.CircularView_cv_progressbar_color,-1);
        backgroundColor = typedArray.getInt(R.styleable.CircularView_cv_background_color,-1);
        textColor = typedArray.getInt(R.styleable.CircularView_cv_text_color,-1);
        hintColor = typedArray.getInt(R.styleable.CircularView_cv_hint_color,-1);

        //text
        text = typedArray.getString(R.styleable.CircularView_cv_text);
        title = typedArray.getString(R.styleable.CircularView_cv_title);
        state = typedArray.getString(R.styleable.CircularView_cv_state);
        unit = typedArray.getString(R.styleable.CircularView_cv_unit);
        int icon =  typedArray.getResourceId(R.styleable.CircularView_cv_icon,-1);
        bitmap = BitmapFactory.decodeResource(mContext.getResources(),icon);
        init(context);

    }

    private void init(Context context) {
        //Init Background
        rectF = new RectF();

        strokeWidth = dp2px(context,stroke);
        backgroundStrokeWidth = dp2px(context,stroke);
        titleSize = sp2px(context,12);
        textSize = sp2px(context,50);
        stateSize = sp2px(context,12);
        unitSize = sp2px(context,10);
        if (textColor == -1) {
            textColor = Color.parseColor("#deffffff");
        }
        if (hintColor == -1) {
            hintColor = Color.parseColor("#8affffff");
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setAntiAlias(true);//抗锯齿


        //Init Foreground
        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(progressColor);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(backgroundStrokeWidth);
        foregroundPaint.setAntiAlias(true);//抗锯齿

//        foregroundPaint.setStrokeCap(Paint.Cap.ROUND);//设置为圆角

        //显示标题文字
        titlePaint = new Paint();
        titlePaint.setTextSize(titleSize);
        titlePaint.setColor(hintColor);
        titlePaint.setAntiAlias(true);
//        titlePaint.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/gotham.ttf"));
        titlePaint.setTextAlign(Paint.Align.CENTER);

        //显示中间文字
        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
//        textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/gotham-book.ttf"));
        textPaint.setTextAlign(Paint.Align.CENTER);

        //显示状态文字
        statePaint = new Paint();
        statePaint.setTextSize(stateSize);
        statePaint.setColor(hintColor);
        statePaint.setAntiAlias(true);
//        statePaint.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/gotham.ttf"));
        statePaint.setTextAlign(Paint.Align.CENTER);


        //显示单位文字
        unitPaint = new Paint();
        unitPaint.setTextSize(stateSize);
        unitPaint.setColor(textColor);
        unitPaint.setAntiAlias(true);

        //画线
        linePaint = new Paint();
        linePaint.setColor(hintColor);
        linePaint.setStrokeWidth(dip2px(context,1));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        title = title == null ? "" :title;
        text = text == null ? "":text;
        state = state == null ? "":state;
        canvas.drawOval(rectF,backgroundPaint);
        float angle = 360 * progress / 100;
        canvas.drawArc(rectF, startAngle,angle,false,foregroundPaint);
        if (progress2 >0) {
            float angle2 = 360 * progress2 / 100;
            foregroundPaint.setColor(Color.parseColor("#89ffffff"));
            canvas.drawArc(rectF, startAngle,angle2,false,foregroundPaint);
        }
        canvas.drawText(title,width/2,height/5+titleSize,titlePaint);
        canvas.drawText(text,width/2,(height+textSize)/2-20,textPaint);
        canvas.drawText(state,width/2,height/10*7.2f+stateSize,statePaint);
        if (unit != null) {
            canvas.drawText(unit,width/10*8-unitSize-10,(height+textSize)/2+20,statePaint);
        }
        if (bitmap != null) {
            canvas.drawLine(width/5,height/10*7,width/5*4,height/10*7,linePaint);
            canvas.drawBitmap(bitmap,(width-bitmap.getWidth())/2,height/5-bitmap.getHeight()-10,new Paint());
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        height = getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        final int min = Math.min(width,height);
        setMeasuredDimension(min,min);
        float highStroke = (strokeWidth > backgroundStrokeWidth) ? strokeWidth : backgroundStrokeWidth;
        rectF.set(0 + highStroke / 2, 0 + highStroke / 2,min - highStroke / 2,min - highStroke / 2);

    }

    public float getProgress() {
        return progress;
    }

    public CircularView setProgress(float progress) {
        this.progress = (progress<=100) ? progress : 100;
//        Log.d("CircularView", "setProgress() called with: progress = [" + progress + "]");
        return this;
    }

    public CircularView setProgress2(float progress2) {
        this.progress2 = (progress2<=100) ? progress2 : 100;
//        Log.d("CircularView", "setProgress() called with: progress = [" + progress + "]");
        return this;
    }

    public CircularView setProgressWithAnimation(float progress){
        progress = progress<0.1f? 0.5f :progress;
        progress = (progress<=100) ? progress : 100;
        this.setProgressWithAnimation(progress,1000);
        Log.d("CircularView", "setProgressWithAnimation() called with: progress = [" + progress + "]");
        return this;
    }

    public CircularView setProgressWithAnimation(float progress , int duration){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this,"progress",progress);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
        return this;
    }

    public CircularView setText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return text;
    }

    public CircularView setState(String state) {
        this.state = state;
        return this;
    }

    public String getState() {
        return state;
    }

    public CircularView setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void invaliDate(){
        requestLayout();
        invalidate();
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


