package com.noble.tardis.security.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.noble.tardis.security.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);

    UserDetails findDetailsByEmail(String email);

    User findByUsername(String username);
}
