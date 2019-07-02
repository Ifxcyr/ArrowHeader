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
 * @github https://github.com/wuyr/
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
