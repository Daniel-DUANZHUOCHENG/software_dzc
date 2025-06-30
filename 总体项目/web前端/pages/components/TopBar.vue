<template>
  <div class="top-bar">
    <div class="top-container">
      <!-- Logo区域 -->
      <div class="logo-section">
        <router-link to="/home" class="logo-link">
          <img src="/static/logo3.png" alt="LOGO" class="logo-image">
          <span class="logo-text">测盟汇系统</span>
        </router-link>
      </div>

      <!-- 导航菜单区域 -->
      <nav class="nav-section">
        <router-link to="/home" class="nav-item" active-class="active">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </router-link>
        
        <el-dropdown class="nav-item" trigger="hover">
          <span class="nav-dropdown">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="() => handleMenuClick('/user-management')">
                <el-icon><User /></el-icon>用户管理
              </el-dropdown-item>
              <el-dropdown-item v-if="isAdmin" @click="() => handleMenuClick('/tenant-management')">
                <el-icon><House /></el-icon>租户管理
              </el-dropdown-item>
              <el-dropdown-item @click="() => handleMenuClick('/department-management')">
                <el-icon><OfficeBuilding /></el-icon>部门管理
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <el-dropdown class="nav-item" trigger="hover">
          <span class="nav-dropdown">
            <el-icon><Document /></el-icon>
            <span>内容管理</span>
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="() => handleMenuClick('/course-management')">
                <el-icon><Notebook /></el-icon>课程管理
              </el-dropdown-item>
              <el-dropdown-item @click="() => handleMenuClick('/info-management')">
                <el-icon><Document /></el-icon>资讯管理
              </el-dropdown-item>
              <el-dropdown-item @click="() => handleMenuClick('/conference-management')">
                <el-icon><Calendar /></el-icon>会议管理
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <el-dropdown v-if="isAdmin" class="nav-item" trigger="hover">
          <span class="nav-dropdown">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据分析</span>
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="() => handleMenuClick('/user-behavior-management')">
                <el-icon><TrendCharts /></el-icon>用户行为分析
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </nav>

      <!-- 用户信息区域 -->
      <div class="user-section">
        <el-dropdown trigger="click" @command="handleUserCommand">
          <div class="user-info">
            <el-avatar v-if="user.avatar" :src="user.avatar" class="user-avatar"></el-avatar>
            <el-avatar v-else class="user-avatar">
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <span class="username">{{ user.username || '用户' }}</span>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><UserFilled /></el-icon>个人信息
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 面包屑导航 -->
    <div class="breadcrumb-section" v-if="breadcrumbs.length > 0">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
          <router-link v-if="item.link" :to="item.link" class="breadcrumb-link">
            {{ item.name }}
          </router-link>
          <span v-else class="breadcrumb-text">{{ item.name }}</span>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { 
  User, House, OfficeBuilding, UserFilled, Notebook, Document, Calendar,
  ArrowDown, DataAnalysis, TrendCharts, SwitchButton, Setting
} from '@element-plus/icons-vue'
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const user = ref<any>({})
const breadcrumbs = ref<any[]>([])
const isAdmin = ref(false)

onMounted(() => {
  loadUserData()
  updateBreadcrumbs(route.meta?.breadcrumb)
})

watch(route, (newRoute) => {
  updateBreadcrumbs(newRoute.meta?.breadcrumb)
})

const loadUserData = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return

  try {
    const response = await axios.get(`http://localhost:9049/users/profile/${userId}`)
    user.value = response.data
    
    if (user.value.avatar === '/avatar/default.jpg' || !user.value.avatar) {
      user.value.avatar = 'http://localhost:9049/avatar/default.jpg'
    } else {
      user.value.avatar = `http://localhost:9049${user.value.avatar}`
    }

    // 检查是否为管理员
    if (user.value.role === 'Admin') {
      isAdmin.value = true
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

const updateBreadcrumbs = (breadcrumb: any) => {
  breadcrumbs.value = breadcrumb || []
}

const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/user-profile')
      break
    case 'logout':
      localStorage.removeItem('userId')
      localStorage.removeItem('user')
      router.push('/')
      ElMessage.success('已退出登录')
      break
  }
}

const handleMenuClick = (path: string) => {
  console.log('Menu clicked:', path)
  
  // 检查用户是否已登录
  const userId = localStorage.getItem('userId')
  
  if (!userId && path !== '/home') {
    ElMessage.warning('请先登录')
    router.push('/')
    return
  }
  
  // 直接使用router.push
  router.push(path).then(() => {
    console.log('Successfully navigated to:', path)
  }).catch((error) => {
    console.error('Navigation failed:', error)
    ElMessage.error('页面跳转失败')
  })
}
</script>

<style scoped>
.top-bar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.top-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
}

.logo-section {
  display: flex;
  align-items: center;
}

.logo-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: white;
  font-weight: 600;
  font-size: 18px;
  transition: all 0.3s ease;
}

.logo-link:hover {
  transform: translateY(-1px);
}

.logo-image {
  height: 36px;
  margin-right: 12px;
  border-radius: 8px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
}

.nav-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  color: white;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-weight: 500;
  cursor: pointer;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-dropdown {
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-section {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.1);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.username {
  font-weight: 500;
  font-size: 14px;
}

.dropdown-icon {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.user-info:hover .dropdown-icon {
  transform: rotate(180deg);
}

.breadcrumb-section {
  background: rgba(255, 255, 255, 0.1);
  padding: 12px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.breadcrumb-link {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: color 0.3s ease;
}

.breadcrumb-link:hover {
  color: white;
}

.breadcrumb-text {
  color: white;
  font-weight: 500;
}

/* Element Plus 下拉菜单样式覆盖 */
:deep(.el-dropdown-menu) {
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: none;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 8px;
  margin: 4px 8px;
  transition: all 0.3s ease;
}

:deep(.el-dropdown-menu__item:hover) {
  background: #f5f7fa;
  transform: translateX(4px);
}

:deep(.el-breadcrumb__item) {
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .top-container {
    padding: 0 16px;
  }
  
  .nav-section {
    display: none;
  }
  
  .logo-text {
    display: none;
  }
}
</style>

