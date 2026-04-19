package fedod.security.jwt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtSecurityProperties {
    private String publicKeyPath;
    private String issuer;
    private boolean enabled = true;
}
