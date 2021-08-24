package com.skg.apimonkey.service;

import com.skg.apimonkey.domain.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {
    User getUserByLogin(String login);
}