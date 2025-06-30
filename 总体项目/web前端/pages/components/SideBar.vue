<template>
  <el-menu
    class="el-menu-vertical-demo"
    background-color="#2B2947"
    text-color="#fff"
    active-text-color="#ffd04b"
  >
    <el-menu-item index="1">
      <router-link to="/home">
        <el-icon><House /></el-icon> 首页
      </router-link>
    </el-menu-item>
    <el-menu-item index="2">
      <router-link to="/user-management">
        <el-icon><User /></el-icon> 用户管理
      </router-link>
    </el-menu-item>
    <el-menu-item v-if="isAdmin" index="3">
      <router-link to="/tenant-management">
        <el-icon><House /></el-icon> 租户管理
      </router-link>
    </el-menu-item>
    <el-menu-item index="4">
      <router-link to="/department-management">
        <el-icon><OfficeBuilding /></el-icon> 部门管理
      </router-link>
    </el-menu-item>
    <el-menu-item index="5">
      <router-link to="/user-profile">
        <el-icon><UserFilled /></el-icon> 个人信息管理
      </router-link>
    </el-menu-item>
    <el-menu-item index="6">
      <router-link to="/course-management">
        <el-icon><Notebook /></el-icon> 课程管理
      </router-link>
    </el-menu-item>
    <el-menu-item index="7">
      <router-link to="/info-management">
        <el-icon><Document /></el-icon> 资讯管理
      </router-link>
    </el-menu-item>
    <el-menu-item index="8">
      <router-link to="/conference-management">
        <el-icon><Calendar /></el-icon> 会议管理
      </router-link>
    </el-menu-item>
    <el-menu-item v-if="isAdmin" index="9">
      <router-link to="/user-behavior-management">
        <el-icon><Calendar /></el-icon> 用户行为管理
      </router-link>
    </el-menu-item>
  </el-menu>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { House, User, OfficeBuilding, UserFilled, Notebook, Document, Calendar } from '@element-plus/icons-vue';
import axios from 'axios';

const isAdmin = ref(false);

const loadUserData = async () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}');
    try {
      if (user.role === 'Admin') {
        isAdmin.value = true;
      }
    } catch (error) {
      console.error('Failed to load user data:', error);
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
</style>
