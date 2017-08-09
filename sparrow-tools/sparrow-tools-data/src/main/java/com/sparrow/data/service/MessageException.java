package com.sparrow.data.service;

/**
 * Created by yuanzc on 2016/3/9.
 */
public class MessageException extends RuntimeException {
    private String subject;
    private String message;

    public MessageException() {

    }

    public MessageException(String subject, String message) {
        super(message);
        this.subject = subject;
        this.message = message;
    }
}
