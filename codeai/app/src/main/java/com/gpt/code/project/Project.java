package com.gpt.code.project;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class Project {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("rootPath")
    private String rootPath;

    @SerializedName("importTime")
    private long importTime;

    public Project() {
        this.id = UUID.randomUUID().toString();
        this.importTime = System.currentTimeMillis();
    }

    public Project(String name, String rootPath) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.rootPath = rootPath;
        this.importTime = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRootPath() { return rootPath; }
    public void setRootPath(String rootPath) { this.rootPath = rootPath; }

    public long getImportTime() { return importTime; }
    public void setImportTime(long importTime) { this.importTime = importTime; }
}
