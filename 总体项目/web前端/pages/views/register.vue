<template>
  <div class="login-container">
    <div class="login-left">
      <div class="login-box">
        <img src="/static/logo3.png" alt="LOGO" class="logo-image">
        <div class="register">
          <el-button type="primary" @click="registerVisible = true" class="login-button">Register</el-button>
        </div>
		<div></div>
		<div></div>
		<div></div>
		<div></div>
		<div></div>
		<div></div>
		<div></div>
		<div></div>
		<div class="register">
		  <a href="/">返回</a>
		</div>
        <div class="footer">
          <p>© 2018 Connexial, Inc. All rights reserved. <a href="#">Privacy Policy</a></p>
        </div>
      </div>
    </div>
    <div class="login-right">
      <div class="welcome-message">
        <h1>欢迎来到测盟汇系统</h1>
        <p>Welcome to the Testing Alliance Collection System.Testing Alliance Collection System has been the industry's standard dummy text ever since the 1500s.</p>
        <el-button type="default">Read more...</el-button>
      </div>
    </div>
  </div>

  <el-dialog v-model="registerVisible" title="注册" width="30%">
    <div class="register-container">
      <!-- 租户信息表单 -->
      <div v-if="currentPage === 0">
        <h3>租户信息</h3>
        <div class="form-group">
          <el-input placeholder="租户名称" v-model="tenantForm.tenantName" />
        </div>
        <div class="form-group">
          <el-input placeholder="联系人" v-model="tenantForm.contactPerson" />
        </div>
        <div class="form-group">
          <el-input placeholder="联系电话" v-model="tenantForm.phone" />
        </div>
        <div class="form-group">
          <el-input placeholder="备注" v-model="tenantForm.remark" />
        </div>
      </div>

      <!-- 用户信息表单 -->
      <div v-if="currentPage === 1">
        <h3>用户信息</h3>
        <div class="form-group">
          <el-input placeholder="用户名" v-model="userForm.username" />
        </div>
        <div class="form-group">
          <el-input type="password" placeholder="密码" v-model="userForm.password" />
        </div>
        <div class="form-group">
          <el-input placeholder="昵称" v-model="userForm.nickname" />
        </div>
        <div class="form-group">
          <el-input placeholder="电话号码" v-model="userForm.phoneNumber" />
        </div>
        <div class="form-group">
          <el-input type="email" placeholder="邮箱" v-model="userForm.email" />
        </div>
        <div class="form-group">
          <el-radio-group v-model="userForm.gender">
            <el-radio label="Male">男</el-radio>
            <el-radio label="Female">女</el-radio>
          </el-radio-group>
        </div>
        <div class="form-group">
          <el-input type="text" placeholder="验证码" id="register-captcha" v-model="registerForm.captcha" />
		  <Identify :identifyCode="registerIdentifyCode" @click="refreshRegisterCode()" />
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="resetForms">取消</el-button>
        <el-button v-if="currentPage > 0" @click="prevPage">上一步</el-button>
        <el-button v-if="currentPage < totalPages - 1" type="primary" @click="nextPage">下一步</el-button>
        <el-button v-else type="primary" @click="register">注册</el-button>
      </span>
    </div>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../../utils/request.js'
import { ElMessage } from 'element-plus'
import Identify from "../../pages/components/Identify.vue";

const form = ref({
  username: '',
  password: '',
  captcha: ''
})
const registerForm = ref({
  captcha: ''
})
const message = ref('')
const router = useRouter()
const rememberMe = ref(false)

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



// 重置验证码
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

const params = {
  username: form.value.username,
  password: form.value.password,
};

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

const departmentForm = ref({
  id: null,
  departmentName: '',
  status: 'Active',
  createdAt: '',
  parentDepartment: 1,
  manager: '',
  managerPhone: '',
  managerEmail: '',
  tenantId: null,
  path: null
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
  avatar: '',
  tenantId: null,
  path: null
})

const totalPages = 2
const currentPage = ref(0)
const registerVisible = ref(false)

const login = () => {
  if (form.value.captcha !== identifyCode.value) {
    message.value = '验证码错误'
    ElMessage.error(message.value)
    refreshCode()
    return
  }
  localStorage.removeItem('userId');
  localStorage.removeItem('tenantId');
  localStorage.removeItem('userRole');
  localStorage.removeItem('userInfo');
  params.username = form.value.username;
  params.password = form.value.password;
  
  axios.post('/users/login', params)
    .then(response => {
      message.value = response.data.message
      localStorage.setItem('userId', response.data.userId)
      const userData = response.data.user;
      localStorage.setItem('userId', userData.id)
      localStorage.setItem('userRole', userData.role) // 存储用户角色
      localStorage.setItem('tenantId', userData.tenantId) // 如果有租户ID
      localStorage.setItem('userInfo', JSON.stringify(userData))
      setTimeout(() => {
        router.push('/home')
      }, 1000)
    })
    .catch(error => {
      message.value = '登录失败: ' + (error.response ? error.response.data.message : error.message)
      ElMessage.error(message.value)
      refreshCode()
    })
}

