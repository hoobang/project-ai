package com.wechat.service;

import com.wechat.entity.Group;
import com.wechat.entity.GroupMember;
import com.wechat.entity.User;
import java.util.List;

/**
 * 群组领域服务接口。
 * <p>
 * 职责：
 * - 群的创建、维护、删除与检索；
 * - 成员的加入/退出、昵称维护、移除与角色变更；
 * - 成员禁言与解禁；
 * - 邀请码生成与成员查询；
 * - 群聊消息发送。
 * </p>
 */
public interface GroupService {

    /**
     * 创建群组并将创建者设为 OWNER。
     * @param group 群组信息
     * @param creatorId 创建者ID
     * @return 保存后的群组
     * @throws NoSuchElementException 当创建者不存在时
     */
    Group createGroup(Group group, Long creatorId);

    /**
     * 更新群组基础信息。
     * @param group 新的群信息
     * @return 更新后的群组
     * @throws NoSuchElementException 当群不存在时
     */
    Group updateGroup(Group group);

    /**
     * 删除群组。
     * @param groupId 群ID
     * @throws NoSuchElementException 当群不存在时
     */
    void deleteGroup(Long groupId);

    /**
     * 通过主键获取群组。
     * @param groupId 群ID
     * @return 群组
     * @throws NoSuchElementException 当群不存在时
     */
    Group getGroupById(Long groupId);

    /**
     * 按关键字搜索群组。
     * @param keyword 关键字
     * @return 群列表
     */
    List<Group> searchGroups(String keyword);

    /**
     * 查询用户参与的群组。
     * @param userId 用户ID
     * @return 群列表
     */
    List<Group> getUserGroups(Long userId);

    /**
     * 加入群组（校验公开性/邀请码/人数上限）。
     * @param groupId 群ID
     * @param userId 用户ID
     * @param inviteCode 邀请码（私密群必需）
     * @return 群成员
     */
    GroupMember joinGroup(Long groupId, Long userId, String inviteCode);

    /**
     * 退出群组（OWNER 不允许退出）。
     * @param groupId 群ID
     * @param userId 用户ID
     */
    void leaveGroup(Long groupId, Long userId);

    /**
     * 更新群成员的群内昵称。
     * @param groupId 群ID
     * @param userId 用户ID
     * @param groupNickname 群内昵称
     * @return 更新后的群成员
     */
    GroupMember updateGroupMember(Long groupId, Long userId, String groupNickname);

    /**
     * 移除群成员（需 OWNER 或 ADMIN 权限）。
     * @param groupId 群ID
     * @param adminId 操作管理员ID
     * @param memberId 被移除成员ID
     */
    void removeGroupMember(Long groupId, Long adminId, Long memberId);

    /**
     * 变更成员角色（仅 OWNER 可操作）。
     * @param groupId 群ID
     * @param adminId 操作管理员ID（必须为 OWNER）
     * @param memberId 成员ID
     * @param role 新角色（ADMIN/MEMBER）
     */
    void changeMemberRole(Long groupId, Long adminId, Long memberId, String role);

    /**
     * 禁言成员（需 OWNER/ADMIN 权限）。
     * @param groupId 群ID
     * @param adminId 操作管理员ID
     * @param memberId 成员ID
     * @param muteDuration 禁言时长，毫秒
     */
    void muteMember(Long groupId, Long adminId, Long memberId, Long muteDuration);

    /**
     * 解除禁言（需 OWNER/ADMIN 权限）。
     * @param groupId 群ID
     * @param adminId 操作管理员ID
     * @param memberId 成员ID
     */
    void unmuteMember(Long groupId, Long adminId, Long memberId);

    /**
     * 获取群成员用户列表。
     * @param groupId 群ID
     * @return 成员用户列表
     */
    List<User> getGroupMembers(Long groupId);

    /**
     * 判断用户是否为群成员。
     * @param groupId 群ID
     * @param userId 用户ID
     * @return 是否为成员
     */
    boolean isGroupMember(Long groupId, Long userId);

    /**
     * 生成群组邀请码。
     * @param groupId 群ID
     * @return 新邀请码
     */
    String generateInviteCode(Long groupId);

    /**
     * 获取群成员实体。
     * @param groupId 群ID
     * @param userId 用户ID
     * @return 群成员
     * @throws NoSuchElementException 不在群内时
     */
    GroupMember getGroupMember(Long groupId, Long userId);

    // 发送群聊消息
    com.wechat.entity.GroupMessage sendGroupMessage(com.wechat.entity.GroupMessage groupMessage);
}
