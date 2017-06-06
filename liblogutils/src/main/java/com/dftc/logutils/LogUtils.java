package com.dftc.logutils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.dftc.logutils.utils.ObjParser;
import com.dftc.logutils.utils.XmlJsonParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 史上最强Log框架
 * Created by xuqiqiang on 2017/4/28.
 */
public class LogUtils {
    private static final String TAG = "LogUtils";
    public static int level = Log.VERBOSE;
    private static boolean DEBUG;
    private static Context mContext;
    private static String mDirPath;
    private static String mDefaultDate;
    private static LogInterface mService;
    private static ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "connect service");
            mService = LogInterface.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "disconnect service");
            mService = null;
        }
    };

    public static void register(boolean debug) {
        LogUtils.DEBUG = debug;
    }

    public static void register(Context context, String dirPath, boolean debug) {
        mContext = context;
        mDirPath = dirPath;
        LogUtils.DEBUG = debug;
        if (mContext != null && !TextUtils.isEmpty(mDirPath))
            bindService();
    }

    public static void initDefaultDate() {
        mDefaultDate = FormatDate.getFormatDate();
    }

    /**
     * 会影响到其他进程
     */
    public static void unregister() {
        mDirPath = null;
        if (mContext != null) {
            if (mConnection != null)
                mContext.unbindService(mConnection);
            mContext = null;
        }
    }

    public static void vLog(Object obj, String msg, Object... args) {
        if (!DEBUG && TextUtils.isEmpty(mDirPath))
            return;
        if (level <= Log.VERBOSE) {
            String tag = getClassInfoByObject(obj);
            if (args.length > 0) {
                msg = String.format(msg, args);
            }
            StackTraceElement ste = new Throwable().getStackTrace()[1];
            String codeInfo = "(" + ste.getFileName() + ":"
                    + ste.getLineNumber() + ")";
            if (DEBUG)
                Log.v(tag, msg + " " + codeInfo);
            if (!TextUtils.isEmpty(mDirPath)) {
                String systemOut = FormatDate.getFormatTime() + " V/" + tag
                        + codeInfo + " : " + msg + "\r\n";
                startWrite(systemOut);
            }
        }
    }

    public static void dLog(Object obj, String msg, Object... args) {
        if (!DEBUG && TextUtils.isEmpty(mDirPath))
            return;
        if (level <= Log.DEBUG) {
            String tag = getClassInfoByObject(obj);
            if (args.length > 0) {
                msg = String.format(msg, args);
            }
            StackTraceElement ste = new Throwable().getStackTrace()[1];
            String codeInfo = "(" + ste.getFileName() + ":"
                    + ste.getLineNumber() + ")";
            if (DEBUG)
                Log.v(tag, msg + " " + codeInfo);
            if (!TextUtils.isEmpty(mDirPath)) {
                String systemOut = FormatDate.getFormatTime() + " D/" + tag
                        + codeInfo + " : " + msg + "\r\n";
                startWrite(systemOut);
            }
        }
    }


    public static void iLog(Object obj, String msg, Object... args) {
        if (!DEBUG && TextUtils.isEmpty(mDirPath))
            return;
        if (level <= Log.INFO) {
            String tag = getClassInfoByObject(obj);
            if (args.length > 0) {
                msg = String.format(msg, args);
            }
            StackTraceElement ste = new Throwable().getStackTrace()[1];
            String codeInfo = "(" + ste.getFileName() + ":"
                    + ste.getLineNumber() + ")";
            if (DEBUG)
                Log.v(tag, msg + " " + codeInfo);
            if (!TextUtils.isEmpty(mDirPath)) {
                String systemOut = FormatDate.getFormatTime() + " I/" + tag
                        + codeInfo + " : " + msg + "\r\n";
                startWrite(systemOut);
            }
        }
    }

    public static void wLog(Object obj, String msg, Object... args) {
        if (!DEBUG && TextUtils.isEmpty(mDirPath))
            return;
        if (level <= Log.WARN) {
            String tag = getClassInfoByObject(obj);
            if (args.length > 0) {
                msg = String.format(msg, args);
            }
            StackTraceElement ste = new Throwable().getStackTrace()[1];
            String codeInfo = "(" + ste.getFileName() + ":"
                    + ste.getLineNumber() + ")";
            if (DEBUG)
                Log.w(tag, msg + " " + codeInfo);
            if (!TextUtils.isEmpty(mDirPath)) {
                String systemOut = FormatDate.getFormatTime() + " W/" + tag
                        + codeInfo + " : " + msg + "\r\n";
                startWrite(systemOut);
            }
        }
    }

    public static void eLog(Object obj, String msg, Object... args) {
        if (!DEBUG && TextUtils.isEmpty(mDirPath))
            return;
        if (level <= Log.ERROR) {
            String tag = getClassInfoByObject(obj);
            if (args.length > 0) {
                msg = String.format(msg, args);
            }
            StackTraceElement ste = new Throwable().getStackTrace()[1];
            String codeInfo = "(" + ste.getFileName() + ":"
                    + ste.getLineNumber() + ")";
            if (DEBUG)
                Log.v(tag, msg + " " + codeInfo);
            if (!TextUtils.isEmpty(mDirPath)) {
                String systemOut = FormatDate.getFormatTime() + " E/" + tag
                        + codeInfo + " : " + msg + "\r\n";
                startWrite(systemOut);
            }
        }
    }

    public static void json(Object obj, String json) {
        if (!DEBUG && TextUtils.isEmpty(mDirPath))
            return;
        if (level <= Log.DEBUG) {
            String tag = getClassInfoByObject(obj);
            String msg = XmlJsonParser.json(json);
            StackTraceElement ste = new Throwable().getStackTrace()[1];
            String codeInfo = "(" + ste.getFileName() + ":"
                    + ste.getLineNumber() + ")";
            if (DEBUG)
                Log.v(tag, msg + " " + codeInfo);
            if (!TextUtils.isEmpty(mDirPath)) {
                String systemOut = FormatDate.getFormatTime() + " D/" + tag
                        + codeInfo + " : " + msg + "\r\n";
                startWrite(systemOut);
            }
        }
    }

    public static void xml(Object obj, String xml) {
        if (!DEBUG && TextUtils.isEmpty(mDirPath))
            return;
        if (level <= Log.DEBUG) {
            String tag = getClassInfoByObject(obj);
            String msg = XmlJsonParser.xml(xml);
            StackTraceElement ste = new Throwable().getStackTrace()[1];
            String codeInfo = "(" + ste.getFileName() + ":"
                    + ste.getLineNumber() + ")";
            if (DEBUG)
                Log.v(tag, msg + codeInfo);
            if (!TextUtils.isEmpty(mDirPath)) {
                String systemOut = FormatDate.getFormatTime() + " D/" + tag
                        + codeInfo + " : " + msg + "\r\n";
                startWrite(systemOut);
            }
        }
    }

    public static void object(Object obj, Object object) {
        if (!DEBUG && TextUtils.isEmpty(mDirPath))
            return;
        if (level <= Log.DEBUG) {
            String tag = getClassInfoByObject(obj);
            String msg = ObjParser.parseObject(object);
            StackTraceElement ste = new Throwable().getStackTrace()[1];
            String codeInfo = "(" + ste.getFileName() + ":"
                    + ste.getLineNumber() + ")";
            if (DEBUG)
                Log.v(tag, msg + " " + codeInfo);
            if (!TextUtils.isEmpty(mDirPath)) {
                String systemOut = FormatDate.getFormatTime() + " D/" + tag
                        + codeInfo + " : " + msg + "\r\n";
                startWrite(systemOut);
            }
        }
    }

    private static String getClassInfoByObject(Object obj) {
        if (obj == null) {
            return "null";
        }
        String simpleName = obj.getClass().getSimpleName();
        if ("String".equals(simpleName)) {
            return obj.toString();
        }
        if (TextUtils.isEmpty(simpleName)) {
            return "TAG";
        }
        return simpleName;
    }

    private static void startWrite(String systemOut) {
        if (mService == null) {
            Log.e(TAG, "mService == null");
            if (mContext != null && !TextUtils.isEmpty(mDirPath)) {
                bindService();
            }
            return;
        }

        try {
            mService.log(mDirPath, systemOut);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void bindService() {
        Intent intent = new Intent("com.dftc.service.logrecord.LogUtilsService");
        try {
            intent = createExplicitFromImplicitIntent(mContext, intent);
            if (intent == null) {
                Log.e(TAG, "LogUtilsService do not exist!");
                return;
            }
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     *
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     *
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
     * @param context
     * @param implicitIntent - The original implicit intent
     * @return Explicit Intent created from the implicit original intent
     */
    @Nullable
    private static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        Log.d(TAG, "createExplicitFromImplicitIntent packageName:" + packageName);
        String className = serviceInfo.serviceInfo.name;
        Log.d(TAG, "createExplicitFromImplicitIntent className:" + className);
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

    @SuppressLint("SimpleDateFormat")
    public static class FormatDate {

        static String getFormatDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            return sdf.format(System.currentTimeMillis());
        }

        static String getYesterdayFormatDate() {
            Calendar ca = Calendar.getInstance();
            ca.setTime(new Date());
            ca.add(Calendar.DATE, -1);
            Date lastDay = ca.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            return sdf.format(lastDay);
        }

        static String getBeforeYesterdayFormatDate() {
            Calendar ca = Calendar.getInstance();
            ca.setTime(new Date());
            ca.add(Calendar.DATE, -2);
            Date lastDay = ca.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            return sdf.format(lastDay);
        }

        static String getFormatTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            return sdf.format(System.currentTimeMillis());
        }
    }
}
