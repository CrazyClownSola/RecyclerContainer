#RecyclerViewContainer
---------------------------------------------

这是基于 [android-Ultra-Pull-To-Refresh] (https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh )  项目进行复刻和整合的专用于RecyclerView的一个容器控件，支持 `API LEVEL >= 11` 配合 `API 21`以上食用更佳

补充一句：如果你没接触过RecyclerView，请百度并且最好去练习下一个基本的RecyclerView怎么写，这样会更有帮助你看懂代码，文档最后有写，仅供参考

###  下拉刷新+加载更多
本类集成了下拉刷新和加载更多功能



##### 截屏
容我去找找怎么截git图……


###使用方式
-----------------------
 
我已经尝试把项目放到maven当中去，暂且只有aar的版本
具体配置如下

maven配置
```
<dependency>
  <groupId>com.github.crazyclownsola</groupId>
  <artifactId>recycler_container</artifactId>
  <version>1.0.6</version>
  <type>apklib</type>
</dependency>

```



最新版本放在 (https://oss.sonatype.org/content/repositories/releases)

in gradle:

```
mavenCentral()
```

gradle, 最新版:
```
// 引用supportV7包中的RecyclerView
compile 'com.android.support:recyclerview-v7:${supportV7Version}'

compile 'com.github.crazyclownsola:recycler_container:1.0.6@aar'
```


######xml示例
```
 <com.sola.module.recycle.fix_container.PTRLMRecyclerContainer
        android:id="@+id/id_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:ptr_duration_to_close="400" // 关闭Header所需的时间间隔
        app:ptr_duration_to_close_header="1000" // Header部分回复到下拉结束状态的时间间隔
        app:ptr_ratio_of_header_height_to_refresh="1.4" // 警戒线的位置占比
        >

        <android.support.v7.widget.RecyclerView
            android:id="@+id/id_recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            />

    </com.sola.module.recycle.fix_container.PTRLMRecyclerContainer>

```
包名略长，不要在意那么多细节

由于是第一个版本，一些配置参数没有过多的添加，后续我会陆续放开这些，同时提供更多的attrs的设置方式

###处理下拉刷新
通过`IPullToRefreshHandler`以检查确定是否可以下来刷新以及在合适的时间刷新数据。

检查是否可以下拉刷新在`PtrDefaultHandler`中有默认简单的实现，你可以根据实际情况完成这个逻辑。

Code
```
@ViewById // 这是我惯用的Annotations库包提供的方法，不习惯的可以用findViewById()
RecyclerView id_recycler_view;
	
```

```
id_ptr_frame.setPTRHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(RecyclerContainerBase frame) {
                refreshMore();
            }
        });
```

主动触发刷新
```
//autoRefresh方法是触发下拉动画的入口方法,true为一次性的立刻进行不呈现拖动动画，false是延迟进行，有动画效果
id_ptr_frame.autoRefresh(true); 
```


####配置HeaderView


自定义刷新的HeaderView，需要继承`IPullToRefreshUIHandler`用于监听刷新的各个状态变更的时候同时HeaderView做出相应的变更

IPullToRefreshUIHandler:
```
public interface IPullToRefreshUIHandler {
   
    void onUIReset(RecyclerContainerBase frame);

    void onUIRefreshPrepare(RecyclerContainerBase frame);

    void onUIRefreshBegin(RecyclerContainerBase frame);

    void onUIRefreshComplete(RecyclerContainerBase frame);

    /**
     * 一般用于判断 当前拖动的位置是否超越警戒线
     *
     * @param frame        界面
     * @param isUnderTouch 是否在touch事件中
     * @param status       状态
     * @param currentPos   当前位置
     * @param lastPos      前一个点击的位置
     * @param offsetHeight Header最大便宜高度
     */
    void onUIPositionChange(RecyclerContainerBase frame,
                            boolean isUnderTouch,
                            byte status,
                            int currentPos,
                            int lastPos,
                            int offsetHeight);

}
```

demo中提供了一个默认实现`RecycleFixHeaderView`仅供参考


```
// 初始化header(不要奇怪为什么有个下划线，这个是Annotations标注库运用的问题，正常代码是直接new的)
RecycleFixHeaderView  headerView = RecycleFixHeaderView_.build(this);
// 代码级别的动态设定HeaderView，也可以在Xml文件中添加进去
id_ptr_frame.setHeaderView(headerView);
//添加UI监听
id_ptr_frame.addPTRUIHandler(headerView);

```

1.0.6版本更新支持xml配置HeaderView
```
<com.sola.module.recycle.fix_container.PTRLMRecyclerContainer
        android:id="@+id/id_ptr_frame"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:ptr_duration_to_close="1000"
        app:ptr_duration_to_close_header="200"
        app:ptr_ratio_of_header_height_to_refresh="1.4">

        <com.sola.module.recycle.demo.view.fix.RecycleFixHeaderView_
            android:id="@+id/id_header_view"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>

        <android.support.v7.widget.RecyclerView
            android:id="@+id/id_recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            />
</com.sola.module.recycle.fix_container.PTRLMRecyclerContainer>

//别忘了在代码中添加UI监听
id_ptr_frame.addPTRUIHandler(headerView);

```



在完成刷新之后请调用
```
//完成界面的刷新
id_ptr_frame.refreshComplete();
```


###处理加载更多

通过`IRecycleLoadMoreHandler`以检查确定是否可以加载更多数据以及在合适的时间加载数据。

Code
```
id_ptr_frame.setLoadMoreHandler(new IRecycleLoadMoreHandler() {
            @Override
            public void onLoadMore(IRecycleLoadMoreContainer container) {
                //do something when view can Load more
             }
        });
```


####配置footerView

自定义加载更多的footerView，需要继承`IRecycleLoadMoreUIHandler`,**这里需要注意，由于主控件使用的是RecyclerView，RecyclerView在footerView的处理上和以前的ListView之间是有区别的，ListView会提供一个addFooterView()，但是RecyclerView并没有RecyclerView这里的处理方式是通过adapter进行FooterView的添加的，这点请注意**

IRecycleLoadMoreUIHandler:
```

public interface IRecycleLoadMoreUIHandler {
   
    void onLoading(IRecycleLoadMoreContainer container);

    void onLoadFinish(IRecycleLoadMoreContainer container, boolean empty, boolean hasMore);

    void onWaitToLoadMore(IRecycleLoadMoreContainer container);

    void onLoadError(IRecycleLoadMoreContainer container, int errorCode, String errorMessage);
}

```

配置代码：
```

// 初始化Footer
RecycleFixFooterView footerView = new RecycleFixFooterView();

 //添加UI监听
id_ptr_frame.setLoadMoreUIHandler(footerView);

//这个适配器是我自己惯用的一种适配写法，这里只是提供参考，在没理解这个的处理方式的前提下，请谨慎食用。
RecycleAnimatorViewAdapter<IRecycleAnimatorListItem> adapter = new RecycleAnimatorViewAdapter<>(this, cacheList);

// 这个地方比较特殊，由于RecyclerView自身的特殊性，FooterView是不能通过addFooterView方法进行加入，
// 只能通过适配器去做footer的界面设定,所以千万别漏了这个方法，如果你想用到LoadMore的效果的话
//此方法为自定义方法
adapter.addFooterView(footerView);
 
```

加载完成之后请调用
```
//参数就不翻译了吧- -看不懂回去问英语老师去
id_ptr_frame.loadMoreFinish(boolean emptyResult, boolean hasMore);
```


####基本的RecyclerView配置

楼下代码是我个人写习惯的一种代码风格，并且有一些沉淀的东西在里面，比方说自定义的Adapter，如果看不懂请使用自己写的

```
// RecyclerView的设定，如果不懂这方面的知识请百度
// 设置布局形式，Linear、grid、瀑布流
id_recycler_view.setLayoutManager(new LinearLayoutManager(this));
// 设置每一项的加载动画
id_recycler_view.setItemAnimator(new DefaultItemAnimator());

// 缓存数据
if (cacheList == null)
    cacheList = new ArrayList<>();
//设定适配器
adapter = new RecycleAnimatorViewAdapter<>(this, cacheList);
id_recycler_view.setAdapter(adapter);

```

#####提供Log日志查看方法

```
id_ptr_frame.setCustomDebug(true);
```



##联系方式和问题建议
-------------------------------------

 - 邮箱 sloveaoqi@163.com
