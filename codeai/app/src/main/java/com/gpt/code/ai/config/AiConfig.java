package com.gpt.code.ai.config;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class AiConfig {

    @SerializedName("id")
    private String id;

    @SerializedName("alias")
    private String alias;

    @SerializedName("apiKey")
    private String apiKey;

    @SerializedName("baseUrl")
    private String baseUrl;

    @SerializedName("modelName")
    private String modelName;

    public AiConfig() {
        this.id = UUID.randomUUID().toString();
    }

    public AiConfig(String alias, String apiKey, String baseUrl, String modelName) {
        this.id = UUID.randomUUID().toString();
        this.alias = alias;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.modelName = modelName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getMaskedApiKey() {
        if (apiKey == null || apiKey.length() <= 6) return "****";
        return apiKey.substring(0, 3) + "****" + apiKey.substring(apiKey.length() - 3);
    }
}
