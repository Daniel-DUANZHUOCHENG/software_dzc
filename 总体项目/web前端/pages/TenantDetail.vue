<template>
  <div class="tenant-detail-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>租户详情</h1>
        <p>查看租户的详细信息和相关数据</p>
      </div>
      <div class="header-actions">
        <el-button @click="goBack" class="action-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <el-button type="primary" @click="editTenant" class="action-btn" v-if="isAdmin">
          <el-icon><Edit /></el-icon>
          编辑
        </el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content" v-loading="loading">
      <div class="tenant-info-section">
        <el-card class="info-card">
          <div class="tenant-header">
            <div class="tenant-avatar-container">
              <div class="tenant-avatar">
                <img 
                  :src="getTenantIcon(tenant.icon)" 
                  :alt="tenant.tenantName" 
                  @error="handleImageError"
                  @click="showImagePreview(getPreviewableImageUrl(tenant.icon))"
                  title="点击预览大图"
                />
              </div>
              <div class="avatar-actions" v-if="isAdmin">
                <el-button 
                  type="danger" 
                  size="small" 
                  :icon="Delete"
                  @click="deleteTenantIcon"
                  title="删除图标"
                >
                  删除图标
                </el-button>
              </div>
            </div>
            <div class="tenant-basic-info">
              <h2>{{ tenant.tenantName }}</h2>
              <p class="tenant-description" v-html="tenant.remark || '暂无描述'"></p>
              <div class="tenant-meta">
                <span class="meta-item">
                  <el-icon><User /></el-icon>
                  联系人：{{ tenant.contactPerson }}
                </span>
                <span class="meta-item">
                  <el-icon><Phone /></el-icon>
                  电话：{{ tenant.phone }}
                </span>
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  创建时间：{{ formatDate(tenant.createdAt) }}
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 统计信息 -->
      <div class="stats-section">
        <el-row :gutter="24">
          <el-col :span="8">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">
                  <el-icon><User /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ userCount }}</div>
                  <div class="stat-label">用户数量</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">
                  <el-icon><OfficeBuilding /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ departmentCount }}</div>
                  <div class="stat-label">部门数量</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">
                  <el-icon><Calendar /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ meetingCount }}</div>
                  <div class="stat-label">会议数量</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 用户列表 - 管理员和租户管理员可见 -->
      <div v-if="!isRegularUser" class="users-section">
        <el-card class="users-card">
          <template #header>
            <div class="card-header">
              <span>用户列表</span>
              <el-button type="primary" size="small" @click="refreshUsers">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          
          <el-table :data="users" style="width: 100%" v-loading="usersLoading">
            <el-table-column prop="username" label="用户名" min-width="120">
              <template #default="{ row }">
                <div class="user-info">
                  <el-avatar :size="32" :src="getUserAvatar(row.avatar)">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <span class="username">{{ row.username }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="nickname" label="昵称" min-width="120" />
            <el-table-column prop="email" label="邮箱" min-width="180" />
            <el-table-column prop="role" label="角色" min-width="100">
              <template #default="{ row }">
                <el-tag :type="row.role === 'Admin' ? 'danger' : 'primary'" size="small">
                  {{ row.role === 'Admin' ? '管理员' : '普通用户' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" min-width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'Active' ? 'success' : 'info'" size="small">
                  {{ row.status === 'Active' ? '正常' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>

    <!-- 图片预览对话框 -->
    <el-dialog 
      v-model="imagePreviewVisible" 
      title="图片预览" 
      width="80%"
      class="image-preview-dialog"
      :close-on-click-modal="true"
    >
      <div class="image-preview-container">
        <el-image 
          :src="previewImageUrl" 
          fit="contain"
          style="width: 100%; max-height: 70vh;"
          :preview-src-list="[previewImageUrl]"
          :initial-index="0"
        />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeImagePreview">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  ArrowLeft, Edit, User, Phone, Calendar, OfficeBuilding, Refresh, Delete 
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../utils/request.js'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const usersLoading = ref(false)
const tenant = ref<any>({})
const users = ref<any[]>([])
const userCount = ref(0)
const departmentCount = ref(0)
const meetingCount = ref(0)
const isAdmin = ref(false)
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

const currentUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
const isRegularUser = computed(() => currentUser.role === 'User')

onMounted(() => {
  const userId = localStorage.getItem('userId')
  if (userId) {
    checkUserRole(userId)
  }
  fetchTenantDetail()
  fetchTenantStats()
  fetchTenantUsers()
})

const checkUserRole = async (userId: string) => {
  try {
    const response = await axios.get(`http://localhost:9049/users/profile/${userId}`)
    isAdmin.value = response.data.role === 'Admin'
  } catch (error) {
    console.error('获取用户角色失败:', error)
  }
}

const fetchTenantDetail = async () => {
  loading.value = true
  try {
    const tenantId = route.params.id
    const response = await axios.get(`http://localhost:9049/tenants/${tenantId}`)
    if (response.data.isOK) {
      tenant.value = response.data.tenant
    }
  } catch (error) {
    console.error('获取租户详情失败:', error)
    ElMessage.error('获取租户详情失败')
  } finally {
    loading.value = false
  }
}

const fetchTenantStats = async () => {
  try {
    const tenantId = route.params.id
    // 这里可以调用后端API获取统计数据
    // 暂时使用模拟数据
    userCount.value = 25
    departmentCount.value = 8
    meetingCount.value = 12
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchTenantUsers = async () => {
  usersLoading.value = true
  try {
    const tenantId = route.params.id
    const response = await axios.get(`http://localhost:9049/users/tenant/${tenantId}`)
    if (response.data.userList) {
      users.value = response.data.userList
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    usersLoading.value = false
  }
}

const getTenantIcon = (iconPath: string) => {
  // 1. 没有icon，返回默认图片
  if (!iconPath || iconPath === 'null' || iconPath === 'undefined') {
    return '/images/default-icon.jpg'
  }
  // 2. 完整URL
  if (iconPath.startsWith('http')) {
    return iconPath
  }
  // 3. 以/开头，认为是后端静态资源
  if (iconPath.startsWith('/')) {
    return `http://localhost:9049${iconPath}`
  }
  // 4. 兜底：假设是上传目录
  return `http://localhost:9049/upload/${iconPath}`
}

const handleImageError = (event: Event) => {
  (event.target as HTMLImageElement).src = '/images/default-icon.jpg'
}

const getUserAvatar = (avatarPath: string) => {
  if (!avatarPath) {
    return ''
  }
  
  if (avatarPath.startsWith('http')) {
    return avatarPath
  }
  
  if (avatarPath.startsWith('/')) {
    return `http://localhost:9049${avatarPath}`
  }
  
  return `http://localhost:9049/${avatarPath}`
}

const formatDate = (date: string) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const goBack = () => {
  router.go(-1)
}

const editTenant = () => {
  router.push(`/tenant-management?edit=${tenant.value.id}`)
}

const refreshUsers = () => {
  fetchTenantUsers()
}

const getPreviewableImageUrl = (iconPath: string) => {
  // 对于默认图片，使用本地路径进行预览
  if (!iconPath || iconPath === 'null' || iconPath === 'undefined') {
    return window.location.origin + '/images/default-icon.jpg'
  }
  
  // 对于其他图片，使用getTenantIcon的逻辑
  return getTenantIcon(iconPath)
}

const showImagePreview = (imageUrl: string) => {
  previewImageUrl.value = imageUrl
  imagePreviewVisible.value = true
}

const closeImagePreview = () => {
  imagePreviewVisible.value = false
  previewImageUrl.value = ''
}

const deleteTenantIcon = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个租户的图标吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // 更新租户信息，将图标设为null
    const updateData = {
      ...tenant.value,
      icon: null
    }
    
    await axios.put(`http://localhost:9049/tenants/${tenant.value.id}`, updateData)
    ElMessage.success('租户图标删除成功')
    
    // 更新本地数据
    tenant.value.icon = null
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除租户图标失败:', error)
      ElMessage.error('删除租户图标失败')
    }
  }
}


</script>

<style scoped>
.tenant-detail-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.page-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.header-content h1 {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.header-content p {
  color: #7f8c8d;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  border-radius: 8px;
  font-weight: 500;
}

/* 图片预览对话框样式 */
.image-preview-dialog .el-dialog__body {
  padding: 20px;
  text-align: center;
}

.image-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.tenant-info-section {
  margin-bottom: 24px;
}

.info-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: none;
}

.tenant-header {
  display: flex;
  align-items: center;
  gap: 24px;
}

.tenant-avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.tenant-avatar {
  width: 120px;
  height: 120px;
  border-radius: 16px;
  overflow: hidden;
  flex-shrink: 0;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.tenant-avatar:hover {
  transform: scale(1.05);
}

.tenant-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-actions {
  display: flex;
  justify-content: center;
}

.tenant-basic-info {
  flex: 1;
}

.tenant-basic-info h2 {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 12px 0;
}

.tenant-description {
  font-size: 16px;
  color: #7f8c8d;
  margin-bottom: 20px;
  line-height: 1.6;
}

.tenant-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #2c3e50;
  font-weight: 500;
}

.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: none;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
}

.users-section {
  margin-bottom: 24px;
}

.users-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username {
  font-weight: 500;
  color: #2c3e50;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .tenant-detail-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .tenant-header {
    flex-direction: column;
    text-align: center;
  }
  
  .tenant-meta {
    justify-content: center;
  }
  
  .stats-section .el-col {
    margin-bottom: 16px;
  }
}
</style> 