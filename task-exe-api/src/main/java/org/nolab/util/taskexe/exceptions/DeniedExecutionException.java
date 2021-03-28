package org.nolab.util.taskexe.exceptions;

import org.nolab.util.taskexe.TaskExecutor;

/**
 * Exception thrown by {@link TaskExecutor}, when task cannot be accepted
 * for execution.
 */
public class DeniedExecutionException extends RuntimeException {

    private static final long serialVersionUID = 1272080514438629042L;

    public DeniedExecutionException() {
    }

    public DeniedExecutionException(String message) {
        super(message);
    }

    public DeniedExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeniedExecutionException(Throwable cause) {
        super(cause);
    }

    protected DeniedExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
