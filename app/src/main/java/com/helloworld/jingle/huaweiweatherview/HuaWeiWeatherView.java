package com.helloworld.jingle.huaweiweatherview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liujian on 2017/10/15.
 */

public class HuaWeiWeatherView extends View {
    private int len;
    private RectF oval;
    private Paint paint;
    private boolean useCenter = false;
    private float startAngle = 120;
    private float sweepAngle = 300;
    private float targetAngle = 200;
    private int radius;
    private boolean isRunning;
    private int state = 1;
    private int score;
    private int red,green;
    private OnAngleColorListener onAngleColorListener;

    public HuaWeiWeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        len = Math.min(width, height);
        radius = len / 2;
        oval = new RectF(0, 0, len, len);
        setMeasuredDimension(len, len);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(oval, startAngle, sweepAngle, useCenter, paint);
        drawLine(canvas);
        drawSmallCircleText(canvas);
    }

    private void drawLine(Canvas canvas) {

        canvas.save();
        canvas.translate(radius, radius);
        canvas.rotate(30f);
        Paint linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(2f);

        Paint targetLinePaint = new Paint();
        targetLinePaint.setStrokeWidth(2f);
        targetLinePaint.setAntiAlias(true);
        float rotateAngle = sweepAngle / 100;
        float hasDraw = 0;
        for (int i = 0; i < 100; i++) {
            if (hasDraw <= targetAngle && targetAngle != 0) {
                float percent = hasDraw / targetAngle;
                 red = 250 - (int) (percent * 255);
                 green = (int) (percent * 255);
                if (onAngleColorListener != null){
                    onAngleColorListener.colorListener(red,green);
                }
                targetLinePaint.setARGB(255, red, green, 0);
                canvas.drawLine(0, radius, 0, radius - 40, targetLinePaint);
            } else {
                canvas.drawLine(0, radius, 0, radius - 40, linePaint);
            }
            hasDraw += rotateAngle;
            canvas.rotate(rotateAngle);
        }
        canvas.restore();
    }

    public void chageAngle(final float trueAngle) {
        if (isRunning) {
            return;
        }
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                switch (state) {
                    case 1:
                        isRunning = true;
                        targetAngle -= 3;
                        if (targetAngle <= 0) {
                            targetAngle = 0;
                            state = 2;
                        }
                        break;
                    case 2:
                        targetAngle += 3;
                        if (targetAngle >= trueAngle) {
                            targetAngle = trueAngle;
                            state = 1;
                            isRunning = false;
                            timer.cancel();

                        }
                        break;
                }
                score = (int)(targetAngle / sweepAngle * 100);
                postInvalidate();

            }
        }, 500, 30);

    }

    private void drawSmallCircleText(Canvas canvas) {
        Paint smallPaint = new Paint();
        smallPaint.setARGB(255, red, green, 0);
        int smallRadius = radius - 60;
        canvas.drawCircle(radius,radius,smallRadius,smallPaint);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(smallRadius / 2);
        canvas.drawText(""+score,radius,radius,textPaint);
        textPaint.setTextSize(smallRadius/6);
        canvas.drawText("分",radius+smallRadius/2,radius-smallRadius/4,textPaint);
        textPaint.setTextSize(smallRadius/6);
        canvas.drawText("点击优化",radius,radius+smallRadius/2,textPaint);
    }

    public void setOnAngleColorListener(OnAngleColorListener onAngleColorListener) {
        this.onAngleColorListener = onAngleColorListener;
    }

    public interface OnAngleColorListener{
        void colorListener(int red, int green);
    }
}

