package com.burning.smile.androidtools.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.burning.smile.androidtools.R;
import com.burning.smile.androidtools.tools.AndroidBitmapUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.androidtools.tools.VolleyManager;
import com.burning.smile.androidtools.widget.photoview.OnSingleTapConfirmedListener;
import com.burning.smile.androidtools.widget.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class PhotoShowAdapter extends PagerAdapter {
    private List<String> mPaths;
    private ArrayList<String> mDelPaths = new ArrayList<>();

    private Context mContext;
    private ImageEventListener imageEventListener;

    public PhotoShowAdapter(Context c, List<String> paths,ImageEventListener imageEventListener) {
        mContext = c;
        mPaths = paths;
        this.imageEventListener = imageEventListener;
    }

    public void addItem(Object obj){
        if(obj instanceof String){
            if(mPaths == null){
                mPaths = new ArrayList<>();
            }
            mPaths.add((String) obj);
        }
    }

    public Object remove(int position) {
        return mPaths.remove(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public Object instantiateItem (ViewGroup container, final int position) {
        RelativeLayout rl = new RelativeLayout(mContext);
        //MatrixImageView iv = new MatrixImageView(mContext);
        final PhotoView iv = new PhotoView(mContext);
        TextView tv = new TextView(mContext);
        ImageView iv_del = new ImageView(mContext);

        int h = 180;
        int margin = 30;
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setPadding(5,5,margin,5);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(18);
        tv.setText((position + 1) + "/" + getCount());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,h);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tv.setLayoutParams(lp);

        lp =  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        iv_del.setImageResource(R.mipmap.ic_small_trash);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        iv.setPadding(0,0,margin,0);
        iv_del.setLayoutParams(lp);

        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv.setLayoutParams(lp);

        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rl.setLayoutParams(vl);

        iv.setOnSingleTapConfirmedListener(new OnSingleTapConfirmedListener() {
            @Override
            public void onSingleTapConfirmed(MotionEvent e) {
                Intent mIntent = new Intent();
                mIntent.putStringArrayListExtra("delPics", mDelPaths);
                ((Activity) mContext).setResult(Activity.RESULT_OK,mIntent);
                ((Activity) mContext).finish();
            }
        });
        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPaths.size() > 1) {
                    imageEventListener.delImage(mPaths.get(position), position);
                    mDelPaths.add(mPaths.get(position));
                    remove(position);
                    notifyDataSetChanged();
                } else {
                    imageEventListener.delImage(mPaths.get(position), position);
                    mDelPaths.add(mPaths.get(position));
                    remove(position);
                    Intent mIntent = new Intent();
                    mIntent.putStringArrayListExtra("delPics", mDelPaths);
                    ((Activity) mContext).setResult(Activity.RESULT_OK, mIntent);
                    ((Activity) mContext).finish();
                }
            }
        });

        rl.addView(iv);
        rl.addView(tv);
        rl.addView(iv_del);
        (container).addView(rl, 0);
        if(mPaths.get(position).contains("http")){
//            AndroidFragUtil.showDialog(((AppCompatActivity)mContext).getSupportFragmentManager(),new LoadingFragment());
            VolleyManager.getInstance(mContext).getImageLoader().get(mPaths.get(position), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    iv.setImageBitmap(response.getBitmap());
                    AndroidFragUtil.dismissDialog(((AppCompatActivity)mContext).getSupportFragmentManager());
                }
                @Override
                public void onErrorResponse(VolleyError error) {
                    AndroidFragUtil.dismissDialog(((AppCompatActivity)mContext).getSupportFragmentManager());
                }
            }, 720, 1080);
        }else{
            iv.setImageBitmap(AndroidBitmapUtil.decodeSampledBitmapFromFile(mPaths.get(position), 720, 1080));
        }
        return rl;
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
    public interface ImageEventListener{
         void delImage(String path, int position);
    }
}
