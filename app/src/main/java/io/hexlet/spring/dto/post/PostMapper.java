package io.hexlet.spring.dto.post;

import io.hexlet.spring.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class PostMapper {
    public abstract Post map(PostCreateDTO dto);
    public abstract void update(PostUpdateDTO dto, @MappingTarget Post model);
    public abstract PostDTO map(Post model);
}
