package com.example.xwn.calendarmanager.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.xwn.calendarmanager.R;
import com.example.xwn.calendarmanager.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xwn on 2016/11/30.
 */

public class LcRangeBar extends View{

    private static final String TAG = "LcRangeBar";
    private int minMark;
    private int maxMark;
    private int minValue=0;
    private int maxValue=6;
    private int markBallColor;
    private int markLineColor;
    private int unMarkLineColor;
    private int markBallRadius;
    private int markLineSize;
    private int unMarkLineSize;
    private Context context;
    private int markRange;
    private int mLineLength;
    private int mMidY;
    private int mLineStartX;
    private int mLineEndX;
    private int mMinPosition;
    private int mMaxPosition;
    private Paint mPaint;
    private boolean isOnMinBall;
    private boolean isOnMaxBall;
    private RangeChangeListener mRangeChangeListener;
    private int ballNumber;
    private List<Integer> ballSiteX;
    private int ballSiteXDivider;

    public LcRangeBar(Context context) {
        this(context,null);
    }
    public LcRangeBar(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }
    public LcRangeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        initAttrs(attrs);
        this.context = context;
    }
    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs,
                    R.styleable.LcRangeBar, 0, 0);
            minMark = ta.getInt(R.styleable.LcRangeBar_minMark,
                    6);
            maxMark = ta.getInt(R.styleable.LcRangeBar_maxMark,
                    18);
            ballNumber = ta.getInt(R.styleable.LcRangeBar_ballNumber,6);
            markBallColor = ta.getColor(R.styleable.LcRangeBar_markBallColor,
                    0xffe0e0e0);
            markLineColor = ta.getColor(R.styleable.LcRangeBar_markLineColor,
                    0xff00c62d);
            unMarkLineColor = ta.getColor(
                    R.styleable.LcRangeBar_unMarkLineColor,
                    0xffd8d8d8);
            markBallRadius = (int) ta.getDimension(
                    R.styleable.LcRangeBar_markBallRadius,
                    DensityUtil.dip2px(getContext(),16.0f));
            markLineSize = (int) ta.getDimension(
                    R.styleable.LcRangeBar_markLineSize,
                    DensityUtil.dip2px(getContext(),12.0f));
            unMarkLineSize = (int) ta.getDimension(
                    R.styleable.LcRangeBar_unMarkLineSize,
                    DensityUtil.dip2px(getContext(),12.0f));
            ta.recycle();
        }
        markRange = maxMark - minMark;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int expectedWidth = DensityUtil.dip2px(context,200);
        int expectedHeight = DensityUtil.dip2px(context,30);
        int finalWidth = expectedWidth;
        int finalHeight = expectedHeight;

        if (widthMode == MeasureSpec.EXACTLY) {
            finalWidth = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            finalWidth = expectedWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            finalHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            finalHeight = markBallRadius;
        }

        mLineLength = (finalWidth - markBallRadius * 2);
        Log.d(TAG, "finalWidth: "+finalWidth);
        Log.d(TAG, "markBallRadius: "+markBallRadius);
        mMidY = finalHeight / 2;
        mLineStartX = markBallRadius;
        mLineEndX = mLineLength + markBallRadius;

        ballSiteX = new ArrayList<>();
        ballSiteXDivider = mLineLength / ballNumber;
        ballSiteX.add(mLineStartX);
        if (ballNumber >=2) {
            for (int i = 0; i < ballNumber - 1; i++) {
                ballSiteX.add(mLineStartX+(i + 1) * ballSiteXDivider);
            }
        }
        ballSiteX.add(mLineEndX);
        Log.d(TAG, "ballSiteXDivider:"+ballSiteXDivider);

        mMinPosition = ballSiteX.get(0);
        mMaxPosition = ballSiteX.get(ballSiteX.size()-1);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawUnMarkLine(canvas);
        drawMarkLine(canvas);
        mPaint.setColor(0xffffffff);
        for (int i=0;i<ballSiteX.size();i++){
            canvas.drawCircle(ballSiteX.get(i),mMidY,unMarkLineSize/4,mPaint);
        }
        drawMarkBalls(canvas);
    }

    private void drawMarkBalls(Canvas canvas) {
        mPaint.setColor(markBallColor);
        canvas.drawCircle(mMinPosition, mMidY, markBallRadius, mPaint);
        canvas.drawCircle(mMaxPosition, mMidY, markBallRadius, mPaint);
    }

    private void drawMarkLine(Canvas canvas) {
        mPaint.setColor(markLineColor);
        mPaint.setStrokeWidth(markLineSize);
        canvas.drawLine(mMinPosition, mMidY, mMaxPosition, mMidY, mPaint);
    }

    private void drawUnMarkLine(Canvas canvas) {
        mPaint.setColor(unMarkLineColor);
        mPaint.setStrokeWidth(unMarkLineSize);
        canvas.drawLine(mLineStartX, mMidY, mLineEndX, mMidY, mPaint);
        canvas.drawCircle(mLineStartX, mMidY,unMarkLineSize/2,mPaint);
        canvas.drawCircle(mLineEndX, mMidY,unMarkLineSize/2,mPaint);


    }
    private boolean isTouchingMaxBall(MotionEvent event) {
        return event.getX() > mMaxPosition - markBallRadius
                && event.getX() < mMaxPosition + markBallRadius
                && event.getY() > mMidY - markBallRadius
                && event.getY() < mMidY + markBallRadius;
    }

    private boolean isTouchingMinBall(MotionEvent event) {
        return event.getX() > mMinPosition - markBallRadius
                && event.getX() < mMinPosition + markBallRadius
                && event.getY() > mMidY - markBallRadius
                && event.getY() < mMidY + markBallRadius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isTouchingMinBall(event)) {
                    isOnMinBall = true;
                } else if (isTouchingMaxBall(event)) {
                    isOnMaxBall =  true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOnMinBall) {
                    moveToMinPosition(event);
                }
                if (isOnMaxBall) {
                    moveToMaxPosition(event);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isOnMinBall) {
                    isOnMinBall = false;
                }
                if (isOnMaxBall) {
                    isOnMaxBall = false;
                }
                break;
        }

        return true;
    }
    private void moveToMinPosition(MotionEvent event) {
        if (event.getX() < mMaxPosition && event.getX() >= mLineStartX&&((mMaxPosition-event.getX())>ballSiteXDivider)) {
            for (int i = 0;i<ballSiteX.size();i++){
                if (event.getX()<ballSiteX.get(i)+ballSiteXDivider/2){
                    mMinPosition = ballSiteX.get(i);
                    minValue = i;
                    break;
                }
            }
            Log.d(TAG, "minValue: "+minValue+"\nmaxValue："+maxValue);
            invalidate();
            /** 配合 10 一起看，这个必须判断是否为空，如果调用者不监听会导致空指针异常
             if (mRangeChangeListener != null) {
             mRangeChangeListener.onMinChange(Math
             .round((float) (mMinPosition - mLineStartX)
             / mLineLength * markRange));
             }
             **/
        }
    }

    private void moveToMaxPosition(MotionEvent event) {
        if (event.getX() > mMinPosition && event.getX() <= mLineEndX&&((event.getX()-mMinPosition)>ballSiteXDivider)) {
            for (int i = 0;i<ballSiteX.size();i++){
                if (event.getX()<ballSiteX.get(i)+ballSiteXDivider/2){
                    mMaxPosition = ballSiteX.get(i);
                    maxValue = i;
                    break;
                }
            }
            Log.d(TAG, "minValue: "+minValue+"\nmaxValue："+maxValue);
            invalidate();
            /** 配合 10 一起看
             if (mRangeChangeListener != null) {
             mRangeChangeListener.onMaxChange(Math
             .round((float) (mMaxPosition - mLineStartX)
             / mLineLength * markRange));
             }
             **/
        }
    }
    public interface RangeChangeListener {
        void onMinChange(int minValue);
        void onMaxChange(int maxValue);
    }

    public void setRangeChangeListener(RangeChangeListener rangeChangeListener) {
        mRangeChangeListener = rangeChangeListener;
    }
    public int getMinValue(){
        return minValue;
    }
    public int getMaxValue(){
        return maxValue;
    }
}
