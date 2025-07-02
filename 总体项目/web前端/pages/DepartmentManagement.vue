<template>
  <div class="department-management-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>部门管理</h1>
        <p>管理组织架构，包括部门创建、编辑、删除等操作</p>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-card class="search-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="部门名称">
            <el-input 
              v-model="searchForm.departmentName" 
              placeholder="请输入部门名称"
              clearable
            />
          </el-form-item>
          <el-form-item label="部门状态">
            <el-select 
              v-model="searchForm.status" 
              placeholder="请选择部门状态"
              clearable
              style="width: 200px;"
            >
              <el-option label="正常" value="Active" />
              <el-option label="停用" value="Inactive" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :loading="searchLoading">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 主要内容区域 - 左右分栏布局 -->
    <div class="main-content">
      <!-- 左侧部门树 -->
      <div class="left-panel">
        <el-card class="tree-card">
          <template #header>
            <div class="tree-header">
              <span class="tree-title">部门组织架构</span>
              <div class="tree-actions" v-if="currentUserRole !== 'User'">
                <el-button type="primary" size="small" @click="handleAddDepartment">
                  <el-icon><Plus /></el-icon>
                  新增
                </el-button>
              </div>
            </div>
          </template>
          
          <div class="tree-content">
            <el-tree
              :data="treeData"
              :props="treeProps"
              node-key="id"
              :default-expand-all="false"
              :expand-on-click-node="false"
              :highlight-current="true"
              @node-click="handleNodeClick"
              class="department-tree"
            >
              <template #default="{ node, data }">
                <div class="tree-node">
                  <el-icon class="node-icon">
                    <OfficeBuilding v-if="!data.parentDepartment" />
                    <Folder v-else />
                  </el-icon>
                  <span class="node-label">{{ node.label }}</span>
                  <span class="node-count" v-if="data.children && data.children.length">
                    ({{ data.children.length }})
                  </span>
                </div>
              </template>
            </el-tree>
          </div>
        </el-card>
      </div>

      <!-- 右侧详情区域 -->
      <div class="right-panel">
        <el-card class="detail-card">
          <template #header>
            <div class="detail-header">
              <span class="detail-title">
                {{ selectedDepartment ? `${selectedDepartment.departmentName} - 部门详情` : '部门详情' }}
              </span>
              <div class="detail-actions" v-if="selectedDepartment && currentUserRole !== 'User'">
                <el-button type="primary" size="small" @click="handleAddChild(selectedDepartment)">
                  <el-icon><Plus /></el-icon>
                  新增子部门
                </el-button>
                <el-button type="warning" size="small" @click="handleEdit(selectedDepartment)">
                  <el-icon><Edit /></el-icon>
                  修改
                </el-button>
                <el-button type="danger" size="small" @click="handleDelete(selectedDepartment)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </div>
          </template>
          
          <div class="detail-content" v-if="selectedDepartment">
            <!-- 部门基本信息 -->
            <div class="info-section">
              <h4>基本信息</h4>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="部门名称">
                  {{ selectedDepartment.departmentName }}
                </el-descriptions-item>
                <el-descriptions-item label="部门状态">
                  <el-tag :type="selectedDepartment.status === 'Active' ? 'success' : 'info'" size="small">
                    {{ selectedDepartment.status === 'Active' ? '正常' : '停用' }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="负责人">
                  {{ selectedDepartment.manager || '未设置' }}
                </el-descriptions-item>
                <el-descriptions-item label="联系电话">
                  {{ selectedDepartment.managerPhone || '未设置' }}
                </el-descriptions-item>
                <el-descriptions-item label="邮箱地址" :span="2">
                  {{ selectedDepartment.managerEmail || '未设置' }}
                </el-descriptions-item>
                <el-descriptions-item label="创建时间" :span="2">
                  {{ formatDate(selectedDepartment.createdAt) }}
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <!-- 子部门列表 -->
            <div class="children-section" v-if="selectedDepartment.children && selectedDepartment.children.length">
              <h4>子部门 ({{ selectedDepartment.children.length }})</h4>
              <el-table :data="selectedDepartment.children" style="width: 100%" size="small">
                <el-table-column prop="departmentName" label="部门名称" />
                <el-table-column prop="manager" label="负责人" />
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'Active' ? 'success' : 'info'" size="small">
                      {{ row.status === 'Active' ? '正常' : '停用' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150" v-if="currentUserRole !== 'User'">
                  <template #default="{ row }">
                    <el-button type="text" size="small" @click="handleNodeClick(row)">
                      查看
                    </el-button>
                    <el-button type="text" size="small" @click="handleEdit(row)">
                      修改
                    </el-button>
                    <el-button type="text" size="small" @click="handleDelete(row)" style="color: #f56c6c;">
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          
          <!-- 未选择部门时的提示 -->
          <div class="empty-content" v-else>
            <el-empty description="请在左侧选择一个部门查看详情">
              <el-button type="primary" @click="handleAddDepartment" v-if="currentUserRole !== 'User'">新增部门</el-button>
            </el-empty>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 新增/编辑部门对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      class="department-dialog"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="departmentForm" 
        :rules="departmentRules" 
        ref="departmentFormRef"
        label-width="100px"
        class="department-form"
      >
        <el-form-item label="上级部门" v-if="isEdit || parentDepartment">
          <el-input 
            :value="parentDepartmentName" 
            disabled
            placeholder="顶级部门"
          />
        </el-form-item>
        
        <el-form-item label="部门名称" prop="departmentName">
          <el-input 
            v-model="departmentForm.departmentName" 
            placeholder="请输入部门名称"
          />
        </el-form-item>
        
        <el-form-item label="显示排序" prop="displayOrder">
          <el-input-number 
            v-model="departmentForm.displayOrder" 
            :min="1" 
            :max="999"
            placeholder="请输入显示排序"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="负责人">
          <el-input 
            v-model="departmentForm.manager" 
            placeholder="请输入负责人姓名"
          />
        </el-form-item>
        
        <el-form-item label="联系电话">
          <el-input 
            v-model="departmentForm.managerPhone" 
            placeholder="请输入联系电话"
          />
        </el-form-item>
        
        <el-form-item label="邮箱">
          <el-input 
            v-model="departmentForm.managerEmail" 
            placeholder="请输入邮箱地址"
          />
        </el-form-item>
        
        <el-form-item label="部门状态" prop="status">
          <el-radio-group v-model="departmentForm.status">
            <el-radio label="Active">正常</el-radio>
            <el-radio label="Inactive">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 错误提示对话框 -->
    <el-dialog 
      v-model="errorDialogVisible" 
      title="操作失败" 
      width="400px"
      class="error-dialog"
    >
      <div class="error-content">
        <el-icon class="error-icon"><Warning /></el-icon>
        <p>{{ errorMessage }}</p>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="errorDialogVisible = false">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { 
  Plus, Search, Refresh, Edit, Delete, Warning, 
  OfficeBuilding, Expand, Fold, Folder
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../utils/request.js'

// 接口类型定义
interface Department {
  id: number
  departmentName: string
  manager?: string
  managerPhone?: string
  managerEmail?: string
  status: 'Active' | 'Inactive'
  createdAt?: string
  parentDepartment?: number
  tenantId?: number
  path?: string
  children?: Department[]
  hasChildren?: boolean
}

interface DepartmentForm {
  id?: number
  departmentName: string
  displayOrder: number
  manager: string
  managerPhone: string
  managerEmail: string
  status: 'Active' | 'Inactive'
  parentDepartment?: number
  tenantId?: number
}

// 响应式数据
const searchForm = ref({
  departmentName: '',
  status: ''
})

const departmentForm = ref<DepartmentForm>({
  departmentName: '',
  displayOrder: 1,
  manager: '',
  managerPhone: '',
  managerEmail: '',
  status: 'Active'
})

const tableData = ref<Department[]>([])
const totalCount = ref(0)
const tableLoading = ref(false)
const searchLoading = ref(false)
const submitLoading = ref(false)

const dialogVisible = ref(false)
const errorDialogVisible = ref(false)
const errorMessage = ref('')

const isEdit = ref(false)
const parentDepartment = ref<Department | null>(null)
const currentEditDepartment = ref<Department | null>(null)

// 新增：左右分栏布局相关的响应式数据
const selectedDepartment = ref<Department | null>(null)

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'departmentName'
}

// 计算属性 - 获取当前用户角色
const currentUserRole = computed(() => {
  const user = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return user.role || 'User'
})

// 计算树形数据
const treeData = computed(() => {
  return tableData.value
})

// 表单引用
const departmentFormRef = ref()

// 计算属性
const dialogTitle = computed(() => {
  if (isEdit.value) {
    return '修改部门'
  } else if (parentDepartment.value) {
    return '新增子部门'
  } else {
    return '新增部门'
  }
})

const parentDepartmentName = computed(() => {
  if (parentDepartment.value) {
    return parentDepartment.value.departmentName
  } else if (currentEditDepartment.value?.parentDepartment) {
    // 查找父部门名称
    const findParent = (depts: Department[], id: number): string => {
      for (const dept of depts) {
        if (dept.id === id) {
          return dept.departmentName
        }
        if (dept.children) {
          const found = findParent(dept.children, id)
          if (found) return found
        }
      }
      return ''
    }
    return findParent(tableData.value, currentEditDepartment.value.parentDepartment)
  }
  return '顶级部门'
})

// 表单验证规则
const departmentRules = {
  departmentName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  displayOrder: [
    { required: true, message: '请输入显示排序', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择部门状态', trigger: 'change' }
  ]
}

// 生命周期
onMounted(() => {
  fetchDepartmentData()
})

// 方法
const fetchDepartmentData = async () => {
  tableLoading.value = true
  try {
    const user = JSON.parse(localStorage.getItem('userInfo') || '{}')
    let response

    if (user.role === 'Admin') {
      // 系统管理员：获取所有部门
      response = await axios.get('http://localhost:9049/departments/getall', {
        params: { path: '' }
      })
    } else if (user.role === 'TAdmin' || user.role === 'User') {
      // 租户管理员和普通用户：获取本租户的所有部门
      response = await axios.get('http://localhost:9049/departments/getByTenantId', {
        params: { tenantId: user.tenantId }
      })
    } else {
      // 备用方案：根据部门ID获取
      response = await axios.get('http://localhost:9049/departments', {
        params: { departementId: user.departmentId }
      })
    }

    let rawData = []
    if (response.data.isOK) {
      rawData = response.data.departmentList || []
    } else {
      rawData = response.data || []
    }
    
    tableData.value = buildDepartmentHierarchy(rawData)
    totalCount.value = rawData.length
    console.log(`📋 获取部门数据成功，用户角色: ${user.role}，部门数量: ${rawData.length}`)
  } catch (error) {
    console.error('获取部门数据失败:', error)
    ElMessage.error('获取部门数据失败')
  } finally {
    tableLoading.value = false
  }
}

const buildDepartmentHierarchy = (departments: any[]): Department[] => {
  const departmentMap: { [key: number]: Department } = {}
  const result: Department[] = []
  
  // 创建部门映射
  departments.forEach(dept => {
    departmentMap[dept.id] = {
      ...dept,
      children: [],
      hasChildren: false
    }
  })
  
  // 构建层级关系
  departments.forEach(dept => {
    if (dept.parentDepartment && departmentMap[dept.parentDepartment]) {
      departmentMap[dept.parentDepartment].children!.push(departmentMap[dept.id])
      departmentMap[dept.parentDepartment].hasChildren = true
    } else {
      result.push(departmentMap[dept.id])
    }
  })
  
  return result
}

const handleSearch = async () => {
  searchLoading.value = true
  try {
    const user = JSON.parse(localStorage.getItem('userInfo') || '{}')
    let response

    if (user.role === 'Admin') {
      // 系统管理员：搜索所有部门
      response = await axios.get('http://localhost:9049/departments/search', {
        params: {
          departmentName: searchForm.value.departmentName || undefined,
          status: searchForm.value.status || undefined
        }
      })
    } else if (user.role === 'TAdmin' || user.role === 'User') {
      // 租户管理员和普通用户：搜索本租户的部门
      response = await axios.get('http://localhost:9049/departments/search', {
        params: {
          departmentName: searchForm.value.departmentName || undefined,
          status: searchForm.value.status || undefined,
          tenantId: user.tenantId
        }
      })
    } else {
      // 备用方案：根据部门ID搜索
      response = await axios.get('http://localhost:9049/departments/search', {
        params: {
          departmentName: searchForm.value.departmentName || undefined,
          status: searchForm.value.status || undefined,
          departementId: user.departmentId
        }
      })
    }
    
    const rawData = response.data.departmentList || []
    tableData.value = buildDepartmentHierarchy(rawData)
    totalCount.value = rawData.length
    console.log(`🔍 搜索部门成功，用户角色: ${user.role}，找到 ${rawData.length} 个部门`)
  } catch (error) {
    console.error('搜索部门失败:', error)
    ElMessage.error('搜索部门失败')
  } finally {
    searchLoading.value = false
  }
}

const handleReset = () => {
  searchForm.value = {
    departmentName: '',
    status: ''
  }
  fetchDepartmentData()
}

const handleAddDepartment = () => {
  isEdit.value = false
  parentDepartment.value = null
  currentEditDepartment.value = null
  
  departmentForm.value = {
    departmentName: '',
    displayOrder: 1,
    manager: '',
    managerPhone: '',
    managerEmail: '',
    status: 'Active'
  }
  
  dialogVisible.value = true
}

const handleAddChild = (row: Department) => {
  isEdit.value = false
  parentDepartment.value = row
  currentEditDepartment.value = null
  
  departmentForm.value = {
    departmentName: '',
    displayOrder: 1,
    manager: '',
    managerPhone: '',
    managerEmail: '',
    status: 'Active',
    parentDepartment: row.id,
    tenantId: row.tenantId
  }
  
  dialogVisible.value = true
}

const handleEdit = (row: Department) => {
  isEdit.value = true
  parentDepartment.value = null
  currentEditDepartment.value = row
  
  departmentForm.value = {
    id: row.id,
    departmentName: row.departmentName,
    displayOrder: 1, // 默认值，可以从后端获取
    manager: row.manager || '',
    managerPhone: row.managerPhone || '',
    managerEmail: row.managerEmail || '',
    status: row.status,
    parentDepartment: row.parentDepartment,
    tenantId: row.tenantId
  }
  
  dialogVisible.value = true
}

const handleDelete = async (row: Department) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除部门 "${row.departmentName}" 吗？删除后不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    await axios.delete(`http://localhost:9049/departments/${row.id}`)
    ElMessage.success('部门删除成功')
    fetchDepartmentData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除部门失败:', error)
      errorMessage.value = error.response?.data?.msg || '删除部门失败，请稍后重试'
      errorDialogVisible.value = true
    }
  }
}

const handleSubmit = async () => {
  if (!departmentFormRef.value) return
  
  try {
    await departmentFormRef.value.validate()
  } catch (error) {
    ElMessage.error('请完善必填信息')
    return
  }
  
  submitLoading.value = true
  
  try {
    const user = JSON.parse(localStorage.getItem('userInfo') || '{}')
    
    if (isEdit.value) {
      // 修改部门
      const updateData = {
        id: departmentForm.value.id,
        departmentName: departmentForm.value.departmentName,
        manager: departmentForm.value.manager,
        managerPhone: departmentForm.value.managerPhone,
        managerEmail: departmentForm.value.managerEmail,
        status: departmentForm.value.status,
        parentDepartment: departmentForm.value.parentDepartment,
        tenantId: departmentForm.value.tenantId,
        createdAt: currentEditDepartment.value?.createdAt,
        path: currentEditDepartment.value?.path
      }
      
      const response = await axios.post('http://localhost:9049/departments/update', updateData)
      
      if (response.data.isOK) {
        ElMessage.success('部门修改成功')
        dialogVisible.value = false
        fetchDepartmentData()
      } else {
        errorMessage.value = response.data.msg || '修改部门失败'
        errorDialogVisible.value = true
      }
    } else {
      // 新增部门
      const insertData = {
        parentDepartment: departmentForm.value.parentDepartment || null,
        tenantId: departmentForm.value.tenantId || user.tenantId,
        departmentName: departmentForm.value.departmentName,
        manager: departmentForm.value.manager,
        managerPhone: departmentForm.value.managerPhone,
        managerEmail: departmentForm.value.managerEmail,
        status: departmentForm.value.status,
        id: null,
        createdAt: null,
        path: null
      }
      
      const response = await axios.post('http://localhost:9049/departments/insert', insertData)
      
      if (response.data.isOK) {
        ElMessage.success('部门创建成功')
        dialogVisible.value = false
        fetchDepartmentData()
      } else {
        errorMessage.value = response.data.msg || '创建部门失败'
        errorDialogVisible.value = true
      }
    }
  } catch (error: any) {
    console.error('提交部门信息失败:', error)
    errorMessage.value = error.response?.data?.msg || '操作失败，请稍后重试'
    errorDialogVisible.value = true
  } finally {
    submitLoading.value = false
  }
}

const expandAll = () => {
  // Element Plus 树形表格暂不支持编程式展开，这里可以添加提示
  ElMessage.info('请手动点击部门名称前的展开按钮')
}

const collapseAll = () => {
  // Element Plus 树形表格暂不支持编程式收起，这里可以添加提示
  ElMessage.info('请手动点击部门名称前的收起按钮')
}

const formatDate = (date: string | undefined) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

// 新增：处理树节点点击事件
const handleNodeClick = (data: Department) => {
  selectedDepartment.value = data
}
</script>

<style scoped>
.department-management-container {
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

/* 搜索区域 */
.search-section {
  margin-bottom: 24px;
}

.search-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: none;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

/* 操作按钮区域 */
.action-section {
  margin-bottom: 24px;
}

.action-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: none;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

/* 主要内容区域 - 左右分栏布局 */
.main-content {
  display: flex;
  gap: 24px;
  height: calc(100vh - 280px);
}

.left-panel {
  width: 350px;
  flex-shrink: 0;
}

.right-panel {
  flex: 1;
  min-width: 0;
}

.tree-card,
.detail-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: none;
  height: 100%;
}

/* 树形组件样式 */
.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-title {
  font-weight: 600;
  color: #2c3e50;
}

.tree-actions {
  display: flex;
  gap: 8px;
}

.tree-content {
  height: calc(100% - 60px);
  overflow-y: auto;
}

.department-tree {
  height: 100%;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.node-icon {
  color: #667eea;
  font-size: 16px;
}

.node-label {
  flex: 1;
  color: #2c3e50;
}

.node-count {
  color: #7f8c8d;
  font-size: 12px;
  background: #ecf0f1;
  padding: 2px 6px;
  border-radius: 10px;
}

/* 详情区域样式 */
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-title {
  font-weight: 600;
  color: #2c3e50;
}

.detail-actions {
  display: flex;
  gap: 8px;
}

.detail-content {
  height: calc(100% - 60px);
  overflow-y: auto;
}

.info-section {
  margin-bottom: 24px;
}

.info-section h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-weight: 600;
  padding-bottom: 8px;
  border-bottom: 2px solid #667eea;
}

.children-section {
  margin-top: 24px;
}

.children-section h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-weight: 600;
  padding-bottom: 8px;
  border-bottom: 2px solid #27ae60;
}

