package com.empty.launchredpacket;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by wulei on 15/12/25.
 */
public class LaunchRedPacketLayout extends RelativeLayout {
    private Drawable drawable;
    private int dWidth;
    private int dHeight;
    private int mWidth;
    private int mHeight;
    int x, y;

    /**
     * 插值器组
     */
    private Interpolator[] interpolatorsArray;

    private Random random;

    public LaunchRedPacketLayout(Context context) {
        super(context);
        init();
    }

    public LaunchRedPacketLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        drawable = getResources().getDrawable(R.drawable.red_packet);
        dWidth = drawable.getIntrinsicWidth();
        dHeight = drawable.getIntrinsicHeight();

        random = new Random();

        interpolatorsArray = new Interpolator[4];
        interpolatorsArray[0] = new LinearInterpolator();
        interpolatorsArray[1] = new AccelerateInterpolator();
        interpolatorsArray[2] = new DecelerateInterpolator();
        interpolatorsArray[3] = new AccelerateDecelerateInterpolator();

        post(new Runnable() {
            @Override
            public void run() {
                mHeight = getMeasuredHeight();
                mWidth = getMeasuredWidth();

                int curWidth = dWidth;
                dWidth = mWidth / 5;
                dHeight = dHeight * dWidth / curWidth;
            }
        });
    }


    /**
     * 发射多个红包
     *
     * @param numb
     */
    public void launch(int numb) throws Exception {
        for (int i = 0; i < numb; i++)
            launch();
    }

    /**
     * 发射红包
     */
    public void launch() throws Exception {
        final RedPacketView imageView = new RedPacketView(getContext());
        imageView.setImageDrawable(drawable);

        //设置位置
        LayoutParams layoutParams = new LayoutParams(dWidth, dHeight);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        layoutParams.addRule(CENTER_HORIZONTAL, TRUE);
        imageView.setLayoutParams(layoutParams);

        final Animator set = addAnimatior(imageView);

        imageView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                x = (int) imageView.getX();
                y = (int) imageView.getY();

                if (!imageView.isTouch) {
                    imageView.isTouch = true;
                    set.end();
                }

                if (MotionEvent.ACTION_UP == event.getAction()) {
                    if (imageView.movable) {
                        ObjectAnimator.ofFloat(imageView, View.ALPHA, 1f).start();
                        AnimatorSet setDown = new AnimatorSet();
                        setDown.playTogether(
                                ObjectAnimator.ofFloat(imageView, "scaleX", 0.8f, 1.5f),
                                ObjectAnimator.ofFloat(imageView, "scaleY", 0.8f, 1.5f)
                        );
                        setDown.start();

                        imageView.movable = false;
                    }
                }

                return false;
            }
        });

        addView(imageView);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                // 动画结束移除view
                if (imageView.isTouch) {
                    imageView.setX(x);
                    imageView.setY(y);
                } else {
                    removeView(imageView);
                }
            }
        });
        set.start();
    }

    /**
     * 设置动画
     *
     * @param target
     */
    private Animator addAnimatior(View target) throws Exception {
        AnimatorSet set = new AnimatorSet();
        AnimatorSet enterSet = getEnterSet(target);

        ValueAnimator bezierValueAnimator = getBSEValueAnimator(target);
        set.playSequentially(enterSet, bezierValueAnimator);
        set.setInterpolator(interpolatorsArray[random.nextInt(4)]);
        set.setTarget(target);
        return set;
    }

    private class BSEListenr implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BSEListenr(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
        }
    }

    /**
     * 设置贝赛尔曲线动画
     *
     * @param target
     * @return
     */
    private ValueAnimator getBSEValueAnimator(View target) {
        //贝赛尔估值器
        BSEEvaluator evaluator = new BSEEvaluator(getPoint(), getPoint());
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF((mWidth - dWidth) / 2, mHeight - dHeight), new PointF(random.nextInt(mWidth), 0));
        animator.addUpdateListener(new BSEListenr(target));
        animator.setTarget(target);
        animator.setDuration(3000);
        return animator;
    }

    private PointF getPoint() {
        PointF pointF = new PointF();
        pointF.x = random.nextInt(mWidth);
        pointF.y = random.nextInt(mHeight - dHeight);
        return pointF;
    }


    /**
     * 估值器
     */
    static class BSEEvaluator implements TypeEvaluator<PointF> {
        private PointF pointF1;
        private PointF pointF2;

        public BSEEvaluator(PointF pointF1, PointF pointF2) {
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            PointF pointF = new PointF();

            float lFraction = 1 - fraction;

            pointF.x = (float) (startValue.x * Math.pow(lFraction, 3) +
                    3 * pointF1.x * fraction * Math.pow(lFraction, 2) +
                    3 * pointF2.x * Math.pow(lFraction, 2) * fraction +
                    endValue.x * Math.pow(fraction, 3));
            pointF.y = (float) (startValue.y * Math.pow(lFraction, 3) +
                    3 * pointF1.y * fraction * Math.pow(lFraction, 2) +
                    3 * pointF2.y * Math.pow(fraction, 2) * lFraction +
                    endValue.y * Math.pow(fraction, 3));

            return pointF;
        }
    }

    /**
     * 入场动画
     *
     * @param target
     * @return
     */
    private AnimatorSet getEnterSet(View target) {
        try {
            AnimatorSet enterSet = new AnimatorSet();

            enterSet.playTogether(
                    ObjectAnimator.ofFloat(target, View.ALPHA, 0, 1f),
                    ObjectAnimator.ofFloat(target, View.SCALE_X, 0.1f, 0.8f),
                    ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.1f, 0.8f)
            );
            enterSet.setDuration(500);
            enterSet.setInterpolator(new LinearInterpolator());
            enterSet.setTarget(target);

            return enterSet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
