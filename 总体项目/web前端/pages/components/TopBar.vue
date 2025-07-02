<template>
  <div class="top-bar" :class="{ 'dark-theme': isDarkMode }">
    <!-- 主导航栏 -->
    <div class="main-navbar">
      <div class="navbar-container">
        <!-- Logo区域 -->
        <div class="logo-section">
          <router-link to="/home" class="logo-link">
            <div class="logo-icon">
              <div class="logo-3d">
                <img src="/static/logo3.png" alt="测盟汇" class="logo-image" />
              </div>
            </div>
            <div class="logo-content">
              <span class="logo-title">测盟汇</span>
              <span class="logo-subtitle"></span>
            </div>
          </router-link>
        </div>

        <!-- 导航菜单区域 -->
        <nav class="nav-section">
          <router-link to="/home" class="nav-item" active-class="active">
            <div class="nav-icon">
              <el-icon><House /></el-icon>
            </div>
            <span class="nav-text">工作台</span>
            <div class="nav-indicator"></div>
          </router-link>
          
          <el-dropdown class="nav-dropdown-wrapper" trigger="hover" popper-class="custom-dropdown">
            <div 
              class="nav-item nav-dropdown" 
              :class="{ 'nav-active': activeMenus.isUserManagement }"
              :style="activeMenus.isUserManagement ? {
                'background': 'linear-gradient(135deg, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0.2)) !important',
                'transform': 'translateY(-3px) scale(1.08) !important',
                'box-shadow': '0 10px 35px rgba(0, 0, 0, 0.25), inset 0 2px 0 rgba(255, 255, 255, 0.5), 0 0 25px rgba(255, 255, 255, 0.4)',
                'border': '2px solid rgba(255, 255, 255, 0.6)',
                'z-index': '10'
              } : {}"
            >
              <div class="nav-icon">
                <el-icon><User /></el-icon>
              </div>
              <span class="nav-text">用户管理</span>
              <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              <div class="nav-indicator"></div>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="glass-dropdown">
                <el-dropdown-item 
                  @click="() => handleMenuClick('/user-management')" 
                  :class="['dropdown-item-custom', { 'active-dropdown-item': route.path === '/user-management' }]"
                >
                  <div class="dropdown-icon"><el-icon><User /></el-icon></div>
                  <span>用户管理</span>
                </el-dropdown-item>
                <el-dropdown-item 
                  v-if="isAdmin" 
                  @click="() => handleMenuClick('/tenant-management')" 
                  :class="['dropdown-item-custom', { 'active-dropdown-item': route.path === '/tenant-management' }]"
                >
                  <div class="dropdown-icon"><el-icon><House /></el-icon></div>
                  <span>租户管理</span>
                </el-dropdown-item>
                <el-dropdown-item 
                  @click="() => handleMenuClick('/department-management')" 
                  :class="['dropdown-item-custom', { 'active-dropdown-item': route.path === '/department-management' }]"
                >
                  <div class="dropdown-icon"><el-icon><OfficeBuilding /></el-icon></div>
                  <span>部门管理</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <el-dropdown class="nav-dropdown-wrapper" trigger="hover" popper-class="custom-dropdown">
            <div 
              class="nav-item nav-dropdown" 
              :class="{ 'nav-active': activeMenus.isContentManagement }"
              :style="activeMenus.isContentManagement ? {
                'background': 'linear-gradient(135deg, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0.2)) !important',
                'transform': 'translateY(-3px) scale(1.08) !important',
                'box-shadow': '0 10px 35px rgba(0, 0, 0, 0.25), inset 0 2px 0 rgba(255, 255, 255, 0.5), 0 0 25px rgba(255, 255, 255, 0.4)',
                'border': '2px solid rgba(255, 255, 255, 0.6)',
                'z-index': '10'
              } : {}"
            >
              <div class="nav-icon">
                <el-icon><Document /></el-icon>
              </div>
              <span class="nav-text">内容管理</span>
              <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              <div class="nav-indicator"></div>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="glass-dropdown">
                <el-dropdown-item 
                  @click="() => handleMenuClick('/course-management')" 
                  :class="['dropdown-item-custom', { 'active-dropdown-item': route.path === '/course-management' }]"
                >
                  <div class="dropdown-icon"><el-icon><Notebook /></el-icon></div>
                  <span>课程管理</span>
                </el-dropdown-item>
                <el-dropdown-item 
                  @click="() => handleMenuClick('/info-management')" 
                  :class="['dropdown-item-custom', { 'active-dropdown-item': route.path === '/info-management' }]"
                >
                  <div class="dropdown-icon"><el-icon><Document /></el-icon></div>
                  <span>资讯管理</span>
                </el-dropdown-item>
                <el-dropdown-item 
                  @click="() => handleMenuClick('/conference-management')" 
                  :class="['dropdown-item-custom', { 'active-dropdown-item': route.path === '/conference-management' }]"
                >
                  <div class="dropdown-icon"><el-icon><Calendar /></el-icon></div>
                  <span>会议管理</span>
                </el-dropdown-item>
                <el-dropdown-item 
                  v-if="isAdmin"
                  @click="() => handleMenuClick('/approval-management')" 
                  :class="['dropdown-item-custom', { 'active-dropdown-item': route.path === '/approval-management' }]"
                >
                  <div class="dropdown-icon"><el-icon><Document /></el-icon></div>
                  <span>审核管理</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <el-dropdown v-if="isAdmin" class="nav-dropdown-wrapper" trigger="hover" popper-class="custom-dropdown">
            <div 
              class="nav-item nav-dropdown" 
              :class="{ 'nav-active': activeMenus.isDataAnalysis }"
              :style="activeMenus.isDataAnalysis ? {
                'background': 'linear-gradient(135deg, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0.2)) !important',
                'transform': 'translateY(-3px) scale(1.08) !important',
                'box-shadow': '0 10px 35px rgba(0, 0, 0, 0.25), inset 0 2px 0 rgba(255, 255, 255, 0.5), 0 0 25px rgba(255, 255, 255, 0.4)',
                'border': '2px solid rgba(255, 255, 255, 0.6)',
                'z-index': '10'
              } : {}"
            >
              <div class="nav-icon">
                <el-icon><DataAnalysis /></el-icon>
              </div>
              <span class="nav-text">数据分析</span>
              <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              <div class="nav-indicator"></div>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="glass-dropdown">
                <el-dropdown-item 
                  @click="() => handleMenuClick('/user-behavior-management')" 
                  :class="['dropdown-item-custom', { 'active-dropdown-item': route.path === '/user-behavior-management' }]"
                >
                  <div class="dropdown-icon"><el-icon><TrendCharts /></el-icon></div>
                  <span>用户行为分析</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </nav>

        <!-- 右侧工具栏 -->
        <div class="toolbar-section">
          <!-- 主题切换按钮 -->
          <div class="theme-toggle" @click="toggleTheme">
            <div class="toggle-icon">
              <el-icon v-if="isDarkMode"><Sunny /></el-icon>
              <el-icon v-else><Moon /></el-icon>
            </div>
          </div>

          <!-- 通知按钮 -->
          <el-popover placement="bottom" width="300" trigger="click">
            <template #reference>
              <div class="notification-btn" @click="fetchNotifications">
                <el-badge :value="unreadCount" class="notification-badge" :hidden="unreadCount === 0">
                  <div class="notification-icon">
                    <el-icon><Bell /></el-icon>
                  </div>
                </el-badge>
              </div>
            </template>

            <div v-if="notifications.length === 0" style="text-align:center;padding:20px;color:#999;">
              暂无通知
            </div>
            <el-scrollbar v-else style="max-height:220px;">
              <el-timeline>
                <el-timeline-item
                  v-for="item in notifications"
                  :key="item.id"
                  :timestamp="formatDate(item.applicationTime)"
                  :type="item.type==='pending' ? 'warning' : (item.status==='approved' ? 'success' : 'danger')"
                >
                  <template v-if="item.type==='pending'">
                    用户 <b>{{ item.applicantName }}</b> 申请加入 <b>{{ item.meetingName }}</b>
                  </template>
                  <template v-else>
                    您申请加入 <b>{{ item.meetingName }}</b> 已被 {{ item.status === 'approved' ? '批准' : '拒绝' }}
                  </template>
                </el-timeline-item>
              </el-timeline>
            </el-scrollbar>
          </el-popover>

          <!-- 用户信息区域 -->
          <el-dropdown trigger="click" @command="handleUserCommand" popper-class="user-dropdown">
            <div class="user-info">
              <div class="user-avatar-container">
                <el-avatar :src="user.avatar" class="user-avatar" :size="40">
                  <el-icon><UserFilled /></el-icon>
                </el-avatar>
                <div class="avatar-status"></div>
              </div>
              <div class="user-details">
                <span class="username">{{ user.username || '用户' }}</span>
                <span class="user-role">{{ user.role || '普通用户' }}</span>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown-menu">
                <div class="user-profile-header">
                  <el-avatar :src="user.avatar" :size="60">
                    <el-icon><UserFilled /></el-icon>
                  </el-avatar>
                  <div class="profile-info">
                    <h4>{{ user.username || '用户' }}</h4>
                    <p>{{ user.email || 'user@example.com' }}</p>
                  </div>
                </div>
                <el-dropdown-item command="profile" class="profile-menu-item">
                  <div class="menu-icon"><el-icon><UserFilled /></el-icon></div>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item command="settings" class="profile-menu-item">
                  <div class="menu-icon"><el-icon><Setting /></el-icon></div>
                  <span>系统设置</span>
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided class="profile-menu-item logout-item">
                  <div class="menu-icon"><el-icon><SwitchButton /></el-icon></div>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 面包屑导航 -->
    <div class="breadcrumb-section" v-if="breadcrumbs.length > 0">
      <div class="breadcrumb-container">
        <el-breadcrumb separator="/" class="custom-breadcrumb">
          <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
            <router-link v-if="item.link" :to="item.link" class="breadcrumb-link">
              {{ item.name }}
            </router-link>
            <span v-else class="breadcrumb-text">{{ item.name }}</span>
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { 
  User, House, OfficeBuilding, UserFilled, Notebook, Document, Calendar,
  ArrowDown, DataAnalysis, TrendCharts, SwitchButton, Setting, Moon, Sunny, Bell
} from '@element-plus/icons-vue'
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '../../utils/request.js'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const user = ref<any>({})
const breadcrumbs = ref<any[]>([])
const isAdmin = ref(false)
const isDarkMode = ref(false)

