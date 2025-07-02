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
          <el-button type="primary" @click="openAddDialog">新增</el-button>
          <el-button type="warning" @click="openEditDialog">修改</el-button>
          <el-button type="danger" @click="handleDeleteConfirm">删除</el-button>
          <el-button type="success" @click="handleExport">导出</el-button>
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
              <el-button type="link" size="small" @click="openEditDialog(scope.row)">修改</el-button>
              <el-button type="link" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
                  action="http://localhost:9049/tenants/upload-icon"
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
                  <div id="editor" class="editor-container"></div>
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
  <el-dialog v-model="editDialogVisible" title="修改租户" width="60%">
        <el-form :model="formData" :rules="rules" ref="editTenantForm" label-width="100px" class="dialog-form">
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
                  action="http://localhost:9049/tenants/upload-icon"
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
                  <div id="edit-editor" class="editor-container"></div>
                </div>
              </el-form-item>
          </el-row>
        </el-form>
        <template #footer>
          <el-button @click="closeEditDialog">取消</el-button>
          <el-button type="primary" @click="submitEditForm">确定</el-button>
        </template>
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
import { ref, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete } from '@element-plus/icons-vue';
import Quill from 'quill';
import 'quill/dist/quill.core.css';
import 'quill/dist/quill.snow.css';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import { useRouter } from 'vue-router';

