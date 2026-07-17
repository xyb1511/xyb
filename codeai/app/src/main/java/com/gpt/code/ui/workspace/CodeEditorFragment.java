package com.gpt.code.ui.workspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.gpt.code.R;

public class CodeEditorFragment extends Fragment {

    private EditText etCode;
    private TextView tvFileName;
    private MaterialButton btnSave;
    private WorkspaceViewModel workspaceViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_code_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etCode = view.findViewById(R.id.etCode);
        tvFileName = view.findViewById(R.id.tvFileName);
        btnSave = view.findViewById(R.id.btnSave);

        workspaceViewModel = new ViewModelProvider(requireActivity()).get(WorkspaceViewModel.class);

        workspaceViewModel.getEditingFile().observe(getViewLifecycleOwner(), fileName -> {
            if (fileName != null) {
                tvFileName.setText(fileName);
                btnSave.setVisibility(View.VISIBLE);
            } else {
                tvFileName.setText("未打开文件");
                btnSave.setVisibility(View.GONE);
            }
        });

        workspaceViewModel.getEditingContent().observe(getViewLifecycleOwner(), content -> {
            etCode.setText(content);
        });

        btnSave.setOnClickListener(v -> {
            String content = etCode.getText().toString();
            boolean success = workspaceViewModel.saveFile(content);
            if (success && getView() != null) {
                Snackbar.make(getView(), "文件已保存", Snackbar.LENGTH_SHORT).show();
            } else if (getView() != null) {
                Snackbar.make(getView(), "保存失败", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
