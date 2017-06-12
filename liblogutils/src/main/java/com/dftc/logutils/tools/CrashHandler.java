package com.dftc.logutils.tools;

import android.util.Log;

import com.dftc.logutils.LogUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Created by xuqiqiang on 2017/6/7.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private static final CrashHandler INSTANCE = new CrashHandler();
    private UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize(boolean reportCrash) {
        if (reportCrash) {
            if (!Thread.getDefaultUncaughtExceptionHandler().equals(this)) {
                Log.d("CrashHandler", "getDefaultUncaughtExceptionHandler()");
                mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(this);
            }
        } else {
            if (mDefaultHandler != null) {
                if (Thread.getDefaultUncaughtExceptionHandler().equals(this)) {
                    Log.d("CrashHandler", "setDefaultUncaughtExceptionHandler(mDefaultHandler)");
                    Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler);
                }

            }
        }
    }

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String message = "AndroidRuntime: Shutting down VM\r\n"
                + writer.toString();
        LogUtils.S.e(message);

        mDefaultHandler.uncaughtException(t, e);
    }
}