export default {
name: 'TenantManagement',
setup() {
const searchForm = ref({
tenantName: '',
contactPerson: '',
phone: ''
});
const tenants = ref([]);
const selectedRow = ref(null);
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

const fetchTenants = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/api/tenants/all');
    tenants.value = response.data.tenantList;
    total.value = response.data.total;
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
  axios.get('http://localhost:9049/tenants/search', { params })
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

const openEditDialog = (row) => {
  if (row) {
    Object.assign(formData.value, row);
  } else if (selectedRow.value) {
    Object.assign(formData.value, selectedRow.value);
  } else {
    ElMessage.error('请先选择要修改的租户');
    return;
  }
  fileList.value = [
    {
      name: '租户图标',
      url: `http://localhost:9049/${formData.value.icon}`
    }
  ];
  editDialogVisible.value = true;
  nextTick(() => {
    initializeEditor('#edit-editor', true, formData.value.remark);
  });
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

  // 使用正确的后端接口
  axios.post('http://localhost:9049/tenants', formData.value).then(() => {
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

  // 使用正确的后端接口
  axios.put(`http://localhost:9049/tenants/${formData.value.id}`, formData.value).then(() => {
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
    return;
  }

  const toolbar = editorContainer.previousElementSibling;
  if (toolbar && toolbar.classList.contains('ql-toolbar')) {
    toolbar.remove();
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

  const quill = new Quill(selector, {
    theme: 'snow',
    modules: {
      toolbar: toolbarOptions
    }
  });

  if (content) {
    quill.root.innerHTML = content;
  }

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
        axios.post('http://localhost:9049/tenants/upload-icon', formData, {
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
        axios.post('http://localhost:9049/tenants/upload-icon', formData, {
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
                <audio controls style="width: 100%;" preload="metadata">
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
        axios.post('http://localhost:9049/tenants/upload-icon', formData, {
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
                <video controls style="width: 100%; max-width: 600px; border-radius: 4px;" preload="metadata">
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

  quill.on('text-change', () => {
    formData.value.remark = quill.root.innerHTML;
  });

  console.log('📝 Quill编辑器初始化完成');
  console.log('编辑器容器:', quill.container);
  console.log('编辑器主题:', quill.theme);

  // 保存当前编辑器实例供外部按钮使用
  currentQuillInstance = quill;

  return quill;
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
    
    await axios.put(`http://localhost:9049/tenants/${row.id}`, updateData);
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
      axios.post('http://localhost:9049/tenants/upload-icon', formData, {
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
          console.log('🔗 最终使用的音频URL:', audioUrl);
          console.log('📝 记录音频地址，准备插入播放器');
          
                     if (currentQuillInstance) {
             const range = currentQuillInstance.getSelection() || { index: 0 };
             
             // 生成音频播放器HTML
             const audioHtml = `
               <div style="margin: 15px 0; padding: 15px; border: 2px solid #e3f2fd; border-radius: 12px; background: linear-gradient(135deg, #f8f9ff 0%, #e3f2fd 100%); box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                 <div style="display: flex; align-items: center; margin-bottom: 10px;">
                   <span style="font-size: 24px; margin-right: 8px;">🎵</span>
                   <div>
                     <p style="margin: 0; font-weight: bold; color: #1976d2; font-size: 16px;">${file.name}</p>
                     <p style="margin: 0; font-size: 12px; color: #666;">音频文件 • ${(file.size / 1024 / 1024).toFixed(2)} MB</p>
                   </div>
                 </div>
                                   <audio controls style="width: 100%; margin-bottom: 10px;" preload="auto">
                    ${possibleUrls.map(url => `<source src="${url}" type="${file.type}">`).join('')}
                    ${possibleUrls.map(url => `<source src="${url}" type="audio/mpeg">`).join('')}
                    ${possibleUrls.map(url => `<source src="${url}" type="audio/wav">`).join('')}
                    您的浏览器不支持音频播放。
                  </audio>
                  <div style="margin-bottom: 10px; padding: 8px; background: #f0f0f0; border-radius: 4px; font-size: 12px;">
                    <strong>调试信息:</strong><br>
                    ${possibleUrls.map((url, index) => `<a href="${url}" target="_blank" style="display: block; color: #666; margin: 2px 0;">URL ${index + 1}: ${url}</a>`).join('')}
                  </div>
                 <div style="text-align: center;">
                   <a href="${audioUrl}" target="_blank" style="color: #1976d2; text-decoration: none; font-size: 12px;">📁 打开文件链接</a>
                 </div>
               </div>
             `;
             
             currentQuillInstance.clipboard.dangerouslyPasteHTML(range.index, audioHtml);
             ElMessage.success('音频上传成功');
           } else {
             ElMessage.error('编辑器未就绪');
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
      axios.post('http://localhost:9049/tenants/upload-icon', formData, {
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
          console.log('🔗 最终使用的视频URL:', videoUrl);
          console.log('📝 记录视频地址，准备插入播放器');
          
                     if (currentQuillInstance) {
             const range = currentQuillInstance.getSelection() || { index: 0 };
             
             // 生成视频播放器HTML
             const videoHtml = `
               <div style="margin: 15px 0; padding: 15px; border: 2px solid #fff3e0; border-radius: 12px; background: linear-gradient(135deg, #fffbf0 0%, #fff3e0 100%); box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                 <div style="display: flex; align-items: center; margin-bottom: 10px;">
                   <span style="font-size: 24px; margin-right: 8px;">🎬</span>
                   <div>
                     <p style="margin: 0; font-weight: bold; color: #f57c00; font-size: 16px;">${file.name}</p>
                     <p style="margin: 0; font-size: 12px; color: #666;">视频文件 • ${(file.size / 1024 / 1024).toFixed(2)} MB</p>
                   </div>
                 </div>
                                   <video controls style="width: 100%; max-width: 600px; border-radius: 8px; margin-bottom: 10px;" preload="auto">
                    ${possibleUrls.map(url => `<source src="${url}" type="${file.type}">`).join('')}
                    ${possibleUrls.map(url => `<source src="${url}" type="video/mp4">`).join('')}
                    ${possibleUrls.map(url => `<source src="${url}" type="video/webm">`).join('')}
                    您的浏览器不支持视频播放。
                  </video>
                  <div style="margin-bottom: 10px; padding: 8px; background: #f0f0f0; border-radius: 4px; font-size: 12px;">
                    <strong>调试信息:</strong><br>
                    ${possibleUrls.map((url, index) => `<a href="${url}" target="_blank" style="display: block; color: #666; margin: 2px 0;">URL ${index + 1}: ${url}</a>`).join('')}
                  </div>
                 <div style="text-align: center;">
                   <a href="${videoUrl}" target="_blank" style="color: #f57c00; text-decoration: none; font-size: 12px;">📁 打开文件链接</a>
                 </div>
               </div>
             `;
             
             currentQuillInstance.clipboard.dangerouslyPasteHTML(range.index, videoHtml);
             ElMessage.success('视频上传成功');
           } else {
             ElMessage.error('编辑器未就绪');
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

return {
  searchForm,
  tenants,
  selectedRow,
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
  handleVideoUpload
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
</style>