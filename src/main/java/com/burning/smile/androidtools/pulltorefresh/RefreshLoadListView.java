package com.burning.smile.androidtools.pulltorefresh;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.burning.smile.androidtools.R;
import com.burning.smile.androidtools.refreshswipe.OnXScrollListener;

import java.text.SimpleDateFormat;

public class RefreshLoadListView extends ListView implements AbsListView.OnScrollListener {

    private static final String TAG = "RefreshListView";
    private Context context;

    private PullToRefreshListHeader headerView; // 头布局的对象
    private RelativeLayout mHeaderViewContent;
    private TextView tvLastUpdateTime; // 头布局的最后更新时间

    private OnRefreshListener mOnRefershListener;
    private PullToRefreshListFooter footerView; // 脚布局的对象
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false; // is refreashing.
    private int mHeaderViewHeight; // header view's height
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private int mTotalItemCount;
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;
    private final static int SCROLL_DURATION = 400;
    private final static int PULL_LOAD_MORE_DELTA = 50;
    private final static float OFFSET_RADIO = 1.8f;
    private float mLastY=-1;
    private float mDownX;
    private float mDownY;
    public RefreshLoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initHeaderView();  
        initFooterView();
        mScroller = new Scroller(context, new DecelerateInterpolator());
        this.setOnScrollListener(this);  
    }  
  
    /** 
     * 初始化脚布局 
     */  
    private void initFooterView() {
        // init footer view
        footerView = new PullToRefreshListFooter(context);
        this.addFooterView(footerView);
    }  
  
    /** 
     * 初始化头布局 
     */  
    private void initHeaderView() {
        headerView = new PullToRefreshListHeader(context);
        mHeaderViewContent = (RelativeLayout) headerView.findViewById(R.id.xlistview_header_content);
        tvLastUpdateTime = (TextView) headerView.findViewById(R.id.xlistview_header_time);// init header height
        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeaderViewHeight = mHeaderViewContent.getHeight();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        addHeaderView(headerView);
    }  
  
    /** 
     * 获得系统的最新时间 
     *  
     * @return 
     */  
    private String getLastUpdateTime() {  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());  
    }

public boolean onTouchEvent(MotionEvent ev) {
    if (mLastY == -1) {
        mLastY = ev.getRawY();
    }
    switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mLastY = ev.getRawY();
            mDownX = ev.getX();
            mDownY = ev.getY();
         
            break;
        case MotionEvent.ACTION_MOVE:
            final float deltaY = ev.getRawY() - mLastY;

            float dy = Math.abs((ev.getY() - mDownY));
            float dx = Math.abs((ev.getX() - mDownX));
            mLastY = ev.getRawY();

            if (Math.pow(dx, 2) / Math.pow(dy, 2) <= 3) {
                if (getFirstVisiblePosition() == 0 && (headerView.getVisiableHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                } else if ((footerView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
            }

            break;
        case MotionEvent.ACTION_UP:
            mLastY = -1; // reset
            if ((getLastVisiblePosition()==mTotalItemCount-1)&&mEnablePullLoad && footerView.getHeight()>0 && footerView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                startLoadMore();
                resetFooterHeight();
                new ResetHeaderHeightTask().execute();
            } else if (getFirstVisiblePosition() == 0) {
                // invoke refresh
                if (mEnablePullRefresh && headerView.getVisiableHeight() > mHeaderViewHeight) {
                    mPullRefreshing = true;
                    headerView.setState(PullToRefreshListHeader.STATE_REFRESHING);
                    if (mOnRefershListener != null) {
                        mOnRefershListener.onRefresh();
                    }
                }
                resetHeaderHeight();
            }

            break;
    }
    return super.onTouchEvent(ev);
}

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                headerView.setVisiableHeight(mScroller.getCurrY());
            } else {
                footerView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    /** 
     * 设置刷新监听事件 
     *  
     * @param listener 
     */  
    public void setOnRefreshListener(OnRefreshListener listener) {  
        mOnRefershListener = listener;  
    }  

    class ResetHeaderHeightTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            mPullRefreshing = false;
            headerView.setState(com.burning.smile.androidtools.refreshswipe.PullToRefreshListHeader.STATE_NORMAL);
            resetHeaderHeight();

        }
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            footerView.hide();
            footerView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            footerView.show();
            footerView.setState(PullToRefreshListFooter.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
            footerView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            footerView.setState(PullToRefreshListFooter.STATE_NORMAL);
        }
    }
    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        tvLastUpdateTime.setText(time);
        tvLastUpdateTime.setText(getLastUpdateTime());
    }
    public void setRefreshTime() {
        tvLastUpdateTime.setText(getLastUpdateTime());
    }
    private void updateHeaderHeight(float delta) {
        headerView.setVisiableHeight((int) delta + headerView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) {
            if (headerView.getVisiableHeight() > mHeaderViewHeight) {
                headerView.setState(PullToRefreshListHeader.STATE_READY);
            } else {
                headerView.setState(PullToRefreshListHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = headerView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    private void updateFooterHeight(float delta) {
        int height = footerView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                // more.
                footerView.setState(PullToRefreshListFooter.STATE_READY);
            } else {
                footerView.setState(PullToRefreshListFooter.STATE_NORMAL);
            }
        }
        footerView.setBottomMargin(height);

        //setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    private void resetFooterHeight() {
        int bottomMargin = footerView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {
        mPullLoading = true;
        footerView.setState(PullToRefreshListFooter.STATE_LOADING);
        if (mOnRefershListener != null) {
            mOnRefershListener.onLoadMore();
        }
    }

}