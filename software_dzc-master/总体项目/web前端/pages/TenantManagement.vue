<template>
  <div class="container">
    <div>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input v-model="searchForm.tenantName" placeholder="租户名称"></el-input>
        </el-col>
        <el-col :span="6">
          <el-input v-model="searchForm.contactPerson" placeholder="联系人"></el-input>
        </el-col>
        <el-col :span="6">
          <el-input v-model="searchForm.phone" placeholder="联系电话"></el-input>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col>
          <el-button 
            v-if="userRole === 'Admin'" 
            type="primary" 
            @click="openAddDialog"
          >
            新增
          </el-button>
          <el-button 
            v-if="selectedRow && canEditTenant(selectedRow)" 
            type="warning" 
            @click="openEditDialog"
          >
            修改
          </el-button>
          <el-button 
            v-if="selectedRows.length > 0 && selectedRows.every(row => canDeleteTenant(row))" 
            type="danger" 
            @click="handleDeleteConfirm"
          >
            删除
          </el-button>
          <el-button type="success" @click="handleExport">导出</el-button>
          
          <!-- 权限提示 -->
          <div v-if="userRole === 'TAdmin'" style="margin-top: 8px; color: #666; font-size: 12px;">
            💡 提示：您只能查看所有租户详情，但只能修改和删除自己的租户信息
          </div>
        </el-col>
      </el-row>
      <el-table
        v-loading="loading"
        :data="tenants"
        style="width: 100%; margin-top: 20px;"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="tenantName" label="租户名称" width="180"></el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="180"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="180"></el-table-column>
        <el-table-column label="租户图标" width="180">
          <template v-slot="scope">
            <div class="tenant-icon-container" style="display: flex; align-items: center; gap: 8px;">
              <img 
                :src="getTenantIcon(scope.row.icon)" 
                alt="租户图标" 
                style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px; cursor: pointer;"
                @error="handleImageError"
                @click="showImagePreview(getPreviewableImageUrl(scope.row.icon))"
                title="点击预览大图"
              >
              <el-button 
                type="danger" 
                size="small" 
                :icon="Delete"
                @click="deleteTenantIcon(scope.row)"
                title="删除图标"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="240">
          <template v-slot="scope">
            <div class="action-buttons">
              <el-button type="link" size="small" @click="goToTenantDetail(scope.row.id)">详情</el-button>
              <el-button 
                v-if="canEditTenant(scope.row)"
                type="link" 
                size="small" 
                @click="openEditDialog(scope.row)"
              >
                修改
              </el-button>
              <el-button 
                v-if="canDeleteTenant(scope.row)"
                type="link" 
                size="small" 
                @click="handleDelete(scope.row)"
              >
                删除
              </el-button>
              <span v-if="!canEditTenant(scope.row) && !canDeleteTenant(scope.row)" style="color: #999; font-size: 12px;">
                只读
              </span>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        style="margin-top: 20px; text-align: right;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[6, 12, 18, 24]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      >
      </el-pagination>
  <!-- 新增租户弹窗 -->
  <el-dialog v-model="addDialogVisible" title="新增租户" width="60%">
        <el-form :model="formData" :rules="rules" ref="tenantForm" label-width="100px" class="dialog-form">
          <!-- AI智能填充区域 -->
          <div class="form-section ai-section">
            <h4><el-icon><MagicStick /></el-icon> 智能填充 (AI)</h4>
            <el-form-item label="一句话描述">
              <el-input 
                v-model="aiPromptText"
                type="textarea"
                :rows="3"
                placeholder="例如：创建一个租户名为阿里巴巴，联系人是马云，电话是13800138000，主要业务是电子商务和云计算"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleAiParse" :loading="aiParsing" plain>
                <el-icon><Promotion /></el-icon> AI 解析并填充表单（包含备注）
              </el-button>
            </el-form-item>
            <p class="ai-tip">提示：添加对公司业务的描述，AI将自动生成专业的公司介绍作为备注</p>
          </div>
          
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="租户名称" prop="tenantName">
                <el-input v-model="formData.tenantName"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="租户图标" prop="icon">
                <el-upload
                  action="/api/tenants/upload-icon"
                  list-type="picture-card"
                  :on-success="handleUploadSuccess"
                  :on-error="handleUploadError"
                  :on-remove="handleRemove"
                  :before-upload="beforeUpload"
                  :file-list="fileList"
                  accept="image/*"
                  :limit="1">
                  <i class="el-icon-plus"></i>
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系人" prop="contactPerson">
                <el-input v-model="formData.contactPerson"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="formData.phone"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="创建时间" prop="createdAt">
                <el-date-picker v-model="formData.createdAt" type="datetime" placeholder="选择创建时间"></el-date-picker>
              </el-form-item>
            </el-col>
		  </el-row>
		  <el-row>
              <el-form-item label="备注" prop="remark">
                <div class="media-tips">
                  <el-alert
                    title="媒体资源提示"
                    type="info"
                    :closable="false"
                    show-icon
                  >
                    <template #default>
                      <p>富文本编辑器支持插入图片、音频和视频：</p>
                      <ul>
                        <li>📷 图片：支持 JPG、PNG、GIF 格式，建议大小不超过10MB</li>
                        <li>🎵 音频：支持 MP3、WAV、OGG、M4A 格式，大小限制50MB</li>
                        <li>🎬 视频：支持 MP4、WebM、OGG 格式，大小限制100MB</li>
                      </ul>
                      <p style="font-size: 12px; color: #666; margin-top: 8px;">
                        💡 如果媒体文件无法播放，请检查文件格式是否正确，或尝试转换为推荐格式。
                      </p>
                    </template>
                  </el-alert>
                </div>
                
                <div class="editor-wrapper">
                  <div class="custom-toolbar">
                    <button 
                      type="button" 
                      class="custom-media-btn image-btn" 
                      @click="handleImageUpload"
                      title="插入图片"
                    >
                      📷 图片
                    </button>
                    <button 
                      type="button" 
                      class="custom-media-btn audio-btn" 
                      @click="handleAudioUpload"
                      title="插入音频"
                    >
                      🎵 音频
                    </button>
                    <button 
                      type="button" 
                      class="custom-media-btn video-btn" 
                      @click="handleVideoUpload"
                      title="插入视频"
                    >
                      🎬 视频
                    </button>
                  </div>
                  <div class="editor-container-wrapper">
                    <div id="editor" class="editor-container"></div>
                  </div>
                </div>
              </el-form-item>
          </el-row>
        </el-form>
        <template #footer>
          <el-button @click="closeAddDialog">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </template>
      </el-dialog>
  <!-- 修改租户弹窗 -->
  <el-dialog v-model="editDialogVisible" title="修改租户" width="80%">
        <el-form :model="formData" :rules="rules" ref="editTenantForm" label-width="100px" class="dialog-form">
          <!-- AI智能填充区域 -->
          <div class="form-section ai-section">
            <h4><el-icon><MagicStick /></el-icon> AI 辅助修改</h4>
            <el-form-item label="修改指令" label-width="100px">
              <el-input 
                v-model="aiPromptText" 
                type="textarea" 
                :rows="3" 
                placeholder="例如：将联系人改为张三，联系电话更新为15812345678" 
              />
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
              <el-form :model="originalFormData" label-width="80px" disabled>
                <el-form-item label="租户名称"><el-input v-model="originalFormData.tenantName" /></el-form-item>
                <el-form-item label="联系人"><el-input v-model="originalFormData.contactPerson" /></el-form-item>
                <el-form-item label="联系电话"><el-input v-model="originalFormData.phone" /></el-form-item>
                <el-form-item label="创建时间">
                  <el-date-picker v-model="originalFormData.createdAt" type="datetime" placeholder="选择创建时间" disabled></el-date-picker>
                </el-form-item>
              </el-form>
            </div>
            <!-- Right Panel -->
            <div class="panel right-panel">
              <h3>修改后 (AI填充 & 可编辑)</h3>
              <el-form :model="formData" :rules="rules" ref="editFormRef" label-width="80px">
                <el-form-item label="租户名称" prop="tenantName"><el-input v-model="formData.tenantName" /></el-form-item>
                <el-form-item label="联系人" prop="contactPerson"><el-input v-model="formData.contactPerson" /></el-form-item>
                <el-form-item label="联系电话" prop="phone"><el-input v-model="formData.phone" /></el-form-item>
                <el-form-item label="创建时间" prop="createdAt">
                  <el-date-picker v-model="formData.createdAt" type="datetime" placeholder="选择创建时间"></el-date-picker>
                </el-form-item>
                <el-form-item label="租户图标" prop="icon">
                  <el-upload
                    action="/api/tenants/upload-icon"
                    list-type="picture-card"
                    :on-success="handleUploadSuccess"
                    :on-error="handleUploadError"
                    :on-remove="handleRemove"
                    :before-upload="beforeUpload"
                    :file-list="fileList"
                    accept="image/*"
                    :limit="1">
                    <i class="el-icon-plus"></i>
                  </el-upload>
                </el-form-item>
                
                <el-form-item label="备注" prop="remark">
                  <div class="editor-wrapper">
                    <div class="custom-toolbar">
                      <button 
                        type="button" 
                        class="custom-media-btn image-btn" 
                        @click="handleImageUpload"
                        title="插入图片"
                      >
                        📷 图片
                      </button>
                      <button 
                        type="button" 
                        class="custom-media-btn audio-btn" 
                        @click="handleAudioUpload"
                        title="插入音频"
                      >
                        🎵 音频
                      </button>
                      <button 
                        type="button" 
                        class="custom-media-btn video-btn" 
                        @click="handleVideoUpload"
                        title="插入视频"
                      >
                        🎬 视频
                      </button>
                    </div>
                    <div class="editor-container-wrapper">
                      <div id="edit-editor" class="editor-container"></div>
                    </div>
                  </div>
                </el-form-item>
              </el-form>
            </div>
          </div>
          
          <template #footer>
            <div class="dialog-footer">
              <el-button @click="editDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="submitEditForm">确定</el-button>
            </div>
          </template>
        </el-form>
      </el-dialog>
  <!-- 租户详情弹窗 -->
  <el-dialog v-model="detailsDialogVisible" title="租户详情" width="60%">
    <el-form :model="detailsFormData" label-width="100px" class="dialog-form" disabled>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="租户名称">
            <el-input v-model="detailsFormData.tenantName" disabled></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="租户图标">
            <img 
              :src="getTenantIcon(detailsFormData.icon)" 
              alt="租户图标" 
              style="width: 100px; height: auto;"
              @error="handleImageError"
            >
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="联系人">
            <el-input v-model="detailsFormData.contactPerson" disabled></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话">
            <el-input v-model="detailsFormData.phone" disabled></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="创建时间">
            <el-date-picker v-model="detailsFormData.createdAt" type="datetime" disabled></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="备注">
            <el-input type="textarea" v-model="detailsFormData.remark" disabled></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="closeDetailsDialog">关闭</el-button>
    </template>
  </el-dialog>

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
  </div>
