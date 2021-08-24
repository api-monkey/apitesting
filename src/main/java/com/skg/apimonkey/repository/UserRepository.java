package com.skg.apimonkey.repository;

import com.skg.apimonkey.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserByLogin(String login);
}