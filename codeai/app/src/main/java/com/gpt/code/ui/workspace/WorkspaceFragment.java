package com.gpt.code.ui.workspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.tabs.TabLayout;
import com.gpt.code.R;

public class WorkspaceFragment extends Fragment {

    private TabLayout tabLayout;
    private WorkspaceViewModel viewModel;

    private ChatFragment chatFragment;
    private FileExplorerFragment fileExplorerFragment;
    private CodeEditorFragment codeEditorFragment;
    private TerminalFragment terminalFragment;

    private static final int TAB_CHAT = 0;
    private static final int TAB_FILES = 1;
    private static final int TAB_EDITOR = 2;
    private static final int TAB_TERMINAL = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workspace, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.tabLayout);

        viewModel = new ViewModelProvider(requireActivity()).get(WorkspaceViewModel.class);

        String projectId = getArguments() != null ? getArguments().getString("projectId") : null;
        if (projectId != null) {
            viewModel.init(projectId);
        }

        chatFragment = new ChatFragment();
        fileExplorerFragment = new FileExplorerFragment();
        codeEditorFragment = new CodeEditorFragment();
        terminalFragment = new TerminalFragment();

        tabLayout.addTab(tabLayout.newTab().setText("聊天"));
        tabLayout.addTab(tabLayout.newTab().setText("文件"));
        tabLayout.addTab(tabLayout.newTab().setText("编辑"));
        tabLayout.addTab(tabLayout.newTab().setText("终端"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Default to Chat tab
        showFragment(chatFragment);
    }

    private void switchFragment(int position) {
        switch (position) {
            case TAB_CHAT:
                showFragment(chatFragment);
                break;
            case TAB_FILES:
                showFragment(fileExplorerFragment);
                break;
            case TAB_EDITOR:
                showFragment(codeEditorFragment);
                break;
            case TAB_TERMINAL:
                showFragment(terminalFragment);
                break;
        }
    }

    private void showFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
