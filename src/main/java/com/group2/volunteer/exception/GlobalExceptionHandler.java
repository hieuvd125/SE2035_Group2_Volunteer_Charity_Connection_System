package com.group2.volunteer.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.ModelAndView;

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

  @ExceptionHandler(AuthException.class)
  public ModelAndView handleAuthException(AuthException ex) {
      ModelAndView mv = new ModelAndView();

      mv.addObject("errorMessage", ex.getMessage());
      mv.setViewName("common/login");

      return mv;
  }

  @ExceptionHandler(InvalidProfileException.class)
  public String handleInvalidProfile(InvalidProfileException ex,
                                     RedirectAttributes redirectAttributes) {
      redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
      return "redirect:/volunteer/profile/edit";
  }

  @ExceptionHandler(InvalidAttendanceProofException.class)
  public String handleInvalidAttendanceProof(InvalidAttendanceProofException ex,
                                             RedirectAttributes redirectAttributes) {
      redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
      return "redirect:/volunteer/attendance/submit";
  }
}
