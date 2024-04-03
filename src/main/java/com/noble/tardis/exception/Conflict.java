package com.noble.tardis.exception;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class Conflict extends InternalServerError {
    protected Instant timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC);

    public Conflict(String message) {
        super(message);
    }
}
