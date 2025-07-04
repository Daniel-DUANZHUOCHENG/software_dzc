<template>
  <div class="user-management-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>用户管理</h1>
        <p>管理系统用户信息，包括用户创建、编辑、删除等操作</p>
      </div>
      <div class="header-actions" v-if="currentUserRole !== 'User'">
        <el-button type="primary" @click="handleAddUser" class="action-btn">
          <el-icon><Plus /></el-icon>
          添加用户
        </el-button>
        <el-button type="success" @click="importUsers" class="action-btn">
          <el-icon><Upload /></el-icon>
          导入用户
        </el-button>
        <el-button type="warning" @click="exportUsers" class="action-btn">
          <el-icon><Download /></el-icon>
          导出用户
        </el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧部门树 -->
      <div class="sidebar-section">
        <div class="sidebar-header">
          <h3>组织架构</h3>
          <el-button type="text" @click="refreshTree" size="small">
            <el-icon><Refresh /></el-icon>
          </el-button>
        </div>
        
        <div class="search-box">
          <el-input 
            v-model="departmentSearch" 
            placeholder="搜索部门..." 
            :prefix-icon="Search"
            clearable
            @input="filterTree"
          />
        </div>
        
        <div class="tree-container">
          <el-tree
            ref="tree"
            :data="filteredTreeData"
            :props="treeProps"
            node-key="id"
            :expand-on-click-node="false"
            :highlight-current="true"
            @node-click="handleNodeClick"
            class="department-tree"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <el-icon><Folder /></el-icon>
                <span>{{ node.label }}</span>
                <span class="user-count" v-if="data.userCount">({{ data.userCount }})</span>
              </div>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- 右侧用户列表 -->
      <div class="content-section">
        <!-- 搜索和筛选 -->
        <div class="search-section">
          <el-card class="search-card">
            <div class="search-form">
              <div class="search-row">
                <el-input 
                  v-model="searchCriteria.username" 
                  placeholder="用户名"
                  :prefix-icon="User"
                  clearable
                />
                <el-input 
                  v-model="searchCriteria.phoneNumber" 
                  placeholder="手机号码"
                  :prefix-icon="Phone"
                  clearable
                />
                <el-select 
                  v-model="searchCriteria.status" 
                  placeholder="用户状态"
                  clearable
                  class="status-select"
                >
                  <el-option label="正常" value="Active" />
                  <el-option label="停用" value="Inactive" />
                </el-select>
              </div>
              
              <div class="search-row">
                <el-date-picker
                  v-model="searchCriteria.createdAt"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  class="date-picker"
                />
                <div class="search-buttons">
                  <el-button type="primary" @click="handleSearch" :loading="searchLoading">
                    <el-icon><Search /></el-icon>
                    搜索
                  </el-button>
                  <el-button @click="handleReset">
                    <el-icon><Refresh /></el-icon>
                    重置
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 用户表格 -->
        <div class="table-section">
          <el-card class="table-card">
            <div class="table-header">
              <div class="table-info">
                <span class="total-count">共 {{ total }} 条记录</span>
                <span class="current-page">第 {{ currentPage }} 页</span>
              </div>
              <div class="table-actions">
                <el-button type="text" @click="refreshTable" size="small">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>
            </div>
            
            <el-table 
              :data="paginatedUsers" 
              style="width: 100%" 
              class="user-table"
              :loading="tableLoading"
              stripe
              border
            >
              <el-table-column prop="username" label="用户名" min-width="120">
                <template #default="{ row }">
                  <div class="user-info">
                    <el-avatar :size="32" :src="row.avatar">
                      <el-icon><User /></el-icon>
                    </el-avatar>
                    <span class="username">{{ row.username }}</span>
                  </div>
                </template>
              </el-table-column>
              
              <el-table-column prop="nickname" label="昵称" min-width="120" />
              
              <el-table-column prop="email" label="邮箱" min-width="180" />
              
              <el-table-column prop="phoneNumber" label="手机号码" min-width="130" />
              
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
              
              <el-table-column prop="createdAt" label="创建时间" min-width="160">
                <template #default="{ row }">
                  {{ formatDate(row.createdAt) }}
                </template>
              </el-table-column>
              
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="{ row }">
                  <div class="action-buttons">
                    <el-button 
                      type="primary" 
                      size="small" 
                      @click="handleEditUser(row)"
                      :icon="Edit"
                      v-if="currentUserRole !== 'User' || (currentUserRole === 'User' && row.id === currentUserId)"
                    >
                      编辑
                    </el-button>
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="confirmDeleteUser(row)"
                      :icon="Delete"
                      v-if="currentUserRole !== 'User' && row.id !== currentUserId"
                    >
                      删除
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            
            <!-- 分页 -->
            <div class="pagination-wrapper">
              <el-pagination
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                background
              />
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEditMode ? '编辑用户' : '添加用户'" 
      :width="isEditMode ? '80%' : '600px'"
      :close-on-click-modal="false"
      append-to-body
      draggable
    >
      <!-- ADD MODE: v-if="!isEditMode" -->
      <el-form 
        v-if="!isEditMode"
        :model="userForm" 
        :rules="userRules" 
        ref="userFormRef"
        label-width="100px"
        class="user-form"
      >
        <div class="form-section ai-section">
          <h4><el-icon><MagicStick /></el-icon> 智能填充 (AI)</h4>
          <el-form-item label="一句话描述">
            <el-input 
              v-model="aiPromptText"
              type="textarea"
              :rows="3"
              placeholder="例如：创建一个叫李四的用户，职位是产品经理，分配到百度，手机号是13812345678"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleAiParse" :loading="aiParsing" plain>
              <el-icon><Promotion /></el-icon> AI 解析并填充表单
            </el-button>
          </el-form-item>
        </div>
        <div class="form-section">
          <h4>基本信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="userForm.username" placeholder="请输入用户名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="密码" prop="password">
                <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别">
                <el-select v-model="userForm.gender" placeholder="请选择性别" style="width: 100%">
                  <el-option label="男" value="Male" />
                  <el-option label="女" value="Female" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div class="form-section">
          <h4>联系信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="邮箱">
                <el-input v-model="userForm.email" placeholder="请输入邮箱" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号码">
                <el-input v-model="userForm.phoneNumber" placeholder="请输入手机号码" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div class="form-section">
          <h4>权限设置</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="角色">
                <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
                  <el-option label="管理员" value="Admin" />
                  <el-option label="普通用户" value="User" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态">
                <el-radio-group v-model="userForm.status">
                  <el-radio label="Active">正常</el-radio>
                  <el-radio label="Inactive">停用</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="职位">
                <el-input v-model="userForm.position" placeholder="请输入职位" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="部门">
                <el-tree :data="treeData" :props="treeProps" show-checkbox node-key="id" :check-strictly="true" @check-change="handleDepartmentCheckChange" class="department-select-tree" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <div class="form-section">
          <h4>其他信息</h4>
          <el-form-item label="备注">
            <el-input v-model="userForm.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
          </el-form-item>
        </div>
      </el-form>

      <!-- EDIT MODE: v-else -->
      <div v-else>
        <div class="form-section ai-section">
          <h4><el-icon><MagicStick /></el-icon> AI 辅助修改</h4>
          <el-form-item label="修改指令" label-width="100px">
            <el-input v-model="aiPromptText" type="textarea" :rows="3" placeholder="例如：将职位改为高级软件工程师，并更新手机号为188..." />
          </el-form-item>
          <el-form-item label-width="100px">
            <el-button type="primary" @click="handleAiParse" :loading="aiParsing" plain>
              <el-icon><Promotion /></el-icon> AI 解析并填充到右侧
            </el-button>
          </el-form-item>
        </div>
        <div class="edit-container">
          <!-- Left Panel -->
          <div class="panel left-panel">
            <h3>修改前</h3>
            <el-form :model="originalUserForm" label-width="80px" disabled>
              <el-form-item label="用户名"><el-input v-model="originalUserForm.username" /></el-form-item>
              <el-form-item label="昵称"><el-input v-model="originalUserForm.nickname" /></el-form-item>
              <el-form-item label="性别"><el-select v-model="originalUserForm.gender" style="width: 100%"><el-option label="男" value="Male" /><el-option label="女" value="Female" /></el-select></el-form-item>
              <el-form-item label="邮箱"><el-input v-model="originalUserForm.email" /></el-form-item>
              <el-form-item label="手机号"><el-input v-model="originalUserForm.phoneNumber" /></el-form-item>
              <el-form-item label="职位"><el-input v-model="originalUserForm.position" /></el-form-item>
              <el-form-item label="角色"><el-select v-model="originalUserForm.role" style="width: 100%"><el-option label="管理员" value="Admin" /><el-option label="普通用户" value="User" /></el-select></el-form-item>
              <el-form-item label="状态"><el-radio-group v-model="originalUserForm.status"><el-radio label="Active">正常</el-radio><el-radio label="Inactive">停用</el-radio></el-radio-group></el-form-item>
            </el-form>
          </div>
          <!-- Right Panel -->
          <div class="panel right-panel">
            <h3>修改后 (AI填充 & 可编辑)</h3>
            <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="80px">
              <el-form-item label="用户名" prop="username"><el-input v-model="userForm.username" disabled /></el-form-item>
              <el-form-item label="昵称" prop="nickname"><el-input v-model="userForm.nickname" /></el-form-item>
              <el-form-item label="性别"><el-select v-model="userForm.gender" style="width: 100%"><el-option label="男" value="Male" /><el-option label="女" value="Female" /></el-select></el-form-item>
              <el-form-item label="邮箱"><el-input v-model="userForm.email" /></el-form-item>
              <el-form-item label="手机号"><el-input v-model="userForm.phoneNumber" /></el-form-item>
              <el-form-item label="职位"><el-input v-model="userForm.position" /></el-form-item>
              <el-form-item label="角色"><el-select v-model="userForm.role" style="width: 100%"><el-option label="管理员" value="Admin" /><el-option label="普通用户" value="User" /></el-select></el-form-item>
              <el-form-item label="状态"><el-radio-group v-model="userForm.status"><el-radio label="Active">正常</el-radio><el-radio label="Inactive">停用</el-radio></el-radio-group></el-form-item>
              <el-form-item label="部门"><el-tree :data="treeData" :props="treeProps" show-checkbox node-key="id" :check-strictly="true" @check-change="handleDepartmentCheckChange" class="department-select-tree" /></el-form-item>
            </el-form>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveUser" :loading="saveLoading">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 错误提示对话框 -->
    <el-dialog 
      v-model="creationErrorDialogVisible" 
      title="操作失败" 
      width="400px"
      class="error-dialog"
    >
      <div class="error-content">
        <el-icon class="error-icon"><Warning /></el-icon>
        <p>{{ creationErrorMessage }}</p>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="creationErrorDialogVisible = false">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 隐藏的文件输入 -->
    <input type="file" ref="fileInput" style="display: none;" @change="handleFileChange">
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { 
  Plus, Upload, Download, Refresh, Search, User, Phone, 
  Edit, Delete, Folder, Warning, MagicStick, Promotion
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../utils/request.js'

// 响应式数据
const aiPromptText = ref('')
const aiParsing = ref(false)
const originalUserForm = ref({}) // 新增：用于存放修改前的用户数据

const isEditMode = ref(false) // 是否为编辑模式
const departmentSearch = ref('')
const searchCriteria = ref({
  username: '',
  phoneNumber: '',
  status: '',
  createdAt: null,
  departmentId: null
})

const users = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const tableLoading = ref(false)
const searchLoading = ref(false)
const saveLoading = ref(false)
const updateLoading = ref(false)

// 表单引用
const userFormRef = ref()
const fileInput = ref()

const dialogVisible = ref(false)
const creationErrorDialogVisible = ref(false)
const creationErrorMessage = ref('')

const treeData = ref<any[]>([])
const filteredTreeData = ref<any[]>([])
const treeProps = {
  children: 'children',
  label: 'departmentName'
}

const userForm = ref({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phoneNumber: '',
  role: 'User',
  gender: '',
  status: 'Active',
  position: '',
  remark: '',
  departmentId: 1 // 设置默认部门ID
})

const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ]
}

