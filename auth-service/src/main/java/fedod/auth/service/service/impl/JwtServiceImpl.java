package fedod.auth.service.service.impl;

import fedod.auth.service.entity.AuthUser;
import fedod.auth.service.service.JwtService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {
    @Override
    public String generateAccessToken(AuthUser user) {
        return "access-" + user.getId();
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
