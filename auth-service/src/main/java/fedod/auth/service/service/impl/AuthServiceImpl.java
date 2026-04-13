package fedod.auth.service.service.impl;

import fedod.auth.service.dto.AuthResponse;
import fedod.auth.service.dto.LoginRequest;
import fedod.auth.service.dto.RegisterRequest;
import fedod.auth.service.entity.AuthUser;
import fedod.auth.service.entity.RefreshToken;
import fedod.auth.service.entity.UserRole;
import fedod.auth.service.entity.UserStatus;
import fedod.auth.service.exception.InvalidCredentialsException;
import fedod.auth.service.exception.UserAlreadyExistsException;
import fedod.auth.service.exception.UserBlockedException;
import fedod.auth.service.repository.AuthUserRepository;
import fedod.auth.service.repository.RefreshTokenRepository;
import fedod.auth.service.service.AuthService;
import fedod.auth.service.service.HashService;
import fedod.auth.service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final HashService hashService;


    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        if (authUserRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        AuthUser user = AuthUser.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        AuthUser savedUser = authUserRepository.save(user);

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshTokenRaw = jwtService.generateRefreshToken();

        saveRefreshToken(savedUser, refreshTokenRaw);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenRaw)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        AuthUser user = authUserRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (user.isBlocked()) {
            throw new UserBlockedException("User is blocked");
        }

        if (user.isDeleted()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenRaw = jwtService.generateRefreshToken();

        saveRefreshToken(user, refreshTokenRaw);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenRaw)
                .build();
    }

    private void saveRefreshToken(AuthUser user, String refreshTokenRaw) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashService.sha256(refreshTokenRaw))
                .expiresAt(LocalDateTime.now().plusDays(30))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
