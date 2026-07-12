document.addEventListener('deviceready', onDeviceReady, false);

function onDeviceReady() {
    console.log('Device is ready');
    initApp();
}

let apiConfig = {
    endpoint: '',
    apiKey: '',
    model: 'gpt-3.5-turbo'
};

function initApp() {
    loadConfig();
    setupEventListeners();
    loadChatHistory();
    updateUIState();
}

function loadConfig() {
    const saved = localStorage.getItem('apiConfig');
    if (saved) {
        apiConfig = JSON.parse(saved);
    }
}

function saveConfig() {
    localStorage.setItem('apiConfig', JSON.stringify(apiConfig));
}

function setupEventListeners() {
    document.getElementById('sendButton').addEventListener('click', sendMessage);
    document.getElementById('messageInput').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') sendMessage();
    });
    document.getElementById('settingsBtn').addEventListener('click', openSettings);
}

function updateUIState() {
    const sendBtn = document.getElementById('sendButton');
    const input = document.getElementById('messageInput');
    if (!apiConfig.endpoint || !apiConfig.apiKey) {
        input.placeholder = '请先配置 API 设置';
        sendBtn.disabled = true;
    } else {
        input.placeholder = '输入消息...';
        sendBtn.disabled = false;
    }
}

function openSettings() {
    const endpoint = prompt('API 端点 (例如: https://api.openai.com/v1)', apiConfig.endpoint);
    if (endpoint !== null) {
        const apiKey = prompt('API 密钥', apiConfig.apiKey);
        if (apiKey !== null) {
            const model = prompt('模型名称 (默认: gpt-3.5-turbo)', apiConfig.model || 'gpt-3.5-turbo');
            if (model !== null) {
                apiConfig.endpoint = endpoint || '';
                apiConfig.apiKey = apiKey || '';
                apiConfig.model = model || 'gpt-3.5-turbo';
                saveConfig();
                updateUIState();
            }
        }
    }
}

function addMessage(content, isUser) {
    const container = document.getElementById('messagesContainer');
    const msgDiv = document.createElement('div');
    msgDiv.className = `message ${isUser ? 'user' : 'ai'}`;
    msgDiv.textContent = content;
    container.appendChild(msgDiv);
    container.scrollTop = container.scrollHeight;
    saveMessage(content, isUser);
}

function saveMessage(content, isUser) {
    const history = getChatHistory();
    history.push({ content, isUser, timestamp: Date.now() });
    localStorage.setItem('chatHistory', JSON.stringify(history));
}

function getChatHistory() {
    const saved = localStorage.getItem('chatHistory');
    return saved ? JSON.parse(saved) : [];
}

function loadChatHistory() {
    const history = getChatHistory();
    history.forEach(msg => {
        const container = document.getElementById('messagesContainer');
        const msgDiv = document.createElement('div');
        msgDiv.className = `message ${msg.isUser ? 'user' : 'ai'}`;
        msgDiv.textContent = msg.content;
        container.appendChild(msgDiv);
    });
}

async function sendMessage() {
    const input = document.getElementById('messageInput');
    const content = input.value.trim();
    if (!content || !apiConfig.endpoint || !apiConfig.apiKey) return;

    addMessage(content, true);
    input.value = '';

    const typingDiv = document.createElement('div');
    typingDiv.className = 'typing-indicator';
    typingDiv.innerHTML = '<span></span><span></span><span></span>';
    document.getElementById('messagesContainer').appendChild(typingDiv);

    try {
        const response = await fetch(`${apiConfig.endpoint}/chat/completions`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${apiConfig.apiKey}`
            },
            body: JSON.stringify({
                model: apiConfig.model,
                messages: [{ role: 'user', content }]
            })
        });

        typingDiv.remove();

        if (!response.ok) {
            const errorDiv = document.createElement('div');
            errorDiv.className = 'error-message';
            errorDiv.textContent = `API 错误: ${response.status}`;
            document.getElementById('messagesContainer').appendChild(errorDiv);
            return;
        }

        const data = await response.json();
        const aiResponse = data.choices?.[0]?.message?.content || '未获取到回复';
        addMessage(aiResponse, false);
    } catch (error) {
        typingDiv.remove();
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.textContent = '网络错误: ' + error.message;
        document.getElementById('messagesContainer').appendChild(errorDiv);
    }
}