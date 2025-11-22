package com.heim.api.admin.application.service;

import com.heim.api.admin.application.dto.UserResponse;
import com.heim.api.admin.application.dto.UserUpdateRequest;
import com.heim.api.users.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsersService {
    Page<User> getUsers(Pageable pageable);
    UserResponse findUserById(Long id);
    UserResponse updateUserData(Long id, UserUpdateRequest request);
}
