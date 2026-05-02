package fedod.user.service.controller;

import fedod.security.jwt.model.JwtUserPrincipal;
import fedod.user.service.dto.*;
import fedod.user.service.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserQuestionnaireController {

    private final QuestionnaireService questionnaireService;
    private final CuisinePreferencesService cuisinePreferencesService;
    private final CookingPreferencesService cookingPreferencesService;
    private final IngredientPreferencesService ingredientPreferencesService;
    private final AllergensService allergensService;
    private final CalorieTargetsService calorieTargetsService;

    @PostMapping("/me/questionnaire")
    @ResponseStatus(HttpStatus.OK)
    public QuestionnaireResponse submitQuestionnaire(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody SubmitQuestionnaireRequest request) {
        log.info("Received questionnaire submission for userId: {}", principal.userId());
        return questionnaireService.submit(principal.userId(), request);
    }

    @GetMapping("/me/questionnaire")
    @ResponseStatus(HttpStatus.OK)
    public QuestionnaireResponse getQuestionnaire(@AuthenticationPrincipal JwtUserPrincipal principal) {
        log.info("Received request to get questionnaire for userId: {}", principal.userId());
        return questionnaireService.get(principal.userId());
    }

    @GetMapping("/me/calorie-targets")
    @ResponseStatus(HttpStatus.OK)
    public CalorieTargetsDto getCalorieTargets(@AuthenticationPrincipal JwtUserPrincipal principal) {
        log.info("Received request to get calorie targets for userId: {}", principal.userId());
        return calorieTargetsService.get(principal.userId());
    }

    @GetMapping("/me/preferences/cuisine")
    @ResponseStatus(HttpStatus.OK)
    public CuisinePreferencesDto getCuisinePreferences(@AuthenticationPrincipal JwtUserPrincipal principal) {
        log.info("Received request to get cuisine preferences for userId: {}", principal.userId());
        return cuisinePreferencesService.get(principal.userId());
    }

    @PutMapping("/me/preferences/cuisine")
    @ResponseStatus(HttpStatus.OK)
    public CuisinePreferencesDto updateCuisinePreferences(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody CuisinePreferencesDto request) {
        log.info("Received request to update cuisine preferences for userId: {}", principal.userId());
        return cuisinePreferencesService.update(principal.userId(), request);
    }

    @GetMapping("/me/preferences/ingredients")
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientPreferenceDto> getIngredientPreferences(@AuthenticationPrincipal JwtUserPrincipal principal) {
        log.info("Received request to get ingredient preferences for userId: {}", principal.userId());
        return ingredientPreferencesService.get(principal.userId());
    }

    @PutMapping("/me/preferences/ingredients")
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientPreferenceDto> updateIngredientPreferences(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody List<@Valid IngredientPreferenceDto> request) {
        log.info("Received request to update ingredient preferences for userId: {}", principal.userId());
        return ingredientPreferencesService.update(principal.userId(), request);
    }

    @GetMapping("/me/preferences/cooking")
    @ResponseStatus(HttpStatus.OK)
    public CookingPreferencesDto getCookingPreferences(@AuthenticationPrincipal JwtUserPrincipal principal) {
        log.info("Received request to get cooking preferences for userId: {}", principal.userId());
        return cookingPreferencesService.get(principal.userId());
    }

    @PutMapping("/me/preferences/cooking")
    @ResponseStatus(HttpStatus.OK)
    public CookingPreferencesDto updateCookingPreferences(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody CookingPreferencesDto request) {
        log.info("Received request to update cooking preferences for userId: {}", principal.userId());
        return cookingPreferencesService.update(principal.userId(), request);
    }

    @GetMapping("/me/allergens")
    @ResponseStatus(HttpStatus.OK)
    public AllergensDto getAllergens(@AuthenticationPrincipal JwtUserPrincipal principal) {
        log.info("Received request to get allergens for userId: {}", principal.userId());
        return allergensService.get(principal.userId());
    }

    @PutMapping("/me/allergens")
    @ResponseStatus(HttpStatus.OK)
    public AllergensDto updateAllergens(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody AllergensDto request) {
        log.info("Received request to update allergens for userId: {}", principal.userId());
        return allergensService.update(principal.userId(), request);
    }
}
