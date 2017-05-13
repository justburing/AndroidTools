package com.burning.smile.androidtools.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Burning on 16/3/3.
 */
public abstract class BaseBaseAdapter extends BaseAdapter{
    protected abstract int getLayoutId();
    protected abstract BaseViewHolder getViewHolder(View convertView);
    protected abstract void initView(int position, View convertView, ViewGroup parent, BaseViewHolder bvh);

    protected Context mContext;

    private ArrayList<Object> mData = new ArrayList<>();

    public BaseBaseAdapter(Context c){
        mContext = c;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = getConvertView();
        BaseViewHolder vh;
        if(null != convertView.getTag()){
            vh = (BaseViewHolder) convertView.getTag();
        } else {
            vh = getViewHolder(convertView);
            convertView.setTag(vh);
        }
        initView(position, convertView, parent, vh);
        return convertView;
    }

    protected View getConvertView(){
        return LayoutInflater.from(mContext).inflate(getLayoutId(), null);
    }

    public void setData(ArrayList<Object> data){
        if(null == data){
            mData.clear();
        } else {
            mData = new ArrayList<>(data);
        }
    }
    public ArrayList<Object> getData(){
        return mData;
    }
    public void addItem(Object data){
        mData.add(data);
    }

    public Object removeItem(int position){
        return mData.remove(position);
    }

    protected class BaseViewHolder{}

    /**
     * 此方法是本次listview嵌套listview的核心方法：计算parentlistview item的高度。
     * 如果不使用此方法，无论innerlistview有多少个item，则只会显示一个item。
     **/
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {        return;    }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