// 计算属性 - 获取当前用户信息
const currentUserRole = computed(() => {
  const user = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return user.role || 'User'
})

const currentUserId = computed(() => {
  const user = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return user.id
})

// 计算属性 - 实现前端分页
const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return users.value.slice(start, end)
})

// 生命周期
onMounted(() => {
  fetchUsers()
  fetchDepartments()
})

// 方法
const fetchUsers = async () => {
  if (tableLoading.value) return
  
  tableLoading.value = true
  try {
    const currentUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
    console.log('🔍 获取用户列表，当前用户角色:', currentUser.role)
    
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }

    let response
    
    // 根据用户角色获取不同的用户数据
    if (currentUser.role === 'Admin') {
      // 系统管理员：获取所有用户
      if (searchCriteria.value.username || searchCriteria.value.phoneNumber || 
          searchCriteria.value.status || searchCriteria.value.createdAt || 
          searchCriteria.value.departmentId) {
        const searchParams = {
          username: searchCriteria.value.username || undefined,
          phoneNumber: searchCriteria.value.phoneNumber || undefined,
          status: searchCriteria.value.status || undefined,
          startDate: searchCriteria.value.createdAt?.[0] || undefined,
          endDate: searchCriteria.value.createdAt?.[1] || undefined
        }
        response = await axios.get('http://localhost:9049/users/search', { params: searchParams })
        users.value = response.data.userList || []
        total.value = response.data.userList?.length || 0
      } else {
        response = await axios.get('http://localhost:9049/users/all')
        users.value = response.data.userList || []
        total.value = response.data.userList?.length || 0
      }
      console.log('👑 系统管理员：获取所有用户', users.value.length, '个')
    } else if (currentUser.role === 'TAdmin') {
      // 租户管理员：只获取本租户的用户
      response = await axios.get(`http://localhost:9049/users/tenant/${currentUser.tenantId}`)
      users.value = response.data.userList || []
      total.value = response.data.userList?.length || 0
      console.log('🏢 租户管理员：获取租户', currentUser.tenantId, '的用户', users.value.length, '个')
    } else {
      // 普通用户：只能查看和编辑自己的信息
      users.value = [currentUser]
      total.value = 1
      console.log('👤 普通用户：只显示自己的信息')
    }
    
    // 确保用户列表中每个用户都有头像
    users.value = users.value.map(user => {
      if (!user.avatar || user.avatar === '/avatar/default.jpg' || user.avatar === 'null') {
        user.avatar = '/images/profile.jpg'
      } else if (!user.avatar.startsWith('http')) {
        user.avatar = `http://localhost:9049${user.avatar}`
      }
      return user
    })
    
  } catch (error) {
    console.error('❌ 获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
    users.value = []
    total.value = 0
  } finally {
    tableLoading.value = false
  }
}

