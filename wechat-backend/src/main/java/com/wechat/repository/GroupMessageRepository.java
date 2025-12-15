package com.wechat.repository;

import com.wechat.entity.Group;
import com.wechat.entity.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 群聊消息仓储接口。
 * <p>
 * 提供按群与时间排序的消息列表查询，以及限定条数的最近消息查询。
 * </p>
 */
@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {

    List<GroupMessage> findByGroupOrderByCreatedAt(Group group);

    List<GroupMessage> findByGroupOrderByCreatedAtDesc(Group group);

    List<GroupMessage> findTop100ByGroupOrderByCreatedAtDesc(Group group);
}
