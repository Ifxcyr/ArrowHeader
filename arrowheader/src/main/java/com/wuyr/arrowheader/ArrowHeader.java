package com.wuyr.arrowheader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.wuyr.arrowdrawable.ArrowDrawable;

/**
 * @author wuyr
 * @github https://github.com/wuyr/ArrowDrawable
 * @since 2019-07-02 下午2:32
 */
public class ArrowHeader extends View implements RefreshHeader {

    private ArrowDrawable mDrawable;
    private RefreshState state = RefreshState.None;
    private int bowColor;
    private int arrowColor;
    private int stringColor;
    private int lineColor;
    private int bowLength;

    public ArrowHeader(Context context) {
        this(context, null);
    }

    public ArrowHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArrowHeader, defStyleAttr, 0);
        bowColor = a.getColor(R.styleable.ArrowHeader_bowColor, 0);
        arrowColor = a.getColor(R.styleable.ArrowHeader_arrowColor, 0);
        stringColor = a.getColor(R.styleable.ArrowHeader_stringColor, 0);
        lineColor = a.getColor(R.styleable.ArrowHeader_lineColor, 0);
        bowLength = a.getDimensionPixelSize(R.styleable.ArrowHeader_bowLength, 0);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mDrawable.draw(canvas);
        if (state != RefreshState.None) {
            invalidate();
        }
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mDrawable = ArrowDrawable.create(this, getMeasuredWidth(), getMeasuredHeight(),
                (int) (bowLength > 0 ? bowLength : getMeasuredWidth() * .3F));
        if (bowColor != 0) {
            mDrawable.setBowColor(bowColor);
        }
        if (arrowColor != 0) {
            mDrawable.setArrowColor(arrowColor);
        }
        if (stringColor != 0) {
            mDrawable.setStringColor(stringColor);
        }
        if (lineColor != 0) {
            mDrawable.setLineColor(lineColor);
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        if (success) {
            mDrawable.hit();
            return getHitAnimationDuration();
        } else {
            mDrawable.miss();
            return getMissAnimationDuration();
        }
    }

    private int getHitAnimationDuration() {
        return (int) (mDrawable.getHitDuration() + (mDrawable.getSkewDuration() * 8) + 400/*400ms停留时长*/);
    }

    private int getMissAnimationDuration() {
        return ((int) mDrawable.getMissDuration()) + 100;/*100ms停留时长*/
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        mDrawable.fire();
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        state = newState;
        if (newState == RefreshState.None) {
            mDrawable.reset();
        }
        invalidate();
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        float value;
        if (percent <= .5F) {
            //弓出现的时候减速一半
            value = percent / 2;
        } else if (percent <= .75F) {
            //箭出现的时候保持原速度
            value = percent - .25F;
        } else {
            //拉弓的时候加速一半
            value = (percent - .5F) * 2;
        }
        mDrawable.setProgress(value);
    }

    /**
     * 设置弓长
     * @param length 弓长
     */
    public void setBowLength(int length) {
        mDrawable.updateSize(getMeasuredWidth(), getMeasuredHeight(), length);
    }

    /**
     * 设置分解的点密度(单位: px)
     *
     * @param precision 新密度
     */
    public void setPrecision(float precision) {
        mDrawable.setPrecision(precision);
    }

    /**
     * 获取线条的坠落时长
     */
    public int getBaseLinesFallDuration() {
        return mDrawable.getBaseLinesFallDuration();
    }

    /**
     * 设置线条的坠落时长
     */
    public void setBaseLinesFallDuration(int duration) {
        mDrawable.setBaseLinesFallDuration(duration);
    }

    /**
     * 获取发射中的弓向下移动的时长
     */
    public long getFiringBowFallDuration() {
        return mDrawable.getFiringBowFallDuration();
    }

    /**
     * 设置发射中的弓向下移动的时长
     */
    public void setFiringBowFallDuration(long duration) {
        mDrawable.setFiringBowFallDuration(duration);
    }

    /**
     * 获取发射后的箭收缩动画时长
     */
    public long getFiredArrowShrinkDuration() {
        return mDrawable.getFiredArrowShrinkDuration();
    }

    /**
     * 设置发射后的箭收缩动画时长
     */
    public void setFiredArrowShrinkDuration(long duration) {
        mDrawable.setFiredArrowShrinkDuration(duration);
    }

    /**
     * 获取发射后的箭每次上下移动的时长
     */
    public long getFiredArrowMoveDuration() {
        return mDrawable.getFiredArrowMoveDuration();
    }

    /**
     * 设置发射后的箭每次上下移动的时长
     */
    public void setFiredArrowMoveDuration(long duration) {
        mDrawable.setFiredArrowMoveDuration(duration);
    }

    /**
     * 获取未命中动画时长
     */
    public long getMissDuration() {
        return mDrawable.getMissDuration();
    }

    /**
     * 设置未命中动画时长
     */
    public void setMissDuration(long duration) {
        mDrawable.setMissDuration(duration);
    }

    /**
     * 获取命中动画时长
     */
    public long getHitDuration() {
        return mDrawable.getHitDuration();
    }

    /**
     * 设置命中动画时长
     */
    public void setHitDuration(long duration) {
        mDrawable.setHitDuration(duration);
    }

    /**
     * 获取命中后每次左右摆动的时间
     */
    public float getSkewDuration() {
        return mDrawable.getSkewDuration();
    }

    /**
     * 设置命中后每次左右摆动的时间
     */
    public void setSkewDuration(float duration) {
        mDrawable.setSkewDuration(duration);
    }

    /**
     * 获取坠落的线条颜色
     */
    public int getLineColor() {
        return mDrawable.getLineColor();
    }

    /**
     * 设置坠落的线条颜色
     */
    public void setLineColor(int color) {
        mDrawable.setLineColor(color);
    }

    /**
     * 获取弓颜色
     */
    public int getBowColor() {
        return mDrawable.getBowColor();
    }

    /**
     * 设置弓颜色
     */
    public void setBowColor(int color) {
        mDrawable.setBowColor(color);
    }

    /**
     * 获取弦颜色
     */
    public int getStringColor() {
        return mDrawable.getStringColor();
    }

    /**
     * 设置弦颜色
     */
    public void setStringColor(int color) {
        mDrawable.setStringColor(color);
    }

    /**
     * 获取箭颜色
     */
    public int getArrowColor() {
        return mDrawable.getArrowColor();
    }

    /**
     * 设置箭颜色
     */
    public void setArrowColor(int color) {
        mDrawable.setArrowColor(color);
    }

    /**
     * 获取命中后左右摆动的幅度
     */
    public float getSkewTan() {
        return mDrawable.getSkewTan();
    }

    /**
     * 设置命中后左右摆动的幅度(正切值)
     */
    public void setSkewTan(float tan) {
        mDrawable.setSkewTan(tan);
    }

    /**
     * 获取命中后一共要摆动的次数
     */
    public int getMaxSkewCount() {
        return mDrawable.getMaxSkewCount();
    }

    /**
     * 设置命中后一共要摆动的次数
     */
    public void setMaxSkewCount(int count) {
        mDrawable.setMaxSkewCount(count);
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.FixedBehind;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
}
