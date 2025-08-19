package br.com.forumhub.infra.exception;


import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class ApiExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)

    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                HttpServletRequest req) {
        List<Map<String, String>> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toField)
                .toList();


        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Campos inválidos", req.getRequestURI());
        body.put("fields", fields);
        return ResponseEntity.badRequest().body(body);
    }


    @ExceptionHandler(DuplicateTopicException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateTopicException ex,
                                                               HttpServletRequest req) {
        Map<String, Object> body = baseBody(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(TopicoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(TopicoNotFoundException ex,
                                                              HttpServletRequest req) {
        Map<String, Object> body = baseBody(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex,
                                                                HttpServletRequest req) {
        Throwable cause = ex.getCause();
        if (cause instanceof UnrecognizedPropertyException upe) {
            String campo = upe.getPropertyName();
            String verbo = switch (req.getMethod()) {
                case "POST" -> "definir";
                case "PUT", "PATCH" -> "alterar";
                default -> "enviar";
            };
            Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST,
                    "Você não pode " + verbo + " o campo '" + campo + "'.", req.getRequestURI());
            body.put("campoNaoPermitido", campo);
            body.put("permitidos", allowedFields(req));
            return ResponseEntity.badRequest().body(body);
        }

        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Requisição inválida", req.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }


    private List<String> allowedFields(HttpServletRequest req) {
        String path = req.getRequestURI();
        String method = req.getMethod();
        if (path != null && path.startsWith("/topicos")) {
            if ("POST".equals(method)) {
                return List.of("titulo", "mensagem", "curso");
            }
            if ("PUT".equals(method) || "PATCH".equals(method)) {
                return List.of("titulo", "mensagem");
            }
        }
        return Collections.emptyList();
    }

    private Map<String, Object> baseBody(HttpStatus status, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now(ZoneOffset.UTC).toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return body;
    }


    private Map<String, String> toField(FieldError e) {
        Map<String, String> m = new HashMap<>();
        m.put("field", e.getField());
        m.put("message", e.getDefaultMessage());
        return m;
    }
}