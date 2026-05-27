package com.hostelmate.filter;

import com.hostelmate.util.SessionUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AuthFilter — Authentication & Authorization Filter
 * 
 * Intercepts requests to protected URLs and checks:
 * 1. User is logged in (has valid session)
 * 2. User has the correct role for the requested path
 * 
 * Protected paths (configured in web.xml):
 *   /student/* — requires STUDENT or ADMIN role
 *   /admin/*   — requires ADMIN role only
 * 
 * @author HostelMate Team
 */
@WebFilter(filterName = "AuthFilter")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // ============================================================
        // 1. Check if user is logged in
        // ============================================================
        if (!SessionUtil.isLoggedIn(httpRequest)) {
            // Not logged in — redirect to login page with a message
            SessionUtil.setFlashMessage(httpRequest, "warning", 
                "Please log in to access this page.");
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // ============================================================
        // 2. Check role-based access for admin paths
        // ============================================================
        String relativePath = requestURI.substring(contextPath.length());

        if (relativePath.startsWith("/admin")) {
            // Admin pages require ADMIN role
            if (!SessionUtil.isAdmin(httpRequest)) {
                SessionUtil.setFlashMessage(httpRequest, "error", 
                    "Access denied. Admin privileges required.");
                httpResponse.sendRedirect(contextPath + "/student/dashboard");
                return;
            }
        }

        // ============================================================
        // 3. Check if user account is still active
        // ============================================================
        if (SessionUtil.getLoggedInUser(httpRequest) != null 
                && !SessionUtil.getLoggedInUser(httpRequest).isActive()) {
            SessionUtil.invalidateSession(httpRequest);
            SessionUtil.setFlashMessage(httpRequest, "error", 
                "Your account has been blocked. Contact the administrator.");
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // User is authenticated and authorized — proceed
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}