// 计算当前激活的菜单项
const activeMenus = computed(() => {
  const currentPath = route.path
  console.log('🧭 当前路由路径:', currentPath) // 调试用
  
  const result = {
    isUserManagement: ['/user-management', '/tenant-management', '/department-management'].includes(currentPath),
    isContentManagement: ['/course-management', '/info-management', '/conference-management', '/approval-management'].includes(currentPath),
    isDataAnalysis: ['/user-behavior-management'].includes(currentPath)
  }
  
  console.log('📊 菜单激活状态:', result) // 调试用
  return result
})

// 初始化主题
const initTheme = () => {
  const savedTheme = localStorage.getItem('theme')
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  
  if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
    isDarkMode.value = true
    document.documentElement.setAttribute('data-theme', 'dark')
  } else {
    isDarkMode.value = false
    document.documentElement.removeAttribute('data-theme')
  }
}

onMounted(() => {
  loadUserData()
  updateBreadcrumbs(route.meta?.breadcrumb)
  initTheme()
  
  // 监听用户头像更新事件
  window.addEventListener('userAvatarUpdated', () => {
    console.log('🔄 TopBar收到头像更新通知，重新加载用户数据')
    loadUserData()
  })
})

watch(route, (newRoute) => {
  updateBreadcrumbs(newRoute.meta?.breadcrumb)
})

