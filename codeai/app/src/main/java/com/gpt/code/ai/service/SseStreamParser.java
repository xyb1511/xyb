package com.gpt.code.ai.service;

import com.google.gson.Gson;

public class SseStreamParser {

    private final Gson gson = new Gson();
    private final StringBuilder buffer = new StringBuilder();

    public String parse(String line) {
        if (line == null || line.isEmpty()) return null;
        if (line.equals("data: [DONE]")) return "[DONE]";
        if (line.startsWith("data: ")) {
            String json = line.substring(6);
            ChatCompletionResponse response = gson.fromJson(json, ChatCompletionResponse.class);
            return response.getContent();
        }
        return null;
    }

    public void reset() {
        buffer.setLength(0);
    }
}
