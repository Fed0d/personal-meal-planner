package fedod.user.service.service;

import fedod.user.service.dto.AllergensDto;

import java.util.UUID;

public interface AllergensService {

    AllergensDto get(UUID userId);

    AllergensDto update(UUID userId, AllergensDto request);
}
