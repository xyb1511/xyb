package com.gpt.code.ui.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gpt.code.R;
import com.gpt.code.project.Project;
import java.util.List;

public class ProjectListFragment extends Fragment {

    public interface OnProjectSelectedListener {
        void onProjectSelected(String projectId, String projectName);
    }

    private RecyclerView rvProjects;
    private TextView tvEmpty;
    private ExtendedFloatingActionButton fabImport;
    private ProjectAdapter adapter;
    private ProjectListViewModel viewModel;
    private OnProjectSelectedListener listener;

    private final ActivityResultLauncher<Intent> folderPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK
                        && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        handleFolderSelected(uri);
                    }
                }
            });

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        if (context instanceof OnProjectSelectedListener) {
            listener = (OnProjectSelectedListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_list, container, false);
        rvProjects = view.findViewById(R.id.rvProjects);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        fabImport = view.findViewById(R.id.fabImport);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProjectListViewModel.class);

        adapter = new ProjectAdapter();
        rvProjects.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvProjects.setAdapter(adapter);

        adapter.setOnProjectActionListener(new ProjectAdapter.OnProjectActionListener() {
            @Override
            public void onProjectClick(Project project) {
                if (listener != null) {
                    listener.onProjectSelected(project.getId(), project.getName());
                }
            }

            @Override
            public void onProjectLongClick(Project project) {
                showRemoveDialog(project);
            }
        });

        fabImport.setOnClickListener(v -> openFolderPicker());

        viewModel.getProjects().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && getView() != null) {
                Snackbar.make(getView(), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void updateUI(List<Project> projects) {
        adapter.setProjects(projects);
        if (projects == null || projects.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvProjects.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvProjects.setVisibility(View.VISIBLE);
        }
    }

    private void openFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        folderPickerLauncher.launch(intent);
    }

    private void handleFolderSelected(Uri uri) {
        requireActivity().getContentResolver().takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        );

        String path = getPathFromUri(uri);
        String name = extractProjectName(uri);
        viewModel.importProject(path, name);
    }

    private String getPathFromUri(Uri uri) {
        String docId = DocumentsContract.getTreeDocumentId(uri);
        if (docId.startsWith("primary:")) {
            return "/storage/emulated/0/" + docId.substring(8);
        }
        return docId;
    }

    private String extractProjectName(Uri uri) {
        String docId = DocumentsContract.getTreeDocumentId(uri);
        String path = docId.contains(":") ? docId.split(":")[1] : docId;
        String[] parts = path.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : path;
    }

    private void showRemoveDialog(Project project) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("移除项目")
                .setMessage("确定要从列表中移除 \"" + project.getName() + "\" 吗？（不会删除项目文件）")
                .setPositiveButton("移除", (d, w) -> viewModel.removeProject(project.getId()))
                .setNegativeButton("取消", null)
                .show();
    }
}
