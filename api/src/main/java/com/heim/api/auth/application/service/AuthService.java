package com.heim.api.auth.application.service;

import com.heim.api.exceptions.DatabaseUnavailableException;
import com.heim.api.exceptions.IncorrectCredentialsException;
import com.heim.api.users.application.dto.UserResponse;
import com.heim.api.users.application.mapper.UserMapper;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserResponse authenticate(String email, String password) {

        try {
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                throw new NoSuchElementException("emailNotFound");
            }

            User user = userOptional.get();

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new IncorrectCredentialsException("passwordIncorrect");
            }

            return userMapper.toResponse(user);

        } catch (DataAccessException e) {
            throw new DatabaseUnavailableException("La base de datos no está disponible.", e);
        }
    }
}

