package com.sola.module.recycle.library.interfaces;

import android.animation.AnimatorSet;
import android.view.View;


/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/9/25
 */
public interface IRecycleAnimatorListItem extends IRecycleListItem {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 获取每一项加载的时候的动画效果
     *
     * @param view
     * @return
     */
    AnimatorSet getAnimatorSet(View view);

}
