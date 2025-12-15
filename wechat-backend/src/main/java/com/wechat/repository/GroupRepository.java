package com.wechat.repository;

import com.wechat.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 群组仓储接口。
 * <p>
 * 提供按群名的模糊查询、公开群搜索以及用户参与的群列表查询；
 * 支持基于邀请码的唯一查找。
 * </p>
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByInviteCode(String inviteCode);

    List<Group> findByGroupNameContaining(String keyword);

    @Query("SELECT g FROM Group g WHERE g.isPublic = true AND g.groupName LIKE %:keyword%")
    List<Group> searchPublicGroups(@Param("keyword") String keyword);

    @Query("SELECT g FROM Group g JOIN GroupMember gm ON g.id = gm.group.id WHERE gm.user.id = :userId ORDER BY g.updatedAt DESC")
    List<Group> findGroupsByUserId(@Param("userId") Long userId);
}
