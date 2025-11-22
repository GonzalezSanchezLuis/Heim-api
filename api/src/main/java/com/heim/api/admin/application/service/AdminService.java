package com.heim.api.admin.application.service;

import com.heim.api.admin.application.dto.AdminRequest;
import com.heim.api.admin.application.dto.AdminResponse;
import com.heim.api.admin.application.mapper.AdminMapper;
import com.heim.api.admin.domain.entity.Admin;
import com.heim.api.admin.infraestructure.repository.AdminRepository;
import com.heim.api.exceptions.DatabaseUnavailableException;
import com.heim.api.exceptions.IncorrectCredentialsException;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.exceptions.EmailAlreadyRegisteredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public  AdminService(AdminRepository adminRepository,
                         BCryptPasswordEncoder bcryptPasswordEncoder,
                         AdminMapper adminMapper,
                         PasswordEncoder passwordEncoder
        ){
        this.adminRepository = adminRepository;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminResponse registerAdmin(AdminRequest adminRequest) throws  Exception{
        Optional<Admin> adminOptional = adminRepository.findByEmail(adminRequest.getEmail());
        if (adminOptional.isPresent()){
            throw new EmailAlreadyRegisteredException("Ya existe un usuario registrado con ese email.");
        }

        String encodedPassword = bcryptPasswordEncoder.encode(adminRequest.getPassword());
        adminRequest.setPassword(encodedPassword);
        Admin newAdmin = adminMapper.toEntity(adminRequest);
        newAdmin.setRole("ADMIN");
        newAdmin.setActive(true);

        return adminMapper.toResponse(adminRepository.save(newAdmin));

    }

    public AdminResponse authenticate(String email, String password){

        try {
            Optional<Admin> adminOptional = adminRepository.findByEmail(email);

            if (adminOptional.isEmpty()) {
                throw new NoSuchElementException("emailNotFound");
            }

            Admin admin = adminOptional.get();

            if (!passwordEncoder.matches(password, admin.getPassword())) {
                throw new IncorrectCredentialsException("passwordIncorrect");
            }

            return adminMapper.toResponse(admin);

        } catch (DataAccessException e) {
            throw new DatabaseUnavailableException("La base de datos no está disponible.", e);
        }

    }
}
