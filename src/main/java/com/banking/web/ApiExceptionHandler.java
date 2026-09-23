package com.banking.web;

import com.banking.exception.AccountNotFoundException;
import com.banking.exception.InsufficientFundsException;
import com.banking.exception.InvalidAmountException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> accountNotFound(AccountNotFoundException exception) {
        return error(exception.getMessage());
    }

    @ExceptionHandler({InvalidAmountException.class, InsufficientFundsException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidOperation(RuntimeException exception) {
        return error(exception.getMessage());
    }

    private Map<String, String> error(String message) {
        return Collections.singletonMap("error", message);
    }
}