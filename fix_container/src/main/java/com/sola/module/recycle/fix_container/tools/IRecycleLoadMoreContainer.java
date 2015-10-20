package com.sola.module.recycle.fix_container.tools;

import android.support.v7.widget.RecyclerView;

/**
 * Description:
 * <p/>
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


    /* ----------------------  上拉加载更多的 相关方法集合 --------------------  */

    void setLoadMoreUIHandler(IRecycleLoadMoreUIHandler handler);

    void setLoadMoreHandler(IRecycleLoadMoreHandler handler);

    void loadMoreFinish(boolean emptyResult, boolean hasMore);

    void loadMoreError(int errorCode, String errorMessage);


}
