package com.skg.apimonkey.service;

import com.skg.apimonkey.domain.user.User;
import com.skg.apimonkey.domain.user.UserSignUp;
import com.skg.apimonkey.exception.UserAlreadyExistException;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {
    User getUserByLogin(String login);

    User registerNewUserAccount(UserSignUp userSignUp) throws UserAlreadyExistException;
}