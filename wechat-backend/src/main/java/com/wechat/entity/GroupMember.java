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
@Table(name = "group_members")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "group_nickname", length = 50)
    private String groupNickname;

    @Column(name = "role", nullable = false, length = 20)
    private String role; // OWNER, ADMIN, MEMBER

    @Column(name = "is_muted", nullable = false)
    private Boolean isMuted = false;

    @Column(name = "mute_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date muteEndTime;

    @Column(name = "joined_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
