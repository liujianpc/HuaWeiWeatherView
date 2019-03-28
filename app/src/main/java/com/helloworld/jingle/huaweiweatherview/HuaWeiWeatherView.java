package com.helloworld.jingle.huaweiweatherview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liujian
 * @date 2017/10/15
 */

public class HuaWeiWeatherView extends View {
    private int mLen;
    private RectF mRectF;
    private Paint mPaint;
    private boolean mUseCenter = false;
    private float mStartAngle = 120;
    private float mSweepAngle = 300;
    private float mTargetAngle = 200;
    private int mRadius;
    private boolean mIsRunning;
    private int mState = 1;
    private int mScore;
    private int mRed, mGreen, mBlue;
    private OnAngleColorListener mOnAngleColorListener;

    private Paint mLinePaint;
    private Paint mTargetLinePaint;
    private Paint mSmallPaint;
    private Paint mTextPaint;

    public HuaWeiWeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(2f);

        mTargetLinePaint = new Paint();
        mTargetLinePaint.setStrokeWidth(2f);
        mTargetLinePaint.setAntiAlias(true);

        mSmallPaint = new Paint();

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mLen = Math.min(width, height);
        mRadius = mLen / 2;
        mRectF = new RectF(0, 0, mLen, mLen);
        setMeasuredDimension(mLen, mLen);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRectF, mStartAngle, mSweepAngle, mUseCenter, mPaint);
        drawLine(canvas);
        drawSmallCircleText(canvas);
    }

    private void drawLine(Canvas canvas) {

        canvas.save();
        canvas.translate(mRadius, mRadius);
        //canvas先转动一个角度
        canvas.rotate(30f);


        float rotateAngle = mSweepAngle / 100;
        float hasDraw = 0;
        //等分圆弧，扫过的区域渐变色，非扫过的区域白色
        //使用百分比实现渐变效果
        for (int i = 0; i < 100; i++) {
            if (hasDraw <= mTargetAngle && mTargetAngle != 0) {
                float percent = hasDraw / mTargetAngle;
                mRed = 250 - (int) (percent * 255);
                mGreen = (int) (percent * 255);
                mBlue = 0;
                if (mOnAngleColorListener != null) {
                    mOnAngleColorListener.colorListener(mRed, mGreen, mBlue);
                }
                mTargetLinePaint.setARGB(255, mRed, mGreen, mBlue);
                canvas.drawLine(0, mRadius, 0, mRadius - 40, mTargetLinePaint);
            } else {
                canvas.drawLine(0, mRadius, 0, mRadius - 40, mLinePaint);
            }
            hasDraw += rotateAngle;
            canvas.rotate(rotateAngle);
        }
        canvas.restore();
    }

    /**
     * 动画效果实现
     *
     * @param trueAngle
     */
    public void changeAngle(final float trueAngle) {
        if (mIsRunning) {
            return;
        }

        final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                switch (mState) {
                    //回退
                    case 1:
                        mIsRunning = true;
                        mTargetAngle -= 3;
                        if (mTargetAngle <= 0) {
                            mTargetAngle = 0;
                            mState = 2;
                        }
                        break;
                    //前进
                    case 2:
                        mTargetAngle += 3;
                        if (mTargetAngle >= trueAngle) {
                            mTargetAngle = trueAngle;
                            mState = 1;
                            mIsRunning = false;
                            executorService.shutdown();
                        }
                        break;
                    default:
                        break;
                }
                mScore = (int) (mTargetAngle / mSweepAngle * 100);
                postInvalidate();
            }
        }, 500, 30, TimeUnit.MILLISECONDS);
        /*final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                switch (mState) {
                    //回退
                    case 1:
                        mIsRunning = true;
                        mTargetAngle -= 3;
                        if (mTargetAngle <= 0) {
                            mTargetAngle = 0;
                            mState = 2;
                        }
                        break;
                    //前进
                    case 2:
                        mTargetAngle += 3;
                        if (mTargetAngle >= trueAngle) {
                            mTargetAngle = trueAngle;
                            mState = 1;
                            mIsRunning = false;
                            timer.cancel();

                        }
                        break;
                }
                mScore = (int)(mTargetAngle / mSweepAngle * 100);
                postInvalidate();

            }
        }, 500, 30);*/

    }

    private void drawSmallCircleText(Canvas canvas) {

        mSmallPaint.setARGB(255, mRed, mGreen, 0);
        int smallRadius = mRadius - 60;
        canvas.drawCircle(mRadius, mRadius, smallRadius, mSmallPaint);


        mTextPaint.setTextSize(smallRadius / 2);
        canvas.drawText("" + mScore, mRadius, mRadius, mTextPaint);
        mTextPaint.setTextSize(smallRadius / 6);
        canvas.drawText("分", mRadius + smallRadius / 2, mRadius - smallRadius / 4, mTextPaint);
        mTextPaint.setTextSize(smallRadius / 6);
        canvas.drawText("点击优化", mRadius, mRadius + smallRadius / 2, mTextPaint);
    }

    public void setOnAngleColorListener(OnAngleColorListener onAngleColorListener) {
        this.mOnAngleColorListener = onAngleColorListener;
    }

    public interface OnAngleColorListener {
        void colorListener(int red, int green, int blue);
    }
}

