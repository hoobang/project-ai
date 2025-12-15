package com.wechat.repository;

import com.wechat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口。
 * <p>
 * 提供按用户名、邮箱、手机号的查询与存在性判断；
 * 支持基于关键字的模糊搜索（用户名/昵称/邮箱/手机号）。
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByUsernameOrEmailOrPhone(String username, String email, String phone);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.nickname LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phone LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
