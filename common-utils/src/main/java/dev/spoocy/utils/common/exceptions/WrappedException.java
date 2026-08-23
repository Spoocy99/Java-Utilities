package dev.spoocy.utils.common.exceptions;

import dev.spoocy.utils.common.misc.Args;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * A wrapper exception that can be used to wrap checked exceptions in a runtime exception.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class WrappedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WrappedException(@NotNull Throwable cause) {
        super(cause);
    }

    public WrappedException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }

    public static RuntimeException wrap(@NotNull Throwable throwable) {
        Throwable checked = Args.notNull(throwable, "throwable");

        if (checked instanceof Error) {
            throw (Error) checked;
        }

        if (checked instanceof RuntimeException) {
            return (RuntimeException) checked;
        }

        if (checked instanceof IOException) {
            return wrapIO((IOException) checked);
        }

        return new WrappedException(checked);
    }

    public static UncheckedIOException wrapIO(@NotNull IOException exception) {
        return new UncheckedIOException(Args.notNull(exception, "exception"));
    }

    @Override
    public String getMessage() {
        Throwable cause = this.getCause();
        return cause != null ? cause.getMessage() : super.getMessage();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public static void rethrow(@NotNull Throwable throwable) {
        Throwable checked = Objects.requireNonNull(throwable, "throwable");
        throw wrap(checked);
    }
}
