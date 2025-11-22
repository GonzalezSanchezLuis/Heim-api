package com.heim.api.admin.application.mapper;

import com.heim.api.admin.application.dto.UserResponse;
import com.heim.api.admin.application.dto.UserUpdateRequest;
import com.heim.api.users.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsersMapper {
    UserResponse toResponse(User user);
    User toEntity(UserUpdateRequest userUpdateRequest);
}
