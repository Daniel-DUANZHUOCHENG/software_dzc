<template>
  <div class="chat-message-item">
    <div v-html="renderedContent" class="markdown-body"></div>
  </div>
</template>

<script setup>
import { computed, onMounted, nextTick, onUpdated } from 'vue';
import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';
import 'highlight.js/styles/github-dark.css'; // 更换为更现代的主题

const props = defineProps({
  content: {
    type: String,
    required: true,
  },
});

// 初始化Markdown渲染器
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        // 返回高亮后的HTML，并添加复制按钮
        const highlightedCode = hljs.highlight(str, { language: lang, ignoreIllegals: true }).value;
        return `
          <div class="code-block-wrapper">
            <div class="code-block-header">
              <div class="code-block-info">
                <div class="code-block-dots">
                  <span class="dot red"></span>
                  <span class="dot yellow"></span>
                  <span class="dot green"></span>
                </div>
                <span class="language-tag">${lang}</span>
              </div>
              <button class="copy-btn" data-code="${encodeURIComponent(str)}">
                <svg class="copy-icon" viewBox="0 0 24 24" width="16" height="16">
                  <path fill="currentColor" d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
                </svg>
                <span class="copy-text">复制</span>
              </button>
            </div>
            <pre class="hljs"><code>${highlightedCode}</code></pre>
          </div>
        `;
      } catch (__) {}
    }
    // 如果没有指定语言或高亮失败，则原样返回
    const escapedCode = md.utils.escapeHtml(str);
    return `
      <div class="code-block-wrapper">
        <div class="code-block-header">
          <div class="code-block-info">
            <div class="code-block-dots">
              <span class="dot red"></span>
              <span class="dot yellow"></span>
              <span class="dot green"></span>
            </div>
            <span class="language-tag">text</span>
          </div>
          <button class="copy-btn" data-code="${encodeURIComponent(str)}">
            <svg class="copy-icon" viewBox="0 0 24 24" width="16" height="16">
              <path fill="currentColor" d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
            </svg>
            <span class="copy-text">复制</span>
          </button>
        </div>
        <pre class="hljs"><code>${escapedCode}</code></pre>
      </div>
    `;
  },
});

// 渲染Markdown内容
const renderedContent = computed(() => md.render(props.content));

// DOM更新后，为复制按钮绑定事件
const addCopyEventListeners = () => {
  const messageElement = document.querySelectorAll('.chat-message-item');
  if (messageElement.length === 0) return;

  const lastMessage = messageElement[messageElement.length - 1]; // 只处理最新的消息
  const copyButtons = lastMessage.querySelectorAll('.copy-btn');
  
  copyButtons.forEach(button => {
    // 避免重复绑定
    if (button.dataset.listenerAttached) return;
    
    button.dataset.listenerAttached = 'true';
    button.onclick = async (e) => {
      e.preventDefault();
      const codeText = decodeURIComponent(button.dataset.code);
      const copyText = button.querySelector('.copy-text');
      const copyIcon = button.querySelector('.copy-icon');
      
      try {
        await navigator.clipboard.writeText(codeText);
        copyText.textContent = '已复制!';
        copyIcon.style.transform = 'scale(1.2)';
        button.classList.add('copied');
        
        setTimeout(() => {
          copyText.textContent = '复制';
          copyIcon.style.transform = 'scale(1)';
          button.classList.remove('copied');
        }, 2000);
      } catch (err) {
        console.error('复制失败:', err);
        copyText.textContent = '复制失败';
        setTimeout(() => {
          copyText.textContent = '复制';
        }, 2000);
      }
    };
  });
};

// 监听内容变化，并在DOM更新后添加事件监听
onMounted(() => {
  nextTick(addCopyEventListeners);
});

// 使用 onUpdated 钩子来处理流式消息更新后的 DOM
onUpdated(() => {
  nextTick(addCopyEventListeners);
});

</script>

<style scoped>
.chat-message-item {
  width: 100%;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

/* Markdown 渲染样式 */
.markdown-body {
  line-height: 1.6;
  font-size: 14px; /* 减小字体 */
  color: #2c3e50;
}

.markdown-body h1, 
.markdown-body h2, 
.markdown-body h3, 
.markdown-body h4, 
.markdown-body h5, 
.markdown-body h6 {
  margin-top: 16px; /* 减小间距 */
  margin-bottom: 12px;
  font-weight: 600;
  line-height: 1.25;
  color: #1a202c;
}

.markdown-body h1 {
  font-size: 1.6em; /* 减小标题字体 */
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 6px;
}

.markdown-body h2 {
  font-size: 1.3em;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 4px;
}

.markdown-body h3 {
  font-size: 1.1em;
}

.markdown-body p {
  margin-bottom: 12px; /* 减小段落间距 */
  text-align: justify;
}

.markdown-body ul, 
.markdown-body ol {
  padding-left: 1.5em; /* 减小缩进 */
  margin-bottom: 12px;
}

.markdown-body li {
  margin-bottom: 6px;
}

.markdown-body li > ul,
.markdown-body li > ol {
  margin-top: 6px;
  margin-bottom: 6px;
}

.markdown-body blockquote {
  border-left: 4px solid #667eea;
  padding: 12px 16px; /* 减小padding */
  margin: 12px 0;
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.05) 0%, rgba(102, 126, 234, 0.02) 100%);
  border-radius: 0 8px 8px 0;
  color: #4a5568;
  font-style: italic;
}

