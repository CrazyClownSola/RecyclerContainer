package com.sola.module.recycle.fix_container.tools;

import android.support.v7.widget.RecyclerView;

/**
 * Description:
 *
 * author: Sola
 * 2015/10/13
 */
public interface IRecycleLoadMoreContainer {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    void setOnScrollListener(RecyclerView.OnScrollListener listener);

    void setShowLoadingForFirstPage(boolean shown);

    void setLoadMoreUIHandler(IRecycleLoadMoreUIHandler handler);

    void setLoadMoreHandler(IRecycleLoadMoreHandler handler);

    void loadMoreFinish(boolean emptyResult, boolean hasMore);

    void loadMoreError(int errorCode, String errorMessage);


}
