package com.wangjin.doc.exceptions;

/**
 * @program: gen-interfacedoc
 * @description
 * @author: 王进
 **/
public class NotUseException extends RuntimeException {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public NotUseException() {
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public NotUseException(String message) {
        super(message);
    }
}
