package com.wechat.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

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
