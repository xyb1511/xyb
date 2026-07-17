package com.gpt.code.ui.workspace;

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
import com.gpt.code.R;
import com.gpt.code.ui.workspace.FileSystemManager.FileItem;

public class FileExplorerFragment extends Fragment {

    private RecyclerView rvFiles;
    private TextView tvCurrentPath;
    private View btnUp;
    private FileExplorerAdapter adapter;
    private WorkspaceViewModel workspaceViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_explorer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFiles = view.findViewById(R.id.rvFiles);
        tvCurrentPath = view.findViewById(R.id.tvCurrentPath);
        btnUp = view.findViewById(R.id.btnUp);

        workspaceViewModel = new ViewModelProvider(requireActivity()).get(WorkspaceViewModel.class);

        adapter = new FileExplorerAdapter();
        rvFiles.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFiles.setAdapter(adapter);

        workspaceViewModel.getCurrentDir().observe(getViewLifecycleOwner(), path -> {
            tvCurrentPath.setText(path);
        });

        workspaceViewModel.getFileItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
        });

        btnUp.setOnClickListener(v -> workspaceViewModel.navigateUp());

        adapter.setOnItemClickListener(item -> {
            if (item.isDirectory) {
                workspaceViewModel.navigateTo(item.path);
            } else {
                workspaceViewModel.openFile(item.path);
            }
        });
    }
}
