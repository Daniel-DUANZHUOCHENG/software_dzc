<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>

    <!-- 主要内容区域 -->
    <div class="login-content">
      <!-- 左侧信息区域 -->
      <div class="info-section">
        <div class="info-content">
          <div class="logo-section">
            <img src="/static/logo3.png" alt="LOGO" class="logo-image">
            <h1 class="system-title">测盟汇系统</h1>
          </div>
          <div class="welcome-text">
            <h2>欢迎回来</h2>
            <p>中国电子质量管理协会计算机软硬件和信息系统质量测评分会</p>
            <p>专业的会议管理、信息交流与知识共享平台</p>
          </div>
          <div class="features">
            <div class="feature-item">
              <el-icon><Calendar /></el-icon>
              <span>智能会议管理</span>
            </div>
            <div class="feature-item">
              <el-icon><Document /></el-icon>
              <span>行业资讯分享</span>
            </div>
            <div class="feature-item">
              <el-icon><Notebook /></el-icon>
              <span>在线课程学习</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单区域 -->
      <div class="form-section">
        <div class="login-card">
          <div class="card-header">
            <h2>用户登录</h2>
            <p>请输入您的账号信息</p>
          </div>

          <div class="form-container">
            <div class="form-group">
              <label>用户名</label>
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
              />
            </div>

            <div class="form-group">
              <label>密码</label>
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </div>

            <div class="form-group captcha-group">
              <label>验证码</label>
              <div class="captcha-container">
                <el-input 
                  v-model="form.captcha" 
                  placeholder="请输入验证码"
                  size="large"
                />
                <div class="captcha-image" @click="refreshCode">
                  <Identify :identifyCode="identifyCode" />
                </div>
              </div>
            </div>

            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <a href="#" class="forgot-link">忘记密码？</a>
            </div>

            <el-button 
              type="primary" 
              @click="login" 
              class="login-button"
              size="large"
              :loading="loading"
            >
              {{ loading ? '登录中...' : '立即登录' }}
            </el-button>

            <el-button 
              type="info" 
              @click="testNavigation" 
              class="test-button"
              size="large"
              style="margin-top: 10px;"
            >
              测试跳转
            </el-button>

            <div class="register-link">
              <span>还没有账号？</span>
              <a href="#" @click.prevent="registerVisible = true" class="register-btn">立即注册</a>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 注册对话框 -->
    <el-dialog 
      v-model="registerVisible" 
      title="用户注册" 
      width="600px"
      class="register-dialog"
      :close-on-click-modal="false"
    >
      <div class="register-container">
        <!-- 租户信息表单 -->
        <div v-if="currentPage === 0" class="register-step">
          <div class="step-header">
            <h3>租户信息</h3>
            <p>请填写企业基本信息</p>
          </div>
          <div class="form-group">
            <label>租户名称 *</label>
            <el-input placeholder="请输入租户名称" v-model="tenantForm.tenantName" />
          </div>
          <div class="form-group">
            <label>联系人 *</label>
            <el-input placeholder="请输入联系人姓名" v-model="tenantForm.contactPerson" />
          </div>
          <div class="form-group">
            <label>联系电话 *</label>
            <el-input placeholder="请输入联系电话" v-model="tenantForm.phone" />
          </div>
          <div class="form-group">
            <label>备注</label>
            <el-input 
              type="textarea" 
              placeholder="请输入备注信息" 
              v-model="tenantForm.remark"
              :rows="3"
            />
          </div>
        </div>

        <!-- 用户信息表单 -->
        <div v-if="currentPage === 1" class="register-step">
          <div class="step-header">
            <h3>用户信息</h3>
            <p>请填写管理员账号信息</p>
          </div>
          <div class="form-group">
            <label>用户名 *</label>
            <el-input placeholder="请输入用户名" v-model="userForm.username" />
          </div>
          <div class="form-group">
            <label>密码 *</label>
            <el-input type="password" placeholder="请输入密码" v-model="userForm.password" show-password />
          </div>
          <div class="form-group">
            <label>昵称 *</label>
            <el-input placeholder="请输入昵称" v-model="userForm.nickname" />
          </div>
          <div class="form-group">
            <label>电话号码 *</label>
            <el-input placeholder="请输入电话号码" v-model="userForm.phoneNumber" />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <el-input type="email" placeholder="请输入邮箱" v-model="userForm.email" />
          </div>
          <div class="form-group">
            <label>性别</label>
            <el-radio-group v-model="userForm.gender">
              <el-radio label="Male">男</el-radio>
              <el-radio label="Female">女</el-radio>
            </el-radio-group>
          </div>
          <div class="form-group captcha-group">
            <label>验证码 *</label>
            <div class="captcha-container">
              <el-input 
                v-model="registerForm.captcha" 
                placeholder="请输入验证码"
              />
              <div class="captcha-image" @click="refreshRegisterCode">
                <Identify :identifyCode="registerIdentifyCode" />
              </div>
            </div>
          </div>
        </div>

        <div class="dialog-footer">
          <el-button @click="resetForms" size="large">取消</el-button>
          <el-button v-if="currentPage > 0" @click="prevPage" size="large">上一步</el-button>
          <el-button 
            v-if="currentPage < totalPages - 1" 
            type="primary" 
            @click="nextPage" 
            size="large"
          >
            下一步
          </el-button>
          <el-button 
            v-else 
            type="primary" 
            @click="register" 
            size="large"
            :loading="registerLoading"
          >
            {{ registerLoading ? '注册中...' : '完成注册' }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../../utils/request.js'
import { ElMessage } from 'element-plus'
import { User, Lock, Calendar, Document, Notebook } from '@element-plus/icons-vue'
import Identify from "../../pages/components/Identify.vue"

const form = ref({
  username: '',
  password: '',
  captcha: ''
})

const registerForm = ref({
  captcha: ''
})

const router = useRouter()
const rememberMe = ref(false)
const loading = ref(false)
const registerLoading = ref(false)

const identifyCode = ref('')
const registerIdentifyCode = ref('')
const identifyCodes = ref('1234567890abcdefjhijklinopqrsduvwxyz')
const registerIdentifyCodes = ref('1234567890abcdefjhijklinopqrsduvwxyz')

const randomNum = (min: number, max: number) => {
  return Math.floor(Math.random() * (max - min) + min)
}

const makeCode = (o: any, l: number) => {
  o.value = ''
  for (let i = 0; i < l; i++) {
    o.value += identifyCodes.value[randomNum(0, identifyCodes.value.length)]
  }
}

const makeCode2 = (o: any, l: number) => {
  o.value = ''
  for (let i = 0; i < l; i++) {
    o.value += registerIdentifyCodes.value[randomNum(0, registerIdentifyCodes.value.length)]
  }
}

const refreshCode = () => {
  makeCode(identifyCode, 4)
}

const refreshRegisterCode = () => {
  makeCode(registerIdentifyCode, 4)
}

onMounted(() => {
  refreshCode()
  refreshRegisterCode()
})

// 注册相关
const registerVisible = ref(false)
const currentPage = ref(0)
const totalPages = 2

const tenantForm = ref({
  id: null,
  adminUsername: '',
  password: null,
  contactPerson: '',
  phone: '',
  tenantName: '',
  createdAt: '',
  icon: null,
  remark: '',
  rootDepartmentId: null
})

const userForm = ref({
  id: null,
  username: '',
  password: '',
  nickname: '',
  phoneNumber: '',
  email: '',
  gender: '',
  departmentId: null,
  status: 'Active',
  role: 'TAdmin',
  createdAt: null,
  position: '企业管理员',
  remark: '',
  avatar: null,
  tenantId: null
})

const testNavigation = async () => {
  console.log('Testing navigation to /home...')
  try {
    await router.push('/home')
    console.log('Test navigation successful')
    ElMessage.success('测试跳转成功')
  } catch (error) {
    console.error('Test navigation failed:', error)
    ElMessage.error('测试跳转失败')
  }
}

const login = async () => {
  if (!form.value.username || !form.value.password || !form.value.captcha) {
    ElMessage.error('请填写完整的登录信息')
    return
  }

  if (form.value.captcha.toLowerCase() !== identifyCode.value.toLowerCase()) {
    ElMessage.error('验证码错误')
    refreshCode()
    return
  }

  loading.value = true

  try {
    // 先测试连接
    console.log('🔍 测试后端连接...')
    try {
      const testResponse = await axios.get('/users/test')
      console.log('✅ 后端连接测试成功:', testResponse.data)
    } catch (testError) {
      console.error('❌ 后端连接测试失败:', testError)
      ElMessage.error('无法连接到服务器，请检查后端是否启动')
      loading.value = false
      return
    }

    console.log('🔑 发送登录请求...')
    console.log('📤 请求数据:', {
      username: form.value.username,
      password: form.value.password
    })
    console.log('🌐 请求URL: /users/login')
    
    const response = await axios.post('/users/login', {
      username: form.value.username,
      password: form.value.password
    })

    console.log('📥 登录响应:', response)
    console.log('📥 响应数据:', response.data)
    
    // 安全地检查响应数据
    const responseData = response?.data
    console.log('🔍 解析响应数据:', responseData)
    
    if (responseData && responseData.userId && responseData.userId > 0 && responseData.user) {
      console.log('✅ 登录成功，存储用户数据...')
      console.log('👤 用户信息:', responseData.user)
      
      localStorage.setItem('userId', responseData.userId.toString())
      localStorage.setItem('userInfo', JSON.stringify(responseData.user))
      ElMessage.success('登录成功')
      
      console.log('🔄 跳转到首页...')
      await router.push('/home')
      console.log('✅ 跳转完成')
    } else {
      console.log('❌ 登录失败详情:', {
        hasData: !!responseData,
        userId: responseData?.userId,
        hasUser: !!responseData?.user,
        message: responseData?.message
      })
      ElMessage.error(responseData?.message || '登录失败，响应数据格式异常')
    }
  } catch (error) {
    console.error('💥 登录错误详情:', error)
    console.error('💥 错误响应:', error.response)
    console.error('💥 错误请求:', error.request)
    console.error('💥 错误配置:', error.config)
    
    if (error.response) {
      console.error('📊 响应状态:', error.response.status)
      console.error('📊 响应数据:', error.response.data)
      console.error('📊 响应头:', error.response.headers)
      
      if (error.response.status === 401) {
        ElMessage.error('用户名或密码错误')
      } else if (error.response.status === 500) {
        ElMessage.error('服务器内部错误')
      } else {
        ElMessage.error(`请求失败 (${error.response.status}): ${error.response.data?.message || '未知错误'}`)
      }
    } else if (error.request) {
      console.error('📡 请求发送但无响应:', error.request)
      ElMessage.error('网络连接失败，请检查网络连接或后端服务状态')
    } else {
      console.error('⚙️ 请求配置错误:', error.message)
      ElMessage.error('请求配置错误: ' + error.message)
    }
  } finally {
    loading.value = false
  }
}

const nextPage = () => {
  if (currentPage.value === 0) {
    if (!tenantForm.value.tenantName || !tenantForm.value.contactPerson || !tenantForm.value.phone) {
      ElMessage.error('请填写完整的租户信息')
      return
    }
  }
  currentPage.value++
}

const prevPage = () => {
  currentPage.value--
}

const resetForms = () => {
  registerVisible.value = false
  currentPage.value = 0
  tenantForm.value = {
    id: null,
    adminUsername: '',
    password: null,
    contactPerson: '',
    phone: '',
    tenantName: '',
    createdAt: '',
    icon: null,
    remark: '',
    rootDepartmentId: null
  }
  userForm.value = {
    id: null,
    username: '',
    password: '',
    nickname: '',
    phoneNumber: '',
    email: '',
    gender: '',
    departmentId: null,
    status: 'Active',
    role: 'TAdmin',
    createdAt: null,
    position: '企业管理员',
    remark: '',
    avatar: null,
    tenantId: null
  }
  registerForm.value.captcha = ''
  refreshRegisterCode()
}

const register = async () => {
  if (!userForm.value.username || !userForm.value.password || !userForm.value.nickname || 
      !userForm.value.phoneNumber || !registerForm.value.captcha) {
    ElMessage.error('请填写完整的用户信息')
    return
  }

  if (registerForm.value.captcha.toLowerCase() !== registerIdentifyCode.value.toLowerCase()) {
    ElMessage.error('验证码错误')
    refreshRegisterCode()
    return
  }

  registerLoading.value = true

  try {
    // 创建租户
    const tenantResponse = await axios.post('/api/tenants', {
      tenantName: tenantForm.value.tenantName,
      contactPerson: tenantForm.value.contactPerson,
      phone: tenantForm.value.phone,
      remark: tenantForm.value.remark
    })

    if (tenantResponse.data.success) {
      const tenantId = tenantResponse.data.tenant.id

      // 创建用户
      const userResponse = await axios.post('/users/insert', {
        username: userForm.value.username,
        password: userForm.value.password,
        nickname: userForm.value.nickname,
        phoneNumber: userForm.value.phoneNumber,
        email: userForm.value.email,
        gender: userForm.value.gender,
        role: userForm.value.role,
        position: userForm.value.position,
        tenantId: tenantId
      })

      if (userResponse.data.success) {
        ElMessage.success('注册成功，请登录')
        resetForms()
      } else {
        ElMessage.error(userResponse.data.message || '用户创建失败')
      }
    } else {
      ElMessage.error(tenantResponse.data.message || '租户创建失败')
    }
  } catch (error: any) {
    console.error('注册失败:', error)
    ElMessage.error(error.response?.data?.message || '注册失败，请检查网络连接')
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 100px;
  height: 100px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.shape-3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

.login-content {
  display: flex;
  width: 100%;
  max-width: 1200px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  z-index: 1;
}

.info-section {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 40px;
  display: flex;
  align-items: center;
  position: relative;
}

.info-content {
  text-align: center;
}

.logo-section {
  margin-bottom: 40px;
}

.logo-image {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  margin-bottom: 20px;
}

.system-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.welcome-text {
  margin-bottom: 40px;
}

.welcome-text h2 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
}

.welcome-text p {
  font-size: 16px;
  line-height: 1.6;
  opacity: 0.9;
  margin-bottom: 8px;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  font-weight: 500;
}

.feature-item .el-icon {
  font-size: 20px;
  background: rgba(255, 255, 255, 0.2);
  padding: 8px;
  border-radius: 8px;
}

.form-section {
  flex: 1;
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 100%;
  max-width: 400px;
}

.card-header {
  text-align: center;
  margin-bottom: 40px;
}

.card-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.card-header p {
  color: #7f8c8d;
  font-size: 16px;
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.captcha-group {
  margin-bottom: 8px;
}

.captcha-container {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.captcha-container .el-input {
  flex: 1;
}

.captcha-image {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.captcha-image:hover {
  transform: scale(1.05);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.forgot-link {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.forgot-link:hover {
  color: #764ba2;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #7f8c8d;
}

.register-btn {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
  transition: color 0.3s ease;
}

.register-btn:hover {
  color: #764ba2;
}

/* 注册对话框样式 */
.register-dialog {
  border-radius: 16px;
}

.register-container {
  padding: 20px 0;
}

.register-step {
  margin-bottom: 30px;
}

.step-header {
  text-align: center;
  margin-bottom: 30px;
}

.step-header h3 {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.step-header p {
  color: #7f8c8d;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ecf0f1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-content {
    flex-direction: column;
    margin: 20px;
    border-radius: 16px;
  }

  .info-section {
    padding: 40px 20px;
  }

  .form-section {
    padding: 40px 20px;
  }

  .system-title {
    font-size: 24px;
  }

  .welcome-text h2 {
    font-size: 20px;
  }

  .features {
    gap: 16px;
  }

  .feature-item {
    font-size: 14px;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e1e8ed;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

:deep(.el-checkbox__label) {
  color: #2c3e50;
  font-weight: 500;
}

:deep(.el-radio__label) {
  color: #2c3e50;
  font-weight: 500;
}
</style>
