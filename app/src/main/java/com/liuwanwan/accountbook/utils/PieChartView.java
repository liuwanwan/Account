package com.liuwanwan.accountbook.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class PieChartView extends View {
    private float sum = 0f;
    private Map kindsMap = new LinkedHashMap<String, Float>();
    private ArrayList<Integer> colors = new ArrayList<>();
    private float startAngle = 15f;//起始角度
    private Paint mPaint;//饼状画笔
    private Paint mTextPaint; // 文字画笔
    private int mRadius = 240; //外园的半径
    private float animatedValue;
    private RectF oval;
	int dAngle = 10;
    private DecimalFormat dff;//格式化小数点

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mTextPaint = new Paint();

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        dff = new DecimalFormat("0.0");
        for (int i = 1; i <= 40; i++) {
            int r = (new Random().nextInt(100) + 10) * i;
            int g = (new Random().nextInt(100) + 10) * 3 * i;
            int b = (new Random().nextInt(100) + 10) * 2 * i;
            int color = Color.rgb(r, g, b);
            if (Math.abs(r - g) > 10 && Math.abs(r - b) > 10 && Math.abs(b - g) > 10) {
                colors.add(color);
            }
        }
    }

    public PieChartView(Context context) {
        this(context, null, 0);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*int wideSize = MeasureSpec.getSize(widthMeasureSpec);
        int wideMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width, height;
        if (wideMode == MeasureSpec.EXACTLY) { //精确值 或matchParent
            width = wideSize;
        } else {
            width = mRadius * 2 + getPaddingLeft() + getPaddingRight();
            if (wideMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, wideSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) { //精确值 或matchParent
            height = heightSize;
        } else {
            height = mRadius * 2 + getPaddingTop() + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
        mRadius = (int) (Math.min(width - getPaddingLeft() - getPaddingRight(),
                height - getPaddingTop() - getPaddingBottom()) * 1.0f / 2);*/
        oval = new RectF(-mRadius, -mRadius, mRadius, mRadius);
    }

    @Override
    protected void onDraw(Canvas mCanvas) {
        super.onDraw(mCanvas);
        mCanvas.translate((getWidth() + getPaddingLeft() - getPaddingRight()) / 2, (getHeight() + getPaddingTop() - getPaddingBottom()) / 2);
        paintPie(mCanvas);
    }

    private void paintPie(final Canvas mCanvas) {
        if (kindsMap != null) {
            Set<Map.Entry<String, Float>> entrySet = kindsMap.entrySet();
            Iterator<Map.Entry<String, Float>> iterator = entrySet.iterator();
            int i = 0;
            float currentAngle = startAngle;
            float preAngle = startAngle;
			float adjustPreAngle=0f;
            while (iterator.hasNext()) {
                Map.Entry<String, Float> entry = iterator.next();
                String kinds = entry.getKey();
                float num = entry.getValue();
                float needDrawAngle = num / sum * 360;
                String drawAngle = dff.format(needDrawAngle / 360 * 100);
                kinds = kinds + " " + drawAngle + "%";
                float textAngle = needDrawAngle / 2 + currentAngle;
				float temp=Math.min(needDrawAngle - 1, animatedValue - currentAngle);
                if (temp>= 0) {
                    mPaint.setColor(colors.get(i));
                    mCanvas.drawArc(oval, currentAngle, temp, true, mPaint);
                    mPaint.setColor(Color.WHITE);
                    mPaint.setAlpha(10);
                    mCanvas.drawCircle(0, 0, mRadius / 2 + dp2px(10), mPaint);
                    mPaint.setAlpha(255);
                    mCanvas.drawCircle(0, 0, mRadius / 2, mPaint);
                    adjustPreAngle=drawMarkerLineAndText(mCanvas, textAngle, kinds, preAngle,adjustPreAngle);
                    drawCenterText(mCanvas, "¥" + sum, 0, 0);
                }
                preAngle = currentAngle + needDrawAngle / 2;
                currentAngle = currentAngle + needDrawAngle;

                i++;
            }
        }
    }

    //画中间文字标题
    private void drawCenterText(Canvas mCanvas, String text, float x, float y) {
        Rect rect = new Rect();
        Paint p = new Paint();
        p.setTextSize(sp2px(20));
        p.setColor(Color.RED);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        p.setTypeface(font);
        p.getTextBounds(text, 0, text.length(), rect);
        mCanvas.drawText(text, x - rect.width() / 2, y + rect.height() / 2, p);
    }

    //画指引线和文字
    private float drawMarkerLineAndText(Canvas mCanvas, float textAngle, String kinds, float preAngle,float adjustPreAngle) {
        Rect rect = new Rect();
        mTextPaint.setTextSize(sp2px(12));
        mTextPaint.getTextBounds(kinds, 0, kinds.length(), rect);
        Path path = new Path();
        path.close();
        final float x = (float) (mRadius * Math.cos(Math.toRadians(textAngle)));
        final float y = (float) (mRadius * Math.sin(Math.toRadians(textAngle)));
        path.moveTo(x, y);//圆周上
        float x1, y1;
        boolean t=preAngle != startAngle && (textAngle - preAngle) < dAngle;
        if (t) {//不是第一个扇形，且
            x1 = (float) (mRadius * 1.1 * Math.cos(Math.toRadians(adjustPreAngle+dAngle)));
            y1 = (float) (mRadius * 1.1 * Math.sin(Math.toRadians(adjustPreAngle+dAngle)));
        } else {
            x1 = (float) (mRadius * 1.1 * Math.cos(Math.toRadians(textAngle)));
            y1 = (float) (mRadius * 1.1 * Math.sin(Math.toRadians(textAngle)));
        }
        path.lineTo(x1, y1);
        float landLineX;
        if (270f > textAngle && textAngle > 90f) {
            landLineX = x1 - 20;
        } else {
            landLineX = x1 + 20;
        }
        path.lineTo(landLineX, y1);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float mTextHeight = fontMetrics.descent - fontMetrics.ascent;
        float textBottom = fontMetrics.bottom;
        mCanvas.drawPath(path, mTextPaint);

        float textWidth = mTextPaint.measureText(kinds);
        if (270f > textAngle && textAngle > 90f) {
            mCanvas.drawText(kinds, landLineX - textWidth / 2, y1 + mTextHeight / 2 - textBottom, mTextPaint);
        } else {
            mCanvas.drawText(kinds, landLineX + textWidth / 2, y1 + mTextHeight / 2 - textBottom, mTextPaint);
        }
		if(t)
			return  adjustPreAngle+ dAngle;
		else
			return textAngle;
    }

    public void setDataMap(LinkedHashMap<String, Float> map) {
        this.kindsMap = map;
        sum = getSum(map);
    }

    public void startDraw() {
        if (kindsMap != null && colors != null) {
            initAnimator();
        }
    }

    private void initAnimator() {
        ValueAnimator anim = ValueAnimator.ofFloat(0 + startAngle, 360 + startAngle);
        anim.setDuration(1500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animatedValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
    }

    private float getSum(Map<String, Float> map) {
        Set<String> set = map.keySet();
        Iterator<String> iterator = set.iterator();
        float sum = 0f;

        while (iterator.hasNext()) {
            String kinds = iterator.next();
            float num = map.get(kinds);
            sum = sum + num;
        }
        return sum;
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }
}
