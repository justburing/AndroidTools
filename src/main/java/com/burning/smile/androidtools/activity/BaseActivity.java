package com.burning.smile.androidtools.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.burning.smile.androidtools.R;


/**
 * Created by Burning on 16/3/3.
 */
public abstract class BaseActivity extends AppCompatActivity{
    protected final String TAG = "==="+getClass().getName()+"===";
    protected abstract void init();
    protected abstract int getLayoutId();

    protected Toolbar mToolbar;
    protected TextView mToolBarTitle;

    private boolean isToolbarBackEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (null != findViewById(R.id.toolbar)) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        }
        setToolbarTitleColor(Color.parseColor("#ffffff"));
        init();
        if (null != mToolbar) {
            mToolbar.setTitle("");
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(isToolbarBackEnabled);
            getSupportActionBar().setDisplayShowHomeEnabled(isToolbarBackEnabled);
            customToolBar();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initInResume();
    }
    protected void initInResume(){
        //需要在onResume周期处理的数据
    }
    public void customToolBar(){
        //箭头变白
        final Drawable upArrow = ContextCompat.getDrawable(this,R.mipmap.ic_back);
//        upArrow.setBounds(0, 0, 16,16);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        upArrow.setColorFilter(ContextCompat.getColor(this,R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setToolbarTitle(@StringRes int resId){
//        if(mToolbar != null){
//            mToolbar.setTitle(resId);
//        }
        if(mToolBarTitle!=null){
            mToolBarTitle.setText(resId);
        }
    }

    public void setToolbarTitle(CharSequence title){
//        if(mToolbar != null){
//            mToolbar.setTitle(title);
//        }
        if(mToolBarTitle!=null){
            mToolBarTitle.setText(title);
        }
    }

    public void setToolbarTitleColor(@ColorInt int color){
//        if(mToolbar != null){
//            mToolbar.setTitleTextColor(color);
//        }
        if(mToolBarTitle!=null){
            mToolBarTitle.setTextColor(color);
        }
    }

    public void setToolbarBackEnabled(boolean enabled){
        isToolbarBackEnabled = enabled;
    }

    public int getScreenWidth() {
        return ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }

    public int getScreenHeight() {
        return ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }
    public void toast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    protected void regViewOnClickListener(View.OnClickListener listener, int... ids){
        if(ids != null){
            for (int id : ids){
                View v = findViewById(id);
                if(v != null){
                    v.setOnClickListener(listener);
                }
            }
        }
    }
}
