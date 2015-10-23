package com.sola.module.recycle.fix_container.tools;

import android.view.View;

import com.sola.module.recycle.fix_container.RecyclerViewRefreshContainerBase;

/**
 * Description:
 * author: Sola
 * 2015/10/16
 */
public interface IPullToRefreshHandler {
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
     * Check can do refresh or not. For example the content is empty or the first child is in view.
     *
     * @param frame   界面
     * @param content 多数为RecyclerView
     * @param header  自定义的HeaderView
     * @return 是否能进行更新
     */
    boolean checkCanDoRefresh(final RecyclerViewRefreshContainerBase frame, final View content, final View header);

    /**
     * When refresh begin
     *
     * @param frame 界面
     */
    void onRefreshBegin(final RecyclerViewRefreshContainerBase frame);
}