.empty-content {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
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

.department-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.department-icon {
  color: #667eea;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* 对话框样式 */
.department-dialog {
  border-radius: 16px;
}

.department-form {
  max-height: 60vh;
  overflow-y: auto;
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
    flex-direction: column;
    height: auto;
  }
  
  .left-panel {
    width: 100%;
    height: 400px;
  }
  
  .right-panel {
    height: 500px;
  }
}

@media (max-width: 768px) {
  .department-management-container {
    padding: 16px;
  }
  
  .search-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .main-content {
    gap: 16px;
  }
  
  .left-panel {
    height: 300px;
  }
  
  .right-panel {
    height: 400px;
  }
  
  .tree-actions .el-button,
  .detail-actions .el-button {
    font-size: 12px;
    padding: 4px 8px;
  }
  
  .detail-actions {
    flex-wrap: wrap;
    gap: 4px;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-table) {
  border-radius: 12px;
}

/* 树形组件样式覆盖 */
:deep(.el-tree-node__content) {
  padding: 8px 12px;
  border-radius: 8px;
  margin: 2px 0;
  transition: all 0.3s ease;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f8f9ff;
  transform: translateX(4px);
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #667eea;
  color: white;
}

:deep(.el-tree-node.is-current > .el-tree-node__content .node-icon) {
  color: white;
}

:deep(.el-tree-node.is-current > .el-tree-node__content .node-count) {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

/* 描述列表样式覆盖 */
:deep(.el-descriptions__body .el-descriptions__table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-descriptions__label) {
  background-color: #f8f9ff;
  font-weight: 600;
  color: #2c3e50;
}

:deep(.el-descriptions__content) {
  background-color: white;
  color: #2c3e50;
}

:deep(.el-table th) {
  background: #f8f9fa;
  color: #2c3e50;
  font-weight: 600;
}

:deep(.el-table td) {
  border-bottom: 1px solid #ecf0f1;
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