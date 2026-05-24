package fedod.meal.plan.orchestrator.service.impl;

import fedod.meal.plan.orchestrator.dto.IngredientChatRequest;
import fedod.meal.plan.orchestrator.dto.advisor.MealAdvisorResponse;
import fedod.meal.plan.orchestrator.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final RestClient mealAdvisorRestClient;

    public ChatServiceImpl(@Qualifier("mealAdvisorRestClient") RestClient mealAdvisorRestClient) {
        this.mealAdvisorRestClient = mealAdvisorRestClient;
    }

    @Override
    public MealAdvisorResponse fromIngredients(UUID userId, IngredientChatRequest request) {
        Map<String, Object> body = Map.of("ingredients", request.ingredients());

        log.info("Forwarding chat request to meal-advisor: userId={} ingredients={}",
                userId, request.ingredients().size());

        try {
            return mealAdvisorRestClient.post()
                    .uri("/recommend")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .exchange((req, res) -> {
                        HttpStatusCode status = res.getStatusCode();
                        if (status.isError()) {
                            String rawBody = readBody(res.getBody());
                            String detail  = extractDetail(rawBody);
                            log.warn("meal-advisor returned {} for userId={} — body: {}",
                                    status, userId, rawBody);
                            throw new ResponseStatusException(status,
                                    "ML-сервис: " + (detail != null ? detail : status));
                        }
                        return res.bodyTo(MealAdvisorResponse.class);
                    });
        } catch (ResourceAccessException e) {
            log.error("meal-advisor unreachable (check MEAL_ADVISOR_URL): {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatusCode.valueOf(503),
                    "ML-сервис временно недоступен");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling meal-advisor", e);
            throw new ResponseStatusException(HttpStatusCode.valueOf(502),
                    "Сбой при обращении к ML-сервису: " + e.getMessage());
        }
    }

    private String readBody(java.io.InputStream in) {
        try {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return "";
        }
    }

    private String extractDetail(String body) {
        if (body == null || body.isBlank()) return null;
        int idx = body.indexOf("\"detail\"");
        if (idx < 0) return body;
        int colon  = body.indexOf(':', idx);
        int quote1 = body.indexOf('"', colon + 1);
        int quote2 = body.indexOf('"', quote1 + 1);
        return (quote1 > 0 && quote2 > quote1) ? body.substring(quote1 + 1, quote2) : body;
    }
}
