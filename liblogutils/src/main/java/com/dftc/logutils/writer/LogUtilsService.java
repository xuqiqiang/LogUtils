package com.dftc.logutils.writer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.dftc.logutils.LogInterface;
import com.dftc.logutils.utils.FormatDate;
import com.dftc.logutils.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * If you want to enable writing, you need to add the following code in manifest:<p>
 * &lt;service<p>
 * android:name="com.dftc.logutils.writer.LogUtilsService"<p>
 * android:exported="true"<p>
 * android:process="com.dftc.service.logrecorder"&gt;<p>
 * &lt;intent-filter&gt;<p>
 * &lt;action android:name="com.dftc.service.logrecorder.LogUtilsService" /&gt;<p>
 * &lt;/intent-filter&gt;<p>
 * &lt;/service&gt;
 * <p>
 * Created by xuqiqiang on 2017/6/2.
 */
public class LogUtilsService extends Service {
    private static final String TAG = "LogUtilsService";

    private static final Map<String, String> mLogMap = Collections
            .synchronizedMap(new HashMap<String, String>());

    private static final long WRITE_DELAY = 5000;
    private static final long MAX_FILE_SIZE = 1024 * 1024;
    private static final int MAX_FILE_SUM = 20;
    private static final int MAX_FILE_SUM_CACHE = 10;
    private static final float MAX_CPU_RATE = 60.0f;

    private static final ExecutorService mExecutorService = Executors
            .newSingleThreadExecutor();
    private final LogInterface.Stub mBinder = new LogInterface.Stub() {
        @Override
        public void log(String dirPath, String message) throws RemoteException {
            if (TextUtils.isEmpty(dirPath)) {
                Log.e(TAG, "log dirPath == null");
                return;
            }

            synchronized (mLogMap) {
                String mMessage = mLogMap.get(dirPath);
                if (TextUtils.isEmpty(mMessage)) {
                    mMessage = message;
                } else
                    mMessage += message;
                mLogMap.put(dirPath, mMessage);
            }
        }
    };

