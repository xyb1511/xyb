package com.gpt.code.ui.workspace;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.gpt.code.ai.config.AiConfig;
import com.gpt.code.ai.config.AiConfigRepository;
import com.gpt.code.ai.service.AiService;
import com.gpt.code.ai.service.ChatCompletionRequest;
import com.gpt.code.data.ChatRepository;
import com.gpt.code.data.entity.ChatMessageEntity;
import com.gpt.code.data.entity.ChatSessionEntity;
import com.gpt.code.project.Project;
import com.gpt.code.project.ProjectRepository;
import com.gpt.code.ui.workspace.FileSystemManager.FileItem;
import androidx.lifecycle.Observer;
import java.util.ArrayList;
import java.util.List;

public class WorkspaceViewModel extends AndroidViewModel {

    private final AiService aiService;
    private final FileSystemManager fileSystemManager;
    private final TerminalExecutor terminalExecutor;
    private final AiConfigRepository aiConfigRepository;
    private final ProjectRepository projectRepository;
    private final ChatRepository chatRepository;

    private final MutableLiveData<List<ChatMessageEntity>> messagesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> currentDirLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<FileItem>> fileItemsLiveData = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<String> editingFileLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> editingContentLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<String>> terminalOutputLiveData = new MutableLiveData<>(new ArrayList<>());
    private final List<String> terminalOutputLines = new ArrayList<>();

    private final MutableLiveData<List<ChatSessionEntity>> sessionsLiveData = new MutableLiveData<>();

    private String projectPath;
    private String projectId;
    private String editingFilePath;
    private long currentSessionId = -1;

    public WorkspaceViewModel(@NonNull Application application) {
        super(application);
        aiService = new AiService();
        fileSystemManager = new FileSystemManager();
        terminalExecutor = new TerminalExecutor();
        aiConfigRepository = new AiConfigRepository(application);
        projectRepository = new ProjectRepository(application);
        chatRepository = new ChatRepository(application);
    }

    public void init(String projectId) {
        this.projectId = projectId;
        Project project = projectRepository.getProjectById(projectId);
        if (project != null) {
            this.projectPath = project.getRootPath();
            navigateTo(projectPath);
        }
        loadSessions();
        createNewSession();
    }

    // --- Session Management ---
    public LiveData<List<ChatSessionEntity>> getSessions() { return sessionsLiveData; }

    private void loadSessions() {
        chatRepository.getSessionsByProject(projectId).observeForever(sessions -> {
            sessionsLiveData.setValue(sessions);
        });
    }

    public void createNewSession() {
        ChatSessionEntity session = new ChatSessionEntity(projectId,
                "会话 " + (sessionsLiveData.getValue() != null
                        ? sessionsLiveData.getValue().size() + 1 : 1));
        long newSessionId = chatRepository.createSessionSync(session);
        currentSessionId = newSessionId;
        messagesLiveData.setValue(new ArrayList<>());

        // Reload sessions
        loadSessions();
    }

    public void loadSession(long sessionId) {
        currentSessionId = sessionId;
        chatRepository.getMessagesBySession(sessionId).observeForever(messages -> {
            messagesLiveData.setValue(messages);
        });
    }

    // --- Chat ---
    public LiveData<List<ChatMessageEntity>> getMessages() { return messagesLiveData; }
    public LiveData<Boolean> getLoading() { return loadingLiveData; }
    public LiveData<String> getError() { return errorLiveData; }