const fetchDepartments = async () => {
  try {
    // 获取当前用户信息
    const currentUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
    console.log('🔍 当前用户信息:', currentUser)
    
    let departmentResponse
    let tenantResponse
    
    // 根据用户角色获取不同的数据
    if (currentUser.role === 'Admin') {
      // 系统管理员：获取所有租户和部门
      try {
        const [deptRes, tenantRes] = await Promise.all([
          axios.get('http://localhost:9049/departments/getall', { params: { path: '' } }),
          axios.get('http://localhost:9049/api/tenants/all')
        ])
        departmentResponse = deptRes
        tenantResponse = tenantRes
        console.log('👑 系统管理员：获取所有租户和部门')
      } catch (error) {
        console.log('尝试单独获取部门数据...')
        departmentResponse = await axios.get('http://localhost:9049/departments/getall', {
          params: { path: '' }
        })
      }
    } else if (currentUser.role === 'TAdmin') {
      // 租户管理员：只获取本租户的部门和租户信息
      try {
        const [deptRes, tenantRes] = await Promise.all([
          axios.get('http://localhost:9049/departments/getByTenantId', {
            params: { tenantId: currentUser.tenantId }
          }),
          axios.get(`http://localhost:9049/tenants/${currentUser.tenantId}`)
        ])
        departmentResponse = deptRes
        tenantResponse = tenantRes
        console.log('🏢 租户管理员：获取租户', currentUser.tenantId, '的部门')
      } catch (error) {
        console.log('尝试单独获取部门数据...')
        departmentResponse = await axios.get('http://localhost:9049/departments/getByTenantId', {
          params: { tenantId: currentUser.tenantId }
        })
      }
    } else {
      // 普通用户：只获取自己部门的信息
      if (currentUser.departmentId) {
        departmentResponse = await axios.get('http://localhost:9049/departments/getByDepartmentId', {
          params: { departmentId: currentUser.departmentId }
        })
        console.log('👤 普通用户：获取部门', currentUser.departmentId, '的信息')
      } else {
        treeData.value = []
        filteredTreeData.value = []
        console.log('⚠️ 用户没有部门信息，显示空组织架构')
        return
      }
    }
    
    if (departmentResponse && departmentResponse.data.isOK) {
      const departments = departmentResponse.data.departmentList || []
      const tenants = tenantResponse?.data?.data || tenantResponse?.data?.tenants || []
      
      console.log('📋 获取到的部门列表:', departments)
      console.log('🏢 获取到的租户列表:', tenants)
      
      treeData.value = buildTenantDepartmentTree(departments, tenants, currentUser)
      filteredTreeData.value = [...treeData.value]
    } else {
      console.log('⚠️ 获取部门数据失败')
      treeData.value = []
      filteredTreeData.value = []
    }
  } catch (error) {
    console.error('❌ 获取部门树失败:', error)
    // 备用方案
    try {
      const response = await axios.get('http://localhost:9049/departments/getall', {
        params: { path: '' }
      })
      
      if (response.data.isOK) {
        const currentUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
        let departments = response.data.departmentList || []
        
        // 在前端进行数据过滤
        if (currentUser.role === 'TAdmin') {
          departments = departments.filter(dept => dept.tenantId === currentUser.tenantId)
        } else if (currentUser.role === 'User') {
          departments = departments.filter(dept => dept.id === currentUser.departmentId)
        }
        
        treeData.value = buildTenantDepartmentTree(departments, [], currentUser)
        filteredTreeData.value = [...treeData.value]
        console.log('🔧 使用备用方案获取部门数据成功')
      }
    } catch (fallbackError) {
      console.error('❌ 备用方案也失败:', fallbackError)
      ElMessage.error('获取部门树失败')
      treeData.value = []
      filteredTreeData.value = []
    }
  }
}

