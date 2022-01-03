package com.skg.apimonkey.repository;

import com.skg.apimonkey.domain.user.User;
import com.skg.apimonkey.domain.user.auth2.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> getUserByLogin(String login);

    Optional<User> getUserByLoginAndProvider(String login, AuthProvider provider);
}