</template>
<script>
import axios from '../utils/request.js';
import { ref, onMounted, nextTick, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete, MagicStick, Promotion } from '@element-plus/icons-vue';
import Quill from 'quill';
import 'quill/dist/quill.core.css';
import 'quill/dist/quill.snow.css';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import { useRouter } from 'vue-router';

export default {
name: 'TenantManagement',
components: {
  Delete, MagicStick, Promotion
},
setup() {
const searchForm = ref({
tenantName: '',
contactPerson: '',
phone: ''
});
const tenants = ref([]);
const selectedRow = ref(null);
const selectedRows = ref([]); // 存储所有选中的行
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(6);
const total = ref(0);
const addDialogVisible = ref(false);
const editDialogVisible = ref(false);
const detailsDialogVisible = ref(false);
const imagePreviewVisible = ref(false);
const previewImageUrl = ref('');
const formData = ref({
tenantName: '',
contactPerson: '',
phone: '',
icon: '',
createdAt: '',
remark: ''
});
const detailsFormData = ref({
tenantName: '',
contactPerson: '',
phone: '',
icon: '',
createdAt: '',
remark: ''
});
const fileList = ref([]);
const rules = ref({
tenantName: [{ required: true, message: '请输入租户名称', trigger: 'blur' }],
contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
icon: [{ required: true, message: '请上传租户图标', trigger: 'change' }],
createdAt: [{ required: true, message: '请选择创建时间', trigger: 'change' }],
remark: [{ required: true, message: '请输入备注', trigger: 'blur' }]
});

const router = useRouter();

// 用户权限控制
const currentUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
const userRole = ref(currentUser.role || 'User')
const userTenantId = ref(currentUser.tenantId || null)

// 权限检查函数
const canEditTenant = (tenant) => {
  if (userRole.value === 'Admin') {
    return true // 系统管理员可以编辑所有租户
  }
  if (userRole.value === 'TAdmin') {
    return tenant.id === userTenantId.value // 租户管理员只能编辑自己的租户
  }
  return false // 普通用户不能编辑
}

const canDeleteTenant = (tenant) => {
  if (userRole.value === 'Admin') {
    return true // 系统管理员可以删除所有租户
  }
  if (userRole.value === 'TAdmin') {
    return tenant.id === userTenantId.value // 租户管理员只能删除自己的租户
  }
  return false // 普通用户不能删除
}

const fetchTenants = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/api/tenants/all');
    tenants.value = response.data.tenantList;
    total.value = response.data.total;
    
    console.log('当前用户权限:', {
      role: userRole.value,
      tenantId: userTenantId.value,
      canEdit: tenants.value.map(t => ({ id: t.id, name: t.tenantName, canEdit: canEditTenant(t) }))
    })
  } catch (error) {
    ElMessage.error('获取租户数据失败');
  } finally {
    loading.value = false;
  }
};

onMounted(fetchTenants);

const handleSearch = () => {
  const params = {
    tenantName: searchForm.value.tenantName,
    contactPerson: searchForm.value.contactPerson,
    phone: searchForm.value.phone
  };
        axios.get('http://localhost:9049/api/tenants/search', { params })
    .then(response => {
      tenants.value = response.data.tenantList;
      total.value = response.data.total;
    })
    .catch(error => {
      console.error('Error searching data:', error);
    });
};

const handleReset = () => {
  searchForm.value.tenantName = '';
  searchForm.value.contactPerson = '';
  searchForm.value.phone = '';
  fetchTenants();
};

