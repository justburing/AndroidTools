package com.burning.smile.androidtools.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

/**
 * Created by Burning on 2016/2/22.
 */
public class AndroidAnimate {
    public final static int TO_LEFT =0;
    public final static int TO_UP =1;
    public final static int TO_RIGHT =2;
    public final static int TO_DOWN =3;

    //淡入
    public static void fadeInView(View view){
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(500).setListener(null);
    }
    //淡出
    public static void fadeOutView(final View view){
        view.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                super.onAnimationEnd(animation);
            }
        });
    }
    //移动
    public static void moveOutView(final View view,int deraction){
        AnimationSet aset = new AnimationSet(true);
        //初始化 Alpha动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 0f);
        TranslateAnimation trans;
        switch (deraction){
            case TO_LEFT://left
                trans = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,-10,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0);
                break;
            case TO_UP://up
                trans = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,10);
                break;
            case TO_RIGHT://right
                trans = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,10,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0);
                break;
            case TO_DOWN://down
                trans = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,-10);
                break;
            default:
                break;

        }
        trans = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,10,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0);
        trans.setDuration(100);
        aset.addAnimation(trans);
        aset.addAnimation(alphaAnimation);
        aset.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(aset);
    }

    public static void startPropertyAnimation(final View target, final int startValue, final int endValue){
        final IntEvaluator intEvaluator=new IntEvaluator();
        //将动画值限定在(1,100)之间
        ValueAnimator valueAnimator=ValueAnimator.ofInt(1,100);
        //动画持续时间
        valueAnimator.setDuration(5000);
        //监听动画的执行
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //得到当前瞬时的动画值,在(1,100)之间
                Integer currentAnimatedValue = (Integer) valueAnimator.getAnimatedValue();
                //计算得到当前系数fraction
                float fraction = currentAnimatedValue / 100f;
                System.out.println("currentAnimatedValue=" + currentAnimatedValue + ",fraction=" + fraction);
                //评估出当前的宽度其设置
                target.getLayoutParams().width = intEvaluator.evaluate(fraction, startValue, endValue);
                target.requestLayout();
            }
        });
        //开始动画
        valueAnimator.start();
    }
}
