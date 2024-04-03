package com.noble.tardis.exception;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequest extends InternalServerError {
    protected Instant timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC);

    public BadRequest(String message) {
        super(message);
    }
}
