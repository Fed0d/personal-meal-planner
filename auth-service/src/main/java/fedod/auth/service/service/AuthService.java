package fedod.auth.service.service;

import fedod.auth.service.dto.request.LoginRequest;
import fedod.auth.service.dto.request.LogoutRequest;
import fedod.auth.service.dto.request.RefreshTokenRequest;
import fedod.auth.service.dto.request.RegisterRequest;
import fedod.auth.service.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(LogoutRequest request);
}
