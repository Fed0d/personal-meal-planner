package fedod.user.service.dto;

import lombok.Builder;

@Builder
public record AllergensDto(
        Boolean nuts,
        Boolean sesame,
        Boolean peanut,
        Boolean fish,
        Boolean crustaceans,
        Boolean molluscs,
        Boolean dairy,
        Boolean gluten,
        Boolean egg,
        Boolean celery,
        Boolean soy,
        Boolean foodAdditives,
        Boolean mustard,
        Boolean strawberry,
        Boolean none
) {
}
