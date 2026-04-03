package dev.spoocy.utils.common.exceptions;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WrappedExceptionTest {

    @Test
    void wrapKeepsRuntimeExceptionsUntouched() {
        RuntimeException runtimeException = new IllegalStateException("boom");

        RuntimeException wrapped = WrappedException.wrap(runtimeException);

        assertSame(runtimeException, wrapped);
    }

    @Test
    void wrapConvertsIOExceptionToUncheckedIOException() {
        IOException ioException = new IOException("io");

        RuntimeException wrapped = WrappedException.wrap(ioException);

        assertSame(UncheckedIOException.class, wrapped.getClass());
        assertSame(ioException, wrapped.getCause());
    }

    @Test
    void wrapRethrowsErrors() {
        AssertionError error = new AssertionError("fatal");

        AssertionError thrown = assertThrows(AssertionError.class, () -> WrappedException.wrap(error));

        assertSame(error, thrown);
    }

    @Test
    void rethrowConvertsIOExceptionToUncheckedIOException() {
        IOException ioException = new IOException("io");

        UncheckedIOException thrown = assertThrows(UncheckedIOException.class, () -> WrappedException.rethrow(ioException));

        assertSame(ioException, thrown.getCause());
    }

    @Test
    void rethrowWrapsCheckedExceptions() {
        Exception checked = new Exception("checked");

        WrappedException thrown = assertThrows(WrappedException.class, () -> WrappedException.rethrow(checked));

        assertSame(checked, thrown.getCause());
    }
}

