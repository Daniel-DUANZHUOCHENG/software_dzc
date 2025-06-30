<template>
  <div id="app">
    <TopBar v-if="!isLoginPage" />
    <div class="main-content" v-if="!isLoginPage">
      <router-view />
    </div>
    <router-view v-else />
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import TopBar from './pages/components/TopBar.vue';
import { computed } from 'vue';

const route = useRoute();

const isLoginPage = computed(() => route.path === '/' || route.path === '/register');
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
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  margin: 16px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: auto;
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
</style>

