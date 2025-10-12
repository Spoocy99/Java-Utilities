package dev.spoocy.utils.common.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class WrappedException extends RuntimeException {

    public WrappedException(Throwable cause) {
        super(cause);
    }

    public WrappedException(String message, Throwable cause) {
        super(message, cause);
    }

    public static WrappedException wrap(@NotNull Throwable throwable) {
        return new WrappedException(throwable);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public static void rethrow(@NotNull Throwable throwable) {
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }

        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }

        throw new WrappedException(throwable);
    }
}
