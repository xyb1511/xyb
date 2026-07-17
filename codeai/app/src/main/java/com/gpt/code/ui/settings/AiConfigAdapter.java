package com.gpt.code.ui.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gpt.code.R;
import com.gpt.code.ai.config.AiConfig;
import java.util.ArrayList;
import java.util.List;

public class AiConfigAdapter extends RecyclerView.Adapter<AiConfigAdapter.ViewHolder> {

    private List<AiConfig> configs = new ArrayList<>();
    private String activeConfigId;
    private OnConfigActionListener listener;

    public interface OnConfigActionListener {
        void onConfigClick(AiConfig config);
        void onConfigLongClick(AiConfig config);
    }

    public void setOnConfigActionListener(OnConfigActionListener listener) {
        this.listener = listener;
    }

    public void setConfigs(List<AiConfig> configs, String activeConfigId) {
        this.configs = configs != null ? configs : new ArrayList<>();
        this.activeConfigId = activeConfigId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ai_config, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AiConfig config = configs.get(position);
        holder.tvAlias.setText(config.getAlias());
        holder.tvModel.setText("模型: " + config.getModelName());
        holder.tvBaseUrl.setText(config.getBaseUrl());

        boolean isActive = config.getId().equals(activeConfigId);
        holder.rbActive.setChecked(isActive);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onConfigClick(config);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onConfigLongClick(config);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return configs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAlias, tvModel, tvBaseUrl;
        RadioButton rbActive;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlias = itemView.findViewById(R.id.tvAlias);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvBaseUrl = itemView.findViewById(R.id.tvBaseUrl);
            rbActive = itemView.findViewById(R.id.rbActive);
        }
    }
}
