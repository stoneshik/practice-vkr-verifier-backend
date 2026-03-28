package vkr.verifier.controller;

import java.time.Instant;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import vkr.verifier.dto.response.ErrorMessageResponseDto;
import vkr.verifier.exception.FileStorageException;
import vkr.verifier.exception.InvalidFileException;
import vkr.verifier.exception.ReportNotFoundException;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(InvalidFileException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessageResponseDto handleException(InvalidFileException e) {
        return ErrorMessageResponseDto.builder()
            .time(Instant.now())
            .message(e.getMessage())
            .build();
    }

    @ExceptionHandler(ReportNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessageResponseDto handleException(ReportNotFoundException e) {
        return ErrorMessageResponseDto.builder()
            .time(Instant.now())
            .message(e.getMessage())
            .build();
    }

    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageResponseDto handleException(NotFoundException e) {
        return ErrorMessageResponseDto.builder()
            .time(Instant.now())
            .message(e.getMessage())
            .build();
    }

    @ExceptionHandler(InternalServerError.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageResponseDto handleException(InternalServerError e) {
        return ErrorMessageResponseDto.builder()
            .time(Instant.now())
            .message("Ошибка сервера")
            .build();
    }
}
