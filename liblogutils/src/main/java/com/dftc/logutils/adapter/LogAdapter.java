package com.dftc.logutils.adapter;

/**
 * Created by xuqiqiang on 2017/6/7.
 */
public interface LogAdapter {

    boolean isLoggable(int priority, String tag);

    void log(int priority, String tag, String message);
}