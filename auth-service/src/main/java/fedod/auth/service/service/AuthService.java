package fedod.auth.service.service;

import fedod.auth.service.dto.AuthResponse;
import fedod.auth.service.dto.LoginRequest;
import fedod.auth.service.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