const handleExport = async () => {
  try {
    const response = await axios.get('/api/tenants/all');
    const exportData = response.data.tenantList.map(item => {
      return {
        租户ID: item.id,
        租户名称: item.tenantName,
        联系人: item.contactPerson,
        联系电话: item.phone,
        图标: item.icon,
        创建时间: item.createdAt,
        备注: item.remark
      };
    });
    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, '租户列表');
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
    saveAs(blob, '租户列表.xlsx');
  } catch (error) {
    console.error('Error exporting data:', error);
  }
};

const resetForm = () => {
  formData.value = {
    tenantName: '',
    contactPerson: '',
    phone: '',
    icon: '',
    createdAt: '',
    remark: '',
    adminUsername: '',
    password: '',
    rootDepartmentId: null
  };
  fileList.value = [];
};

const openAddDialog = () => {
  addDialogVisible.value = true;
  resetForm();
  nextTick(() => {
    initializeEditor('#editor', false);
  });
};

const closeAddDialog = () => {
  addDialogVisible.value = false;
};

const editTenant = (row) => {
  console.log('开始编辑租户：', row.tenantName);
  console.log('租户备注内容：', row.remark);
  
  // 复制原始数据以供比较
  originalFormData.value = JSON.parse(JSON.stringify(row));
  
  // 设置表单数据
  formData.value = { ...row };
  
  // 初始化文件列表（如果有图标）
  fileList.value = [];
  if (row.icon && row.icon !== 'default.png') {
    fileList.value.push({
      name: row.icon.split('/').pop() || row.icon,
      url: `http://localhost:9049${row.icon}`
    });
  }
  
  // 先打开对话框
  editDialogVisible.value = true;
  
  // 使用nextTick确保DOM已更新
  nextTick(() => {
    console.log('准备初始化编辑器，备注内容:', formData.value.remark);
    
    // 确保编辑器容器存在
    const editorContainer = document.querySelector('#edit-editor');
    if (!editorContainer) {
      console.error('找不到编辑器容器 #edit-editor');
      return;
    }
    
    // 延迟初始化编辑器，确保DOM完全渲染
    setTimeout(() => {
      // 初始化编辑器并设置内容
      const quill = initializeEditor('#edit-editor', true, formData.value.remark || '');
      console.log('编辑器初始化完成，内容已设置');
      
      // 确保内容被正确设置
      if (quill && formData.value.remark) {
        quill.root.innerHTML = formData.value.remark;
      }
    }, 300);
  });
  
  // 清空AI提示文本
  aiPromptText.value = '';
};

const openEditDialog = (row) => {
  editTenant(row);
};

const closeEditDialog = () => {
  editDialogVisible.value = false;
};

const openDetailsDialog = (row) => {
  detailsDialogVisible.value = true;
  Object.assign(detailsFormData.value, row);
};

const closeDetailsDialog = () => {
  detailsDialogVisible.value = false;
};

const handleUploadSuccess = (response, file, fileList) => {
  console.log('图片上传响应:', response);
  console.log('响应数据类型:', typeof response);
  console.log('响应完整内容:', JSON.stringify(response, null, 2));
  
  // 检查响应数据的结构
  if (response && typeof response === 'object') {
    // 尝试不同的可能字段名
    const imageUrl = response.url || response.data?.url || response.path || response.data?.path || 
                    response.fileName || response.data?.fileName || response.filePath || 
                    response.data?.filePath || response.imageUrl || response.data?.imageUrl;
    
    console.log('提取的图片URL:', imageUrl);
    
    if (imageUrl) {
      // 存储相对路径，用于保存到数据库
      if (imageUrl.startsWith('http')) {
        // 如果是完整URL，提取相对路径部分
        const url = new URL(imageUrl);
        formData.value.icon = url.pathname;
      } else {
        // 如果是相对路径，确保以/开头
        formData.value.icon = imageUrl.startsWith('/') ? imageUrl : '/' + imageUrl;
      }
      
      console.log('最终存储的图标路径:', formData.value.icon);
      ElMessage.success('图片上传成功');
      
      // 更新文件列表显示
      fileList.value = [{
        name: file.name,
        url: `http://localhost:9049${formData.value.icon}`
      }];
    } else {
      console.error('响应中未找到图片URL，响应内容:', JSON.stringify(response, null, 2));
      ElMessage.error('图片上传失败：响应中未包含图片URL');
    }
  } else if (typeof response === 'string') {
    // 如果响应直接是字符串URL
    if (response.startsWith('http')) {
      const url = new URL(response);
      formData.value.icon = url.pathname;
    } else {
      formData.value.icon = response.startsWith('/') ? response : '/' + response;
    }
    
    console.log('字符串响应，最终存储路径:', formData.value.icon);
    ElMessage.success('图片上传成功');
    
    fileList.value = [{
      name: file.name,
      url: `http://localhost:9049${formData.value.icon}`
    }];
  } else {
    console.error('无效的响应数据格式:', response);
    ElMessage.error('图片上传失败：无效的响应数据格式');
  }
};

const handleRemove = (file, fileList) => {
  formData.value.icon = '';
};

const handleUploadError = (error, file, fileList) => {
  console.error('图片上传失败:', error);
  
  // 尝试解析错误信息
  let errorMessage = '未知错误';
  if (error && error.message) {
    errorMessage = error.message;
  } else if (error && error.response) {
    errorMessage = error.response.data?.message || error.response.statusText || '服务器错误';
  } else if (typeof error === 'string') {
    errorMessage = error;
  }
  
  ElMessage.error(`图片上传失败: ${errorMessage}`);
};

const beforeUpload = (file) => {
  // 检查文件类型
  const isImage = file.type.startsWith('image/');
  if (!isImage) {
    ElMessage.error('只能上传图片文件！');
    return false;
  }
  
  // 检查文件大小 (限制10MB)
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB！');
    return false;
  }
  
  return true;
};

const submitForm = () => {
  if (!formData.value.tenantName || !formData.value.contactPerson || !formData.value.phone) {
    ElMessage.error('请填写所有必填项：租户名称、联系人、联系电话');
    return;
  }

  // 使用正确的后端接口 - 修正为/api/tenants路径
  axios.post('http://localhost:9049/api/tenants/insert2', formData.value).then(() => {
    ElMessage.success('租户创建成功');
    closeAddDialog();
    fetchTenants();
  }).catch(error => {
    console.error('租户创建失败:', error);
    ElMessage.error('租户创建失败: ' + (error.response?.data?.message || error.message));
  });
};

const submitEditForm = () => {
  if (!formData.value.tenantName || !formData.value.contactPerson || !formData.value.phone) {
    ElMessage.error('请填写所有必填项：租户名称、联系人、电话');
    return;
  }

  // 权限检查
  if (!canEditTenant({ id: formData.value.id })) {
    ElMessage.error('您没有权限修改此租户');
    return;
  }

  // 确保获取最新的富文本编辑器内容
  const editorContainer = document.querySelector('#edit-editor');
  if (editorContainer) {
    const quill = Quill.find(editorContainer);
    if (quill) {
      formData.value.remark = quill.root.innerHTML;
      console.log('提交前获取富文本内容:', formData.value.remark.substring(0, 50) + '...');
    }
  }

  // 使用正确的后端接口 - 修正为/api/tenants路径
  axios.put(`/api/tenants/${formData.value.id}`, formData.value).then(() => {
    ElMessage.success('租户修改成功');
    closeEditDialog();
    fetchTenants();
  }).catch(error => {
    console.error('租户修改失败:', error);
    ElMessage.error('租户修改失败: ' + (error.response?.data?.message || error.message));
  });
};

