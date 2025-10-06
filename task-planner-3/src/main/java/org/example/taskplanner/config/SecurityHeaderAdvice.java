package org.example.taskplanner.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class SecurityHeaderAdvice {

    @ModelAttribute
    public void setSecurityHeaders(HttpServletResponse response) {
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'");
    }
}