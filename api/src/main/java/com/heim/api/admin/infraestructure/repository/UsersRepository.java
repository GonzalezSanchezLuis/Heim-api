package com.heim.api.admin.infraestructure.repository;

import com.heim.api.users.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsersRepository extends CrudRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.role = :role")
    Page<User> findByRol(String role, Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.driver d WHERE u.userId = :id")
    Optional<User> findUserWithDriverById(@Param("id")Long id);
}
