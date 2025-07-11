<template>
  <div class="ai-chat-container">
    <!-- 顶部工具栏 -->
    <div class="chat-header">
      <div class="header-left">
        <div class="ai-selector">
          <el-select v-model="selectedAI" placeholder="选择AI模型" class="ai-select">
            <el-option
              v-for="ai in aiModels"
              :key="ai.id"
              :label="ai.name"
              :value="ai.id"
            />
          </el-select>
        </div>
        
        <el-button type="primary" :icon="Plus" @click="newConversation" class="new-chat-btn">
          <span>新对话</span>
        </el-button>
      </div>

      <div class="header-center">
        <div v-if="props.selectedResource?.name" class="current-doc">
          <el-icon class="doc-icon"><Document /></el-icon>
          <span>{{ props.selectedResource.name }}</span>
        </div>
      </div>
      
      <div class="header-right">
        <div class="user-info">
          <div class="user-avatar">
            <el-icon><User /></el-icon>
          </div>
          <span class="user-name">{{ userInfo.name || `用户ID: ${userInfo.id}` }}</span>
        </div>
      </div>
    </div>

    <!-- 对话消息区域 -->
    <div ref="messagesContainer" class="chat-messages">
      <div class="chat-background"></div>
      <div 
        v-for="(message, index) in messages" 
        :key="index" 
        :class="['message', message.role]"
        :style="{ animationDelay: `${index * 0.1}s` }"
      >
        <div class="message-wrapper">
          <div class="avatar">
            <el-icon v-if="message.role === 'user'" class="user-icon"><User /></el-icon>
            <div v-else class="ai-avatar">
              <img src="../../static/Logo2.png" alt="AI" />
            </div>
          </div>
          <div class="content">
            <!-- 使用新组件渲染内容 -->
            <ChatMessage v-if="message.content" :content="message.content" />
            
            <!-- 流式输出时的加载动画 -->
            <div v-if="isLoading && index === messages.length - 1" class="typing-indicator">
              <div class="typing-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
              <span class="typing-text">AI正在思考中...</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="messages.length === 0" class="empty-state">
        <div class="empty-icon">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <h3>开始与AI对话</h3>
        <p>选择一个AI模型，然后在下方输入您的问题</p>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <div class="input-container">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :autosize="{ minRows: 1, maxRows: 6 }"
          placeholder="输入消息，Shift + Enter 换行..."
          @keydown.enter.exact.prevent="sendMessage"
          @keydown.shift.enter.exact.prevent="inputMessage += '\n'"
          class="message-input"
        />
        <el-button 
          type="primary" 
          :disabled="!inputMessage.trim() || isLoading"
          @click="sendMessage"
          :icon="Promotion"
          class="send-btn"
          :loading="isLoading"
        >
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted, nextTick } from 'vue';
import { User, Promotion, Plus, Document, ChatDotRound } from '@element-plus/icons-vue';
import request from '../../utils/request'; // 假设你的请求拦截器在这里
import ChatMessage from './ChatMessage.vue'; // 引入消息组件

// 父组件传递的属性，增加了 apiKey
const props = defineProps({
  selectedResource: {
    type: Object,
    required: true,
    // 增加默认值防止初始报错
    default: () => ({ id: null, name: '', path: '', type: '', apiKey: '' }) 
  }
});

// 响应式数据
const inputMessage = ref('');
const messages = ref([]);
const isLoading = ref(false);
const selectedAI = ref('deepseek'); // 你可以根据需要绑定到请求参数
const aiModels = ref([
  { id: '1', name: 'deepseek' },
  { id: '2', name: '豆包' },
  { id: '3', name: '千问' },
]);
const messagesContainer = ref(null);
const conversationId = ref(null);
const userInfo = reactive({ id: 'default_user_123', name: '测试用户' });

const fileTypeMap = {
  'pdf-type': 'document',
  'mp4-type': 'document',
  'txt-type': 'document',
  'ppt-type': 'document',
  'pptx-type': 'document',
  'docx-type': 'document',
  'xlsx-type': 'document',
  'xls-type': 'document',
};


