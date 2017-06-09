package com.dftc.logutils.printer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.dftc.logutils.BuildConfig;
import com.dftc.logutils.tools.CrashHandler;
import com.dftc.logutils.config.LogConfig;
import com.dftc.logutils.LogInterface;
import com.dftc.logutils.adapter.LogAdapter;
import com.dftc.logutils.adapter.NativeLogAdapter;
import com.dftc.logutils.adapter.StressLogAdapter;
import com.dftc.logutils.utils.FormatDate;
import com.dftc.logutils.utils.Utils;

import static com.dftc.logutils.LogUtils.TAG;

/**
 * Created by xuqiqiang on 2017/6/2.
 */
public class LogPrinter implements Printer {

    private static final int MAX_TMP_SIZE = 10000;
    private LogConfig config;
    private LogAdapter mLogAdapter;
    private StringBuilder writeLogTmp;
    private LogInterface mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "connect service");
            mService = LogInterface.Stub.asInterface(service);
            if (writeLogTmp.length() > 0) {
                startWrite("");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "disconnect service");
            mService = null;
        }
    };

    public LogPrinter() {
        this.config = LogConfig.newBuilder()
                .debug(BuildConfig.DEBUG)
                .build();
        this.mLogAdapter = new NativeLogAdapter();
        this.writeLogTmp = new StringBuilder();
    }

    private void bindService() {
        if (mService != null)
            return;
        Intent intent = new Intent("com.dftc.service.logrecorder.LogUtilsService");
        try {
            intent = Utils.createExplicitFromImplicitIntent(config.context, intent);
            if (intent == null) {
                Log.e(TAG, "LogUtilsService do not exist!");
                return;
            }
            config.context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public LogConfig getConfig() {
        return this.config;
    }

    @Override
    public void setConfig(LogConfig config) {
        this.config = config;
        if (config.stress)
            this.mLogAdapter = new StressLogAdapter();
        if (config.context != null && !TextUtils.isEmpty(config.dirPath))
            bindService();
        if (config.reportCrash) {
            CrashHandler.getInstance().initialize();
        }
    }

    @Override
    public void finalize() {
        config.dirPath = null;
        if (config.context != null) {
            if (mConnection != null)
                config.context.unbindService(mConnection);
            config.context = null;
        }
    }

    public synchronized void log(LogAdapter logAdapter, int priority,
                                 StackTraceElement ste,
                                 String tag, Throwable throwable, String msg, Object... args) {
        if (config.level > priority ||
                !config.debug && TextUtils.isEmpty(config.dirPath))
            return;
        if (TextUtils.isEmpty(tag)) {
            tag = config.tag;
        }
        String message = Utils.createMessage(throwable, msg, args);

        if (ste == null)
            ste = new Throwable().getStackTrace()[2];
        String codeInfo = "(" + ste.getFileName() + ":"
                + ste.getLineNumber() + ")";
        if (config.debug) {
            String log = message;
            if (log.endsWith(" ") || log.endsWith("\n")) {
                log += codeInfo;
            } else
                log += " " + codeInfo;
            if (logAdapter != null)
                logAdapter.log(priority, tag, log);
            else
                mLogAdapter.log(priority, tag, log);
        }

        if (!TextUtils.isEmpty(config.dirPath)) {
            String writeLog = FormatDate.getFormatTime()
                    + " " + Utils.logLevel(priority) + "/" + tag
                    + codeInfo + " : " + message + "\r\n";
            startWrite(writeLog);
        }
    }

    private synchronized void startWrite(String writeLog) {
        if (mService == null) {
            Log.e(TAG, "mService == null");
            writeLogTmp.append(writeLog);
            if (writeLogTmp.length() > MAX_TMP_SIZE) {
                writeLogTmp.delete(0, writeLogTmp.length() - MAX_TMP_SIZE);
            }
            if (config.context != null && !TextUtils.isEmpty(config.dirPath)) {
                bindService();
            }
            return;
        }

        try {
            if (writeLogTmp.length() > 0) {
                if (!TextUtils.isEmpty(writeLog))
                    writeLogTmp.append(writeLog);
                mService.log(config.dirPath, writeLogTmp.toString());
                writeLogTmp.delete(0, writeLogTmp.length());
                writeLogTmp = new StringBuilder();
            } else
                mService.log(config.dirPath, writeLog);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}