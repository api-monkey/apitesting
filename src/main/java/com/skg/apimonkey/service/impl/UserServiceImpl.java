package com.skg.apimonkey.service.impl;

import com.skg.apimonkey.config.SecurityConfig;
import com.skg.apimonkey.domain.user.User;
import com.skg.apimonkey.domain.user.UserSignUp;
import com.skg.apimonkey.domain.user.UserType;
import com.skg.apimonkey.domain.user.auth2.AuthProvider;
import com.skg.apimonkey.domain.user.auth2.UserPrincipal;
import com.skg.apimonkey.exception.ResourceNotFoundException;
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
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecurityConfig securityConfig;

    @Override
    @Transactional(readOnly = false)
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        Optional<User> user = userRepository.getUserByLoginAndProvider(login, AuthProvider.local);
        UserDetails userDetails = null;
        if(user.isPresent()) {
            GrantedAuthority authority = new SimpleGrantedAuthority(user.get().getType().name());
            userDetails = (UserDetails)new org.springframework.security.core.userdetails.User(user.get().getLogin(), user.get().getPassword(), Arrays.asList(authority));
        }
        return userDetails;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByLogin(String login) {
        return userRepository.getUserByLogin(login);
    }

    @Transactional
    public UserDetails loadUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
        return UserPrincipal.create(user);
    }

    @Override
    @Transactional
    public User registerNewUserAccount(UserSignUp userDto) throws UserAlreadyExistException {

        Optional<User> userOpt = userRepository.getUserByLogin(userDto.getEmail());

        if (userOpt.isPresent() && Objects.equals(AuthProvider.local, userOpt.get().getProvider())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + userDto.getEmail());
        }

        User user = userOpt.orElseGet(User::new);
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setLogin(userDto.getEmail());
        user.setPassword(securityConfig.passwordEncoder().encode(userDto.getPassword()));
        user.setType(UserType.ROLE_USER);
        user.setProvider(AuthProvider.local);
        user.setEmailVerified(userOpt.isPresent());
        user.setCreated(userOpt.isPresent() ? userOpt.get().getCreated() : new Date());
        return userRepository.save(user);
    }

    private boolean loginExist(String email) {
        return userRepository.getUserByLogin(email).isPresent();
    }
}