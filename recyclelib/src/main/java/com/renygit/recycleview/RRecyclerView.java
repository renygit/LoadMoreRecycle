package com.renygit.recycleview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import cn.bingoogolapple.androidcommon.adapter.BGABindingRecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAHeaderAndFooterAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;

/**
 * Created by admin on 2017/6/28.
 */

public class RRecyclerView extends RecyclerView {

    private boolean mLoadMoreEnabled = true;
    private boolean mLoadingData = false;//是否正在加载数据
    private OnLoadMoreListener mLoadMoreListener;

    private LoadMoreView mLoadMoreView;
    /**
     * 当前RecyclerView类型
     */
    protected LayoutManagerType layoutManagerType;
    /**
     * 最后一个可见的item的位置
     */
    private int lastVisibleItemPosition;
    /**
     * 最后一个的位置
     */
    private int[] lastPositions;
    /**
     * Y轴移动的实际距离（最顶部为0）
     */
    private int mScrolledYDistance = 0;

    /**
     * X轴移动的实际距离（最左侧为0）
     */
    private int mScrolledXDistance = 0;
    /**
     * 是否需要监听控制
     */
    private boolean mIsScrollDown = true;

    private RScrollListener mRScrollListener;

    /**
     * 当前滑动的状态
     */
    private int currentScrollState = 0;

    private boolean isNoMore = false;

    /**
     * 触发在上下滑动监听器的容差距离
     */
    private static final int HIDE_THRESHOLD = 20;

    /**
     * 滑动的距离
     */
    private int mDistance = 0;

    public void setRScrollListener(RScrollListener listener) {
        mRScrollListener = listener;
    }

    public interface RScrollListener {
        void onScrollUp();//scroll down to up

        void onScrollDown();//scroll from up to down

        void onScrolled(int distanceX, int distanceY);// moving state,you can get the move distance

        void onScrollStateChanged(int state);
    }

    public RRecyclerView(Context context) {
        this(context, null);
    }

