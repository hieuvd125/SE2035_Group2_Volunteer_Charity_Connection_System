package com.group2.volunteer.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ModelAndView handleAuthException(AuthException ex) {
        ModelAndView mv = new ModelAndView();

        mv.addObject("errorMessage", ex.getMessage());
        mv.setViewName("common/login");

        return mv;
    }
}