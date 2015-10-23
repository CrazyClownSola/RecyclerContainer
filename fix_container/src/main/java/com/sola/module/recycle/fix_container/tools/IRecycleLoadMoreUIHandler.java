package com.sola.module.recycle.fix_container.tools;

/**
 * Description:
 *
 * author: Sola
 * 2015/10/13
 */
public interface IRecycleLoadMoreUIHandler {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    void onLoading(IRecycleLoadMoreContainer container);

    void onLoadFinish(IRecycleLoadMoreContainer container, boolean empty, boolean hasMore);

    void onWaitToLoadMore(IRecycleLoadMoreContainer container);

    void onLoadError(IRecycleLoadMoreContainer container, int errorCode, String errorMessage);
}
