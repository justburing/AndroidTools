package com.burning.smile.androidtools.widget.record;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AudioRecorder {

    private MediaRecorder mediaRecorder;
    private String filePath;
    //TODO 最好需要进行外部存储卡是否可读判断
    private static File saveDirectory = Environment.getExternalStorageDirectory();      //文件存储根目录;
    private static String vocDir = "VOC";
    private File mediaStorageDir = new File(saveDirectory,vocDir);
    private boolean isPrepared = false;

    private static AudioRecorder mInstance;

    private AudioRecorder(){}

    /**
     * 回调准备完成
     */
    public interface AudioStateListener{
        void wellPrepared();
    }
    public AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener listener){
        mListener = listener;
    }

    public static AudioRecorder getInstance() {
        if (mInstance == null) {
            synchronized (AudioRecorder.class) {
                if (mInstance == null) mInstance = new AudioRecorder();
            }
        }
        return mInstance;
    }


    public void prepared() {
        try {
            isPrepared = false;

            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdir();
            }
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            filePath = mediaStorageDir.getPath() + File.separator + "AUD_" + timeStamp + ".amr";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setOutputFile(filePath);                                  //设置输出路径
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);            //设置音频源为麦克风
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);    //设置录制的音频格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);       //设置录制音频的编码为amr

            mediaRecorder.prepare();
            mediaRecorder.start();
            isPrepared = true;
            if(mListener!=null){
                mListener.wellPrepared();
            }
        } catch (IOException e) {
          //  e.printStackTrace();
            Log.e("AudioRecorder","prepare failed："+e.toString());
        }
    }

    public void release() {
        isPrepared = false;
        if(mediaRecorder!=null){
            try {
                mediaRecorder.stop();
            } catch(RuntimeException e) {
                deleteOldFile();  //you must delete the outputfile when the recorder stop failed.
            } finally {
                mediaRecorder.release();
                mediaRecorder = null;
            }
        }
    }

    public void cancel() {
        release();
        deleteOldFile();
    }

    public void deleteOldFile() {
        if(filePath!=null){
            File file = new File(filePath);
            file.delete();
            filePath = null ;
        }
    }

    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            try {
                //mediaRecorder.getMaxAmplitude() 1-32767
                return maxLevel * mediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (IllegalStateException e) {
                Log.e("AudioRecorder","mediaRecorder.getMaxAmplitude() wrong");
            }
        }
        return 1;
    }

    public String getFilePath() {
        if(filePath!=null){
            return filePath;
        }
        return null;
    }
    public void setMediaDir(File dir){
        this.mediaStorageDir = dir;
    }
    public static void clearDirectory(){
        File[] files = (new File(saveDirectory,vocDir)).listFiles();
        for(int i=0;i<files.length;i++){
            File file = files[i];
            file.delete();
        }

    }
}