const handleDeleteConfirm = () => {
  if (!selectedRow.value) {
    ElMessage.error('请先选择要删除的租户');
    return;
  }
  ElMessageBox.confirm('此操作将永久删除该租户, 是否继续?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    handleDelete(selectedRow.value);
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

const handleDelete = (row) => {
  // 权限检查
  if (!canDeleteTenant(row)) {
    ElMessage.error('您没有权限删除此租户');
    return;
  }
  
  axios.delete(`/api/tenants/delete/${row.id}`)
    .then(() => {
      ElMessage.success('租户删除成功');
      fetchTenants();
    })
    .catch(error => {
      ElMessage.error('租户删除失败: ' + error.message);
    });
};

const handleSelectionChange = (rows) => {
  selectedRow.value = rows.length ? rows[0] : null;
  selectedRows.value = rows; // 存储所有选中的行
  
  console.log('选中的租户:', {
    count: rows.length,
    names: rows.map(r => r.tenantName),
    canEdit: rows.filter(r => canEditTenant(r)).length,
    canDelete: rows.filter(r => canDeleteTenant(r)).length
  });
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchTenants();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  fetchTenants();
};

const initializeEditor = (selector, isEdit = false, content = '') => {
  const editorContainer = document.querySelector(selector);
  if (!editorContainer) {
    console.error('Editor container not found:', selector);
    return null;
  }

  console.log(`初始化编辑器 ${selector}，isEdit=${isEdit}，内容长度=${content?.length || 0}`);

  // 清除现有编辑器
  const existingToolbar = editorContainer.parentElement.querySelector('.ql-toolbar');
  if (existingToolbar) {
    existingToolbar.remove();
  }

  editorContainer.innerHTML = '';

  const toolbarOptions = [
    ['bold', 'italic', 'underline', 'strike'],
    ['blockquote', 'code-block'],
    [{ 'header': 1 }, { 'header': 2 }],
    [{ 'list': 'ordered' }, { 'list': 'bullet' }],
    [{ 'script': 'sub' }, { 'script': 'super' }],
    [{ 'indent': '-1' }, { 'indent': '+1' }],
    [{ 'direction': 'rtl' }],
    [{ 'size': ['small', false, 'large', 'huge'] }],
    [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
    [{ 'color': [] }, { 'background': [] }],
    [{ 'font': [] }],
    [{ 'align': [] }],
    ['link', 'image'],
    ['clean']
  ];

  console.log('🔧 开始初始化Quill编辑器，选择器:', selector);

  // 设置编辑器配置，添加宽度约束
  const quill = new Quill(selector, {
    theme: 'snow',
    modules: {
      toolbar: toolbarOptions
    },
    bounds: editorContainer.parentElement, // 设置边界为父元素
    formats: [
      'bold', 'italic', 'underline', 'strike', 'blockquote', 'code-block',
      'header', 'list', 'script', 'indent', 'direction', 'size',
      'color', 'background', 'font', 'align', 'link', 'image'
    ]
  });

  // 设置内容
  if (content) {
    console.log('设置编辑器内容:', content.substring(0, 50) + '...');
    quill.root.innerHTML = content;
    // 确保formData中的remark也被更新
    formData.value.remark = content;
  }

  // 手动设置编辑器宽度约束
  quill.root.style.maxWidth = '100%';
  quill.root.style.overflowX = 'hidden';
  quill.root.style.wordBreak = 'break-word';
  quill.root.style.whiteSpace = 'pre-wrap';
  
  // 添加文本换行处理
  quill.on('text-change', () => {
    // 更新表单数据
    formData.value.remark = quill.root.innerHTML;
    console.log('编辑器内容已更新');
    
    // 强制处理长文本换行
    enforceTextWrapping(quill.root);
  });
  
  // 处理图片插入
  const toolbar_quill = quill.getModule('toolbar');
  
  // 图片上传处理
  toolbar_quill.addHandler('image', () => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();
    
    input.onchange = () => {
      const file = input.files[0];
      if (file) {
        const formData = new FormData();
        formData.append('file', file);
        
        // 上传图片到服务器
        axios.post('/api/tenants/upload-icon', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then(response => {
          console.log('富文本编辑器图片上传响应:', response);
          
          if (response && response.data) {
            // 尝试不同的可能字段名
            const imageUrl = response.data.url || response.data.path || response.data.fileName;
            
            if (imageUrl) {
              // 确保使用完整URL
              const fullImageUrl = imageUrl.startsWith('http') 
                ? imageUrl 
                : `http://localhost:9049${imageUrl.startsWith('/') ? imageUrl : '/' + imageUrl}`;
              
              const range = quill.getSelection();
              quill.insertEmbed(range.index, 'image', fullImageUrl);
              ElMessage.success('图片上传成功');
            } else {
              console.error('富文本编辑器响应中未找到图片URL:', response.data);
              ElMessage.error('图片上传失败：响应中未包含图片URL');
            }
          } else {
            console.error('富文本编辑器无效的响应数据格式:', response);
            ElMessage.error('图片上传失败：无效的响应数据格式');
          }
        }).catch(error => {
          console.error('富文本编辑器图片上传失败:', error);
          const errorMessage = error.response?.data?.message || error.message || '未知错误';
          ElMessage.error(`图片上传失败: ${errorMessage}`);
        });
      }
    };
  });

  // 自定义音频上传按钮
  const audioButton = document.createElement('button');
  audioButton.innerHTML = '🎵';
  audioButton.title = '插入音频';
  audioButton.onclick = () => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'audio/*');
    input.click();
    
         input.onchange = () => {
       const file = input.files[0];
       if (file) {
         // 验证音频文件大小 (限制50MB)
         if (file.size > 50 * 1024 * 1024) {
           ElMessage.error('音频文件大小不能超过50MB');
           return;
         }
         
         const formData = new FormData();
         formData.append('file', file);
         
         // 上传音频到服务器
        axios.post('/api/tenants/upload-icon', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then(response => {
          if (response.data && response.data.url) {
            const audioUrl = response.data.url.startsWith('http') 
              ? response.data.url 
              : `http://localhost:9049${response.data.url}`;
            
            const range = quill.getSelection() || { index: 0 };
            const audioHtml = `
              <div style="margin: 10px 0; padding: 10px; border: 1px solid #ddd; border-radius: 8px; background: #f9f9f9;">
                <p style="margin: 0 0 8px 0; font-weight: bold; color: #666;">🎵 音频文件: ${file.name}</p>
                <audio controls style="width: 100%; margin-bottom: 10px;" preload="auto">
                  <source src="${audioUrl}" type="${file.type}">
                  <source src="${audioUrl}" type="audio/mpeg">
                  <source src="${audioUrl}" type="audio/wav">
                  <source src="${audioUrl}" type="audio/ogg">
                  <p style="color: #999; font-style: italic;">
                    您的浏览器不支持音频播放。
                    <a href="${audioUrl}" target="_blank" style="color: #007bff;">点击下载音频文件</a>
                  </p>
                </audio>
              </div>
            `;
            quill.clipboard.dangerouslyPasteHTML(range.index, audioHtml);
            ElMessage.success('音频上传成功');
          } else {
            ElMessage.error('音频上传失败：无效的响应数据');
          }
        }).catch(error => {
          console.error('音频上传失败:', error);
          ElMessage.error('音频上传失败');
        });
      }
    };
  };

  // 自定义视频上传按钮
  const videoButton = document.createElement('button');
  videoButton.innerHTML = '🎬';
  videoButton.title = '插入视频';
  videoButton.onclick = () => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'video/*');
    input.click();
    
    input.onchange = () => {
             const file = input.files[0];
       if (file) {
         // 验证视频文件大小 (限制100MB)
         if (file.size > 100 * 1024 * 1024) {
           ElMessage.error('视频文件大小不能超过100MB');
           return;
         }
         
         const formData = new FormData();
         formData.append('file', file);
         
         // 上传视频到服务器
        axios.post('/api/tenants/upload-icon', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then(response => {
          if (response.data && response.data.url) {
            const videoUrl = response.data.url.startsWith('http') 
              ? response.data.url 
              : `http://localhost:9049${response.data.url}`;
            
            const range = quill.getSelection() || { index: 0 };
            const videoHtml = `
              <div style="margin: 10px 0; padding: 10px; border: 1px solid #ddd; border-radius: 8px; background: #f9f9f9;">
                <p style="margin: 0 0 8px 0; font-weight: bold; color: #666;">🎬 视频文件: ${file.name}</p>
                <video controls style="width: 100%; max-width: 600px; border-radius: 8px; margin-bottom: 10px;" preload="auto">
                  <source src="${videoUrl}" type="${file.type}">
                  <source src="${videoUrl}" type="video/mp4">
                  <source src="${videoUrl}" type="video/webm">
                  <source src="${videoUrl}" type="video/ogg">
                  <p style="color: #999; font-style: italic;">
                    您的浏览器不支持视频播放。
                    <a href="${videoUrl}" target="_blank" style="color: #007bff;">点击下载视频文件</a>
                  </p>
                </video>
              </div>
            `;
            quill.clipboard.dangerouslyPasteHTML(range.index, videoHtml);
            ElMessage.success('视频上传成功');
          } else {
            ElMessage.error('视频上传失败：无效的响应数据');
          }
        }).catch(error => {
          console.error('视频上传失败:', error);
          ElMessage.error('视频上传失败');
        });
      }
    };
  };

  // 将自定义按钮添加到工具栏 - 使用延迟确保DOM已加载
  setTimeout(() => {
    const toolbarElement = quill.container.querySelector('.ql-toolbar');
    if (toolbarElement) {
      // 设置按钮样式
      audioButton.className = 'ql-custom-audio';
      audioButton.style.cssText = `
        width: 28px !important;
        height: 28px !important;
        border: 1px solid #ccc !important;
        border-radius: 4px !important;
        background: white !important;
        margin: 0 2px !important;
        cursor: pointer !important;
        font-size: 16px !important;
        display: inline-flex !important;
        align-items: center !important;
        justify-content: center !important;
        vertical-align: top !important;
      `;
      
      videoButton.className = 'ql-custom-video';
      videoButton.style.cssText = `
        width: 28px !important;
        height: 28px !important;
        border: 1px solid #ccc !important;
        border-radius: 4px !important;
        background: white !important;
        margin: 0 2px !important;
        cursor: pointer !important;
        font-size: 16px !important;
        display: inline-flex !important;
        align-items: center !important;
        justify-content: center !important;
        vertical-align: top !important;
      `;
      
      // 添加hover效果
      audioButton.addEventListener('mouseenter', () => {
        audioButton.style.background = '#f0f0f0';
        audioButton.style.borderColor = '#999';
      });
      audioButton.addEventListener('mouseleave', () => {
        audioButton.style.background = 'white';
        audioButton.style.borderColor = '#ccc';
      });
      
      videoButton.addEventListener('mouseenter', () => {
        videoButton.style.background = '#f0f0f0';
        videoButton.style.borderColor = '#999';
      });
      videoButton.addEventListener('mouseleave', () => {
        videoButton.style.background = 'white';
        videoButton.style.borderColor = '#ccc';
      });
      
      // 将按钮添加到工具栏末尾
      toolbarElement.appendChild(audioButton);
      toolbarElement.appendChild(videoButton);
      
      console.log('✅ 音频(🎵)和视频(🎬)按钮已成功添加到工具栏');
      console.log('工具栏子元素数量:', toolbarElement.children.length);
    } else {
      console.error('❌ 未找到工具栏元素');
      console.log('Quill容器:', quill.container);
    }
  }, 800); // 增加延迟时间确保DOM完全加载

  // 添加图片点击预览功能
  quill.root.addEventListener('click', (e) => {
    if (e.target.tagName === 'IMG') {
      showImagePreview(e.target.src);
    }
  });

  console.log('📝 Quill编辑器初始化完成');
  console.log('编辑器容器:', quill.container);
  console.log('编辑器主题:', quill.theme);

  // 保存当前编辑器实例供外部按钮使用
  currentQuillInstance = quill;
  
  return quill;
};

// 强制处理长文本换行
const enforceTextWrapping = (element) => {
  if (!element) return;
  
  // 处理所有文本节点
  const textNodes = Array.from(element.childNodes).filter(
    node => node.nodeType === Node.TEXT_NODE || 
           (node.nodeType === Node.ELEMENT_NODE && 
            !['IMG', 'VIDEO', 'AUDIO'].includes(node.tagName))
  );
  
  textNodes.forEach(node => {
    if (node.nodeType === Node.TEXT_NODE) {
      // 对于纯文本节点，确保其父元素有正确的样式
      if (node.parentElement) {
        node.parentElement.style.maxWidth = '100%';
        node.parentElement.style.wordBreak = 'break-word';
        node.parentElement.style.overflowWrap = 'break-word';
        node.parentElement.style.whiteSpace = 'pre-wrap';
      }
    } else if (node.nodeType === Node.ELEMENT_NODE) {
      // 对于元素节点，设置样式并递归处理其子节点
      node.style.maxWidth = '100%';
      node.style.wordBreak = 'break-word';
      node.style.overflowWrap = 'break-word';
      node.style.whiteSpace = 'pre-wrap';
      
      // 递归处理子节点
      enforceTextWrapping(node);
    }
  });
};

const goToTenantDetail = (id) => {
  router.push(`/tenantDetail/${id}`);
};

const getTenantIcon = (iconPath) => {
  console.log('TenantManagement getTenantIcon called with:', iconPath)
  
  if (!iconPath || iconPath === 'null' || iconPath === 'undefined') {
    console.log('No icon path provided, using default icon')
    return '/images/default-icon.jpg'
  }
  
  // 如果已经是完整URL，直接返回
  if (iconPath.startsWith('http')) {
    console.log('Using full URL:', iconPath)
    return iconPath
  }
  
  // 如果是相对路径，添加服务器地址
  if (iconPath.startsWith('/')) {
    const fullUrl = `http://localhost:9049${iconPath}`
    console.log('Using relative path with server:', fullUrl)
    return fullUrl
  }
  
  // 默认情况，添加服务器地址
  const fullUrl = `http://localhost:9049/${iconPath}`
  console.log('Using default path with server:', fullUrl)
  return fullUrl
}

const getPreviewableImageUrl = (iconPath) => {
  // 对于默认图片，使用本地路径进行预览
  if (!iconPath || iconPath === 'null' || iconPath === 'undefined') {
    return window.location.origin + '/images/default-icon.jpg'
  }
  
  // 对于其他图片，使用getTenantIcon的逻辑
  return getTenantIcon(iconPath)
}

const handleImageError = (event) => {
  console.error('Error loading tenant icon, using default')
  event.target.src = '/images/default-icon.jpg'
}

const showImagePreview = (imageUrl) => {
  previewImageUrl.value = imageUrl;
  imagePreviewVisible.value = true;
}

const closeImagePreview = () => {
  imagePreviewVisible.value = false;
  previewImageUrl.value = '';
}

const deleteTenantIcon = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个租户的图标吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    
    // 更新租户信息，将图标设为null
    const updateData = {
      ...row,
      icon: null
    };
    
            await axios.put(`http://localhost:9049/api/tenants/${row.id}`, updateData);
    ElMessage.success('租户图标删除成功');
    fetchTenants();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除租户图标失败:', error);
      ElMessage.error('删除租户图标失败');
    }
  }
}

