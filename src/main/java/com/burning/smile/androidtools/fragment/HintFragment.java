package com.burning.smile.androidtools.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.burning.smile.androidtools.R;
import com.burning.smile.androidtools.tools.AndroidFragUtil;

import java.util.Map;

/**
 * Created by Burning on 2016/3/3.
 */
public class HintFragment extends DialogFragment {
    TextView title;
    TextView content;
    String strTitle;
    String strContent;
    Button btn_yes;
    Button btn_no;
    Boolean onlyTitle = false;
    OnConfirmListener confirmListener;
    View.OnClickListener listener;

    public static HintFragment getInstance(OnConfirmListener confirmListener,String title,String content,View.OnClickListener listener) {
        HintFragment context = new HintFragment();
        context.init(confirmListener,title,content,listener);
        return context;
    }
    public static HintFragment getInstance(String title,String content,View.OnClickListener listener) {
        HintFragment context = new HintFragment();
        context.init(title,content,listener);
        return context;
    }
    public static HintFragment getInstance(boolean onlyTitle,String title,String content,View.OnClickListener listener) {
        HintFragment context = new HintFragment();
        context.init(onlyTitle,title,content,listener);
        return context;
    }
    public void init(String title,String content,View.OnClickListener listener){
        this.strTitle = title;
        this.strContent = content;
        this.listener = listener;
    }
    public void init(boolean onlyTitle,String title,String content,View.OnClickListener listener){
        this.onlyTitle = onlyTitle;
        this.strTitle = title;
        this.strContent = content;
        this.listener = listener;
    }
    public void init(OnConfirmListener confirmListener,String title,String content,View.OnClickListener listener){
        this.confirmListener = confirmListener;
        this.strTitle = title;
        this.strContent = content;
        this.listener = listener;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View rootView = inflater.inflate(R.layout.fragment_dialog_confirm,null);
        title = (TextView) rootView.findViewById(R.id.title);
        content = (TextView) rootView.findViewById(R.id.content);
        btn_yes = (Button) rootView.findViewById(R.id.yes);
        btn_no = (Button) rootView.findViewById(R.id.no);
        if(onlyTitle){
            title.setText(strTitle);
            title.setGravity(View.TEXT_ALIGNMENT_CENTER);
            content.setVisibility(View.GONE);
        }else{
            title.setText(strTitle);
            content.setText(strContent);
        }
        bindEventListener();
        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    public void bindEventListener(){
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidFragUtil.dismissDialog(getFragmentManager());
            }
        });
        if(listener==null){
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmListener.handleData(null);
                }
            });
        }else{
            btn_yes.setOnClickListener(listener);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public interface OnConfirmListener{
        void handleData(Map<String, Object> data);
    }
}
