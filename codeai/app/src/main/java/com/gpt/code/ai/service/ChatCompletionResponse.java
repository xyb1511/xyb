package com.gpt.code.ai.service;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatCompletionResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("choices")
    private List<Choice> choices;

    public static class Choice {
        @SerializedName("delta")
        private Delta delta;

        @SerializedName("message")
        private Message message;

        @SerializedName("index")
        private int index;

        public Delta getDelta() { return delta; }
        public Message getMessage() { return message; }
    }

    public static class Delta {
        @SerializedName("content")
        private String content;

        public String getContent() { return content; }
    }

    public static class Message {
        @SerializedName("content")
        private String content;

        public String getContent() { return content; }
    }

    public List<Choice> getChoices() { return choices; }

    public String getContent() {
        if (choices == null || choices.isEmpty()) return "";
        Choice choice = choices.get(0);
        if (choice.delta != null && choice.delta.getContent() != null) {
            return choice.delta.getContent();
        }
        if (choice.message != null && choice.message.getContent() != null) {
            return choice.message.getContent();
        }
        return "";
    }
}
