# 需求实施计划

- [ ] 1. 更新项目依赖和基础架构
  - 在 app/build.gradle 中添加 Material Design 3 (com.google.android.material)、Navigation、ViewModel/LiveData、Room、OkHttp、Gson 依赖
  - 在 app/build.gradle 中启用 ViewBinding 和 Room schema 注解处理
  - 创建 res/values/themes.xml，配置 Material 3 主题（亮色/暗色）
  - 创建 res/values-v31/themes.xml，配置 Android 12+ Dynamic Color 支持
  - 更新 AndroidManifest.xml，添加 INTERNET 权限和 Application 主题引用

- [ ] 2. 设置 Navigation 导航结构
  - 在 res/navigation/ 中创建 nav_graph.xml，定义 ProjectListFragment、SettingsFragment、WorkspaceFragment 三个目的地，通过 argument 传递 projectId
  - 修改 activity_main.xml，使用 FragmentContainerView + BottomNavigationView 作为主导航
  - 更新 MainActivity.java，集成 NavController 和 BottomNavigationView 联动
  - 创建 ProjectListFragment、SettingsFragment、WorkspaceFragment 三个空壳 Fragment

- [ ] 3. 实现数据模型
  - [ ] 3.1 创建 AiConfig 实体类（com.gpt.code.ai.config 包），字段：id(String)、alias(String)、apiKey(String)、baseUrl(String)、modelName(String)，实现序列化为 JSON 的方法
  - [ ] 3.2 创建 Project 实体类（com.gpt.code.project 包），字段：id(String)、name(String)、rootPath(String)、importTime(long)，实现序列化为 JSON 的方法
  - [ ] 3.3 创建 Room 数据库实体类（com.gpt.code.data.entity 包）：ChatSessionEntity（id, projectId, title, createdAt）、ChatMessageEntity（id, sessionId, role, content, timestamp）
  - [ ] 3.4 创建 Room DAO 接口：SessionDao（增删查）、MessageDao（按 sessionId 查询消息列表、插入消息）
  - [ ] 3.5 创建 ChatDatabase Room 数据库类，定义版本号和迁移策略

- [x] 3.6 为数据模型编写单元测试
  - 测试 AiConfig JSON 序列化/反序列化正确性
  - 测试 Project JSON 序列化/反序列化正确性
  - 测试 Room 数据库基本 CRUD 操作

- [ ] 4. 实现数据存储层
  - [ ] 4.1 创建 AiConfigRepository（com.gpt.code.ai.config 包），封装 SharedPreferences 读写：增删改查配置列表、获取/设置当前活跃配置
  - [ ] 4.2 创建 ProjectRepository（com.gpt.code.project 包），封装 SharedPreferences 读写：增删查项目列表，添加方法 isValidProject() 校验项目目录是否包含 build.gradle
  - [ ] 4.3 创建 ChatRepository（com.gpt.code.data 包），封装 Room 数据库操作：创建会话、查询历史会话、添加消息、查询会话消息

- [ ] 5. 实现设置页面（AI 配置管理）— 对应需求 1
  - [ ] 5.1 创建 fragment_settings.xml 布局：RecyclerView 配置列表 + Material 3 FAB 添加按钮 + EmptyState 引导视图
  - [ ] 5.2 创建 item_ai_config.xml 列表项布局：配置别名、模型名称、当前活跃标记（RadioButton）、编辑/删除按钮
  - [ ] 5.3 创建 dialog_add_ai_config.xml 表单对话框布局：别名、API Key、Base URL、模型名称 四个 TextInputLayout
  - [ ] 5.4 创建 AiConfigAdapter RecyclerView 适配器，处理配置项展示、选择活跃配置、长按弹出编辑/删除菜单
  - [ ] 5.5 创建 AddEditConfigDialog 对话框 Fragment，处理新增和编辑两种模式
  - [ ] 5.6 创建 SettingsViewModel，连接 AiConfigRepository，管理配置列表 LiveData 和 CRUD 操作
  - [ ] 5.7 完善 SettingsFragment，绑定 ViewModel，处理配置选择、添加、编辑、删除事件流

- [x] 5.8 为设置页面编写 Espresso UI 测试
  - 添加配置的完整流程测试
  - 编辑已有配置测试
  - 删除配置测试

- [ ] 6. 检查点 - 确保设置页功能完整可用，进入项目列表开发

- [ ] 7. 实现项目列表页面 — 对应需求 2
  - [ ] 7.1 创建 fragment_project_list.xml 布局：RecyclerView 项目列表 + Material 3 FAB 导入按钮 + EmptyState 首次引导视图
  - [ ] 7.2 创建 item_project.xml 列表项布局：项目名称、路径摘要、导入时间
  - [ ] 7.3 创建 ProjectAdapter RecyclerView 适配器，处理项目点击进入和长按移除
  - [ ] 7.4 创建 ProjectListViewModel，连接 ProjectRepository，管理项目列表 LiveData 和导入/移除操作
  - [ ] 7.5 完善 ProjectListFragment，绑定 ViewModel，处理导入项目（使用 SAF 文件选择器）、校验项目有效性、导航进入工作区

