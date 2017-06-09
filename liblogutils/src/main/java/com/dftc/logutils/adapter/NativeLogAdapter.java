package com.dftc.logutils.adapter;

import android.util.Log;

/**
 * Created by xuqiqiang on 2017/6/7.
 */
public class NativeLogAdapter implements LogAdapter {

    public NativeLogAdapter() {

    }

    @Override
    public boolean isLoggable(int priority, String tag) {
        return true;
    }

    @Override
    public void log(int priority, String tag, String message) {
        Log.println(priority, tag, message);
    }

}