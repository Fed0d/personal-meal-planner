package fedod.user.service.controller;

import fedod.user.service.dto.QuestionnaireResponse;
import fedod.user.service.service.QuestionnaireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final QuestionnaireService questionnaireService;

    @GetMapping("/{userId}/meal-profile")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('SERVICE')")
    public QuestionnaireResponse getMealProfile(@PathVariable UUID userId) {
        log.info("Internal: getting meal profile for userId: {}", userId);
        return questionnaireService.get(userId);
    }
}
