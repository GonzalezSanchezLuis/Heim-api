package com.heim.api.users.application.service;

import com.heim.api.fcm.application.service.FcmTokenService;
import com.heim.api.users.application.dto.UserRequest;
import com.heim.api.users.application.dto.UserResponse;
import com.heim.api.users.application.mapper.UserMapper;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.exceptions.EmailAlreadyRegisteredException;
import com.heim.api.users.infraestructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;
    private  final FcmTokenService fcmTokenService;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper,
                       BCryptPasswordEncoder bcryptPasswordEncoder,
                       FcmTokenService fcmTokenService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
        this.fcmTokenService = fcmTokenService;
    }


    public UserResponse registerUser(UserRequest userRequest) throws Exception {
        Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyRegisteredException("Ya existe un usuario registrado con ese email.");
        } else {
            //System.out.println(userRequest);
            String encodePassword = bcryptPasswordEncoder.encode(userRequest.getPassword());
            userRequest.setPassword(encodePassword);
            User newUser = userMapper.toEntity(userRequest);
            newUser.setActive(true);
            newUser.setRole("USER");

            //System.out.println("Mapped User: " + newUser);

            return  userMapper.toResponse(userRepository.save(newUser));
        }
    }

    public UserResponse getUserById(Long userId) throws NoSuchElementException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return userMapper.toResponse(user);
    }



    public UserResponse updateUserData(Long userId, UserRequest userRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        if (userRequest.getPassword() != null &&  !userRequest.getPassword().isEmpty()){
            userRequest.setPassword(bcryptPasswordEncoder.encode(userRequest.getPassword()));
        }

        user.setFullName(userRequest.getFullName());
        user.setDocument(userRequest.getDocument());
        user.setPhone(userRequest.getPhone());
        user.setEmail(userRequest.getEmail());
        user.setUrlAvatarProfile(userRequest.getUrlAvatarProfile());
        userRepository.save(user);
        return userMapper.toResponse(user);
    }


    public void userDelete(Long userId){
        User userToDelete = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        userRepository.delete(userToDelete);
    }

}
