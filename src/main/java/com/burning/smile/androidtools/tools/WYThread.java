package com.burning.smile.androidtools.tools;

/**
 * Created by Burning on 2016/5/13.
 */
public class WYThread extends Thread {
    volatile boolean stop;

    public void terminate() {
        stop = true;
    }

    @Override
    public synchronized void start() {
        stop = false;
        super.start();
    }

    public boolean isTerminated() {
        return stop;
    }
}
