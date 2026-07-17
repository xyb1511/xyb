package com.gpt.code.ui.project;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.gpt.code.project.Project;
import com.gpt.code.project.ProjectRepository;
import java.util.List;

public class ProjectListViewModel extends AndroidViewModel {

    private final ProjectRepository repository;
    private final MutableLiveData<List<Project>> projectsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public ProjectListViewModel(@NonNull Application application) {
        super(application);
        repository = new ProjectRepository(application);
        loadProjects();
    }

    private void loadProjects() {
        projectsLiveData.setValue(repository.getAllProjects());
    }

    public LiveData<List<Project>> getProjects() { return projectsLiveData; }
    public LiveData<String> getError() { return errorLiveData; }

    public void importProject(String rootPath, String name) {
        if (!repository.isValidProject(rootPath)) {
            errorLiveData.setValue("选择的目录不是有效的 Android 项目（缺少 build.gradle 文件）");
            return;
        }

        Project project = new Project(name, rootPath);
        repository.addProject(project);
        loadProjects();
    }

    public void removeProject(String projectId) {
        repository.removeProject(projectId);
        loadProjects();
    }
}
