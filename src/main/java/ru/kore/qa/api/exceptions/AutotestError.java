package ru.kore.qa.api.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutotestError extends AssertionError {

    public AutotestError(String message, Throwable ex) {
        super(message);
        log.error(message, ex);
    }

    public AutotestError(String message) {
        super(message);
        log.error(message);
    }
}
