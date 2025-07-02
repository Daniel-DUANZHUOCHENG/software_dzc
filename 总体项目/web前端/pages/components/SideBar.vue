<template>
  <el-menu
    class="el-menu-vertical-demo"
    background-color="#2B2947"
    text-color="#fff"
    active-text-color="#ffd04b"
  >
    <!-- 首页 - 所有用户都可以访问 -->
    <el-menu-item index="1">
      <router-link to="/home">
        <el-icon><House /></el-icon> 首页
      </router-link>
    </el-menu-item>

    <!-- 个人信息管理 - 所有用户都可以访问 -->
    <el-menu-item index="5">
      <router-link to="/user-profile">
        <el-icon><UserFilled /></el-icon> 个人信息管理
      </router-link>
    </el-menu-item>

    <!-- 用户管理 - 所有用户都可以访问（但内部权限控制不同） -->
    <el-menu-item index="2">
      <router-link to="/user-management">
        <el-icon><User /></el-icon> 用户管理
      </router-link>
    </el-menu-item>

    <!-- 租户管理 - 只有系统管理员可以访问 -->
    <el-menu-item v-if="isSystemAdmin" index="3">
      <router-link to="/tenant-management">
        <el-icon><House /></el-icon> 租户管理
      </router-link>
    </el-menu-item>

    <!-- 部门管理 - 只有管理员和租户管理员可以访问，普通用户不显示 -->
    <el-menu-item v-if="isAdminOrTAdmin" index="4">
      <router-link to="/department-management">
        <el-icon><OfficeBuilding /></el-icon> 部门管理
      </router-link>
    </el-menu-item>

    <!-- 课程管理 - 所有用户都可以查看，但编辑权限在页面内控制 -->
    <el-menu-item index="6">
      <router-link to="/course-management">
        <el-icon><Notebook /></el-icon> 课程管理
      </router-link>
    </el-menu-item>

    <!-- 资讯管理 - 所有用户都可以查看，但编辑权限在页面内控制 -->
    <el-menu-item index="7">
      <router-link to="/info-management">
        <el-icon><Document /></el-icon> 资讯管理
      </router-link>
    </el-menu-item>

    <!-- 会议管理 - 所有用户都可以查看，但编辑权限在页面内控制 -->
    <el-menu-item index="8">
      <router-link to="/conference-management">
        <el-icon><Calendar /></el-icon> 会议管理
      </router-link>
    </el-menu-item>

    <!-- 用户行为管理 - 只有系统管理员可以访问 -->
    <el-menu-item v-if="isSystemAdmin" index="9">
      <router-link to="/user-behavior-management">
        <el-icon><TrendCharts /></el-icon> 用户行为管理
      </router-link>
    </el-menu-item>

    <!-- 审核管理 - 管理员和租户管理员可以访问 -->
    <el-menu-item v-if="isAdminOrTAdmin" index="10">
      <router-link to="/approval-management">
        <el-icon><Check /></el-icon> 审核管理
      </router-link>
    </el-menu-item>

    <!-- 系统设置 - 所有角色可见 -->
    <el-menu-item index="13">
      <router-link to="/system-settings">
        <el-icon><Setting /></el-icon> 系统设置
      </router-link>
    </el-menu-item>

    <!-- 普通用户专用菜单项 -->
    <div v-if="isRegularUser" class="user-menu-divider">
      <div class="divider-text">个人功能</div>
    </div>
    
    <!-- 我的课程 - 普通用户查看自己创建的课程 -->
    <el-menu-item v-if="isRegularUser" index="11">
      <router-link to="/my-courses">
        <el-icon><Reading /></el-icon> 我的课程
      </router-link>
    </el-menu-item>

    <!-- 我的会议 - 普通用户查看自己的会议 -->
    <el-menu-item v-if="isRegularUser" index="12">
      <router-link to="/my-meetings">
        <el-icon><Calendar /></el-icon> 我的会议
      </router-link>
    </el-menu-item>
  </el-menu>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue';
import { House, User, OfficeBuilding, UserFilled, Notebook, Document, Calendar, TrendCharts, Check, Reading, Setting } from '@element-plus/icons-vue';

const userRole = ref('');

// 计算权限
const isSystemAdmin = computed(() => userRole.value === 'Admin');
const isAdminOrTAdmin = computed(() => userRole.value === 'Admin' || userRole.value === 'TAdmin');
const isRegularUser = computed(() => userRole.value === 'User');

const loadUserData = () => {
  try {
          const user = JSON.parse(localStorage.getItem('userInfo') || '{}');
    userRole.value = user.role || 'User';
    console.log('👤 当前用户角色:', userRole.value);
    console.log('🔐 权限状态:', {
      系统管理员: isSystemAdmin.value,
      管理员或租户管理员: isAdminOrTAdmin.value,
      普通用户: isRegularUser.value
    });
  } catch (error) {
    console.error('❌ 加载用户数据失败:', error);
    userRole.value = 'User'; // 默认为普通用户
  }
};

onMounted(() => {
  loadUserData();
});
</script>

<style scoped>
.el-menu-vertical-demo {
  width: 150px;
  height: 100vh;
  background-color: #2B2947;
}

.el-menu-item {
  color: white;
}

.el-menu-item a {
  color: white;
  text-decoration: none;
}

.el-menu-item a:hover {
  text-decoration: underline;
}

.user-menu-divider {
  margin: 20px 10px 10px 10px;
  border-top: 1px solid #444;
  padding-top: 10px;
}

.divider-text {
  color: #888;
  font-size: 12px;
  text-align: center;
  margin-bottom: 10px;
}
</style>
