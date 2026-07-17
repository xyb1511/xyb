package com.gpt.code.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.gpt.code.R;
import com.gpt.code.ai.config.AiConfig;
import java.util.List;

public class SettingsFragment extends Fragment {

    private RecyclerView rvConfigs;
    private TextView tvEmpty;
    private ExtendedFloatingActionButton fabAddConfig;
    private AiConfigAdapter adapter;
    private SettingsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        rvConfigs = view.findViewById(R.id.rvConfigs);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        fabAddConfig = view.findViewById(R.id.fabAddConfig);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        adapter = new AiConfigAdapter();
        rvConfigs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvConfigs.setAdapter(adapter);

        adapter.setOnConfigActionListener(new AiConfigAdapter.OnConfigActionListener() {
            @Override
            public void onConfigClick(AiConfig config) {
                viewModel.setActiveConfigId(config.getId());
            }

            @Override
            public void onConfigLongClick(AiConfig config) {
                showEditDeleteDialog(config);
            }
        });

        fabAddConfig.setOnClickListener(v -> showAddDialog());

        viewModel.getConfigs().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getActiveConfigId().observe(getViewLifecycleOwner(), activeId -> {
            List<AiConfig> configs = viewModel.getConfigs().getValue();
            if (configs != null) {
                adapter.setConfigs(configs, activeId);
            }
        });
    }

    private void updateUI(List<AiConfig> configs) {
        adapter.setConfigs(configs, viewModel.getActiveConfigId().getValue());
        if (configs == null || configs.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvConfigs.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvConfigs.setVisibility(View.VISIBLE);
        }
    }

    private void showAddDialog() {
        AddEditConfigDialog dialog = AddEditConfigDialog.newAddInstance();
        dialog.setOnConfigSavedListener((config, isEdit) -> viewModel.addConfig(config));
        dialog.show(getChildFragmentManager(), "AddEditConfigDialog");
    }

    private void showEditDeleteDialog(AiConfig config) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(config.getAlias())
                .setItems(new String[]{"编辑", "删除"}, (d, which) -> {
                    if (which == 0) {
                        AddEditConfigDialog editDialog = AddEditConfigDialog.newEditInstance(config);
                        editDialog.setOnConfigSavedListener((c, isEdit) -> viewModel.updateConfig(c));
                        editDialog.show(getChildFragmentManager(), "AddEditConfigDialog");
                    } else {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("确认删除")
                                .setMessage("确定要删除配置 \"" + config.getAlias() + "\" 吗？")
                                .setPositiveButton("删除", (d2, w) -> viewModel.deleteConfig(config.getId()))
                                .setNegativeButton("取消", null)
                                .show();
                    }
                })
                .show();
    }
}
