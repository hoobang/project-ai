package com.wechat.service.impl;

import com.wechat.entity.User;
import com.wechat.repository.UserRepository;
import com.wechat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User login(String usernameOrEmailOrPhone, String password) {
        Optional<User> userOptional = userRepository.findByUsernameOrEmailOrPhone(usernameOrEmailOrPhone, usernameOrEmailOrPhone, usernameOrEmailOrPhone);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return updateLastLogin(user.getId());
            }
        }
        throw new IllegalArgumentException("Invalid username/email/phone or password");
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        
        if (!existingUser.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (!existingUser.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists");
        }
        
        existingUser.setNickname(user.getNickname());
        existingUser.setAvatar(user.getAvatar());
        existingUser.setSignature(user.getSignature());
        existingUser.setGender(user.getGender());
        existingUser.setLocation(user.getLocation());
        existingUser.setBirthDate(user.getBirthDate());
        existingUser.setActive(user.getActive());
        
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public User updateLastLogin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        user.setLastLogin(new Date());
        return userRepository.save(user);
    }

    @Override
    public User changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
