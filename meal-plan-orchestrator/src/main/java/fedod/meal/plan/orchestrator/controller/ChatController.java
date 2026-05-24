package fedod.meal.plan.orchestrator.controller;

import fedod.meal.plan.orchestrator.dto.IngredientChatRequest;
import fedod.meal.plan.orchestrator.dto.advisor.MealAdvisorResponse;
import fedod.meal.plan.orchestrator.service.ChatService;
import fedod.security.jwt.model.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/orchestrator/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/from-ingredients")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public MealAdvisorResponse fromIngredients(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody IngredientChatRequest request
    ) {
        log.info("Chat from-ingredients: userId={} count={}",
                principal.userId(), request.ingredients().size());
        return chatService.fromIngredients(principal.userId(), request);
    }
}
