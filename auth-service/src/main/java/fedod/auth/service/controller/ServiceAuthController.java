package fedod.auth.service.controller;

import fedod.auth.service.config.properties.ServiceProperties;
import fedod.auth.service.dto.request.ServiceTokenRequest;
import fedod.auth.service.dto.response.ServiceTokenResponse;
import fedod.auth.service.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth/service")
public class ServiceAuthController {

    private final JwtService jwtService;
    private final ServiceProperties serviceProperties;

    @PostMapping("/token")
    public ResponseEntity<ServiceTokenResponse> serviceToken(@Valid @RequestBody ServiceTokenRequest request) {
        log.info("Received service token request");

        if (!request.apiKey().equals(serviceProperties.getApiKey())) {
            log.warn("Invalid service API key provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateServiceToken();
        log.info("Service token issued successfully");

        return ResponseEntity.ok(ServiceTokenResponse.builder()
                .accessToken(token)
                .build());
    }
}
