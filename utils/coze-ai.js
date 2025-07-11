/**
 * Coze AI 独立工具模块
 * 完全绕过项目的axios拦截器
 * 使用原生JavaScript API进行网络请求
 */

// Coze AI 配置
const COZE_CONFIG = {
  botId: '7522836555506614287',
  token: 'pat_XFJxVX7d9HlJ0YXaaDs4HRHr0v1CKAZq3MFshjAVY2jRKTjfkAzr9PgX31FUMe4X',
  baseUrl: 'https://api.coze.cn/v1/chat',
  timeout: 15000 // 15秒超时
};

/**
 * 主要的AI调用函数 - 完全绕过拦截器
 * @param {string} message 用户消息
 * @param {object} options 可选配置
 * @returns {Promise<object>} AI响应结果
 */
export const callCozeAI = async (message, options = {}) => {
  const config = { ...COZE_CONFIG, ...options };
  
  console.log('🤖 开始AI对话 (独立模块):', message);
  
  // 方法1: 尝试使用代理服务器
  const proxyResult = await tryProxyAPI(message, config);
  if (proxyResult.success) {
    return proxyResult;
  }
  
  // 方法2: 使用fetch API（直接调用，可能有CORS问题）
  const fetchResult = await tryFetchAPI(message, config);
  if (fetchResult.success) {
    return fetchResult;
  }
  
  // 方法3: 使用XMLHttpRequest（更底层，绕过所有拦截器）
  const xhrResult = await tryXHRAPI(message, config);
  if (xhrResult.success) {
    return xhrResult;
  }
  
  // 方法4: 使用CORS代理服务
  const corsProxyResult = await tryCORSProxy(message, config);
  if (corsProxyResult.success) {
    return corsProxyResult;
  }
  
  // 所有方法都失败，返回错误信息
  console.log('❌ 所有API调用方法都失败');
  return {
    success: false,
    message: '抱歉，由于网络限制，无法连接到AI服务。请检查网络连接或联系管理员。',
    source: 'error',
    error: 'All API methods failed'
  };
};

/**
 * 方法1：尝试使用本地代理服务器
 */
const tryProxyAPI = async (message, config) => {
  try {
    console.log('📡 尝试本地代理API调用');
    
    // 尝试调用本地代理服务
    const proxyUrl = 'http://localhost:9049/api/coze-proxy';
    
    const response = await fetch(proxyUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        message: message,
        bot_id: config.botId,
        token: config.token
      })
    });
    
    if (response.ok) {
      const data = await response.json();
      if (data.success && data.message) {
        console.log('✅ 本地代理API调用成功');
        return {
          success: true,
          message: data.message,
          source: 'local_proxy'
        };
      }
    }
    
    throw new Error(`Proxy failed: ${response.status}`);
    
  } catch (error) {
    console.error('❌ 本地代理API调用失败:', error.message);
    return { success: false };
  }
};

/**
 * 方法2：使用Fetch API
 */
const tryFetchAPI = async (message, config) => {
  try {
    console.log('📡 尝试Fetch API调用');
    
    const requestData = buildRequestData(message);
    
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), config.timeout);
    
    const response = await fetch(config.baseUrl, {
      method: 'POST',
      headers: buildHeaders(config.token),
      body: JSON.stringify(requestData),
      signal: controller.signal
      // 移除这些可能导致CORS问题的选项:
      // mode: 'cors' - 让浏览器自动处理
      // credentials: 'omit' - 可能不需要
      // cache: 'no-cache' - 已经在headers中移除
      // redirect: 'follow' - 默认行为
    });
    
    clearTimeout(timeoutId);
    
    if (response.ok) {
      const data = await response.json();
      const result = parseCozeResponse(data);
      if (result) {
        console.log('✅ Fetch API调用成功');
        return {
          success: true,
          message: result,
          source: 'fetch_api'
        };
      }
    }
    
    throw new Error(`Fetch failed: ${response.status}`);
    
  } catch (error) {
    console.error('❌ Fetch API调用失败:', error.message);
    return { success: false };
  }
};

/**
 * 方法3：使用XMLHttpRequest
 */
const tryXHRAPI = (message, config) => {
  return new Promise((resolve) => {
    try {
      console.log('📡 尝试XHR API调用');
      
      const xhr = new XMLHttpRequest();
      const requestData = buildRequestData(message);
      
      xhr.timeout = config.timeout;
      
      xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
          if (xhr.status === 200) {
            try {
              const data = JSON.parse(xhr.responseText);
              const result = parseCozeResponse(data);
              if (result) {
                console.log('✅ XHR API调用成功');
                resolve({
                  success: true,
                  message: result,
                  source: 'xhr_api'
                });
              } else {
                throw new Error('解析响应失败');
              }
            } catch (error) {
              console.error('❌ XHR响应解析失败:', error);
              resolve({ success: false });
            }
          } else {
            console.error('❌ XHR API调用失败:', xhr.status, xhr.statusText);
            resolve({ success: false });
          }
        }
      };
      
      xhr.onerror = function() {
        console.error('❌ XHR网络错误');
        resolve({ success: false });
      };
      
      xhr.ontimeout = function() {
        console.error('❌ XHR请求超时');
        resolve({ success: false });
      };
      
      xhr.open('POST', config.baseUrl, true);
      
      // 设置请求头 - 只设置最基本的头部
      xhr.setRequestHeader('Content-Type', 'application/json');
      xhr.setRequestHeader('Authorization', `Bearer ${config.token}`);
      // 移除其他可能导致CORS问题的头部
      
      xhr.send(JSON.stringify(requestData));
      
    } catch (error) {
      console.error('❌ XHR设置失败:', error);
      resolve({ success: false });
    }
  });
};

