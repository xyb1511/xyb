package com.gpt.code;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.gpt.code.ui.project.ProjectListFragment;
import com.gpt.code.ui.settings.SettingsFragment;
import com.gpt.code.ui.workspace.WorkspaceFragment;

public class MainActivity extends AppCompatActivity implements ProjectListFragment.OnProjectSelectedListener {

    private ProjectListFragment projectListFragment;
    private SettingsFragment settingsFragment;
    private BottomAppBar bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        projectListFragment = new ProjectListFragment();
        settingsFragment = new SettingsFragment();

        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.projectListFragment) {
                showFragment(projectListFragment);
                return true;
            } else if (id == R.id.settingsFragment) {
                showFragment(settingsFragment);
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            showFragment(projectListFragment);
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onProjectSelected(String projectId, String projectName) {
        WorkspaceFragment workspaceFragment = new WorkspaceFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        args.putString("projectName", projectName);
        workspaceFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, workspaceFragment)
                .addToBackStack(null)
                .commit();
    }
}