const loadUserData = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return

  try {
    const response = await axios.get(`http://localhost:9049/users/profile/${userId}`)
    console.log('📥 用户信息响应:', response.data)
    
    // 确保响应数据存在
    if (!response.data) {
      console.error('❌ 用户信息响应为空')
      return
    }
    
    user.value = response.data
    
    // 修复头像URL处理逻辑
    console.log('🔍 原始头像路径:', user.value?.avatar);
    
    if (!user.value.avatar || user.value.avatar === '/avatar/default.jpg' || user.value.avatar === 'null' || user.value.avatar === null || user.value.avatar === 'undefined') {
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

    // 检查是否为管理员
    if (user.value.role === 'Admin' || user.value.role === 'TAdmin') {
      isAdmin.value = true
    }
    
    console.log('✅ 用户数据加载完成:', user.value);
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 设置默认数据避免界面错误
    user.value = {
      username: '用户',
      avatar: '/images/profile.jpg'
    }
  }
}

// 添加刷新用户数据的方法
const refreshUserData = () => {
  loadUserData()
}

// 监听localStorage中user数据的变化
const handleStorageChange = (e: StorageEvent) => {
  if (e.key === 'user' || e.key === 'userUpdated') {
    console.log('🔄 检测到用户数据更新，重新加载头像');
    loadUserData()
  }
}