    public void sendMessage(String content) {
        AiConfig config = aiConfigRepository.getActiveConfig();
        if (config == null) {
            errorLiveData.setValue("请先在设置中配置 AI 模型");
            return;
        }

        List<ChatMessageEntity> currentMessages = messagesLiveData.getValue();
        if (currentMessages == null) currentMessages = new ArrayList<>();

        // Add user message
        ChatMessageEntity userMsg = new ChatMessageEntity(
                currentSessionId > 0 ? currentSessionId : 0, "user", content);
        currentMessages.add(userMsg);
        messagesLiveData.setValue(currentMessages);
        if (currentSessionId > 0) {
            chatRepository.insertMessage(userMsg);
        }

        // Build request
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.addMessage("system", "你是一个编程助手，帮助用户在 Android 项目中编写代码。" +
                "当前项目的文件结构如下:\n" + fileSystemManager.getProjectFileContext(projectPath));

        for (ChatMessageEntity msg : currentMessages) {
            request.addMessage(msg.getRole(), msg.getContent());
        }

        // Send to AI
        MutableLiveData<String> contentLiveData = new MutableLiveData<>();
        aiService.sendMessage(config, request, contentLiveData, loadingLiveData, errorLiveData);

        // Track AI response
        final StringBuilder aiResponse = new StringBuilder();
        final Observer<String> contentObserver = new Observer<String>() {
            @Override
            public void onChanged(String chunk) {
                if (chunk != null) {
                    aiResponse.append(chunk);
                }
            }
        };
        contentLiveData.observeForever(contentObserver);

        final Observer<Boolean> loadingObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                if (!loading && aiResponse.length() > 0) {
                    List<ChatMessageEntity> msgs = messagesLiveData.getValue();
                    if (msgs == null) msgs = new ArrayList<>();
                    ChatMessageEntity aiMsg = new ChatMessageEntity(
                            currentSessionId > 0 ? currentSessionId : 0,
                            "assistant", aiResponse.toString());
                    msgs.add(aiMsg);
                    messagesLiveData.postValue(msgs);
                    if (currentSessionId > 0) {
                        chatRepository.insertMessage(aiMsg);
                    }
                    loadingLiveData.removeObserver(this);
                    contentLiveData.removeObserver(contentObserver);
                }
            }
        };
        loadingLiveData.observeForever(loadingObserver);

        final Observer<String> errorObserver = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    loadingLiveData.postValue(false);
                    errorLiveData.removeObserver(this);
                }
            }
        };
        errorLiveData.observeForever(errorObserver);
    }

    // --- File Explorer ---
    public LiveData<String> getCurrentDir() { return currentDirLiveData; }
    public LiveData<List<FileItem>> getFileItems() { return fileItemsLiveData; }

    public void navigateTo(String path) {
        currentDirLiveData.setValue(path);
        fileItemsLiveData.setValue(fileSystemManager.listDirectory(path));
    }

    public void navigateUp() {
        String current = currentDirLiveData.getValue();
        if (current == null || current.equals("/")) return;
        String parent = new java.io.File(current).getParent();
        if (parent != null) {
            navigateTo(parent);
        }
    }

    public void openFile(String path) {
        String content = fileSystemManager.readFile(path);
        if (content != null) {
            editingFilePath = path;
            editingFileLiveData.setValue(path);
            editingContentLiveData.setValue(content);
        }
    }

    // --- Code Editor ---
    public LiveData<String> getEditingFile() { return editingFileLiveData; }
    public LiveData<String> getEditingContent() { return editingContentLiveData; }

    public boolean saveFile(String content) {
        if (editingFilePath == null) return false;
        return fileSystemManager.writeFile(editingFilePath, content);
    }

    // --- Terminal ---
    public LiveData<List<String>> getTerminalOutput() { return terminalOutputLiveData; }

    public void executeTerminalCommand(String command) {
        terminalOutputLines.add("$ " + command);
        terminalOutputLiveData.setValue(new ArrayList<>(terminalOutputLines));

        terminalExecutor.execute(command, projectPath, new TerminalExecutor.OutputListener() {
            @Override
            public void onOutput(String line) {
                terminalOutputLines.add(line);
                terminalOutputLiveData.postValue(new ArrayList<>(terminalOutputLines));
            }

            @Override
            public void onComplete(int exitCode) {
                terminalOutputLines.add("[退出码: " + exitCode + "]");
                terminalOutputLiveData.postValue(new ArrayList<>(terminalOutputLines));
            }

            @Override
            public void onError(String error) {
                terminalOutputLines.add("[错误: " + error + "]");
                terminalOutputLiveData.postValue(new ArrayList<>(terminalOutputLines));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        terminalExecutor.cancel();
    }
}
