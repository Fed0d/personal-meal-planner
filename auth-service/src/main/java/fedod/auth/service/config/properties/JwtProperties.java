package fedod.auth.service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    private String privateKeyPath;
    private long accessTokenExpirationMinutes;
    private long refreshTokenExpirationDays;
}
