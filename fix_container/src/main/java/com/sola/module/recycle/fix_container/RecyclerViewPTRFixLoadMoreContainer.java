package com.sola.module.recycle.fix_container;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.sola.module.recycle.fix_container.tools.IPullToRefreshContainer;
import com.sola.module.recycle.fix_container.tools.IPullToRefreshHandler;
import com.sola.module.recycle.fix_container.tools.IPullToRefreshUIHandler;


/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/10/19
 */
public class RecyclerViewPTRFixLoadMoreContainer extends RecyclerViewLoadMoreContainer
        implements IPullToRefreshContainer {


    // ===========================================================
    // Constants
    // ===========================================================

    public static boolean DEBUG = false;

    protected final String LOG_TAG = "Sola";

    //初始化状态
    public final static byte PTR_STATUS_INIT = 1;
    //准备状态
    public final static byte PTR_STATUS_PREPARE = 2;
    //加载状态
    public final static byte PTR_STATUS_LOADING = 3;
    //加载完成
    public final static byte PTR_STATUS_COMPLETE = 4;

    // 用于标记mFlag的标志的状态值

    private final static byte FLAG_AUTO_REFRESH_AT_ONCE = 0x01;

    private final static byte FLAG_AUTO_REFRESH_BUT_LATER = 0x01 << 1;

    private final static byte MASK_AUTO_REFRESH = 0x03;
    // ===========================================================
    // Fields
    // ===========================================================


    private byte mStatus = PTR_STATUS_INIT;

    /**
     * 状态标志位
     */
    private int mFlag;

    /**
     * 监听下拉状态变更事件的Handler
     */
    private IPullToRefreshHandler mPTRHandler;

    /**
     * 下拉刷新的View控件监听
     */
    private IPullToRefreshUIHandler mPTRUIHandler;


    private int mDurationToClose = 200;
    /**
     * 标志位，标志是否是在touch事件中
     */
    boolean isUnderTouch = false;

    /**
     * 是否已经发出取消事件
     */
    boolean hasSendCancelEvent = false;

    /**
     * 记录下当前所按下的位置
     */
    int mPressedPos;

    /**
     * 记录下前一个点击的位置
     */
    int mLastPos;

    /**
     * 记录下上一次拖动的界面的记录点位
     */
    int mLastFlingY;

    /**
     * 记录下刷新完成后的Y的值
     */
    int mRefreshCompleteY;

    /**
     * 记录前一次Touch事件的位置
     */
    PointF mLastMovePos = new PointF();


    /**
     * 记录下上一次Move事件
     */
    MotionEvent mLastMoveEvent;


    float mResistance = 1.7f;

    /**
     *
     */
    Scroller mScroller = new Scroller(getContext());

    /**
     * LOG 用
     **/
    private long mLoadingStartTime = 0;

    private int mLoadingMinTime = 500;

    boolean mScrollerRunning = false;

    private ScrollerRunner scrollerRunner = new ScrollerRunner();

    private Runnable mPerformRefreshCompleteDelay = new Runnable() {
        @Override
        public void run() {
            performRefreshComplete();
        }
    };
    // ===========================================================
    // Constructors
    // ===========================================================

    public RecyclerViewPTRFixLoadMoreContainer(Context context) {
        super(context);
    }

    public RecyclerViewPTRFixLoadMoreContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewPTRFixLoadMoreContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public void setLoadingMinTime(int mLoadingMinTime) {
        this.mLoadingMinTime = mLoadingMinTime;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (scrollerRunner != null)
            scrollerRunner.destroy();
        if (mPerformRefreshCompleteDelay != null) {
            removeCallbacks(mPerformRefreshCompleteDelay);
        }
    }

    /**
     * 官方针对Scroller专门实现的方法，父类方法什么都没做，意在由我们去自定义
     * 调用方是在ViewGroup每次drawChild的时候做的
     * 有一个问题在这边，computeScroll在每次 scroller.startScroller方法的时候，必定会执行完设定的值。
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
//        if (mScrollerRunning && (mScroller.computeScrollOffset() ||
//                !mScroller.isFinished())) {
//            int change = mScroller.getCurrY();
//            int deltaY = change - mLastFlingY;
//            mLastFlingY = change;
//            Log.d(LOG_TAG,
//                    "computeScroll change[" + change + "] " +
//                            "deltaY[" + deltaY + "] " +
//                            "[" + mLastFlingY + "] " +
//                            "[" + mCurrentPos + "]");
//            movePos(deltaY);
//        } else
//            mScrollerRunning = false;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        if (!isEnabled() || mContent == null || mHeaderView == null)
            return dispatchTouchEventSuper(ev);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isUnderTouch = false;
                if (mCurrentPos > POS_START) {
                    if (DEBUG) {
                        Log.d(LOG_TAG, "call onRelease when user release");
                    }
                    onRelease(false);
                    if (mCurrentPos != mPressedPos) {
                        if (DEBUG) {
                            Log.d(LOG_TAG, "Action send cancel event");
                        }
                        sendCancelEvent();
                        return true;
                    }
                    return dispatchTouchEventSuper(ev);
                } else
                    return dispatchTouchEventSuper(ev);
            case MotionEvent.ACTION_DOWN:
                hasSendCancelEvent = false; //标志位清零
                //设置标志位，并且记录当前点击的位置
                isUnderTouch = true;
                mPressedPos = mCurrentPos;
                mLastMovePos.set(ev.getX(), ev.getY());
                Log.v(LOG_TAG, String.format("ACTION_DOWN:  lastX:%s  lastY:%s, " +
                                "currentPos: %s",
                        mLastMovePos.x, mLastMovePos.y, mCurrentPos));

                scrollerRunner.abortIfWorking();
                dispatchTouchEventSuper(ev);//这个方法没特别搞懂是为什么
                return true; // 代表事件已经被自定义实现
            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = ev;
                float x = ev.getX();
                float y = ev.getY();
                float offsetX = x - mLastMovePos.x;
                // TODO: 2015/10/19 注意测试下  mResistance的用处
                float offsetY = (y - mLastMovePos.y) / mResistance;

                mLastMovePos.set(x, y);

                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;
                boolean canMoveUp = mCurrentPos > POS_START;

//                if (DEBUG) {
//                    boolean canMoveDown = mPTRHandler != null &&
//                            mPTRHandler.checkCanDoRefresh(this, mContent, mHeaderView);
//                    Log.v(LOG_TAG, String.format("ACTION_MOVE: y:%s lastY:%s  offsetY:%s, " +
//                                    "currentPos: %s, moveUp: %s, canMoveUp: %s, moveDown: %s:" +
//                                    " canMoveDown: %s",
//                            y, mLastMovePos.y, offsetY, mCurrentPos, moveUp, canMoveUp, moveDown, canMoveDown));
//                }
                if (moveDown && mPTRHandler != null &&
                        !mPTRHandler.checkCanDoRefresh(this, mContent, mHeaderView))
                    return dispatchTouchEventSuper(ev);

                if ((moveUp && canMoveUp) || moveDown) {
                    Log.d(LOG_TAG,
                            "action movePos deltaY[" + offsetY + "]");
                    movePos(offsetY);
                    return true;
                }

        }

        return dispatchTouchEventSuper(ev);
    }


    // ===========================================================
    // PullToRefresh的接口实现
    // ===========================================================

    @Override
    public void refreshComplete() {
        if (DEBUG) {
            Log.i(LOG_TAG, "refreshComplete");
        }
        int delay = (int) (mLoadingMinTime - System.currentTimeMillis() - mLoadingStartTime);
        if (delay <= 0) {
            if (DEBUG) {
                Log.d(LOG_TAG, "performRefreshComplete at once");
            }
            performRefreshComplete();
        } else {
            postDelayed(mPerformRefreshCompleteDelay, delay);
            if (DEBUG) {
                Log.d(LOG_TAG, String.format(
                        "performRefreshComplete after delay: %s", delay));
            }
        }

    }

    @Override
    public void autoRefresh(boolean atOnce) {
        if (mStatus != PTR_STATUS_INIT)
            return;
        mFlag |= atOnce ? FLAG_AUTO_REFRESH_AT_ONCE :
                FLAG_AUTO_REFRESH_BUT_LATER;
        mStatus = PTR_STATUS_PREPARE;
        if (mPTRUIHandler != null)
            mPTRUIHandler.onUIRefreshPrepare(this);
        if (DEBUG) {
            Log.i(LOG_TAG, String.format(
                    "PtrUIHandler: onUIRefreshPrepare, mFlag %s", mFlag));
        }
        if (DEBUG) {
            Log.d(LOG_TAG,
                    String.format("tryToScrollTo: autoRefresh  to:%s",
                            POS_START));
        }
        //界面下拉到警戒线
        tryToScrollTo(mOffsetToRefresh, mDurationToClose);
        if (atOnce) { // 一次性的刷新的情况，界面直接进入Loading的模式
            mStatus = PTR_STATUS_LOADING;
            preformRefresh();
        }
    }

    @Override
    public void addPTRUIHandler(IPullToRefreshUIHandler handler) {
        mPTRUIHandler = handler;
    }

    @Override
    public void setPTRHandler(IPullToRefreshHandler handler) {
        mPTRHandler = handler;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public boolean dispatchTouchEventSuper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    /**
     * 移动点位，记录下移动的位置
     *
     * @param offsetY 偏移量
     */
    private void movePos(float offsetY) {
        //第一次操作为 向上滑动的时候进入如下方法
        if (offsetY < 0 && (mCurrentPos == POS_START))
            return;
        int to = mCurrentPos + (int) offsetY; // 需要移动的距离
        if (to < POS_START)
            to = POS_START;
        mLastPos = mCurrentPos;
        mCurrentPos = to;
        int change = to - mLastPos;
        updatePos(change);
    }

    /**
     * 更新位置，主要是进行HeaderView和Content的移动
     *
     * @param change 移动的距离
     */
    private void updatePos(int change) {
        if (change == 0)
            return;
        // TODO: 2015/10/19 这段代码需要进行测试
        if (isUnderTouch && !hasSendCancelEvent && mCurrentPos != mPressedPos) {
            hasSendCancelEvent = true;

            if (DEBUG) {
                Log.d(LOG_TAG, "UpPos send cancel event");
            }
            sendCancelEvent();
        }

        // 触发onPrepare的点，一共两种情况
        // 第一种是：界面最开始，监听到界面移动的情况下
        if (hasLeftStartPosition() && mStatus == PTR_STATUS_INIT) {
            mStatus = PTR_STATUS_PREPARE;
            mPTRUIHandler.onUIRefreshPrepare(this);
            if (DEBUG) {
                Log.i(LOG_TAG, String.format(
                        "PtrUIHandler: onUIRefreshPrepare, mFlag %s", mFlag));
            }
        }
        if (mLastPos != POS_START && mCurrentPos == POS_START) {
            tryToNotifyReset();

            if (isUnderTouch) {
                sendDownEvent();
            }
        }


        //根据条件判断是否需要进入到Loading的状态
        //检索条件1：在touch事件的生命周期内，并且autoRefresh方法未被调用过，并且当前点位刚过警戒线，
        // 由于mPullToRefresh一直为false，此条件忽略
        //检索条件2：autoRefresh(false)被调用，并且当前点位已经超过Header的高度的时候,此处应该是缓处理
        if (mStatus == PTR_STATUS_PREPARE) {
            if ((mFlag & MASK_AUTO_REFRESH) == FLAG_AUTO_REFRESH_BUT_LATER
                    && (mLastPos < mHeaderHeight && mCurrentPos >= mHeaderHeight)) {
                tryToPerformRefresh();
            }
        }
        if (DEBUG) {
            Log.v(LOG_TAG, String.format(
                    "updatePos: change: %s, current: %s last: %s, top: %s, headerHeight: %s",
                    change, mCurrentPos, mLastPos, mContent.getTop(), mHeaderHeight));
        }
        //至关重要的移动View的代码
//        if(mHeaderV)
        mHeaderView.offsetTopAndBottom(change);
        mContent.offsetTopAndBottom(change);
        invalidate();
        if (mPTRUIHandler != null)
            mPTRUIHandler.onUIPositionChange(this,
                    isUnderTouch, mStatus, mCurrentPos, mLastPos,
                    mOffsetToRefresh);
    }

    /**
     * 判断是否需要进行Refresh
     */
    private void tryToPerformRefresh() {
        if (mStatus != PTR_STATUS_PREPARE)
            return;
        //这里进行判定
        //筛选条件1：在autoRefresh 被调用过的情况下，当前点位是否超过Header的Height
        //筛选条件2；当前拖动的点位已经超过mOffsetToRefresh的时候
        if (mCurrentPos > mHeaderHeight && isAutoRefresh()
                || mCurrentPos >= mOffsetToRefresh) {
            mStatus = PTR_STATUS_LOADING;
            preformRefresh();
            if (DEBUG) {
                Log.i(LOG_TAG, "PtrUIHandler: onUIRefreshBegin [" + mFlag + "][" + mCurrentPos + "]");
            }
        }
    }

    private void preformRefresh() {
        mLoadingStartTime = System.currentTimeMillis();
        if (mPTRUIHandler != null)
            mPTRUIHandler.onUIRefreshBegin(this);
        if (mPTRHandler != null)
            mPTRHandler.onRefreshBegin(this);
    }

    private void performRefreshComplete() {
        mStatus = PTR_STATUS_COMPLETE;
        if (mScrollerRunning
                && isAutoRefresh()) {
            if (DEBUG) {
                Log.d(LOG_TAG,
                        "performRefreshComplete do nothing");
            }
            return;
        }
        notifyUIRefreshComplete(false);
    }

    private void onRelease(boolean stayForLoading) {
        tryToPerformRefresh();
        if (mStatus == PTR_STATUS_LOADING) {
            if (mCurrentPos > mHeaderHeight && !stayForLoading) {
                if (DEBUG) {
                    Log.d(LOG_TAG,
                            String.format("tryToScrollTo: onRelease , to:%s",
                                    mHeaderHeight));
                }
                tryToScrollTo(mHeaderHeight, mDurationToClose);
            }
        } else {
            if (mStatus == PTR_STATUS_COMPLETE)
                notifyUIRefreshComplete(false);
            else
                tryScrollBackToTop();
        }
    }

    private void tryToScrollTo(int to, int mDurationToClose) {
        if (mCurrentPos == to)
            return;
        int start = mCurrentPos;
        int distance = to - start;

        mLastFlingY = 0;

        removeCallbacks(scrollerRunner);

        mScroller.startScroll(0, 0, 0, distance, mDurationToClose);

        post(scrollerRunner);
        if (DEBUG) {
            Log.d(LOG_TAG,
                    String.format("tryToScrollTo: start: %s, distance:%s, to:%s, scrollY:%s",
                            start, distance, to, mScroller.getCurrY()));
        }
        mScrollerRunning = true;
//        invalidate();
    }


    protected void onPtrScrollFinish() {
        if (mCurrentPos > POS_START && isAutoRefresh()) {
            if (DEBUG) {
                Log.d(LOG_TAG, "call onRelease after scroll finish");
            }
            onRelease(true);
        }
    }

    private void tryScrollBackToTop() {
        if (!isUnderTouch) {
            if (DEBUG) {
                Log.d(LOG_TAG,
                        String.format("tryToScrollTo: tryScrollBackToTop , to:%s",
                                POS_START));
            }
            tryToScrollTo(POS_START, mDurationToClose);
        }
    }


    private void notifyUIRefreshComplete(boolean ignoreHook) {
        if (mPTRUIHandler != null) {
            if (DEBUG) {
                Log.i(LOG_TAG, "PtrUIHandler: onUIRefreshComplete");
            }
            mPTRUIHandler.onUIRefreshComplete(this);
        }
        mRefreshCompleteY = mCurrentPos;
        if (!isUnderTouch) {
            if (DEBUG) {
                Log.d(LOG_TAG,
                        String.format("tryToScrollTo: notifyUIRefreshComplete  to:%s",
                                POS_START));
            }
            tryToScrollTo(POS_START, mDurationToClose);
        }
        tryToNotifyReset();
    }

    private boolean tryToNotifyReset() {
        if ((mStatus == PTR_STATUS_COMPLETE || mStatus == PTR_STATUS_PREPARE)
                && mCurrentPos == POS_START) {
            if (mPTRUIHandler != null)
                mPTRUIHandler.onUIReset(this);
            if (DEBUG) {
                Log.i(LOG_TAG, "PtrUIHandler: onUIReset");
            }

            mStatus = PTR_STATUS_INIT;
            clearFlag();
            return true;
        }
        return false;
    }


    private void clearFlag() {
        mFlag = mFlag & ~MASK_AUTO_REFRESH;
    }

    private boolean hasLeftStartPosition() {
        return mLastPos == POS_START && mCurrentPos > POS_START;
    }

    private void sendCancelEvent() {
        // The ScrollChecker will update position and lead to send cancel event when mLastMoveEvent is null.
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(
                last.getDownTime(),
                last.getEventTime() +
                        ViewConfiguration.getLongPressTimeout(),
                MotionEvent.ACTION_CANCEL,
                last.getX(),
                last.getY(),
                last.getMetaState());
        dispatchTouchEventSuper(e);
    }

    private void sendDownEvent() {
        if (DEBUG) {
            Log.d(LOG_TAG, "send down event");
        }
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSuper(e);
    }

    public boolean isAutoRefresh() {
        return (mFlag & MASK_AUTO_REFRESH) > 0;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private class ScrollerRunner implements Runnable {
        @Override
        public void run() {
            if (mScroller.computeScrollOffset() ||
                    !mScroller.isFinished()) {
                int change = mScroller.getCurrY();
                int deltaY = change - mLastFlingY;
                mLastFlingY = change;
                Log.d(LOG_TAG,
                        "scroller runner change[" + change + "] " +
                                "deltaY[" + deltaY + "] " +
                                "[" + mLastFlingY + "] " +
                                "[" + mCurrentPos + "]");
                movePos(deltaY);
                post(this);
            } else {
                reset();
                onPtrScrollFinish();
            }
        }

        private void abortIfWorking() {
            if (mScrollerRunning) {
                if (!mScroller.isFinished())
                    mScroller.forceFinished(true);
                if (mCurrentPos > POS_START && isAutoRefresh()) {
                    if (DEBUG) {
                        Log.d(LOG_TAG, "call onRelease after scroll abort");
                    }
                    onRelease(true);
                }
                reset();
            }

        }

        private void reset() {
            if (DEBUG) {
                Log.v(LOG_TAG, String.format("finish, currentPos:%s",
                        mCurrentPos));
            }
            mScrollerRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        private void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }

    }

}
