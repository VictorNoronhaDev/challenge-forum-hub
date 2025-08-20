package br.com.forumhub.infra.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        String body = "{" +
                "\"timestamp\":\"" + OffsetDateTime.now(ZoneOffset.UTC) + "\"," +
                "\"status\":403," +
                "\"error\":\"Forbidden\"," +
                "\"message\":\"Acesso negado\"," +
                "\"path\":\"" + request.getRequestURI() + "\"}";
        response.getWriter().write(body);
    }
}