// 当前活动的Quill编辑器实例
let currentQuillInstance = null;

const handleAudioUpload = () => {
  console.log('🎵 点击音频上传按钮');
  const input = document.createElement('input');
  input.setAttribute('type', 'file');
  input.setAttribute('accept', 'audio/*');
  input.click();
  
  input.onchange = () => {
    const file = input.files[0];
    if (file) {
      // 验证音频文件大小 (限制50MB)
      if (file.size > 50 * 1024 * 1024) {
        ElMessage.error('音频文件大小不能超过50MB');
        return;
      }
      
      // 添加调试信息和格式验证
      console.log('📁 上传音频文件:', file.name, '类型:', file.type, '大小:', (file.size / 1024 / 1024).toFixed(2) + 'MB');
      
      // 验证音频文件格式并提供建议
      const supportedAudioTypes = ['audio/mpeg', 'audio/mp3', 'audio/wav', 'audio/ogg', 'audio/m4a'];
      if (!supportedAudioTypes.includes(file.type) && !file.name.match(/\.(mp3|wav|ogg|m4a)$/i)) {
        ElMessage.warning('建议使用 MP3、WAV、OGG 或 M4A 格式的音频文件以获得最佳兼容性');
      }
      
      const formData = new FormData();
      formData.append('file', file);
      
      // 上传音频到服务器
      axios.post('/api/tenants/upload-icon', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        console.log('📥 音频上传响应数据:', response.data);
        
        if (response.data && response.data.url) {
          console.log('🔗 后端返回的原始URL:', response.data.url);
          
          // 尝试多种可能的URL格式
          const possibleUrls = [
            response.data.url, // 原始URL
            response.data.url.startsWith('http') ? response.data.url : `http://localhost:9049${response.data.url}`, // 带域名
            response.data.url.replace('/icons/', '/uploads/'), // 可能的uploads路径
            response.data.url.replace('/icons/', '/files/'), // 可能的files路径
            response.data.url.replace('//icons/', '/icons/'), // 修复双斜杠
            `/api${response.data.url}`, // API路径
            `http://localhost:9049/api/files${response.data.url.replace('/icons', '')}` // API文件服务
          ];
          
          console.log('🔍 尝试的所有URL:', possibleUrls);
          
          // 使用第一个URL作为默认值，但在HTML中提供所有选项
          const audioUrl = possibleUrls[1];
          
          // 查找活动的编辑器
          let editorContainer = document.querySelector('#editor');
          // 如果找不到，尝试查找编辑对话框中的编辑器
          if (!editorContainer) {
            editorContainer = document.querySelector('#edit-editor');
          }
          
          if (editorContainer) {
            const quill = Quill.find(editorContainer);
            if (quill) {
              const range = quill.getSelection() || { index: 0 };
              const audioHtml = `
                <div style="margin: 10px 0; padding: 10px; border: 1px solid #ddd; border-radius: 8px; background: #f9f9f9;">
                  <p style="margin: 0 0 8px 0; font-weight: bold; color: #666;">🎵 音频文件: ${file.name}</p>
                  <audio controls style="width: 100%; margin-bottom: 10px;" preload="auto">
                    <source src="${audioUrl}" type="${file.type}">
                    <source src="${audioUrl}" type="audio/mpeg">
                    <source src="${audioUrl}" type="audio/wav">
                    <source src="${audioUrl}" type="audio/ogg">
                    <p style="color: #999; font-style: italic;">
                      您的浏览器不支持音频播放。
                      <a href="${audioUrl}" target="_blank" style="color: #007bff;">点击下载音频文件</a>
                    </p>
                  </audio>
                </div>
              `;
              quill.clipboard.dangerouslyPasteHTML(range.index, audioHtml);
              ElMessage.success('音频上传成功');
            } else {
              console.error('找不到Quill实例');
              ElMessage.error('无法将音频插入编辑器');
            }
          } else {
            console.error('找不到编辑器容器');
            ElMessage.error('找不到编辑器容器');
          }
        } else {
          ElMessage.error('音频上传失败：无效的响应数据');
        }
      }).catch(error => {
        console.error('音频上传失败:', error);
        ElMessage.error('音频上传失败');
      });
    }
  };
};

