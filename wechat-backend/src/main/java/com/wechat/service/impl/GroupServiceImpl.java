package com.wechat.service.impl;

import com.wechat.entity.Group;
import com.wechat.entity.GroupMember;
import com.wechat.entity.User;
import com.wechat.repository.GroupMemberRepository;
import com.wechat.repository.GroupMessageRepository;
import com.wechat.repository.GroupRepository;
import com.wechat.repository.UserRepository;
import com.wechat.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private UserRepository userRepository;

    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Random RANDOM = new Random();

    @Override
    public Group createGroup(Group group, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new NoSuchElementException("Creator not found"));

        group.setCreator(creator);
        group.setMemberCount(1);
        group.setInviteCode(generateRandomCode());

        Group savedGroup = groupRepository.save(group);

        // Add creator as group member with OWNER role
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(savedGroup);
        groupMember.setUser(creator);
        groupMember.setRole("OWNER");
        groupMemberRepository.save(groupMember);

        return savedGroup;
    }

    @Override
    public Group updateGroup(Group group) {
        Group existingGroup = groupRepository.findById(group.getId())
                .orElseThrow(() -> new NoSuchElementException("Group not found"));

        existingGroup.setGroupName(group.getGroupName());
        existingGroup.setGroupAvatar(group.getGroupAvatar());
        existingGroup.setGroupDescription(group.getGroupDescription());
        existingGroup.setIsPublic(group.getIsPublic());

        return groupRepository.save(existingGroup);
    }

    @Override
    public void deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));

        groupRepository.delete(group);
    }

    @Override
    public Group getGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
    }

    @Override
    public List<Group> searchGroups(String keyword) {
        return groupRepository.findByGroupNameContaining(keyword);
    }

    @Override
    public List<Group> getUserGroups(Long userId) {
        return groupRepository.findGroupsByUserId(userId);
    }

    @Override
    public GroupMember joinGroup(Long groupId, Long userId, String inviteCode) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("User is already a member of this group");
        }

        if (!group.getIsPublic() && !group.getInviteCode().equals(inviteCode)) {
            throw new IllegalArgumentException("Invalid invite code");
        }

        if (group.getMemberCount() >= group.getMaxMembers()) {
            throw new IllegalArgumentException("Group is full");
        }

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMember.setRole("MEMBER");

        GroupMember savedMember = groupMemberRepository.save(groupMember);

        // Update group member count
        group.setMemberCount(group.getMemberCount() + 1);
        groupRepository.save(group);

        return savedMember;
    }

    @Override
    public void leaveGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        GroupMember groupMember = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new NoSuchElementException("User is not a member of this group"));

        // If user is owner, cannot leave group (must transfer ownership first)
        if ("OWNER".equals(groupMember.getRole())) {
            throw new IllegalArgumentException("Group owner cannot leave the group. Transfer ownership first.");
        }

        groupMemberRepository.delete(groupMember);

        // Update group member count
        group.setMemberCount(group.getMemberCount() - 1);
        groupRepository.save(group);
    }

    @Override
    public GroupMember updateGroupMember(Long groupId, Long userId, String groupNickname) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        GroupMember groupMember = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new NoSuchElementException("User is not a member of this group"));

        groupMember.setGroupNickname(groupNickname);
        return groupMemberRepository.save(groupMember);
    }

    @Override
    public void removeGroupMember(Long groupId, Long adminId, Long memberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NoSuchElementException("Admin not found"));
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        GroupMember adminMember = groupMemberRepository.findByGroupAndUser(group, admin)
                .orElseThrow(() -> new NoSuchElementException("Admin is not a member of this group"));

        GroupMember targetMember = groupMemberRepository.findByGroupAndUser(group, member)
                .orElseThrow(() -> new NoSuchElementException("Member is not a member of this group"));

        // Check if admin has permission to remove member
        if (!"OWNER".equals(adminMember.getRole()) && !"ADMIN".equals(adminMember.getRole())) {
            throw new IllegalArgumentException("Only group owner or admin can remove members");
        }

        // Cannot remove group owner
        if ("OWNER".equals(targetMember.getRole())) {
            throw new IllegalArgumentException("Cannot remove group owner");
        }

        // Cannot remove admin unless you are the owner
        if ("ADMIN".equals(targetMember.getRole()) && !"OWNER".equals(adminMember.getRole())) {
            throw new IllegalArgumentException("Only group owner can remove admins");
        }

        groupMemberRepository.delete(targetMember);

        // Update group member count
        group.setMemberCount(group.getMemberCount() - 1);
        groupRepository.save(group);
    }

    @Override
    public void changeMemberRole(Long groupId, Long adminId, Long memberId, String role) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NoSuchElementException("Admin not found"));
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        GroupMember adminMember = groupMemberRepository.findByGroupAndUser(group, admin)
                .orElseThrow(() -> new NoSuchElementException("Admin is not a member of this group"));

        GroupMember targetMember = groupMemberRepository.findByGroupAndUser(group, member)
                .orElseThrow(() -> new NoSuchElementException("Member is not a member of this group"));

        // Only group owner can change roles
        if (!"OWNER".equals(adminMember.getRole())) {
            throw new IllegalArgumentException("Only group owner can change member roles");
        }

        // Cannot change owner role
        if ("OWNER".equals(targetMember.getRole())) {
            throw new IllegalArgumentException("Cannot change owner role");
        }

        targetMember.setRole(role);
        groupMemberRepository.save(targetMember);
    }

    @Override
    public void muteMember(Long groupId, Long adminId, Long memberId, Long muteDuration) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NoSuchElementException("Admin not found"));
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        GroupMember adminMember = groupMemberRepository.findByGroupAndUser(group, admin)
                .orElseThrow(() -> new NoSuchElementException("Admin is not a member of this group"));

        GroupMember targetMember = groupMemberRepository.findByGroupAndUser(group, member)
                .orElseThrow(() -> new NoSuchElementException("Member is not a member of this group"));

        // Check if admin has permission to mute member
        if (!"OWNER".equals(adminMember.getRole()) && !"ADMIN".equals(adminMember.getRole())) {
            throw new IllegalArgumentException("Only group owner or admin can mute members");
        }

        // Cannot mute group owner
        if ("OWNER".equals(targetMember.getRole())) {
            throw new IllegalArgumentException("Cannot mute group owner");
        }

        // Cannot mute admin unless you are the owner
        if ("ADMIN".equals(targetMember.getRole()) && !"OWNER".equals(adminMember.getRole())) {
            throw new IllegalArgumentException("Only group owner can mute admins");
        }

        targetMember.setIsMuted(true);
        targetMember.setMuteEndTime(new Date(System.currentTimeMillis() + muteDuration));
        groupMemberRepository.save(targetMember);
    }

    @Override
    public void unmuteMember(Long groupId, Long adminId, Long memberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NoSuchElementException("Admin not found"));
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        GroupMember adminMember = groupMemberRepository.findByGroupAndUser(group, admin)
                .orElseThrow(() -> new NoSuchElementException("Admin is not a member of this group"));

        GroupMember targetMember = groupMemberRepository.findByGroupAndUser(group, member)
                .orElseThrow(() -> new NoSuchElementException("Member is not a member of this group"));

        // Check if admin has permission to unmute member
        if (!"OWNER".equals(adminMember.getRole()) && !"ADMIN".equals(adminMember.getRole())) {
            throw new IllegalArgumentException("Only group owner or admin can unmute members");
        }

        targetMember.setIsMuted(false);
        targetMember.setMuteEndTime(null);
        groupMemberRepository.save(targetMember);
    }

    @Override
    public List<User> getGroupMembers(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));

        List<GroupMember> groupMembers = groupMemberRepository.findByGroup(group);
        return groupMembers.stream()
                .map(GroupMember::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isGroupMember(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return groupMemberRepository.existsByGroupAndUser(group, user);
    }

    @Override
    public String generateInviteCode(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));

        String inviteCode = generateRandomCode();
        group.setInviteCode(inviteCode);
        groupRepository.save(group);

        return inviteCode;
    }

    @Override
    public GroupMember getGroupMember(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new NoSuchElementException("User is not a member of this group"));
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Override
    public com.wechat.entity.GroupMessage sendGroupMessage(com.wechat.entity.GroupMessage groupMessage) {
        // 检查发送者是否为群成员
        if (!isGroupMember(groupMessage.getGroup().getId(), groupMessage.getSender().getId())) {
            throw new IllegalArgumentException("Sender is not a member of this group");
        }
        
        // 检查发送者是否被禁言
        GroupMember senderMember = getGroupMember(groupMessage.getGroup().getId(), groupMessage.getSender().getId());
        if (senderMember.getIsMuted() && senderMember.getMuteEndTime().after(new Date())) {
            throw new IllegalArgumentException("Sender is muted");
        }
        
        // 保存群聊消息
        return groupMessageRepository.save(groupMessage);
    }
}
