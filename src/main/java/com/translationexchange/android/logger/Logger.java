package com.translationexchange.android.logger;

import android.util.Log;

import com.translationexchange.core.logger.LoggerInterface;

/**
 * Created by ababenko on 8/17/2016.
 */
public class Logger implements LoggerInterface {
    @Override
    public void logException(String message, Exception ex) {
        error("Logger", message, ex);
    }

    @Override
    public void logException(Exception ex) {
        error("Logger", ex.getMessage(), ex);
    }

    @Override
    public void debug(Object message) {
        debug("Logger", message.toString());
    }

    @Override
    public void info(Object message) {
        info("Logger", message.toString());
    }

    @Override
    public void warn(Object message) {
        warn("Logger", message.toString());
    }

    @Override
    public void error(Object message) {
        error("Logger", message.toString());
    }

    @Override
    public void error(String tag, String message) {
        Log.e(tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable e) {
        Log.e(tag, message, e);
    }

    @Override
    public void debug(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void info(String tag, String message) {
        Log.i(tag, message);
    }

    @Override
    public void warn(String tag, String message) {
        Log.w(tag, message);
    }
}
