package com.example.votingsystem.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminIPInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) return true;

        String storedIP = (String) session.getAttribute("adminIP");
        if (storedIP == null) return true;

        String currentIP = request.getHeader("X-Forwarded-For") != null 
            ? request.getHeader("X-Forwarded-For").split(",")[0] 
            : request.getRemoteAddr();

        if (!storedIP.equals(currentIP)) {
            session.invalidate();
            response.sendRedirect("/login?error=ip_changed");
            return false;
        }
        return true;
    }
}