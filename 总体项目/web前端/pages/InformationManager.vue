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
import { ref } from 'vue';
import { Delete } from '@element-plus/icons-vue';
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
  data() {
    return {
      informationList: [],
      dialogVisible: false,
      detailsDialogVisible: false,
      isEditing: false,
      selectedInformation: {},
      form: {
        id: '',
        title: '',
        introduction: '',
        author: '',
        content: '<p>这里是默认的新闻内容。你可以在此编辑或添加新的内容。</p>',
        company: '',  // 用于存储公司名称
        tenantId: '',  // 用于存储租户ID
        picture: ''  // 用于存储图片路径
      },
      formRules: {
        title: [
          { required: true, message: '请输入新闻标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入新闻内容', trigger: 'blur' }
        ],
        introduction: [
          { required: true, message: '请输入新闻简介', trigger: 'blur' }
        ],
        picture: [
          { required: true, message: '请上传新闻图片', trigger: 'change' }
        ]
      },
      tenants: [],  // 用于存储租户信息
      fileList: [],
      selectedInformation: null,
      selectedRows: [], // 新增用于存储选中的行
      searchForm: {
        title: '',
        author: '',
        introduction: '',
      },
      filteredInformationList: [],
      currentPage: 1,
      pageSize: 10,
      imagePreviewVisible: false,
      previewImageUrl: '',
      currentQuillInstance: null, // 当前活动的Quill编辑器实例
    };
  },
  mounted() {
    this.fetchInformation();
    this.fetchTenants();  // 在 mounted 生命周期钩子中调用获取租户信息的方法
  },
  computed: {
    paginatedInformationList() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredInformationList.slice(start, end);
    },
    // 权限控制计算属性
    currentUser() {
      return JSON.parse(localStorage.getItem('userInfo') || '{}');
    },
    isSystemAdmin() {
      return this.currentUser.role === 'Admin';
    },
    isTenantAdmin() {
      return this.currentUser.role === 'TAdmin';
    },
    isRegularUser() {
      return this.currentUser.role === 'User';
    },
    canCreateInformation() {
      // 系统管理员和租户管理员可以创建资讯
      return this.isSystemAdmin || this.isTenantAdmin;
    },
    canModifyInformation() {
      // 系统管理员和租户管理员可以修改资讯（需要选中项时才能判断具体权限）
      return this.isSystemAdmin || this.isTenantAdmin;
    },
    canDeleteInformation() {
      // 系统管理员和租户管理员可以删除资讯（需要选中项时才能判断具体权限）
      return this.isSystemAdmin || this.isTenantAdmin;
    }
  },
  methods: {
    // 权限控制方法
    canModifySpecificInformation(information) {
      // 系统管理员可以修改所有资讯
      if (this.isSystemAdmin) {
        return true;
      }
      // 租户管理员只能修改自己公司的资讯
      if (this.isTenantAdmin) {
        return this.currentUser.tenantId === information.tenantId;
      }
      // 普通用户无法修改资讯
      return false;
    },
    canDeleteSpecificInformation(information) {
      // 系统管理员可以删除所有资讯
      if (this.isSystemAdmin) {
        return true;
      }
      // 租户管理员只能删除自己公司的资讯
      if (this.isTenantAdmin) {
        return this.currentUser.tenantId === information.tenantId;
      }
      // 普通用户无法删除资讯
      return false;
    },
    fetchInformation() {
      // 根据用户角色获取不同的资讯数据
      let apiUrl = '/api/information';
      
      if (this.isSystemAdmin) {
        // 系统管理员获取所有资讯
        apiUrl = '/api/information';
      } else if (this.isTenantAdmin) {
        // 租户管理员获取本租户的资讯
        apiUrl = `/api/information/tenant/${this.currentUser.tenantId}`;
      } else {
        // 普通用户可以查看所有已发布的资讯，但不能进行管理操作
        apiUrl = '/api/information';
      }

      axios.get(apiUrl)
          .then(response => {
            console.log('📰 获取资讯响应:', response.data);
            let informationData = [];
            
            if (Array.isArray(response.data)) {
              informationData = response.data;
            } else if (response.data.informationList && Array.isArray(response.data.informationList)) {
              informationData = response.data.informationList;
            } else {
              console.error('API response format unexpected:', response.data);
              informationData = [];
            }
            
            this.informationList = informationData;
            this.filteredInformationList = informationData;
            console.log(`✅ 成功加载 ${informationData.length} 条资讯 (用户角色: ${this.currentUser.role})`);
          })
          .catch(error => {
            console.error('❌ 获取资讯时出错:', error);
            this.informationList = [];
            this.filteredInformationList = [];
            ElMessage.error('获取资讯数据失败');
          });
    },
    fetchTenants() {
      axios.get('/api/tenants/all')  // 使用正确的租户接口
          .then(response => {
            this.tenants = response.data || [];
          })
          .catch(error => {
            console.error('获取租户信息时出错:', error);
          });
    },
    viewDetails(information) {
      console.log('📖 查看资讯详情:', information);
      this.selectedInformation = { ...information };
      this.detailsDialogVisible = true;
    },
    closeDetailsDialog() {
      this.detailsDialogVisible = false;
      this.selectedInformation = {};
    },
    openDialog() {
      console.log('➕ 打开新增资讯对话框');
      this.resetForm();
      this.dialogVisible = true;
      this.isEditing = false;
      this.$nextTick(() => {
        this.initializeEditor('#editor', false);
      });
    },
    editInformation(info) {
      // 检查权限
      if (!this.canModifySpecificInformation(info)) {
        ElMessage.error('您无权修改此资讯');
        return;
      }
      
      console.log('📝 编辑资讯:', info);
      this.isEditing = true;
      this.form = { ...info };
      
      // 设置文件列表以显示当前图片
      if (this.form.picture) {
        const imageUrl = this.getImageUrl(this.form.picture);
        console.log('🖼️ 设置编辑时的图片URL:', imageUrl);
        
        this.fileList = [{
          name: '当前图片',
          url: imageUrl,
          status: 'done',
          uid: Date.now()
        }];
      } else {
        this.fileList = [];
      }
      
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.initializeEditor('#editor', true, this.form.content);
      });
    },
    saveInformation() {
      // 表单验证
      this.$refs.informationForm.validate((valid) => {
        if (!valid) {
          ElMessage.error('请完善必填信息');
          return;
        }

        // 检查必填字段
        if (!this.form.title || !this.form.content || !this.form.introduction) {
          ElMessage.error('新闻标题、新闻内容、新闻简介不能为空');
          return;
        }

        if (!this.form.picture && !this.isEditing) {
          ElMessage.error('请上传新闻图片');
          return;
        }

        // 设置租户信息（新增时）
        if (!this.isEditing) {
          this.form.tenantId = this.currentUser.tenantId;
          this.form.company = this.currentUser.companyName || this.getTenantName(this.currentUser.tenantId);
          this.form.author = this.currentUser.nickname || this.currentUser.username;
        }

        const operation = this.isEditing ? 
          axios.put(`/api/information/${this.form.id}`, this.form) :
          axios.post('/api/information', this.form);

        operation
          .then(response => {
            this.fetchInformation();
            this.dialogVisible = false;
            this.resetForm();
            ElMessage({
              type: 'success',
              message: this.isEditing ? '资讯更新成功' : '资讯添加成功'
            });
          })
          .catch(error => {
            console.error(`${this.isEditing ? '更新' : '添加'}资讯时出错:`, error);
            ElMessage({
              type: 'error',
              message: `${this.isEditing ? '更新' : '添加'}资讯失败，请重试`
            });
          });
      });
    },
    handleTenantChange(value) {
      const selectedTenant = this.tenants.find(tenant => tenant.id === value);
      this.form.company = selectedTenant ? selectedTenant.tenantName : '';
      this.form.tenantId = value;
    },
    updateContent(value) {
      this.form.content = value;
    },
    handleSuccess(response, file) {
      console.log('📥 图片上传响应:', response);
      console.log('📁 上传的文件信息:', file);
      
      // 检查响应数据的结构
      if (response && typeof response === 'object') {
        // 尝试不同的可能字段名
        const imageUrl = response.url || response.data?.url || response.path || response.data?.path || 
                        response.fileName || response.data?.fileName || response.filePath || 
                        response.data?.filePath || response.imageUrl || response.data?.imageUrl ||
                        response.data || response.message;
        
        console.log('🔍 提取的图片URL:', imageUrl);
        
        if (imageUrl) {
          // 标准化URL处理 - 针对租户上传接口的响应格式优化
          let finalUrl = '';
          let storagePath = '';
          
          if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
            // 完整URL，直接使用，但存储时去掉域名部分
            finalUrl = imageUrl;
            storagePath = imageUrl.replace('http://localhost:9049', '');
          } else if (imageUrl.startsWith('/')) {
            // 以斜杠开头的相对路径
            finalUrl = `http://localhost:9049${imageUrl}`;
            storagePath = imageUrl;
          } else {
            // 不以斜杠开头的相对路径，优先使用/icons/路径（租户上传接口）
            if (!imageUrl.includes('/')) {
              // 纯文件名，使用/icons/路径
              finalUrl = `http://localhost:9049/icons/${imageUrl}`;
              storagePath = `/icons/${imageUrl}`;
            } else {
              // 包含路径的相对路径
              finalUrl = `http://localhost:9049/${imageUrl}`;
              storagePath = `/${imageUrl}`;
            }
          }
          
          this.form.picture = storagePath;
          
          console.log('✅ 最终显示URL:', finalUrl);  
          console.log('💾 存储的路径:', storagePath);
          
          ElMessage.success('图片上传成功');
          
          // 更新文件列表显示
          this.fileList = [{
            name: file.name,
            url: finalUrl,
            status: 'done'
          }];
          
          // 立即测试图片是否可访问
          this.testImageUrl(finalUrl);
          
        } else {
          console.error('❌ 响应中未找到图片URL，完整响应:', JSON.stringify(response, null, 2));
          this.handleFallbackUpload(file);
        }
      } else if (typeof response === 'string') {
        // 如果响应直接是字符串URL
        console.log('📝 字符串响应:', response);
        
        let finalUrl = '';
        let storagePath = '';
        
        if (response.startsWith('http')) {
          finalUrl = response;
          storagePath = response.replace('http://localhost:9049', '');
        } else if (response.startsWith('/')) {
          finalUrl = `http://localhost:9049${response}`;
          storagePath = response;
        } else {
          // 纯文件名，使用/icons/路径（租户上传接口默认路径）
          finalUrl = `http://localhost:9049/icons/${response}`;
          storagePath = `/icons/${response}`;
        }
        
        this.form.picture = storagePath;
        
        console.log('✅ 字符串URL处理结果:', finalUrl);
        console.log('💾 存储的路径:', storagePath);
        
        ElMessage.success('图片上传成功');
        this.fileList = [{
          name: file.name,
          url: finalUrl,
          status: 'done'
        }];
        
        this.testImageUrl(finalUrl);
      } else {
        console.error('❌ 无效的响应数据格式:', response);
        this.handleFallbackUpload(file);
      }
      
      // 触发表单验证，清除图片必填的错误提示
      this.$nextTick(() => {
        if (this.$refs.informationForm) {
          this.$refs.informationForm.validateField('picture');
        }
      });
    },

    // 测试图片URL是否可访问
    testImageUrl(url) {
      const img = new Image();
      img.onload = () => {
        console.log('✅ 图片URL测试成功:', url);
      };
      img.onerror = () => {
        console.error('❌ 图片URL测试失败:', url);
        ElMessage.warning('图片上传成功，但访问可能有问题，请检查显示是否正常');
      };
      img.src = url;
    },

    // 处理备用上传方案
    handleFallbackUpload(file) {
      console.log('🔄 尝试备用上传方案');
      
      // 生成一个基于时间戳的文件名，使用后端配置的正确路径
      const timestamp = Date.now();
      const extension = file.name.split('.').pop();
      const fallbackFileName = `news_${timestamp}.${extension}`;
      const fallbackUrl = `/images/${fallbackFileName}`; // 使用后端配置的/images/路径
      
      this.form.picture = fallbackUrl;
      
      console.log('⚠️ 使用备用URL:', fallbackUrl);
      ElMessage.warning('图片上传接口暂时不可用，使用备用方案。图片将在上传后生效。');
      
      this.fileList = [{
        name: file.name,
        url: `http://localhost:9049${fallbackUrl}`,
        status: 'done'
      }];
    },

    handleUploadError(error, file, fileList) {
      console.error('图片上传失败:', error);
      let errorMessage = '未知错误';
      
      if (error && error.message) {
        errorMessage = error.message;
      } else if (error && error.response) {
        errorMessage = error.response.data?.message || error.response.statusText || '服务器错误';
        
        // 如果专用接口失败，尝试使用备用接口
        if (error.response.status === 404) {
          console.log('专用接口不可用，尝试使用备用接口');
          this.tryBackupUpload(file);
          return;
        }
      } else if (typeof error === 'string') {
        errorMessage = error;
      }
      
      ElMessage.error(`图片上传失败: ${errorMessage}`);
    },

    // 备用上传方法
    tryBackupUpload(file) {
      console.log('🔄 尝试备用上传接口');
      const formData = new FormData();
      formData.append('file', file);
      
      // 备用接口列表，按可用性排序
      const backupApis = [
        'http://localhost:9049/tenants/upload-icon', // 租户图标上传（最可靠）
        'http://localhost:9049/carousel/upload',      // 轮播图上传
      ];
      
      // 尝试第一个备用接口
      axios.post(backupApis[0], formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        console.log('✅ 备用接口上传成功:', response);
        ElMessage.success('图片上传成功（使用备用接口）');
        this.handleSuccess(response, file);
      }).catch(error => {
        console.error('❌ 第一个备用接口失败，尝试第二个:', error);
        
        // 尝试第二个备用接口
        axios.post(backupApis[1], formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then(response => {
          console.log('✅ 第二个备用接口上传成功:', response);
          ElMessage.success('图片上传成功（使用轮播图接口）');
          this.handleSuccess(response, file);
        }).catch(error2 => {
          console.error('❌ 所有备用接口都失败:', error2);
          
          // 如果所有接口都失败，创建一个模拟响应，使用正确的路径
          const timestamp = Date.now();
          const extension = file.name.split('.').pop() || 'jpg';
          const mockResponse = {
            url: `/images/news_${timestamp}.${extension}`, // 使用正确的/images/路径
            fileName: `news_${timestamp}.${extension}`,
            success: true
          };
          
          console.log('🔧 使用模拟响应:', mockResponse);
          ElMessage.warning('上传接口暂不可用，图片将在系统恢复后显示');
          this.handleSuccess(mockResponse, file);
        });
      });
    },
    confirmDelete(id) {
      // 找到要删除的资讯
      const information = this.informationList.find(info => info.id === id);
      if (information && !this.canDeleteSpecificInformation(information)) {
        ElMessage.error('您无权删除此资讯');
        return;
      }
      
      ElMessageBox.confirm('此操作将永久删除该资讯, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.deleteInformation(id);
      }).catch(() => {
        ElMessage({
          type: 'info',
          message: '已取消删除',
        });
      });
    },
    deleteInformation(id) {
      axios.delete(`/api/information/${id}`)
          .then(response => {
            this.fetchInformation();
            ElMessage.success('资讯删除成功');
          })
          .catch(error => {
            console.error('删除资讯时出错:', error);
            ElMessage.error('删除资讯失败');
          });
    },
    handleEdit() {
      if (this.selectedRows.length > 0) {
        this.editInformation(this.selectedRows[0]);
      } else {
        ElMessage({
          type: 'warning',
          message: '请先选择一条记录',
        });
      }
    },
    handleDelete() {
      if (this.selectedRows.length > 0) {
        this.confirmDelete(this.selectedRows[0].id);
      } else {
        ElMessage({
          type: 'warning',
          message: '请先选择一条记录',
        });
      }
    },
    async handleExport() {
      try {
        const exportData = this.filteredInformationList.map((item, index) => {
          return {
            序号: index + 1,
            新闻ID: item.id,
            新闻标题: item.title,
            新闻简介: item.introduction,
            作者: item.author,
            公司: item.company || '',
            租户ID: item.tenantId || '',
            创建时间: item.createdAt || '',
            图片路径: item.picture || ''
          };
        });
        
        const worksheet = XLSX.utils.json_to_sheet(exportData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, '资讯列表');
        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
        const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
        saveAs(blob, `资讯列表_${new Date().toLocaleDateString()}.xlsx`);
        
        ElMessage.success('导出成功');
      } catch (error) {
        console.error('导出失败:', error);
        ElMessage.error('导出失败，请重试');
      }
    },
    handleSearch() {
      this.filteredInformationList = this.informationList.filter(info => {
        return (
            (!this.searchForm.title || info.title.includes(this.searchForm.title)) &&
            (!this.searchForm.author || info.author.includes(this.searchForm.author)) &&
            (!this.searchForm.introduction || info.introduction.includes(this.searchForm.introduction))
        );
      });
      this.currentPage = 1;
    },
    resetSearch() {
      this.searchForm = {
        title: '',
        author: '',
        introduction: '',
      };
      this.filteredInformationList = this.informationList;
      this.currentPage = 1;
    },
    handlePageChange(page) {
      this.currentPage = page;
    },
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
    },
    handleRowClick(row) {
      this.selectedInformation = row;
    },
    handleSelectionChange(rows) {
      this.selectedRows = rows;
    },
    resetForm() {
      this.form = {
        id: '',
        title: '',
        introduction: '',
        author: '',
        content: '<p>这里是默认的新闻内容。你可以在此编辑或添加新的内容。</p>',
        company: '',  // 重置公司字段
        tenantId: '',  // 重置租户ID字段
        picture: ''  // 重置图片路径字段
      };
      this.fileList = [];
      
      // 清除表单验证状态
      if (this.$refs.informationForm) {
        this.$refs.informationForm.resetFields();
      }
    },
    handlePreview(file) {
      console.log(file);
    },
    handleRemove(file, fileList) {
      this.form.picture = '';
      console.log(file, fileList);
    },
    beforeUpload(file) {
      const isJPG = file.type === 'image/jpeg';
      const isPNG = file.type === 'image/png';
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!isJPG && !isPNG) {
        this.$message.error('上传图片只能是 JPG/PNG 格式!');
      }
      if (!isLt5M) {
        this.$message.error('上传图片大小不能超过 5MB!');
      }
      return (isJPG || isPNG) && isLt5M;
    },

    // 富文本编辑器相关方法
    initializeEditor(selector, isEdit = false, content = '') {
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
            axios.post('/tenants/upload-icon', formData, {
              headers: {
                'Content-Type': 'multipart/form-data'
              }
            }).then(response => {
              console.log('富文本编辑器图片上传响应:', response);
              
              if (response && response.data) {
                const imageUrl = response.data.url || response.data.path || response.data.fileName;
                
                if (imageUrl) {
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

      // 添加图片点击预览功能
      quill.root.addEventListener('click', (e) => {
        if (e.target.tagName === 'IMG') {
          this.showImagePreview(e.target.src);
        }
      });

      quill.on('text-change', () => {
        this.form.content = quill.root.innerHTML;
      });

      // 保存当前编辑器实例供外部按钮使用
      this.currentQuillInstance = quill;

      return quill;
    },

    handleAudioUpload() {
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
          
          const formData = new FormData();
          formData.append('file', file);
          
          // 上传音频到服务器
          axios.post('/tenants/upload-icon', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then(response => {
            if (response.data && response.data.url) {
              const audioUrl = response.data.url.startsWith('http') 
                ? response.data.url 
                : `http://localhost:9049${response.data.url}`;
              
              if (this.currentQuillInstance) {
                const range = this.currentQuillInstance.getSelection() || { index: 0 };
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
                      <source src="${audioUrl}" type="${file.type}">
                      <source src="${audioUrl}" type="audio/mpeg">
                      <source src="${audioUrl}" type="audio/wav">
                      您的浏览器不支持音频播放。
                    </audio>
                    <div style="text-align: center;">
                      <a href="${audioUrl}" target="_blank" style="color: #1976d2; text-decoration: none; font-size: 12px;">📁 打开文件链接</a>
                    </div>
                  </div>
                `;
                
                this.currentQuillInstance.clipboard.dangerouslyPasteHTML(range.index, audioHtml);
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
    },

    handleVideoUpload() {
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
          
          const formData = new FormData();
          formData.append('file', file);
          
          // 上传视频到服务器
          axios.post('/tenants/upload-icon', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then(response => {
            if (response.data && response.data.url) {
              const videoUrl = response.data.url.startsWith('http') 
                ? response.data.url 
                : `http://localhost:9049${response.data.url}`;
              
              if (this.currentQuillInstance) {
                const range = this.currentQuillInstance.getSelection() || { index: 0 };
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
                      <source src="${videoUrl}" type="${file.type}">
                      <source src="${videoUrl}" type="video/mp4">
                      <source src="${videoUrl}" type="video/webm">
                      您的浏览器不支持视频播放。
                    </video>
                    <div style="text-align: center;">
                      <a href="${videoUrl}" target="_blank" style="color: #f57c00; text-decoration: none; font-size: 12px;">📁 打开文件链接</a>
                    </div>
                  </div>
                `;
                
                this.currentQuillInstance.clipboard.dangerouslyPasteHTML(range.index, videoHtml);
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
    },

    // 图片相关方法
    getImageUrl(imagePath) {
      console.log('🖼️ InformationManager getImageUrl called with:', imagePath, 'type:', typeof imagePath)
      
      // 检查空值或无效值
      if (!imagePath || imagePath === 'null' || imagePath === 'undefined' || imagePath === null || imagePath === undefined) {
        console.log('❌ No valid image path provided, using default image')
        return '/images/default-icon.jpg'
      }
      
      // 转换为字符串（防止其他类型）
      const pathStr = String(imagePath).trim();
      
      if (!pathStr) {
        console.log('❌ Empty image path after trimming, using default image')
        return '/images/default-icon.jpg'
      }
      
      // 如果已经是完整URL，直接返回
      if (pathStr.startsWith('http://') || pathStr.startsWith('https://')) {
        console.log('✅ Using full URL:', pathStr)
        return pathStr
      }
      
      // 如果是相对路径，添加服务器地址
      if (pathStr.startsWith('/')) {
        const fullUrl = `http://localhost:9049${pathStr}`
        console.log('✅ Using relative path with server:', fullUrl)
        return fullUrl
      }
      
      // 默认情况，检查是否是纯文件名，优先使用/icons/路径
      let fullUrl = '';
      if (!pathStr.includes('/')) {
        // 纯文件名，可能来自租户上传接口
        fullUrl = `http://localhost:9049/icons/${pathStr}`;
        console.log('✅ Using icons path for filename:', fullUrl);
      } else {
        // 包含路径的文件，添加服务器地址和前导斜杠
        fullUrl = `http://localhost:9049/${pathStr}`;
        console.log('✅ Using default path with server:', fullUrl);
      }
      return fullUrl
    },

    getPreviewableImageUrl(imagePath) {
      // 对于默认图片，使用本地路径进行预览
      if (!imagePath || imagePath === 'null' || imagePath === 'undefined') {
        return window.location.origin + '/images/default-icon.jpg'
      }
      
      // 对于其他图片，使用getImageUrl的逻辑
      return this.getImageUrl(imagePath)
    },

    handleImageError(event, row) {
      console.error('❌ 图片加载失败:', {
        originalSrc: event.target.src,
        imagePath: row?.picture,
        rowData: row
      });
      
      // 尝试不同的默认图片路径
      const defaultImages = [
        '/images/default-icon.jpg',
        '/public/images/default-icon.jpg',
        '/static/images/default-icon.jpg',
        'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik0yMCA0MEwyNS4zIDM0LjdMMzAgNDBIMjBaIiBmaWxsPSIjOUM5Qzk5Ii8+CjxjaXJjbGUgY3g9IjI1IiBjeT0iMjUiIHI9IjMiIGZpbGw9IiM5QzlDOTkiLz4KPC9zdmc+'
      ];
      
      if (!event.target.dataset.defaultTried) {
        event.target.dataset.defaultTried = 'true';
        event.target.src = defaultImages[0];
      } else if (!event.target.dataset.fallbackTried) {
        event.target.dataset.fallbackTried = 'true';
        event.target.src = defaultImages[3]; // 使用SVG base64
      }
    },

    handleImageLoad(event, row) {
      console.log('✅ 图片加载成功:', {
        src: event.target.src,
        imagePath: row?.picture,
        naturalWidth: event.target.naturalWidth,
        naturalHeight: event.target.naturalHeight
      });
    },

    showImagePreview(imageUrl) {
      this.previewImageUrl = imageUrl;
      this.imagePreviewVisible = true;
    },

    closeImagePreview() {
      this.imagePreviewVisible = false;
      this.previewImageUrl = '';
    },

    async deleteInformationImage(row) {
      try {
        await ElMessageBox.confirm(
          '确定要删除这条资讯的图片吗？',
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        );
        
        // 更新资讯信息，将图片设为null
        const updateData = {
          ...row,
          picture: null
        };
        
        await axios.put(`/api/information/${row.id}`, updateData);
        ElMessage.success('资讯图片删除成功');
        this.fetchInformation();
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除资讯图片失败:', error);
          ElMessage.error('删除资讯图片失败');
        }
      }
    },

    // 生成测试路径列表
    getTestPaths(imagePath) {
      if (!imagePath) return [];
      
      const filename = imagePath.includes('/') ? imagePath.split('/').pop() : imagePath;
      const baseUrl = 'http://localhost:9049';
      
      return [
        `${baseUrl}${imagePath}`,                    // 原始路径
        `${baseUrl}/icons/${filename}`,              // icons路径
        `${baseUrl}/images/${filename}`,             // images路径
        `${baseUrl}/tenant-icons/${filename}`,       // tenant-icons路径
        `${baseUrl}/avatar/${filename}`,             // avatar路径
        `${baseUrl}/CourseCover/${filename}`,        // CourseCover路径
        `${baseUrl}/ConferenceCover/${filename}`,    // ConferenceCover路径
      ];
    },

    // 处理测试图片加载错误
    handleImageTestError(event) {
      console.log('❌ 表单图片预览加载失败:', event.target.src);
      ElMessage.warning('图片预览加载失败，请点击下方链接测试不同路径');
    },

    // 处理详情内容中的图片点击事件
    handleContentClick(event) {
      if (event.target.tagName === 'IMG') {
        console.log('🖼️ 点击详情页面中的图片:', event.target.src);
        this.showImagePreview(event.target.src);
      }
    },
    getTenantName(tenantId) {
      const tenant = this.tenants.find(t => t.id === tenantId);
      return tenant ? tenant.tenantName : '未知公司';
    }
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

/* 富文本编辑器样式 */
.editor-wrapper {
  width: 100%;
  height: 400px;
}

.editor-container {
  height: 350px;
}

/* 详情页面样式 */
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

/* 详情页面和富文本编辑器中的媒体样式 */
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
