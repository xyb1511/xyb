package com.gpt.code.ui.settings;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.gpt.code.ai.config.AiConfig;
import com.gpt.code.ai.config.AiConfigRepository;
import java.util.List;

public class SettingsViewModel extends AndroidViewModel {

    private final AiConfigRepository repository;
    private final MutableLiveData<List<AiConfig>> configsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> activeConfigIdLiveData = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        repository = new AiConfigRepository(application);
        loadConfigs();
    }

    private void loadConfigs() {
        configsLiveData.setValue(repository.getAllConfigs());
        activeConfigIdLiveData.setValue(repository.getActiveConfigId());
    }

    public LiveData<List<AiConfig>> getConfigs() { return configsLiveData; }
    public LiveData<String> getActiveConfigId() { return activeConfigIdLiveData; }

    public void addConfig(AiConfig config) {
        repository.addConfig(config);
        if (repository.getActiveConfigId() == null) {
            repository.setActiveConfigId(config.getId());
        }
        loadConfigs();
    }

    public void updateConfig(AiConfig config) {
        repository.updateConfig(config);
        loadConfigs();
    }

    public void deleteConfig(String configId) {
        repository.deleteConfig(configId);
        if (configId.equals(repository.getActiveConfigId())) {
            repository.setActiveConfigId(null);
        }
        loadConfigs();
    }

    public void setActiveConfigId(String configId) {
        repository.setActiveConfigId(configId);
        loadConfigs();
    }
}
