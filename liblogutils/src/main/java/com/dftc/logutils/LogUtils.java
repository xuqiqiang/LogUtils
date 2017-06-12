package com.dftc.logutils;

import android.util.Log;

import com.dftc.logutils.adapter.LogAdapter;
import com.dftc.logutils.adapter.StressLogAdapter;
import com.dftc.logutils.config.LogConfig;
import com.dftc.logutils.printer.LogPrinter;
import com.dftc.logutils.printer.Printer;
import com.dftc.logutils.parser.ObjParser;
import com.dftc.logutils.utils.Utils;
import com.dftc.logutils.parser.XmlJsonParser;

/**
 * The best Log framework on the earth
 * <p>
 * Created by xuqiqiang on 2017/4/28.
 */
public class LogUtils {
    public static final String TAG = "LogUtils";

    private static final Printer printer = new LogPrinter();

    public static void initialize(LogConfig config) {
        printer.setConfig(config);
    }

    public static void destroy() {
        printer.finalize();
    }

    public static void v(String msg, Object... args) {
        printer.log(null, Log.VERBOSE, null, null, null, msg, args);
    }

    public static void vLog(Object obj, String msg, Object... args) {
        printer.log(null, Log.VERBOSE, null, Utils.getClassInfoByObject(obj), null, msg, args);
    }

    public static void d(String msg, Object... args) {
        printer.log(null, Log.DEBUG, null, null, null, msg, args);
    }

    public static void dLog(Object obj, String msg, Object... args) {
        printer.log(null, Log.DEBUG, null, Utils.getClassInfoByObject(obj), null, msg, args);
    }

    public static void i(String msg, Object... args) {
        printer.log(null, Log.INFO, null, null, null, msg, args);
    }

    public static void iLog(Object obj, String msg, Object... args) {
        printer.log(null, Log.INFO, null, Utils.getClassInfoByObject(obj), null, msg, args);
    }

    public static void w(String msg, Object... args) {
        printer.log(null, Log.WARN, null, null, null, msg, args);
    }

    public static void wLog(Object obj, String msg, Object... args) {
        printer.log(null, Log.WARN, null, Utils.getClassInfoByObject(obj), null, msg, args);
    }

    public static void e(String msg, Object... args) {
        printer.log(null, Log.ERROR, null, null, null, msg, args);
    }

    public static void eLog(Object obj, String msg, Object... args) {
        printer.log(null, Log.ERROR, null, Utils.getClassInfoByObject(obj), null, msg, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        printer.log(null, Log.ERROR, null, null, throwable, message, args);
    }

    public static void eLog(Object obj, Throwable throwable, String msg, Object... args) {
        printer.log(null, Log.ERROR, null, Utils.getClassInfoByObject(obj), throwable, msg, args);
    }

    /**
     * Formats the given json content and print it
     */
    public static void json(String json) {
        printer.log(null, Log.DEBUG, null, null,
                null, XmlJsonParser.json(json));
    }

    public static void json(Object obj, String json) {
        printer.log(null, Log.DEBUG, null, Utils.getClassInfoByObject(obj),
                null, XmlJsonParser.json(json));
    }

    /**
     * Formats the given xml content and print it
     */
    public static void xml(String xml) {
        printer.log(null, Log.DEBUG, null, null,
                null, XmlJsonParser.xml(xml));
    }

    public static void xml(Object obj, String xml) {
        printer.log(null, Log.DEBUG, null, Utils.getClassInfoByObject(obj),
                null, XmlJsonParser.xml(xml));
    }

    public static void object(Object object) {
        printer.log(null, Log.DEBUG, null, null,
                null, ObjParser.parseObject(object));
    }

    public static void object(Object obj, Object object) {
        printer.log(null, Log.DEBUG, null, Utils.getClassInfoByObject(obj),
                null, ObjParser.parseObject(object));
    }

    public static void cpuRate() {
        final StackTraceElement ste = new Throwable().getStackTrace()[1];
        new Thread() {
            public void run() {
                printer.log(null, Log.INFO, ste,
                        null, null, "Current process("
                                + android.os.Process.myPid()
                                + ") use cpu : "
                                + String.format("%.2f", Utils.getProcessCpuRate())
                                + "%");
            }
        }.start();
    }

    public static class S {

        private static LogAdapter mLogAdapter = new StressLogAdapter();

        public static void v(String msg, Object... args) {
            printer.log(mLogAdapter, Log.VERBOSE, null, null, null, msg, args);
        }

        public static void d(String msg, Object... args) {
            printer.log(mLogAdapter, Log.DEBUG, null, null, null, msg, args);
        }

        public static void i(String msg, Object... args) {
            printer.log(mLogAdapter, Log.INFO, null, null, null, msg, args);
        }

        public static void w(String msg, Object... args) {
            printer.log(mLogAdapter, Log.WARN, null, null, null, msg, args);
        }

        public static void e(String msg, Object... args) {
            printer.log(mLogAdapter, Log.ERROR, null, null, null, msg, args);
        }

        public static void e(Throwable throwable, String message, Object... args) {
            printer.log(mLogAdapter, Log.ERROR, null, null, throwable, message, args);
        }

        /**
         * Formats the given json content and print it
         */
        public static void json(String json) {
            printer.log(mLogAdapter, Log.DEBUG, null, null,
                    null, XmlJsonParser.json(json));
        }

        /**
         * Formats the given xml content and print it
         */
        public static void xml(String xml) {
            printer.log(mLogAdapter, Log.DEBUG, null, null,
                    null, XmlJsonParser.xml(xml));
        }

        public static void object(Object object) {
            printer.log(mLogAdapter, Log.DEBUG, null, null,
                    null, ObjParser.parseObject(object));
        }

        public static void cpuRate() {
            final StackTraceElement ste = new Throwable().getStackTrace()[1];
            new Thread() {
                public void run() {
                    printer.log(null, Log.INFO, ste,
                            null, null, "Current process("
                                    + android.os.Process.myPid()
                                    + ") use cpu : "
                                    + String.format("%.2f", Utils.getProcessCpuRate())
                                    + "%");
                }
            }.start();
        }

    }

}
