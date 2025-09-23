package com.innowise.repository;

import com.innowise.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id  IN :ids")
    List<User> findByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM users WHERE surname = :surname", nativeQuery = true)
    List<User> findDySurnameNative(@Param("surname") String surname);
}