// 构建租户-部门三级树形结构
const buildTenantDepartmentTree = (departments: any[], tenants: any[], currentUser: any) => {
  // 租户图标映射 - 支持多种租户名称格式
  const tenantIcons = {
    '京都动画': '🎬',
    'MAPPA公司': '🎭', 
    'Madhouse公司': '🎪',
    '阿里巴巴集团': '🛒',
    '阿里巴巴': '🛒',
    '腾讯科技': '💬',
    '腾讯': '💬',
    '字节跳动': '📱',
    '百度': '🔍',
    'Baidu': '🔍',
    '华为技术': '📡',
    '华为': '📡',
    '小米科技': '📱',
    '小米': '📱',
    '网易': '🎮',
    'NetEase': '🎮',
    '东北大学软件学院': '🎓',
    '东北大学': '🎓'
  }
  
  // 按租户分组部门
  const tenantMap = new Map()
  const departmentMap = new Map()
  
  // 创建部门映射 - 标准化字段名
  departments.forEach(dept => {
    const standardizedDept = {
      ...dept,
      // 标准化常用字段名
      id: dept.id || dept.Id,
      departmentName: dept.departmentName || dept.DepartmentName,
      manager: dept.manager || dept.Manager,
      tenantId: dept.tenantId || dept.TenantId,
      parentDepartment: dept.parentDepartment || dept.ParentDepartment || dept.parent_id || 0,
      children: [] as any[],
      userCount: 0,
      isLeaf: false,
      type: 'department'
    }
    departmentMap.set(standardizedDept.id, standardizedDept)
    console.log(`📁 创建部门映射: ${standardizedDept.departmentName}(ID:${standardizedDept.id})`)
  })
  
  // 构建部门层级结构 - 正确处理父子关系
  console.log('🏗️ 开始构建部门层级结构，部门总数:', departments.length)
  
  departments.forEach(dept => {
    const deptId = dept.id || dept.Id
    const node = departmentMap.get(deptId)
    if (!node) {
      console.warn(`⚠️ 找不到部门节点: ${deptId}`)
      return
    }
    
    console.log(`📋 处理部门: ${node.departmentName}(ID:${node.id}) 父部门ID:${node.parentDepartment}`)
    
    if (node.parentDepartment && node.parentDepartment !== 0 && departmentMap.has(node.parentDepartment)) {
      // 有父部门，添加到父部门的children中
      const parent = departmentMap.get(node.parentDepartment)
      parent.children.push(node)
      console.log(`  ↳ 添加到父部门: ${parent.departmentName}`)
    } else {
      // 根部门（parentDepartment为0或不存在），按租户分组
      if (!tenantMap.has(node.tenantId)) {
        tenantMap.set(node.tenantId, [])
      }
      tenantMap.get(node.tenantId).push(node)
      console.log(`  ↳ 作为租户${node.tenantId}的根部门`)
    }
  })
  
  console.log('🎯 租户部门分组结果:', tenantMap)
  
  // 构建最终的租户-部门树
  const result: any[] = []
  
  if (currentUser.role === 'Admin') {
    // 系统管理员：显示所有租户
    const allTenantIds = new Set([...tenantMap.keys()])
    
    // 添加租户信息中的租户ID
    if (tenants && tenants.length > 0) {
      tenants.forEach(tenant => {
        allTenantIds.add(tenant.id || tenant.Id)
      })
    }
    
    allTenantIds.forEach(tenantId => {
      const tenant = tenants.find(t => (t.id || t.Id) === tenantId)
      const tenantDepartments = tenantMap.get(tenantId) || []
      
      // 增强租户名获取逻辑，支持多种字段名和预设映射
      let tenantName = `租户${tenantId}`
      if (tenant) {
        tenantName = tenant.tenantName || tenant.name || tenant.Name || tenant.TenantName || `租户${tenantId}`
      } else {
        // 预设的租户ID到名称映射（备用方案）
        const tenantIdMapping = {
          1: '京都动画',
          2: 'MAPPA公司', 
          3: 'Madhouse公司',
          4: '阿里巴巴集团',
          5: '腾讯科技',
          6: '字节跳动',
          7: '百度',
          8: '华为技术',
          9: '小米科技',
          10: '网易',
          11: '东北大学软件学院'
        }
        tenantName = tenantIdMapping[tenantId] || `租户${tenantId}`
      }
      
      console.log(`🏢 租户${tenantId}解析为: "${tenantName}"`)
      const tenantIcon = tenantIcons[tenantName] || '🏢'
      
      result.push({
        id: `tenant-${tenantId}`,
        departmentName: `${tenantIcon} ${tenantName}`,
        tenantId: tenantId,
        type: 'tenant',
        children: tenantDepartments,
        userCount: tenantDepartments.reduce((sum, dept) => sum + (dept.userCount || 0), 0)
      })
    })
  } else if (currentUser.role === 'TAdmin') {
    // 租户管理员：只显示自己的租户
    const tenantId = currentUser.tenantId
    const tenant = tenants.find(t => (t.id || t.Id) === tenantId)
    const tenantDepartments = tenantMap.get(tenantId) || []
    
    // 为租户管理员也使用增强的租户名获取逻辑
    let tenantName = `我的企业`
    if (tenant) {
      tenantName = tenant.tenantName || tenant.name || tenant.Name || tenant.TenantName || `我的企业`
    } else {
      // 预设的租户ID到名称映射（备用方案）
      const tenantIdMapping = {
        1: '京都动画',
        2: 'MAPPA公司', 
        3: 'Madhouse公司',
        4: '阿里巴巴集团',
        5: '腾讯科技',
        6: '字节跳动',
        7: '百度',
        8: '华为技术',
        9: '小米科技',
        10: '网易',
        11: '东北大学软件学院'
      }
      tenantName = tenantIdMapping[tenantId] || `我的企业`
    }
    
    console.log(`🏢 租户管理员租户${tenantId}解析为: "${tenantName}"`)
    const tenantIcon = tenantIcons[tenantName] || '🏢'
    
    result.push({
      id: `tenant-${tenantId}`,
      departmentName: `${tenantIcon} ${tenantName}`,
      tenantId: tenantId,
      type: 'tenant',
      children: tenantDepartments,
      userCount: tenantDepartments.reduce((sum, dept) => sum + (dept.userCount || 0), 0)
    })
  } else {
    // 普通用户：只显示自己部门在租户下的位置，构建树形结构显示自己的部门位置
    const userDept = departmentMap.get(currentUser.departmentId)
    if (userDept) {
      // 构建用户部门的完整路径树形结构
      const buildUserDepartmentPath = (dept) => {
        const path = [dept]
        let currentDept = dept
        
        // 向上查找父部门，构建完整路径
        while (currentDept.parentDepartment && currentDept.parentDepartment !== 0) {
          const parent = departmentMap.get(currentDept.parentDepartment)
          if (parent) {
            path.unshift(parent)
            currentDept = parent
          } else {
            break
          }
        }
        
        return path
      }
      
      const departmentPath = buildUserDepartmentPath(userDept)
      
      // 找到用户的租户信息
      const userTenantId = currentUser.tenantId
      const tenant = tenants.find(t => (t.id || t.Id) === userTenantId)
      
      // 获取租户名称
      let tenantName = `我的企业`
      if (tenant) {
        tenantName = tenant.tenantName || tenant.name || tenant.Name || tenant.TenantName || `我的企业`
      } else {
        const tenantIdMapping = {
          1: '京都动画',
          2: 'MAPPA公司', 
          3: 'Madhouse公司',
          4: '阿里巴巴集团',
          5: '腾讯科技',
          6: '字节跳动',
          7: '百度',
          8: '华为技术',
          9: '小米科技',
          10: '网易',
          11: '东北大学软件学院'
        }
        tenantName = tenantIdMapping[userTenantId] || `我的企业`
      }
      
      const tenantIcon = tenantIcons[tenantName] || '🏢'
      
      // 构建嵌套的树形结构
      const buildNestedStructure = (path, index = 0) => {
        if (index >= path.length) return []
        
        const currentDept = path[index]
        const nextDepts = buildNestedStructure(path, index + 1)
        
        return [{
          ...currentDept,
          children: nextDepts,
          userCount: index === path.length - 1 ? 1 : 0, // 只有用户所在的最终部门显示用户数
          type: 'department'
        }]
      }
      
      const nestedDepartments = buildNestedStructure(departmentPath)
      
      // 创建租户节点包含用户的部门路径
      result.push({
        id: `tenant-${userTenantId}`,
        departmentName: `${tenantIcon} ${tenantName}`,
        tenantId: userTenantId,
        type: 'tenant',
        children: nestedDepartments,
        userCount: 1
      })
    }
  }
  
  return result
}

