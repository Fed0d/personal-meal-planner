package fedod.user.service.controller;

import fedod.security.jwt.model.JwtUserPrincipal;
import fedod.user.service.dto.UpsertUserProfileRequest;
import fedod.user.service.dto.UserProfileResponse;
import fedod.user.service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileResponse getMe(@AuthenticationPrincipal JwtUserPrincipal principal) {
        log.info("Received request to get current user profile for userId: {}", principal.userId());

        return userProfileService.getCurrentUserProfile(principal.userId());
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileResponse upsertMe(@AuthenticationPrincipal JwtUserPrincipal principal,
                                        @Valid @RequestBody UpsertUserProfileRequest upsertUserProfileRequest
    ) {
        log.info("Received request to update current user profile for userId: {}", principal.userId());

        return userProfileService.upsertCurrentUserProfile(principal.userId(), upsertUserProfileRequest);
    }
}
