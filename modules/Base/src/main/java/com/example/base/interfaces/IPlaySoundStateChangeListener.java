package com.example.base.interfaces;

/**
 * @author ZJ
 * @time 17/7/4 10:48
 * @des
 */

public interface IPlaySoundStateChangeListener {

    /**
     * 播放结束
     */
    void playComplete();

    void playStart();

    void playError();

    void playInterrupt();
}