const filterTree = () => {
  if (!departmentSearch.value) {
    filteredTreeData.value = [...treeData.value]
    return
  }
  
  const searchTerm = departmentSearch.value.toLowerCase().trim()
  console.log('🔍 搜索部门关键词:', searchTerm)
  
  const filterNode = (nodes: any[]): any[] => {
    return nodes.filter(node => {
      // 检查当前节点是否匹配
      const nameMatch = node.departmentName && node.departmentName.toLowerCase().includes(searchTerm)
      const managerMatch = node.manager && node.manager.toLowerCase().includes(searchTerm)
      const currentMatch = nameMatch || managerMatch
      
      // 递归过滤子节点
      if (node.children && node.children.length > 0) {
        const filteredChildren = filterNode(node.children)
        node.children = filteredChildren
        
        // 如果当前节点匹配或有匹配的子节点，则保留
        return currentMatch || filteredChildren.length > 0
      }
      
      // 叶子节点，只依赖当前匹配
      return currentMatch
    }).map(node => ({
      ...node,
      // 确保保留过滤后的子节点
      children: node.children || []
    }))
  }
  
  const filtered = filterNode(JSON.parse(JSON.stringify(treeData.value)))
  filteredTreeData.value = filtered
  
  console.log('📋 搜索结果:', filtered.length, '个部门')
}

