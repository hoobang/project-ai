package com.wechat.service;

import com.wechat.entity.Friendship;
import com.wechat.entity.User;
import java.util.List;

/**
 * 好友关系领域服务接口。
 * <p>
 * 职责：
 * - 发起/接受/拒绝好友请求与删除好友；
 * - 查询好友列表、待处理与已发送请求；
 * - 判断双方是否为好友与统计数量。
 * </p>
 */
public interface FriendshipService {

    /**
     * 发送好友请求。
     * @param userId 发起请求的用户ID
     * @param friendId 目标用户ID
     * @return 新建的好友关系（PENDING）
     * @throws IllegalArgumentException 自己给自己发或重复关系
     * @throws NoSuchElementException 当用户不存在时
     */
    Friendship sendFriendRequest(Long userId, Long friendId);

    /**
     * 接受好友请求。
     * @param userId 当前用户ID
     * @param friendId 请求发起方ID
     * @return 更新后的好友关系（ACCEPTED）
     * @throws NoSuchElementException 请求不存在或用户不存在
     * @throws IllegalArgumentException 状态不为 PENDING
     */
    Friendship acceptFriendRequest(Long userId, Long friendId);

    /**
     * 拒绝好友请求。
     * @param userId 当前用户ID
     * @param friendId 请求发起方ID
     * @return 更新后的好友关系（REJECTED）
     * @throws NoSuchElementException 请求不存在或用户不存在
     * @throws IllegalArgumentException 状态不为 PENDING
     */
    Friendship rejectFriendRequest(Long userId, Long friendId);

    /**
     * 删除好友关系。
     * @param userId 当前用户ID
     * @param friendId 对方用户ID
     */
    void deleteFriend(Long userId, Long friendId);

    /**
     * 获取好友列表。
     * @param userId 用户ID
     * @return 好友用户列表
     */
    List<User> getFriends(Long userId);

    /**
     * 获取待处理的好友请求（别人发给我）。
     * @param userId 用户ID
     * @return 申请者用户列表
     */
    List<User> getPendingFriendRequests(Long userId);

    /**
     * 获取我发出的待处理请求。
     * @param userId 用户ID
     * @return 目标用户列表
     */
    List<User> getSentFriendRequests(Long userId);

    /**
     * 判断双方是否已是好友。
     * @param userId 用户ID
     * @param friendId 朋友ID
     * @return 是否为好友
     */
    boolean isFriend(Long userId, Long friendId);

    /**
     * 获取双方之间的好友关系。
     * @param userId 用户ID
     * @param friendId 朋友ID
     * @return 好友关系
     * @throws NoSuchElementException 当不存在关系时
     */
    Friendship getFriendship(Long userId, Long friendId);

    /**
     * 统计好友数量。
     * @param userId 用户ID
     * @return 数量
     */
    long countFriends(Long userId);

    /**
     * 统计待处理请求数量（别人发给我的）。
     * @param userId 用户ID
     * @return 数量
     */
    long countPendingRequests(Long userId);
}
