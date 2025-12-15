package com.wechat.repository;

import com.wechat.entity.Group;
import com.wechat.entity.GroupMember;
import com.wechat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroupAndUser(Group group, User user);

    List<GroupMember> findByGroup(Group group);

    List<GroupMember> findByUser(User user);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.role IN ('OWNER', 'ADMIN')")
    List<GroupMember> findAdminsByGroupId(@Param("groupId") Long groupId);

    long countByGroup(Group group);

    boolean existsByGroupAndUser(Group group, User user);
}
