<template>
  <div class="info-management-container">
    <el-main>
      <!-- 搜索栏 -->
      <el-row :gutter="20" class="search-bar">
        <el-col :span="4">
          <el-input
              placeholder="请输入新闻标题"
              v-model="searchForm.title"
              class="search-box"
          ></el-input>
        </el-col>
        <el-col :span="4">
          <el-input
              placeholder="请输入作者"
              v-model="searchForm.author"
              class="search-box"
          ></el-input>
        </el-col>
        <el-col :span="4">
          <el-input
              placeholder="请输入新闻简介"
              v-model="searchForm.introduction"
              class="search-box"
          ></el-input>
        </el-col>
        <el-col :span="2">
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        </el-col>
        <el-col :span="2">
          <el-button type="default" icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>

      <!-- 功能按钮栏 -->
      <el-row :gutter="20" style="margin-top: 10px;">
        <el-col :span="24" class="button-bar">
          <el-button v-if="canCreateInformation" type="primary" icon="el-icon-plus" @click="openDialog">新建</el-button>
          <el-button v-if="canModifyInformation" type="success" icon="el-icon-edit" @click="handleEdit">修改</el-button>
          <el-button v-if="canDeleteInformation" type="danger" icon="el-icon-delete" @click="handleDelete">删除</el-button>
          <el-button type="warning" icon="el-icon-upload2" @click="handleExport">导出</el-button>
        </el-col>
      </el-row>

      <!-- 资讯列表表格 -->
      <el-table :data="paginatedInformationList" style="width: 100%; margin-top: 20px;" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="title" label="新闻标题"></el-table-column>
        <el-table-column prop="introduction" label="新闻简介"></el-table-column>
        <el-table-column prop="author" label="作者"></el-table-column>
        <el-table-column prop="company" label="公司" v-if="isSystemAdmin"></el-table-column>
        <el-table-column label="新闻图片" width="200">
          <template #default="scope">
            <div class="image-container" style="display: flex; flex-direction: column; align-items: center; gap: 4px;">
              <div style="display: flex; align-items: center; gap: 8px;">
                <img 
                  :src="getImageUrl(scope.row.picture)" 
                  alt="新闻图片" 
                  style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px; cursor: pointer; border: 1px solid #ddd;"
                  @error="(event) => handleImageError(event, scope.row)"
                  @load="(event) => handleImageLoad(event, scope.row)"
                  @click="showImagePreview(getPreviewableImageUrl(scope.row.picture))"
                  title="点击预览大图"
                >
                <el-button 
                  v-if="canModifySpecificInformation(scope.row)"
                  type="danger" 
                  size="small" 
                  :icon="Delete"
                  @click="deleteInformationImage(scope.row)"
                  title="删除图片"
                />
              </div>
              <!-- 显示图片路径调试信息 -->
              <div v-if="scope.row.picture" style="font-size: 10px; color: #999; text-align: center; max-width: 180px; word-break: break-all;">
                {{ scope.row.picture }}
              </div>
              <div v-else style="font-size: 10px; color: #ff6b6b; text-align: center;">
                无图片
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-link @click="viewDetails(scope.row)" type="success" class="action-link">详情</el-link>
            <el-link 
              v-if="canModifySpecificInformation(scope.row)" 
              @click="editInformation(scope.row)" 
              type="primary" 
              class="action-link"
            >
              修改
            </el-link>
            <el-link 
              v-if="canDeleteSpecificInformation(scope.row)" 
              @click="confirmDelete(scope.row.id)" 
              type="danger" 
              class="action-link"
            >
              删除
            </el-link>
            <span v-if="!canModifySpecificInformation(scope.row) && !isSystemAdmin" style="color: #999; font-size: 12px;">
              (其他公司资讯)
            </span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredInformationList.length"
          :page-sizes="[10, 20, 30, 40]"
          v-model:page-size="pageSize"
          v-model:current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
          style="margin-top: 20px; text-align: right;"
      >
      </el-pagination>

      <!-- 新建/编辑资讯弹窗 -->
      <el-dialog v-model="dialogVisible" title="添加资讯管理" width="60%">
        <el-form :model="form" :rules="formRules" ref="informationForm">
          <el-form-item label="新闻标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入新闻标题"></el-input>
          </el-form-item>
          <el-form-item label="新闻图片路径" prop="picture">
            <el-upload
                class="upload-demo"
                action="/tenants/upload-icon"
                :on-success="handleSuccess"
                :on-error="handleUploadError"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :before-upload="beforeUpload"
                :file-list="fileList"
                accept="image/*"
                :limit="1"
            >
              <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
              <div slot="tip" class="el-upload__tip">请上传大小不超过 5MB 格式为 png/jpg/jpeg 的文件</div>
            </el-upload>
            
            <!-- 调试信息显示 -->
            <div v-if="form.picture" class="debug-info" style="margin-top: 10px; padding: 8px; background: #f0f0f0; border-radius: 4px; font-size: 12px;">
              <div><strong>存储路径:</strong> {{ form.picture }}</div>
              <div><strong>显示URL:</strong> {{ getImageUrl(form.picture) }}</div>
              <div style="margin-top: 5px;">
                <img :src="getImageUrl(form.picture)" style="max-width: 100px; max-height: 60px; border: 1px solid #ddd;" 
                     @load="() => console.log('✅ 表单图片预览加载成功')"
                     @error="(event) => handleImageTestError(event)" />
              </div>
              <!-- 备用路径测试 -->
              <div style="margin-top: 8px; font-size: 11px; color: #666;">
                <div><strong>备用路径测试:</strong></div>
                <div v-for="testPath in getTestPaths(form.picture)" :key="testPath" style="margin: 2px 0;">
                  <a :href="testPath" target="_blank" style="color: #007bff; text-decoration: none;">
                    {{ testPath }}
                  </a>
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="新闻内容" prop="content">
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
          <el-form-item label="作者">
            <el-input v-model="form.author" placeholder="请输入作者"></el-input>
          </el-form-item>
          <el-form-item label="新闻简介" prop="introduction">
            <el-input v-model="form.introduction" type="textarea" placeholder="请输入新闻简介"></el-input>
          </el-form-item>
          <el-form-item label="摘要" prop="summary">
            <el-input
              v-model="form.summary"
              type="textarea"
              :rows="3"
              placeholder="请输入内容摘要"
            />
          </el-form-item>
          <el-form-item label="标签" prop="tags">
            <el-input v-model="form.tags" placeholder="请输入标签，用逗号分隔" />
          </el-form-item>
          <el-form-item label="选择租户">
            <el-select v-model="form.tenantId" placeholder="请选择" @change="handleTenantChange">
              <el-option
                  v-for="item in tenants"
                  :key="item.id"
                  :label="item.tenantName"
                  :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button @click="generateSummaryAndTags" :loading="aiLoading" type="primary" plain>
              <el-icon style="margin-right: 8px;"><MagicStick /></el-icon>
              AI 生成摘要和标签
            </el-button>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveInformation">确定</el-button>
        </span>
      </el-dialog>

      <!-- 详情弹窗 -->
      <el-dialog v-model="detailsDialogVisible" title="资讯详情" width="80%">
        <div class="details-content">
          <div class="details-header">
            <h2>{{ selectedInformation.title }}</h2>
            <div class="details-meta">
              <span>作者：{{ selectedInformation.author }}</span>
              <span>简介：{{ selectedInformation.introduction }}</span>
            </div>
          </div>
          <div class="details-image" v-if="selectedInformation.picture">
            <img 
              :src="getImageUrl(selectedInformation.picture)" 
              alt="资讯图片" 
              style="max-width: 100%; max-height: 300px; object-fit: contain; border-radius: 8px; cursor: pointer; transition: transform 0.3s ease;"
              @error="event => event.target.src = '/images/default-icon.jpg'"
              @click="showImagePreview(getImageUrl(selectedInformation.picture))"
              @mouseenter="event => event.target.style.transform = 'scale(1.05)'"
              @mouseleave="event => event.target.style.transform = 'scale(1)'"
              title="点击查看大图"
            />
          </div>
          <div class="details-content-html" v-html="selectedInformation.content" @click="handleContentClick"></div>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="closeDetailsDialog">关闭</el-button>
          </div>
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
    </el-main>
  </div>
