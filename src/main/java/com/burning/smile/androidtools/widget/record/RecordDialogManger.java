package com.burning.smile.androidtools.widget.record;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.burning.smile.androidtools.R;

import java.text.DecimalFormat;

/**
 * 作者    Burning
 * 时间    2015/10/21 9:38
 */
public class RecordDialogManger {
    private Dialog mDialog;

    private ImageView mIcon;
    private ImageView mVoice;

    private TextView mLable;

    private Context mContext;

    private boolean isShowVoice = true;

    public RecordDialogManger(Context context){
        this.mContext = context;
    }

    public void showRecordingDialog(){
        mDialog = new Dialog(mContext, R.style.enddialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_record,null);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);

        mIcon= (ImageView) mDialog.findViewById(R.id.rec_icon);
        mVoice = (ImageView) mDialog.findViewById(R.id.rec_volume);
        mLable = (TextView) mDialog.findViewById(R.id.rec_text);

//        Window dialogWindow = mDialog.getWindow();
//        WindowManager m = dialogWindow.getWindowManager();
//        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();// 获取对话框当前的参数值
//        lp.gravity = Gravity.CENTER;
//        lp.width = 250;
//        lp.height = 250;
//        lp.height = (int) (d.getWidth() * 0.5); // 高度设置为屏幕的0.6
//        lp.width = (int) (d.getWidth() * 0.5); // 宽度设置为屏幕的0.65
//        dialogWindow.setAttributes(lp);

        mDialog.show();
    }
    public void recording(){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.mipmap.recorder);
            mLable.setText(R.string.rec_cancle);
            isShowVoice = true;
        }
    }

    public void wantToCancel(){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.mipmap.cancel);
            mLable.setText(R.string.rec_cancle_end);
            isShowVoice = false;
        }
    }

    public void tooShort(){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.mipmap.voice_to_short);
            mLable.setText(R.string.rec_tooshort);
            isShowVoice = false;
        }
    }

    public void dimissDialog(){
        if(mDialog!=null&&mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null ;
        }
    }

    /**
     * 通过level去更新voice上的图片
     * @param level
     */
    public void updateVoiceLevel(int level){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            if(isShowVoice){
                mVoice.setVisibility(View.VISIBLE);
            }else{
                mVoice.setVisibility(View.GONE);
            }
            mLable.setVisibility(View.VISIBLE);
            int resId = mContext.getResources().getIdentifier("v"+level,"mipmap",mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }

    /**
     * 如果是自动录音，文字提示剩余时间
     * @param time
     */
    DecimalFormat fnum  =   new  DecimalFormat("##0.0");
    public void updateLastTime(float time){
        mLable.setText("录音时间还剩"+fnum.format(time)+"秒");
    }

}
