package com.stateless.service.exceptions;

import com.stateless.service.dto.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ControllerAdvice
public class Exceptions {
    private static final Logger logger =
            LoggerFactory.getLogger(Exceptions.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    com.stateless.service.dto.ErrorMessage badRequestException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        List<HashMap> lm = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            HashMap<String, String> d = new HashMap<>();
            d.put("fieldName", fieldError.getField());
            d.put("message", fieldError.getDefaultMessage());
            lm.add(d);
        }

        return new ErrorMessage(lm);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public HashMap<String, String> handleException(Exception e) {
        e.printStackTrace();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("message", e.getMessage());
        return map;
    }
}