    public RRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (mLoadMoreEnabled) {
            setLoadMoreFooter(new LoadMoreView(getContext()));
        }
    }

    /**
     * 设置自定义的footerview
     */
    private void setLoadMoreFooter(LoadMoreView loadMoreFooter) {
        this.mLoadMoreView = loadMoreFooter;

        //wxm:mFootView inflate的时候没有以RecyclerView为parent，所以要设置LayoutParams
        ViewGroup.LayoutParams layoutParams = mLoadMoreView.getLayoutParams();
        if (layoutParams != null) {
            mLoadMoreView.setLayoutParams(new LayoutParams(layoutParams));
        } else {
            mLoadMoreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }

    //获取LoadMoreView 可以设置自定义布局
    public LoadMoreView getLoadMoreView() {
        return mLoadMoreView;
    }

    public void setConfig(RStyleConfig config) {
        if(null != mLoadMoreView){
            mLoadMoreView.setConfig(config);
        }
    }

    /**
     * 到底加载是否可用
     */
    public void setLoadMoreEnabled(boolean enabled) {
        mLoadMoreEnabled = enabled;
        if (!enabled) {
            mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_HIDE);
        }
    }

    /**
     * 设置是否已加载全部
     *
     * @param noMore
     */
    public void setNoMore(boolean noMore) {
        mLoadingData = false;
        isNoMore = noMore;
        if (isNoMore) {
            mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_THEEND);
        } else {
            mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_HIDE);
        }
    }

    public void setError() {
        mLoadingData = false;
        mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_ERROR);
    }

    public void setLoading() {
        mLoadingData = true;
        mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_LOADING);
    }

    public void loadComplete() {
        if (mLoadingData) {
            mLoadingData = false;
            mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_HIDE);
        }
    }


    @Override
    public void setAdapter(Adapter adapter){
        if (null == adapter) return;
        if(adapter instanceof BGABindingRecyclerViewAdapter){
            BGABindingRecyclerViewAdapter mWrapAdapter = (BGABindingRecyclerViewAdapter) adapter;
            if (mLoadMoreEnabled && mWrapAdapter.getFootersCount() == 0) {
                mWrapAdapter.addFooterView(mLoadMoreView);
            }

            if (mLoadMoreEnabled) {
                super.setAdapter(mWrapAdapter.getHeaderAndFooterAdapter());
            } else {
                super.setAdapter(mWrapAdapter);
            }
        }else if(adapter instanceof BGAHeaderAndFooterAdapter){
            BGAHeaderAndFooterAdapter mWrapAdapter = (BGAHeaderAndFooterAdapter) adapter;
            if (mLoadMoreEnabled && mWrapAdapter.getFootersCount() == 0) {
                mWrapAdapter.addFooterView(mLoadMoreView);
            }
            super.setAdapter(mWrapAdapter);
        }else if(adapter instanceof BGARecyclerViewAdapter){
            BGARecyclerViewAdapter mWrapAdapter = (BGARecyclerViewAdapter) adapter;
            if (mLoadMoreEnabled && mWrapAdapter.getFootersCount() == 0) {
                mWrapAdapter.addFooterView(mLoadMoreView);
            }
            if (mLoadMoreEnabled) {
                super.setAdapter(mWrapAdapter.getHeaderAndFooterAdapter());
            } else {
                super.setAdapter(mWrapAdapter);
            }
        }else {
            throw new RuntimeException("adapter is not BGABindingRecyclerViewAdapter or BGARecyclerViewAdapter or BGAHeaderAndFooterAdapter!");
        }

    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setOnRetryListener(final View.OnClickListener listener) {
        mLoadMoreView.setOnRetry(listener);
    }


    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        int firstVisibleItemPosition = 0;
        RecyclerView.LayoutManager layoutManager = getLayoutManager();

        if (layoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LayoutManagerType.LinearLayout;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LayoutManagerType.GridLayout;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LayoutManagerType.StaggeredGridLayout;
            } else {
                throw new RuntimeException(
                        "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (layoutManagerType) {
            case LinearLayout:
                firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GridLayout:
                firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case StaggeredGridLayout:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(lastPositions);
                firstVisibleItemPosition = findMax(lastPositions);
                break;
        }

        // 根据类型来计算出第一个可见的item的位置，由此判断是否触发到底部的监听器
        // 计算并判断当前是向上滑动还是向下滑动
        calculateScrollUpOrDown(firstVisibleItemPosition, dy);
        // 移动距离超过一定的范围，我们监听就没有啥实际的意义了
        mScrolledXDistance += dx;
        mScrolledYDistance += dy;
        mScrolledXDistance = (mScrolledXDistance < 0) ? 0 : mScrolledXDistance;
        mScrolledYDistance = (mScrolledYDistance < 0) ? 0 : mScrolledYDistance;
        if (mIsScrollDown && (dy == 0)) {
            mScrolledYDistance = 0;
        }
        //Be careful in here
        if (null != mRScrollListener) {
            mRScrollListener.onScrolled(mScrolledXDistance, mScrolledYDistance);
        }

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        currentScrollState = state;

        if (mRScrollListener != null) {
            mRScrollListener.onScrollStateChanged(state);
        }

        if (mLoadMoreListener != null && mLoadMoreEnabled) {
            if (currentScrollState == RecyclerView.SCROLL_STATE_IDLE) {
                RecyclerView.LayoutManager layoutManager = getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (visibleItemCount > 0
                        && lastVisibleItemPosition >= totalItemCount - 1
                        && totalItemCount > visibleItemCount
                        && !isNoMore) {

                    mLoadMoreView.setVisibility(View.VISIBLE);
                    if (mLoadingData) {
                        return;
                    } else {
                        setLoading();
                        mLoadMoreListener.onLoadMore();
                    }

                }

            }
        }

    }

    /**
     * 计算当前是向上滑动还是向下滑动
     */
    private void calculateScrollUpOrDown(int firstVisibleItemPosition, int dy) {
        if (null != mRScrollListener) {
            if (firstVisibleItemPosition == 0) {
                if (!mIsScrollDown) {
                    mIsScrollDown = true;
                    mRScrollListener.onScrollDown();
                }
            } else {
                if (mDistance > HIDE_THRESHOLD && mIsScrollDown) {
                    mIsScrollDown = false;
                    mRScrollListener.onScrollUp();
                    mDistance = 0;
                } else if (mDistance < -HIDE_THRESHOLD && !mIsScrollDown) {
                    mIsScrollDown = true;
                    mRScrollListener.onScrollDown();
                    mDistance = 0;
                }
            }
        }

        if ((mIsScrollDown && dy > 0) || (!mIsScrollDown && dy < 0)) {
            mDistance += dy;
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public enum LayoutManagerType {
        LinearLayout,
        StaggeredGridLayout,
        GridLayout
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
