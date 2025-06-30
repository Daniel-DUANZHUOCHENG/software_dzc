<template>
  <div class="user-management-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>用户管理</h1>
        <p>管理系统用户信息，包括用户创建、编辑、删除等操作</p>
      </div>
      <div class="header-actions">
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
              :data="users" 
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
                    >
                      编辑
                    </el-button>
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="confirmDeleteUser(row)"
                      :icon="Delete"
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

    <!-- 添加用户对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      title="添加用户" 
      width="600px"
      class="user-dialog"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="userForm" 
        :rules="userRules" 
        ref="userFormRef"
        label-width="100px"
        class="user-form"
      >
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
                <el-input 
                  v-model="userForm.password" 
                  type="password" 
                  placeholder="请输入密码"
                  show-password
                />
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
                <el-tree
                  :data="treeData"
                  :props="treeProps"
                  show-checkbox
                  node-key="id"
                  :check-strictly="true"
                  @check-change="handleDepartmentCheckChange"
                  class="department-select-tree"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        
        <div class="form-section">
          <h4>其他信息</h4>
          <el-form-item label="备注">
            <el-input 
              v-model="userForm.remark" 
              type="textarea" 
              :rows="3"
              placeholder="请输入备注信息"
            />
          </el-form-item>
        </div>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveUser" :loading="saveLoading">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑用户对话框 -->
    <el-dialog 
      v-model="editDialogVisible" 
      title="编辑用户" 
      width="600px"
      class="user-dialog"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="editUserForm" 
        :rules="userRules" 
        ref="editUserFormRef"
        label-width="100px"
        class="user-form"
      >
        <div class="form-section">
          <h4>基本信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="editUserForm.username" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="editUserForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别">
                <el-select v-model="editUserForm.gender" placeholder="请选择性别" style="width: 100%">
                  <el-option label="男" value="Male" />
                  <el-option label="女" value="Female" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态">
                <el-radio-group v-model="editUserForm.status">
                  <el-radio label="Active">正常</el-radio>
                  <el-radio label="Inactive">停用</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        
        <div class="form-section">
          <h4>联系信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="邮箱">
                <el-input v-model="editUserForm.email" placeholder="请输入邮箱" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号码">
                <el-input v-model="editUserForm.phoneNumber" placeholder="请输入手机号码" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        
        <div class="form-section">
          <h4>权限设置</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="角色">
                <el-select v-model="editUserForm.role" placeholder="请选择角色" style="width: 100%">
                  <el-option label="管理员" value="Admin" />
                  <el-option label="普通用户" value="User" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职位">
                <el-input v-model="editUserForm.position" placeholder="请输入职位" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        
        <div class="form-section">
          <h4>其他信息</h4>
          <el-form-item label="备注">
            <el-input 
              v-model="editUserForm.remark" 
              type="textarea" 
              :rows="3"
              placeholder="请输入备注信息"
            />
          </el-form-item>
        </div>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateUser" :loading="updateLoading">
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
  Edit, Delete, Folder, Warning 
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// 响应式数据
const departmentSearch = ref('')
const searchCriteria = ref({
  username: '',
  phoneNumber: '',
  status: '',
  createdAt: null,
  departmentId: null
})

const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const tableLoading = ref(false)
const searchLoading = ref(false)
const saveLoading = ref(false)
const updateLoading = ref(false)

// 表单引用
const userFormRef = ref()
const editUserFormRef = ref()

const dialogVisible = ref(false)
const editDialogVisible = ref(false)
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

