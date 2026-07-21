package com.group2.volunteer.config;

import com.group2.volunteer.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        String uri = request.getRequestURI();
        String role = user.getRole();

        if (uri.startsWith("/admin") && !"ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/error/403");
            return false;
        }

        if (uri.startsWith("/projects/create") && !"ORGANIZER".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/error/403");
            return false;
        }

        if (uri.startsWith("/organizer") && !"ORGANIZER".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/error/403");
            return false;
        }

        if (uri.startsWith("/attendance/submit") && !"VOLUNTEER".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/error/403");
            return false;
        }

        if (uri.startsWith("/attendance/verify") && !"ORGANIZER".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/error/403");
            return false;
        }

        if ((uri.startsWith("/profile") || uri.startsWith("/my-activities"))
                && !"VOLUNTEER".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/error/403");
            return false;
        }

        return true;
    }
}
