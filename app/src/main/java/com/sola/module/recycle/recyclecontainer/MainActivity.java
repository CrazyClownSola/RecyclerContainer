package com.sola.module.recycle.recyclecontainer;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import com.sola.module.recycle.library.RecycleContainerBase;
import com.sola.module.recycle.recyclecontainer.adapter.RecycleAnimatorViewAdapter;
import com.sola.module.recycle.recyclecontainer.interfaces.IRecycleAnimatorListItem;
import com.sola.module.recycle.library.interfaces.RecycleLoadMoreHandler;
import com.sola.module.recycle.recyclecontainer.params.TestItemDTO;
import com.sola.module.recycle.recyclecontainer.view.LowExpansionRecycleContainer;
import com.sola.module.recycle.recyclecontainer.view.RecycleFooterView;
import com.sola.module.recycle.recyclecontainer.view.RecycleHeaderView;
import com.sola.module.recycle.recyclecontainer.view.RecycleHeaderView_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import in.srain.cube.views.ptr.PtrFrameLayout;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    @ViewById
    LowExpansionRecycleContainer id_recycler_container;

    RecycleAnimatorViewAdapter<IRecycleAnimatorListItem> adapter;

    List<IRecycleAnimatorListItem> cacheList;

    RecycleHeaderView headerView;

    RecycleFooterView footerView;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @AfterViews
    public void afterViews() {
//        SwipeRefreshLayout

        footerView = new RecycleFooterView();
        headerView = RecycleHeaderView_.build(this);

        id_recycler_container.setUpViews();
        id_recycler_container.setHeaderView(headerView);
        id_recycler_container.addPtrUIHandler(headerView);
//        id_recycler_container.s

        id_recycler_container.setLoadMoreHandler(new RecycleLoadMoreHandler() {
            @Override
            public void onLoadMore(RecycleContainerBase container) {
                loadMore();
            }
        });
        id_recycler_container.setHandler(new LowExpansionRecycleContainer.RefreshHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                refreshMore();
            }
        });

//        id_recycler_container.getRecyclerView().addOnScrollListener();
        id_recycler_container.setLayoutManager(new LinearLayoutManager(this));
        id_recycler_container.setItemAnimator(new DefaultItemAnimator());

        id_recycler_container.setLoadMoreUIHandler(footerView);
        id_recycler_container.setShowLoadingForFirstPage(true);
        if (cacheList == null)
            cacheList = new ArrayList<>();
        adapter = new RecycleAnimatorViewAdapter<>(this, cacheList);
        adapter.addFooterView(footerView);
        id_recycler_container.setAdapter(adapter);
        id_recycler_container.postDelayed(new Runnable() {
            @Override
            public void run() {
                id_recycler_container.autoRefresh(true);
            }
        }, 150);
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Background
    public void refreshMore() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        refreshData();
    }

    @UiThread
    public void refreshData() {
        cacheList = new ArrayList<>();
        TestItemDTO dto;
        for (int i = 0, len = 10; i < len; i++) {

            dto = new TestItemDTO("6666" + new Random().nextInt(100) + i);
            cacheList.add(dto);
        }
        adapter.refreshList(cacheList);
        adapter.notifyDataSetChanged();
        id_recycler_container.refreshComplete();
    }

    @Background
    public void loadMore() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loadMoreData();
    }

    @UiThread
    public void loadMoreData() {
        int t = new Random().nextInt(10);
        TestItemDTO dto;
        for (int i = 0, len = 10; i < len; i++) {
            dto = new TestItemDTO("666666" + i);
            cacheList.add(dto);
        }
        adapter.refreshList(cacheList);
        adapter.notifyDataSetChanged();
        id_recycler_container.loadMoreFinish(cacheList.size() == 0, t % 5 != 0);
//        id_ptr_frame.refreshComplete();
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