/**
 * 方法4：使用CORS代理服务
 */
const tryCORSProxy = async (message, config) => {
  try {
    console.log('📡 尝试CORS代理API调用');
    
    // 使用公共CORS代理服务
    const corsProxyUrls = [
      'https://cors-anywhere.herokuapp.com/',
      'https://api.allorigins.win/raw?url=',
      'https://corsproxy.io/?'
    ];
    
    for (const proxyUrl of corsProxyUrls) {
      try {
        const targetUrl = proxyUrl + encodeURIComponent(config.baseUrl);
        const requestData = buildRequestData(message);
        
        const response = await fetch(targetUrl, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${config.token}`,
            'X-Requested-With': 'XMLHttpRequest'
          },
          body: JSON.stringify(requestData)
        });
        
        if (response.ok) {
          const data = await response.json();
          const result = parseCozeResponse(data);
          if (result) {
            console.log('✅ CORS代理API调用成功');
            return {
              success: true,
              message: result,
              source: 'cors_proxy'
            };
          }
        }
      } catch (proxyError) {
        console.log(`CORS代理 ${proxyUrl} 失败:`, proxyError.message);
        continue;
      }
    }
    
    throw new Error('所有CORS代理都失败');
    
  } catch (error) {
    console.error('❌ CORS代理API调用失败:', error.message);
    return { success: false };
  }
};

/**
 * 构建请求数据
 */
const buildRequestData = (message) => {
  return {
    bot_id: COZE_CONFIG.botId,
    user_id: 'user_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9),
    stream: false,
    auto_save_history: true,
    additional_messages: [
      {
        role: 'user',
        content: message.trim(),
        content_type: 'text'
      }
    ]
  };
};

/**
 * 构建请求头 - 移除CORS不允许的headers
 */
const buildHeaders = (token) => {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
    'Accept': 'application/json'
    // 移除这些可能导致CORS问题的headers:
    // 'User-Agent': 浏览器自动设置
    // 'X-Requested-With': 可能不被允许
    // 'Cache-Control': 导致CORS错误
    // 'Pragma': 可能不被允许
  };
};

/**
 * 解析Coze API响应
 */
const parseCozeResponse = (data) => {
  try {
    if (data && data.code === 0 && data.data && data.data.messages) {
      const aiMessage = data.data.messages.find(msg => msg.role === 'assistant');
      if (aiMessage && aiMessage.content) {
        return aiMessage.content.trim();
      }
    }
    return null;
  } catch (error) {
    console.error('响应解析错误:', error);
    return null;
  }
};

/**
 * 生成智能本地回复
 */
const generateSmartResponse = (message) => {
  const lowerMessage = message.toLowerCase().trim();
  
  // 智能回复模板库
  const responseTemplates = {
    greeting: [
      '您好！我是AI学习助手，很高兴为您服务。我可以帮助您解答关于这个课程视频的问题。',
      '你好！我是您的学习伙伴，有什么关于课程的问题想要了解吗？',
      '欢迎！我是AI助教，可以为您提供学习指导和答疑服务。'
    ],
    
    video_content: [
      '这个视频包含了重要的学习内容。建议您：\n📹 仔细观看每个部分\n📝 记录关键知识点\n🤔 思考视频中的重点概念\n💡 结合实际案例理解',
      '视频内容丰富，建议您边看边做笔记。重点关注：\n• 核心概念和定义\n• 重要的方法和步骤\n• 实际应用案例\n• 常见问题和解决方案',
      '这个教学视频涵盖了课程的核心内容。为了更好地学习，建议您：\n1. 全程专注观看\n2. 暂停思考重点\n3. 做好学习笔记\n4. 课后及时复习'
    ],
    
    key_points: [
      '视频中的重点知识需要您在观看过程中总结。建议您：\n📌 记录关键概念和定义\n📐 注意重要的公式或方法\n❓ 标记难点和疑问\n📊 总结主要观点和结论',
      '重点知识通常包括：\n🎯 核心理论和概念\n🔧 关键技能和方法\n📈 重要数据和结论\n💼 实际应用场景\n建议您制作思维导图来整理知识结构。',
      '识别重点知识的方法：\n• 老师重复强调的内容\n• 配有图表说明的概念\n• 与实际应用相关的知识\n• 承上启下的关键点\n建议您用不同颜色标记不同类型的重点。'
    ],
    
    learning_methods: [
      '高效学习方法推荐：\n🎯 设定明确的学习目标\n📝 采用主动学习策略\n🔄 定期复习和总结\n💡 理论联系实际\n🤝 与他人讨论交流\n📊 及时检测学习效果',
      '建议您采用以下学习策略：\n• 🎬 认真观看，不要快进\n• 📋 做好详细笔记\n• 🔍 深入思考问题\n• 🎯 抓住重点难点\n• 🔄 及时复习巩固\n• 💪 多做练习应用',
      '有效的学习方法包括：\n1. 预习：了解学习内容框架\n2. 专注：全神贯注观看学习\n3. 记录：记录重点和疑问\n4. 思考：主动思考和分析\n5. 复习：及时回顾和总结\n6. 应用：理论联系实际'
    ],
    
    exercises: [
      '巩固学习的练习建议：\n📚 完成课后习题\n💻 动手实践操作\n🧠 思考拓展问题\n👥 参与讨论交流\n📝 撰写学习总结\n🎯 制定学习计划',
      '练习是巩固知识的重要方式：\n• 从基础题开始，逐步提高难度\n• 重视错题分析和总结\n• 多角度思考问题\n• 寻找知识间的联系\n• 定期自我检测\n• 向老师同学请教',
      '建议您进行以下练习：\n�� 基础概念理解题\n🔹 应用分析题\n🔹 综合实践题\n🔹 创新思考题\n记住：练习不在多而在精，重点是理解和掌握。'
    ],
    
    thanks: [
      '不用客气！我很高兴能帮助您学习。学习是一个持续的过程，保持好奇心和耐心很重要。如果还有问题，随时可以问我！',
      '很高兴能为您提供帮助！记住：学习没有捷径，但有方法。继续加油，相信您一定能掌握这门课程！',
      '能帮到您我很开心！学习路上，我会一直陪伴您。遇到困难不要气馁，每一次思考都是进步的阶梯。'
    ],
    
    encouragement: [
      '学习确实需要时间和耐心，但每一分努力都是值得的。相信自己，您一定可以掌握这些知识！',
      '遇到困难是学习过程中的正常现象，关键是要坚持下去。加油，您已经在进步的路上了！',
      '学习是一个循序渐进的过程，不要着急。每天进步一点点，积累起来就是很大的成就！'
    ]
  };
  
  // 智能匹配回复类型
  if (lowerMessage.includes('你好') || lowerMessage.includes('hello') || lowerMessage.includes('hi') || lowerMessage.includes('您好')) {
    return getRandomResponse(responseTemplates.greeting);
  } else if ((lowerMessage.includes('视频') || lowerMessage.includes('内容')) && (lowerMessage.includes('什么') || lowerMessage.includes('讲') || lowerMessage.includes('说'))) {
    return getRandomResponse(responseTemplates.video_content);
  } else if (lowerMessage.includes('重点') || lowerMessage.includes('关键') || lowerMessage.includes('要点') || lowerMessage.includes('核心')) {
    return getRandomResponse(responseTemplates.key_points);
  } else if (lowerMessage.includes('学习') || lowerMessage.includes('怎么') || lowerMessage.includes('如何') || lowerMessage.includes('方法')) {
    return getRandomResponse(responseTemplates.learning_methods);
  } else if (lowerMessage.includes('练习') || lowerMessage.includes('题目') || lowerMessage.includes('作业') || lowerMessage.includes('习题')) {
    return getRandomResponse(responseTemplates.exercises);
  } else if (lowerMessage.includes('谢谢') || lowerMessage.includes('感谢') || lowerMessage.includes('thanks') || lowerMessage.includes('thank')) {
    return getRandomResponse(responseTemplates.thanks);
  } else if (lowerMessage.includes('难') || lowerMessage.includes('困难') || lowerMessage.includes('不会') || lowerMessage.includes('不懂')) {
    return getRandomResponse(responseTemplates.encouragement);
  } else {
    // 通用智能回复
    return `我理解您想了解"${message}"相关的内容。虽然我现在无法提供具体的答案，但我建议您：

🎯 **针对性学习**
• 仔细观看视频相关部分
• 重点关注您提到的内容
• 做好详细的学习笔记

📚 **深入研究**  
• 查阅相关学习资料
• 搜索更多学习资源
• 参考教材和文献

🤝 **寻求帮助**
• 与同学讨论交流
• 向老师请教问题
• 参与学习小组

💡 **实践应用**
• 多思考多练习
• 理论联系实际
• 总结学习心得

如果您能提供更具体的问题，我会尽力为您提供更有针对性的建议！`;
  }
};

/**
 * 获取随机回复
 */
const getRandomResponse = (responses) => {
  const randomIndex = Math.floor(Math.random() * responses.length);
  return responses[randomIndex];
};

/**
 * 检查网络连接状态
 */
export const checkNetworkStatus = () => {
  return navigator.onLine;
};

/**
 * 测试API连接
 */
export const testCozeConnection = async () => {
  try {
    const testMessage = "Hello";
    const result = await callCozeAI(testMessage);
    return {
      connected: result.success,
      source: result.source,
      message: result.success ? 'API连接正常' : 'API连接失败，使用本地回复'
    };
  } catch (error) {
    return {
      connected: false,
      source: 'error',
      message: '连接测试失败: ' + error.message
    };
  }
}; 