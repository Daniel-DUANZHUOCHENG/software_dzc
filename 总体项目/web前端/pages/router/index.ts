import { createRouter, createWebHistory } from 'vue-router'

// 导入组件
import Home from '../views/home.vue'
import Login from '../views/login.vue'
import UserManagement from '../UserManagement.vue';
import TenantManagement from '../TenantManagement.vue';
import TenantDetail from '../TenantDetail.vue';
import DepartmentManagement from '../DepartmentManagement.vue';
import UserProfile from '../UserProfile.vue';
import CourseManagement from '../CourseManagement.vue';
import InformationManager from '../InformationManager.vue';
import ConferenceManagement from '../ConferenceManagement.vue';
import UserBehaviorManagement from '../UserBehaviorManagement.vue';
import SystemSettings from '../SystemSettings.vue';
import ApprovalManagement from '../ApprovalManagement.vue';
import register from '../views/register.vue';

const routes = [
  { path: '/', name: 'login', component: Login },
  { path: '/register', name: 'register', component: register },
  { 
    path: '/home', 
    name: 'home', 
    component: Home,
    meta: {
      breadcrumb: [
        { name: '首页', link: '/' },
      ]
    }
  },
  { 
    path: '/user-management', 
    name: 'userManagement', 
    component: UserManagement,
    meta: {
      breadcrumb: [
        { name: '首页', link: '/' },
        { name: '系统管理', link: '/home' },
        { name: '用户管理' }
      ]
    }
  },
  { 
    path: '/tenant-management', 
    name: 'tenantManagement', 
    component: TenantManagement,
    meta: {
      breadcrumb: [
        { name: '首页', link: '/' },
        { name: '系统管理', link: '/home' },
        { name: '租户管理' }
      ]
    }
  },
  { 
    path: '/tenantDetail/:id', 
    name: 'tenantDetail', 
    component: TenantDetail,
    meta: {
      breadcrumb: [
        { name: '首页', link: '/' },
        { name: '系统管理', link: '/home' },
        { name: '租户管理', link: '/tenant-management' },
        { name: '租户详情' }
      ]
    }
  },
  { 
    path: '/department-management', 
    name: 'departmentManagement', 
    component: DepartmentManagement,
    meta: {
      breadcrumb: [
        { name: '首页', link: '/' },
        { name: '系统管理', link: '/home' },
        { name: '部门管理' }
      ]
    }
  },
  { 
    path: '/user-profile', 
    name: 'userProfile', 
    component: UserProfile,
    meta: {
      breadcrumb: [
        { name: '首页', link: '/' },
        { name: '系统管理', link: '/home' },
        { name: '个人信息管理' }
      ]
    }
  },
  { 
    path: '/course-management', 
    name: 'courseManagement', 
    component: CourseManagement,
    meta: {
      breadcrumb: [
        { name: '首页', link: '/' },
        { name: '系统管理', link: '/home' },
        { name: '课程管理' }
      ]
    }
  },
  { 
    path: '/info-management', 
    name: 'infoManagement', 
    component: InformationManager,
    meta: {
      breadcrumb: [
        { name: '首页', link: '/' },
        { name: '系统管理', link: '/home' },
        { name: '资讯管理' }
      ]
    }
  },
  { 
    path: '/conference-management', 
    name: 'conferenceManagement', 
    component: ConferenceManagement,
    meta: {
      breadcrumb: [
        { name: '首页', link: '/' },
        { name: '系统管理', link: '/home' },
        { name: '会议管理' }
      ]
    }
  },
  { 
      path: '/user-behavior-management', 
      name: 'userBehaviorManagement', 
      component: UserBehaviorManagement,
      meta: {
        breadcrumb: [
          { name: '首页', link: '/' },
          { name: '系统管理', link: '/home' },
          { name: '用户行为管理' }
        ]
      }
    },
  { 
    path: '/system-settings', 
    name: 'systemSettings', 
    component: SystemSettings,
    meta: {
      breadcrumb: [
        { name: '首页', link: '/' },
        { name: '系统管理', link: '/home' },
        { name: '系统设置' }
      ]
    }
  },
  { 
    path: '/approval-management', 
    name: 'approvalManagement', 
    component: ApprovalManagement,
    meta: {
      requireAuth: true,
      requireAdmin: true,
      breadcrumb: [
        { name: '首页', link: '/' },
        { name: '系统管理', link: '/home' },
        { name: '审核管理' }
      ]
    }
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
