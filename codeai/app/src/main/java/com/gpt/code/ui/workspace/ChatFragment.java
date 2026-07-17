package com.gpt.code.ui.workspace;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.appbar.MaterialToolbar;
import com.gpt.code.R;
import com.gpt.code.data.entity.ChatMessageEntity;
import com.gpt.code.data.entity.ChatSessionEntity;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView rvMessages;
    private EditText etInput;
    private MaterialButton btnSend;
    private LinearProgressIndicator progressLoading;
    private MaterialToolbar toolbar;
    private ChatMessageAdapter adapter;
    private WorkspaceViewModel workspaceViewModel;
    private Dialog sessionDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMessages = view.findViewById(R.id.rvMessages);
        etInput = view.findViewById(R.id.etInput);
        btnSend = view.findViewById(R.id.btnSend);
        progressLoading = view.findViewById(R.id.progressLoading);
        toolbar = view.findViewById(R.id.toolbar);

        workspaceViewModel = new ViewModelProvider(requireActivity()).get(WorkspaceViewModel.class);

        adapter = new ChatMessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);

        workspaceViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
            rvMessages.scrollToPosition(adapter.getItemCount() - 1);
        });

        workspaceViewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            progressLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        workspaceViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && getView() != null) {
                Snackbar.make(getView(), error, Snackbar.LENGTH_LONG).show();
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_history) {
                showSessionListDialog();
                return true;
            } else if (id == R.id.action_new_chat) {
                workspaceViewModel.createNewSession();
                return true;
            }
            return false;
        });

        btnSend.setOnClickListener(v -> {
            String input = etInput.getText().toString().trim();
            if (!input.isEmpty()) {
                workspaceViewModel.sendMessage(input);
                etInput.setText("");
            }
        });
    }

    private void showSessionListDialog() {
        workspaceViewModel.getSessions().observe(getViewLifecycleOwner(), sessions -> {
            if (sessions == null || sessions.isEmpty()) {
                if (getContext() != null) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("历史会话")
                            .setMessage("暂无历史会话")
                            .setPositiveButton("确定", null)
                            .show();
                }
                return;
            }

            View dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_session_list, null);
            RecyclerView rvSessions = dialogView.findViewById(R.id.rvSessions);
            SessionListAdapter adapter = new SessionListAdapter();
            rvSessions.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvSessions.setAdapter(adapter);
            adapter.setSessions(sessions);

            sessionDialog = new MaterialAlertDialogBuilder(requireContext())
                    .setView(dialogView)
                    .setNegativeButton("关闭", null)
                    .create();

            adapter.setOnSessionClickListener(session -> {
                workspaceViewModel.loadSession(session.getId());
                if (sessionDialog != null) sessionDialog.dismiss();
            });

            sessionDialog.show();
        });
    }
}