const handleNodeClick = (data) => {
  searchCriteria.value = {
    ...searchCriteria.value,
    departmentId: data.id
  }
  fetchUsers()
}

const handleSearch = async () => {
  searchLoading.value = true
  currentPage.value = 1
  await fetchUsers()
  searchLoading.value = false
}

const handleReset = () => {
  searchCriteria.value = {
    username: '',
    phoneNumber: '',
    status: '',
    createdAt: null,
    departmentId: null
  }
  currentPage.value = 1
  fetchUsers()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  fetchUsers()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchUsers()
}

const handleAddUser = () => {
  isEditMode.value = false
  dialogVisible.value = true
  aiPromptText.value = '' // 清空AI输入框
  // 重置表单
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
  userForm.value = {
    username: '',
    password: '',
    nickname: '',
    email: '',
    phoneNumber: '',
    role: 'User',
    gender: '',
    status: 'Active',
    position: '',
    remark: '',
    departmentId: 1
  }
}

const handleEditUser = (row) => {
  isEditMode.value = true
  dialogVisible.value = true
  aiPromptText.value = '' // 清空AI输入框

  const cleanRow = { ...row }
  // 确保密码不在表单中显示
  delete cleanRow.password;

  // 使用深拷贝，防止响应式对象互相影响
  originalUserForm.value = JSON.parse(JSON.stringify(cleanRow))
  userForm.value = JSON.parse(JSON.stringify(cleanRow))
}