</template>

<script>
import axios from '../utils/request.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ref, onMounted, computed } from 'vue';
import { Delete, MagicStick } from '@element-plus/icons-vue';
import Editor from './Editor/index.vue';
import Quill from 'quill';
import 'quill/dist/quill.core.css';
import 'quill/dist/quill.snow.css';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

export default {
  components: {
    Editor
  },
  setup() {
    const informationList = ref([]);
    const dialogVisible = ref(false);
    const detailsDialogVisible = ref(false);
    const isEditing = ref(false);
    const selectedInformation = ref({});
    const form = ref({
      id: '',
      title: '',
      introduction: '',
      author: '',
      content: '<p>这里是默认的新闻内容。你可以在此编辑或添加新的内容。</p>',
      company: '',
      tenantId: '',
      picture: '',
      summary: '',
      tags: ''
    });
    const formRules = ref({
      title: [{
        required: true,
        message: '请输入新闻标题',
        trigger: 'blur'
      }],
      introduction: [{
        required: true,
        message: '请输入新闻简介',
        trigger: 'blur'
      }],
      content: [{
        required: true,
        message: '请输入新闻内容',
        trigger: 'blur'
      }],
      picture: [{
        required: true,
        message: '请上传新闻图片',
        trigger: 'change'
      }]
    });
    const tenants = ref([]);
    const fileList = ref([]);
    const selectedRows = ref([]);
    const searchForm = ref({
      title: '',
      author: '',
      introduction: '',
    });
    const filteredInformationList = ref([]);
    const currentPage = ref(1);
    const pageSize = ref(10);
    const imagePreviewVisible = ref(false);
    const previewImageUrl = ref('');
    const currentQuillInstance = ref(null);
    const aiLoading = ref(false);
    const informationForm = ref(null);

    // --- Computed Properties ---
    const paginatedInformationList = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      const end = start + pageSize.value;
      return filteredInformationList.value.slice(start, end);
    });

    const currentUser = computed(() => {
      return JSON.parse(localStorage.getItem('userInfo') || '{}');
    });

    const isSystemAdmin = computed(() => currentUser.value.role === 'Admin');
    const isTenantAdmin = computed(() => currentUser.value.role === 'TAdmin');
    const isRegularUser = computed(() => currentUser.value.role === 'User');
    const canCreateInformation = computed(() => isSystemAdmin.value || isTenantAdmin.value);
    const canModifyInformation = computed(() => isSystemAdmin.value || isTenantAdmin.value);
    const canDeleteInformation = computed(() => isSystemAdmin.value || isTenantAdmin.value);

    // --- Methods ---
    const canModifySpecificInformation = (information) => {
      if (isSystemAdmin.value) return true;
      if (isTenantAdmin.value) return currentUser.value.tenantId === information.tenantId;
      return false;
    };

    const canDeleteSpecificInformation = (information) => {
      if (isSystemAdmin.value) return true;
      if (isTenantAdmin.value) return currentUser.value.tenantId === information.tenantId;
      return false;
    };

    const getImageUrl = (imagePath) => {
        if (!imagePath || imagePath === 'null' || imagePath === 'undefined' || imagePath === null || imagePath === undefined) {
          return '/images/default-icon.jpg'
        }
        const pathStr = String(imagePath).trim();
        if (pathStr.startsWith('http://') || pathStr.startsWith('https://')) {
          return pathStr
        }
        if (pathStr.startsWith('/')) {
          return `http://localhost:9049${pathStr}`
        }
        let fullUrl = `http://localhost:9049/${pathStr.includes('/') ? '' : 'icons/'}${pathStr}`;
        return fullUrl
    };

    const fetchInformation = async () => {
      try {
        const response = await axios.get('/api/information');
        const informationData = response.data.data.map(info => ({
          ...info,
          displayPicture: getImageUrl(info.picture)
        }));
        informationList.value = informationData;
        filteredInformationList.value = informationData;
      } catch (error) {
        console.error('❌ 获取资讯时出错:', error);
        ElMessage.error('获取资讯数据失败');
      }
    };

    const fetchTenants = async () => {
      try {
        const response = await axios.get('/api/tenants/all');
        tenants.value = response.data || [];
      } catch (error) {
        console.error('获取租户信息时出错:', error);
      }
    };
    
    onMounted(() => {
      fetchInformation();
      fetchTenants();
    });

    const resetForm = () => {
      form.value = {
        id: '', title: '', introduction: '', author: '',
        content: '<p>这里是默认的新闻内容。</p>', company: '', tenantId: '',
        picture: '', summary: '', tags: ''
      };
      fileList.value = [];
      if (informationForm.value) {
        informationForm.value.resetFields();
      }
    };

    const openDialog = () => {
      resetForm();
      isEditing.value = false;
      dialogVisible.value = true;
    };

    const editInformation = (info) => {
      if (!canModifySpecificInformation(info)) {
        ElMessage.error('您无权修改此资讯');
        return;
      }
      isEditing.value = true;
      form.value = { ...info };
      if (form.value.picture) {
        fileList.value = [{ name: '当前图片', url: getImageUrl(form.value.picture) }];
      } else {
        fileList.value = [];
      }
      dialogVisible.value = true;
    };

    const saveInformation = async () => {
      if (!informationForm.value) return;
      const valid = await informationForm.value.validate();
      if (!valid) {
        ElMessage.error('请完善必填信息');
        return;
      }

      if (!isEditing.value) {
        form.value.tenantId = currentUser.value.tenantId;
        form.value.company = currentUser.value.companyName || '未知公司';
        form.value.author = currentUser.value.nickname || currentUser.value.username;
      }

      const operation = isEditing.value ?
        axios.put(`/api/information/${form.value.id}`, form.value) :
        axios.post('/api/information', form.value);

      try {
        await operation;
        await fetchInformation();
        dialogVisible.value = false;
        ElMessage.success(isEditing.value ? '资讯更新成功' : '资讯添加成功');
      } catch (error) {
        console.error(`${isEditing.value ? '更新' : '添加'}资讯时出错:`, error);
        ElMessage.error(`${isEditing.value ? '更新' : '添加'}资讯失败`);
      }
    };

    const confirmDelete = (id) => {
      const info = informationList.value.find(i => i.id === id);
      if (info && !canDeleteSpecificInformation(info)) {
        ElMessage.error('您无权删除此资讯');
        return;
      }
      ElMessageBox.confirm('此操作将永久删除该资讯, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        deleteInformation(id);
      }).catch(() => {
        ElMessage.info('已取消删除');
      });
    };

    const deleteInformation = async (id) => {
      try {
        await axios.delete(`/api/information/${id}`);
        await fetchInformation();
        ElMessage.success('资讯删除成功');
      } catch (error) {
        console.error('删除资讯时出错:', error);
        ElMessage.error('删除资讯失败');
      }
    };
    
    const handleSuccess = (response, file) => {
        const resData = response.data || response;
        const imageUrl = resData.url || resData.path || resData.fileName || resData.filePath;
        if(imageUrl) {
            form.value.picture = imageUrl.replace('http://localhost:9049', '');
            fileList.value = [{ name: file.name, url: getImageUrl(form.value.picture) }];
            ElMessage.success('图片上传成功');
        } else {
            ElMessage.error('图片上传失败，无法获取URL');
        }
    };
    
    const handleUploadError = (error) => {
        console.error('图片上传失败:', error);
        ElMessage.error('图片上传失败');
    };

    const generateSummaryAndTags = async () => {
      if (!form.value.content || form.value.content.trim() === '') {
        ElMessage.warning('请先在内容编辑器中输入内容！');
        return;
      }
      aiLoading.value = true;
      try {
        const response = await fetch('/api/ai/summarize', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ content: form.value.content })
        });
        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.error || 'AI 服务响应失败');
        }
        const aiResult = await response.json();
        const aiContent = JSON.parse(aiResult.choices[0].message.content);
        if (aiContent.summary) form.value.summary = aiContent.summary;
        if (aiContent.tags && Array.isArray(aiContent.tags)) form.value.tags = aiContent.tags.join(', ');
        ElMessage.success('摘要和标签已由 AI 生成！');
      } catch (error) {
        console.error('AI 生成失败:', error);
        ElMessage.error(`AI 生成失败: ${error.message}`);
      } finally {
        aiLoading.value = false;
      }
    };
    
    // ... other methods like handleSearch, resetSearch etc.
     const handleSearch = () => {
       filteredInformationList.value = informationList.value.filter(info => {
          return (
             (!searchForm.value.title || info.title.includes(searchForm.value.title)) &&
             (!searchForm.value.author || info.author.includes(searchForm.value.author)) &&
             (!searchForm.value.introduction || info.introduction.includes(searchForm.value.introduction))
          );
        });
       currentPage.value = 1;
     };

     const resetSearch = () => {
       searchForm.value = { title: '', author: '', introduction: '' };
       filteredInformationList.value = informationList.value;
       currentPage.value = 1;
     };

     const handlePageChange = (page) => { currentPage.value = page; };
     const handleSizeChange = (size) => { pageSize.value = size; currentPage.value = 1; };
     const handleRowClick = (row) => { selectedInformation.value = row; };
     const handleSelectionChange = (rows) => { selectedRows.value = rows; };
     const updateContent = (value) => { form.value.content = value; };

    return {
      informationList, dialogVisible, detailsDialogVisible, isEditing, selectedInformation, form, formRules,
      tenants, fileList, selectedRows, searchForm, filteredInformationList, currentPage, pageSize,
      imagePreviewVisible, previewImageUrl, currentQuillInstance, aiLoading, informationForm,
      paginatedInformationList, currentUser, isSystemAdmin, isTenantAdmin, isRegularUser, canCreateInformation,
      canModifyInformation, canDeleteInformation, canModifySpecificInformation, canDeleteSpecificInformation,
      fetchInformation, fetchTenants, resetForm, openDialog, editInformation, saveInformation, confirmDelete,
      deleteInformation, handleSuccess, handleUploadError, generateSummaryAndTags, handleSearch, resetSearch,
      handlePageChange, handleSizeChange, handleRowClick, handleSelectionChange, updateContent, getImageUrl
    };
  }
};
</script>

