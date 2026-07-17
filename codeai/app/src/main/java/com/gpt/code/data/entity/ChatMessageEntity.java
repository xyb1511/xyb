package com.gpt.code.data.entity;

public class ChatMessageEntity {

    private long id;
    private long sessionId;
    private String role;
    private String content;
    private long timestamp;

    public ChatMessageEntity() {}

    public ChatMessageEntity(long sessionId, String role, String content) {
        this.id = System.currentTimeMillis() + (long)(Math.random() * 1000);
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
