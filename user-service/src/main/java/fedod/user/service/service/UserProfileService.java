package fedod.user.service.service;

import fedod.user.service.dto.UpsertUserProfileRequest;
import fedod.user.service.dto.UserProfileResponse;

import java.util.UUID;

public interface UserProfileService {

    UserProfileResponse getCurrentUserProfile(UUID userId);

    UserProfileResponse upsertCurrentUserProfile(UUID userId, UpsertUserProfileRequest request);

    UserProfileResponse updateCurrentUserProfile(UUID userId, UpsertUserProfileRequest request);
}
