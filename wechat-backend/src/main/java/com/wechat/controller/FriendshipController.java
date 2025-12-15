package com.wechat.controller;

import com.wechat.config.CustomUserDetails;
import com.wechat.entity.Friendship;
import com.wechat.entity.User;
import com.wechat.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/list")
    public ResponseEntity<List<User>> getFriends(@AuthenticationPrincipal CustomUserDetails currentUser) {
        Long userId = currentUser.getUser().getId();
        List<User> friends = friendshipService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @PostMapping("/request")
    public ResponseEntity<Friendship> sendFriendRequest(@RequestBody FriendRequest request, @AuthenticationPrincipal CustomUserDetails currentUser) {
        Long userId = currentUser.getUser().getId();
        Friendship friendship = friendshipService.sendFriendRequest(userId, request.getFriendId());
        return ResponseEntity.ok(friendship);
    }

    @PostMapping("/accept")
    public ResponseEntity<Friendship> acceptFriendRequest(@RequestBody FriendRequest request, @AuthenticationPrincipal CustomUserDetails currentUser) {
        Long userId = currentUser.getUser().getId();
        Friendship friendship = friendshipService.acceptFriendRequest(userId, request.getFriendId());
        return ResponseEntity.ok(friendship);
    }

    @PostMapping("/reject")
    public ResponseEntity<Friendship> rejectFriendRequest(@RequestBody FriendRequest request, @AuthenticationPrincipal CustomUserDetails currentUser) {
        Long userId = currentUser.getUser().getId();
        Friendship friendship = friendshipService.rejectFriendRequest(userId, request.getFriendId());
        return ResponseEntity.ok(friendship);
    }

    @DeleteMapping("/delete/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long friendId, @AuthenticationPrincipal CustomUserDetails currentUser) {
        Long userId = currentUser.getUser().getId();
        friendshipService.deleteFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<List<User>> getPendingFriendRequests(@AuthenticationPrincipal CustomUserDetails currentUser) {
        Long userId = currentUser.getUser().getId();
        List<User> pendingRequests = friendshipService.getPendingFriendRequests(userId);
        return ResponseEntity.ok(pendingRequests);
    }

    // 内部类用于接收请求参数
    static class FriendRequest {
        private Long friendId;

        public Long getFriendId() {
            return friendId;
        }

        public void setFriendId(Long friendId) {
            this.friendId = friendId;
        }
    }
}
