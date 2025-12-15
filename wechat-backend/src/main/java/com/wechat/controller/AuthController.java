package com.wechat.controller;

import com.wechat.config.JwtTokenUtil;
import com.wechat.entity.User;
import com.wechat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证与用户登录相关的 REST 控制器。
 * <p>
 * 提供接口：
 * - POST `/auth/register`：注册用户；
 * - POST `/auth/login`：登录并返回 JWT；
 * - GET `/auth/me`：获取当前登录用户信息。
 * </p>
 * 实现要点：
 * - 使用 {@link org.springframework.security.authentication.AuthenticationManager} 与自定义的
 *   {@link org.springframework.security.core.userdetails.UserDetailsService} 完成认证；
 * - JWT 的生成与校验由 {@link com.wechat.config.JwtTokenUtil} 处理；
 * - 控制器保持无状态，认证信息通过请求头中的 Bearer Token 传递。
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            User user = userService.login(username, password);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String token = jwtTokenUtil.generateToken(userDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("user", user);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userService.findUserByUsername(username)
                    .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