// 添加存储事件监听
onMounted(() => {
  loadUserData()
  updateBreadcrumbs(route.meta?.breadcrumb)
  initTheme()
  window.addEventListener('storage', handleStorageChange)
  
  // 监听自定义事件
  window.addEventListener('userAvatarUpdated', loadUserData)
})

const updateBreadcrumbs = (breadcrumb: any) => {
  breadcrumbs.value = breadcrumb || []
}

const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/user-profile')
      break
    case 'settings':
      router.push('/system-settings')
      break
    case 'logout':
      localStorage.removeItem('userId')
              localStorage.removeItem('userInfo')
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

const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  const html = document.documentElement
  
  if (isDarkMode.value) {
    html.setAttribute('data-theme', 'dark')
    localStorage.setItem('theme', 'dark')
  } else {
    html.removeAttribute('data-theme')
    localStorage.setItem('theme', 'light')
  }
  
  // 通知其他组件主题变化
  window.dispatchEvent(new CustomEvent('theme-change', {
    detail: { isDark: isDarkMode.value }
  }))
  
  console.log('主题已切换为:', isDarkMode.value ? '深色模式' : '浅色模式')
}

// 通知相关状态
const notifications = ref([])
const unreadCount = ref(0)

const fetchNotifications = async () => {
  const user = JSON.parse(localStorage.getItem('userInfo') || '{}')
  try {
    if (user.role === 'TAdmin') {
      const { data } = await axios.get(`/api/meeting-applications/tenant/${user.tenantId}/pending`)
      notifications.value = (data.applications || []).map(a=>({
        ...a,
        type:'pending',
        meetingName: a.meetingName || a.conferenceName || a.conferencename || ''
      }))
      unreadCount.value = notifications.value.length
    } else if (user.role === 'User') {
      const { data } = await axios.get(`/api/meeting-applications/applicant/${user.id}`)
      notifications.value = (data.applications || []).filter(a=>a.status!=='pending').map(a=>({
        ...a,
        type:'result',
        meetingName: a.meetingName || a.conferenceName || a.conferencename || ''
      }))
      unreadCount.value = notifications.value.length
    }
  } catch(e){ console.error('通知拉取失败',e)}
}

