package com.sola.module.recycle.fix_container.tools;

import android.view.View;

import com.sola.module.recycle.fix_container.RecyclerViewRefreshContainerBase;

/**
 * Description:
 * <p/>
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
     * <p/>}
     */
    boolean checkCanDoRefresh(final RecyclerViewRefreshContainerBase frame, final View content, final View header);

    /**
     * When refresh begin
     */
    void onRefreshBegin(final RecyclerViewRefreshContainerBase frame);
}
