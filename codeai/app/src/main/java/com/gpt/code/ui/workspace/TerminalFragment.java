package com.gpt.code.ui.workspace;

import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.gpt.code.R;

public class TerminalFragment extends Fragment {

    private RecyclerView rvOutput;
    private EditText etCommand;
    private MaterialButton btnExecute;
    private TerminalOutputAdapter adapter;
    private WorkspaceViewModel workspaceViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_terminal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvOutput = view.findViewById(R.id.rvOutput);
        etCommand = view.findViewById(R.id.etCommand);
        btnExecute = view.findViewById(R.id.btnExecute);

        workspaceViewModel = new ViewModelProvider(requireActivity()).get(WorkspaceViewModel.class);

        adapter = new TerminalOutputAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        rvOutput.setLayoutManager(layoutManager);
        rvOutput.setAdapter(adapter);

        workspaceViewModel.getTerminalOutput().observe(getViewLifecycleOwner(), lines -> {
            adapter.setLines(lines);
            rvOutput.scrollToPosition(adapter.getItemCount() - 1);
        });

        btnExecute.setOnClickListener(v -> {
            String command = etCommand.getText().toString().trim();
            if (!command.isEmpty()) {
                workspaceViewModel.executeTerminalCommand(command);
                etCommand.setText("");
            }
        });
    }
}
