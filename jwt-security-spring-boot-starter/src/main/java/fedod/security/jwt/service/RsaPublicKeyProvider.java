package fedod.security.jwt.service;

import fedod.security.jwt.config.JwtSecurityProperties;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
public class RsaPublicKeyProvider {

    private final PublicKey publicKey;

    public RsaPublicKeyProvider(JwtSecurityProperties properties, ResourceLoader resourceLoader) {
        try {
            this.publicKey = loadPublicKey(resourceLoader, properties.getPublicKeyPath());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA public key", e);
        }
    }

    private PublicKey loadPublicKey(ResourceLoader resourceLoader, String location) throws Exception {
        Resource resource = resourceLoader.getResource(location);
        String key = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        String normalized = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(normalized);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
}
