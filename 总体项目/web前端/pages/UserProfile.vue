<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <h1>个人信息管理</h1>
        <p class="subtitle">管理您的个人资料和账户设置</p>
      </div>
    </template>
    
    <div class="profile-container">
      <!-- 头像部分 -->
      <div class="avatar-section">
        <div class="text-center">
          <el-avatar 
            v-if="user.avatar" 
            :src="user.avatar" 
            class="avatar clickable-avatar"
            @click="showAvatarPreview"
            title="点击预览头像"
          ></el-avatar>
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </div>
        <div class="avatar-actions">
          <el-button type="primary" @click="openAvatarDialog" class="upload-btn">
            <el-icon><Camera /></el-icon>
            更换头像
          </el-button>
        </div>
      </div>

      <!-- 用户信息表单 -->
      <div class="form-section">
        <el-form :model="user" ref="userFormRef" label-width="120px" class="user-form">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户名">
                <el-input v-model="user.username" disabled></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="真实姓名">
                <el-input v-model="user.realname"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="邮箱">
                <el-input v-model="user.email"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="电话">
                <el-input v-model="user.phone"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="部门">
                <el-input v-model="user.department"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="角色">
                <el-input v-model="user.role" disabled></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        
        <div class="button-group">
          <el-button type="primary" @click="submitForm" size="large">
            <el-icon><Check /></el-icon>
            保存修改
          </el-button>
          <el-button type="warning" @click="openPasswordDialog" size="large">
            <el-icon><Lock /></el-icon>
            修改密码
          </el-button>
        </div>
      </div>
    </div>

    <!-- 头像上传对话框 -->
    <el-dialog v-model="avatarDialogVisible" title="更换头像" width="500px" class="avatar-dialog">
      <div class="upload-container">
        <el-upload
          class="avatar-uploader"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :data="uploadData"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :on-error="handleAvatarError"
          :before-upload="beforeAvatarUpload">
          <el-button size="large" type="primary">选取文件</el-button>
        </el-upload>
        <div v-if="previewImage" class="preview-container">
          <img :src="previewImage" class="preview-image" alt="预览图片">
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="avatarDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAvatarUpload">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="500px" class="password-dialog">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmPasswordChange">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 头像预览对话框 -->
    <el-dialog 
      v-model="avatarPreviewVisible" 
      title="头像预览" 
      width="60%"
      class="avatar-preview-dialog"
      :close-on-click-modal="true"
    >
      <div class="avatar-preview-container">
        <el-image 
          :src="user.avatar" 
          fit="contain"
          style="width: 100%; max-height: 70vh;"
          :preview-src-list="[user.avatar]"
          :initial-index="0"
        />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeAvatarPreview">关闭</el-button>
          <el-button type="primary" @click="openAvatarDialog">更换头像</el-button>
        </div>
      </template>
    </el-dialog>
  </el-card>
</template>

<script lang="ts" setup>
import { ref, onMounted, nextTick } from 'vue'
import { Camera, Check, Lock } from '@element-plus/icons-vue'
import axios from '../utils/request.js'
import { ElMessage } from 'element-plus'

