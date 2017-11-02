package com.hs.startandlayoutdemo;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * 作者：twopai on 2017/11/2.
 * 邮箱：twopai@hotmail.com
 */
/**
 * 实现类似iOS拍照的按钮滑动功能,这里就以最近项目中用到的开始、暂停、停止为准
 * 需求分析：
 * 1：进入项目就只有暂停按钮居中，
 * 2：点击暂停按钮，暂停按钮消失，开始和停止按钮向两边滑动至屏幕的（1/6-按钮宽度的一半）和（5/6-按钮宽度的一半）
 * 3：点击开始按钮，开始按钮和停止按钮向中间滑动，当重合时显示，暂停按钮显示
 */
public class StartAndLayout extends RelativeLayout {
    private View mStartButton;//开始按钮
    private View mPauseButton;//暂停按钮
    private View mStopButton;//停止按钮
    private int mScreenWidth=0;//屏幕宽度
    private int mCenterXOne;
    private int mCenterXTwo;
    private int mCenterXThree;
    private final int START_TAG=0;//开始tag
    private final int PAUSE_TAG=1;//暂停tag
    private int mStartX;//开始按钮的位置
    private float mPauseX;//暂停按钮位置
    private int mStopX;//停止按钮的位置

    public StartAndLayout(Context context) {
        this(context,null);
    }

    public StartAndLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public StartAndLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenWidth =getScreenWidth(context);
        //将屏幕宽分为三等分，设置按钮start、pause、stop分别为一等分中点、二等分中点、三等分中点
        mCenterXOne =mScreenWidth/6;
        mCenterXTwo =mScreenWidth/2;
        mCenterXThree =mScreenWidth*5/6;
    }

    /**
     * 待布局加载完成获取子view  onFinishInflate->onMeasure->onLayout->onDraw
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount != 3) {
            throw new RuntimeException("下一级子view必须为三个");
        }
        mStartButton = getChildAt(0);
        mPauseButton = getChildAt(1);
        mStopButton = getChildAt(2);
        //理论上来说只是加载布局，不应该有操作，但下面这两行就加在这里
        mStartButton.setVisibility(GONE);
        mStopButton.setVisibility(GONE);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mPauseButton.setX(mCenterXTwo-mPauseButton.getMeasuredWidth()/2);
        mPauseButton.requestLayout();//此方法很重要，刷新位置，不设置，控件不会动

        mStartButton.setTag(START_TAG);
        mPauseButton.setTag(PAUSE_TAG);
        mStartButton.setOnClickListener(onClickListener);
        mPauseButton.setOnClickListener(onClickListener);
        //获取三个按钮位置
        mPauseX =mPauseButton.getX();
        mStartX =mCenterXOne-mPauseButton.getMeasuredWidth()/2;
        mStopX =mCenterXThree-mPauseButton.getMeasuredWidth()/2;
    }

    /**
     * 获取屏幕宽度
     * @param context 上下文
     */
    public int getScreenWidth(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
    private OnClickListener onClickListener=new OnClickListener() {
        @Override
        public void onClick(View view) {
            int tag = (int) view.getTag();
            switch (tag){
                case START_TAG:
                    initStart();
                    break;
                case PAUSE_TAG:
                    initPause();
                    break;
            }
        }
    };

    /**
     * 开始
     */
    private void initStart() {
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator animatorStart=ValueAnimator.ofFloat(mStartX,mPauseX);
        animatorStart.setDuration(500);
        animatorStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                mStartButton.setX(animatedValue);
                mStartButton.requestLayout();
                if (animatedValue == mPauseX) {
                    showPauseButton();
                }
            }
        });
        ValueAnimator animatorStop=ValueAnimator.ofFloat(mStopX,mPauseX);
        animatorStop.setDuration(500);
        animatorStop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                mStopButton.setX(animatedValue);
                mStopButton.requestLayout();
            }
        });
        animatorSet.play(animatorStart).with(animatorStop);
        animatorSet.start();
    }

    /**
     * 暂停
     */
    private void initPause() {
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator animatorStart=ValueAnimator.ofFloat(mPauseX,mStartX);
        animatorStart.setDuration(500);
        animatorStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                hidePauseButton();
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                mStartButton.setX(animatedValue);
                mStartButton.requestLayout();
            }
        });
        ValueAnimator animatorStop=ValueAnimator.ofFloat(mPauseX,mStopX);
        animatorStop.setDuration(500);
        animatorStop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                mStopButton.setX(animatedValue);
                mStopButton.requestLayout();
            }
        });
        animatorSet.play(animatorStart).with(animatorStop);
        animatorSet.start();
    }

    private void showPauseButton() {
        mStartButton.setVisibility(GONE);
        mPauseButton.setVisibility(VISIBLE);
        mStopButton.setVisibility(GONE);
    }

    private void hidePauseButton() {
        mStartButton.setVisibility(VISIBLE);
        mPauseButton.setVisibility(GONE);
        mStopButton.setVisibility(VISIBLE);
    }
}