onMounted(()=>{ fetchNotifications(); setInterval(fetchNotifications,15000) })

const formatDate = (dt)=>{
  if(!dt) return ''
  return new Date(dt).toLocaleString('zh-CN',{month:'2-digit',day:'2-digit',hour:'2-digit',minute:'2-digit'})
}
</script>

<style scoped>
/* 清新现代配色方案 */
:root {
  --primary-color: #00BCD4;
  --primary-light: #4DD0E1;
  --primary-dark: #0097A7;
  --secondary-color: #81C784;
  --accent-color: #FF7043;
  --success-color: #66BB6A;
  --warning-color: #FFCA28;
  --error-color: #EF5350;
  
  --surface-color: rgba(255, 255, 255, 0.98);
  --surface-elevated: rgba(255, 255, 255, 0.95);
  --text-primary: #263238;
  --text-secondary: #546E7A;
  --text-tertiary: #78909C;
  --text-inverse: #FFFFFF;
  
  --border-color: rgba(0, 188, 212, 0.12);
  --shadow-primary: rgba(0, 188, 212, 0.2);
  --gradient-primary: linear-gradient(135deg, #00BCD4 0%, #81C784 100%);
  --gradient-secondary: linear-gradient(135deg, #4DD0E1 0%, #A5D6A7 100%);
}

[data-theme="dark"] {
  --surface-color: rgba(30, 30, 30, 0.98);
  --surface-elevated: rgba(40, 40, 40, 0.95);
  --text-primary: #FFFFFF;
  --text-secondary: #B0BEC5;
  --text-tertiary: #90A4AE;
  --text-inverse: #263238;
  
  --border-color: rgba(0, 188, 212, 0.2);
  --shadow-primary: rgba(0, 188, 212, 0.3);
}

/* 主容器 */
.top-bar {
  background: var(--gradient-primary);
  color: white;
  position: sticky;
  top: 0;
  z-index: 1000;
  backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 
    0 8px 32px var(--shadow-primary),
    0 2px 8px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  font-family: 'Inter', 'Segoe UI', 'Roboto', sans-serif;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.top-bar.dark-theme {
  background: linear-gradient(135deg, #0F1419 0%, #1A1A1A 50%, #212121 100%);
  border-bottom-color: rgba(0, 188, 212, 0.2);
}

/* 主导航栏 */
.main-navbar {
  position: relative;
  overflow: hidden;
}

.main-navbar::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.1),
    transparent
  );
  animation: shimmer 4s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

.navbar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 72px;
  position: relative;
  z-index: 1;
}

/* Logo区域 */
.logo-section {
  display: flex;
  align-items: center;
}

.logo-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: white;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  gap: 16px;
}

.logo-icon {
  position: relative;
}

.logo-3d {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.05));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.logo-3d:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.logo-image {
  width: 32px;
  height: 32px;
  object-fit: contain;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.logo-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.logo-title {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #ffffff 0%, #e3f2fd 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}

.logo-subtitle {
  font-size: 12px;
  opacity: 0.8;
  font-weight: 400;
  letter-spacing: 0.1em;
}

/* 导航区域 */
.nav-section {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  justify-content: center;
  margin: 0 40px;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  border-radius: 12px;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1), transform 0.3s ease, box-shadow 0.3s ease;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  backdrop-filter: blur(10px);
  border: 1px solid transparent;
  overflow: hidden;
  will-change: transform, box-shadow, background;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.nav-item:hover::before {
  opacity: 1;
}

.nav-item:hover:not(.active):not(.nav-active) {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
  box-shadow: 
    0 8px 25px rgba(0, 0, 0, 0.15),
    0 2px 8px rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.nav-dropdown-wrapper .nav-item.nav-active,
.nav-item.active,
.nav-item.nav-active {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0.2)) !important;
  box-shadow: 0 10px 35px rgba(0, 0, 0, 0.25), inset 0 2px 0 rgba(255, 255, 255, 0.5), 0 0 25px rgba(255, 255, 255, 0.4) !important;
  border: 2px solid rgba(255, 255, 255, 0.6) !important;
  color: white !important;
  transform: translateY(-3px) scale(1.08) !important;
  backdrop-filter: blur(20px) !important;
  animation: pulseGlow 2s ease-in-out infinite !important;
  z-index: 10 !important;
}

.nav-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%) scaleX(0);
  width: 60%;
  height: 2px;
  background: white;
  border-radius: 1px;
  transition: transform 0.3s ease;
}

