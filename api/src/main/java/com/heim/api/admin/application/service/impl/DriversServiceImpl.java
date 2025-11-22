package com.heim.api.admin.application.service.impl;

import com.heim.api.admin.application.dto.DriverUpdateRequestDTO;
import com.heim.api.admin.application.dto.UserResponse;
import com.heim.api.admin.application.dto.UserUpdateRequest;
import com.heim.api.admin.application.mapper.UsersMapper;
import com.heim.api.admin.application.service.UsersService;
import com.heim.api.admin.infraestructure.repository.UsersRepository;
import com.heim.api.drivers.domain.entity.Driver;
import com.heim.api.drivers.infraestructure.repository.DriverRepository;
import com.heim.api.users.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
public class DriversServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final DriverRepository  driversRepository;

    public DriversServiceImpl(UsersRepository usersRepository,
                              UsersMapper usersMapper,
                               DriverRepository driversRepository){
        this.usersRepository = usersRepository;
        this.usersMapper = usersMapper;
        this.driversRepository = driversRepository;

    }

    @Override
    public Page<User> getUsers(Pageable pageable) {

        final String DRIVER_ROL = "DRIVER";
        return usersRepository.findByRol(DRIVER_ROL,pageable);
    }

    @Override
    public UserResponse findUserById(Long id) throws NoSuchElementException {
        User user = usersRepository.findUserWithDriverById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return usersMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUserData(Long id, UserUpdateRequest request) {
        User userToUpdate = usersRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        userToUpdate.setFullName(request.getFullName());
        userToUpdate.setDocument(request.getDocument());
        userToUpdate.setPhone(request.getPhone());
        userToUpdate.setEmail(request.getEmail());
        userToUpdate.setActive(request.isActive());

       // usersRepository.save(userToUpdate);
        System.out.println("DATOS DEL CONDUCTOR USUARIO" + userToUpdate);


        Driver driverToUpdate = userToUpdate.getDriver();
        System.out.println("DATOS DEL VEHICULO A ACTUALIZAR" + driverToUpdate);

        if (driverToUpdate != null && request.getDriver() != null) {
            DriverUpdateRequestDTO driverDto = request.getDriver();
            driverToUpdate.setLicenseNumber(driverDto.getLicenseNumber());
            driverToUpdate.setVehicleType(driverDto.getVehicleType());
            driverToUpdate.setEnrollVehicle(driverDto.getEnrollVehicle());

            System.out.println("DATOS DEL VEHICULO" + driverDto);
            driversRepository.save(driverToUpdate);

        }

        User updatedUser = usersRepository.save(userToUpdate);
        return usersMapper.toResponse(updatedUser);
    }

}
