package com.liuwanwan.accountbook.utils;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import java.util.*;
import android.animation.*;
import android.view.animation.*;

public class ColumnChartView extends View
{
	//private Map kindsMap = new LinkedHashMap<String, Float>();
	private float animatedValue;
	private float animatedEnd=300f;
	private int leftColor;//双柱左侧
	private int rightColor;//双柱右侧
	private int lineColor;//横轴线
	private int selectLeftColor;//点击选中左侧
	private int selectRightColor;//点击选中右侧
	private int leftColorBottom;//左侧底部
	private int rightColorBottom;//右侧底部
	private Paint mPaint, mChartPaint, mShadowPaint;//横轴画笔、柱状图画笔、阴影画笔
	private int mWidth, mHeight, mStartWidth, mChartWidth, mSize;//屏幕宽度高度、柱状图起始位置、柱状图宽度
	private Rect mBound;
	private LinkedHashMap<String, Float> map = new LinkedHashMap<String, Float>();//柱状图高度占比
	private Rect rect;//柱状图矩形
	//private getNumberListener listener;//点击接口
	private int num=6;//柱状图初始个数
	private int selectIndex = -1;//点击选中柱状图索引
	private List<Integer> selectIndexRoles = new ArrayList<>();
	private float max;
	/*public void setList(LinkedHashMap<String, Float> map) {
	 this.map = map;
	 num=map.size();
	 mStartWidth = getWidth() / (2*num+1);
	 mSize = mStartWidth;
	 //mSize = getWidth() / 39;
	 //mStartWidth = getWidth() / num;
	 //mChartWidth = getWidth() / num;
	 //max=map.get(0).floatValue();
	 invalidate();
	 }
	 */
	//public void setListener(getNumberListener listener) {
	//this.listener = listener;
	//}

	public ColumnChartView(Context context)
	{
		this(context, null);
	}

