package com.d2vfactory.throwmoney.controller;

import com.d2vfactory.throwmoney.domain.ExceptionDTO;
import com.d2vfactory.throwmoney.exceptions.RuntimeThrowMoneyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@ControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeThrowMoneyException.class)
    public ExceptionDTO handleTodoException(RuntimeThrowMoneyException e) {
        return createExceptionDTO(e);
    }

    @ExceptionHandler(value = Exception.class)
    public ExceptionDTO handleException(Exception e) {
        return createExceptionDTO(e);
    }

    private ExceptionDTO createExceptionDTO(Exception e) {
        return ExceptionDTO.builder()
                .message(e.getMessage())
                .cause(e.toString())
                .build();
    }

}
