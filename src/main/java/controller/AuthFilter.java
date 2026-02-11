package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        String uri = request.getRequestURI();
        String context = request.getContextPath();

        boolean loggedIn;
        if (session != null && session.getAttribute("username") != null) {
            loggedIn = true;
        } else {
            loggedIn = false;
        }

        if (uri.endsWith("index.html") || uri.endsWith("signup.html") ||
                uri.startsWith(context + "/auth") || uri.startsWith(context + "/js") ||
                uri.startsWith(context + "/css") || uri.startsWith(context + "/images")
        ) {
            if (loggedIn && uri.endsWith("index.html")) {
                response.sendRedirect(context + "/dashboard");
                return;
            }
            chain.doFilter(req, res);
            return;
        }

        if (!loggedIn) {
            if (uri.startsWith(context + "/api")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().print(
                        "{\"success\":false,\"message\":\"Session expired\"}"
                );
                return;
            }

            response.sendRedirect(context + "/index.html");
            return;
        }
        chain.doFilter(req, res);
    }
}