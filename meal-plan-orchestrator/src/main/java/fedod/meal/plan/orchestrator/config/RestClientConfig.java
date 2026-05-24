package fedod.meal.plan.orchestrator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final DietAssistantProperties dietAssistantProperties;
    private final MealAdvisorProperties mealAdvisorProperties;

    @Bean
    public RestClient dietAssistantRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(dietAssistantProperties.getConnectTimeoutMs()))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(dietAssistantProperties.getReadTimeoutMs()));

        return RestClient.builder()
                .baseUrl(dietAssistantProperties.getUrl())
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    public RestClient mealAdvisorRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(mealAdvisorProperties.getConnectTimeoutMs()))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(mealAdvisorProperties.getReadTimeoutMs()));

        return RestClient.builder()
                .baseUrl(mealAdvisorProperties.getUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
