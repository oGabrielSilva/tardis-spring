package com.noble.tardis.exception.dto;

import java.time.Instant;

public record ExceptionResponseDto(Instant timestamp, String message, String url, int status) {

}