    @Nullable
    private static File getWriteFile(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "Error occurred during creating dir : " + dirPath);
                return null;
            }
        }

        String date = FormatDate.getFormatDate();
        int fileIndex = 1;
        File file;
        do {
            file = new File(dir,
                    date + "-" + (fileIndex++) + ".log");
        } while (file.exists() && file.length() > MAX_FILE_SIZE);

        if (!file.exists()) {
            File f = handleOutdatedFiles(dir);
            if (f != null)
                file = f;
            try {
                if (!file.createNewFile()) {
                    Log.e(TAG, "Error occurred during creating file : " + file.getAbsolutePath());
                    return null;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error occurred during creating file : " + file.getAbsolutePath());
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    private static boolean write(String dirPath, String info) {
        if (TextUtils.isEmpty(dirPath))
            return false;
        File file = getWriteFile(dirPath);
        if (file == null)
            return false;
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        byte[] b = info.getBytes();
        try {
            outputStream.write(b, 0, b.length);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Nullable
    private static List<File> getFileList(String dirPath, String date) {
        List<File> list = new ArrayList<>();
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        int fileIndex = 1;
        File file;
        do {
            file = new File(dir,
                    date + "-" + (fileIndex++) + ".log");
        } while (file.exists() && list.add(file));
        return list;
    }

    public static List<File> getYesterdayFileList(String dirPath) {
        return getFileList(dirPath, FormatDate.getYesterdayFormatDate());
    }

    /**
     * 从指定日期的日志中删除sum个文件，并重置编号.
     *
     * @param date 指定日期.
     * @param sum  需要删除的文件个数，0表示删除所有文件.
     * @return {@link int} 删除的文件个数.
     */
    private static int deleteFileList(String dirPath, String date, int sum) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return 0;
        }
        if (TextUtils.isEmpty(date))
            return 0;
        int fileIndex = 1;
        File file = null;
        do {
            if (file != null) {
                if (sum > 0) {
                    if (fileIndex - 1 <= sum)
                        file.delete();
                    else {
                        file.renameTo(new File(dir,
                                date + "-" + (fileIndex - 1 - sum) + ".log"));
                    }
                } else {
                    file.delete();
                }
            }
            file = new File(dir,
                    date + "-" + (fileIndex++) + ".log");
        } while (file.exists());
        int fileSum = fileIndex - 2;
        return sum <= 0 ? fileSum : fileSum < sum ? fileSum : sum;
    }

    private static int fixFileList(File dir, String date, int deleteIndex) {
        if (TextUtils.isEmpty(date) || deleteIndex <= 0)
            return 0;
        int fileIndex = deleteIndex + 1;
        File file = null;
        do {
            if (file != null) {
                file.renameTo(new File(dir,
                        date + "-" + (fileIndex - 1 - deleteIndex) + ".log"));
            }
            file = new File(dir,
                    date + "-" + (fileIndex++) + ".log");
        } while (file.exists());
        return fileIndex - 1 - deleteIndex;
    }

    /**
     * 清理过时的日志，如果文件数还是大于80，把日志删除到只剩50个，如果删除了今天的日志，会调整
     * 今天的日志的编号，并返回一个正确编号的文件.
     *
     * @return {@link File} 返回一个正确编号的文件.
     */
    @Nullable
    private static File handleOutdatedFiles(File dir) {
        File[] files = dir.listFiles();
        if (files != null && files.length > MAX_FILE_SUM + MAX_FILE_SUM_CACHE) {
            Log.d(TAG, "handle outdated files");
            List<File> fileList = Arrays.asList(files);
            Collections.sort(fileList, new Comparator<File>() {

                @Override
                public int compare(File o1, File o2) {
                    long t1 = o1.lastModified();
                    long t2 = o2.lastModified();
                    if (t1 < t2)
                        return -1;
                    else if (t1 == t2)
                        return 0;
                    else
                        return 1;
                }
            });
            int deleteSum = files.length - MAX_FILE_SUM;
            String today = FormatDate.getFormatDate();
            int todayIndex = 0;
            int i = 0;
            do {
                File file = fileList.get(i);
                if (file.getName().startsWith(today)) {
                    todayIndex++;
                }
                file.delete();
            } while (++i < deleteSum);
            if (todayIndex > 0)
                return new File(dir,
                        today + "-" + fixFileList(dir, today, todayIndex) + ".log");
        }
        return null;
    }

    private void log(String dirPath, String message) {
        try {
            mBinder.log(dirPath, message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onCreate() {
        Log.d(TAG, "service created:" + android.os.Process.myPid());
        wakeupSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service started id = " + startId);
        wakeupSelf();
        startWriteThread();
        return super.onStartCommand(intent, flags, startId);
    }

    private void wakeupSelf() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + WRITE_DELAY;
        Intent i = new Intent(this, LogUtilsService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }

    private void startWriteThread() {
        Iterator iterator = mLogMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry entry = (Map.Entry) iterator.next();
            mExecutorService.execute(new Thread() {
                @Override
                public void run() {

                    String dirPath = (String) entry.getKey();
                    Log.d(TAG, "startWriteThread dirPath : " + dirPath);
                    if (TextUtils.isEmpty(dirPath))
                        return;

                    float cpuRate = Utils.getCpuRate();
                    log(dirPath, "Current cpu rate : "
                            + String.format("%.2f", cpuRate)
                            + "%\r\n");
                    if (cpuRate > MAX_CPU_RATE) {
                        log(dirPath, "Current cpu rate is too high!\n");
                        return;
                    }

                    String message;
                    synchronized (mLogMap) {
                        message = (String) entry.getValue();
                        if (TextUtils.isEmpty(message))
                            return;
                        mLogMap.put(dirPath, "");
                    }
                    if (!write(dirPath, message))
                        Log.e("LogUtils", "Error occurred during writing log : " + message);
                }
            });
        }
    }

    public IBinder onBind(Intent intent) {
        Log.d(TAG, "service on bind");
        return mBinder;
    }

    public void onDestroy() {
        Log.d(TAG, "service on destroy");
        super.onDestroy();
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "service on unbind");
        return super.onUnbind(intent);
    }

    public void onRebind(Intent intent) {
        Log.d(TAG, "service on rebind");
        super.onRebind(intent);
    }
}
