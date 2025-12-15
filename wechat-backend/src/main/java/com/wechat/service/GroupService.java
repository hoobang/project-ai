package com.wechat.service;

import com.wechat.entity.Group;
import com.wechat.entity.GroupMember;
import com.wechat.entity.User;
import java.util.List;

public interface GroupService {

    Group createGroup(Group group, Long creatorId);

    Group updateGroup(Group group);

    void deleteGroup(Long groupId);

    Group getGroupById(Long groupId);

    List<Group> searchGroups(String keyword);

    List<Group> getUserGroups(Long userId);

    GroupMember joinGroup(Long groupId, Long userId, String inviteCode);

    void leaveGroup(Long groupId, Long userId);

    GroupMember updateGroupMember(Long groupId, Long userId, String groupNickname);

    void removeGroupMember(Long groupId, Long adminId, Long memberId);

    void changeMemberRole(Long groupId, Long adminId, Long memberId, String role);

    void muteMember(Long groupId, Long adminId, Long memberId, Long muteDuration);

    void unmuteMember(Long groupId, Long adminId, Long memberId);

    List<User> getGroupMembers(Long groupId);

    boolean isGroupMember(Long groupId, Long userId);

    String generateInviteCode(Long groupId);

    GroupMember getGroupMember(Long groupId, Long userId);

    // 发送群聊消息
    com.wechat.entity.GroupMessage sendGroupMessage(com.wechat.entity.GroupMessage groupMessage);
}
