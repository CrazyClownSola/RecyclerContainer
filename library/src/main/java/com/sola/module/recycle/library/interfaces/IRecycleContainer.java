package com.sola.module.recycle.library.interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/10/13
 */
public interface IRecycleContainer {
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

    void setLoadMoreUIHandler(RecycleLoadMoreUIHandler handler);

    void setLoadMoreHandler(RecycleLoadMoreHandler handler);

    void loadMoreFinish(boolean emptyResult, boolean hasMore);

    void loadMoreError(int errorCode, String errorMessage);


//    /* --------------------------- 下拉刷新的种种方法 --------------------------- */
//
//    void setRefreshUIHandler(RefreshUIHandler handler);
//
//    void setRefreshHandler(RefreshHandler handler);
//
//    void refreshComplete();
//
//    void refreshError(int errorCode,String errorMessage);

}
