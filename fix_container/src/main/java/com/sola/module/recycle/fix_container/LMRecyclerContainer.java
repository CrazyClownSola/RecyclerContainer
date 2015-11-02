package com.sola.module.recycle.fix_container;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.sola.module.recycle.fix_container.tools.IRecycleLoadMoreContainer;
import com.sola.module.recycle.fix_container.tools.IRecycleLoadMoreHandler;
import com.sola.module.recycle.fix_container.tools.IRecycleLoadMoreUIHandler;


/**
 * RecyclerView 附带上LoadMore效果的控件，注意该类中的子布局中一定要有RecyclerView，否则主动抛异常
 *
 * author: Sola
 * 2015/10/20
 */
public class LMRecyclerContainer extends RecyclerContainerBase
        implements IRecycleLoadMoreContainer {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    RecyclerView.OnScrollListener mOnScrollListener;

    /**
     * 用于判断RecyclerView的界面布局的类型
     */
    protected LAYOUT_MANAGER_TYPE layoutManagerType;

    /**
     * 加载更多状态变更时间的Handler
     */
    private IRecycleLoadMoreHandler mLoadMoreHandler;

    /**
     * 加载更多的UIView控件监听
     */
    private IRecycleLoadMoreUIHandler mLoadMoreUIHandler;

    boolean isLoading, mHasMore, mLoadError, mShowLoadingForFirstPage = false;
    boolean isListEmpty = true;

    int mVisibleItemCount = 0; // Scroll滑动事件中缓存的能够呈现的项目数
    int mTotalItemCount = 0;// 所有项总数
    int mFirstVisibleItem = 0;//显示的第一项

    private RecyclerView.OnScrollListener scrollerListener = new RecyclerView.OnScrollListener() {
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
    };

    // ===========================================================
    // Constructors
    // ===========================================================

    public LMRecyclerContainer(Context context) {
        super(context);
    }

    public LMRecyclerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LMRecyclerContainer(Context context, AttributeSet attrs, int defStyleAttr) {
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
//        recy
        if (mContent instanceof RecyclerView) {
            ((RecyclerView) mContent).addOnScrollListener(scrollerListener);
        } else
            throw new IllegalStateException("Load More Container must has one RecyclerView");

    }


    @Override
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.mOnScrollListener = listener;
    }

    @Override
    public void setShowLoadingForFirstPage(boolean shown) {
        mShowLoadingForFirstPage = shown;
    }

    @Override
    public void setLoadMoreUIHandler(IRecycleLoadMoreUIHandler handler) {
        mLoadMoreUIHandler = handler;
    }

    @Override
    public void setLoadMoreHandler(IRecycleLoadMoreHandler handler) {
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
    private void onReachBottom() {
        if (mLoadError) {
            return;
        }
        tryToPerformLoadMore();
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

    /**
     * 用于判定RecyclerView是属于何种类型的布局方式
     */
    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }
}
