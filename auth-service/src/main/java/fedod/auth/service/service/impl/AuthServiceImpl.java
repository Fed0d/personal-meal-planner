package fedod.auth.service.service.impl;

import fedod.auth.service.config.properties.JwtProperties;
import fedod.auth.service.dto.request.LoginRequest;
import fedod.auth.service.dto.request.LogoutRequest;
import fedod.auth.service.dto.request.RefreshTokenRequest;
import fedod.auth.service.dto.request.RegisterRequest;
import fedod.auth.service.dto.response.AuthResponse;
import fedod.auth.service.entity.AuthUser;
import fedod.auth.service.entity.RefreshToken;
import fedod.auth.service.entity.enums.UserRole;
import fedod.auth.service.entity.enums.UserStatus;
import fedod.auth.service.exception.InvalidCredentialsException;
import fedod.auth.service.exception.UserAlreadyExistsException;
import fedod.auth.service.exception.UserBlockedException;
import fedod.auth.service.repository.AuthUserRepository;
import fedod.auth.service.repository.RefreshTokenRepository;
import fedod.auth.service.service.AuthService;
import fedod.auth.service.service.HashService;
import fedod.auth.service.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final HashService hashService;
    private final JwtProperties jwtProperties;


    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.email());

        String normalizedEmail = normalizeEmail(request.email());

        if (authUserRepository.existsByEmail(normalizedEmail)) {
            log.error("Registration failed - email already in use: {}", normalizedEmail);
            throw new UserAlreadyExistsException("Email is already in use");
        }

        AuthUser user = AuthUser.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        AuthUser savedUser = authUserRepository.save(user);

        log.info("User registered successfully with email: {}", savedUser.getEmail());

        return issueTokens(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to log in user with email: {}", request.email());

        String normalizedEmail = normalizeEmail(request.email());

        AuthUser user = authUserRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    log.error("Login failed - user not found with email: {}", normalizedEmail);
                    return new InvalidCredentialsException("User not found with email: " + normalizedEmail);
                });

        if (user.isBlocked()) {
            log.error("Login failed - user is blocked with email: {}", normalizedEmail);
            throw new UserBlockedException("User is blocked");
        }

        if (user.isDeleted()) {
            log.error("Login failed - user is deleted with email: {}", normalizedEmail);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.error("Login failed - invalid password for email: {}", normalizedEmail);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        log.info("Refreshing access token using refresh token");

        String tokenHash = hashService.sha256(request.refreshToken());

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found or invalid");
                    return new InvalidCredentialsException("Invalid refresh token");
                });

        if (!refreshToken.isActive()) {
            log.warn("Refresh token is expired, revoked, or invalid");
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        AuthUser user = refreshToken.getUser();

        if (user.getStatus() == UserStatus.BLOCKED) {
            log.warn("User is blocked, cannot refresh token for user with email: {}", user.getEmail());
            throw new UserBlockedException("User is blocked");
        }

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);

        return issueTokens(user);
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        log.info("Logging out user and revoking refresh token");

        String tokenHash = hashService.sha256(request.refreshToken());

        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.revoke();
            refreshTokenRepository.save(token);

            log.info("User logged out successfully, refresh token revoked");
        });
    }

    private AuthResponse issueTokens(AuthUser user) {
        log.info("Issuing new access and refresh tokens for user with email: {}", user.getEmail());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenRaw = jwtService.generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashService.sha256(refreshTokenRaw));
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(jwtProperties.getRefreshTokenExpirationDays()));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        log.info("Tokens issued successfully for user with email: {}", user.getEmail());

        return new AuthResponse(accessToken, refreshTokenRaw);
    }


    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
