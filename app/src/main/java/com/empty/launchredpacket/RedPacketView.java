package com.empty.launchredpacket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by wulei on 16/1/19.
 */
public class RedPacketView extends ImageView {
    private Paint mPaint, moneyPaint;
    private Path mPath;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private int x, y, mLastX, mLastY;
    public boolean movable = true;
    public boolean isTouch = false;
    private String[] moneys = new String[]{"￥5", "￥10", "￥20", "￥50"};
    private Rect mTextBound;
    private String mText;
    private Random mRandom;
    private boolean isComplete = false;

    /**
     * 笔触的宽度
     */
    private static final float PAINT_WIDTH = 20;
    /**
     * 默认绘制的最小距离
     */
    private static final float DEFAULT_PATH_INSTANCE = 5;

    public RedPacketView(Context context) {
        super(context);
        init();
    }

    public RedPacketView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RedPacketView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPath = new Path();
        mRandom = new Random();

        initPaint();
        initMoneyPaint();

        //随机产生一个面值
        mText = moneys[mRandom.nextInt(moneys.length)];

        //获取字体的宽高
        moneyPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#c0c0c0"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        /**
         * 设置接合处的形态
         */
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        /**
         * 抗抖动
         */
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(PAINT_WIDTH);
    }

    /**
     * money画笔
     */
    private void initMoneyPaint() {
        moneyPaint = new Paint();
        moneyPaint.setColor(Color.RED);
        moneyPaint.setAntiAlias(true);
        moneyPaint.setTextSize(30);
        mTextBound = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        try {
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

            Bitmap bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.red_packet));

            mCanvas.drawBitmap(bitmap, null, new RectF(0, 0, width, height), null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            canvas.drawText(mText, getWidth() / 2 - mTextBound.width() / 2, getHeight() / 2 + mTextBound.height() / 2, moneyPaint);

            if (isComplete) return;

            //设置图片的结合方式
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            mCanvas.drawPath(mPath, mPaint);

            canvas.drawBitmap(mBitmap, 0, 0, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //路径的初始化位置
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (movable) {
                    // 跟手滑效果
                    setX(x + getLeft() + getTranslationX() - getWidth() / 2);
                    setY(y + getTop() + getTranslationY() - getHeight() / 2);
                } else if (Math.abs(x - mLastX) > DEFAULT_PATH_INSTANCE || Math.abs(y - mLastY) > DEFAULT_PATH_INSTANCE) {
                    // 记录手指擦除路径
                    mPath.lineTo(x, y);
                    invalidate();
                }
            case MotionEvent.ACTION_UP:
                MyAsyncTask task = new MyAsyncTask();
                task.execute();
                break;
        }

        //记录上次位置
        mLastX = x;
        mLastY = y;
        return true;
    }

    /**
     * 查看目前的红包的擦除比例，实现完全擦除
     */
    class MyAsyncTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] params) {
            clearOverPercent();
            return null;
        }

        private void clearOverPercent()
        {
            int[] mPixels;

            int w = getWidth();
            int h = getHeight();

            float wipeArea = 0;
            float totalArea = w * h;

            Bitmap bitmap = Bitmap.createBitmap(mBitmap);

            mPixels = new int[w * h];

            //拿到所有像素信息
            bitmap.getPixels(mPixels, 0, w, 0, 0, w, h);

            //获取擦除部分的面积
            int index = 0;
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    if (mPixels[index] == 0) {
                        wipeArea++;
                    }
                    index++;
                }
            }

            int percent = (int) (wipeArea / totalArea * 100);
            if (percent > 70) {
                isComplete = true;
                postInvalidate();
            }
        }
    };
}
