package fedod.user.service.service.impl;

import fedod.user.service.dto.*;
import fedod.user.service.entity.*;
import fedod.user.service.exception.UserProfileNotFoundException;
import fedod.user.service.repository.*;
import fedod.user.service.service.BmrCalculationService;
import fedod.user.service.service.BmrResult;
import fedod.user.service.service.QuestionnaireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final UserProfileRepository userProfileRepository;
    private final UserIngredientPreferenceRepository ingredientPreferenceRepository;
    private final BmrCalculationService bmrCalculationService;

    @Override
    @Transactional
    public QuestionnaireResponse submit(UUID userId, SubmitQuestionnaireRequest request) {
        log.info("Submitting questionnaire for userId: {}", userId);

        UserProfile profile = userProfileRepository.findById(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUserId(userId);
            return p;
        });

        profile.setGender(request.gender());
        profile.setBirthDate(request.birthDate());

        applyBodyMetrics(profile, request);
        applyGoal(profile, request);
        applyActivity(profile, request);
        applyCookingPreferences(profile, request);
        applyCuisinePreferences(profile, request);
        applyAllergens(profile, request);

        BmrResult bmrResult = bmrCalculationService.calculate(
                profile.getGender(),
                profile.getBirthDate(),
                profile.getUserBodyMetrics().getHeightCm(),
                profile.getUserBodyMetrics().getWeightKg(),
                profile.getUserActivity().getActivityLevel(),
                profile.getUserGoal().getGoalType()
        );
        applyCalorieTargets(profile, bmrResult);

        userProfileRepository.save(profile);

        ingredientPreferenceRepository.deleteByUserId(userId);
        List<UserIngredientPreference> ingredients = request.ingredientPreferences().stream()
                .map(dto -> UserIngredientPreference.builder()
                        .userId(userId)
                        .ingredientName(dto.name())
                        .score(dto.score())
                        .build())
                .toList();
        ingredientPreferenceRepository.saveAll(ingredients);

        log.info("Questionnaire submitted for userId: {}", userId);
        return buildResponse(profile, ingredients);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionnaireResponse get(UUID userId) {
        log.info("Fetching questionnaire for userId: {}", userId);

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for userId: " + userId));

        List<UserIngredientPreference> ingredients = ingredientPreferenceRepository.findByUserId(userId);

        return buildResponse(profile, ingredients);
    }

    private void applyBodyMetrics(UserProfile profile, SubmitQuestionnaireRequest request) {
        UserBodyMetrics metrics = profile.getUserBodyMetrics();
        if (metrics == null) {
            metrics = new UserBodyMetrics();
            profile.setBodyMetrics(metrics);
        }
        metrics.setHeightCm(request.heightCm());
        metrics.setWeightKg(request.weightKg());
        metrics.setTargetWeightKg(request.targetWeightKg());
    }

    private void applyGoal(UserProfile profile, SubmitQuestionnaireRequest request) {
        UserGoal goal = profile.getUserGoal();
        if (goal == null) {
            goal = new UserGoal();
            profile.setGoal(goal);
        }
        goal.setGoalType(request.goalType());
    }

    private void applyActivity(UserProfile profile, SubmitQuestionnaireRequest request) {
        UserActivity activity = profile.getUserActivity();
        if (activity == null) {
            activity = new UserActivity();
            profile.setActivity(activity);
        }
        activity.setActivityLevel(request.activityLevel());
    }

    private void applyCookingPreferences(UserProfile profile, SubmitQuestionnaireRequest request) {
        UserCookingPreferences cooking = profile.getUserCookingPreferences();
        if (cooking == null) {
            cooking = new UserCookingPreferences();
            profile.setCookingPreferences(cooking);
        }
        cooking.setActiveCookingTimeMin(request.activeCookingTimeMin());
        cooking.setPassiveCookingTimeMin(request.passiveCookingTimeMin());
    }

    private void applyCuisinePreferences(UserProfile profile, SubmitQuestionnaireRequest request) {
        UserCuisinePreferences cuisine = profile.getCuisinePreferences();
        if (cuisine == null) {
            cuisine = new UserCuisinePreferences();
            profile.setCuisinePreferences(cuisine);
        }
        CuisinePreferencesDto dto = request.cuisinePreferences();
        cuisine.setAsian(dto.asian());
        cuisine.setEuropean(dto.european());
        cuisine.setEastern(dto.eastern());
        cuisine.setSlavic(dto.slavic());
        cuisine.setAmerican(dto.american());
        cuisine.setMexican(dto.mexican());
    }

    private void applyAllergens(UserProfile profile, SubmitQuestionnaireRequest request) {
        UserAllergens allergens = profile.getAllergens();
        if (allergens == null) {
            allergens = new UserAllergens();
            profile.setAllergens(allergens);
        }
        AllergensDto dto = request.allergens();
        allergens.setNuts(dto.nuts());
        allergens.setSesame(dto.sesame());
        allergens.setPeanut(dto.peanut());
        allergens.setFish(dto.fish());
        allergens.setCrustaceans(dto.crustaceans());
        allergens.setMolluscs(dto.molluscs());
        allergens.setDairy(dto.dairy());
        allergens.setGluten(dto.gluten());
        allergens.setEgg(dto.egg());
        allergens.setCelery(dto.celery());
        allergens.setSoy(dto.soy());
        allergens.setFoodAdditives(dto.foodAdditives());
        allergens.setMustard(dto.mustard());
        allergens.setStrawberry(dto.strawberry());
        allergens.setNone(dto.none());
    }

    private void applyCalorieTargets(UserProfile profile, BmrResult bmrResult) {
        UserCalorieTargets targets = profile.getUserCalorieTargets();
        if (targets == null) {
            targets = new UserCalorieTargets();
            profile.setCalorieTargets(targets);
        }
        targets.setBmr(bmrResult.bmr());
        targets.setTargetCalories(bmrResult.targetCalories());
    }

    private QuestionnaireResponse buildResponse(UserProfile profile, List<UserIngredientPreference> ingredients) {
        UserActivity activity = profile.getUserActivity();
        UserCookingPreferences cooking = profile.getUserCookingPreferences();
        UserCalorieTargets targets = profile.getUserCalorieTargets();
        UserCuisinePreferences cuisine = profile.getCuisinePreferences();
        UserAllergens allergens = profile.getAllergens();

        CuisinePreferencesDto cuisineDto = cuisine == null ? null : CuisinePreferencesDto.builder()
                .asian(cuisine.getAsian())
                .european(cuisine.getEuropean())
                .eastern(cuisine.getEastern())
                .slavic(cuisine.getSlavic())
                .american(cuisine.getAmerican())
                .mexican(cuisine.getMexican())
                .build();

        List<IngredientPreferenceDto> ingredientDtos = ingredients.stream()
                .map(i -> IngredientPreferenceDto.builder()
                        .name(i.getIngredientName())
                        .score(i.getScore())
                        .build())
                .toList();

        AllergensDto allergensDto = allergens == null ? null : AllergensDto.builder()
                .nuts(allergens.getNuts())
                .sesame(allergens.getSesame())
                .peanut(allergens.getPeanut())
                .fish(allergens.getFish())
                .crustaceans(allergens.getCrustaceans())
                .molluscs(allergens.getMolluscs())
                .dairy(allergens.getDairy())
                .gluten(allergens.getGluten())
                .egg(allergens.getEgg())
                .celery(allergens.getCelery())
                .soy(allergens.getSoy())
                .foodAdditives(allergens.getFoodAdditives())
                .mustard(allergens.getMustard())
                .strawberry(allergens.getStrawberry())
                .none(allergens.getNone())
                .build();

        return QuestionnaireResponse.builder()
                .activityLevel(activity != null ? activity.getActivityLevel() : null)
                .activeCookingTimeMin(cooking != null ? cooking.getActiveCookingTimeMin() : null)
                .passiveCookingTimeMin(cooking != null ? cooking.getPassiveCookingTimeMin() : null)
                .bmr(targets != null ? targets.getBmr() : null)
                .targetCalories(targets != null ? targets.getTargetCalories() : null)
                .cuisinePreferences(cuisineDto)
                .ingredientPreferences(ingredientDtos)
                .allergens(allergensDto)
                .build();
    }
}
