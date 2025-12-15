package com.wechat.repository;

import com.wechat.entity.Group;
import com.wechat.entity.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {

    List<GroupMessage> findByGroupOrderByCreatedAt(Group group);

    List<GroupMessage> findByGroupOrderByCreatedAtDesc(Group group);

    List<GroupMessage> findTop100ByGroupOrderByCreatedAtDesc(Group group);
}
