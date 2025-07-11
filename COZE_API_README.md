# Coze API 代理服务器使用说明

## 🎯 解决的问题

由于浏览器的CORS（跨域资源共享）限制，前端无法直接调用Coze API。本代理服务器解决了这个问题。

## 📋 快速开始

### 1. 启动代理服务器

**方法一：使用批处理脚本（推荐）**
```bash
# 双击运行
start-proxy.bat
```

**方法二：手动启动**
```bash
# 安装依赖
npm install express cors

# 启动服务器
node coze-proxy-server.js
```

### 2. 验证服务器状态

打开浏览器访问：`http://localhost:9049/health`

如果看到以下响应，说明服务器正常运行：
```json
{
  "status": "ok",
  "message": "Coze代理服务器运行正常",
  "port": 9049,
  "timestamp": "2024-01-01T12:00:00.000Z"
}
```

### 3. 测试API调用

打开 `test-coze-api.html` 进行测试：
- 检查代理服务器状态
- 测试API调用功能
- 查看详细日志

## 🔧 配置说明

### 代理服务器配置

- **端口**: 9049
- **CORS**: 允许所有来源
- **超时**: 30秒
- **日志**: 控制台输出

### API配置

```javascript
const COZE_CONFIG = {
  baseUrl: 'https://api.coze.cn/v1/chat',
  botId: '7522836555506614287',
  token: 'pat_XFJxVX7d9HlJ0YXaaDs4HRHr0v1CKAZq3MFshjAVY2jRKTjfkAzr9PgX31FUMe4X',
  timeout: 30000
};
```

## 📡 API调用方式

### 1. 通过代理服务器调用

```javascript
const response = await fetch('http://localhost:9049/api/coze-proxy', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    message: '你好',
    bot_id: '7522836555506614287',
    token: 'pat_XFJxVX7d9HlJ0YXaaDs4HRHr0v1CKAZq3MFshjAVY2jRKTjfkAzr9PgX31FUMe4X'
  })
});
```

### 2. 使用封装的AI模块

```javascript
import { callCozeAI } from './utils/coze-ai.js';

const result = await callCozeAI('你好，请介绍一下自己');
console.log(result.message);
```

## 🚀 多重调用策略

系统采用多重调用策略，确保高可用性：

1. **本地代理服务器** - 最优选择
2. **直接API调用** - 备用方案
3. **XMLHttpRequest** - 兼容性方案
4. **CORS代理服务** - 公共代理

## 📊 日志和调试

### 服务器日志
代理服务器会在控制台输出详细日志：
```
🔄 收到代理请求: { message: '你好', bot_id: '7522836555506614287' }
✅ Coze API响应: { data: { messages: [...] } }
```

### 前端日志
前端AI模块会输出调用过程：
```
🤖 开始AI对话 (独立模块): 你好
📡 尝试本地代理API调用
✅ 本地代理API调用成功
```

## ❓ 常见问题

### Q: 代理服务器无法启动
A: 
- 检查Node.js是否安装
- 确认端口9049未被占用
- 检查防火墙设置

### Q: API调用失败
A:
- 确认代理服务器正在运行
- 检查网络连接
- 验证API token是否有效

### Q: CORS错误
A:
- 确保使用代理服务器调用
- 检查代理服务器的CORS配置
- 尝试重启代理服务器

## 🛠️ 技术架构

```
前端页面 → AI模块 → 代理服务器 → Coze API
                 ↓
              多重备用方案
```

## 📝 文件说明

- `coze-proxy-server.js` - 代理服务器主文件
- `start-proxy.bat` - Windows启动脚本
- `utils/coze-ai.js` - 前端AI调用模块
- `test-coze-api.html` - API测试工具
- `COZE_API_README.md` - 本说明文档

## 🔒 安全注意事项

1. **Token保护**: 不要在前端直接暴露API token
2. **本地运行**: 代理服务器仅用于本地开发
3. **生产环境**: 生产环境需要专门的后端服务

## 📞 支持

如果遇到问题，请检查：
1. 代理服务器是否正常运行
2. 网络连接是否正常
3. 浏览器控制台是否有错误信息
4. API token是否有效

---

*最后更新: 2024年1月* 