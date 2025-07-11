<template>
  <div id="app">
    <TopBar v-if="!isLoginPage" />
    <div class="main-content" v-if="!isLoginPage">
      <router-view />
    </div>
    <router-view v-else />
  </div>

  <div class="floating-container">
    <!-- 悬浮按钮 -->
    <button class="floating-button" @click="showPopup = true">
      <svg class="ai-icon" viewBox="0 0 24 24" width="24" height="24">
        <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
      </svg>
      <span class="button-text">AI</span>
    </button>

    <!-- 可拖拽的弹窗 -->
    <div
      v-if="showPopup"
      class="popup-modal"
      :style="{ top: popupPosition.y + 'px', left: popupPosition.x + 'px' }"
      ref="popupRef"
    >
      <div
        class="popup-header"
        @mousedown="startDrag"
      >
        <div class="header-left">
          <div class="ai-avatar">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
          </div>
          <div class="header-info">
            <span class="header-title">AI智能助手</span>
            <span class="header-status">在线</span>
          </div>
        </div>
        <div class="header-actions">
          <button class="minimize-btn" @click="minimizePopup" title="最小化">
            <svg viewBox="0 0 24 24" width="16" height="16">
              <path fill="currentColor" d="M6 19h12v2H6v-2z"/>
            </svg>
          </button>
          <button class="close-btn" @click="showPopup = false" title="关闭">
            <svg viewBox="0 0 24 24" width="16" height="16">
              <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
          </button>
        </div>
      </div>
      <!-- 内容 -->
      <div class="popup-content">
        <AiChat :selectedResource="{ id: null, name: '', path: '', type: '', apiKey: '' }" />
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import TopBar from './pages/components/TopBar.vue';
import { computed } from 'vue';
import AiChat from './pages/AI/AiChat.vue';

const route = useRoute();

const isLoginPage = computed(() => route.path === '/' || route.path === '/register');

import { ref } from 'vue';

// 控制弹窗的显示/隐藏
const showPopup = ref(false);

// 弹窗的位置
const popupPosition = ref({ x: window.innerWidth - 550, y: 50 });

// 拖拽相关状态
const isDragging = ref(false);
const dragStartOffset = ref({ x: 0, y: 0 });
const popupRef = ref(null); // 用于获取弹窗元素的引用

// 鼠标按下，开始拖拽
const startDrag = (event) => {
  isDragging.value = true;
  // 获取弹窗的 DOM 元素
  const popupElement = popupRef.value;
  // 计算鼠标点击位置相对于弹窗左上角的偏移
  dragStartOffset.value.x = event.clientX - popupElement.offsetLeft;
  dragStartOffset.value.y = event.clientY - popupElement.offsetTop;

  // 添加全局事件监听器
  document.addEventListener('mousemove', onDrag);
  document.addEventListener('mouseup', stopDrag);
};

// 鼠标移动，执行拖拽
const onDrag = (event) => {
  if (isDragging.value) {
    // 计算弹窗新的左上角位置
    let newX = event.clientX - dragStartOffset.value.x;
    let newY = event.clientY - dragStartOffset.value.y;
    
    // 限制弹窗不能拖出屏幕（调整高度限制为750px）
    newX = Math.max(0, Math.min(newX, window.innerWidth - 500));
    newY = Math.max(0, Math.min(newY, window.innerHeight - 750));
    
    popupPosition.value.x = newX;
    popupPosition.value.y = newY;
  }
};

// 鼠标松开，停止拖拽
const stopDrag = () => {
  isDragging.value = false;
  // 移除全局事件监听器，避免不必要的性能消耗
  document.removeEventListener('mousemove', onDrag);
  document.removeEventListener('mouseup', stopDrag);
};

// 最小化弹窗
const minimizePopup = () => {
  showPopup.value = false;
};

</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  padding: 24px;
  margin: 16px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: auto;
  position: relative;
  z-index: 1;
}

.main-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  z-index: -1;
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Element Plus 组件样式覆盖 */
.el-card {
  border-radius: 12px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;
  border: none !important;
}

.el-button {
  border-radius: 8px !important;
  font-weight: 500 !important;
}

.el-input__inner {
  border-radius: 8px !important;
}

.el-table {
  border-radius: 12px !important;
  overflow: hidden !important;
}

.el-pagination {
  margin-top: 20px;
  text-align: center;
}

/* 悬浮按钮的样式 */
.floating-button {
  position: fixed;
  bottom: 40px;
  right: 40px;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  border: none;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
  cursor: pointer;
  z-index: 999;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  backdrop-filter: blur(10px);
}

.floating-button:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.4);
}

.floating-button:active {
  transform: translateY(-2px) scale(1.02);
}

.ai-icon {
  opacity: 0.9;
}

.button-text {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

/* 弹窗的样式 */
.popup-modal {
  position: fixed;
  width: 500px;
  height: 900px; /* 从600px增加到750px */
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  display: flex;
  flex-direction: column;
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  animation: popupSlideIn 0.3s ease-out;
}

@keyframes popupSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 弹窗头部的样式 */
.popup-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  cursor: move;
  display: flex;
  justify-content: space-between;
  align-items: center;
  user-select: none;
  transition: background 0.3s ease;
}

.popup-header:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(45deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.header-title {
  font-weight: 600;
  font-size: 16px;
  color: #2c3e50;
}

.header-status {
  font-size: 12px;
  color: #27ae60;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-status::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #27ae60;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(39, 174, 96, 0.7);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(39, 174, 96, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(39, 174, 96, 0);
  }
}

.header-actions {
  display: flex;
  gap: 8px;
}

.minimize-btn,
.close-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: #666;
}

.minimize-btn:hover {
  background: rgba(255, 193, 7, 0.2);
  color: #ffc107;
  transform: scale(1.1);
}

.close-btn:hover {
  background: rgba(220, 53, 69, 0.2);
  color: #dc3545;
  transform: scale(1.1);
}

.popup-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.popup-content > * {
  flex: 1;
  height: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .popup-modal {
    width: calc(100vw - 40px);
    height: calc(100vh - 80px); /* 调整移动端高度，给顶部留更少空间 */
    left: 20px !important;
    top: 40px !important; /* 调整顶部位置 */
  }
  
  .floating-button {
    width: 60px;
    height: 60px;
    bottom: 20px;
    right: 20px;
  }
}
</style>

