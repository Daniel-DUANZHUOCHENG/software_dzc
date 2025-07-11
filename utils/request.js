import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:9049', // 后端API基础地址
  timeout: 15000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const userId = localStorage.getItem('userId')
    
    console.log('🔍 请求拦截器 - 当前用户信息:', { userInfo, userId })
    console.log('🔍 请求拦截器 - 请求URL:', config.url)
    console.log('🔍 请求拦截器 - 完整URL:', config.baseURL + config.url)
    
    // 跳过登录和注册接口的权限头设置
    const isAuthRequest = config.url && (
      config.url.includes('/login') || 
      config.url.includes('/register') ||
      config.url.includes('/test')
    )
    
    // 如果有用户信息且不是认证请求，添加权限相关的请求头
    if ((userInfo.id || userId) && !isAuthRequest) {
      // 优先使用userInfo中的id，如果没有则使用localStorage中的userId
      const currentUserId = userInfo.id || userId
      const currentRole = userInfo.role || 'User'
      const currentTenantId = userInfo.tenantId || userInfo.tenant_id || '1'
      const currentUserName = userInfo.nickname || userInfo.username || 'Unknown'
      
      // 设置权限头
      config.headers['User-Id'] = currentUserId.toString()
      config.headers['User-Role'] = currentRole
      config.headers['User-Tenant-Id'] = currentTenantId.toString()
      // 对包含中文的用户名进行Base64编码，避免HTTP头字符集问题
      config.headers['User-Name'] = btoa(encodeURIComponent(currentUserName))
      
      console.log('✅ 请求拦截器 - 已设置权限头:', {
        'User-Id': currentUserId.toString(),
        'User-Role': currentRole,
        'User-Tenant-Id': currentTenantId.toString(),
        'User-Name': `${currentUserName} (已Base64编码)`,
        'Authorization': config.headers['Authorization'] || '未设置'
      })
    } else if (!isAuthRequest) {
      console.warn('⚠️ 请求拦截器 - 缺少用户信息，但继续发送请求（已跳过权限验证）')
      console.warn('⚠️ userInfo:', userInfo)
      console.warn('⚠️ userId:', userId)
    }
    
    // 设置Content-Type
    if (!config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json'
    }
    
    return config
  },
  error => {
    console.error('❌ 请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    console.log('✅ 响应拦截器 - 收到响应:', {
      url: response.config.url,
      status: response.status,
      dataType: typeof res,
      hasData: !!res,
      responseData: res  // 添加完整的响应数据调试信息
    })
    
    // 如果是文件下载等特殊响应，直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    
    // 🔧 特殊处理：登录接口使用LoginResponse格式，直接返回原始响应
    if (response.config.url && response.config.url.includes('/users/login')) {
      console.log('🔍 登录接口响应，直接返回原始数据:', res)
      return response // 返回完整的response对象，保持原有格式
    }
    
    // 🔧 修复：检查后端实际使用的字段名 isOK 而不是 success
    if (res.isOK === false) {
      // 权限不足或其他错误
      const errorMessage = res.msg || res.message || '请求失败'
      console.error('❌ 后端返回错误:', {
        isOK: res.isOK,
        msg: res.msg,
        message: res.message,
        url: response.config.url
      })
      ElMessage.error(errorMessage)
      return Promise.reject(new Error(errorMessage))
    }
    
    // 成功响应，直接返回原始response，保持兼容性（前端页面依赖 response.data）
    console.log('✅ 响应成功，返回完整响应:', res)
    return response  // 返回完整的response对象
  },
  error => {
    let message = '请求失败'
    
    console.error('❌ 响应拦截器 - 发生错误:', error)
    
    if (error.response) {
      const status = error.response.status
      
      console.error('📊 响应拦截器 - 错误详情:', {
        status: status,
        statusText: error.response.statusText,
        url: error.config?.url,
        method: error.config?.method,
        data: error.response.data,
        headers: error.response.headers,
        requestHeaders: error.config?.headers
      })
      
      switch (status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          // 可以在这里处理登录过期，跳转到登录页面
          localStorage.removeItem('userInfo')
          localStorage.removeItem('userId')
          window.location.href = '/login'
          break
        case 403:
          message = '权限不足，无法访问该资源'
          console.error('❌ 403权限错误详情:', {
            url: error.config?.url,
            method: error.config?.method,
            headers: error.config?.headers,
            userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
          })
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `请求失败 (${status})`
      }
      
      // 如果后端返回了具体的错误信息，使用后端的错误信息
      if (error.response.data && error.response.data.message) {
        message = error.response.data.message
      }
    } else if (error.request) {
      console.error('📡 网络请求失败:', error.request)
      message = '网络连接失败，请检查网络连接'
    } else {
      console.error('📝 请求配置错误:', error.message)
      message = '请求配置错误: ' + error.message
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// Coze AI API调用工具函数 - 绕过拦截器
export const callCozeAI = async (message, config = {}) => {
  const defaultConfig = {
    botId: '7522836555506614287',
    token: 'pat_XFJxVX7d9HlJ0YXaaDs4HRHr0v1CKAZq3MFshjAVY2jRKTjfkAzr9PgX31FUMe4X',
    baseUrl: 'https://api.coze.cn/v1/chat'
  };

  const finalConfig = { ...defaultConfig, ...config };

  try {
    console.log('🤖 开始调用Coze AI API (绕过拦截器):', message);
    
    // 构建请求数据
    const requestData = {
      bot_id: finalConfig.botId,
      user_id: 'course_user_' + Date.now(),
      stream: false,
      auto_save_history: true,
      additional_messages: [
        {
          role: 'user',
          content: message,
          content_type: 'text'
        }
      ]
    };

    console.log('📤 发送请求数据:', requestData);

    // 方法1：使用原生fetch直接调用Coze API（绕过axios拦截器）
    try {
      const response = await fetch(finalConfig.baseUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${finalConfig.token}`,
          'Accept': 'application/json',
          'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
          // 添加更多header来避免被拦截
          'X-Requested-With': 'XMLHttpRequest',
          'Cache-Control': 'no-cache'
        },
        body: JSON.stringify(requestData),
        // 重要：设置mode为cors，credentials为omit来绕过某些拦截
        mode: 'cors',
        credentials: 'omit',
        cache: 'no-cache'
      });

      console.log('📥 API响应状态:', response.status);

      if (response.ok) {
        const data = await response.json();
        console.log('✅ Coze API响应数据:', data);

        // 解析响应数据
        if (data.code === 0 && data.data && data.data.messages) {
          const aiMessage = data.data.messages.find(msg => msg.role === 'assistant');
          if (aiMessage && aiMessage.content) {
            return {
              success: true,
              message: aiMessage.content,
              source: 'coze_api'
            };
          }
        }
      }

      throw new Error(`API调用失败: ${response.status}`);

    } catch (fetchError) {
      console.error('🚫 直接API调用失败:', fetchError);
      
      // 方法2：尝试使用JSONP方式（如果支持）
      return await callCozeAPIWithJSONP(message, finalConfig);
    }

  } catch (error) {
    console.error('❌ Coze API调用完全失败:', error);
    return {
      success: false,
      message: getSmartDefaultResponse(message),
      source: 'fallback'
    };
  }
};

// 使用JSONP方式调用（绕过CORS和拦截器）
const callCozeAPIWithJSONP = async (message, config) => {
  try {
    console.log('🔄 尝试使用JSONP方式调用API');
    
    // 由于Coze API不支持JSONP，这里直接返回到本地智能回复
    return {
      success: false,
      message: getSmartDefaultResponse(message),
      source: 'local_ai'
    };
    
  } catch (error) {
    console.error('🚫 JSONP调用失败:', error);
    
    return {
      success: false,
      message: getSmartDefaultResponse(message),
      source: 'local_ai'
    };
  }
};

// 备用API调用方法 - 完全绕过拦截器
const callCozeAPIFallback = async (message, config) => {
  try {
    console.log('🔄 尝试备用API调用方法（绕过拦截器）');
    
    // 方法2a：使用XMLHttpRequest绕过axios拦截器
    return await callCozeAPIWithXHR(message, config);
    
  } catch (error) {
    console.error('🚫 备用调用失败:', error);
    
    return {
      success: false,
      message: getSmartDefaultResponse(message),
      source: 'local_ai'
    };
  }
};

// 使用XMLHttpRequest绕过拦截器
const callCozeAPIWithXHR = (message, config) => {
  return new Promise((resolve) => {
    try {
      const xhr = new XMLHttpRequest();
      const requestData = {
        bot_id: config.botId,
        user_id: 'course_user_' + Date.now(),
        stream: false,
        auto_save_history: true,
        additional_messages: [
          {
            role: 'user',
            content: message,
            content_type: 'text'
          }
        ]
      };

      xhr.open('POST', config.baseUrl, true);
      xhr.setRequestHeader('Content-Type', 'application/json');
      xhr.setRequestHeader('Authorization', `Bearer ${config.token}`);
      xhr.setRequestHeader('Accept', 'application/json');
      
      xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
          if (xhr.status === 200) {
            try {
              const data = JSON.parse(xhr.responseText);
              console.log('✅ XHR API响应数据:', data);
              
              if (data.code === 0 && data.data && data.data.messages) {
                const aiMessage = data.data.messages.find(msg => msg.role === 'assistant');
                if (aiMessage && aiMessage.content) {
                  resolve({
                    success: true,
                    message: aiMessage.content,
                    source: 'xhr_api'
                  });
                  return;
                }
              }
            } catch (parseError) {
              console.error('XHR响应解析失败:', parseError);
            }
          }
          
          // 如果到这里，说明API调用失败，使用本地智能回复
          resolve({
            success: false,
            message: getSmartDefaultResponse(message),
            source: 'local_ai'
          });
        }
      };

      xhr.onerror = function() {
        console.error('XHR请求失败');
        resolve({
          success: false,
          message: getSmartDefaultResponse(message),
          source: 'local_ai'
        });
      };

      xhr.ontimeout = function() {
        console.error('XHR请求超时');
        resolve({
          success: false,
          message: getSmartDefaultResponse(message),
          source: 'local_ai'
        });
      };

      xhr.timeout = 10000; // 10秒超时
      xhr.send(JSON.stringify(requestData));
      
    } catch (error) {
      console.error('XHR请求创建失败:', error);
      resolve({
        success: false,
        message: getSmartDefaultResponse(message),
        source: 'local_ai'
      });
    }
  });
};

// 智能默认回复生成器
const getSmartDefaultResponse = (message) => {
  const lowerMessage = message.toLowerCase();
  
  // 预定义的智能回复模板
  const responses = {
    greeting: [
      '您好！我是AI学习助手，很高兴为您服务。我可以帮助您解答关于这个课程视频的问题。',
      '你好！我是您的学习伙伴，有什么关于课程的问题想要了解吗？'
    ],
    video_content: [
      '这个视频包含了重要的学习内容。建议您仔细观看视频，并记录关键知识点。如果您有具体的问题，可以暂停视频并深入思考。',
      '视频内容丰富，建议您边看边做笔记，记录重要概念和要点。'
    ],
    key_points: [
      '视频中的重点知识需要您在观看过程中总结。建议您：\n1. 记录关键概念和定义\n2. 注意重要的公式或方法\n3. 标记难点和疑问\n4. 总结主要观点',
      '重点知识通常包括核心概念、关键步骤和重要结论。建议您制作思维导图来整理知识结构。'
    ],
    learning_methods: [
      '建议您采用以下学习方法：\n1. 🎯 认真观看视频，不要快进\n2. 📝 做好笔记，记录重点\n3. 🔄 课后复习，巩固记忆\n4. 💡 实践应用所学知识\n5. 🤔 思考问题，加深理解',
      '有效的学习方法包括：主动学习、分段学习、及时复习、实践应用。建议您根据自己的情况选择合适的方法。'
    ],
    exercises: [
      '建议您在观看完视频后：\n1. 寻找相关的练习题进行巩固\n2. 尝试解决实际问题\n3. 与同学讨论交流\n4. 向老师请教疑难问题',
      '练习是巩固知识的重要方式。建议您从基础题开始，逐步提高难度。'
    ],
    thanks: [
      '不用客气！我很高兴能帮助您学习。如果您还有其他问题，随时可以问我。祝您学习愉快！',
      '很高兴能为您提供帮助！继续加油，相信您一定能学好这门课程。'
    ]
  };

  // 智能匹配回复类型
  if (lowerMessage.includes('你好') || lowerMessage.includes('hello') || lowerMessage.includes('hi')) {
    return getRandomResponse(responses.greeting);
  } else if (lowerMessage.includes('视频') && (lowerMessage.includes('什么') || lowerMessage.includes('内容'))) {
    return getRandomResponse(responses.video_content);
  } else if (lowerMessage.includes('重点') || lowerMessage.includes('关键') || lowerMessage.includes('要点')) {
    return getRandomResponse(responses.key_points);
  } else if (lowerMessage.includes('学习') || lowerMessage.includes('怎么') || lowerMessage.includes('如何')) {
    return getRandomResponse(responses.learning_methods);
  } else if (lowerMessage.includes('练习') || lowerMessage.includes('题目') || lowerMessage.includes('作业')) {
    return getRandomResponse(responses.exercises);
  } else if (lowerMessage.includes('谢谢') || lowerMessage.includes('感谢') || lowerMessage.includes('thanks')) {
    return getRandomResponse(responses.thanks);
  } else {
    return `我理解您想了解"${message}"相关的内容。虽然我现在无法提供具体的答案，但我建议您：\n\n1. 📹 仔细观看视频相关部分\n2. 📚 查阅相关资料\n3. 🤝 与同学老师交流\n4. 💭 多思考多实践\n\n如果您能提供更具体的问题，我会尽力为您解答！`;
  }
};

// 获取随机回复
const getRandomResponse = (responses) => {
  return responses[Math.floor(Math.random() * responses.length)];
};

export default service 