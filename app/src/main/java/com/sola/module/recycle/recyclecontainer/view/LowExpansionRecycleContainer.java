package com.sola.module.recycle.recyclecontainer.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.sola.module.recycle.library.RecyclerContainer;
import com.sola.module.recycle.library.interfaces.RecycleLoadMoreHandler;
import com.sola.module.recycle.library.interfaces.RecycleLoadMoreUIHandler;
import com.sola.module.recycle.recyclecontainer.R;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Description:
 * <p 低拓展性的RecyclerView的组件 />
 * author: Sola
 * 2015/10/15
 */
public class LowExpansionRecycleContainer extends PtrFrameLayout {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    RecyclerContainer id_load_more_container;

    RecyclerView id_recycler_view;

    RefreshHandler handler;
    // ===========================================================
    // Constructors
    // ===========================================================

    public LowExpansionRecycleContainer(Context context) {
        super(context);
    }

    public LowExpansionRecycleContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LowExpansionRecycleContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public RecyclerView getRecyclerView() {
        return id_recycler_view;
    }

    public void setHandler(RefreshHandler handler) {
        this.handler = handler;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    protected void onFinishInflate() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_recycle_container, this);
        super.onFinishInflate();
        id_load_more_container = (RecyclerContainer) findViewById(R.id.id_load_more_container);
        id_recycler_view = id_load_more_container.getRecyclerView();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    public void setUpViews() {
        setDurationToClose(500);
        setLoadingMinTime(500);
        if (getHeaderView() == null) {
            PtrClassicDefaultHeader header =
                    new PtrClassicDefaultHeader(getContext());
            setHeaderView(header);
            addPtrUIHandler(header);
        }
        setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {
                return PtrDefaultHandler.checkContentCanBePulledDown(
                        ptrFrameLayout, id_recycler_view, view1);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                if (handler != null)
                    handler.onRefreshBegin(ptrFrameLayout);
            }
        });
    }

    public void setLoadMoreHandler(RecycleLoadMoreHandler handler) {
        id_load_more_container.setLoadMoreHandler(handler);
    }

    public void setLoadMoreUIHandler(RecycleLoadMoreUIHandler handler) {
        id_load_more_container.setLoadMoreUIHandler(handler);
    }

    public void setShowLoadingForFirstPage(boolean shown) {
        id_load_more_container.setShowLoadingForFirstPage(shown);
//        id_load_more_container.
    }

    public void loadMoreFinish(boolean emptyResult, boolean hasMore) {
        id_load_more_container.loadMoreFinish(emptyResult, hasMore);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decoration) {
        id_recycler_view.addItemDecoration(decoration);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        id_recycler_view.setLayoutManager(layoutManager);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
        id_recycler_view.setItemAnimator(itemAnimator);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        id_recycler_view.setAdapter(adapter);
    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================


    public interface RefreshHandler {
        void onRefreshBegin(PtrFrameLayout ptrFrameLayout);
    }

}
