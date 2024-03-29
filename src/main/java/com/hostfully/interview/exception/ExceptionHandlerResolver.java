package com.hostfully.interview.exception;

import com.hostfully.interview.model.dto.ErrorInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerResolver {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody ErrorInfoDto
    handleBadRequest(HttpServletRequest req, Exception ex) {
        return new ErrorInfoDto(ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody ErrorInfoDto
    handleHttpMessageNotReadableException(HttpServletRequest req, Exception ex) {
        return new ErrorInfoDto("Malformed JSON request");
    }
}
