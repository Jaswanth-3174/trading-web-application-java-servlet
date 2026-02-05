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

        boolean loggedIn = session != null && session.getAttribute("username") != null;

        String context = request.getContextPath();
        if (uri.endsWith("index.html") || uri.endsWith("signup.html") ||
                uri.startsWith(context + "/auth") || uri.startsWith(context + "/js") ||
                uri.startsWith(context + "/css") || uri.startsWith(context + "/images")) {
            if (loggedIn && uri.endsWith("index.html")) {
                response.sendRedirect(context + "/dashboard");
                return;
            }
            chain.doFilter(req, res);
            return;
        }

        if (!loggedIn) {
            response.sendRedirect(context + "/index.html");
            return;
        }
        chain.doFilter(req, res);
    }
}
