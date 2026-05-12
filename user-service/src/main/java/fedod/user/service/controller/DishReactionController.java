package fedod.user.service.controller;

import fedod.security.jwt.model.JwtUserPrincipal;
import fedod.user.service.dto.DishReactionDto;
import fedod.user.service.dto.SetDishReactionRequest;
import fedod.user.service.service.DishReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/me/dish-reactions")
@RequiredArgsConstructor
public class DishReactionController {

    private final DishReactionService dishReactionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DishReactionDto> getAll(@AuthenticationPrincipal JwtUserPrincipal principal) {
        log.info("Get all dish reactions for userId: {}", principal.userId());
        return dishReactionService.getAll(principal.userId());
    }

    @GetMapping("/{dishId}")
    @ResponseStatus(HttpStatus.OK)
    public DishReactionDto getOne(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long dishId
    ) {
        log.info("Get dish reaction for dishId: {} userId: {}", dishId, principal.userId());
        return DishReactionDto.builder()
                .dishId(dishId)
                .reaction(dishReactionService.getOne(principal.userId(), dishId).orElse(null))
                .build();
    }

    @PutMapping("/{dishId}")
    @ResponseStatus(HttpStatus.OK)
    public DishReactionDto set(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long dishId,
            @Valid @RequestBody SetDishReactionRequest request
    ) {
        log.info("Set dish reaction {} for dishId: {} userId: {}", request.reaction(), dishId, principal.userId());
        return dishReactionService.set(principal.userId(), dishId, request.reaction());
    }

    @DeleteMapping("/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long dishId
    ) {
        log.info("Remove dish reaction for dishId: {} userId: {}", dishId, principal.userId());
        dishReactionService.remove(principal.userId(), dishId);
    }
}
