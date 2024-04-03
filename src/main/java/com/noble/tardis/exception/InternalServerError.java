package com.noble.tardis.exception;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerError extends RuntimeException {
    protected Instant timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC);

    public InternalServerError(String message) {
        super(message);
    }
}