const user = ref<any>({})
const avatarDialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const avatarPreviewVisible = ref(false)
const uploadData = ref({ userId: parseInt(localStorage.getItem('userId') || '1') })
const previewImage = ref<string | null>(null)
const userFormRef = ref()
const passwordFormRef = ref()

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value !== passwordForm.value.newPassword) {
        callback(new Error('两次输入的新密码不一致'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

const uploadUrl = 'http://localhost:9049/users/upload-avatar'
const uploadHeaders = {
  'User-Id': localStorage.getItem('userId') || '1',
  'User-Role': JSON.parse(localStorage.getItem('userInfo') || '{}').role || 'User',
  'User-Tenant-Id': JSON.parse(localStorage.getItem('userInfo') || '{}').tenantId || '1',
  'User-Name': btoa(encodeURIComponent(JSON.parse(localStorage.getItem('userInfo') || '{}').nickname || JSON.parse(localStorage.getItem('userInfo') || '{}').username || 'Unknown'))
}

const handleAvatarSuccess = async (response: any, file: any) => {
  console.log('✅ 头像上传响应:', response)
  console.log('📁 上传文件信息:', file.name, '类型:', file.type)
  
  // 处理用户头像上传接口的响应（后端返回 /avatar/ 路径）
  let avatarUrl = ''
  if (typeof response === 'string') {
    avatarUrl = response
  } else if (response && typeof response === 'object') {
    // 尝试多种可能的字段名
    avatarUrl = response.url || response.data?.url || response.path || response.data?.path || 
               response.fileName || response.data?.fileName || response.filePath || 
               response.data?.filePath || response.imageUrl || response.data?.imageUrl || response;
  }
  
  console.log('🔍 解析到的头像路径:', avatarUrl);
  
  if (avatarUrl) {
    // 处理后端返回的 /avatar/ 路径
    let displayAvatarUrl = avatarUrl;
    
    // 如果是 /avatar/ 开头的路径，构建完整URL用于显示
    if (avatarUrl.startsWith('/avatar/')) {
      displayAvatarUrl = `http://localhost:9049${avatarUrl}`;
    } else if (!avatarUrl.startsWith('http')) {
      // 其他相对路径情况
      displayAvatarUrl = `http://localhost:9049${avatarUrl.startsWith('/') ? avatarUrl : '/' + avatarUrl}`;
    }
    
    console.log('🔗 最终显示的头像URL:', displayAvatarUrl);
    
    // 立即更新显示
    user.value.avatar = displayAvatarUrl;
    
    // 触发其他组件更新（如TopBar）
    localStorage.setItem('userUpdated', Date.now().toString());
    window.dispatchEvent(new CustomEvent('userAvatarUpdated'));
    
    console.log('📡 已通知TopBar更新头像显示');
    
    ElMessage.success('头像上传成功！');
    avatarDialogVisible.value = false;
    previewImage.value = null; // 清空预览
    
    // 强制重新渲染（确保头像立即显示）
    await nextTick();
    
  } else {
    console.error('❌ 响应中未找到头像URL:', response);
    ElMessage.error('头像上传失败：响应中未包含图片URL');
  }
}

const handleAvatarError = (error: any, file: any) => {
  console.error('❌ 头像上传失败:', error);
  let errorMessage = '头像上传失败';
  
  if (error && error.message) {
    errorMessage += ': ' + error.message;
  } else if (error && error.response) {
    errorMessage += ': ' + (error.response.data?.message || error.response.statusText || '服务器错误');
  }
  
  ElMessage.error(errorMessage);
}

const beforeAvatarUpload = (file: File) => {
  console.log('📋 准备上传头像:', {
    文件名: file.name,
    文件类型: file.type,
    文件大小: `${(file.size / 1024 / 1024).toFixed(2)} MB`,
    用户ID: uploadData.value.userId
  });

  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('头像图片必须是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }

  // 确保有有效的用户ID
  if (!uploadData.value.userId || uploadData.value.userId <= 0) {
    const userId = localStorage.getItem('userId');
    uploadData.value.userId = parseInt(userId || '1');
    console.log('🔄 重新设置用户ID:', uploadData.value.userId);
  }

  const reader = new FileReader();
  reader.onload = (e: any) => {
    previewImage.value = e.target.result;
  };
  reader.readAsDataURL(file);

  console.log('✅ 头像文件验证通过，准备上传');
  return true
}

const openAvatarDialog = () => {
  avatarDialogVisible.value = true
  avatarPreviewVisible.value = false // 关闭预览对话框
}

const confirmAvatarUpload = () => {
  avatarDialogVisible.value = false
}

const showAvatarPreview = () => {
  if (user.value.avatar) {
    avatarPreviewVisible.value = true
  }
}

const closeAvatarPreview = () => {
  avatarPreviewVisible.value = false
}

const openPasswordDialog = () => {
  passwordDialogVisible.value = true
}

const confirmPasswordChange = () => {
  passwordFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const userId = localStorage.getItem('userId')
        const params = new URLSearchParams();
        params.append('userId', userId!);
        params.append('oldPassword', passwordForm.value.oldPassword);
        params.append('newPassword', passwordForm.value.newPassword);
        
        const response = await axios.put(`/users/updatePassword`, params)
        ElMessage.success('密码修改成功')
        passwordDialogVisible.value = false
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '密码修改失败')
      }
    } else {
      ElMessage.error('请完整填写表单')
    }
  })
}

const submitForm = () => {
  userFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        await axios.put('/users/update', user.value)
        ElMessage.success('用户信息修改成功')
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '用户信息修改失败')
      }
    } else {
      ElMessage.error('请完整填写表单')
    }
  })
}