.markdown-body blockquote p {
  margin-bottom: 0;
}

.markdown-body table {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.markdown-body th,
.markdown-body td {
  padding: 8px 12px; /* 减小表格padding */
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
  font-size: 13px; /* 减小表格字体 */
}

.markdown-body th {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  font-weight: 600;
}

.markdown-body tr:nth-child(even) {
  background-color: #f8fafc;
}

.markdown-body tr:hover {
  background-color: #edf2f7;
}

.markdown-body a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
}

.markdown-body a:hover {
  color: #764ba2;
  text-decoration: underline;
}

.markdown-body code:not(pre > code) {
  background: linear-gradient(45deg, #f7fafc, #edf2f7);
  padding: 3px 6px; /* 减小padding */
  margin: 0 2px;
  font-size: 0.85em;
  border-radius: 4px;
  font-family: "SFMono-Regular", "Monaco", "Inconsolata", "Liberation Mono", "Consolas", monospace;
  color: #e53e3e;
  border: 1px solid #e2e8f0;
  font-weight: 500;
}

.markdown-body strong {
  font-weight: 700;
  color: #1a202c;
}

.markdown-body em {
  font-style: italic;
  color: #4a5568;
}

.markdown-body hr {
  border: none;
  height: 2px;
  background: linear-gradient(90deg, transparent, #e2e8f0, transparent);
  margin: 16px 0;
}

/* 代码块包裹器样式 */
.code-block-wrapper {
  position: relative;
  background: #0d1117;
  border-radius: 8px; /* 减小圆角 */
  margin: 12px 0; /* 减小间距 */
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  border: 1px solid #30363d;
}

.code-block-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px; /* 减小padding */
  background: #161b22;
  border-bottom: 1px solid #30363d;
}

.code-block-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.code-block-dots {
  display: flex;
  gap: 4px;
}

.dot {
  width: 10px; /* 减小点的大小 */
  height: 10px;
  border-radius: 50%;
  opacity: 0.8;
}

.dot.red {
  background: #ff5f56;
}

.dot.yellow {
  background: #ffbd2e;
}

.dot.green {
  background: #27ca3f;
}

.language-tag {
  color: #8b949e;
  font-size: 11px; /* 减小字体 */
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.copy-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: #21262d;
  color: #8b949e;
  border: 1px solid #30363d;
  padding: 4px 8px; /* 减小padding */
  border-radius: 4px;
  cursor: pointer;
  font-size: 11px; /* 减小字体 */
  transition: all 0.2s ease;
  font-weight: 500;
}

.copy-btn:hover {
  background: #30363d;
  color: #c9d1d9;
  border-color: #8b949e;
  transform: translateY(-1px);
}

.copy-btn.copied {
  background: #238636;
  color: white;
  border-color: #238636;
}

.copy-icon {
  transition: transform 0.2s ease;
}

.copy-text {
  font-size: 11px;
}

/* highlight.js 样式覆盖 */
.hljs {
  padding: 12px; /* 减小padding */
  background: #0d1117 !important;
  border-radius: 0;
  overflow-x: auto;
  font-family: "SFMono-Regular", "Monaco", "Inconsolata", "Liberation Mono", "Consolas", monospace;
  font-size: 13px; /* 减小字体 */
  line-height: 1.5;
  color: #c9d1d9;
}

.hljs::-webkit-scrollbar {
  height: 6px;
}

.hljs::-webkit-scrollbar-track {
  background: #161b22;
  border-radius: 3px;
}

.hljs::-webkit-scrollbar-thumb {
  background: #30363d;
  border-radius: 3px;
}

.hljs::-webkit-scrollbar-thumb:hover {
  background: #484f58;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .markdown-body {
    font-size: 13px;
  }
  
  .code-block-header {
    padding: 6px 8px;
  }
  
  .hljs {
    padding: 8px;
    font-size: 12px;
  }
  
  .copy-btn {
    padding: 3px 6px;
    font-size: 10px;
  }
  
  .language-tag {
    font-size: 10px;
  }
}

/* 打印样式 */
@media print {
  .code-block-header {
    display: none;
  }
  
  .code-block-wrapper {
    box-shadow: none;
    border: 1px solid #ccc;
  }
  
  .hljs {
    background: white !important;
    color: black !important;
  }
}
</style>