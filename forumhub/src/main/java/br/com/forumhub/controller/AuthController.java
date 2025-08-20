package br.com.forumhub.controller;


import br.com.forumhub.controller.dto.LoginRequest;
import br.com.forumhub.controller.dto.TokenResponse;
import br.com.forumhub.infra.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping({"/auth", ""})
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;


    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Validated @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );
        String token = tokenService.generateToken(auth.getName());
        return ResponseEntity.ok(new TokenResponse(token, "Bearer"));
    }
}