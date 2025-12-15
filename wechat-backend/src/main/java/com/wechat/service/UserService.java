package com.wechat.service;

import com.wechat.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * 用户领域服务接口。
 * <p>
 * 职责：
 * - 用户注册、登录、信息维护与删除；
 * - 基于用户名/邮箱/手机号的检索与存在性校验；
 * - 审计信息更新（如最后登录时间）与密码变更。
 * </p>
 */
public interface UserService {

    /**
     * 注册新用户。
     * @param user 待注册的用户信息（包含唯一性字段与密码）
     * @return 持久化后的用户
     * @throws IllegalArgumentException 当用户名/邮箱/手机号重复时
     */
    User registerUser(User user);

    /**
     * 用户登录（支持用户名/邮箱/手机号）。
     * @param username 用户名或邮箱或手机号
     * @param password 明文密码
     * @return 登录成功的用户，并更新最后登录时间
     * @throws IllegalArgumentException 当凭证不正确时
     */
    User login(String username, String password);

    /**
     * 通过主键查询用户。
     * @param id 用户ID
     * @return 可选的用户
     */
    Optional<User> findUserById(Long id);

    /**
     * 通过用户名查询用户。
     * @param username 用户名
     * @return 可选的用户
     */
    Optional<User> findUserByUsername(String username);

    /**
     * 通过邮箱查询用户。
     * @param email 邮箱
     * @return 可选的用户
     */
    Optional<User> findUserByEmail(String email);

    /**
     * 通过手机号查询用户。
     * @param phone 手机号
     * @return 可选的用户
     */
    Optional<User> findUserByPhone(String phone);

    /**
     * 更新用户资料（不包含密码）。
     * @param user 新的资料
     * @return 更新后的用户
     * @throws NoSuchElementException 当用户不存在时
     * @throws IllegalArgumentException 当邮箱或手机号与其他用户冲突时
     */
    User updateUser(User user);

    /**
     * 删除用户。
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 关键字搜索用户（用户名/昵称/邮箱/手机号）。
     * @param keyword 关键字
     * @return 匹配的用户列表
     */
    List<User> searchUsers(String keyword);

    /**
     * 判断用户名是否存在。
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 判断邮箱是否存在。
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 判断手机号是否存在。
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 更新用户的最后登录时间为当前时间。
     * @param userId 用户ID
     * @return 更新后的用户
     * @throws NoSuchElementException 当用户不存在时
     */
    User updateLastLogin(Long userId);

    /**
     * 修改用户密码。
     * @param userId 用户ID
     * @param oldPassword 旧密码（明文）
     * @param newPassword 新密码（明文）
     * @return 更新后的用户
     * @throws NoSuchElementException 当用户不存在时
     * @throws IllegalArgumentException 当旧密码不匹配时
     */
    User changePassword(Long userId, String oldPassword, String newPassword);
}
