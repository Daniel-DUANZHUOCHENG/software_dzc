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
            :src="displayAvatar" 
            class="avatar clickable-avatar"
            @click="showAvatarPreview"
            title="点击预览头像"
          ></el-avatar>
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
            <el-col :span="12"><el-form-item label="用户名"><el-input v-model="user.username" disabled></el-input></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="真实姓名"><el-input v-model="user.realname"></el-input></el-form-item></el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12"><el-form-item label="邮箱"><el-input v-model="user.email"></el-input></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="电话"><el-input v-model="user.phone"></el-input></el-form-item></el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12"><el-form-item label="部门"><el-input v-model="user.department"></el-input></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="角色"><el-input v-model="user.role" disabled></el-input></el-form-item></el-col>
          </el-row>
        </el-form>
        
        <div class="button-group">
          <el-button type="primary" @click="saveProfile" size="large">
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

    <!-- 头像更换对话框 -->
    <el-dialog v-model="avatarDialogVisible" title="更换头像" width="600px" class="avatar-dialog">
      <el-tabs v-model="activeTab" class="avatar-tabs">
        <!-- AI 生成 Tab -->
        <el-tab-pane label="AI 生成头像" name="ai">
          <div class="ai-generator-container">
            <p class="ai-prompt-label">请输入你的头像创意描述：</p>
            <el-input
              v-model="aiPrompt"
              placeholder="例如：一只戴着墨镜的宇航员猫，赛博朋克风格"
              :rows="3" type="textarea" clearable maxlength="100" show-word-limit
            />
            <el-button type="primary" @click="generateAvatarWithCoze" :loading="aiLoading" class="generate-btn">
              {{ aiLoading ? '正在生成中...' : '开始生成' }}
            </el-button>
            <div v-if="aiGeneratedImageUrl || aiLoading" class="ai-preview-container">
              <div v-if="aiLoading" class="loading-placeholder">
                <el-icon class="is-loading"><Loading /></el-icon>
                <p>请稍候，AI正在创作中...</p>
              </div>
              <img v-if="aiGeneratedImageUrl && !aiLoading" :src="aiGeneratedImageUrl" class="preview-image" alt="AI 生成的头像预览">
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 本地上传 Tab -->
        <el-tab-pane label="本地上传" name="upload">
          <div class="upload-container">
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl" :headers="uploadHeaders" :data="uploadData"
              :show-file-list="false" :on-success="handleAvatarSuccess" :on-error="handleAvatarError" :before-upload="beforeAvatarUpload"
            >
              <el-button size="large" type="primary">选取文件</el-button>
            </el-upload>
            <div v-if="previewImage" class="preview-container">
              <img :src="previewImage" class="preview-image" alt="预览图片">
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="avatarDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAvatarChange" :disabled="activeTab === 'ai' && !aiGeneratedImageUrl">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="500px" class="password-dialog">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword"><el-input v-model="passwordForm.oldPassword" type="password" show-password></el-input></el-form-item>
        <el-form-item label="新密码" prop="newPassword"><el-input v-model="passwordForm.newPassword" type="password" show-password></el-input></el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword"><el-input v-model="passwordForm.confirmPassword" type="password" show-password></el-input></el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmPasswordChange">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 头像预览对话框 -->
    <el-dialog v-model="avatarPreviewVisible" title="头像预览" width="60%" class="avatar-preview-dialog" :close-on-click-modal="true">
      <div class="avatar-preview-container">
        <el-image 
          :src="displayAvatar" 
          fit="contain" style="width: 100%; max-height: 70vh;"
          :preview-src-list="[displayAvatar]" :initial-index="0"
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
import { ref, onMounted, computed } from 'vue'
import axios from '../utils/request.js'
import { ElMessage } from 'element-plus'
import { Camera, Check, Lock, Loading } from '@element-plus/icons-vue'

const user = ref<any>({})
const avatarDialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const avatarPreviewVisible = ref(false)
const uploadData = ref({ userId: 1 })
const previewImage = ref<string | null>(null)
const userFormRef = ref()
const passwordFormRef = ref()

