package com.sola.module.recycle.fix_container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sola.module.recycle.fix_container.tools.IPullToRefreshUIHandler;

/**
 * PTRContainer的基础类，主要是解决界面View的布局问题
 * author: Sola
 * 2015/10/16
 */
public abstract class RecyclerContainerBase extends ViewGroup {

    // ===========================================================
    // Constants
    // ===========================================================

    public final static int POS_START = 0;

    // ===========================================================
    // Fields
    // ===========================================================

    /**
     * 顶部刷新的布局，一般需要继承#IPullToRefreshUIHandler,比较推荐代码上动态加入HeaderView
     */
    View mHeaderView;

    /**
     * 底部LoadMore的布局，需要继承#IRecycleLoadMoreUIHandler,
     * 但是由于RecyclerView的特殊性，这个布局需要通过Adapter进行加入，这里只是作为一个监听而存在
     */
    View mFooterView;

    /**
     * 主布局，一般为RecyclerView或者ListView
     */
    View mContent;

    /***************** Indicator指示器部分  ********************/

    /**
     * 当前点位，会被用于各种各样的判断中去
     * 只在movePos的时候被设置值
     */
    int mCurrentPos;

    int mHeaderHeight;

    /**
     * 警戒线的高度，默认为1.2*mHeaderHeight，判断是否需要触发刷新事件
     */
    int mOffsetToRefresh;

    /***************** 默认的一些配置值的部分  ********************/

    /**
     * 警戒线的比例值
     */
    float mRatioOfHeaderHeightToRefresh = 1.2f;

    // ===========================================================
    // Constructors
    // ===========================================================

    public RecyclerContainerBase(Context context) {
        super(context);
    }

    public RecyclerContainerBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerContainerBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View headerView) {
        if (headerView == null)
            return;
        if (mHeaderView != null && mHeaderView != headerView)
            removeView(mHeaderView);
        ViewGroup.LayoutParams lp = headerView.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            headerView.setLayoutParams(
                    lp
            );
        }
        this.mHeaderView = headerView;
        addView(headerView);
    }

    public int getmCurrentPos() {
        return mCurrentPos;
    }

    public void setRatioOfHeaderHeightToRefresh(float mRatioOfHeaderHeightToRefresh) {
        this.mRatioOfHeaderHeightToRefresh = mRatioOfHeaderHeightToRefresh;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    protected void onFinishInflate() {
        final int childCount = getChildCount();
        if (childCount > 2)
            throw new IllegalStateException("Pull to Refresh Container only can hold 2 elements");
        else if (childCount == 2) {
            // 如果布局当中有两个子类
            if (mContent == null) {
                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                if (child1 instanceof IPullToRefreshUIHandler) {
                    mHeaderView = child1;
                    mContent = child2;
                } else if (child2 instanceof IPullToRefreshUIHandler) {
                    mHeaderView = child2;
                    mContent = child1;
                } else {
                    if (mContent == null && mHeaderView == null) {
                        mHeaderView = child1;
                        mContent = child2;
                    } else {
                        if (mHeaderView == null) {
                            mHeaderView = mContent == child1 ? child2 : child1;
                        } else {
                            mContent = mHeaderView == child1 ? child2 : child1;
                        }
                    }
                }
            }
        } else if (childCount == 1) {
            mContent = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in PTR Container is empty. Do you forget to specify its id in xml layout file?");
            mContent = errorView;
            addView(mContent);
        }
        if (mHeaderView != null)
            mHeaderView.bringToFront();
        super.onFinishInflate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int offsetX = getmCurrentPos();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        if (mHeaderView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetX - mHeaderHeight;
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(left, top, right, bottom);
        }
        if (mContent != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetX;
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight();
            mContent.layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            mHeaderHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mOffsetToRefresh = (int) (mHeaderHeight * mRatioOfHeaderHeightToRefresh);
        }

        if (mContent != null)
            measureContentView(mContent, widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * 对布局进行动态Margin的设定
     */
    private void measureContentView(View child,
                                    int parentWidthMeasureSpec,
                                    int parentHeightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }


    // ===========================================================
    // 由于是继承与ViewGroup的所以布局方式并有被配置过，所以调整下如下方法
    // ===========================================================

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }
    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
