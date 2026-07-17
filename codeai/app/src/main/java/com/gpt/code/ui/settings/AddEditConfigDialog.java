package com.gpt.code.ui.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.gpt.code.R;
import com.gpt.code.ai.config.AiConfig;

public class AddEditConfigDialog extends DialogFragment {

    private static final String ARG_CONFIG = "config";
    private static final String ARG_IS_EDIT = "is_edit";

    private AiConfig existingConfig;
    private boolean isEdit;
    private OnConfigSavedListener listener;

    private EditText etAlias, etApiKey, etBaseUrl, etModelName;

    public interface OnConfigSavedListener {
        void onConfigSaved(AiConfig config, boolean isEdit);
    }

    public static AddEditConfigDialog newAddInstance() {
        AddEditConfigDialog dialog = new AddEditConfigDialog();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_EDIT, false);
        dialog.setArguments(args);
        return dialog;
    }

    public static AddEditConfigDialog newEditInstance(AiConfig config) {
        AddEditConfigDialog dialog = new AddEditConfigDialog();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_EDIT, true);
        args.putString(ARG_CONFIG, new com.google.gson.Gson().toJson(config));
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnConfigSavedListener(OnConfigSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            isEdit = args.getBoolean(ARG_IS_EDIT, false);
            String configJson = args.getString(ARG_CONFIG);
            if (configJson != null) {
                existingConfig = new com.google.gson.Gson().fromJson(configJson, AiConfig.class);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_ai_config, null);

        etAlias = view.findViewById(R.id.etAlias);
        etApiKey = view.findViewById(R.id.etApiKey);
        etBaseUrl = view.findViewById(R.id.etBaseUrl);
        etModelName = view.findViewById(R.id.etModelName);

        if (isEdit && existingConfig != null) {
            etAlias.setText(existingConfig.getAlias());
            etApiKey.setText(existingConfig.getApiKey());
            etBaseUrl.setText(existingConfig.getBaseUrl());
            etModelName.setText(existingConfig.getModelName());
        }

        String title = isEdit ? "编辑 AI 配置" : "添加 AI 配置";

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(view)
                .setPositiveButton("保存", (d, which) -> saveConfig())
                .setNegativeButton("取消", null)
                .create();

        return dialog;
    }

    private void saveConfig() {
        String alias = etAlias.getText().toString().trim();
        String apiKey = etApiKey.getText().toString().trim();
        String baseUrl = etBaseUrl.getText().toString().trim();
        String modelName = etModelName.getText().toString().trim();

        if (alias.isEmpty()) {
            etAlias.setError("别名不能为空");
            return;
        }
        if (apiKey.isEmpty()) {
            etApiKey.setError("API Key 不能为空");
            return;
        }
        if (baseUrl.isEmpty()) {
            etBaseUrl.setError("Base URL 不能为空");
            return;
        }
        if (modelName.isEmpty()) {
            etModelName.setError("模型名称不能为空");
            return;
        }

        AiConfig config;
        if (isEdit && existingConfig != null) {
            config = existingConfig;
        } else {
            config = new AiConfig();
        }
        config.setAlias(alias);
        config.setApiKey(apiKey);
        config.setBaseUrl(baseUrl);
        config.setModelName(modelName);

        if (listener != null) {
            listener.onConfigSaved(config, isEdit);
        }
    }
}
