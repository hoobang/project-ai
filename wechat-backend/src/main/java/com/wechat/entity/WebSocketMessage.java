package com.wechat.entity;

import lombok.Data;
import java.util.Date;

/**
 * WebSocket 传输使用的消息数据模型（非持久化）。
 * <p>
 * 用途：
 * - 作为 STOMP 消息体在客户端与服务端之间传递；
 * - 支持私聊与群聊的通用字段（含用户与群的标识）。
 * </p>
 * 说明：
 * - 该类不映射数据库，仅用于实时通信场景。
 */
@Data
public class WebSocketMessage {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long groupId;
    private String content;
    private String type; // TEXT, IMAGE, VOICE, VIDEO, FILE
    private Date timestamp;
    private Boolean isRead;
    private String senderName;
    private String senderAvatar;
    private String fileName;
    private String fileUrl;
}