const handleSaveUser = async () => {
  if (!userFormRef.value) return
  try {
    await userFormRef.value.validate();
  } catch (error) {
    ElMessage.error('请完善必填信息');
    return;
  }

  if (isEditMode.value) {
    // 更新逻辑
    updateLoading.value = true;
    try {
      if (!userForm.value.nickname) {
        ElMessage.error('昵称不能为空');
        return;
      }
      await axios.post('http://localhost:9049/users/reset', userForm.value);
      ElMessage.success('用户更新成功');
      dialogVisible.value = false;
      fetchUsers();
    } catch (error) {
      console.error('更新用户失败:', error);
      creationErrorMessage.value = error.response?.data?.msg || '更新用户失败';
      creationErrorDialogVisible.value = true;
    } finally {
      updateLoading.value = false;
    }
  } else {
    // 创建逻辑
    saveLoading.value = true;
    try {
      if (!userForm.value.username || !userForm.value.password || !userForm.value.nickname) {
        ElMessage.error('用户名、密码、昵称不能为空');
        saveLoading.value = false;
        return;
      }
      const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
      const userData = {
        ...userForm.value,
        id: null,
        createdAt: new Date().toISOString(),
        tenantId: currentUser.tenantId || 1,
        path: null
      };
      const response = await axios.post('http://localhost:9049/users/insert', userData);
      if (response.data.isOK) {
        ElMessage.success('用户创建成功');
        dialogVisible.value = false;
        fetchUsers();
      } else {
        creationErrorMessage.value = response.data.msg || '创建用户失败';
        creationErrorDialogVisible.value = true;
      }
    } catch (error) {
      console.error('创建用户失败:', error);
      creationErrorMessage.value = error.response?.data?.msg || '创建用户失败，请检查网络连接';
      creationErrorDialogVisible.value = true;
    } finally {
      saveLoading.value = false;
    }
  }
};

const confirmDeleteUser = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.username}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    await axios.get(`http://localhost:9049/users/delete`, { 
      params: { id: row.id } 
    })
    ElMessage.success('用户删除成功')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }
  }
}

const refreshTree = () => {
  fetchDepartments()
}

const refreshTable = () => {
  fetchUsers()
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const exportUsers = async () => {
  try {
    const exportData = users.value.map((item, index) => {
      return {
        序号: index + 1,
        用户ID: item.id,
        用户名: item.username,
        昵称: item.nickname,
        邮箱: item.email,
        手机号码: item.phoneNumber,
        角色: item.role === 'Admin' ? '管理员' : '普通用户',
        状态: item.status === 'Active' ? '正常' : '停用',
        性别: item.gender || '',
        职位: item.position || '',
        部门ID: item.departmentId || '',
        租户ID: item.tenantId || '',
        创建时间: formatDate(item.createdAt),
        备注: item.remark || ''
      };
    });
    
    const XLSX = await import('xlsx');
    const { saveAs } = await import('file-saver');
    
    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, '用户列表');
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
    saveAs(blob, `用户列表_${new Date().toLocaleDateString()}.xlsx`);
    
    ElMessage.success('用户列表导出成功');
  } catch (error) {
    console.error('导出失败:', error);
    ElMessage.error('导出失败，请重试');
  }
}

const importUsers = () => {
  if (fileInput.value) {
    fileInput.value.click()
  }
}

