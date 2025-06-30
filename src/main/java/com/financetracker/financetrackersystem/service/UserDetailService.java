package com.financetracker.financetrackersystem.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.financetracker.financetrackersystem.entity.UserEntity;
import com.financetracker.financetrackersystem.repository.UserRepository;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userData = userRepository.findByUsername(username).orElse(null);
        if (userData == null) {
            throw new UsernameNotFoundException("User not found");
        }
        UserDetails user = User.builder()
            .username(userData.getUsername())
            .password(userData.getPassword())
            .build();
        return user;
    }

}
