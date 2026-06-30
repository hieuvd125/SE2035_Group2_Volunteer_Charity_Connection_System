package com.group2.volunteer.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequest(BadRequestException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public String handleValidationExceptions(Exception ex, Model model) {
        model.addAttribute("error", "Dữ liệu nhập vào không hợp lệ. Vui lòng kiểm tra lại các trường bắt buộc!");
        return "error/400";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        model.addAttribute("error", "Đã xảy ra lỗi hệ thống: " + ex.getMessage());
        return "error/500";
    }
}