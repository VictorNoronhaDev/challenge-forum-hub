package br.com.forumhub.infra.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String body = "{" +
                "\"timestamp\":\"" + OffsetDateTime.now(ZoneOffset.UTC) + "\"," +
                "\"status\":401," +
                "\"error\":\"Unauthorized\"," +
                "\"message\":\"Token inválido ou ausente\"," +
                "\"path\":\"" + request.getRequestURI() + "\"}";
        response.getWriter().write(body);
    }
}