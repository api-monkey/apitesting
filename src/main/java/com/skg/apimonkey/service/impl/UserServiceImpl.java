package com.skg.apimonkey.service.impl;

import com.skg.apimonkey.config.SecurityConfig;
import com.skg.apimonkey.domain.user.User;
import com.skg.apimonkey.domain.user.UserSignUp;
import com.skg.apimonkey.domain.user.UserType;
import com.skg.apimonkey.exception.UserAlreadyExistException;
import com.skg.apimonkey.repository.UserRepository;
import com.skg.apimonkey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecurityConfig securityConfig;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        User user = userRepository.getUserByLogin(login);
        UserDetails userDetails = null;
        if(user != null) {
            GrantedAuthority authority = new SimpleGrantedAuthority(user.getType().name());
            userDetails = (UserDetails)new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), Arrays.asList(authority));
        }
        return userDetails;
    }
    @Override
    @Transactional(readOnly = true)
    public User getUserByLogin(String login) {
        return userRepository.getUserByLogin(login);
    }

    @Override
    @Transactional
    public User registerNewUserAccount(UserSignUp userDto) throws UserAlreadyExistException {

        if (loginExist(userDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + userDto.getEmail());
        }

        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .login(userDto.getEmail())
                .password(securityConfig.getEncoder().encode(userDto.getPassword()))
                .type(UserType.ROLE_USER)
                .created(new Date())
                .build();
        return userRepository.save(user);
    }

    private boolean loginExist(String email) {
        return userRepository.getUserByLogin(email) != null;
    }
}