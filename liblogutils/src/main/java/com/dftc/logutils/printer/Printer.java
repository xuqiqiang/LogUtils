package com.dftc.logutils.printer;

import com.dftc.logutils.adapter.LogAdapter;
import com.dftc.logutils.config.LogConfig;

/**
 * Created by xuqiqiang on 2017/6/7.
 */
public interface Printer {

    LogConfig getConfig();

    void setConfig(LogConfig config);

    void finalize();

    void log(LogAdapter logAdapter, int priority,
             StackTraceElement ste, String tag,
             Throwable throwable, String msg, Object... args);

}