onMounted(async () => {
  // 模拟加载用户信息
  await loadUserInfo(); // 你的用户信息加载逻辑
});

// 加载用户信息
const loadUserInfo = async () => {
  // 从本地存储获取用户ID
  const userId = localStorage.getItem('userId') || 'default_user'
//   const response = await request.get('/study/getUser', { params: { userId } } );
//   userInfo.name = response.data.username;
  userInfo.id = userId;
}

const newConversation = () => {
  messages.value = [];
  conversationId.value = null;
  isLoading.value = false;
};

let eventSource = null; // 将 eventSource 提升到函数外部，以便在需要时可以从外部关闭

// 发送消息 - 已重构为支持流式响应
const sendMessage = async () => {
  const content = inputMessage.value.trim();
  if (!content || isLoading.value) return;

  // 如果上一次的流还未关闭，先强制关闭
  if (eventSource && eventSource.readyState !== EventSource.CLOSED) {
    eventSource.close();
  }

  // 1. 将用户消息添加到列表
  messages.value.push({ role: 'user', content });
  const prompt = content;
  inputMessage.value = '';

  // 2. 准备AI的响应占位符
  isLoading.value = true;
  messages.value.push({ role: 'assistant', content: '' });
  await nextTick(); // 确保DOM更新完毕
  scrollToBottom();

  try {
    // 3. 构造后端流式接口的URL
    const backendApiUrl = new URL(`${window.location.origin}/api/dify/chat`);

    backendApiUrl.searchParams.append('query', prompt);
    // backendApiUrl.searchParams.append('user', userInfo.id);
    backendApiUrl.searchParams.append('user', 666);
    if (conversationId.value) {
      backendApiUrl.searchParams.append('conversationId', conversationId.value);
    }
    // 4. 创建并连接 EventSource
    eventSource = new EventSource(backendApiUrl.href);



    // 5. 监听从后端发送的具名事件
    
    // 监听 'message' 事件，用于接收AI回复的文本片段
    eventSource.addEventListener('message', (event) => {
      const data = JSON.parse(event.data);
      if (data.answer) {
        messages.value[messages.value.length - 1].content += data.answer;
        scrollToBottom();
      }
      // 持续更新 conversationId
      if (data.conversationId) {
        // console.log('修改conversationId:', data.conversationId);
        conversationId.value = data.conversationId;
      }
    });

    // 监听 'workflow_finished' 或 'message_end' 事件，表示对话正常结束
    const handleStreamEnd = (event) => {
      console.log(`流已正常结束，事件: ${event.type}`);
      isLoading.value = false;
      eventSource.close();
    };
    eventSource.addEventListener('workflow_finished', handleStreamEnd);
    eventSource.addEventListener('message_end', handleStreamEnd);
    
    // 监听 'error' 事件，处理连接中断或后端抛出的错误
    eventSource.onerror = (error) => {
      console.error('EventSource 发生错误或连接被关闭:', error);
      const lastMessage = messages.value[messages.value.length - 1];
      if (lastMessage.content === '') {
        // 如果AI还没回复任何内容就出错了
        lastMessage.content = '抱歉，连接到服务时发生错误。';
      } else {
        // 如果是中途出错
        lastMessage.content += '\n\n[连接已中断]';
      }
      isLoading.value = false;
      eventSource.close();
    };

  } catch (error) {
    // 这个 catch 块主要捕获创建 EventSource 实例时的同步错误
    console.error('创建 EventSource 连接失败:', error);
    messages.value[messages.value.length - 1].content = `抱歉，无法连接到聊天服务：${error.message}`;
    isLoading.value = false;
  }
};


const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: 100%; /* 改为100%以适应父容器 */
  width: 100%;
  margin: 0;
  border-radius: 0; /* 去掉圆角，因为在弹窗中 */
  overflow: hidden;
  background: transparent; /* 透明背景 */
  box-shadow: none; /* 去掉阴影 */
  border: none; /* 去掉边框 */
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-selector {
  position: relative;
}