- [x] 7.6 为项目列表页面编写 Espresso UI 测试
  - 导入有效项目测试
  - 导入无效目录（无 build.gradle）错误提示测试
  - 移除项目测试

- [ ] 8. 检查点 - 确保项目列表功能完整可用，进入工作区开发

- [ ] 9. 实现 AI 网络服务 — 对应需求 3
  - [ ] 9.1 创建 AiService（com.gpt.code.ai.service 包），使用 OkHttp 封装 OpenAI 兼容 Chat Completion API 调用，支持流式 SSE 响应
  - [ ] 9.2 创建 ChatCompletionRequest 请求模型类：model(String)、messages(List<Message>)、stream(boolean)
  - [ ] 9.3 创建 ChatCompletionResponse 响应模型类：choices(List<Choice>) 含 delta/message 内容
  - [ ] 9.4 创建 SseStreamParser 工具类，逐行解析 SSE text/event-stream 响应，提取 data 字段 JSON
  - [ ] 9.5 在 AiService 中实现 sendMessage() 方法，传入 AiConfig + 消息列表 + 项目上下文（文件结构），返回 LiveData 流式响应

- [ ] 10. 实现项目工作区页面 — 对应需求 3、4、5
  - [ ] 10.1 创建 fragment_workspace.xml 布局：ViewPager2/TabLayout 三栏结构（聊天、文件、终端）+ 可左右滑动切换
  - [ ] 10.2 创建 WorkspaceViewModel，管理项目路径、当前会话、AI 服务调用协调

  - [ ] 10.3 实现聊天子面板（ChatPanelView / ChatFragment）— 对应需求 3
    - 创建 fragment_chat.xml：RecyclerView 消息列表 + 底部输入框 + 发送按钮，使用 Material 3 样式
    - 创建 item_message_user.xml 和 item_message_assistant.xml 消息气泡布局
    - 创建 ChatAdapter RecyclerView 适配器，处理用户消息（右对齐气泡）和 AI 消息（左对齐气泡 + 代码块高亮）
    - 创建 ChatViewModel，管理消息列表 LiveData、发送消息、接收流式 AI 响应、文件变更 diff 预览状态
    - 创建 DiffPreviewDialog 对话框，展示 AI 要修改的文件的 diff 对比，用户确认/拒绝后执行
    - 完善 ChatFragment，绑定 ViewModel，处理消息发送、流式内容更新、diff 确认、代码块渲染

  - [ ] 10.4 实现文件浏览器子面板（FileExplorerFragment）— 对应需求 4
    - 创建 fragment_file_explorer.xml：RecyclerView 文件/目录列表 + 返回上级目录按钮
    - 创建 FileExplorerAdapter，处理目录和文件的区分展示（图标 + 名称 + 展开折叠）
    - 创建 FileSystemManager 工具类，封装 java.io.File 的目录遍历、文件读取、文件写入操作
    - 创建 FileExplorerViewModel，管理当前目录路径 LiveData 和文件列表状态
    - 完善 FileExplorerFragment，绑定 ViewModel，处理目录导航、文件点击打开编辑器

  - [ ] 10.5 实现代码编辑器子面板（CodeEditorFragment）— 对应需求 4
    - 创建 fragment_code_editor.xml：ScrollView + EditText（等宽字体、行号显示、语法高亮基础支持）
    - 创建 CodeEditorFragment，加载指定文件内容到编辑器，提供保存按钮
    - 创建 CodeEditorViewModel，管理当前编辑文件路径、文件内容和保存状态

  - [ ] 10.6 实现终端子面板（TerminalFragment）— 对应需求 5
    - 创建 fragment_terminal.xml：RecyclerView 输出历史 + 底部命令输入框
    - 创建 TerminalExecutor 工具类，使用 ProcessBuilder 在项目根目录执行 shell 命令，异步读取 stdout/stderr
    - 创建 TerminalViewModel，管理命令执行历史 LiveData 和执行状态
    - 完善 TerminalFragment，绑定 ViewModel，处理命令提交、实时输出展示、超时终止

- [ ] 11. 检查点 - 确保工作区完整功能可用，进行整体端到端验证

- [ ] 12. 实现会话历史管理 — 对应需求 6
  - 创建 dialog_session_list.xml：RecyclerView 会话列表（标题 + 创建时间）
  - 创建 SessionListAdapter，处理会话选择（还原历史）和新建会话
  - 在 WorkspaceFragment 中添加历史会话按钮，点击弹出 SessionListDialog
  - 在 ChatViewModel 中集成 ChatRepository，实现会话保存、加载、新建功能
  - 在 WorkspaceFragment 初始化时自动加载最近会话或创建新会话
