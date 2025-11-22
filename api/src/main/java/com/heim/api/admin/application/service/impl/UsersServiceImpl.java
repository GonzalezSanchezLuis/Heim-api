package com.heim.api.admin.application.service.impl;

import com.heim.api.admin.application.dto.UserResponse;
import com.heim.api.admin.application.dto.UserUpdateRequest;
import com.heim.api.admin.application.mapper.UsersMapper;
import com.heim.api.admin.application.service.UsersService;
import com.heim.api.admin.infraestructure.repository.UsersRepository;
import com.heim.api.users.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;


@Service
public class UsersServiceImpl  implements UsersService {
    private final UsersRepository usersRepository;
    private final UsersMapper userMapper;

    public UsersServiceImpl(UsersRepository usersRepository,
                            UsersMapper userMapper){
        this.usersRepository = usersRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Page<User> getUsers(Pageable pageable){
        final String USER_ROL = "USER";
        return usersRepository.findByRol(USER_ROL,pageable);
    }

    @Override
    public UserResponse  findUserById(Long id) throws NoSuchElementException  {
       User user = usersRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
       return userMapper.toResponse(user);

    }

    @Override
    public UserResponse updateUserData(Long id, UserUpdateRequest request) {
        User user = usersRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        user.setFullName(request.getFullName());
        user.setDocument(request.getDocument());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
       user.setActive(request.isActive());
        usersRepository.save(user);
        return userMapper.toResponse(user);
    }

}
