package com.sola.module.recycle.fix_container.utils;

import android.view.View;
import android.widget.AbsListView;

import com.sola.module.recycle.fix_container.RecyclerViewRefreshContainerBase;
import com.sola.module.recycle.fix_container.tools.IPullToRefreshHandler;

/**
 * 定义了一个默认使用的 Pull to Refresh Handler事件监听的类
 *
 * author: Sola
 * 2015/10/20
 */
public abstract class PtrDefaultHandler implements IPullToRefreshHandler {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    public static boolean canChildScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return view.canScrollVertically(-1);
        }
    }


    public static boolean checkContentCanBePulledDown(RecyclerViewRefreshContainerBase frame, View content, View header) {
        return !canChildScrollUp(content);
    }

    @Override
    public boolean checkCanDoRefresh(RecyclerViewRefreshContainerBase frame, View content, View header) {
        return checkContentCanBePulledDown(frame, content, header);
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
