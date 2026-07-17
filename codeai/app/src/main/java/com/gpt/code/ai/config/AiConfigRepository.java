package com.gpt.code.ai.config;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AiConfigRepository {

    private static final String PREFS_NAME = "ai_config_prefs";
    private static final String KEY_CONFIGS = "ai_configs";
    private static final String KEY_ACTIVE_ID = "active_config_id";

    private final SharedPreferences prefs;
    private final Gson gson;

    public AiConfigRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public List<AiConfig> getAllConfigs() {
        String json = prefs.getString(KEY_CONFIGS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<AiConfig>>() {}.getType();
        List<AiConfig> configs = gson.fromJson(json, listType);
        return configs != null ? configs : new ArrayList<>();
    }

    public void saveAllConfigs(List<AiConfig> configs) {
        String json = gson.toJson(configs);
        prefs.edit().putString(KEY_CONFIGS, json).apply();
    }

    public void addConfig(AiConfig config) {
        List<AiConfig> configs = getAllConfigs();
        configs.add(config);
        saveAllConfigs(configs);
    }

    public void updateConfig(AiConfig config) {
        List<AiConfig> configs = getAllConfigs();
        for (int i = 0; i < configs.size(); i++) {
            if (configs.get(i).getId().equals(config.getId())) {
                configs.set(i, config);
                break;
            }
        }
        saveAllConfigs(configs);
    }

    public void deleteConfig(String configId) {
        List<AiConfig> configs = getAllConfigs();
        for (int i = 0; i < configs.size(); i++) {
            if (configs.get(i).getId().equals(configId)) {
                configs.remove(i);
                break;
            }
        }
        saveAllConfigs(configs);
    }

    public void setActiveConfigId(String configId) {
        prefs.edit().putString(KEY_ACTIVE_ID, configId).apply();
    }

    public String getActiveConfigId() {
        return prefs.getString(KEY_ACTIVE_ID, null);
    }

    public AiConfig getActiveConfig() {
        String activeId = getActiveConfigId();
        if (activeId == null) return null;
        List<AiConfig> configs = getAllConfigs();
        for (AiConfig config : configs) {
            if (config.getId().equals(activeId)) {
                return config;
            }
        }
        return null;
    }
}
