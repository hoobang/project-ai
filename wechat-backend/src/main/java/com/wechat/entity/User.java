package com.wechat.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 用户实体，对应表 {@code users}。
 * <p>
 * 主要字段：
 * - 唯一身份信息：用户名、邮箱、手机号；
 * - 展示信息：昵称、头像、个性签名、性别、位置、生日；
 * - 审计信息：创建时间、更新时间、最后登录；
 * - 状态：是否启用 {@code active}。
 * </p>
 * 数据库关系：
 * - 作为消息、群组、好友关系等的关联主体。
 * 生命周期：
 * - {@link #onCreate()} 与 {@link #onUpdate()} 自动维护审计时间。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(unique = true, nullable = false, length = 11)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String nickname;

    @Column(length = 255)
    private String avatar;

    @Column(length = 500)
    private String signature;

    @Column(length = 20)
    private String gender;

    private String location;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    private Boolean active = true;

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
