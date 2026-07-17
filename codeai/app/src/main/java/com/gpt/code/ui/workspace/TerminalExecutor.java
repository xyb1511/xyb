package com.gpt.code.ui.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class TerminalExecutor {

    public interface OutputListener {
        void onOutput(String line);
        void onComplete(int exitCode);
        void onError(String error);
    }

    private Process currentProcess;
    private boolean running;

    public void execute(String command, String workingDir, OutputListener listener) {
        if (running) {
            listener.onError("已有命令正在执行中");
            return;
        }

        running = true;

        new Thread(() -> {
            try {
                ProcessBuilder builder = new ProcessBuilder("sh", "-c", command);
                builder.directory(new File(workingDir));
                builder.redirectErrorStream(true);

                currentProcess = builder.start();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(currentProcess.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    listener.onOutput(line);
                }

                int exitCode = currentProcess.waitFor();
                listener.onComplete(exitCode);
                running = false;

            } catch (Exception e) {
                listener.onError("命令执行异常: " + e.getMessage());
                running = false;
            }
        }).start();
    }

    public void cancel() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
            running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }
}