const activeTab = ref('ai')
const aiPrompt = ref('')
const aiLoading = ref(false)
const aiGeneratedImageUrl = ref<string | null>(null)

const displayAvatar = computed(() => {
  if (!user.value.avatar || typeof user.value.avatar !== 'string') {
    return '/images/profile.jpg';
  }
  if (user.value.avatar.startsWith('http')) {
    return user.value.avatar;
  }
  return `http://localhost:9049${user.value.avatar.startsWith('/') ? user.value.avatar : '/' + user.value.avatar}`;
});

const COZE_API_KEY = 'Bearer pat_0Vd3EZHRCf0LSj9Wl5IZ7qXImvDJ7FwBkCN5KqmtOvMlR3aqk2KOXa3PatnIvEF2';
const COZE_BOT_ID = '7513910253214597132';
const COZE_API_URL = 'https://api.coze.cn/v3/chat';

const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' });
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (rule:any, value:any, callback:any) => {
      if (value !== passwordForm.value.newPassword) {
        callback(new Error('两次输入的新密码不一致'));
      } else {
        callback();
      }
    }, trigger: 'blur' }
  ]
};

const uploadUrl = 'http://localhost:9049/users/upload-avatar';
const uploadHeaders = {
  'User-Id': localStorage.getItem('userId') || '1',
  'User-Role': JSON.parse(localStorage.getItem('userInfo') || '{}').role || 'User',
  'User-Tenant-Id': JSON.parse(localStorage.getItem('userInfo') || '{}').tenantId || '1',
  'User-Name': btoa(encodeURIComponent(JSON.parse(localStorage.getItem('userInfo') || '{}').nickname || JSON.parse(localStorage.getItem('userInfo') || '{}').username || 'Unknown'))
};

const handleAvatarSuccess = (response: any) => {
  let avatarPath = '';
  if (typeof response === 'string') {
    avatarPath = response;
  } else if (response && typeof response === 'object') {
    avatarPath = response.url || response.data?.url || response.path || response.data?.path || response.fileName || response.data?.fileName || response.filePath || response.data?.filePath || response.imageUrl || response.data?.imageUrl || response;
  }
  
  if (avatarPath) {
    user.value.avatar = avatarPath;
    ElMessage.success('文件选取成功，点击“确定”保存。');
  } else {
    ElMessage.error('头像上传失败：响应中未包含图片URL');
  }
};

const handleAvatarError = (error: any) => { ElMessage.error('头像上传失败'); };

const beforeAvatarUpload = (file: File) => {
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!');
    return false;
  }
  previewImage.value = URL.createObjectURL(file);
  return true;
};

const openAvatarDialog = () => {
  avatarDialogVisible.value = true;
  activeTab.value = 'ai';
  aiPrompt.value = '';
  aiGeneratedImageUrl.value = null;
  previewImage.value = null;
  aiLoading.value = false;
};

const confirmAvatarChange = async () => {
  if (activeTab.value === 'ai') {
    if (aiGeneratedImageUrl.value) {
      user.value.avatar = aiGeneratedImageUrl.value;
    } else {
      ElMessage.warning('请先生成头像。');
      return;
    }
  }
  try {
    await saveProfile(true); // 传入一个标志，表示是更换头像
  } catch (e) {
    console.error("保存失败，对话框将保持打开", e);
  }
};

const saveProfile = async (isAvatarChange = false) => {
  try {
    await axios.put('/users/update', user.value);
    
    // 根据操作类型给出不同提示
    const successMessage = isAvatarChange ? '头像更换成功！正在刷新...' : '用户信息修改成功！正在刷新...';
    ElMessage.success(successMessage);
    
    localStorage.setItem('userUpdated', Date.now().toString());
    window.dispatchEvent(new CustomEvent('userAvatarUpdated'));
    
    // 延迟 1.5 秒后刷新页面
    setTimeout(() => {
      window.location.reload();
    }, 500);

    // 如果是通过更换头像调用的，成功后关闭对话框
    if (isAvatarChange) {
        avatarDialogVisible.value = false;
    }

  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '用户信息修改失败');
    throw error;
  }
};

