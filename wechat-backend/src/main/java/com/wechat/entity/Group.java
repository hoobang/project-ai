package com.wechat.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 群组实体，对应表 {@code chat_groups}。
 * <p>
 * 主要字段：
 * - 基本信息：群名、头像、描述；
 * - 权限与规模：是否公开、最大成员数、成员数量；
 * - 管理信息：创建者、邀请码；
 * - 审计时间：创建与更新。
 * </p>
 * 生命周期：
 * - {@link #onCreate()} 与 {@link #onUpdate()} 自动维护审计时间。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(name = "group_avatar", length = 255)
    private String groupAvatar;

    @Column(name = "group_description", columnDefinition = "TEXT")
    private String groupDescription;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "member_count", nullable = false)
    private Integer memberCount = 0;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers = 500;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @Column(name = "invite_code", length = 20, unique = true)
    private String inviteCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
