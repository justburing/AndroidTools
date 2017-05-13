package com.burning.smile.androidtools.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.burning.smile.androidtools.R;


/**
 * Created by Rising on 2016/4/13.
 */
public class LoadListView extends ListView implements AbsListView.OnScrollListener{
    View footer;
    int totalItemCount;
    int lastVisibleItem;
    boolean isLoading;
    ILoadListener loadListener;
    public LoadListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }

    /**
     * add the footBar to the ListView
     * @param context
     */
    private void initView(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.load_list_footer,null);
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //  when scroll the bottom
        if(totalItemCount == lastVisibleItem&&scrollState == SCROLL_STATE_IDLE){
            if(!isLoading){
                isLoading = true;
                footer.findViewById(R.id.load_layout).setVisibility(VISIBLE);
                footer.findViewById(R.id.no_more).setVisibility(GONE);
                //Load more
                loadListener.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem+visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    /**
     * load data finished
     */
    public void loadComplete(){
        isLoading = false;
        footer.findViewById(R.id.load_layout).setVisibility(GONE);
    }

    public void loadNoMore(){
        footer.findViewById(R.id.load_layout).setVisibility(GONE);
        footer.findViewById(R.id.no_more).setVisibility(VISIBLE);
    }

    public void setLoadListener(ILoadListener listener){
        this.loadListener = listener;
    }
    /**
     * callBack for Load more data
     */
    public interface ILoadListener{
        void onLoad();
    }
}
