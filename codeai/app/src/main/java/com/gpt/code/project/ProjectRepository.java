package com.gpt.code.project;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProjectRepository {

    private static final String PREFS_NAME = "project_prefs";
    private static final String KEY_PROJECTS = "imported_projects";

    private final SharedPreferences prefs;
    private final Gson gson;

    public ProjectRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public List<Project> getAllProjects() {
        String json = prefs.getString(KEY_PROJECTS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<Project>>() {}.getType();
        List<Project> projects = gson.fromJson(json, listType);
        return projects != null ? projects : new ArrayList<>();
    }

    private void saveAllProjects(List<Project> projects) {
        String json = gson.toJson(projects);
        prefs.edit().putString(KEY_PROJECTS, json).apply();
    }

    public void addProject(Project project) {
        List<Project> projects = getAllProjects();
        projects.add(project);
        saveAllProjects(projects);
    }

    public void removeProject(String projectId) {
        List<Project> projects = getAllProjects();
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getId().equals(projectId)) {
                projects.remove(i);
                break;
            }
        }
        saveAllProjects(projects);
    }

    public boolean isValidProject(String rootPath) {
        if (rootPath == null || rootPath.isEmpty()) return false;
        File dir = new File(rootPath);
        if (!dir.exists() || !dir.isDirectory()) return false;
        File buildFile = new File(dir, "build.gradle");
        return buildFile.exists() && buildFile.isFile();
    }

    public Project getProjectById(String projectId) {
        for (Project project : getAllProjects()) {
            if (project.getId().equals(projectId)) {
                return project;
            }
        }
        return null;
    }
}