const showAvatarPreview = () => { avatarPreviewVisible.value = true; };
const closeAvatarPreview = () => { avatarPreviewVisible.value = false; };
const openPasswordDialog = () => { passwordDialogVisible.value = true; };

const confirmPasswordChange = () => {
  passwordFormRef.value.validate(async (valid: boolean) => {
    if(valid) {
      try {
        const userId = localStorage.getItem('userId');
        const params = new URLSearchParams();
        params.append('userId', userId!);
        params.append('oldPassword', passwordForm.value.oldPassword);
        params.append('newPassword', passwordForm.value.newPassword);
        
        await axios.put(`/users/updatePassword`, params);
        
        passwordDialogVisible.value = false;
        ElMessage.success('密码修改成功！页面即将刷新。');

        // 延迟刷新
        setTimeout(() => {
          window.location.reload();
        }, 1500);

      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || '密码修改失败');
      }
    }
  });
};

const generateAvatarWithCoze = async () => {
  if (!aiPrompt.value.trim()) {
    ElMessage.warning('请输入您的创意描述！');
    return;
  }
  aiLoading.value = true;
  aiGeneratedImageUrl.value = null;
  try {
    const requestBody = {
      bot_id: COZE_BOT_ID,
      user_id: `user_${localStorage.getItem('userId') || 'anonymous'}`,
      stream: true,
      additional_messages: [{
        role: "user",
        content: `生成一张头像，主题是“${aiPrompt.value}”。要求：风格可爱，画面干净，突出主体，正方形。`,
        content_type: "text"
      }]
    };
    ElMessage.info('任务已提交，正在接收生成结果...');
    const response = await fetch(COZE_API_URL, {
      method: 'POST',
      headers: { 'Authorization': COZE_API_KEY, 'Content-Type': 'application/json', 'Accept': 'text/event-stream' },
      body: JSON.stringify(requestBody)
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.msg || `API 请求失败: ${response.status}`);
    }
    const reader = response.body?.getReader();
    if (!reader) throw new Error('无法获取响应读取器');
    const decoder = new TextDecoder();
    let finalImageUrl: string | null = null;
    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      const chunk = decoder.decode(value, { stream: true });
      const lines = chunk.split('\n');
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const dataStr = line.substring(5).trim();
          if (dataStr === '[DONE]') continue;
          try {
            const data = JSON.parse(dataStr);
            if (data.type === 'tool_response' && data.content?.startsWith('http')) {
              if (!finalImageUrl) { 
                finalImageUrl = data.content;
                console.log('✅ 成功从流中捕获到图片 URL:', finalImageUrl);
              }
            }
          } catch (e) {}
        }
      }
    }
    if (finalImageUrl) {
      aiGeneratedImageUrl.value = finalImageUrl;
      ElMessage.success('头像生成成功！');
    } else {
      throw new Error('未能从AI响应中获取有效图片链接');
    }
  } catch (error: any) {
    ElMessage.error(`生成失败: ${error.message}`);
    console.error('❌ Coze API 调用或处理失败:', error);
  } finally {
    aiLoading.value = false;
  }
};

onMounted(() => {
  const userId = localStorage.getItem('userId');
  if (userId) {
    uploadData.value.userId = parseInt(userId);
    axios.get(`/users/profile/${userId}`)
      .then(response => {
        user.value = response.data;
        user.value.department = "研发部门";
      })
      .catch(error => {
        console.error(error);
        ElMessage.error('获取用户信息失败');
      });
  }
});
</script>