.ai-select {
  width: 140px; /* 减小宽度 */
}

.ai-select :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 2px solid transparent;
  background: linear-gradient(45deg, #f0f2f5, #ffffff);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.ai-select :deep(.el-input__wrapper):hover {
  border-color: #667eea;
  transform: translateY(-1px);
}

.new-chat-btn {
  border-radius: 8px;
  padding: 8px 16px; /* 减小padding */
  background: linear-gradient(45deg, #667eea, #764ba2);
  border: none;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  font-size: 14px;
}

.new-chat-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.current-doc {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 16px;
  color: #667eea;
  font-size: 12px;
  font-weight: 500;
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.doc-icon {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  color: #333;
  font-size: 12px;
  backdrop-filter: blur(10px);
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(45deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
}

.user-name {
  font-weight: 500;
}

.chat-messages {
  flex: 1;
  position: relative;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chat-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.05) 0%, transparent 50%);
  pointer-events: none;
}

.message {
  display: flex;
  max-width: 90%;
  animation: messageSlideIn 0.3s ease-out forwards;
  opacity: 0;
  transform: translateY(20px);
}

@keyframes messageSlideIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  align-self: flex-end;
}

.message.assistant {
  align-self: flex-start;
}

.message-wrapper {
  display: flex;
  gap: 12px;
  width: 100%;
}

.message.user .message-wrapper {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message.user .avatar {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
}

.ai-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(45deg, #f093fb, #f5576c);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 6px;
}

.ai-avatar img {
  width: 24px;
  height: 24px;
  border-radius: 50%;
}

.content {
  padding: 12px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  position: relative;
  min-width: 60px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.message.user .content {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
}

.message.user .content :deep(.markdown-body) {
  color: white;
}

.message.user .content :deep(.markdown-body code:not(pre > code)) {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  color: rgba(102, 126, 234, 0.6);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-state h3 {
  font-size: 18px;
  margin-bottom: 8px;
  font-weight: 600;
}

.empty-state p {
  font-size: 14px;
  opacity: 0.8;
}

.input-area {
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  flex-shrink: 0;
}

.input-container {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  max-width: 100%;
}

.message-input {
  flex: 1;
}

.message-input :deep(.el-textarea__inner) {
  border-radius: 16px;
  padding: 12px 16px;
  line-height: 1.5;
  resize: none;
  border: 2px solid transparent;
  background: rgba(248, 250, 252, 0.8);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  font-size: 14px;
}

.message-input :deep(.el-textarea__inner):focus {
  border-color: #667eea;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.send-btn {
  height: 40px;
  min-width: 70px;
  border-radius: 20px;
  background: linear-gradient(45deg, #667eea, #764ba2);
  border: none;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 14px;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 打字加载动画 */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
}

.typing-dots {
  display: flex;
  gap: 3px;
}

.typing-dots span {
  height: 6px;
  width: 6px;
  background: linear-gradient(45deg, #667eea, #764ba2);
  border-radius: 50%;
  display: inline-block;
  animation: wave 1.4s infinite ease-in-out;
}

.typing-dots span:nth-of-type(2) {
  animation-delay: -1.2s;
}

.typing-dots span:nth-of-type(3) {
  animation-delay: -1.0s;
}

.typing-text {
  font-size: 12px;
  color: #666;
  font-style: italic;
}

@keyframes wave {
  0%, 60%, 100% {
    transform: initial;
  }
  30% {
    transform: translateY(-8px);
  }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 2px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.5);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-header {
    padding: 8px 12px;
    flex-direction: column;
    gap: 8px;
  }
  
  .header-left,
  .header-center,
  .header-right {
    width: 100%;
    justify-content: center;
  }
  
  .message {
    max-width: 95%;
  }
  
  .input-area {
    padding: 12px;
  }
}
</style>