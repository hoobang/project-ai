package com.wechat.service.impl;

import com.wechat.config.CustomUserDetails;
import com.wechat.entity.User;
import com.wechat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmailOrPhone) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsernameOrEmailOrPhone(usernameOrEmailOrPhone, usernameOrEmailOrPhone, usernameOrEmailOrPhone);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new CustomUserDetails(
                    user,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }
        throw new UsernameNotFoundException("User not found with username/email/phone: " + usernameOrEmailOrPhone);
    }
}
