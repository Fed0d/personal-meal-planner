package fedod.auth.service.service;

import fedod.auth.service.entity.AuthUser;

public interface JwtService {

    String generateAccessToken(AuthUser user);

    String generateServiceToken();

    String generateRefreshToken();
}
