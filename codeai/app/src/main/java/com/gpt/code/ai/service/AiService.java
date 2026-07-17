package com.gpt.code.ai.service;

import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.gpt.code.ai.config.AiConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiService {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final Gson gson;

    public AiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public void sendMessage(AiConfig config, ChatCompletionRequest request,
                            MutableLiveData<String> contentLiveData,
                            MutableLiveData<Boolean> loadingLiveData,
                            MutableLiveData<String> errorLiveData) {

        loadingLiveData.postValue(true);

        new Thread(() -> {
            try {
                String url = config.getBaseUrl();
                if (!url.endsWith("/")) url += "/";
                url += "chat/completions";

                request.setModel(config.getModelName());
                request.setStream(true);

                String jsonBody = gson.toJson(request);
                RequestBody body = RequestBody.create(JSON, jsonBody);

                Request httpRequest = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + config.getApiKey())
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                Response response = client.newCall(httpRequest).execute();

                if (!response.isSuccessful()) {
                    String errorMsg = "API 请求失败 (HTTP " + response.code() + ")";
                    if (response.code() == 401) {
                        errorMsg = "API Key 认证失败，请检查设置中的 API Key 是否正确";
                    }
                    errorLiveData.postValue(errorMsg);
                    loadingLiveData.postValue(false);
                    return;
                }

                SseStreamParser parser = new SseStreamParser();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.body().byteStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    String content = parser.parse(line);
                    if (content != null) {
                        if ("[DONE]".equals(content)) break;
                        contentLiveData.postValue(content);
                    }
                }

                reader.close();
                response.close();
                loadingLiveData.postValue(false);

            } catch (IOException e) {
                errorLiveData.postValue("网络请求失败: " + e.getMessage());
                loadingLiveData.postValue(false);
            }
        }).start();
    }

    public void sendMessageSync(AiConfig config, ChatCompletionRequest request,
                                MutableLiveData<String> resultLiveData,
                                MutableLiveData<Boolean> loadingLiveData,
                                MutableLiveData<String> errorLiveData) {

        loadingLiveData.postValue(true);

        new Thread(() -> {
            try {
                String url = config.getBaseUrl();
                if (!url.endsWith("/")) url += "/";
                url += "chat/completions";

                request.setModel(config.getModelName());
                request.setStream(false);

                String jsonBody = gson.toJson(request);
                RequestBody body = RequestBody.create(JSON, jsonBody);

                Request httpRequest = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + config.getApiKey())
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                Response response = client.newCall(httpRequest).execute();

                if (!response.isSuccessful()) {
                    String errorMsg = "API 请求失败 (HTTP " + response.code() + ")";
                    if (response.code() == 401) {
                        errorMsg = "API Key 认证失败，请检查设置中的 API Key 是否正确";
                    }
                    errorLiveData.postValue(errorMsg);
                    loadingLiveData.postValue(false);
                    return;
                }

                String responseBody = response.body().string();
                ChatCompletionResponse chatResponse = gson.fromJson(
                        responseBody, ChatCompletionResponse.class);
                resultLiveData.postValue(chatResponse.getContent());
                loadingLiveData.postValue(false);

                response.close();

            } catch (IOException e) {
                errorLiveData.postValue("网络请求失败: " + e.getMessage());
                loadingLiveData.postValue(false);
            }
        }).start();
    }
}
