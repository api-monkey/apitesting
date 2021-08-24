package com.skg.apimonkey.service.impl;

import com.skg.apimonkey.domain.user.User;
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

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

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
}