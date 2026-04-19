package fedod.auth.service.service.security;

import fedod.auth.service.config.properties.JwtProperties;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Getter
@Component
public class RsaPrivateKeyProvider {

    private final PrivateKey privateKey;

    public RsaPrivateKeyProvider(JwtProperties jwtProperties, ResourceLoader resourceLoader) {
        try {
            this.privateKey = loadPrivateKey(resourceLoader, jwtProperties.getPrivateKeyPath());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA keys", e);
        }
    }

    private PrivateKey loadPrivateKey(ResourceLoader resourceLoader, String location)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Resource resource = resourceLoader.getResource(location);
        String key = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        String normalizedKey = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(normalizedKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }
}