const handleFileChange = async (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return
  
  if (!file.name.endsWith('.xlsx') && !file.name.endsWith('.xls')) {
    ElMessage.error('请选择Excel文件')
    return
  }
  
  try {
    const XLSX = await import('xlsx')
    const reader = new FileReader()
    
    reader.onload = (e) => {
      try {
        const data = e.target?.result
        const workbook = XLSX.read(data, { type: 'array' })
        const sheetName = workbook.SheetNames[0]
        const worksheet = workbook.Sheets[sheetName]
        const jsonData = XLSX.utils.sheet_to_json(worksheet)
        
        console.log('导入的用户数据:', jsonData)
        ElMessage.success(`成功读取 ${jsonData.length} 条用户记录`)
        
        // 这里可以进一步处理导入的数据
        // 比如批量创建用户等操作
        
      } catch (error) {
        console.error('解析Excel文件失败:', error)
        ElMessage.error('解析Excel文件失败')
      }
    }
    
    reader.readAsArrayBuffer(file)
  } catch (error) {
    console.error('读取文件失败:', error)
    ElMessage.error('读取文件失败')
  }
  
  // 清空input值，允许重复选择同一文件
  ;(event.target as HTMLInputElement).value = ''
}

const handleDepartmentCheckChange = (data, checked) => {
  if (checked) {
    userForm.value.departmentId = data.id
  }
}

const handleAiParse = async () => {
  if (!aiPromptText.value.trim()) {
    ElMessage.warning('请输入描述信息');
    return;
  }
  aiParsing.value = true;
  try {
    const response = await axios.post('/ai/parse-form/user', { text: aiPromptText.value });
    const data = response.data;

    if (data.error) {
      ElMessage.error('AI解析失败: ' + data.error);
      return;
    }

    // 动态填充表单
    for (const key in data) {
      if (key in userForm.value) {
        userForm.value[key] = data[key];
      }
    }
    
    ElMessage.success('AI填充成功！请核对信息。');

    // 特殊处理部门信息
    if (data.departmentName) {
      console.log(`AI识别到部门为: "${data.departmentName}"。由于部门是树形选择器，需要您手动选择对应的部门。`);
      ElMessage.info(`AI识别到部门为: "${data.departmentName}"，请手动选择。`);
    }

  } catch (error) {
    console.error("AI parse error:", error);
    ElMessage.error('调用AI解析接口失败');
  } finally {
    aiParsing.value = false;
  }
};
</script>

<style scoped>
.user-management-container {
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.user-management-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 25% 25%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

/* 页面标题 */
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

/* 主要内容区域 */
.main-content {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
}

/* 侧边栏 */
.sidebar-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.sidebar-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.search-box {
  margin-bottom: 16px;
}

.tree-container {
  max-height: 400px;
  overflow-y: auto;
}

.department-tree {
  border: none;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
}

.user-count {
  color: #7f8c8d;
  font-size: 12px;
}

/* 内容区域 */
.content-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 搜索区域 */
.search-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: none;
}

.search-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-row {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-row .el-input {
  flex: 1;
}

.status-select {
  width: 150px;
}

.date-picker {
  width: 300px;
}

.search-buttons {
  display: flex;
  gap: 12px;
}

/* 表格区域 */
.table-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: none;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ecf0f1;
}

.table-info {
  display: flex;
  gap: 16px;
  color: #7f8c8d;
  font-size: 14px;
}

.total-count {
  font-weight: 600;
  color: #2c3e50;
}

.user-table {
  border-radius: 12px;
  overflow: hidden;
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

.action-buttons {
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 对话框样式 (已被全局样式取代) */
/*
.user-dialog {
  border-radius: 16px;
}

.user-form {
  max-height: 60vh;
  overflow-y: auto;
}
*/

.form-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #ecf0f1;
}

.form-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.form-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #667eea;
}

.department-select-tree {
  width: 100%;
}

/*
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ecf0f1;
}
*/

/* 错误对话框 */
.error-dialog {
  border-radius: 16px;
}

.error-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 0;
}

.error-icon {
  font-size: 48px;
  color: #e74c3c;
}

.error-content p {
  margin: 0;
  font-size: 16px;
  color: #2c3e50;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .sidebar-section {
    order: 2;
  }
}

@media (max-width: 768px) {
  .user-management-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .header-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .search-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-buttons {
    justify-content: center;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-table) {
  border-radius: 12px;
}

:deep(.el-table th) {
  background: #f8f9fa;
  color: #2c3e50;
  font-weight: 600;
}

:deep(.el-table td) {
  border-bottom: 1px solid #ecf0f1;
}

:deep(.el-pagination) {
  margin-top: 20px;
}

:deep(.el-tree-node__content) {
  height: 40px;
  border-radius: 8px;
  margin: 2px 0;
  transition: all 0.3s ease;
}

:deep(.el-tree-node__content:hover) {
  background: rgba(102, 126, 234, 0.1);
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background: rgba(102, 126, 234, 0.2);
  color: #667eea;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-select) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
}

.edit-container {
  display: flex;
  gap: 20px;
}

.panel {
  flex: 1;
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
}

.panel h3 {
  margin-top: 0;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
  font-weight: 600;
}

.left-panel {
  background-color: #f5f5f5;
}

.right-panel {
  background-color: #ffffff;
}
</style>
