package fedod.security.jwt.util;

import fedod.security.jwt.model.JwtUserPrincipal;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@NoArgsConstructor
public class SecurityUtil {

    public static Optional<JwtUserPrincipal> getCurrentPrincipal() {
        log.debug("Attempting to retrieve current user principal from security context");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("No authentication found in security context");
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtUserPrincipal jwtUserPrincipal) {
            log.info("Successfully retrieved user principal - {}", jwtUserPrincipal);
            return Optional.of(jwtUserPrincipal);
        }

        assert principal != null;
        log.warn("Principal is not of type JwtUserPrincipal - found type: {}", principal.getClass().getName());
        return Optional.empty();
    }

    public static UUID getCurrentUserId() {
        return getCurrentPrincipal()
                .map(JwtUserPrincipal::userId)
                .orElseThrow(() -> new IllegalStateException("No authenticated user found in security context"));
    }
}
