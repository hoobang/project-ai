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

/**
 * Spring Security 的用户详情服务实现。
 * <p>
 * 支持通过用户名、邮箱或手机号登录：
 * - 查询用户后，包装为 {@link com.wechat.config.CustomUserDetails}；
 * - 默认授予 `ROLE_USER` 角色。
 * </p>
 * 异常：
 * - 用户不存在时抛出 {@link org.springframework.security.core.userdetails.UsernameNotFoundException}。
 */
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
