package com.noble.tardis.exception.handler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.noble.tardis.exception.BadRequest;
import com.noble.tardis.exception.Conflict;
import com.noble.tardis.exception.InternalServerError;
import com.noble.tardis.exception.NotFound;
import com.noble.tardis.exception.Unauthorized;
import com.noble.tardis.exception.dto.ExceptionResponseDto;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ExceptionResponseDto> generic(HttpServletRequest request, Exception ex) {
                System.out.println(Instant.now());
                System.err.println(ex.getMessage());
                return new ResponseEntity<>(new ExceptionResponseDto(LocalDateTime.now().toInstant(ZoneOffset.UTC),
                                "Unexpected exception", request.getRequestURL().toString(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value()),
                                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ExceptionHandler(InternalServerError.class)
        public ResponseEntity<ExceptionResponseDto> internalServerError(HttpServletRequest request,
                        InternalServerError ex) {
                return new ResponseEntity<>(new ExceptionResponseDto(LocalDateTime.now().toInstant(ZoneOffset.UTC),
                                ex.getMessage(), request.getRequestURL().toString(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value()),
                                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(BadRequest.class)
        public ResponseEntity<ExceptionResponseDto> badRequest(HttpServletRequest request,
                        BadRequest ex) {
                return new ResponseEntity<>(new ExceptionResponseDto(LocalDateTime.now().toInstant(ZoneOffset.UTC),
                                ex.getMessage(), request.getRequestURL().toString(), HttpStatus.BAD_REQUEST.value()),
                                HttpStatus.BAD_REQUEST);
        }

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(NotFound.class)
        public ResponseEntity<ExceptionResponseDto> notFound(HttpServletRequest request,
                        NotFound ex) {
                return new ResponseEntity<>(new ExceptionResponseDto(LocalDateTime.now().toInstant(ZoneOffset.UTC),
                                ex.getMessage(), request.getRequestURL().toString(), HttpStatus.NOT_FOUND.value()),
                                HttpStatus.NOT_FOUND);
        }

        @ResponseStatus(HttpStatus.CONFLICT)
        @ExceptionHandler(Conflict.class)
        public ResponseEntity<ExceptionResponseDto> conflict(HttpServletRequest request,
                        Conflict ex) {
                return new ResponseEntity<>(new ExceptionResponseDto(LocalDateTime.now().toInstant(ZoneOffset.UTC),
                                ex.getMessage(), request.getRequestURL().toString(), HttpStatus.CONFLICT.value()),
                                HttpStatus.CONFLICT);
        }

        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        @ExceptionHandler(Unauthorized.class)
        public ResponseEntity<ExceptionResponseDto> unauthorized(HttpServletRequest request,
                        Unauthorized ex) {
                return new ResponseEntity<>(new ExceptionResponseDto(LocalDateTime.now().toInstant(ZoneOffset.UTC),
                                ex.getMessage(), request.getRequestURL().toString(), HttpStatus.UNAUTHORIZED.value()),
                                HttpStatus.UNAUTHORIZED);
        }
}
