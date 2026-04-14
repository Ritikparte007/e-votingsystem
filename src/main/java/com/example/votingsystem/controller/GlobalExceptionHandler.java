package com.example.votingsystem.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice                                      // ✅ Separate class — AdminController ke bahar
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(
            MaxUploadSizeExceededException exc,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error",
            "File size too large! Maximum allowed size is 10MB.");
        return "redirect:/admin";
    }
}