<style>
.info-management-container {
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.info-management-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 40% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 60% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.el-table,
.el-card,
.el-button,
.el-input,
.el-select,
.el-pagination,
.el-dialog {
  position: relative;
  z-index: 1;
}

.el-table {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.el-button + .el-button {
  margin-left: 10px;
}

.upload-demo {
  width: 200px;
  margin: 0 auto;
}

.search-bar {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-top: 10px;
}

.search-box {
  margin-right: 10px;
  width: 200px;
}

.action-link {
  margin-right: 10px;
  color: blue;
}

.image-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.image-container img {
  transition: transform 0.3s ease;
}

.image-container img:hover {
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

.editor-wrapper {
  width: 100%;
  height: 400px;
}

.editor-container {
  height: 350px;
}

.details-content {
  max-height: 80vh;
  overflow-y: auto;
}

.details-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.details-header h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.details-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.details-image {
  margin: 20px 0;
  text-align: center;
}

.details-content-html {
  margin-top: 20px;
  line-height: 1.6;
}

:deep(.ql-editor) img,
.details-content-html img {
  cursor: pointer;
  transition: transform 0.3s ease;
  max-width: 100%;
  height: auto;
  border-radius: 8px;
}

:deep(.ql-editor) img:hover,
.details-content-html img:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

:deep(.ql-editor) audio {
  width: 100%;
  margin: 10px 0;
  border-radius: 8px;
}

:deep(.ql-editor) video {
  width: 100%;
  max-width: 600px;
  margin: 10px 0;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

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
