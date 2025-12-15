package com.wechat.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 好友关系实体，对应表 {@code friendships}。
 * <p>
 * 状态流转：
 * - PENDING：发起请求，待对方处理；
 * - ACCEPTED：对方接受，成为好友；
 * - REJECTED：对方拒绝。
 * </p>
 * 审计：
 * - 创建、更新、接受时间；
 * - 在 {@link #onUpdate()} 中，当状态为 ACCEPTED 且未设置接受时间时自动补充。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friendships")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // PENDING, ACCEPTED, REJECTED

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "accepted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date acceptedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
        if ("ACCEPTED".equals(status) && acceptedAt == null) {
            acceptedAt = new Date();
        }
    }
}
