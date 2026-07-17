package com.gpt.code.ui.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileSystemManager {

    public static class FileItem {
        public String name;
        public String path;
        public boolean isDirectory;

        public FileItem(String name, String path, boolean isDirectory) {
            this.name = name;
            this.path = path;
            this.isDirectory = isDirectory;
        }
    }

    public List<FileItem> listDirectory(String path) {
        List<FileItem> items = new ArrayList<>();
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) return items;

        File[] files = dir.listFiles();
        if (files == null) return items;

        Arrays.sort(files, (a, b) -> {
            if (a.isDirectory() && !b.isDirectory()) return -1;
            if (!a.isDirectory() && b.isDirectory()) return 1;
            return a.getName().compareToIgnoreCase(b.getName());
        });

        for (File file : files) {
            items.add(new FileItem(file.getName(), file.getAbsolutePath(), file.isDirectory()));
        }
        return items;
    }

    public String readFile(String path) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            return null;
        }
        return sb.toString();
    }

    public boolean writeFile(String path, String content) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String getProjectFileContext(String projectPath) {
        StringBuilder sb = new StringBuilder();
        sb.append("项目文件结构:\n");
        appendDirTree(sb, new File(projectPath), 0, 3);
        return sb.toString();
    }

    private void appendDirTree(StringBuilder sb, File dir, int depth, int maxDepth) {
        if (depth >= maxDepth) return;
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.getName().startsWith(".") || file.getName().equals("build")) continue;
            StringBuilder indentBuilder = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                indentBuilder.append("  ");
            }
            String indent = indentBuilder.toString();
            if (file.isDirectory()) {
                sb.append(indent).append("[").append(file.getName()).append("]\n");
                appendDirTree(sb, file, depth + 1, maxDepth);
            } else {
                sb.append(indent).append("- ").append(file.getName()).append("\n");
            }
        }
    }
}
