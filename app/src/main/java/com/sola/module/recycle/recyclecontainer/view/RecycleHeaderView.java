package com.sola.module.recycle.recyclecontainer.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sola.module.recycle.recyclecontainer.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/10/13
 */
@EViewGroup(R.layout.layout_progress_bar)
public class RecycleHeaderView extends LinearLayout implements PtrUIHandler {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    @ViewById
    ImageView id_image_loading_inner;

    @ViewById
    ImageView id_image_loading;

    @ViewById
    TextView id_text_progress_message;

    AnimatorSet set;

    Animator rotation;

    RotateAnimation mFlipAnimation;

    // ===========================================================
    // Constructors
    // ===========================================================
    public RecycleHeaderView(Context context) {
        this(context, null);
    }

    public RecycleHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        rotation = ObjectAnimator.ofFloat(id_image_loading, "rotation", 0, 360);
//        ((ObjectAnimator) rotation).setRepeatCount(ValueAnimator.INFINITE);
//        set = new AnimatorSet();
//        set.play(rotation);
//        set.setInterpolator(new LinearInterpolator());
//        set.setStartDelay(0);
//        set.setDuration(4000);

        mFlipAnimation = new RotateAnimation(0.0F, 360.0F, 1, 0.5F, 1, 0.5F);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(1500);
        mFlipAnimation.setFillAfter(true);
        mFlipAnimation.setRepeatCount(Animation.INFINITE);

    }
    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public void onUIReset(PtrFrameLayout ptrFrameLayout) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout ptrFrameLayout) {
        id_image_loading.setVisibility(View.INVISIBLE);
        id_text_progress_message.setText("下拉即可刷新");
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        id_image_loading.setVisibility(View.VISIBLE);
        id_text_progress_message.setText("正在加载，请稍候");
//        set.start();
        id_image_loading.clearAnimation();
        id_image_loading.startAnimation(mFlipAnimation);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout ptrFrameLayout) {
        id_image_loading.setVisibility(View.INVISIBLE);
        id_text_progress_message.setText("更新完毕");
//        set.cancel();
        id_image_loading.clearAnimation();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        int mOffsetToRefresh = frame.getOffsetToRefresh();
        int currentPos = ptrIndicator.getCurrentPosY();
        int lastPos = ptrIndicator.getLastPosY();
        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == 2) {
                this.crossRotateLineFromBottomUnderTouch(frame);

            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh && isUnderTouch && status == 2) {
            this.crossRotateLineFromTopUnderTouch(frame);
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================
    private void crossRotateLineFromBottomUnderTouch(PtrFrameLayout frame) {
        id_image_loading.setVisibility(View.INVISIBLE);
        if (frame.isPullToRefresh()) {
            id_text_progress_message.setText("下拉即可刷新");
        } else {
            id_text_progress_message.setText("往下拉");
        }
    }

    private void crossRotateLineFromTopUnderTouch(PtrFrameLayout frame) {
        id_image_loading.setVisibility(View.INVISIBLE);
        if (!frame.isPullToRefresh()) {
            id_text_progress_message.setText("松开即可更新");
        }

    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