.nav-item.active .nav-indicator,
.nav-item.nav-active .nav-indicator,
.nav-item:hover .nav-indicator {
  transform: translateX(-50%) scaleX(1);
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.8), white, rgba(255, 255, 255, 0.8));
  height: 3px;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.6);
}

.nav-icon {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.nav-item:hover .nav-icon,
.nav-item.active .nav-icon,
.nav-item.nav-active .nav-icon {
  transform: scale(1.15);
  color: white;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
}

.nav-text {
  font-weight: 500;
  letter-spacing: 0.02em;
}

.dropdown-arrow {
  font-size: 12px;
  margin-left: 4px;
  transition: transform 0.3s ease;
}

.nav-dropdown:hover .dropdown-arrow {
  transform: rotate(180deg);
}

/* 工具栏区域 */
.toolbar-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.theme-toggle,
.notification-btn {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  color: white;
}

.theme-toggle:hover,
.notification-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.toggle-icon,
.notification-icon {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.theme-toggle:hover .toggle-icon {
  transform: rotate(180deg);
}

.notification-badge {
  position: relative;
}

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.user-avatar-container {
  position: relative;
}

.user-avatar {
  border: 2px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.avatar-status {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #4CAF50;
  border: 2px solid white;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-weight: 600;
  font-size: 14px;
  color: white;
  letter-spacing: 0.02em;
}

.user-role {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 400;
}

.dropdown-icon {
  font-size: 14px;
  transition: transform 0.3s ease;
  margin-left: 4px;
}

.user-info:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* 面包屑导航 */
.breadcrumb-section {
  background: rgba(255, 255, 255, 0.1);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.breadcrumb-container {
  padding: 12px 32px;
}

.custom-breadcrumb {
  font-size: 13px;
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .navbar-container {
    padding: 0 24px;
  }
  
  .nav-section {
    margin: 0 20px;
  }
  
  .nav-text {
    display: none;
  }
  
  .nav-item {
    padding: 12px;
  }
}

@media (max-width: 768px) {
  .navbar-container {
    height: 64px;
    padding: 0 16px;
  }
  
  .logo-content {
    display: none;
  }
  
  .nav-section {
    display: none;
  }
  
  .user-details {
    display: none;
  }
}
</style>

<!-- 全局样式 -->
<style>
/* 自定义下拉菜单样式 */
.glass-dropdown {
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(20px) saturate(180%) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 16px !important;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.15),
    0 8px 32px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.6) !important;
  padding: 12px 0 !important;
  min-width: 200px !important;
}

.dropdown-item-custom {
  display: flex !important;
  align-items: center !important;
  gap: 12px !important;
  padding: 12px 16px !important;
  margin: 2px 8px !important;
  border-radius: 12px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  cursor: pointer !important;
  color: #2c3e50 !important;
  font-weight: 500 !important;
  position: relative !important;
  overflow: hidden !important;
}

.dropdown-item-custom:hover {
  background: linear-gradient(135deg, 
    rgba(64, 224, 208, 0.15), 
    rgba(72, 187, 185, 0.12)
  ) !important;
  transform: translateX(6px) scale(1.02) !important;
  color: #1a8b8a !important;
  box-shadow: 
    0 6px 20px rgba(64, 224, 208, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.5) !important;
  border-left: 3px solid #40e0d0 !important;
}

.dropdown-item-custom::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(64, 224, 208, 0.08), 
    transparent
  );
  transition: left 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.dropdown-item-custom:hover::before {
  left: 100%;
}

.dropdown-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 6px;
  background: rgba(64, 224, 208, 0.1);
  color: #40e0d0;
  transition: all 0.3s ease;
}

