package com.hostelmate.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * EncodingFilter — UTF-8 Character Encoding Filter
 * 
 * Ensures all requests and responses use UTF-8 encoding.
 * This prevents character encoding issues with special characters
 * (e.g., the Rupee symbol ₹, accented names).
 * 
 * Applied to all URLs via web.xml mapping.
 * 
 * @author HostelMate Team
 */
@WebFilter(filterName = "EncodingFilter")
public class EncodingFilter implements Filter {

    private static final String ENCODING = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Set UTF-8 encoding on both request and response
        request.setCharacterEncoding(ENCODING);
        response.setCharacterEncoding(ENCODING);
        // Do not force setContentType to text/html here as it breaks static assets like CSS/JS

        // Continue the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}
