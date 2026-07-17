package com.gpt.code.data.entity;

import java.util.concurrent.atomic.AtomicLong;

public class ChatSessionEntity {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(System.currentTimeMillis());

    private long id;
    private String projectId;
    private String title;
    private long createdAt;

    public ChatSessionEntity() {}

    public ChatSessionEntity(String projectId, String title) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.projectId = projectId;
        this.title = title;
        this.createdAt = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
