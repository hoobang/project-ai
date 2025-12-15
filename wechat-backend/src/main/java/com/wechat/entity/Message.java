package com.wechat.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 私聊消息实体，对应表 {@code messages}。
 * <p>
 * 主要字段：
 * - 发送者与接收者（用户关联）；
 * - 文本内容与类型（TEXT/IMAGE/VOICE/VIDEO/FILE）；
 * - 附件信息：路径、文件名、大小；
 * - 已读状态与审计时间。
 * </p>
 * 生命周期：
 * - {@link #onCreate()} 与 {@link #onUpdate()} 自动维护创建与更新时间。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "file_path", length = 255)
    private String filePath;

    @Column(name = "file_name", length = 100)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "is_read", nullable = false)
    private Boolean read = false;

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
