package com.wechat.service;

import com.wechat.entity.Friendship;
import com.wechat.entity.User;
import java.util.List;

public interface FriendshipService {

    Friendship sendFriendRequest(Long userId, Long friendId);

    Friendship acceptFriendRequest(Long userId, Long friendId);

    Friendship rejectFriendRequest(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> getPendingFriendRequests(Long userId);

    List<User> getSentFriendRequests(Long userId);

    boolean isFriend(Long userId, Long friendId);

    Friendship getFriendship(Long userId, Long friendId);

    long countFriends(Long userId);

    long countPendingRequests(Long userId);
}