	public ColumnChartView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ColumnChartView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		/*获取我们自定义的样式属性
		 TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyChartView, defStyleAttr, 0);
		 int n = array.getIndexCount();
		 for (int i = 0; i < n; i++) {
		 int attr = array.getIndex(i);
		 switch (attr) {
		 case R.styleable.MyChartView_leftColor:
		 // 默认颜色设置为黑色
		 leftColor = array.getColor(attr, Color.BLACK);
		 break;
		 case R.styleable.MyChartView_selectLeftColor:
		 // 默认颜色设置为黑色
		 selectLeftColor = array.getColor(attr, Color.BLACK);
		 break;
		 case R.styleable.MyChartView_rightColor:
		 rightColor = array.getColor(attr, Color.BLACK);
		 break;
		 case R.styleable.MyChartView_selectRightColor:
		 selectRightColor = array.getColor(attr, Color.BLACK);
		 break;
		 case R.styleable.MyChartView_xyColor:
		 lineColor = array.getColor(attr, Color.BLACK);
		 break;
		 case R.styleable.MyChartView_leftColorBottom:
		 leftColorBottom = array.getColor(attr, Color.BLACK);
		 break;
		 case R.styleable.MyChartView_rightColorBottom:
		 rightColorBottom = array.getColor(attr, Color.BLACK);
		 break;
		 }
		 }
		 array.recycle();*/
		//init();
	}

	//初始化画笔
	private void init()
	{
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mBound = new Rect();
		mChartPaint = new Paint();
		mChartPaint.setAntiAlias(true);
		//mShadowPaint = new Paint();
		//mShadowPaint.setAntiAlias(true);
		//mShadowPaint.setColor(Color.WHITE);
		mWidth = getWidth();
		mHeight = getHeight();
		mStartWidth = getWidth() / (2 * num + 1);
		mSize = mStartWidth;

	}
	/*
	 //测量高宽度
	 @Override
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	 super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	 int width;
	 int height;
	 int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	 int heightSize = MeasureSpec.getSize(heightMeasureSpec);
	 int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	 int heightMode = MeasureSpec.getMode(heightMeasureSpec);

	 if (widthMode == MeasureSpec.EXACTLY) {
	 width = widthSize;
	 } else {
	 width = widthSize * 1 / 2;
	 }
	 if (heightMode == MeasureSpec.EXACTLY) {
	 height = heightSize;
	 } else {
	 height = heightSize * 1 / 2;
	 }

	 setMeasuredDimension(width, height);
	 }
	 */
	/*计算高度宽度
	 @Override
	 protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
	 super.onLayout(changed, left, top, right, bottom);
	 mWidth = getWidth();
	 mHeight = getHeight();
	 mStartWidth = getWidth() / (2*num+1);
	 mSize = mStartWidth;
	 //mChartWidth = getWidth() / num- mSize;
	 }*/

	//重写onDraw绘制
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		init();
		if (map != null)
		{
			Set<Map.Entry<String, Float>> entrySet = map.entrySet();
			Iterator<Map.Entry<String, Float>> iterator = entrySet.iterator();
			int i = 0;
			while (iterator.hasNext())
			{
				Map.Entry<String, Float> entry = iterator.next();
				String kinds = entry.getKey();
				float money= entry.getValue();
				//画横坐标
				mPaint.setColor(Color.BLACK);
				mPaint.setTextSize(35);
				mPaint.setTextAlign(Paint.Align.CENTER);
				mPaint.getTextBounds(kinds, 0, kinds.length(), mBound);
				canvas.drawText(kinds, mStartWidth + mSize / 2, mHeight - 30, mPaint);

				if (i == 0)
					max = money;
				float columnHeight= money / max * 500;

				//画柱状图
				//mPaint.setStyle(Paint.Style.FILL);
				mChartPaint.setColor(Color.CYAN);
				RectF rectF = new RectF();
				rectF.left = mStartWidth;
				rectF.right = mStartWidth + mSize;
				rectF.bottom = mHeight - 100;
				//rectF.top = (float) ((mHeight - 100 - columnHeight * animatedValue / animatedEnd)*0.90f);
				rectF.top = (mHeight - 100 - columnHeight * animatedValue / animatedEnd)*1.0f;
				canvas.drawRoundRect(rectF, 10, 10, mChartPaint);
				mPaint.setColor(Color.RED);
				canvas.drawText("¥" + money, mStartWidth + mSize / 2, rectF.top - 10, mPaint);

				mStartWidth += mSize * 2;

				i++;
			}
		}
	}
	public void setDataMap(LinkedHashMap<String, Float> map)
	{
		this.map = map;
		num = map.size();
		//this.kindsMap = map;
        //sum = getSum(map);
    }
	public void startDraw()
	{
        if (map != null)
		{
            initAnimator();
        }
    }

    private void initAnimator()
	{
        ValueAnimator anim = ValueAnimator.ofFloat(0, animatedEnd);
        anim.setDuration(1500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator)
				{
					animatedValue = (float) valueAnimator.getAnimatedValue();
					invalidate();
				}
			});
        anim.start();
    }
	/*
	 @Override
	 public void onWindowFocusChanged(boolean hasWindowFocus) {
	 super.onWindowFocusChanged(hasWindowFocus);
	 if (hasWindowFocus) {

	 }
	 }

	 /**
	 * 注意:
	 * 当屏幕焦点变化时重新侧向起始位置,必

	 @Override
	 protected void onWindowVisibilityChanged(int visibility) {
	 super.onWindowVisibilityChanged(visibility);
	 if (visibility == VISIBLE) {
	 mSize = getWidth() / 39;
	 mStartWidth = getWidth() / 13;
	 mChartWidth = getWidth() / 13 - mSize - 3;
	 }
	 }

	 /**
	 * 柱状图touch事件
	 * 获取触摸位置计算属于哪个月份的
	 * @param ev
	 * @return
	 */
	/*@Override
	 public boolean onTouchEvent(MotionEvent ev) {

	 int x = (int) ev.getX();
	 int y = (int) ev.getY();
	 int left = 0;
	 int top = 0;
	 int right = mWidth / 12;
	 int bottom = mHeight - 100;
	 switch (ev.getAction()) {
	 case MotionEvent.ACTION_DOWN:
	 for (int i = 0; i < 12; i++) {
	 rect = new Rect(left, top, right, bottom);
	 left += mWidth / 12;
	 right += mWidth / 12;
	 if (rect.contains(x, y)) {
	 listener.getNumber(i, x, y);
	 number = i;
	 selectIndex = i;
	 selectIndexRoles.clear();
	 ;
	 selectIndexRoles.add(selectIndex * 2 + 1);
	 selectIndexRoles.add(selectIndex * 2);
	 invalidate();
	 }
	 }
	 break;
	 case MotionEvent.ACTION_UP:

	 break;
	 }
	 return true;
	 }

	 public interface getNumberListener {
	 void getNumber(int number, int x, int y);
	 }

	 public int getLeftColor() {
	 return leftColor;
	 }

	 public void setLeftColor(int leftColor) {
	 this.leftColor = leftColor;
	 }

	 public int getRightColor() {
	 return rightColor;
	 }

	 public void setRightColor(int rightColor) {
	 this.rightColor = rightColor;
	 }

	 public int getLineColor() {
	 return lineColor;
	 }

	 public void setLineColor(int lineColor) {
	 this.lineColor = lineColor;
	 }

	 public int getSelectLeftColor() {
	 return selectLeftColor;
	 }

	 public void setSelectLeftColor(int selectLeftColor) {
	 this.selectLeftColor = selectLeftColor;
	 }

	 public int getSelectRightColor() {
	 return selectRightColor;
	 }

	 public void setSelectRightColor(int selectRightColor) {
	 this.selectRightColor = selectRightColor;
	 }

	 public int getLeftColorBottom() {
	 return leftColorBottom;
	 }

	 public void setLeftColorBottom(int leftColorBottom) {
	 this.leftColorBottom = leftColorBottom;
	 }

	 public int getRightColorBottom() {
	 return rightColorBottom;
	 }

	 public void setRightColorBottom(int rightColorBottom) {
	 this.rightColorBottom = rightColorBottom;
	 }*/
}
