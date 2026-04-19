package fedod.auth.service.service.security;

import fedod.auth.service.config.properties.JwtProperties;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
@Component
public class RsaKeyProvider {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public RsaKeyProvider(JwtProperties jwtProperties, ResourceLoader resourceLoader) {
        try {
            this.privateKey = loadPrivateKey(resourceLoader, jwtProperties.getPrivateKeyPath());
            this.publicKey = loadPublicKey(resourceLoader, jwtProperties.getPublicKeyPath());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA keys", e);
        }
    }

    @SneakyThrows
    private PrivateKey loadPrivateKey(ResourceLoader resourceLoader, String location) {
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

    @SneakyThrows
    private PublicKey loadPublicKey(ResourceLoader resourceLoader, String location) {
        Resource resource = resourceLoader.getResource(location);
        String key = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        String normalizedKey = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(normalizedKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
}