const handleVideoUpload = () => {
  console.log('🎬 点击视频上传按钮');
  const input = document.createElement('input');
  input.setAttribute('type', 'file');
  input.setAttribute('accept', 'video/*');
  input.click();
  
  input.onchange = () => {
    const file = input.files[0];
    if (file) {
      // 验证视频文件大小 (限制100MB)
      if (file.size > 100 * 1024 * 1024) {
        ElMessage.error('视频文件大小不能超过100MB');
        return;
      }
      
      // 添加调试信息和格式验证
      console.log('📁 上传视频文件:', file.name, '类型:', file.type, '大小:', (file.size / 1024 / 1024).toFixed(2) + 'MB');
      
      // 验证视频文件格式并提供建议
      const supportedVideoTypes = ['video/mp4', 'video/webm', 'video/ogg', 'video/avi', 'video/mov'];
      if (!supportedVideoTypes.includes(file.type) && !file.name.match(/\.(mp4|webm|ogg|avi|mov)$/i)) {
        ElMessage.warning('建议使用 MP4、WebM 或 OGG 格式的视频文件以获得最佳兼容性');
      }
      
      const formData = new FormData();
      formData.append('file', file);
      
      // 上传视频到服务器
      axios.post('/api/tenants/upload-icon', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        console.log('📥 视频上传响应数据:', response.data);
        
        if (response.data && response.data.url) {
          console.log('🔗 后端返回的原始URL:', response.data.url);
          
          // 尝试多种可能的URL格式
          const possibleUrls = [
            response.data.url, // 原始URL
            response.data.url.startsWith('http') ? response.data.url : `http://localhost:9049${response.data.url}`, // 带域名
            response.data.url.replace('/icons/', '/uploads/'), // 可能的uploads路径
            response.data.url.replace('/icons/', '/files/'), // 可能的files路径
            response.data.url.replace('//icons/', '/icons/'), // 修复双斜杠
            `/api${response.data.url}`, // API路径
            `http://localhost:9049/api/files${response.data.url.replace('/icons', '')}` // API文件服务
          ];
          
          console.log('🔍 尝试的所有视频URL:', possibleUrls);
          
          // 使用第一个URL作为默认值，但在HTML中提供所有选项
          const videoUrl = possibleUrls[1];
          
          // 查找活动的编辑器
          let editorContainer = document.querySelector('#editor');
          // 如果找不到，尝试查找编辑对话框中的编辑器
          if (!editorContainer) {
            editorContainer = document.querySelector('#edit-editor');
          }
          
          if (editorContainer) {
            const quill = Quill.find(editorContainer);
            if (quill) {
              const range = quill.getSelection() || { index: 0 };
              const videoHtml = `
                <div style="margin: 10px 0; padding: 10px; border: 1px solid #ddd; border-radius: 8px; background: #f9f9f9;">
                  <p style="margin: 0 0 8px 0; font-weight: bold; color: #666;">🎬 视频文件: ${file.name}</p>
                  <video controls style="width: 100%; max-width: 600px; border-radius: 8px; margin-bottom: 10px;" preload="auto">
                    <source src="${videoUrl}" type="${file.type}">
                    <source src="${videoUrl}" type="video/mp4">
                    <source src="${videoUrl}" type="video/webm">
                    <source src="${videoUrl}" type="video/ogg">
                    <p style="color: #999; font-style: italic;">
                      您的浏览器不支持视频播放。
                      <a href="${videoUrl}" target="_blank" style="color: #007bff;">点击下载视频文件</a>
                    </p>
                  </video>
                </div>
              `;
              quill.clipboard.dangerouslyPasteHTML(range.index, videoHtml);
              ElMessage.success('视频上传成功');
            } else {
              console.error('找不到Quill实例');
              ElMessage.error('无法将视频插入编辑器');
            }
          } else {
            console.error('找不到编辑器容器');
            ElMessage.error('找不到编辑器容器');
          }
        } else {
          ElMessage.error('视频上传失败：无效的响应数据');
        }
      }).catch(error => {
        console.error('视频上传失败:', error);
        ElMessage.error('视频上传失败');
      });
    }
  };
};

