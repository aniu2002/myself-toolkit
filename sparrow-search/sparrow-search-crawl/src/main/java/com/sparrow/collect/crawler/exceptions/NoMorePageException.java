package com.sparrow.collect.crawler.exceptions;

/**
 * Created by Administrator on 2017/7/16 0016.
 */
public class NoMorePageException extends RuntimeException {
    public NoMorePageException() {
    }

    public NoMorePageException(String message) {
        super(message);
    }

    public NoMorePageException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMorePageException(Throwable cause) {
        super(cause);
    }

    public NoMorePageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