const nextPage = () => {
  if (currentPage.value < totalPages - 1) {
    currentPage.value++
  }
}

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
  }
}

const register = () => {
  if (registerForm.value.captcha !== registerIdentifyCode.value) {
    message.value = '验证码错误'
    ElMessage.error(message.value)
    refreshRegisterCode()
    return
  }
  departmentForm.value.departmentName = tenantForm.value.tenantName
  departmentForm.value.parentDepartment = 1
  departmentForm.value.managerPhone = userForm.value.phoneNumber
  departmentForm.value.manager = userForm.value.username
  departmentForm.value.managerEmail = userForm.value.email
  tenantForm.value.adminUsername = userForm.value.username
  const requestData = {
    tenant: tenantForm.value,
    department: departmentForm.value,
    user: userForm.value
  }
  axios.post('/api/tenants/insert', requestData)
    .then(response => {
      if (response.data.isOK) {
        ElMessage.success('注册成功')
        resetForms()
      } else {
        ElMessage.error('注册失败: ' + (response.data.msg || '未知错误'))
      }
    })
    .catch(error => {
      ElMessage.error('注册失败: ' + (error.response ? error.response.data.message : error.message))
    })
}

const resetForms = () => {
  registerVisible.value = false
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

  departmentForm.value = {
    id: null,
    departmentName: '',
    status: 'Active',
    createdAt: '',
    parentDepartment: 1,
    manager: '',
    managerPhone: '',
    managerEmail: '',
    tenantId: null,
    path: null
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
    avatar: '',
    tenantId: null,
    path: null
  }
  registerForm.value = {
    captcha: ''
  }
}
</script>

<style scoped>
body {
  font-family: Arial, sans-serif;
  background-color: #f5f5f5;
  margin: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 2000px;
  height: 100%; /* 设置容器高度 */
  background-color: white;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  overflow: hidden;
  margin: auto;
  background: url('/static/images/bg6.jpg');
}

.login-left {
  width: 20%;
  height: 70%;
  padding: 10px;
  margin: 110px;
  background-color: #F4F6FA;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  align-items: center;
  margin-left: 250px;
}

.login-box {
  width: 100%;
  padding: 20px;
  height: 100%;
  background: #F4F6FA;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  border-radius: 20px;
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  margin-left: 20px;
}

.logo-image {
  display: block;
  margin: 0 auto 20px;
  height: 40px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group .el-input {
  width: 100%;
}

.options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forgot-password {
  color: #007BFF;
  text-decoration: none;
}

.forgot-password:hover {
  text-decoration: underline;
}

.login-button {
  width: 100%;
  padding: 10px;
  background-color: #FF4B4B;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.login-button:hover {
  background-color: #FF3333;
}

.register {
  text-align: center;
  margin-top: 10px;
}

.register a {
  color: #007BFF;
  text-decoration: none;
}

.register a:hover {
  text-decoration: underline;
}

.footer {
  text-align: center;
}

.footer p {
  font-size: 12px;
  color: #999;
}

.footer a {
  color: #007BFF;
  text-decoration: none;
}

.footer a:hover {
  text-decoration: underline;
}

.login-right {
  width: 50%;
  height: 75%;
  background: url('/static/images/bg4.jpg') no-repeat center center;
  background-size: cover;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: center;
  margin-top: 100px;
  margin-left: 140px;
  box-shadow: 0 20px 22px rgba(0, 0, 0, 0.2); /* 添加阴影效果 */
}

.welcome-message {
  background-color: rgba(0, 0, 0, 0.5);
  padding: 40px;
  border-radius: 10px;
}

.welcome-message h1 {
  font-size: 2.5em;
  margin-bottom: 20px;
}

.welcome-message p {
  font-size: 1.2em;
  margin-bottom: 20px;
}

.register-container {
  padding: 20px;
  height:100%;
}

.register-container h3 {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

.register-container .form-group {
  margin-bottom: 15px;
}

.register-container .form-group .el-input,
.register-container .form-group input {
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.dialog-footer .el-button {
  margin: 0 10px;
}
</style>