// AI功能相关变量
const aiPromptText = ref('')
const aiParsing = ref(false)
const originalFormData = ref({})

// 备注AI生成相关变量
const remarkPromptText = ref('')
const aiGeneratingRemark = ref(false)

// AI解析函数
const handleAiParse = async () => {
  if (!aiPromptText.value.trim()) {
    ElMessage.warning('请输入描述信息');
    return;
  }
  aiParsing.value = true;
  try {
    // 调用后端API解析文本
    const response = await axios.post('/api/ai/parse-form/tenant', { text: aiPromptText.value });
    
    if (response.data.error) {
      ElMessage.error('AI解析失败: ' + response.data.error);
      aiParsing.value = false;
      return;
    }
    
    // 更新表单数据
    const data = response.data;
    
    // 保存原始数据用于比较
    originalFormData.value = { ...formData.value };
    
    // 更新表单字段
    if (data.tenantName) formData.value.tenantName = data.tenantName;
    if (data.contactPerson) formData.value.contactPerson = data.contactPerson;
    if (data.phone) formData.value.phone = data.phone;
    
    // 尝试生成备注内容
    try {
      const remarkResponse = await axios.post('/api/ai/generate/tenant-remark', {
        tenantName: formData.value.tenantName || '未命名租户',
        description: aiPromptText.value
      });
      
      if (remarkResponse.data && remarkResponse.data.remark) {
        // 更新编辑器内容
        const editorContainer = document.querySelector('#editor');
        if (editorContainer) {
          // 获取Quill实例并设置内容
          const quill = Quill.find(editorContainer);
          if (quill) {
            // 直接设置HTML内容
            quill.root.innerHTML = remarkResponse.data.remark;
            // 确保formData中的remark也被更新
            formData.value.remark = remarkResponse.data.remark;
            console.log('AI生成的备注内容已设置到编辑器中');
          } else {
            console.error('找不到Quill实例');
            // 尝试重新初始化编辑器
            setTimeout(() => {
              initializeEditor('#editor', false, remarkResponse.data.remark);
            }, 100);
          }
        } else {
          console.error('找不到编辑器容器');
        }
      }
    } catch (remarkError) {
      console.error("AI生成备注错误:", remarkError);
      ElMessage.warning('表单填充成功，但备注生成失败。');
    }

    ElMessage.success('AI填充成功！已自动生成备注内容，请核对信息。');
  } catch (error) {
    console.error("AI parse error:", error);
    ElMessage.error('调用AI解析接口失败: ' + (error.response?.data?.message || error.message));
  } finally {
    aiParsing.value = false;
  }
};

// AI生成备注
const handleAiGenerateRemark = async () => {
  if (!remarkPromptText.value.trim() && !formData.value.tenantName) {
    ElMessage.warning('请输入公司描述信息或至少填写租户名称');
    return;
  }
  aiGeneratingRemark.value = true;
  try {
    // 调用后端AI生成API
    const response = await axios.post('/api/ai/generate/tenant-remark', { 
      tenantName: formData.value.tenantName || '未命名租户', 
      description: remarkPromptText.value 
    });
    const data = response.data;

    if (data.error) {
      ElMessage.error('AI生成失败: ' + data.error);
      return;
    }

    if (data.remark) {
      // 更新编辑器内容
      const editorContainer = document.querySelector('#editor');
      if (editorContainer) {
        const quill = Quill.find(editorContainer);
        if (quill) {
          // 直接设置HTML内容
          quill.root.innerHTML = data.remark;
          // 确保formData中的remark也被更新
          formData.value.remark = data.remark;
          ElMessage.success('AI已生成专业备注内容！');
        } else {
          console.error('找不到Quill实例');
          // 尝试重新初始化编辑器
          setTimeout(() => {
            initializeEditor('#editor', false, data.remark);
          }, 100);
        }
      } else {
        console.error('找不到编辑器容器');
        ElMessage.error('更新编辑器内容失败');
      }
    } else {
      ElMessage.warning('AI未返回有效内容');
    }
    
  } catch (error) {
    console.error("AI生成备注错误:", error);
    ElMessage.error('调用AI生成接口失败: ' + (error.response?.data?.message || error.message));
  } finally {
    aiGeneratingRemark.value = false;
  }
};

// 处理图片上传
const handleImageUpload = () => {
  const input = document.createElement('input');
  input.setAttribute('type', 'file');
  input.setAttribute('accept', 'image/*');
  input.click();
  
  input.onchange = () => {
    const file = input.files[0];
    if (file) {
      // 验证图片文件大小 (限制10MB)
      if (file.size > 10 * 1024 * 1024) {
        ElMessage.error('图片文件大小不能超过10MB');
        return;
      }
      
      const formData = new FormData();
      formData.append('file', file);
      
      // 上传图片到服务器
      axios.post('/api/tenants/upload-icon', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        if (response.data && response.data.url) {
          const imageUrl = response.data.url.startsWith('http') 
            ? response.data.url 
            : `http://localhost:9049${response.data.url}`;
          
          // 获取编辑器实例并插入图片
          let editorContainer = document.querySelector('#editor');
          // 如果找不到，尝试查找编辑对话框中的编辑器
          if (!editorContainer) {
            editorContainer = document.querySelector('#edit-editor');
          }
          
          if (editorContainer) {
            const quill = Quill.find(editorContainer);
            if (quill) {
              const range = quill.getSelection() || { index: 0 };
              quill.insertEmbed(range.index, 'image', imageUrl);
              ElMessage.success('图片上传成功');
            } else {
              console.error('找不到Quill实例');
            }
          } else {
            console.error('找不到编辑器容器');
          }
        } else {
          ElMessage.error('图片上传失败：无效的响应数据');
        }
      }).catch(error => {
        console.error('图片上传失败:', error);
        ElMessage.error('图片上传失败: ' + (error.response?.data?.message || error.message));
      });
    }
  };
};

