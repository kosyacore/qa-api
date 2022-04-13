package ru.kore.qa.api.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemoryError extends AssertionError {

    public MemoryError(String message) {
        super(message);
    }

    public MemoryError(String message, Throwable ex) {
        super(message);
        log.error(ex.toString());
    }
}