.dropdown-item-custom:hover .dropdown-icon {
  background: rgba(64, 224, 208, 0.2);
  color: #1a8b8a;
  transform: scale(1.1) rotate(5deg);
}

.active-dropdown-item {
  background: linear-gradient(135deg, 
    rgba(64, 224, 208, 0.2), 
    rgba(72, 187, 185, 0.15)
  ) !important;
  color: #1a8b8a !important;
  border-left: 3px solid #40e0d0 !important;
  font-weight: 600 !important;
}

.active-dropdown-item .dropdown-icon {
  background: rgba(64, 224, 208, 0.25);
  color: #1a8b8a;
}

/* 用户下拉菜单样式 */
:deep(.user-dropdown) {
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(20px) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 16px !important;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.15),
    0 8px 32px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.6) !important;
  padding: 20px 0 !important;
  min-width: 280px !important;
}

.user-dropdown-menu {
  background: transparent !important;
  border: none !important;
  border-radius: 16px !important;
  padding: 0 !important;
  box-shadow: none !important;
}

.user-profile-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 0 20px 16px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  margin-bottom: 8px;
}

.profile-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.profile-info p {
  margin: 0;
  font-size: 14px;
  color: #7f8c8d;
}

.profile-menu-item {
  display: flex !important;
  align-items: center !important;
  gap: 12px !important;
  padding: 12px 20px !important;
  margin: 2px 8px !important;
  border-radius: 12px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  cursor: pointer !important;
  color: #2c3e50 !important;
  font-weight: 500 !important;
  position: relative !important;
  overflow: hidden !important;
}

.profile-menu-item:hover {
  background: linear-gradient(135deg, 
    rgba(64, 224, 208, 0.15), 
    rgba(72, 187, 185, 0.12)
  ) !important;
  transform: translateX(8px) scale(1.02) !important;
  color: #1a8b8a !important;
  box-shadow: 
    0 8px 25px rgba(64, 224, 208, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.6) !important;
  border-left: 4px solid #40e0d0 !important;
}

.profile-menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(64, 224, 208, 0.08), 
    transparent
  );
  transition: left 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.profile-menu-item:hover::before {
  left: 100%;
}

.menu-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 8px;
  background: rgba(64, 224, 208, 0.1);
  color: #40e0d0;
  transition: all 0.3s ease;
}

.profile-menu-item:hover .menu-icon {
  background: rgba(64, 224, 208, 0.2);
  color: #1a8b8a;
  transform: scale(1.1);
}

.logout-item {
  border-top: 1px solid rgba(0, 0, 0, 0.08) !important;
  margin-top: 8px !important;
  padding-top: 16px !important;
}

.logout-item:hover {
  background: linear-gradient(135deg, 
    rgba(231, 76, 60, 0.1), 
    rgba(192, 57, 43, 0.08)
  ) !important;
  color: #e74c3c !important;
  border-left: 4px solid #e74c3c !important;
}

