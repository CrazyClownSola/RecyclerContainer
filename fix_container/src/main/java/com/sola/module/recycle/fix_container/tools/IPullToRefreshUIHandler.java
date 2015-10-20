package com.sola.module.recycle.fix_container.tools;

import com.sola.module.recycle.fix_container.RecyclerViewRefreshContainerBase;

/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/10/16
 */
public interface IPullToRefreshUIHandler {
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
     * When the content view has reached top and refresh has been completed, view will be reset.
     */
    void onUIReset(RecyclerViewRefreshContainerBase frame);

    /**
     * prepare for loading
     */
    void onUIRefreshPrepare(RecyclerViewRefreshContainerBase frame);

    /**
     * perform refreshing UI
     */
    void onUIRefreshBegin(RecyclerViewRefreshContainerBase frame);

    /**
     * perform UI after refresh
     */
    void onUIRefreshComplete(RecyclerViewRefreshContainerBase frame);

    /**
     * 一般用于判断 当前拖动的位置是否超越警戒线
     */
    void onUIPositionChange(RecyclerViewRefreshContainerBase frame,
                            boolean isUnderTouch,
                            byte status,
                            int currentPos,
                            int lastPos,
                            int offsetHeight);

}
