package com.wechat.entity;

import lombok.Data;
import java.util.Date;

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
