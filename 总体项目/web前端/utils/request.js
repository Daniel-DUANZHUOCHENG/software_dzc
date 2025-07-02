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

export default service 