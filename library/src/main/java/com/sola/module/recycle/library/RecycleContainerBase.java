package com.sola.module.recycle.library;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sola.module.recycle.library.interfaces.IRecycleContainer;
import com.sola.module.recycle.library.interfaces.RecycleLoadMoreHandler;
import com.sola.module.recycle.library.interfaces.RecycleLoadMoreUIHandler;

/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/10/13
 */
public abstract class RecycleContainerBase extends LinearLayout implements IRecycleContainer {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    RecyclerView.OnScrollListener mOnScrollListener;

    RecycleLoadMoreHandler mLoadMoreHandler;

    RecycleLoadMoreUIHandler mLoadMoreUIHandler;

    boolean isLoading, mHasMore, mLoadError, mShowLoadingForFirstPage = false;
    boolean isListEmpty = true;

    RecyclerView mRecycleView;

    int mVisibleItemCount = 0; // Scroll滑动事件中缓存的能够呈现的项目数
    int mTotalItemCount = 0;// 所有项总数
    int mFirstVisibleItem = 0;// 显示的第一项

    protected LAYOUT_MANAGER_TYPE layoutManagerType;

    // ===========================================================
    // Constructors
    // ===========================================================
    public RecycleContainerBase(Context context) {
        super(context);
    }

    public RecycleContainerBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleContainerBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRecycleView = retrieveRecycleView();
        init();
    }

    @Override
    public void setShowLoadingForFirstPage(boolean shown) {
        mShowLoadingForFirstPage = shown;
    }

    @Override
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.mOnScrollListener = listener;
    }

    @Override
    public void setLoadMoreUIHandler(RecycleLoadMoreUIHandler handler) {
        mLoadMoreUIHandler = handler;
    }

    @Override
    public void setLoadMoreHandler(RecycleLoadMoreHandler handler) {
        mLoadMoreHandler = handler;
    }

    @Override
    public void loadMoreFinish(boolean emptyResult, boolean hasMore) {
        mLoadError = false;
        isListEmpty = emptyResult;
        isLoading = false;
        mHasMore = hasMore;

        if (mLoadMoreUIHandler != null) {
            mLoadMoreUIHandler.onLoadFinish(this, emptyResult, hasMore);
        }
    }

    @Override
    public void loadMoreError(int errorCode, String errorMessage) {
        isLoading = false;
        mLoadError = true;
        if (mLoadMoreUIHandler != null) {
            mLoadMoreUIHandler.onLoadError(this, errorCode, errorMessage);
        }
    }
    // ===========================================================
    // Methods
    // ===========================================================

    private void init() {
        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int[] lastPositions;

            private boolean mIsEnd = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (null != mOnScrollListener) {
                    mOnScrollListener.onScrollStateChanged(recyclerView, newState);
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mIsEnd) {
                        onReachBottom();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (null != mOnScrollListener) {
                    mOnScrollListener.onScrolled(recyclerView, dx, dy);
                }
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                if (layoutManagerType == null) {
                    if (layoutManager instanceof GridLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
                    } else {
                        throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
                    }
                }

                switch (layoutManagerType) {
                    case LINEAR:
                        mVisibleItemCount = layoutManager.getChildCount();
                        mTotalItemCount = layoutManager.getItemCount();
                    case GRID:
//                        lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        mFirstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                        break;
                    case STAGGERED_GRID:
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                        if (lastPositions == null)
                            lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];

                        staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
//                        lastVisibleItemPosition = findMax(lastPositions);

                        staggeredGridLayoutManager.findFirstVisibleItemPositions(lastPositions);
                        mFirstVisibleItem = findMin(lastPositions);
                        break;
                }
                mIsEnd = (mTotalItemCount - mVisibleItemCount) <= mFirstVisibleItem;
            }
        });
    }

    private void tryToPerformLoadMore() {
        if (isLoading) {
            return;
        }

        if (!mHasMore && !(isListEmpty && mShowLoadingForFirstPage)) {
            return;
        }

        isLoading = true;

        if (mLoadMoreUIHandler != null) {
            mLoadMoreUIHandler.onLoading(this);
        }
        if (null != mLoadMoreHandler) {
            mLoadMoreHandler.onLoadMore(this);
        }
    }

    private void onReachBottom() {
        if (mLoadError) {
            return;
        }
        tryToPerformLoadMore();
    }

    private int findMin(int[] lastPositions) {
        int min = Integer.MAX_VALUE;
        for (int value : lastPositions) {
            if (value != RecyclerView.NO_POSITION && value < min)
                min = value;
        }
        return min;
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================


    protected abstract RecyclerView retrieveRecycleView();

    /**
     * 用于判定RecyclerView是属于何种类型的布局方式
     */
    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }
}
