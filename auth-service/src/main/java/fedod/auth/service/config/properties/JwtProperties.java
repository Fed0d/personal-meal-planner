package fedod.auth.service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private String privateKeyPath;
    private String publicKeyPath;
    private long accessTokenExpirationMinutes;
    private long refreshTokenExpirationDays;
    private String issuer;
}
