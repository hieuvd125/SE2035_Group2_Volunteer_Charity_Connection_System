package com.group2.volunteer.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
@ExceptionHandler(UserNotLoggedInException.class)
    public String handleUserNotLoggedIn(UserNotLoggedInException ex, RedirectAttributes redirectAttributes){
    redirectAttributes.addFlashAttribute("errorMessage",ex.getMessage());
    return "redirect:/login";
}
@ExceptionHandler(SaveProjectException.class)
    public String handleSaveProjectException(SaveProjectException ex, RedirectAttributes redirectAttributes){
    redirectAttributes.addFlashAttribute("errorMessage",ex.getMessage());
    return "redirect:/projects/homepage";
}
}