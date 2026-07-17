# Requirements Document

## Introduction

AI 编程助手是一个 Android 原生应用，将 AIDEpro 风格的项目管理与远程大模型 AI 编程能力相结合。用户可以在应用中管理 Android 项目、配置多个 AI 模型，并让 AI 在项目中进行完整的代码编写、构建和版本控制操作。

## Glossary

- **AI 配置**：用户保存的 AI 大模型连接信息，包括 API Key、Base URL、模型名称等参数
- **项目**：用户设备上的 Android 项目目录，包含完整的 Gradle 构建结构和源代码
- **AI 会话**：用户与 AI 模型之间的一次交互对话，AI 可以在此对话中对项目执行编程操作
- **终端**：应用内嵌的命令行终端，AI 可通过终端执行构建、测试等命令

## Requirements

### Requirement 1: AI 配置管理

**User Story:** AS 开发者，I want 在设置页面中管理多个 AI 模型配置，SO that 我可以灵活切换不同的大模型进行编程。

#### Acceptance Criteria

1. WHEN 用户打开设置页面，系统 SHALL 展示已保存的所有 AI 配置列表。
2. WHEN 用户点击"添加配置"按钮，系统 SHALL 展示配置表单，包含 API Key、Base URL、模型名称、配置别名四个输入字段。
3. WHEN 用户填写配置表单并点击保存，系统 SHALL 校验所有必填字段非空，校验通过后持久化存储配置。
4. IF 用户填写的 Base URL 格式无效，系统 SHALL 提示 URL 格式错误并要求修正。
5. WHEN 用户在配置列表中长按某条配置，系统 SHALL 提供编辑和删除操作选项。
6. WHEN 系统启动时，系统 SHALL 自动加载上次使用的 AI 配置作为当前活跃配置。

### Requirement 2: 项目管理主界面

**User Story:** AS 开发者，I want 在主界面浏览和管理我的所有 Android 项目，SO that 我可以快速定位和进入需要 AI 协助的项目。

#### Acceptance Criteria

1. WHEN 用户打开应用，系统 SHALL 在主界面展示设备上的 Android 项目列表。
2. WHEN 用户点击"导入项目"按钮，系统 SHALL 打开系统文件选择器，允许用户选择项目根目录。
3. IF 用户选择的目录不是有效的 Android 项目（缺少 build.gradle），系统 SHALL 提示并拒绝导入。
4. WHEN 用户在项目列表中点击某个项目，系统 SHALL 进入该项目的工作界面。
5. WHEN 用户在项目列表长按某个项目，系统 SHALL 提供移除（不删除文件）此项目的选项。
6. WHEN 用户首次启动应用且无已导入项目，系统 SHALL 展示引导提示指导用户导入项目。

### Requirement 3: AI 聊天编程界面

**User Story:** AS 开发者，I want 在项目工作区与 AI 进行对话编程，SO that AI 可以理解我的需求并对项目代码进行实际的读写和修改操作。

#### Acceptance Criteria

1. WHEN 用户进入项目工作区，系统 SHALL 展示聊天界面，包含历史消息列表和底部输入区域。
2. WHEN 用户发送编程指令，系统 SHALL 将指令与当前项目的文件结构和上下文一起发送到已配置的 AI 模型。
3. WHEN AI 返回响应，系统 SHALL 在聊天界面中展示 AI 回复，AI 回复中的代码块使用语法高亮渲染。
4. WHEN AI 需要修改项目文件，系统 SHALL 在聊天消息中展示文件变更预览（diff 视图），并在用户确认后执行实际文件写入。
5. IF AI API 调用失败（网络错误、认证失败等），系统 SHALL 在聊天界面中展示友好的错误提示并允许用户重试。
6. WHILE AI 正在处理请求，系统 SHALL 展示加载状态指示器并禁用输入区域。

### Requirement 4: 项目文件浏览与编辑

**User Story:** AS 开发者，I want 在应用中浏览和手动编辑项目文件，SO that 我可以在 AI 协助之外自行修改代码。

#### Acceptance Criteria

1. WHEN 用户进入项目工作区，系统 SHALL 提供文件浏览器面板，以树形结构展示项目的目录和文件。
2. WHEN 用户点击某个代码文件，系统 SHALL 在内置代码编辑器中打开该文件，支持语法高亮和行号显示。
3. WHEN 用户在编辑器中修改代码并保存，系统 SHALL 将修改写入原文件。
4. WHEN AI 修改了项目文件，系统 SHALL 自动刷新文件浏览器和已打开的编辑器内容。

### Requirement 5: 内置终端

**User Story:** AS 开发者，I want 在应用内使用终端执行命令，SO that 我可以进行 Gradle 构建、测试和版本控制操作。

#### Acceptance Criteria

1. WHEN 用户进入项目工作区，系统 SHALL 提供可切换的终端面板。
2. WHEN 用户在终端中输入命令，系统 SHALL 在项目根目录下执行该命令并实时显示输出。
3. WHEN 命令执行完成，系统 SHALL 显示退出码和在聊天界面中通知 AI 命令执行结果。
4. IF 命令执行超时（超过 120 秒），系统 SHALL 自动终止命令并提示用户。

### Requirement 6: 会话历史管理

**User Story:** AS 开发者，I want 保存和查看历史 AI 编程会话，SO that 我可以回顾之前的编程过程。

#### Acceptance Criteria

1. WHEN 用户与 AI 进行对话，系统 SHALL 自动将对话保存到当前项目的会话历史中。
2. WHEN 用户打开项目的会话列表，系统 SHALL 按时间倒序展示历史会话。
3. WHEN 用户点击某条历史会话，系统 SHALL 还原该会话的完整对话内容。
4. WHEN 用户点击"新建会话"，系统 SHALL 创建空白的对话窗口，不清除历史会话。