.logout-item:hover .menu-icon {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

/* 通知徽章样式 */
.el-badge__content {
  background: linear-gradient(135deg, #ff4757, #ff6b7a) !important;
  border: 2px solid white !important;
  font-size: 10px !important;
  font-weight: 600 !important;
  box-shadow: 0 2px 8px rgba(255, 71, 87, 0.4) !important;
}

/* 动画增强 */
@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

@keyframes pulseGlow {
  0%, 100% { 
    box-shadow: 
      0 8px 30px rgba(0, 0, 0, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.4),
      0 0 20px rgba(255, 255, 255, 0.3);
  }
  50% { 
    box-shadow: 
      0 12px 40px rgba(0, 0, 0, 0.25),
      inset 0 1px 0 rgba(255, 255, 255, 0.5),
      0 0 30px rgba(255, 255, 255, 0.5);
    transform: translateY(-2px) scale(1.08);
  }
}

@keyframes glow {
  0%, 100% { box-shadow: 0 0 5px var(--primary-color); }
  50% { box-shadow: 0 0 20px var(--primary-color), 0 0 30px var(--primary-color); }
}

/* 深色主题适配 */
[data-theme="dark"] .nav-item.active,
[data-theme="dark"] .nav-item.nav-active {
  background: linear-gradient(135deg, rgba(0, 188, 212, 0.4), rgba(0, 188, 212, 0.2)) !important;
  box-shadow: 
    0 8px 30px rgba(0, 188, 212, 0.3),
    inset 0 1px 0 rgba(0, 188, 212, 0.4),
    0 0 20px rgba(0, 188, 212, 0.4) !important;
  border-color: rgba(0, 188, 212, 0.6) !important;
}

[data-theme="dark"] .glass-dropdown {
  background: rgba(30, 30, 30, 0.95) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
}

[data-theme="dark"] .user-dropdown-menu {
  background: rgba(30, 30, 30, 0.98) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
}

[data-theme="dark"] .dropdown-item-custom,
[data-theme="dark"] .profile-menu-item {
  color: #ffffff !important;
}

[data-theme="dark"] .dropdown-item-custom .dropdown-icon {
  color: #cccccc !important;
}

[data-theme="dark"] .dropdown-item-custom span {
  color: #ffffff !important;
}

[data-theme="dark"] .dropdown-item-custom.active-dropdown-item {
  background: linear-gradient(135deg, #00BCD4, #26C6DA) !important;
  color: white !important;
  font-weight: bold !important;
  transform: translateX(6px) scale(1.03) !important;
  box-shadow: 0 6px 16px rgba(0, 188, 212, 0.6) !important;
  border-left: 4px solid rgba(255, 255, 255, 0.8) !important;
  position: relative !important;
  z-index: 2 !important;
}

[data-theme="dark"] .dropdown-item-custom.active-dropdown-item:hover {
  background: linear-gradient(135deg, #00ACC1, #26C6DA) !important;
  color: white !important;
  transform: translateX(8px) scale(1.05) !important;
  box-shadow: 0 8px 20px rgba(0, 188, 212, 0.7) !important;
  border-left: 5px solid white !important;
}

[data-theme="dark"] .dropdown-item-custom.active-dropdown-item:hover .dropdown-icon {
  color: white !important;
  opacity: 1 !important;
  transform: scale(1.15) !important;
}

[data-theme="dark"] .dropdown-item-custom.active-dropdown-item:hover span {
  color: white !important;
  font-weight: bold !important;
}

[data-theme="dark"] .dropdown-item-custom:hover {
  background: linear-gradient(135deg, rgba(0, 188, 212, 0.2), rgba(0, 188, 212, 0.3)) !important;
  color: #E0F7FA !important;
  font-weight: bold !important;
  transform: translateX(4px) scale(1.02) !important;
  box-shadow: 0 4px 12px rgba(0, 188, 212, 0.4) !important;
  border-left: 3px solid #4DD0E1 !important;
  position: relative !important;
  z-index: 1 !important;
}

[data-theme="dark"] .dropdown-item-custom:hover span {
  color: #E0F7FA !important;
  font-weight: bold !important;
}

[data-theme="dark"] .dropdown-item-custom:hover .dropdown-icon {
  color: #E0F7FA !important;
  opacity: 1 !important;
  transform: scale(1.15) !important;
  filter: brightness(1.1) !important;
}
</style>