return {
  searchForm,
  tenants,
  selectedRow,
  selectedRows,
  loading,
  currentPage,
  pageSize,
  total,
  addDialogVisible,
  editDialogVisible,
  detailsDialogVisible,
  formData,
  detailsFormData,
  fileList,
  rules,
  // 权限控制
  userRole,
  userTenantId,
  canEditTenant,
  canDeleteTenant,
  handleSearch,
  handleReset,
  handleExport,
  openAddDialog,
  closeAddDialog,
  openEditDialog,
  closeEditDialog,
  openDetailsDialog,
  closeDetailsDialog,
  submitForm,
  submitEditForm,
  handleUploadSuccess,
  handleUploadError,
  beforeUpload,
  handleRemove,
  handleDeleteConfirm,
  handleDelete,
  handleSelectionChange,
  handleSizeChange,
  handleCurrentChange,
  goToTenantDetail,
  getTenantIcon,
  handleImageError,
  imagePreviewVisible,
  previewImageUrl,
  showImagePreview,
  closeImagePreview,
  deleteTenantIcon,
  handleAudioUpload,
  handleVideoUpload,
  aiPromptText,
  aiParsing,
  originalFormData,
  remarkPromptText,
  aiGeneratingRemark,
  handleAiParse,
  handleAiGenerateRemark,
  handleImageUpload
};
}
};
</script>

<style scoped>
.container {
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 70% 70%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.el-row {
  margin-bottom: 20px;
  position: relative;
  z-index: 1;
}

.el-table,
.el-card,
.el-button,
.el-input,
.el-select,
.el-pagination {
  position: relative;
  z-index: 1;
}

.action-buttons {
  display: flex;
  justify-content: space-around;
}

.editor-wrapper {
  width: 100%;
  height: 400px;
}

.dialog-form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.tenant-icon-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tenant-icon-container img {
  transition: transform 0.3s ease;
}

.tenant-icon-container img:hover {
  transform: scale(1.1);
}

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

/* 富文本编辑器中的媒体样式 */
.ql-editor img {
  cursor: pointer;
  transition: transform 0.3s ease;
  max-width: 100%;
  height: auto;
}

.ql-editor img:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.ql-editor audio {
  width: 100%;
  margin: 10px 0;
  border-radius: 8px;
}

.ql-editor video {
  width: 100%;
  max-width: 600px;
  margin: 10px 0;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 自定义工具栏按钮样式 */
:deep(.ql-toolbar) button[title="插入音频"],
:deep(.ql-toolbar) button[title="插入视频"] {
  width: 28px;
  height: 28px;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: white;
  margin: 0 2px;
  cursor: pointer;
  font-size: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

:deep(.ql-toolbar) button[title="插入音频"]:hover,
:deep(.ql-toolbar) button[title="插入视频"]:hover {
  background: #f0f0f0;
  border-color: #999;
}

/* 媒体提示样式 */
.media-tips {
  margin-bottom: 16px;
}

.media-tips .el-alert {
  border-radius: 8px;
}

.media-tips ul {
  margin: 8px 0 0 0;
  padding-left: 20px;
}

.media-tips li {
  margin: 4px 0;
  font-size: 14px;
}

/* 自定义媒体按钮样式 */
.custom-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.custom-media-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.custom-media-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.custom-media-btn:active {
  transform: translateY(0);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.audio-btn {
  color: #6f42c1;
}

.video-btn {
  color: #e83e8c;
}

/* 分页器样式 */
.el-pagination {
  text-align: right;
  margin-top: 20px;
}

.hidden-input {
  display: none;
}

/* 添加AI相关样式 */
.form-section {
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 20px;
}

.form-section h4 {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #409eff;
}

.form-section h4 .el-icon {
  margin-right: 8px;
}

.ai-section {
  background-color: #f0f9ff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
  border: 1px solid #d9ecff;
}

.edit-container {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.panel {
  flex: 1;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.left-panel {
  background-color: #f5f7fa;
}

.right-panel {
  background-color: #fff;
}

.panel h3 {
  font-size: 16px;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

/* AI备注生成区域样式 */
.ai-remark-section {
  background-color: #f0f9ff;
  border-radius: 8px;
  padding: 16px;
  margin: 16px 0;
  border: 1px solid #d9ecff;
}

.image-btn {
  color: #409eff;
}

.audio-btn {
  color: #67c23a;
}

.video-btn {
  color: #f56c6c;
}

/* 编辑器包装容器 */
.editor-wrapper {
  width: 100% !important;
  max-width: 100% !important;
  overflow: hidden !important;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

/* 添加额外的编辑器容器包装 */
.editor-container-wrapper {
  width: 100%;
  max-width: 100%;
  overflow-x: hidden;
  position: relative;
}

.editor-container {
  height: 300px;
  overflow-y: auto;
  width: 100%;
  max-width: 100%;
  overflow-x: hidden;
  box-sizing: border-box;
  position: relative;
}

/* 强制Quill编辑器内容区域宽度固定 */
:deep(.ql-container),
:deep(.ql-editor) {
  min-height: 280px;
  width: 100% !important;
  max-width: 100% !important;
  overflow-x: hidden !important;
  word-break: break-word !important;
  white-space: pre-wrap !important;
  box-sizing: border-box !important;
}

/* 确保图片等媒体内容不超出容器 */
:deep(.ql-editor) img,
:deep(.ql-editor) audio,
:deep(.ql-editor) video {
  max-width: 100% !important;
  height: auto !important;
}

/* 确保文本内容不超出容器 */
:deep(.ql-editor) p,
:deep(.ql-editor) div,
:deep(.ql-editor) h1,
:deep(.ql-editor) h2,
:deep(.ql-editor) h3,
:deep(.ql-editor) h4,
:deep(.ql-editor) h5,
:deep(.ql-editor) h6,
:deep(.ql-editor) ul,
:deep(.ql-editor) ol,
:deep(.ql-editor) li,
:deep(.ql-editor) blockquote,
:deep(.ql-editor) pre,
:deep(.ql-editor) code {
  max-width: 100% !important;
  word-wrap: break-word !important;
  overflow-wrap: break-word !important;
  white-space: pre-wrap !important;
}

/* 确保编辑器工具栏不溢出 */
:deep(.ql-toolbar) {
  width: 100% !important;
  max-width: 100% !important;
  overflow-x: auto !important;
  flex-wrap: wrap !important;
}

.ai-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}
</style>

<style>
/* 全局样式修复，确保富文本编辑器内容不会溢出 */
.ql-editor {
  max-width: 100% !important;
  overflow-x: hidden !important;
  word-break: break-word !important;
  white-space: pre-wrap !important;
}

.ql-editor * {
  max-width: 100% !important;
  overflow-wrap: break-word !important;
}

.ql-editor p {
  white-space: pre-wrap !important;
}

.ql-container {
  max-width: 100% !important;
}

.ql-tooltip {
  left: 0 !important;
  max-width: 100% !important;
  overflow-x: auto !important;
}
</style>