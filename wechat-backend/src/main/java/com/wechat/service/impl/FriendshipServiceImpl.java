package com.wechat.service.impl;

import com.wechat.entity.Friendship;
import com.wechat.entity.User;
import com.wechat.repository.FriendshipRepository;
import com.wechat.repository.UserRepository;
import com.wechat.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Friendship sendFriendRequest(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Friend not found"));

        Optional<Friendship> existingFriendship = friendshipRepository.findFriendshipBetweenUsers(user, friend);
        if (existingFriendship.isPresent()) {
            throw new IllegalArgumentException("Friendship already exists");
        }

        Friendship friendship = new Friendship();
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setStatus("PENDING");

        return friendshipRepository.save(friendship);
    }

    @Override
    public Friendship acceptFriendRequest(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Friend not found"));

        Friendship friendship = friendshipRepository.findByUserAndFriend(friend, user)
                .orElseThrow(() -> new NoSuchElementException("Friend request not found"));

        if (!"PENDING".equals(friendship.getStatus())) {
            throw new IllegalArgumentException("Friend request is not in pending state");
        }

        friendship.setStatus("ACCEPTED");
        return friendshipRepository.save(friendship);
    }

    @Override
    public Friendship rejectFriendRequest(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Friend not found"));

        Friendship friendship = friendshipRepository.findByUserAndFriend(friend, user)
                .orElseThrow(() -> new NoSuchElementException("Friend request not found"));

        if (!"PENDING".equals(friendship.getStatus())) {
            throw new IllegalArgumentException("Friend request is not in pending state");
        }

        friendship.setStatus("REJECTED");
        return friendshipRepository.save(friendship);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Friend not found"));

        Friendship friendship = friendshipRepository.findFriendshipBetweenUsers(user, friend)
                .orElseThrow(() -> new NoSuchElementException("Friendship not found"));

        friendshipRepository.delete(friendship);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<User> friendsFromUser = friendshipRepository.findAcceptedFriendsByUser(user);
        List<User> friendsFromFriend = friendshipRepository.findAcceptedFriendsByFriend(user);

        friendsFromUser.addAll(friendsFromFriend);
        if (friendsFromUser.isEmpty()) {
            return userRepository.findAll().stream()
                    .filter(u -> !u.getId().equals(userId))
                    .collect(Collectors.toList());
        }
        return friendsFromUser;
    }

    @Override
    public List<User> getPendingFriendRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<Friendship> pendingRequests = friendshipRepository.findByFriendAndStatus(user, "PENDING");
        return pendingRequests.stream()
                .map(Friendship::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getSentFriendRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<Friendship> sentRequests = friendshipRepository.findByUserAndStatus(user, "PENDING");
        return sentRequests.stream()
                .map(Friendship::getFriend)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Friend not found"));

        Optional<Friendship> friendship = friendshipRepository.findFriendshipBetweenUsers(user, friend);
        return friendship.isPresent() && "ACCEPTED".equals(friendship.get().getStatus());
    }

    @Override
    public Friendship getFriendship(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Friend not found"));

        return friendshipRepository.findFriendshipBetweenUsers(user, friend)
                .orElseThrow(() -> new NoSuchElementException("Friendship not found"));
    }

    @Override
    public long countFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        long friendsFromUser = friendshipRepository.countByUserAndStatus(user, "ACCEPTED");
        long friendsFromFriend = friendshipRepository.countByFriendAndStatus(user, "ACCEPTED");

        return friendsFromUser + friendsFromFriend;
    }

    @Override
    public long countPendingRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return friendshipRepository.countByFriendAndStatus(user, "PENDING");
    }
}
