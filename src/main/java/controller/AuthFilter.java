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

        String context = request.getContextPath();
        String uri = request.getRequestURI();

        HttpSession session = request.getSession(false);

        boolean loggedIn = (session != null && session.getAttribute("username") != null);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (uri.equals(context + "/")) {
            if (loggedIn) {
                response.sendRedirect(context + "/dashboard");
            } else {
                response.sendRedirect(context + "/index.html");
            }
            return;
        }

        boolean isPublic = uri.endsWith("login.html") ||
                uri.endsWith("signup.html") || uri.startsWith(context + "/auth") ||
                uri.startsWith(context + "/js") || uri.startsWith(context + "/css") ||
                uri.startsWith(context + "/images") || uri.endsWith(".ico");
        if (loggedIn && (uri.endsWith("login.html") || uri.endsWith("signup.html")
        )) {
            response.sendRedirect(context + "/dashboard");
            return;
        }

        if (isPublic) {
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

            response.sendRedirect(context + "/auth/login.html");
            return;
        }

        boolean isValidRoute = uri.startsWith(context + "/dashboard") ||
                uri.startsWith(context + "/api") || uri.startsWith(context + "/logout");

        if (!isPublic && !isValidRoute) {
            response.sendRedirect(context + "/dashboard");
            return;
        }
        chain.doFilter(req, res);
    }
}