package fedod.user.service.service;

import fedod.user.service.dto.QuestionnaireResponse;
import fedod.user.service.dto.SubmitQuestionnaireRequest;

import java.util.UUID;

public interface QuestionnaireService {

    QuestionnaireResponse submit(UUID userId, SubmitQuestionnaireRequest request);

    QuestionnaireResponse get(UUID userId);
}