onMounted(() => {
  const userId = localStorage.getItem('userId');
  console.log('🔍 当前用户ID:', userId);
  
  // 更新上传数据中的用户ID
  uploadData.value.userId = parseInt(userId || '1');
  console.log('📤 上传数据配置:', uploadData.value);
  
  axios.get(`/users/profile/${userId}`)
    .then(response => {
      user.value = response.data;
      user.value.department = "研发部门"
      
      // 修复头像URL处理逻辑
      console.log('🔍 加载的原始头像路径:', user.value.avatar);
      
      if (!user.value.avatar || user.value.avatar === '/avatar/default.jpg' || 
          user.value.avatar === 'null' || user.value.avatar === null || 
          user.value.avatar === 'undefined') {
        user.value.avatar = '/images/profile.jpg' // 使用本地默认头像
        console.log('📸 使用默认头像:', user.value.avatar);
      } else if (user.value.avatar.startsWith('http')) {
        // 已经是完整URL，直接使用
        console.log('📸 使用完整URL头像:', user.value.avatar);
      } else if (user.value.avatar.startsWith('/avatar/')) {
        // 用户头像路径，构建完整URL
        user.value.avatar = `http://localhost:9049${user.value.avatar}`
        console.log('📸 用户头像完整URL:', user.value.avatar);
      } else if (user.value.avatar.startsWith('/tenants/')) {
        // 租户路径，直接拼接
        user.value.avatar = `http://localhost:9049${user.value.avatar}`
        console.log('📸 租户头像完整URL:', user.value.avatar);
      } else if (user.value.avatar.startsWith('/')) {
        // 其他以/开头的相对路径
        user.value.avatar = `http://localhost:9049${user.value.avatar}`
        console.log('📸 其他相对路径头像URL:', user.value.avatar);
      } else {
        // 不以/开头的相对路径，默认为avatar路径
        user.value.avatar = `http://localhost:9049/avatar/${user.value.avatar}`
        console.log('📸 补全路径的头像URL:', user.value.avatar);
      }
    })
    .catch(error => {
      console.error(error);
      ElMessage.error('获取用户信息失败');
    });
});
</script>

<style scoped>
.box-card {
  max-width: 1200px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  border: none;
  overflow: hidden;
}

.card-header {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin: -20px -20px 0;
}

.card-header h1 {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.profile-container {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 40px;
  padding: 40px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.avatar {
  width: 150px;
  height: 150px;
  border: 4px solid #fff;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.clickable-avatar:hover {
  transform: scale(1.05);
  border-color: #409EFF;
  box-shadow: 0 12px 40px rgba(64, 158, 255, 0.3);
}

.avatar-actions {
  margin-top: 20px;
}

.upload-btn {
  border-radius: 25px;
  padding: 12px 24px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.form-section {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.user-form {
  margin-bottom: 30px;
}

.user-form .el-form-item {
  margin-bottom: 24px;
}

.user-form .el-input {
  border-radius: 12px;
}

.user-form .el-input__wrapper {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.user-form .el-input__wrapper:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.button-group .el-button {
  border-radius: 25px;
  padding: 14px 28px;
  font-size: 16px;
  font-weight: 500;
  min-width: 140px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.button-group .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

/* 对话框样式 */
.avatar-dialog .el-dialog,
.password-dialog .el-dialog {
  border-radius: 20px;
  overflow: hidden;
}

.avatar-dialog .el-dialog__header,
.password-dialog .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
}

.upload-container {
  text-align: center;
  padding: 40px 20px;
}

.avatar-uploader .el-upload {
  border: 2px dashed #d9d9d9;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  padding: 40px;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.preview-container {
  margin-top: 20px;
}

.preview-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.dialog-footer .el-button {
  border-radius: 20px;
  padding: 10px 20px;
  min-width: 100px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
  
  .avatar {
    width: 120px;
    height: 120px;
  }
  
  .button-group {
    flex-direction: column;
    align-items: center;
  }
  
  .button-group .el-button {
    width: 100%;
    max-width: 300px;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.avatar-section,
.form-section {
  animation: fadeInUp 0.6s ease forwards;
}

.avatar-section {
  animation-delay: 0.1s;
}

.form-section {
  animation-delay: 0.2s;
}
</style>
