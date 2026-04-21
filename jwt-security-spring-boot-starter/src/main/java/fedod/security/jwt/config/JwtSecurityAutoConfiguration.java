package fedod.security.jwt.config;

import fedod.security.jwt.filter.JwtAuthenticationFilter;
import fedod.security.jwt.handler.JwtAccessDeniedHandler;
import fedod.security.jwt.handler.JwtAuthenticationEntryPoint;
import fedod.security.jwt.service.JwtTokenValidator;
import fedod.security.jwt.service.RsaPublicKeyProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration
@EnableConfigurationProperties(JwtSecurityProperties.class)
public class JwtSecurityAutoConfiguration {

    @Bean
    public RsaPublicKeyProvider rsaPublicKeyProvider(JwtSecurityProperties properties,
                                                     ResourceLoader resourceLoader) {
        return new RsaPublicKeyProvider(properties, resourceLoader);
    }

    @Bean
    public JwtTokenValidator jwtTokenValidator(RsaPublicKeyProvider publicKeyProvider,
                                               JwtSecurityProperties properties) {
        return new JwtTokenValidator(publicKeyProvider, properties);
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new JwtAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public JwtAccessDeniedHandler jwtAccessDeniedHandler(ObjectMapper objectMapper) {
        return new JwtAccessDeniedHandler(objectMapper);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenValidator validator,
                                                           JwtAuthenticationEntryPoint entryPoint) {
        return new JwtAuthenticationFilter(validator, entryPoint);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("User not found");
        };
    }
}
