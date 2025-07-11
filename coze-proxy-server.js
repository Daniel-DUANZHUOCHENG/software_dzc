/**
 * Coze API 代理服务器
 * 用于解决浏览器CORS跨域问题
 * 
 * 使用方法：
 * 1. 安装依赖: npm install express cors
 * 2. 运行服务器: node coze-proxy-server.js
 * 3. 服务器将在 http://localhost:9049 启动
 */

const express = require('express');
const cors = require('cors');
const https = require('https');
const http = require('http');

const app = express();
const PORT = 9049;

// 启用CORS
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization', 'X-Requested-With']
}));

// 解析JSON请求体
app.use(express.json());

// 代理路由
app.post('/api/coze-proxy', (req, res) => {
  const { message, bot_id, token } = req.body;
  
  console.log('🔄 收到代理请求:', { message, bot_id });
  
  // 构建请求数据
  const requestData = JSON.stringify({
    bot_id: bot_id,
    user_id: "user_" + Date.now(),
    stream: false,
    auto_save_history: true,
    additional_messages: [
      {
        role: "user",
        content: message,
        content_type: "text"
      }
    ]
  });
  
  // 请求选项
  const options = {
    hostname: 'api.coze.cn',
    port: 443,
    path: '/v1/chat',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      'Accept': 'application/json',
      'Content-Length': Buffer.byteLength(requestData)
    }
  };
  
  // 发送请求到Coze API
  const apiReq = https.request(options, (apiRes) => {
    let data = '';
    
    apiRes.on('data', (chunk) => {
      data += chunk;
    });
    
    apiRes.on('end', () => {
      try {
        const jsonData = JSON.parse(data);
        console.log('✅ Coze API响应:', jsonData);
        
        // 解析响应
        let aiMessage = '';
        if (jsonData.data && jsonData.data.messages) {
          const assistantMessage = jsonData.data.messages.find(msg => msg.role === 'assistant');
          if (assistantMessage && assistantMessage.content) {
            aiMessage = assistantMessage.content;
          }
        }
        
        if (aiMessage) {
          res.json({
            success: true,
            message: aiMessage,
            source: 'coze_api'
          });
        } else {
          res.json({
            success: false,
            message: '抱歉，AI服务暂时无法响应，请稍后再试。',
            error: 'No assistant message found'
          });
        }
        
      } catch (error) {
        console.error('❌ 解析响应失败:', error);
        res.json({
          success: false,
          message: '抱歉，处理AI响应时出现错误。',
          error: error.message
        });
      }
    });
  });
  
  apiReq.on('error', (error) => {
    console.error('❌ API请求失败:', error);
    res.json({
      success: false,
      message: '抱歉，无法连接到AI服务。',
      error: error.message
    });
  });
  
  // 发送请求数据
  apiReq.write(requestData);
  apiReq.end();
});

// 健康检查
app.get('/health', (req, res) => {
  res.json({ 
    status: 'ok', 
    message: 'Coze代理服务器运行正常',
    port: PORT,
    timestamp: new Date().toISOString()
  });
});

// 启动服务器
app.listen(PORT, () => {
  console.log(`🚀 Coze代理服务器已启动:`);
  console.log(`   - 地址: http://localhost:${PORT}`);
  console.log(`   - 代理接口: http://localhost:${PORT}/api/coze-proxy`);
  console.log(`   - 健康检查: http://localhost:${PORT}/health`);
  console.log(`   - 时间: ${new Date().toLocaleString()}`);
});

// 优雅关闭
process.on('SIGINT', () => {
  console.log('\n🛑 正在关闭代理服务器...');
  process.exit(0);
});

process.on('SIGTERM', () => {
  console.log('\n🛑 正在关闭代理服务器...');
  process.exit(0);
}); 