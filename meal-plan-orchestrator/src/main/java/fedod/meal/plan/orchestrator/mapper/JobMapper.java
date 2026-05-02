package fedod.meal.plan.orchestrator.mapper;

import fedod.meal.plan.orchestrator.dto.JobResponse;
import fedod.meal.plan.orchestrator.entity.Job;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobMapper {

    JobResponse toResponse(Job job);
}
