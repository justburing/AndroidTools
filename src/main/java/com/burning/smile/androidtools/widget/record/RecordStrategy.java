package com.burning.smile.androidtools.widget.record;

/**
 * 作者    Burning
 * 时间    2015/10/19 23:37

 */
public interface RecordStrategy {
    /**
     * 在这里进行录音准备工作，重置录音文件名等
     */
    public void prepared();
    /**
     * 释放资源
     */
    public void release();

    /**
     * 取消录音
     */
    public void cancel();

    /**
     * 录音失败时删除原来的旧文件
     */
    public void deleteOldFile();

    /**
     * 获取录音音量的大小
     * @return
     */
    public double getVoiceLevel(int maxlevel);

    /**
     * 返回录音文件完整路径
     * @return
     */
    public String getFilePath();
}
