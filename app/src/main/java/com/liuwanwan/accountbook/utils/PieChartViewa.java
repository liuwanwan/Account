package com.liuwanwan.accountbook.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.liuwanwan.accountbook.R;

import java.util.ArrayList;
import java.util.List;

public class PieChartViewa extends View {

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    /**
     * 饼图半径
     */
    private float pieChartCircleRadius = 100;

    private float textBottom;
    /**
     * 记录文字大小
     */
    private float mTextSize = 14;

    /**
     * 饼图所占矩形区域（不包括文字）
     */
    private RectF pieChartCircleRectF = new RectF();
   // private RectF inerPieCharCircleRectF=new RectF();
    /**
     * 饼状图信息列表
     */
    private List<PieceDataHolder> pieceDataHolders = new ArrayList<>();


    /**
     * 标记线长度
     */
    private float markerLineLength = 30f;

    public PieChartViewa(Context context) {
        super(context);
        init(null, 0);
    }

    public PieChartViewa(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PieChartViewa(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PieChartView, defStyle, 0);

        pieChartCircleRadius = a.getDimension(
                R.styleable.PieChartView_circleRadius,
                pieChartCircleRadius);

        mTextSize = a.getDimension(R.styleable.PieChartView_textSize, mTextSize)/getResources().getDisplayMetrics().density;

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);


        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {

        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, getContext().getResources().getDisplayMetrics()));

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.descent - fontMetrics.ascent;
        textBottom = fontMetrics.bottom;
    }

    /**
     * 设置饼状图的半径
     *
     * @param pieChartCircleRadius 饼状图的半径（px）
     */
    public void setPieChartCircleRadius(int pieChartCircleRadius) {

        this.pieChartCircleRadius = pieChartCircleRadius;

        invalidate();
    }

    /**
     * 设置标记线的长度
     *
     * @param markerLineLength 标记线的长度（px）
     */
    public void setMarkerLineLength(int markerLineLength) {
        this.markerLineLength = markerLineLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initPieChartCircleRectF();

        drawAllSectors(canvas);

    }

    private void drawAllSectors(Canvas canvas) {
        float sum = 0f;
        for (PieceDataHolder pieceDataHolder : pieceDataHolders) {
            sum += pieceDataHolder.value;
        }

        float sum2 = 0f;
        float preAngel=0f;
        for (PieceDataHolder pieceDataHolder : pieceDataHolders) {
            float startAngel = sum2 / sum * 360;
            sum2 += pieceDataHolder.value;
            float sweepAngel = pieceDataHolder.value / sum * 360;
            drawSector(canvas, pieceDataHolder.color, startAngel, sweepAngel);
            drawMarkerLineAndText(canvas, pieceDataHolder.color,preAngel,startAngel + sweepAngel / 2, pieceDataHolder.marker);
       		drawLabel(canvas,sum);
            preAngel=startAngel+sweepAngel/2;
		}
    }

	private void drawLabel(Canvas canvas, float sum)
	{
		Paint paint = new Paint();
		paint.setTextSize(72);
		
		String string = "¥"+sum;

		
		//该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center  
        paint.setTextAlign(Paint.Align.CENTER);  

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();  
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top  
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom  

        int baseLineY = (int) (getHeight()/2 - top/2 - bottom/2);//基线中间点的y轴计算公式  

        canvas.drawText(string,getWidth()/2,baseLineY,paint); 
		//paint.setTextAlign(Paint.Align.CENTER);
		//canvas.drawText("¥"+sum,getWidth()/2,getHeight()/2,paint);
	}

    private void initPieChartCircleRectF() {
        pieChartCircleRectF.left = getWidth() / 2 - pieChartCircleRadius;
        pieChartCircleRectF.top = getHeight() / 2 - pieChartCircleRadius;
        pieChartCircleRectF.right = pieChartCircleRectF.left + pieChartCircleRadius * 2;
        pieChartCircleRectF.bottom = pieChartCircleRectF.top + pieChartCircleRadius * 2;
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.(sp)
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * Sets the view's text dimension attribute value. In the PieChartView view, this dimension
     * is the font size.
     *
     * @param textSize The text dimension attribute value to use.(sp)
     */
    public void setTextSize(float textSize) {
        mTextSize = textSize;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * 设置饼状图要显示的数据
     *
     * @param data 列表数据
     */
    public void setData(List<PieceDataHolder> data) {

        if (data != null) {
            pieceDataHolders.clear();
            pieceDataHolders.addAll(data);
        }

        invalidate();
    }

    /**
     * 绘制扇形，空心
     *
     * @param canvas     画布
     * @param color      要绘制扇形的颜色
     * @param startAngle 起始角度
     * @param sweepAngle 结束角度
     */
    protected void drawSector(Canvas canvas, int color, float startAngle, float sweepAngle) {

        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawArc(pieChartCircleRectF, startAngle, sweepAngle, true, paint);

        Paint paint1 = new Paint();//内部圆
        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.FILL);
        paint1.setColor(Color.WHITE);
        //canvas.drawArc(inerPieCharCircleRectF, startAngle, sweepAngle, true, paint1);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float)( pieChartCircleRadius*0.8), paint1);
    }

    /**
     * 绘制标注线和标记文字
     *
     * @param canvas      画布
     // @param color       标记的颜色
     * @param rotateAngel 标记线和水平相差旋转的角度
     */
    protected void drawMarkerLineAndText(Canvas canvas, int color1,float preAngel,float rotateAngel, String text) {
        int color=Color.BLACK;
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);

        Path path = new Path();
        path.close();
        //path.moveTo(getWidth() / 2, getHeight() / 2);
        final float x = (float) (getWidth() / 2 + pieChartCircleRadius * Math.cos(Math.toRadians(rotateAngel)));
        final float y = (float) (getHeight() / 2 + pieChartCircleRadius * Math.sin(Math.toRadians(rotateAngel)));
        //path.lineTo(x, y);
        path.moveTo(x,y);
        final float x1 = (float) (getWidth() / 2 + (markerLineLength + pieChartCircleRadius) * Math.cos(Math.toRadians(rotateAngel)));
        float y1 = (float) (getHeight() / 2 + (markerLineLength + pieChartCircleRadius) * Math.sin(Math.toRadians(rotateAngel)));
       /* if ((90f > rotateAngel && preAngel > 0f)|| (180f > rotateAngel && preAngel > 90f)||(270f > rotateAngel && preAngel > 180f)||(360f > rotateAngel && preAngel >270f)){
            float dh=20;
            float preY=(float) (getHeight() / 2 + (markerLineLength + pieChartCircleRadius) * Math.sin(Math.toRadians(preAngel)));
            float nowY = (float) (getHeight() / 2 + (markerLineLength + pieChartCircleRadius) * Math.sin(Math.toRadians(rotateAngel)));
            Log.v("AAA","P="+preY+"AAA"+nowY);
            if ((nowY-preY)<dh&&(nowY-preY)>0){
                y1=preY+dh;
            }
            if ((preY-nowY)<dh&&(preY-nowY)>0){
                y1=preY-dh;
            }
        }*/

        //path.lineTo(x, y);
        path.lineTo(x1,y1);
        float landLineX;
        if (270f > rotateAngel && rotateAngel > 90f) {
            landLineX = x1 - 20;
        } else {
            landLineX = x1 + 20;
        }
        path.lineTo(landLineX, y1);
        canvas.drawPath(path, paint);

        mTextPaint.setColor(color);
        if (270f > rotateAngel && rotateAngel > 90f) {
            float textWidth = mTextPaint.measureText(text);
            canvas.drawText(text, landLineX - textWidth, y1 + mTextHeight / 2 - textBottom, mTextPaint);

        } else {
            canvas.drawText(text, landLineX, y1 + mTextHeight / 2 - textBottom, mTextPaint);
        }

    }

    /**
     * 饼状图每块的信息持有者
     */
    public static final class PieceDataHolder {

        /**
         * 每块扇形的值的大小
         */
        private float value;

        /**
         * 扇形的颜色
         */
        private int color;

        /**
         * 每块的标记
         */
        private String marker;


        public PieceDataHolder(float value, int color, String marker) {
            this.value = value;
            this.color = color;
            this.marker = marker;
        }
    }

}
