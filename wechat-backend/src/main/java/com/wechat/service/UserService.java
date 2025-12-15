package com.wechat.service;

import com.wechat.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(User user);

    User login(String username, String password);

    Optional<User> findUserById(Long id);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByPhone(String phone);

    User updateUser(User user);

    void deleteUser(Long id);

    List<User> searchUsers(String keyword);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    User updateLastLogin(Long userId);

    User changePassword(Long userId, String oldPassword, String newPassword);
}
