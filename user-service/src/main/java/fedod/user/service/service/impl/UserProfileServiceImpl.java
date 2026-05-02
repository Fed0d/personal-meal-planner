package fedod.user.service.service.impl;

import fedod.user.service.dto.UpsertUserProfileRequest;
import fedod.user.service.dto.UserProfileResponse;
import fedod.user.service.entity.*;
import fedod.user.service.exception.UserProfileNotFoundException;
import fedod.user.service.repository.UserCalorieTargetsRepository;
import fedod.user.service.repository.UserProfileRepository;
import fedod.user.service.service.BmrCalculationService;
import fedod.user.service.service.BmrResult;
import fedod.user.service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserCalorieTargetsRepository calorieTargetsRepository;
    private final BmrCalculationService bmrCalculationService;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(UUID userId) {
        log.info("Fetching user profile for userId: {}", userId);

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for userId: " + userId));

        log.info("User profile found: {}", profile);
        return toResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse upsertCurrentUserProfile(UUID userId, UpsertUserProfileRequest request) {
        log.info("Upserting user profile for userId: {}", userId);

        UserProfile profile = userProfileRepository.findById(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUserId(userId);
            return p;
        });

        if (profile.getUserBodyMetrics() == null) {
            profile.setBodyMetrics(new UserBodyMetrics());
        }
        if (profile.getUserGoal() == null) {
            profile.setGoal(new UserGoal());
        }

        applyUpdates(profile, request);
        recalculateCaloriesIfPossible(profile);

        UserProfile savedProfile = userProfileRepository.save(profile);
        log.info("User profile upserted for userId: {}", userId);
        return toResponse(savedProfile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateCurrentUserProfile(UUID userId, UpsertUserProfileRequest request) {
        log.info("Updating user profile for userId: {}", userId);

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for userId: " + userId));

        if (profile.getUserBodyMetrics() == null) {
            profile.setBodyMetrics(new UserBodyMetrics());
        }
        if (profile.getUserGoal() == null) {
            profile.setGoal(new UserGoal());
        }

        applyUpdates(profile, request);
        recalculateCaloriesIfPossible(profile);

        UserProfile updatedProfile = userProfileRepository.save(profile);
        log.info("User profile updated for userId: {}", userId);
        return toResponse(updatedProfile);
    }

    private void applyUpdates(UserProfile profile, UpsertUserProfileRequest request) {
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setBirthDate(request.birthDate());
        profile.setGender(request.gender());

        UserBodyMetrics bodyMetrics = profile.getUserBodyMetrics();
        bodyMetrics.setUserId(profile.getUserId());
        bodyMetrics.setHeightCm(request.heightCm());
        bodyMetrics.setWeightKg(request.weightKg());
        bodyMetrics.setTargetWeightKg(request.targetWeightKg());

        UserGoal goal = profile.getUserGoal();
        goal.setUserId(profile.getUserId());
        goal.setGoalType(request.goalType());

        if (request.activityLevel() != null) {
            UserActivity activity = profile.getUserActivity();
            if (activity == null) {
                activity = new UserActivity();
                profile.setActivity(activity);
            }
            activity.setActivityLevel(request.activityLevel());
        }
    }

    private void recalculateCaloriesIfPossible(UserProfile profile) {
        UserBodyMetrics metrics = profile.getUserBodyMetrics();
        UserGoal goal = profile.getUserGoal();
        UserActivity activity = profile.getUserActivity();

        if (profile.getGender() == null || profile.getBirthDate() == null
                || metrics == null || metrics.getHeightCm() == null || metrics.getWeightKg() == null
                || goal == null || goal.getGoalType() == null
                || activity == null || activity.getActivityLevel() == null) {
            return;
        }

        BmrResult result = bmrCalculationService.calculate(
                profile.getGender(), profile.getBirthDate(),
                metrics.getHeightCm(), metrics.getWeightKg(),
                activity.getActivityLevel(), goal.getGoalType()
        );

        UserCalorieTargets targets = profile.getUserCalorieTargets();
        if (targets == null) {
            targets = new UserCalorieTargets();
            profile.setCalorieTargets(targets);
        }
        targets.setBmr(result.bmr());
        targets.setTargetCalories(result.targetCalories());
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        UserBodyMetrics metrics = profile.getUserBodyMetrics();
        UserGoal goal = profile.getUserGoal();
        UserActivity activity = profile.getUserActivity();
        UserCalorieTargets targets = profile.getUserCalorieTargets();

        return UserProfileResponse.builder()
                .userId(profile.getUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .birthDate(profile.getBirthDate())
                .gender(profile.getGender())
                .heightCm(metrics != null ? metrics.getHeightCm() : null)
                .weightKg(metrics != null ? metrics.getWeightKg() : null)
                .targetWeightKg(metrics != null ? metrics.getTargetWeightKg() : null)
                .goalType(goal != null ? goal.getGoalType() : null)
                .activityLevel(activity != null ? activity.getActivityLevel() : null)
                .bmr(targets != null ? targets.getBmr() : null)
                .targetCalories(targets != null ? targets.getTargetCalories() : null)
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
