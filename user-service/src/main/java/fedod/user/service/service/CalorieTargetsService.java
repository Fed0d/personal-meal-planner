package fedod.user.service.service;

import fedod.user.service.dto.CalorieTargetsDto;

import java.util.UUID;

public interface CalorieTargetsService {

    CalorieTargetsDto get(UUID userId);
}
