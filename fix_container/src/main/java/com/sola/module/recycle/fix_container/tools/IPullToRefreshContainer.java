package com.sola.module.recycle.fix_container.tools;

/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/10/16
 */
public interface IPullToRefreshContainer {
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
     * 更新完成
     */
    void refreshComplete();

    /**
     * 开始更新的入口方法
     *
     * @param atOnce 是否是一次性的
     */
    void autoRefresh(boolean atOnce);

    /* --------------------------- 下拉刷新的种种方法 --------------------------- */

    void addPTRUIHandler(IPullToRefreshUIHandler handler);

    void setPTRHandler(IPullToRefreshHandler handler);

}
