package com.skg.apimonkey.service;

import com.skg.apimonkey.domain.user.User;
import com.skg.apimonkey.domain.user.UserSignUp;
import com.skg.apimonkey.exception.UserAlreadyExistException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;


public interface UserService extends UserDetailsService {
    Optional<User> getUserByLogin(String login);

    User registerNewUserAccount(UserSignUp userSignUp) throws UserAlreadyExistException;

    UserDetails loadUserById(Integer id);

    User save(User user);
}