package bo.edu.usfx.descuentos;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CabecerasSeguridadFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
            FilterChain chain) throws ServletException, IOException {
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("X-Frame-Options", "DENY");
        res.setHeader("Content-Security-Policy", "default-src 'none'; frame-ancestors 'none'");
        res.setHeader("Referrer-Policy", "no-referrer");
        res.setHeader("Cache-Control", "no-store");
        res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        chain.doFilter(req, res);
    }
}
