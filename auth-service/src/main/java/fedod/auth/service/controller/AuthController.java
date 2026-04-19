package fedod.auth.service.controller;

import fedod.auth.service.dto.request.LoginRequest;
import fedod.auth.service.dto.request.LogoutRequest;
import fedod.auth.service.dto.request.RefreshTokenRequest;
import fedod.auth.service.dto.request.RegisterRequest;
import fedod.auth.service.dto.response.AuthResponse;
import fedod.auth.service.dto.response.MeResponse;
import fedod.auth.service.dto.response.MessageResponse;
import fedod.auth.service.service.AuthService;
import fedod.auth.service.service.security.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for email: {}", request.email());

        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for email: {}", request.email());

        return authService.login(request);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Received token refresh request");

        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse logout(@Valid @RequestBody LogoutRequest request) {
        log.info("Received logout request");

        authService.logout(request);

        return MessageResponse.builder()
                .message("Logged out successfully")
                .build();
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public MeResponse me(@AuthenticationPrincipal JwtUserPrincipal principal) {
        log.info("Received request for current user info - userId: {}", principal.userId());

        return MeResponse.builder()
                .userId(principal.userId())
                .email(principal.email())
                .role(principal.role())
                .build();
    }
}
