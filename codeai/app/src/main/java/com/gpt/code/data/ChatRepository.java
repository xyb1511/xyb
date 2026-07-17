package com.gpt.code.data;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gpt.code.data.entity.ChatMessageEntity;
import com.gpt.code.data.entity.ChatSessionEntity;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRepository {

    private static final String PREFS_NAME = "chat_prefs";
    private static final String KEY_SESSIONS_PREFIX = "sessions_";
    private static final String KEY_MESSAGES_PREFIX = "messages_";

    private final SharedPreferences prefs;
    private final Gson gson;
    private final ExecutorService executor;

    public ChatRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.executor = Executors.newSingleThreadExecutor();
    }

    private Type sessionListType = new TypeToken<List<ChatSessionEntity>>() {}.getType();
    private Type messageListType = new TypeToken<List<ChatMessageEntity>>() {}.getType();

    public LiveData<List<ChatSessionEntity>> getSessionsByProject(String projectId) {
        MutableLiveData<List<ChatSessionEntity>> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            String json = prefs.getString(KEY_SESSIONS_PREFIX + projectId, "[]");
            List<ChatSessionEntity> sessions = gson.fromJson(json, sessionListType);
            if (sessions == null) sessions = new ArrayList<>();
            liveData.postValue(sessions);
        });
        return liveData;
    }

    public LiveData<List<ChatMessageEntity>> getMessagesBySession(long sessionId) {
        MutableLiveData<List<ChatMessageEntity>> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            String json = prefs.getString(KEY_MESSAGES_PREFIX + sessionId, "[]");
            List<ChatMessageEntity> messages = gson.fromJson(json, messageListType);
            if (messages == null) messages = new ArrayList<>();
            liveData.postValue(messages);
        });
        return liveData;
    }

    public void createSession(ChatSessionEntity session, Runnable callback) {
        executor.execute(() -> {
            String key = KEY_SESSIONS_PREFIX + session.getProjectId();
            String json = prefs.getString(key, "[]");
            List<ChatSessionEntity> sessions = gson.fromJson(json, sessionListType);
            if (sessions == null) sessions = new ArrayList<>();
            sessions.add(0, session);
            prefs.edit().putString(key, gson.toJson(sessions)).apply();
            if (callback != null) callback.run();
        });
    }

    public long createSessionSync(ChatSessionEntity session) {
        String key = KEY_SESSIONS_PREFIX + session.getProjectId();
        String json = prefs.getString(key, "[]");
        List<ChatSessionEntity> sessions = gson.fromJson(json, sessionListType);
        if (sessions == null) sessions = new ArrayList<>();
        sessions.add(0, session);
        prefs.edit().putString(key, gson.toJson(sessions)).apply();
        return session.getId();
    }

    public void insertMessage(ChatMessageEntity message) {
        executor.execute(() -> {
            String key = KEY_MESSAGES_PREFIX + message.getSessionId();
            String json = prefs.getString(key, "[]");
            List<ChatMessageEntity> messages = gson.fromJson(json, messageListType);
            if (messages == null) messages = new ArrayList<>();
            messages.add(message);
            prefs.edit().putString(key, gson.toJson(messages)).apply();
        });
    }

    public void deleteSession(long sessionId, String projectId) {
        executor.execute(() -> {
            // Remove session
            String sessionKey = KEY_SESSIONS_PREFIX + projectId;
            String sessionsJson = prefs.getString(sessionKey, "[]");
            List<ChatSessionEntity> sessions = gson.fromJson(sessionsJson, sessionListType);
            if (sessions != null) {
                for (int i = 0; i < sessions.size(); i++) {
                    if (sessions.get(i).getId() == sessionId) {
                        sessions.remove(i);
                        break;
                    }
                }
                prefs.edit().putString(sessionKey, gson.toJson(sessions)).apply();
            }
            // Remove messages
            String msgKey = KEY_MESSAGES_PREFIX + sessionId;
            prefs.edit().remove(msgKey).apply();
        });
    }
}