const editUserForm = ref({
  id: null,
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
  departmentId: null
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

// 生命周期
onMounted(() => {
  fetchUsers()
  fetchDepartments()
})

// 方法
const fetchUsers = async () => {
  tableLoading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    // 如果有搜索条件，使用搜索接口
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
      
      const response = await axios.get('http://localhost:9049/users/search', { params: searchParams })
      users.value = response.data.userList || []
      total.value = response.data.userList?.length || 0
    } else {
      // 使用分页接口
      const response = await axios.get('http://localhost:9049/users/page', { params })
      users.value = response.data.users || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    tableLoading.value = false
  }
}

const fetchDepartments = async () => {
  try {
    // 使用现有的部门接口，获取所有部门
    const response = await axios.get('http://localhost:9049/departments/getall', {
      params: { path: '' } // 获取根路径下的所有部门
    })
    
    if (response.data.isOK) {
      // 将平铺的部门列表转换为树形结构
      const departments = response.data.departmentList || []
      treeData.value = buildDepartmentTree(departments)
      filteredTreeData.value = [...treeData.value]
    }
  } catch (error) {
    console.error('获取部门树失败:', error)
    ElMessage.error('获取部门树失败')
  }
}

// 构建部门树形结构
const buildDepartmentTree = (departments: any[]) => {
  const departmentMap = new Map()
  const rootDepartments: any[] = []
  
  // 创建部门映射
  departments.forEach(dept => {
    departmentMap.set(dept.id, {
      ...dept,
      children: [] as any[],
      userCount: 0 // 暂时设为0，后续可以添加用户统计
    })
  })
  
  // 构建树形结构
  departments.forEach(dept => {
    const node = departmentMap.get(dept.id)
    if (dept.parentDepartment && departmentMap.has(dept.parentDepartment)) {
      const parent = departmentMap.get(dept.parentDepartment)
      parent.children.push(node)
    } else {
      rootDepartments.push(node)
    }
  })
  
  return rootDepartments
}

const filterTree = () => {
  if (!departmentSearch.value) {
    filteredTreeData.value = [...treeData.value]
    return
  }
  
  const filterNode = (nodes: any[]) => {
    return nodes.filter(node => {
      const match = node.departmentName.toLowerCase().includes(departmentSearch.value.toLowerCase())
      if (node.children) {
        node.children = filterNode(node.children)
        return match || node.children.length > 0
      }
      return match
    })
  }
  
  filteredTreeData.value = filterNode([...treeData.value])
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
  dialogVisible.value = true
}

const handleEditUser = (row) => {
  editUserForm.value = { ...row }
  editDialogVisible.value = true
}

const handleSaveUser = async () => {
  // 先进行表单校验
  if (!userFormRef.value) {
    ElMessage.error('表单引用未找到')
    return
  }
  
  try {
    await userFormRef.value.validate()
  } catch (error) {
    ElMessage.error('请完善必填信息')
    return
  }
  
  saveLoading.value = true
  try {
    // 确保必填字段不为空
    if (!userForm.value.username || !userForm.value.password || !userForm.value.nickname) {
      ElMessage.error('用户名、密码、昵称不能为空')
      saveLoading.value = false
      return
    }
    
    // 获取当前登录用户信息
    const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
    
    // 准备要发送的数据
    const userData = {
      id: null, // 新用户ID为null
      username: userForm.value.username,
      password: userForm.value.password,
      nickname: userForm.value.nickname,
      phoneNumber: userForm.value.phoneNumber || '',
      email: userForm.value.email || '',
      gender: userForm.value.gender || '',
      departmentId: userForm.value.departmentId || 1, // 确保有部门ID
      status: userForm.value.status || 'Active',
      role: userForm.value.role || 'User',
      createdAt: new Date().toISOString(), // 设置创建时间
      position: userForm.value.position || '',
      remark: userForm.value.remark || '',
      avatar: null,
      tenantId: currentUser.tenantId || 1, // 设置租户ID
      path: null // 路径字段，后端会处理
    }
    
    console.log('发送用户数据:', userData)
    
    const response = await axios.post('http://localhost:9049/users/insert', userData)
    console.log('后端响应:', response.data)
    console.log('响应状态:', response.status)
    
    if (response.data.isOK) {
      ElMessage.success('用户创建成功')
      dialogVisible.value = false
      fetchUsers()
    } else {
      creationErrorMessage.value = response.data.msg || '创建用户失败'
      creationErrorDialogVisible.value = true
    }
  } catch (error) {
    console.error('创建用户失败:', error)
    console.error('错误详情:', error.response?.data)
    creationErrorMessage.value = error.response?.data?.msg || '创建用户失败，请检查网络连接'
    creationErrorDialogVisible.value = true
  } finally {
    saveLoading.value = false
  }
}

const handleUpdateUser = async () => {
  // 先进行表单校验
  if (!editUserFormRef.value) {
    ElMessage.error('表单引用未找到')
    return
  }
  
  try {
    await editUserFormRef.value.validate()
  } catch (error) {
    ElMessage.error('请完善必填信息')
    return
  }
  
  updateLoading.value = true
  try {
    // 确保昵称不为空
    if (!editUserForm.value.nickname) {
      ElMessage.error('昵称不能为空')
      return
    }
    
    await axios.post('http://localhost:9049/users/reset', editUserForm.value)
    ElMessage.success('用户更新成功')
    editDialogVisible.value = false
    fetchUsers()
  } catch (error) {
    console.error('更新用户失败:', error)
    creationErrorMessage.value = error.response?.data?.msg || '更新用户失败'
    creationErrorDialogVisible.value = true
  } finally {
    updateLoading.value = false
  }
}

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

const exportUsers = () => {
  ElMessage.info('导出功能开发中...')
}

const importUsers = () => {
  ElMessage.info('导入功能开发中...')
}

const handleFileChange = () => {
  ElMessage.info('文件上传功能开发中...')
}

const handleDepartmentCheckChange = (data, checked) => {
  if (checked) {
    userForm.value.departmentId = data.id
  }
}
</script>

<style scoped>
.user-management-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
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

/* 对话框样式 */
.user-dialog {
  border-radius: 16px;
}

.user-form {
  max-height: 60vh;
  overflow-y: auto;
}

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
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ecf0f1;
}

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
</style>
