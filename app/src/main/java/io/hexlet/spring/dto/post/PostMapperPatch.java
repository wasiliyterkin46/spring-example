package io.hexlet.spring.dto.post;

import io.hexlet.spring.mapper.JsonNullableMapper;
import io.hexlet.spring.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        // Подключение JsonNullableMapper
        uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class PostMapperPatch {
    public abstract void update(PostPatchDTO dto, @MappingTarget Post model);
}
