package com.sola.module.recycle.library.interfaces;


import com.sola.module.recycle.library.RecycleContainerBase;

/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/10/13
 */
public interface RecycleLoadMoreUIHandler {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    void onLoading(RecycleContainerBase container);

    void onLoadFinish(RecycleContainerBase container, boolean empty, boolean hasMore);

    void onWaitToLoadMore(RecycleContainerBase container);

    void onLoadError(RecycleContainerBase container, int errorCode, String errorMessage);
}
