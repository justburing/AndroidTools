package com.burning.smile.androidtools.widget.record;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class RecordButton extends Button implements AudioRecorder.AudioStateListener {
    private static final float MIN_RECORD_TIME = 1f;    // 最短录音时间，单位秒
    private static final int DISTANCE_Y_CANCLE = 50; //手指滑动的最小距离
    public static final int RECORD_OFF = 1;        // 不在录音
    public static final int RECORD_ON = 2;         // 正在录音
    public static final int RECORD_CANCEL = 3;      // 取消录音

    private boolean isRecording = false;             //是否已经开始录音
    private int recordState = RECORD_OFF;            //录音状态
    private float recordTime = 0.0f;         //录音时长，如果太短则表示录音失败

    //是否触发longClick
    private boolean mReady = false;

    private RecordDialogManger mDialogManager;
    private AudioRecorder mAudioRecorderManger;
    /**
     * 获取音量大小
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording){
                try {
                    Thread.sleep(100);
                    recordTime+=0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public RecordButton(Context context) {
        this(context, null);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDialogManager = new RecordDialogManger(getContext());
        mAudioRecorderManger = AudioRecorder.getInstance();
        mAudioRecorderManger.setOnAudioStateListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mReady = true;
                mAudioRecorderManger.prepared();
                return false;
            }
        });
    }

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    /**
     * 录音完成后的回调
     */
    public interface  AudioFinishRecorderListener{
        void play();
        void onFinish(float seconds, String filePath);
    }
    private AudioFinishRecorderListener mListener;
    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        this.mListener = listener;
    }
    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGED = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;

    private Handler mHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_AUDIO_PREPARED:
                    //显示应该是在auio end prepared以后
                    mDialogManager.showRecordingDialog();
                    mDialogManager.recording();
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mAudioRecorderManger.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManager.dimissDialog();
                    break;
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                changeState(RECORD_ON);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(true);
                if (!mReady) {
                    //连LongClick都没有触发，直接reset就可以
                    reset();
                    if(mListener!=null){
                        mListener.play();
                    }
                    return super.onTouchEvent(event);
                }
                if(!isRecording ||recordTime < MIN_RECORD_TIME){
                    //录音还没初始化好||录音时间小于6秒
                    mDialogManager.tooShort();
                    mAudioRecorderManger.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS,1300);
                }else if(recordState == RECORD_ON){
                    //正常录制结束
                    mDialogManager.dimissDialog();
                    if(mListener!=null){
                        mListener.onFinish(recordTime,mAudioRecorderManger.getFilePath());
                    }
                    mAudioRecorderManger.release();
                } else if (recordState == RECORD_CANCEL) {
                    mDialogManager.dimissDialog();
                    mAudioRecorderManger.cancel();
                }
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                if (isRecording) {
                    //根据xy的坐标，判断是否取消
                    if (wantToCancel(x, y)) {
                        changeState(RECORD_CANCEL);
                    } else {
                        changeState(RECORD_ON);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    //判断是否需要取消录音操作
    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -DISTANCE_Y_CANCLE || y > getWidth() + DISTANCE_Y_CANCLE) {
            return true;
        }
        return false;
    }

    //重置录音状态
    private void reset() {
        isRecording = false;
        mReady = false;
        recordTime = 0;
        changeState(RECORD_OFF);
    }


    private void changeState(int state) {
        if (recordState != state) {
            recordState = state;
            switch (state) {
                case RECORD_OFF:
//                    setText(R.string.rec_normal);
                    break;
                case RECORD_ON:
//                    setText(R.string.rec_recording);
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;
                case RECORD_CANCEL:
//                    setText(R.string.rec_cancle);
                    mDialogManager.wantToCancel();
                    break;
                default:
                    break;
            }

        }
    }


}
