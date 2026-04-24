package fedod.user.service.service.impl;

import fedod.user.service.dto.UpsertUserProfileRequest;
import fedod.user.service.dto.UserProfileResponse;
import fedod.user.service.entity.UserBodyMetrics;
import fedod.user.service.entity.UserGoal;
import fedod.user.service.entity.UserProfile;
import fedod.user.service.exception.UserProfileNotFoundException;
import fedod.user.service.repository.UserProfileRepository;
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
    public UserProfileResponse createCurrentUserProfile(UUID userId, UpsertUserProfileRequest request) {
        log.info("Creating user profile for userId: {}", userId);

        if (userProfileRepository.existsById(userId)) {
            UserProfile userProfile = userProfileRepository.findById(userId)
                    .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for userId: " + userId));

            applyUpdates(userProfile, request);
            UserProfile savedProfile = userProfileRepository.save(userProfile);

            log.info("User profile updated for userId: {}", userId);

            return toResponse(savedProfile);
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userId);

        UserBodyMetrics userBodyMetrics = new UserBodyMetrics();
        UserGoal userGoal = new UserGoal();

        userProfile.setUserBodyMetrics(userBodyMetrics);
        userProfile.setUserGoal(userGoal);

        applyUpdates(userProfile, request);

        UserProfile savedProfile = userProfileRepository.save(userProfile);

        log.info("User profile created for userId: {}", userId);

        return toResponse(savedProfile);
    }

    @Override
    public UserProfileResponse updateCurrentUserProfile(UUID userId, UpsertUserProfileRequest request) {
        log.info("Updating user profile for userId: {}", userId);

        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for userId: " + userId));

        if (userProfile.getUserBodyMetrics() == null) {
            userProfile.setUserBodyMetrics(new UserBodyMetrics());
        }

        if (userProfile.getUserGoal() == null) {
            userProfile.setGoal(new UserGoal());
        }

        applyUpdates(userProfile, request);

        UserProfile updatedProfile = userProfileRepository.save(userProfile);

        log.info("User profile updated for userId: {}", userId);

        return toResponse(updatedProfile);
    }

    private void applyUpdates(UserProfile userProfile, UpsertUserProfileRequest upsertUserProfileRequest) {
        userProfile.setFirstName(upsertUserProfileRequest.firstName());
        userProfile.setLastName(upsertUserProfileRequest.lastName());
        userProfile.setBirthDate(upsertUserProfileRequest.birthDate());
        userProfile.setGender(upsertUserProfileRequest.gender());

        UserBodyMetrics bodyMetrics = userProfile.getUserBodyMetrics();
        bodyMetrics.setHeightCm(upsertUserProfileRequest.heightCm());
        bodyMetrics.setWeightKg(upsertUserProfileRequest.weightKg());
        bodyMetrics.setTargetWeightKg(upsertUserProfileRequest.targetWeightKg());

        UserGoal goal = userProfile.getUserGoal();
        goal.setGoalType(upsertUserProfileRequest.goalType());
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .userId(profile.getUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .birthDate(profile.getBirthDate())
                .gender(profile.getGender())
                .heightCm(profile.getUserBodyMetrics() != null ? profile.getUserBodyMetrics().getHeightCm() : null)
                .weightKg(profile.getUserBodyMetrics() != null ? profile.getUserBodyMetrics().getWeightKg() : null)
                .targetWeightKg(profile.getUserGoal() != null ? profile.getUserBodyMetrics().getTargetWeightKg() : null)
                .goalType(profile.getUserGoal() != null ? profile.getUserGoal().getGoalType() : null)
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}