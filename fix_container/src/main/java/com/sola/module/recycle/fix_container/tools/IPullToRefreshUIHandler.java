package com.sola.module.recycle.fix_container.tools;

import com.sola.module.recycle.fix_container.RecyclerContainerBase;

/**
 * Description:
 *
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
     *
     * @param frame 界面
     */
    void onUIReset(RecyclerContainerBase frame);

    /**
     * prepare for loading
     *
     * @param frame 界面
     */
    void onUIRefreshPrepare(RecyclerContainerBase frame);

    /**
     * perform refreshing UI
     *
     * @param frame 界面
     */
    void onUIRefreshBegin(RecyclerContainerBase frame);

    /**
     * perform UI after refresh
     *
     * @param frame 界面
     */
    void onUIRefreshComplete(RecyclerContainerBase frame);

    /**
     * 一般用于判断 当前拖动的位置是否超越警戒线
     *
     * @param frame        界面
     * @param isUnderTouch 是否在touch事件中
     * @param status       状态
     * @param currentPos   当前位置
     * @param lastPos      前一个点击的位置
     * @param offsetHeight Header最大便宜高度
     */
    void onUIPositionChange(RecyclerContainerBase frame,
                            boolean isUnderTouch,
                            byte status,
                            int currentPos,
                            int lastPos,
                            int offsetHeight);

}