<style scoped>
/* 您的所有样式 */
.box-card { max-width: 1200px; margin: 0 auto; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); border-radius: 20px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1); border: none; overflow: hidden; }
.card-header { text-align: center; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; margin: -20px -20px 0; }
.card-header h1 { font-size: 28px; font-weight: 600; margin: 0 0 8px 0; text-shadow: 0 2px 4px rgba(0,0,0,0.3); }
.subtitle { font-size: 16px; opacity: 0.9; margin: 0; }
.profile-container { display: grid; grid-template-columns: 300px 1fr; gap: 40px; padding: 40px; }
.avatar-section { display: flex; flex-direction: column; align-items: center; background: rgba(255, 255, 255, 0.9); border-radius: 20px; padding: 30px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); backdrop-filter: blur(10px); }
.avatar { width: 150px; height: 150px; border: 4px solid #fff; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15); transition: all 0.3s ease; }
.clickable-avatar:hover { transform: scale(1.05); border-color: #409EFF; box-shadow: 0 12px 40px rgba(64, 158, 255, 0.3); }
.avatar-actions { margin-top: 20px; }
.upload-btn { border-radius: 25px; padding: 12px 24px; font-weight: 500; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4); transition: all 0.3s ease; }
.upload-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6); }
.form-section { background: rgba(255, 255, 255, 0.9); border-radius: 20px; padding: 30px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); backdrop-filter: blur(10px); }
.user-form { margin-bottom: 30px; }
.user-form .el-form-item { margin-bottom: 24px; }
.user-form .el-input { border-radius: 12px; }
.user-form .el-input__wrapper { border-radius: 12px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06); transition: all 0.3s ease; }
.user-form .el-input__wrapper:hover { box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); }
.button-group { display: flex; justify-content: center; gap: 20px; margin-top: 30px; }
.button-group .el-button { border-radius: 25px; padding: 14px 28px; font-size: 16px; font-weight: 500; min-width: 140px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); transition: all 0.3s ease; }
.button-group .el-button:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2); }
.avatar-dialog .el-dialog, .password-dialog .el-dialog { border-radius: 20px; overflow: hidden; }
.avatar-dialog .el-dialog__header, .password-dialog .el-dialog__header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; }
.upload-container { text-align: center; padding: 40px 20px; }
.avatar-uploader .el-upload { border: 2px dashed #d9d9d9; border-radius: 12px; cursor: pointer; position: relative; overflow: hidden; transition: all 0.3s ease; padding: 40px; }
.avatar-uploader .el-upload:hover { border-color: #409eff; background: #f0f9ff; }
.preview-container { margin-top: 20px; }
.preview-image { max-width: 200px; max-height: 200px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); }
.avatar-preview-container { display: flex; justify-content: center; align-items: center; min-height: 300px; background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 12px; padding: 20px; }
.dialog-footer { display: flex; justify-content: center; gap: 15px; }
.dialog-footer .el-button { border-radius: 20px; padding: 10px 20px; min-width: 100px; }
@media (max-width: 768px) {
  .profile-container { grid-template-columns: 1fr; gap: 20px; padding: 20px; }
  .avatar { width: 120px; height: 120px; }
  .button-group { flex-direction: column; align-items: center; }
  .button-group .el-button { width: 100%; max-width: 300px; }
}
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
.avatar-section, .form-section { animation: fadeInUp 0.6s ease forwards; }
.avatar-section { animation-delay: 0.1s; }
.form-section { animation-delay: 0.2s; }
.avatar-tabs { margin-top: -10px; }
.ai-generator-container { padding: 10px 20px; display: flex; flex-direction: column; gap: 15px; }
.ai-prompt-label { font-size: 14px; color: #606266; margin-bottom: -5px; }
.generate-btn { width: 100%; margin-top: 5px; }
.ai-preview-container { margin-top: 15px; width: 100%; min-height: 200px; display: flex; justify-content: center; align-items: center; background-color: #f7f8fa; border-radius: 12px; border: 1px dashed #dcdfe6; }
.loading-placeholder { text-align: center; color: #909399; }
.loading-placeholder .el-icon { font-size: 40px; }
</style>