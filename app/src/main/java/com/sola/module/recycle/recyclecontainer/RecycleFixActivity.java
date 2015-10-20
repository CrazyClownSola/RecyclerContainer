package com.sola.module.recycle.recyclecontainer;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;

import com.sola.module.recycle.fix_container.RecyclerViewPTRFixLoadMoreContainer;
import com.sola.module.recycle.fix_container.RecyclerViewRefreshContainerBase;
import com.sola.module.recycle.fix_container.tools.IPullToRefreshHandler;
import com.sola.module.recycle.fix_container.tools.IRecycleLoadMoreContainer;
import com.sola.module.recycle.fix_container.tools.IRecycleLoadMoreHandler;
import com.sola.module.recycle.fix_container.utils.PtrDefaultHandler;
import com.sola.module.recycle.recyclecontainer.adapter.RecycleAnimatorViewAdapter;
import com.sola.module.recycle.recyclecontainer.interfaces.IRecycleAnimatorListItem;
import com.sola.module.recycle.recyclecontainer.params.TestItemDTO;
import com.sola.module.recycle.recyclecontainer.view.RecycleFooterView;
import com.sola.module.recycle.recyclecontainer.view.fix.RecycleFixFooterView;
import com.sola.module.recycle.recyclecontainer.view.fix.RecycleFixHeaderView;
import com.sola.module.recycle.recyclecontainer.view.fix.RecycleFixHeaderView_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Description:
 * <p/>
 * author: Sola
 * 2015/10/20
 */
@EActivity(R.layout.activity_recycle_fix)
public class RecycleFixActivity extends AppCompatActivity {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    @ViewById
    Toolbar id_tool_bar;

    @ViewById
    RecyclerView id_recycler_view;

    /**
     * 主要的界面控件，整合了 下拉刷新和 加载更多的界面控件
     */
    @ViewById
    RecyclerViewPTRFixLoadMoreContainer id_ptr_frame;

    /**
     * 自定义的下拉界面，可根据具体需求进行修改
     */
    RecycleFixHeaderView headerView;

    /**
     * 自定义的LoadMore界面
     */
    RecycleFixFooterView footerView;

    /**
     * 这个是我个人惯用的Adapter适配写法，仅供参考
     */
    RecycleAnimatorViewAdapter<IRecycleAnimatorListItem> adapter;

    /**
     * 缓存的数组
     */
    List<IRecycleAnimatorListItem> cacheList;

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


    @AfterViews
    public void afterViews() {
        setSupportActionBar(id_tool_bar);
        // 初始化header
        headerView = RecycleFixHeaderView_.build(this);
        // 初始化Footer
        footerView = new RecycleFixFooterView();
        //设定PullToRefresh的事件监听
        id_ptr_frame.setPTRHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(RecyclerViewRefreshContainerBase frame) {
                refreshMore();
            }
        });
        // 代码级别的动态设定HeaderView，也可以在Xml文件中添加进去
        id_ptr_frame.setHeaderView(headerView);
        //添加UI监听
        id_ptr_frame.addPTRUIHandler(headerView);
        //设定 LoadMore的事件监听
        id_ptr_frame.setLoadMoreHandler(new IRecycleLoadMoreHandler() {
            @Override
            public void onLoadMore(IRecycleLoadMoreContainer container) {
                loadMore();
            }
        });
        //添加UI监听
        id_ptr_frame.setLoadMoreUIHandler(footerView);
        //初始配置，后期我会进行一定的优化把这个去除掉
        id_ptr_frame.setShowLoadingForFirstPage(true);

        // RecyclerView的设定，如果不懂这方面的知识请百度
        id_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        id_recycler_view.setItemAnimator(new DefaultItemAnimator());

        if (cacheList == null)
            cacheList = new ArrayList<>();
        //设定适配器
        adapter = new RecycleAnimatorViewAdapter<>(this, cacheList);
        // 这个地方比较特殊，由于RecyclerView自身的特殊性，FooterView是不能通过addFooterView方法进行加入，
        // 只能通过适配器去做footer的界面设定,所以千万别漏了这个方法，如果你想用到LoadMore的效果的话
        adapter.addFooterView(footerView);
        id_recycler_view.setAdapter(adapter);

        //这里有设置一个延迟只是为了更好的呈现效果
        id_ptr_frame.postDelayed(new Runnable() {
            @Override
            public void run() {
                id_ptr_frame.autoRefresh(false);
            }
        }, 300);
    }

    @Background
    public void refreshMore() {
        try {
            Thread.sleep(1200);
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
        id_ptr_frame.refreshComplete();
        id_ptr_frame.loadMoreFinish(cacheList.size() == 0, true);
    }

    @Background
    public void loadMore() {
        try {
            Thread.sleep(1200);
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
        id_ptr_frame.loadMoreFinish(cacheList.size() == 0, t % 5 != 0);
//        id_ptr_frame.refreshComplete();
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
