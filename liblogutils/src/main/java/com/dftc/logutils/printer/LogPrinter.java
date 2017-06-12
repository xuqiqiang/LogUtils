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
import com.dftc.logutils.LogInterface;
import com.dftc.logutils.adapter.LogAdapter;
import com.dftc.logutils.config.LogConfig;
import com.dftc.logutils.tools.CrashHandler;
import com.dftc.logutils.utils.FormatDate;
import com.dftc.logutils.utils.Utils;

import static com.dftc.logutils.LogUtils.TAG;

/**
 * Created by xuqiqiang on 2017/6/2.
 */
public class LogPrinter implements Printer {

    private static final int MAX_TMP_SIZE = 10000;
    private LogConfig config;
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
        setConfig(LogConfig.newBuilder()
                .debug(BuildConfig.DEBUG)
                .build());
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
        if (config.enableWrite())
            bindService();
        CrashHandler.getInstance().initialize(config.reportCrash);
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
                !config.debug && !config.enableWrite())
            return;
        if (TextUtils.isEmpty(tag)) {
            tag = config.tag;
        }
        String message = Utils.createMessage(throwable, msg, args);
        String newTag = tag;
        if (config.codeInfo) {
            if (ste == null)
                ste = new Throwable().getStackTrace()[2];
            String codeInfo = "(" + ste.getFileName() + ":"
                    + ste.getLineNumber() + ")";
            newTag += codeInfo;
        }
        if (config.debug) {
            if (logAdapter == null) {
                logAdapter = config.logAdapter;
            }
            if (logAdapter.isLoggable(priority, tag))
                logAdapter.log(priority, newTag, message);
        }
        if (config.enableWrite()) {
            String writeLog = FormatDate.getFormatTime()
                    + " " + Utils.logLevel(priority) + "/" + newTag
                    + " : " + message + "\r\n";
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
            if (config.enableWrite()) {
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