<template>
  <el-card class="box-card">
    <div slot="header" class="clearfix">
      <span>个人信息</span>
    </div>
    <div class="text-center">
      <el-avatar v-if="user.avatar" :src="user.avatar" class="avatar"></el-avatar>
      <i v-else class="el-icon-plus avatar-uploader-icon"></i>
<!--      <el-button type="primary" @click="openAvatarDialog">修改头像</el-button> -->
    </div>
    <el-form :model="user" ref="userForm" label-width="100px" class="user-form">
      <el-form-item label="用户名">
        <el-input v-model="user.username" disabled></el-input>
      </el-form-item>
      <el-form-item label="用户昵称" prop="nickname">
        <el-input v-model="user.nickname"></el-input>
      </el-form-item>
      <el-form-item label="手机号码" prop="phoneNumber">
        <el-input v-model="user.phoneNumber"></el-input>
      </el-form-item>
      <el-form-item label="用户邮箱" prop="email">
        <el-input v-model="user.email"></el-input>
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-select v-model="user.gender" placeholder="请选择性别">
          <el-option label="男" value="male"></el-option>
          <el-option label="女" value="female"></el-option>
          <el-option label="其他" value="other"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属部门">
       <el-input v-model="user.department" disabled></el-input>
<!-- 		<el-input v-model="" disabled ></el-input> -->
      </el-form-item>
      <el-form-item label="用户角色">
        <el-input v-model="user.role" disabled></el-input>
      </el-form-item>
      <el-form-item label="创建日期">
        <el-input v-model="user.createdAt" disabled></el-input>
      </el-form-item>
    </el-form>
    <div class="button-group">
      <el-button type="primary" @click="submitForm">保存修改</el-button>
      <el-button type="warning" @click="openPasswordDialog">修改密码</el-button>
      <el-button type="default" @click="openAvatarDialog">修改头像</el-button>
    </div>
    <!-- 修改头像对话框 -->
    <el-dialog v-model="avatarDialogVisible" title="上传新头像" width="50%">
      <div class="upload-container">
        <el-upload
          class="avatar-uploader"
          action="http://localhost:9049/users/upload-avatar"
          :data="uploadData"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload">
          <el-button size="large" type="primary">选取文件</el-button>
        </el-upload>
        <div class="el-upload__tip">文件格式：jpg, png, 文件大小不能超过 2MB</div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="avatarDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAvatarUpload">确定</el-button>
      </span>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="50%">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPasswordChange">确定</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const user = ref<any>({})
const avatarDialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const uploadData = ref({ userId: localStorage.getItem('userId') })
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

const handleAvatarSuccess = (response: any) => {
  console.log('Avatar upload response:', response)
  
  // 处理不同的响应格式
  let avatarUrl = ''
  if (typeof response === 'string') {
    avatarUrl = response
  } else if (response && response.url) {
    avatarUrl = response.url
  } else {
    avatarUrl = response
  }
  
  // 确保URL格式正确
  if (avatarUrl && !avatarUrl.startsWith('http')) {
    avatarUrl = `http://localhost:9049${avatarUrl.startsWith('/') ? '' : '/'}${avatarUrl}`
  }
  
  user.value.avatar = avatarUrl
  ElMessage.success('头像上传成功')
  avatarDialogVisible.value = false
}

const beforeAvatarUpload = (file: File) => {
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

  const reader = new FileReader();
  reader.onload = (e: any) => {
    previewImage.value = e.target.result;
  };
  reader.readAsDataURL(file);

  return true
}

const openAvatarDialog = () => {
  avatarDialogVisible.value = true
}

const confirmAvatarUpload = () => {
  avatarDialogVisible.value = false
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
        
        const response = await axios.put(`http://localhost:9049/users/updatePassword`, params)
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
        await axios.put('http://localhost:9049/users/update', user.value)
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
  axios.get(`http://localhost:9049/users/profile/${userId}`)
    .then(response => {
      user.value = response.data;
	  user.value.department = "研发部门"
      if (user.value.avatar === '/avatar/default.jpg' || !user.value.avatar) {
        user.value.avatar = 'http://localhost:9049/avatar/default.jpg';
      } else {
        user.value.avatar = `http://localhost:9049${user.value.avatar}`;
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
  width: 100%;
}

.avatar-uploader .el-upload {
  display: block;
  width: 178px;
  height: 178px;
  line-height: 178px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  margin: 0 auto;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
  margin: 0 auto;
}
.text-center {
  text-align: center;
  margin-bottom: 20px;
}

.user-form {
  margin-top: 20px;
}

.button-group {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.button-group .el-button {
  margin: 0 10px;
}

.upload-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
