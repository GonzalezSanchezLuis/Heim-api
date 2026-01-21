package com.heim.api.users.application.mapper;


import com.heim.api.users.application.dto.UserRequest;
import com.heim.api.users.application.dto.UserResponse;
import com.heim.api.users.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "active", source = "active")
    UserResponse toResponse(User user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "active", source = "active")
    User toEntity(UserRequest userRequest);
}

