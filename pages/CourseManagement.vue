<template>
  <div class="container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-input v-model="searchForm.coursename" placeholder="课程名称"></el-input>
      </el-col>
      <el-col :span="6">
        <el-input v-model="searchForm.number" placeholder="课程排序"></el-input>
      </el-col>
      <el-col :span="6">
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col>
        <el-button type="primary" @click="openAddDialog">新增</el-button>
        <el-button type="warning" @click="openEditDialog" v-if="canModifyCourse">修改</el-button>
        <el-button type="danger" @click="handleDeleteConfirm" v-if="canModifyCourse">删除</el-button>
        <el-button type="success" @click="handleExport">导出</el-button>
      </el-col>
    </el-row>
    <el-table
      v-loading="loading"
      :data="paginatedCourses"
      style="width: 100%; margin-top: 20px;"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="courseID" label="课程ID" width="100"></el-table-column>
      <el-table-column prop="coursename" label="课程名称" width="180"></el-table-column>
      <el-table-column prop="courseintro" label="课程简介" width="200"></el-table-column>
      <el-table-column label="课程封面" width="120">
        <template v-slot="scope">
          <img 
            :src="scope.row.coverpath ? `http://localhost:9049${scope.row.coverpath}` : '/images/default-icon.jpg'" 
            alt="课程封面" 
            style="width: 80px; height: 60px; object-fit: cover; border-radius: 4px; cursor: pointer;"
            @error="event => event.target.src = '/images/default-icon.jpg'"
            @click="showImagePreview(scope.row.coverpath ? `http://localhost:9049${scope.row.coverpath}` : '/images/default-icon.jpg')"
            title="点击预览大图"
          >
        </template>
      </el-table-column>
      <el-table-column prop="number" label="课程排序" width="100"></el-table-column>
      <el-table-column prop="owner" label="课程作者" width="120"></el-table-column>
      <el-table-column label="审核状态" width="100">
        <template v-slot="scope">
          <el-tag 
            :type="getApprovalStatusType(scope.row.approvalStatus)" 
            size="small"
          >
            {{ getApprovalStatusLabel(scope.row.approvalStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="拒绝原因" width="150" show-overflow-tooltip>
        <template v-slot="scope">
          <span v-if="scope.row.approvalStatus === 'rejected' && scope.row.rejectionReason">
            {{ scope.row.rejectionReason }}
          </span>
          <span v-else style="color: #999;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="课程视频" width="120">
        <template v-slot="scope">
          <div style="display: flex; flex-direction: column; align-items: center;">
            <el-button 
              type="text" 
              @click="previewVideo(scope.row.videopath)"
              :disabled="!scope.row.videopath || scope.row.videopath === 'null' || scope.row.videopath === ''"
              style="color: #409EFF;"
            >
              {{ (scope.row.videopath && scope.row.videopath !== 'null' && scope.row.videopath !== '') ? '预览视频' : '无视频' }}
            </el-button>
            <span v-if="scope.row.videopath && scope.row.videopath !== 'null' && scope.row.videopath !== ''" 
                  style="font-size: 10px; color: #999; margin-top: 2px;">
              {{ scope.row.videopath.substring(scope.row.videopath.lastIndexOf('/') + 1) }}
            </span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template v-slot="scope">
          <div class="action-buttons">
            <el-button type="link" size="small" @click="openEditDialog(scope.row)" v-if="canModifySpecificCourse(scope.row)">修改</el-button>
            <el-button type="link" size="small" @click="handleDelete(scope.row)" v-if="canModifySpecificCourse(scope.row)">删除</el-button>
            <el-button 
              v-if="scope.row.approvalStatus === 'rejected' && canModifySpecificCourse(scope.row)" 
              type="link" 
              size="small" 
              style="color: #f56c6c;"
              @click="resubmitForApproval(scope.row)"
            >
              重新提交审核
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页组件 - 使用与用户管理页面一致的样式 -->
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

    <!-- 新增课程弹窗 -->
    <el-dialog v-model="addDialogVisible" title="新增课程" width="60%">
      <el-form :model="formData" :rules="rules" ref="courseForm" label-width="100px" class="dialog-form">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="课程名称" prop="coursename">
              <el-input v-model="formData.coursename" placeholder="请输入课程名称"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="课程封面" prop="coverpath">
              <el-upload
                action="http://localhost:9049/api/courses/upload-cover"
                list-type="picture-card"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :on-remove="handleRemove"
                :before-upload="beforeImageUpload"
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
            <el-form-item label="课程简介" prop="courseintro">
              <el-input v-model="formData.courseintro" type="textarea" placeholder="请输入课程简介"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程排序" prop="number">
              <el-input v-model="formData.number" placeholder="请输入课程排序"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程视频" prop="videopath">
              <el-upload
                :action="videoUploadUrl"
                :on-success="handleVideoUploadSuccess"
                :on-error="handleVideoUploadError"
                :on-remove="handleVideoRemove"
                :before-upload="beforeVideoUpload"
                :file-list="videoFileList"
                accept="video/*"
                :limit="1"
                :http-request="customVideoUpload">
                <el-button type="primary">上传视频文件</el-button>
                <div slot="tip" class="el-upload__tip">支持MP4、WebM、OGG等格式，大小不超过100MB</div>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程作者" prop="owner">
              <el-input v-model="formData.owner" placeholder="请输入课程作者"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="closeAddDialog">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 修改课程弹窗 -->
    <el-dialog v-model="editDialogVisible" title="修改课程" width="60%">
      <el-form :model="formData" :rules="rules" ref="editCourseForm" label-width="100px" class="dialog-form">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="课程名称" prop="coursename">
              <el-input v-model="formData.coursename" placeholder="请输入课程名称"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="课程封面" prop="coverpath">
              <el-upload
                action="http://localhost:9049/api/courses/upload-cover"
                list-type="picture-card"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :on-remove="handleRemove"
                :before-upload="beforeImageUpload"
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
            <el-form-item label="课程简介" prop="courseintro">
              <el-input v-model="formData.courseintro" type="textarea" placeholder="请输入课程简介"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程排序" prop="number">
              <el-input v-model="formData.number" placeholder="请输入课程排序"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程视频" prop="videopath">
              <el-upload
                :action="videoUploadUrl"
                :on-success="handleVideoUploadSuccess"
                :on-error="handleVideoUploadError"
                :on-remove="handleVideoRemove"
                :before-upload="beforeVideoUpload"
                :file-list="videoFileList"
                accept="video/*"
                :limit="1"
                :http-request="customVideoUpload">
                <el-button type="primary">上传视频文件</el-button>
                <div slot="tip" class="el-upload__tip">支持MP4、WebM、OGG等格式，大小不超过100MB</div>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程作者" prop="owner">
              <el-input v-model="formData.owner" placeholder="请输入课程作者"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="closeEditDialog">取消</el-button>
        <el-button type="primary" @click="submitEditForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog 
      v-model="imagePreviewVisible" 
      title="图片预览" 
      width="80%"
      class="image-preview-dialog"
      :close-on-click-modal="true"
      top="10vh"
      :z-index="2500"
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

    <!-- 视频预览对话框 - 修改为左右布局 -->
    <el-dialog 
      v-model="videoPreviewVisible" 
      title="视频预览 & AI智能问答" 
      width="90%"
      class="video-preview-dialog"
      :close-on-click-modal="false"
      top="15vh"
      :z-index="9999"
      :modal="false"
    >
      <div class="video-ai-container">
        <!-- 左侧视频区域 -->
        <div class="video-section">
          <div class="video-preview-container">
            <video 
              :src="previewVideoUrl" 
              controls
              style="width: 100%; max-height: 60vh; border-radius: 8px;"
              preload="metadata"
              @error="handleVideoError"
              @loadeddata="handleVideoLoaded"
            >
              您的浏览器不支持视频播放。
            </video>
          </div>
        </div>

        <!-- 右侧AI问答区域 -->
        <div class="ai-chat-section">
          <div class="ai-chat-header">
            <h3>AI智能问答</h3>
            <span class="ai-status" :class="{ 'online': aiConnected, 'offline': !aiConnected }">
              {{ aiConnected ? '在线' : '离线' }}
            </span>
          </div>
          
          <!-- 聊天消息区域 -->
          <div class="chat-messages" ref="chatMessagesRef">
            <div v-if="chatMessages.length === 0" class="welcome-message">
              <div class="ai-avatar">🤖</div>
              <div class="message-content">
                <p>你好！我是AI助手，可以帮你分析和解答关于这个课程视频的问题。</p>
                <p>💡 我会自动获取当前视频的网络地址，并基于视频内容为你提供帮助。</p>
                <p>你可以问我：</p>
                <ul>
                  <li>这个视频讲了什么主要内容？</li>
                  <li>视频中有哪些重点知识？</li>
                  <li>如何更好地学习这个视频内容？</li>
                  <li>请推荐相关的练习题</li>
                </ul>
              </div>
            </div>
            
            <div v-for="(message, index) in chatMessages" :key="index" class="message-item" :class="message.type">
              <div class="message-avatar">
                <span v-if="message.type === 'user'">👤</span>
                <span v-else>🤖</span>
              </div>
              <div class="message-content">
                <div class="message-text" v-html="formatMessage(message.content)"></div>
                <div class="message-time">{{ formatTime(message.timestamp) }}</div>
              </div>
            </div>
            
            <!-- AI正在思考 -->
            <div v-if="aiThinking" class="message-item ai">
              <div class="message-avatar">🤖</div>
              <div class="message-content">
                <div class="thinking-indicator">
                  <span class="dot"></span>
                  <span class="dot"></span>
                  <span class="dot"></span>
                  <span class="thinking-text">AI正在思考...</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 输入区域 -->
          <div class="chat-input-area">
            <div class="input-wrapper">
              <el-input
                v-model="currentMessage"
                type="textarea"
                :rows="2"
                placeholder="输入你的问题..."
                @keydown.enter.prevent="handleSendMessage"
                :disabled="aiThinking"
                class="chat-input"
              />
              <el-button 
                type="primary" 
                @click="handleSendMessage"
                :disabled="!currentMessage.trim() || aiThinking"
                class="send-button"
              >
                发送
              </el-button>
            </div>
            <div class="quick-questions">
              <span class="quick-label">快速提问：</span>
              <el-button 
                v-for="question in quickQuestions" 
                :key="question"
                size="small" 
                type="text"
                @click="sendQuickQuestion(question)"
                :disabled="aiThinking"
                class="quick-question-btn"
              >
                {{ question }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeVideoPreview">关闭</el-button>
          <el-button type="primary" @click="openVideoInNewTab">在新标签页打开视频</el-button>
          <el-button type="success" @click="clearChatHistory">清空对话</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from '../utils/request.js';
import { checkNetworkStatus, testCozeConnection } from '../utils/coze-ai.js';
import { ref, computed, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

export default {
  name: 'CourseManagement',
  setup() {
    const searchForm = ref({
      coursename: '',
      number: ''
    });
    const courses = ref([]);
    const selectedRow = ref(null);
    const loading = ref(false);
    const currentPage = ref(1);
    const pageSize = ref(20);
    const total = ref(0);
    const addDialogVisible = ref(false);
    const editDialogVisible = ref(false);
    const imagePreviewVisible = ref(false);
    const videoPreviewVisible = ref(false);
    const previewImageUrl = ref('');
    const previewVideoUrl = ref('');
    const formData = ref({
      coursename: '',
      coverpath: '',
      courseintro: '',
      number: '',
      videopath: '',
      owner: '',
      tenantID: null
    });
    const fileList = ref([]);
    const videoFileList = ref([]);
    
    // AI智能问答相关数据
    const aiConnected = ref(true);
    const aiThinking = ref(false);
    const currentMessage = ref('');
    const chatMessages = ref([]);
    const chatMessagesRef = ref(null);
    const quickQuestions = ref([
      '这个视频讲了什么主要内容？',
      '视频中有哪些重点知识？',
      '如何更好地学习这个视频内容？',
      '请推荐相关的练习题',
      '视频的难点在哪里？',
      '如何记忆视频中的知识点？'
    ]);

    // 检查AI连接状态
    const checkAIConnection = async () => {
      try {
        // 检查网络连接
        if (!checkNetworkStatus()) {
          aiConnected.value = false;
          console.log('🔌 网络连接断开');
          return;
        }

        // 测试API连接
        const result = await testCozeConnection();
        aiConnected.value = result.connected;
        console.log('🔗 AI连接状态:', result.message);
        
      } catch (error) {
        console.error('AI连接检测失败:', error);
        aiConnected.value = false;
      }
    };

    const rules = ref({
      coursename: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
      courseintro: [{ required: true, message: '请输入课程简介', trigger: 'blur' }],
      number: [{ required: true, message: '请输入课程排序', trigger: 'blur' }],
      owner: [{ required: true, message: '请输入课程作者', trigger: 'blur' }]
    });

    // 课程视频上传接口
    const videoUploadUrl = 'http://localhost:9049/api/courses/upload-video';

    // 权限控制 - 判断是否可以修改课程（全局）
    const canModifyCourse = computed(() => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
      // 只有管理员和租户管理员可以修改课程，普通用户不能修改
      return userInfo.role === 'Admin' || userInfo.role === 'TAdmin';
    });

    // 权限控制 - 判断是否可以修改特定课程
    const canModifySpecificCourse = (course) => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
      
      // 系统管理员可以修改所有课程
      if (userInfo.role === 'Admin') {
        return true;
      }
      
      // 租户管理员只能修改自己租户的课程
      if (userInfo.role === 'TAdmin') {
        return userInfo.tenantId === course.tenantID;
      }
      
      // 普通用户不能修改任何课程
      if (userInfo.role === 'User') {
        return false;
      }
      
      return false;
    };

    // 计算属性 - 实现前端分页
    const paginatedCourses = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return courses.value.slice(start, end)
    });

    const fetchCourses = async () => {
      loading.value = true;
      try {
        // 从localStorage获取用户信息
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
        const isAdmin = userInfo.role === 'Admin';
        const currentUserId = userInfo.id;
        const currentTenantId = userInfo.tenantId;
        
        console.log('👤 当前用户信息:', { 
          role: userInfo.role, 
          isAdmin, 
          userId: currentUserId,
          tenantId: currentTenantId 
        });
        
        let allCourses = [];
        
        if (isAdmin) {
          // 管理员：获取所有课程
          const response = await axios.get('http://localhost:9049/api/courses');
          allCourses = response.data.courses || [];
          console.log('📋 管理员用户，获取所有课程:', allCourses.length, '门');
        } else {
          try {
            // 1) 获取已审核通过的课程（所有人可见）
            const approvedResponse = await axios.get('http://localhost:9049/api/courses/status/approved');
            const approvedCourses = approvedResponse.data.courses || [];

            // 2) 获取自己创建的课程（所有状态）
            const allCoursesResponse = await axios.get('http://localhost:9049/api/courses');
            const userOwnCourses = (allCoursesResponse.data.courses || []).filter(course => 
              course.owner === userInfo.username || course.owner === userInfo.nickname
            );
            
            // 合并并去重（以courseID为准）
            const courseMap = new Map();
            
            // 先添加已审核通过的课程
            approvedCourses.forEach(course => {
              courseMap.set(course.courseID, course);
            });
            
            // 再添加用户自己的课程（会覆盖重复的）
            userOwnCourses.forEach(course => {
              courseMap.set(course.courseID, course);
            });
            
            allCourses = Array.from(courseMap.values());
            
            console.log('📋 普通用户课程统计:', {
              已审核通过: approvedCourses.length,
              用户自己的: userOwnCourses.length,
              合并后总数: allCourses.length
            });
          } catch (error) {
            console.error('获取普通用户课程失败:', error);
            const response = await axios.get('http://localhost:9049/api/courses/status/approved');
            allCourses = response.data.courses || [];
          }
        }
        
        console.log('📚 最终课程列表:', allCourses);
        
        // 检查每个课程的信息
        if (allCourses.length > 0) {
          allCourses.forEach((course, index) => {
            console.log(`📹 课程${index + 1} [${course.coursename}] 审核状态:`, course.approvalStatus, '创建者:', course.owner, '租户ID:', course.tenantID);
          });
        }
        
        courses.value = allCourses;
        total.value = allCourses.length;
      } catch (error) {
        console.error('获取课程数据失败:', error);
        ElMessage.error('获取课程数据失败');
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      fetchCourses();
      checkAIConnection();
    });

    const handleSearch = () => {
      const params = {
        coursename: searchForm.value.coursename,
        number: searchForm.value.number
      };
      axios.get('http://localhost:9049/api/courses/search', { params })
        .then(response => {
          courses.value = response.data.courses;
          total.value = response.data.total;
        })
        .catch(error => {
          console.error('Error searching data:', error);
          ElMessage.error('搜索失败');
        });
    };

    const handleReset = () => {
      searchForm.value.coursename = '';
      searchForm.value.number = '';
      fetchCourses();
    };

    const openAddDialog = () => {
      addDialogVisible.value = true;
      resetForm();

      // 获取当前登录用户的 TenantId
      const user = JSON.parse(localStorage.getItem('userInfo'));
      formData.value.tenantID = user ? user.tenantId : null;
    };

    const closeAddDialog = () => {
      addDialogVisible.value = false;
    };

    const openEditDialog = (row) => {
      const user = JSON.parse(localStorage.getItem('userInfo'));
      if (user && user.role === 'TAdmin' && user.tenantId !== row.tenantID) {
        ElMessage.error('您无权修改其他租户的课程');
        return;
      }
      if (row) {
        Object.assign(formData.value, row);
      } else if (selectedRow.value) {
        Object.assign(formData.value, selectedRow.value);
      } else {
        ElMessage.error('请先选择要修改的课程');
        return;
      }
      // 加载课程封面到 fileList
      if (formData.value.coverpath) {
        fileList.value = [
          {
            name: '课程封面',
            url: `http://localhost:9049${formData.value.coverpath}`
          }
        ];
      }
      // 加载课程视频到 videoFileList
      if (formData.value.videopath) {
        videoFileList.value = [
          {
            name: '课程视频',
            url: `http://localhost:9049${formData.value.videopath}`
          }
        ];
      }
      editDialogVisible.value = true;
    };

    const closeEditDialog = () => {
      editDialogVisible.value = false;
    };

    const handleUploadSuccess = (response, file, fileList) => {
      console.log('课程封面上传响应:', response);
      
      if (response && typeof response === 'object') {
        // 尝试多种可能的字段名
        const imageUrl = response.url || response.data?.url || response.path || response.data?.path || 
                        response.fileName || response.data?.fileName || response.filePath || 
                        response.data?.filePath || response.imageUrl || response.data?.imageUrl;
        
        if (imageUrl) {
          // 存储相对路径，不带域名
          if (imageUrl.startsWith('http')) {
            formData.value.coverpath = imageUrl.replace('http://localhost:9049', '');
          } else {
            formData.value.coverpath = imageUrl.startsWith('/') ? imageUrl : '/' + imageUrl;
          }
          
          console.log('存储的封面路径:', formData.value.coverpath);
          ElMessage.success('课程封面上传成功');
          
          // 更新文件列表显示
          fileList.value = [{
            name: file.name,
            url: `http://localhost:9049${formData.value.coverpath}`
          }];
        } else {
          console.error('响应中未找到图片URL，响应内容:', JSON.stringify(response, null, 2));
          ElMessage.error('课程封面上传失败：响应中未包含图片URL');
        }
      } else if (typeof response === 'string') {
        // 如果响应直接是字符串URL
        formData.value.coverpath = response.startsWith('http') ? response.replace('http://localhost:9049', '') : ('/' + response);
        ElMessage.success('课程封面上传成功');
        fileList.value = [{
          name: file.name,
          url: `http://localhost:9049${formData.value.coverpath}`
        }];
      } else {
        console.error('无效的响应数据格式:', response);
        ElMessage.error('课程封面上传失败：无效的响应数据格式');
      }
    };

    const handleUploadError = (error, file, fileList) => {
      console.error('课程封面上传失败:', error);
      ElMessage.error('课程封面上传失败');
    };

    const handleRemove = (file, fileList) => {
      formData.value.coverpath = '';
    };

    const handleVideoUploadSuccess = (response, file, fileList) => {
      console.log('📥 课程视频上传响应数据:', response);
      console.log('📊 响应数据类型:', typeof response);
      console.log('📋 响应完整内容:', JSON.stringify(response, null, 2));
      console.log('📁 上传文件信息:', file.name, '类型:', file.type, '大小:', (file.size / 1024 / 1024).toFixed(2) + 'MB');
      console.log('🔍 上传前formData.videopath:', formData.value.videopath);
      
      // 完全照搬租户管理的成功逻辑 - 检查响应数据的结构
      if (response && typeof response === 'object') {
        // 尝试不同的可能字段名 - 与租户管理完全相同
        const videoUrl = response.url || response.data?.url || response.path || response.data?.path || 
                        response.fileName || response.data?.fileName || response.filePath || 
                        response.data?.filePath || response.videoUrl || response.data?.videoUrl;
        
        console.log('🔗 提取的视频URL:', videoUrl);
        
        if (videoUrl) {
          // 存储相对路径，用于保存到数据库
          if (videoUrl.startsWith('http')) {
            // 如果是完整URL，提取相对路径部分
            const url = new URL(videoUrl);
            formData.value.videopath = url.pathname;
          } else {
            // 如果是相对路径，确保以/开头
            formData.value.videopath = videoUrl.startsWith('/') ? videoUrl : '/' + videoUrl;
          }
          
          console.log('💾 最终存储的视频路径:', formData.value.videopath);
          ElMessage.success('课程视频上传成功');
          
          // 更新文件列表显示
          videoFileList.value = [{
            name: file.name,
            url: `http://localhost:9049${formData.value.videopath}`
          }];
        } else {
          console.error('❌ 响应中未找到视频URL，响应内容:', JSON.stringify(response, null, 2));
          ElMessage.error('课程视频上传失败：响应中未包含视频URL');
        }
      } else if (typeof response === 'string') {
        // 如果响应直接是字符串URL
        if (response.startsWith('http')) {
          const url = new URL(response);
          formData.value.videopath = url.pathname;
        } else {
          formData.value.videopath = response.startsWith('/') ? response : '/' + response;
        }
        
        console.log('💾 字符串响应，最终存储路径:', formData.value.videopath);
        ElMessage.success('课程视频上传成功');
        
        videoFileList.value = [{
          name: file.name,
          url: `http://localhost:9049${formData.value.videopath}`
        }];
      } else {
        console.error('❌ 无效的响应数据格式:', response);
        ElMessage.error('课程视频上传失败：无效的响应数据格式');
      }
    };

    const handleVideoUploadError = (error, file, fileList) => {
      console.error('📹 课程视频上传失败详情:', error);
      
      let errorMessage = '课程视频上传失败';
      
      if (error && error.response) {
        console.error('🚨 服务器响应错误:', error.response);
        if (error.response.data && error.response.data.message) {
          errorMessage = error.response.data.message;
        } else if (error.response.status) {
          errorMessage = `服务器错误 ${error.response.status}`;
        }
      } else if (error && error.message) {
        errorMessage = error.message;
      }
      
      ElMessage.error(errorMessage);
    };

    const handleVideoRemove = (file, fileList) => {
      formData.value.videopath = '';
    };

    // 直接使用成功的租户上传接口
    const customVideoUpload = async (option) => {
      const { file, onProgress, onSuccess, onError } = option;
      
      console.log('🎬 开始视频上传，文件:', file.name);
      console.log('📁 文件大小:', (file.size / 1024 / 1024).toFixed(2), 'MB');

      try {
        const formData = new FormData();
        formData.append('file', file);

        const response = await axios.post(videoUploadUrl, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          onUploadProgress: (progressEvent) => {
            const percent = Math.round((progressEvent.loaded / progressEvent.total) * 100);
            onProgress({ percent });
          }
        });

        console.log('✅ 视频上传成功，响应:', response.data);
        
        // 上传成功，调用成功回调
        onSuccess(response.data, file);

      } catch (error) {
        console.error('❌ 视频上传失败:', error);
        onError(error);
        
        const errorMessage = error.response?.data?.message || error.message || '视频上传失败';
        ElMessage.error(`视频上传失败: ${errorMessage}`);
      }
    };

    const beforeImageUpload = (file) => {
      const isImage = file.type.startsWith('image/');
      if (!isImage) {
        ElMessage.error('只能上传图片文件！');
        return false;
      }
      
      const isLt10M = file.size / 1024 / 1024 < 10;
      if (!isLt10M) {
        ElMessage.error('图片大小不能超过10MB！');
        return false;
      }
      
      return true;
    };

    const beforeVideoUpload = (file) => {
      // 支持多种视频格式
      const supportedVideoTypes = ['video/mp4', 'video/webm', 'video/ogg', 'video/avi', 'video/mov'];
      const isVideoType = supportedVideoTypes.includes(file.type) || file.name.match(/\.(mp4|webm|ogg|avi|mov)$/i);
      
      if (!isVideoType) {
        ElMessage.error('请上传支持的视频格式：MP4、WebM、OGG、AVI、MOV');
        return false;
      }
      
      const isLt100M = file.size / 1024 / 1024 < 100;
      if (!isLt100M) {
        ElMessage.error('视频文件大小不能超过100MB！');
        return false;
      }
      
      console.log('📁 开始上传视频:', file.name, '类型:', file.type, '大小:', (file.size / 1024 / 1024).toFixed(2) + 'MB');
      
      // 对于非MP4格式，给出友好提示
      if (file.type !== 'video/mp4') {
        ElMessage.warning('为获得最佳兼容性，建议使用MP4格式的视频文件');
      }
      
      return true;
    };

    const submitForm = () => {
      if (!formData.value.coursename || !formData.value.courseintro || !formData.value.number || !formData.value.owner) {
        ElMessage.error('请填写所有必填项：课程名称、课程简介、课程排序、课程作者');
        return;
      }

      console.log('📤 准备提交课程数据:', formData.value);
      console.log('📹 提交的视频路径:', formData.value.videopath);
      console.log('🚨 视频路径是否为空:', !formData.value.videopath);
      console.log('🚨 视频路径长度:', formData.value.videopath ? formData.value.videopath.length : 0);

      axios.post('http://localhost:9049/api/courses', formData.value).then((response) => {
        console.log('✅ 课程创建成功响应:', response.data);
        ElMessage.success('课程创建成功，等待管理员审核');
        ElNotification({
          title: '课程创建成功',
          message: '您的课程已成功创建，目前处于待审核状态。您可以在课程管理列表中查看课程状态，管理员审核通过后，课程将对所有用户展示。',
          type: 'success',
          duration: 6000
        });
        closeAddDialog();
        fetchCourses(); // 刷新列表，用户将能看到自己刚创建的待审核课程
      }).catch(error => {
        console.error('课程创建失败:', error);
        ElMessage.error('课程创建失败: ' + (error.response?.data?.message || error.message));
      });
    };

    const submitEditForm = () => {
      if (!formData.value.coursename || !formData.value.courseintro || !formData.value.number || !formData.value.owner) {
        ElMessage.error('请填写所有必填项：课程名称、课程简介、课程排序、课程作者');
        return;
      }

      console.log('📝 准备修改课程数据:', formData.value);
      console.log('📹 修改的视频路径:', formData.value.videopath);

      axios.put(`http://localhost:9049/api/courses/${formData.value.courseID}`, formData.value).then((response) => {
        console.log('✅ 课程修改成功响应:', response.data);
        ElMessage.success('课程修改成功');
        closeEditDialog();
        fetchCourses();
      }).catch(error => {
        console.error('课程修改失败:', error);
        ElMessage.error('课程修改失败: ' + (error.response?.data?.message || error.message));
      });
    };

    const handleDeleteConfirm = () => {
      if (!selectedRow.value) {
        ElMessage.error('请先选择要删除的课程');
        return;
      }
      ElMessageBox.confirm('此操作将永久删除该课程, 是否继续?', '提示', {
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
      const user = JSON.parse(localStorage.getItem('userInfo'));
      if (user && user.role === 'TAdmin' && user.tenantId !== row.tenantID) {
        ElMessage.error('您无权删除其他租户的课程');
        return;
      }

      axios.delete(`http://localhost:9049/api/courses/${row.courseID}`)
        .then(() => {
          ElMessage.success('课程删除成功');
          fetchCourses();
        })
        .catch(error => {
          console.error('课程删除失败:', error);
          ElMessage.error('课程删除失败: ' + (error.response?.data?.message || error.message));
        });
    };

    const handleSelectionChange = (rows) => {
      selectedRow.value = rows.length ? rows[0] : null;
    };

    const handleSizeChange = (size) => {
      pageSize.value = size;
      currentPage.value = 1; // 重置到第一页
    };

    const handleCurrentChange = (page) => {
      currentPage.value = page;
    };

    const handleExport = async () => {
      try {
        const response = await axios.get('http://localhost:9049/api/courses');
        const exportData = response.data.courses.map((item) => {
          return {
            课程ID: item.courseID,
            课程名称: item.coursename,
            课程简介: item.courseintro,
            课程排序: item.number,
            课程作者: item.owner,
            课程封面: item.coverpath,
            课程视频: item.videopath,
            租户ID: item.tenantID
          };
        });
        const worksheet = XLSX.utils.json_to_sheet(exportData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, '课程列表');
        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
        const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
        saveAs(blob, '课程列表.xlsx');
        ElMessage.success('导出成功');
      } catch (error) {
        console.error('导出失败:', error);
        ElMessage.error('导出失败');
      }
    };

    const showImagePreview = (imageUrl) => {
      previewImageUrl.value = imageUrl;
      imagePreviewVisible.value = true;
    };

    const closeImagePreview = () => {
      imagePreviewVisible.value = false;
      previewImageUrl.value = '';
    };

    const previewVideo = (videoPath) => {
      if (videoPath) {
        console.log('🎬 开始视频预览，原始路径:', videoPath);
        
        // 生成多种可能的URL路径 - 基于租户上传接口实际保存位置
        const possibleUrls = [
          videoPath, // 原始路径
          videoPath.startsWith('http') ? videoPath : `http://localhost:9049${videoPath}`, // 带域名
          videoPath.replace('/icons/', '/uploads/'), // 可能的uploads路径
          videoPath.replace('/icons/', '/files/'), // 可能的files路径
          videoPath.replace('//icons/', '/icons/'), // 修复双斜杠
          `/api${videoPath}`, // API路径
          `http://localhost:9049/api/files${videoPath.replace('/icons', '')}`, // API文件服务
          `http://localhost:9049/static${videoPath}`, // 静态文件路径
          `http://localhost:9049/public${videoPath}`, // 公共文件路径
          // 兼容原Video目录
          videoPath.replace('/icons/', '/Video/'), // 如果是icons目录，尝试Video目录
          videoPath.replace('/Video/', '/icons/'), // 如果是Video目录，尝试icons目录
        ];

        console.log('🔍 尝试的所有视频URL:', possibleUrls);

        // 智能选择可访问的URL
        let foundAccessibleUrl = false;
        
        const tryNextUrl = (index) => {
          if (index >= possibleUrls.length) {
            if (!foundAccessibleUrl) {
              console.error('❌ 所有视频URL都不可访问');
              ElMessage.error('视频文件不存在或无法访问，请联系管理员检查服务器配置');
            }
            return;
          }

          const url = possibleUrls[index];
          console.log(`🔍 测试URL ${index + 1}:`, url);
          
          const testVideo = document.createElement('video');
          testVideo.preload = 'metadata';
          testVideo.muted = true;
          
          // 设置超时，避免长时间等待
          const timeout = setTimeout(() => {
            console.log(`⏰ URL ${index + 1} 测试超时`);
            testVideo.remove();
            tryNextUrl(index + 1);
          }, 3000);
          
          testVideo.onloadeddata = () => {
            if (!foundAccessibleUrl) {
              foundAccessibleUrl = true;
              clearTimeout(timeout);
              console.log(`✅ 找到可访问的视频URL (${index + 1}):`, url);
              
              // 使用找到的可访问URL
              previewVideoUrl.value = url;
              videoPreviewVisible.value = true;
              ElMessage.success('开始播放视频');
              
              // 确保弹窗位置正确
              ensureDialogPosition();
            }
            testVideo.remove();
          };
          
          testVideo.onerror = (error) => {
            clearTimeout(timeout);
            console.log(`❌ URL ${index + 1} 不可访问:`, url, error);
            testVideo.remove();
            tryNextUrl(index + 1);
          };
          
          testVideo.src = url;
        };

        // 开始尝试第一个URL
        tryNextUrl(0);

      } else {
        ElMessage.warning('该课程没有上传视频');
      }
    };

    // 重新提交审核
    const resubmitForApproval = (row) => {
      // 将审核状态重置为pending
      const updatedCourse = {
        ...row,
        approvalStatus: 'pending',
        rejectionReason: null
      };
      
      axios.put(`http://localhost:9049/api/courses/${row.courseID}`, updatedCourse).then(() => {
        ElMessage.success('课程已重新提交审核');
        fetchCourses();
      }).catch(error => {
        console.error('重新提交审核失败:', error);
        ElMessage.error('重新提交审核失败: ' + (error.response?.data?.message || error.message));
      });
    };

    // AI智能问答相关方法
    const formatMessage = (content) => {
      // 增强的文本格式化，支持换行、Markdown和基本HTML
      let formatted = content;
      
      // 处理Markdown格式的粗体
      formatted = formatted.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
      
      // 处理换行
      formatted = formatted.replace(/\n/g, '<br>');
      
      // 处理项目符号和编号
      formatted = formatted.replace(/^([一二三四五六七八九十]、)/gm, '<br><strong style="color: #409eff;">$1</strong>');
      
      // 处理emoji和特殊符号
      formatted = formatted.replace(/📹/g, '<span style="font-size: 1.2em;">📹</span>');
      formatted = formatted.replace(/💡/g, '<span style="font-size: 1.1em;">💡</span>');
      formatted = formatted.replace(/📋/g, '<span style="font-size: 1.1em;">📋</span>');
      
      // 处理关键概念高亮
      formatted = formatted.replace(/\*\*关键概念\*\*/g, '<strong style="color: #67c23a;">关键概念</strong>');
      
      // 处理法律条款
      formatted = formatted.replace(/《([^》]*?)》/g, '<em style="color: #e6a23c;">《$1》</em>');
      
      return formatted;
    };

    const formatTime = (timestamp) => {
      const date = new Date(timestamp);
      return date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    };

    const scrollToBottom = () => {
      nextTick(() => {
        const chatContainer = document.querySelector('.chat-messages');
        if (chatContainer) {
          chatContainer.scrollTop = chatContainer.scrollHeight;
        }
      });
    };

    const addMessage = (content, type = 'user') => {
      chatMessages.value.push({
        content,
        type,
        timestamp: Date.now()
      });
      scrollToBottom();
    };

    const sendQuickQuestion = (question) => {
      currentMessage.value = question;
      handleSendMessage();
    };

    const handleSendMessage = async () => {
      if (!currentMessage.value.trim() || aiThinking.value) return;

      const userMessage = currentMessage.value.trim();
      currentMessage.value = '';

      // 添加用户消息
      addMessage(userMessage, 'user');

      // 开始AI思考
      aiThinking.value = true;

      try {
        // 使用成功的Coze API调用模式
        const response = await callCozeAIDirect(userMessage);
        
        if (response.success) {
          // 添加AI回复
          addMessage(response.message, 'ai');
          console.log('✅ 使用Coze AI API回复');
        } else {
          // 添加失败时的回复
          addMessage(response.message, 'ai');
          console.log('⚠️ 使用备用回复');
        }
        
      } catch (error) {
        console.error('AI回复失败:', error);
        addMessage('抱歉，我现在无法回答您的问题，请稍后再试。', 'ai');
        ElMessage.error('AI服务暂时不可用');
      } finally {
        aiThinking.value = false;
      }
    };

    // 提取纯净答案内容的函数
    const extractPureAnswer = (rawMessage) => {
      try {
        // 方法1: 提取QA格式中的最后一个答案
        const qaPattern = /- \*\*问题\*\*：[^-]*?- \*\*答案\*\*：([^-]*?)(?=- \*\*问题\*\*|\{"msg_type"|$)/gs;
        const qaMatches = [...rawMessage.matchAll(qaPattern)];
        
        if (qaMatches.length > 0) {
          let lastAnswer = qaMatches[qaMatches.length - 1][1];
          // 清理答案中的技术信息
          lastAnswer = lastAnswer.replace(/\{"[^}]*\}/g, '').trim();
          if (lastAnswer.length > 20 && !lastAnswer.includes('from_module')) {
            return lastAnswer;
          }
        }
        
        // 方法2: 从tool_output_content中提取
        const toolOutputMatch = rawMessage.match(/"tool_output_content":"([^"]*?)"/);
        if (toolOutputMatch) {
          let toolContent = toolOutputMatch[1];
          // 解码转义字符
          toolContent = toolContent.replace(/\\n/g, '\n').replace(/\\"/g, '"');
          
          // 提取最后一个答案
          const answers = toolContent.match(/- \*\*答案\*\*：([^-]*?)(?=- \*\*问题\*\*|$)/gs);
          if (answers && answers.length > 0) {
            let lastAnswer = answers[answers.length - 1].replace(/- \*\*答案\*\*：/, '').trim();
            if (lastAnswer.length > 20) {
              return lastAnswer;
            }
          }
        }
        
        // 方法3: 直接查找中文内容段落
        const chineseTextPattern = /([^{]*[\u4e00-\u9fa5][^{]*?)(?=\{|$)/g;
        const chineseMatches = [...rawMessage.matchAll(chineseTextPattern)];
        
        if (chineseMatches.length > 0) {
          // 找到最长的中文段落
          let longestText = '';
          for (const match of chineseMatches) {
            const text = match[1].trim();
            if (text.length > longestText.length && 
                text.length > 50 && 
                !text.includes('plugin') && 
                !text.includes('from_module')) {
              longestText = text;
            }
          }
          if (longestText) {
            return longestText;
          }
        }
        
        return null;
      } catch (error) {
        console.error('❌ 提取答案失败:', error);
        return null;
      }
    };

    // 清理AI响应内容的函数
    const cleanAIResponse = (rawMessage) => {
      let cleanedMessage = rawMessage;
      
      try {
        console.log('🧹 开始清理原始消息:', rawMessage.substring(0, 200) + '...');
        
        // 1. 移除所有JSON格式的插件调用和元数据
        cleanedMessage = cleanedMessage.replace(/\{"name":"[^"]*"[^}]*\}/g, '');
        cleanedMessage = cleanedMessage.replace(/\{"x_aiplugin_extra_info"[^}]*\}/g, '');
        cleanedMessage = cleanedMessage.replace(/\{"msg_type"[^}]*\}/g, '');
        cleanedMessage = cleanedMessage.replace(/\{"output"[^}]*\}/g, '');
        cleanedMessage = cleanedMessage.replace(/\{"uuid"[^}]*\}/g, '');
        cleanedMessage = cleanedMessage.replace(/\{"from_module"[^}]*\}/g, '');
        cleanedMessage = cleanedMessage.replace(/\{"from_unit"[^}]*\}/g, '');
        
        // 2. 移除插件标识和技术信息
        cleanedMessage = cleanedMessage.replace(/qa directly streaming reply\./g, '');
        cleanedMessage = cleanedMessage.replace(/"from_module":null,"from_unit":null/g, '');
        cleanedMessage = cleanedMessage.replace(/from_module.*?from_unit.*?null/g, '');
        
        // 3. 智能提取纯净答案内容
        const extractedAnswer = extractPureAnswer(rawMessage);
        if (extractedAnswer) {
          cleanedMessage = extractedAnswer;
          console.log('✅ 提取到纯净答案:', cleanedMessage.substring(0, 100) + '...');
        }
        
        // 5. 强力清理剩余的JSON和技术信息
        // 移除任何包含from_module的文本
        cleanedMessage = cleanedMessage.replace(/[^。！？]*from_module[^。！？]*[。！？]*/g, '');
        
        // 移除任何剩余的JSON格式文本
        cleanedMessage = cleanedMessage.replace(/\{[^}]*\}/g, '');
        
        // 移除包含技术关键词的句子
        const techKeywords = ['plugin', 'uuid', 'msg_type', 'from_module', 'from_unit', 'streaming', 'tool_output'];
        techKeywords.forEach(keyword => {
          const regex = new RegExp(`[^。！？]*${keyword}[^。！？]*[。！？]*`, 'gi');
          cleanedMessage = cleanedMessage.replace(regex, '');
        });
        
        // 如果还是包含很多技术信息，进行最后清理
        if (cleanedMessage.includes('{"') || cleanedMessage.includes('plugin')) {
          // 尝试找到最后一个有意义的文本段落
          const textParts = cleanedMessage.split(/\{[^}]*\}/).filter(part => 
            part.trim().length > 20 && 
            !part.includes('plugin') && 
            !part.includes('msg_type') &&
            !part.includes('uuid') &&
            !part.includes('from_module')
          );
          
          if (textParts.length > 0) {
            cleanedMessage = textParts[textParts.length - 1].trim();
          }
        }
        
        // 6. 移除重复的段落
        const paragraphs = cleanedMessage.split(/\n\s*\n/).filter(p => p.trim().length > 0);
        const uniqueParagraphs = [];
        const seen = new Set();
        
        for (const paragraph of paragraphs) {
          const normalized = paragraph.replace(/\s+/g, ' ').trim();
          if (!seen.has(normalized) && normalized.length > 10) {
            seen.add(normalized);
            uniqueParagraphs.push(paragraph.trim());
          }
        }
        
        cleanedMessage = uniqueParagraphs.join('\n\n');
        
        // 7. 最终清理和格式化
        cleanedMessage = cleanedMessage.replace(/\s+/g, ' ').trim();
        cleanedMessage = formatAIContent(cleanedMessage);
        
        console.log('🔧 内容清理完成:', {
          原始长度: rawMessage.length,
          清理后长度: cleanedMessage.length,
          减少比例: Math.round((1 - cleanedMessage.length / rawMessage.length) * 100) + '%',
          清理后预览: cleanedMessage.substring(0, 100) + '...'
        });
        
        return cleanedMessage;
        
      } catch (error) {
        console.error('❌ 清理AI响应时出错:', error);
        // 如果清理失败，返回基本清理的版本
        return rawMessage.replace(/\{[^}]*\}/g, '').replace(/qa directly streaming reply\./g, '').trim();
      }
    };

    // 格式化AI内容，增加可读性
    const formatAIContent = (content) => {
      let formatted = content;
      
      // 移除QA格式标记，只保留答案内容
      formatted = formatted.replace(/- \*\*问题\*\*：[^-]*?- \*\*答案\*\*：/g, '');
      
      // 如果内容包含"视频内容总结："，进行特殊格式化
      if (formatted.includes('视频内容总结：')) {
        formatted = formatted.replace('视频内容总结：', '📹 **视频内容总结**\n\n');
      }
      
      // 格式化法律条文或要点
      formatted = formatted.replace(/([一二三四五六七八九十])是/g, '\n\n**$1、**');
      formatted = formatted.replace(/([一二三四五六七八九十])、/g, '\n\n**$1、**');
      
      // 处理"这里的"开头的解释段落
      formatted = formatted.replace(/这里的"([^"]*?)"/g, '\n\n💡 **关键概念**："$1"');
      
      // 处理法律条款引用
      formatted = formatted.replace(/《([^》]*?)》/g, '📋 《$1》');
      
      // 添加适当的换行和格式
      formatted = formatted.replace(/。(?=[一二三四五六七八九十])/g, '。\n\n');
      formatted = formatted.replace(/；/g, '；\n');
      
      // 处理段落分隔
      formatted = formatted.replace(/。\s*([A-Z]|[一二三四五六七八九十])/g, '。\n\n$1');
      
      // 清理多余的换行
      formatted = formatted.replace(/\n{3,}/g, '\n\n');
      formatted = formatted.replace(/^\s+|\s+$/g, '');
      
      return formatted;
    };

    // 直接调用Coze API的方法（参考成功的UserProfile代码）
    const callCozeAIDirect = async (message) => {
      const COZE_API_KEY = 'Bearer pat_XFJxVX7d9HlJ0YXaaDs4HRHr0v1CKAZq3MFshjAVY2jRKTjfkAzr9PgX31FUMe4X';
      const COZE_BOT_ID = '7522836555506614287';
      const COZE_API_URL = 'https://api.coze.cn/v3/chat';

      try {
        // 构建包含视频信息的完整消息
        const videoInfo = previewVideoUrl.value ? `视频网络地址为：${previewVideoUrl.value}` : '当前没有视频';
        const fullMessage = `${videoInfo}，我的问题或要求是：${message}`;

        const requestBody = {
          bot_id: COZE_BOT_ID,
          user_id: `user_${Date.now()}`,
          stream: true,
          additional_messages: [{
            role: "user",
            content: fullMessage,
            content_type: "text"
          }]
        };

        console.log('🤖 发送Coze API请求 - 用户问题:', message);
        console.log('🎬 当前视频地址:', previewVideoUrl.value);
        console.log('📝 完整发送内容:', fullMessage);

        const response = await fetch(COZE_API_URL, {
          method: 'POST',
          headers: {
            'Authorization': COZE_API_KEY,
            'Content-Type': 'application/json',
            'Accept': 'text/event-stream'
          },
          body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          throw new Error(errorData.msg || `API 请求失败: ${response.status}`);
        }

        const reader = response.body?.getReader();
        if (!reader) throw new Error('无法获取响应读取器');

        const decoder = new TextDecoder();
        let finalMessage = '';
        let hasContent = false;

        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          const chunk = decoder.decode(value, { stream: true });
          const lines = chunk.split('\n');

          for (const line of lines) {
            if (line.startsWith('data:')) {
              const dataStr = line.substring(5).trim();
              if (dataStr === '[DONE]') continue;

              try {
                const data = JSON.parse(dataStr);
                
                // 处理不同类型的响应数据
                if (data.type === 'answer' && data.content) {
                  // 只收集answer类型的内容，避免收集技术信息
                  const cleanContent = data.content.replace(/\{"[^"]*":[^}]*\}/g, '').trim();
                  if (cleanContent && !cleanContent.includes('from_module') && !cleanContent.includes('plugin')) {
                    finalMessage += cleanContent;
                    hasContent = true;
                  }
                } else if (data.type === 'tool_response' && data.content && !data.content.includes('from_module')) {
                  // 过滤掉包含技术信息的tool_response
                  const cleanContent = data.content.replace(/\{"[^"]*":[^}]*\}/g, '').trim();
                  if (cleanContent && cleanContent.length > 10) {
                    finalMessage += cleanContent;
                    hasContent = true;
                  }
                }
              } catch (e) {
                // 忽略解析错误的行
              }
            }
          }
        }

        if (hasContent && finalMessage.trim()) {
          console.log('✅ Coze API调用成功，收到原始回复:', finalMessage);
          
          // 清理和格式化AI响应内容
          const cleanedMessage = cleanAIResponse(finalMessage.trim());
          console.log('🧹 清理后的回复:', cleanedMessage);
          
          return {
            success: true,
            message: cleanedMessage,
            source: 'coze_api'
          };
        } else {
          throw new Error('未能从AI响应中获取有效内容');
        }

      } catch (error) {
        console.error('❌ Coze API调用失败:', error);
        
        // 返回友好的错误回复
        return {
          success: false,
          message: '抱歉，我现在无法回答您的问题。可能是网络连接问题，请稍后再试。如果问题持续，请联系管理员。',
          source: 'error'
        };
      }
    };

    const clearChatHistory = () => {
      chatMessages.value = [];
      ElMessage.success('对话历史已清空');
    };

    const closeVideoPreview = () => {
      videoPreviewVisible.value = false;
      previewVideoUrl.value = '';
      // 可选：清空聊天记录
      // chatMessages.value = [];
    };

    const handleVideoError = (event) => {
      console.error('视频加载失败:', event);
      // 不显示错误提示，因为智能URL查找会处理这个问题
      // 如果所有URL都失败，会在previewVideo函数中显示统一的错误消息
    };

    const handleVideoLoaded = (event) => {
      console.log('视频加载成功:', event);
      console.log('🎬 视频地址已准备就绪:', previewVideoUrl.value);
      
      // 可以在这里添加一个提示，告知用户AI已经获取到视频信息
      if (chatMessages.value.length === 0) {
        // 只在没有聊天记录时显示提示
        ElMessage.success('视频加载成功！AI助手已获取视频信息，可以开始提问了。');
      }
    };

    const openVideoInNewTab = () => {
      if (previewVideoUrl.value) {
        window.open(previewVideoUrl.value, '_blank');
      }
    };

    // 确保弹窗显示在正确位置
    const ensureDialogPosition = () => {
      nextTick(() => {
        const dialogElement = document.querySelector('.video-preview-dialog .el-dialog');
        if (dialogElement) {
          dialogElement.style.position = 'fixed';
          dialogElement.style.top = '15vh';
          dialogElement.style.zIndex = '10000';
          dialogElement.style.marginTop = '0';
        }
      });
    };

    const resetForm = () => {
      formData.value = {
        coursename: '',
        coverpath: '',
        courseintro: '',
        number: '',
        videopath: '',
        owner: '',
        tenantID: null
      };
      fileList.value = [];
      videoFileList.value = [];
    };

    // 审核状态处理函数
    const getApprovalStatusLabel = (status) => {
      const statusMap = {
        'pending': '待审核',
        'approved': '已通过',
        'rejected': '已拒绝'
      };
      return statusMap[status] || '未知';
    };

    const getApprovalStatusType = (status) => {
      const typeMap = {
        'pending': 'warning',
        'approved': 'success', 
        'rejected': 'danger'
      };
      return typeMap[status] || 'info';
    };

    return {
      searchForm,
      courses,
      paginatedCourses,
      selectedRow,
      loading,
      currentPage,
      pageSize,
      total,
      addDialogVisible,
      editDialogVisible,
      imagePreviewVisible,
      videoPreviewVisible,
      previewImageUrl,
      previewVideoUrl,
      formData,
      fileList,
      videoFileList,
      rules,
      videoUploadUrl,
      canModifyCourse,
      canModifySpecificCourse,
      customVideoUpload,
      handleSearch,
      handleReset,
      openAddDialog,
      closeAddDialog,
      openEditDialog,
      closeEditDialog,
      handleUploadSuccess,
      handleUploadError,
      handleRemove,
      handleVideoUploadSuccess,
      handleVideoUploadError,
      handleVideoRemove,
      beforeImageUpload,
      beforeVideoUpload,
      submitForm,
      submitEditForm,
      handleDeleteConfirm,
      handleDelete,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      handleExport,
      showImagePreview,
      closeImagePreview,
      previewVideo,
      closeVideoPreview,
      handleVideoError,
      handleVideoLoaded,
      openVideoInNewTab,
      ensureDialogPosition,
      getApprovalStatusLabel,
      getApprovalStatusType,
      resubmitForApproval,
      formatMessage,
      formatTime,
      scrollToBottom,
      addMessage,
      sendQuickQuestion,
      handleSendMessage,
      callCozeAIDirect,
      extractPureAnswer,
      cleanAIResponse,
      formatAIContent,
      clearChatHistory,
      checkAIConnection,
      aiConnected,
      aiThinking,
      currentMessage,
      chatMessages,
      chatMessagesRef,
      quickQuestions
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

/* 全局弹窗层级设置 */
.el-overlay {
  z-index: 9998 !important;
}

.el-dialog {
  z-index: 9999 !important;
}

.video-preview-dialog .el-dialog {
  height: 1000px;
  z-index: 10000 !important;
}

.container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 25% 75%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 75% 25%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
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

.el-row {
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 5px;
}

.dialog-form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.image-preview-dialog .el-dialog__body,
.video-preview-dialog .el-dialog__body {
  padding: 20px;
  text-align: center;
}

/* 确保弹窗不被导航栏遮住 */
:deep(.video-preview-dialog) {
  z-index: 9999 !important;
}

:deep(.video-preview-dialog .el-overlay) {
  z-index: 9998 !important;
}

:deep(.video-preview-dialog .el-dialog) {
  position: fixed !important;
  top: 15vh !important;
  margin-top: 0 !important;
  max-height: 75vh !important;
  overflow: hidden;
  z-index: 9999 !important;
}

:deep(.video-preview-dialog .el-dialog__header) {
  position: relative;
  z-index: 10000 !important;
}

:deep(.video-preview-dialog .el-dialog__body) {
  max-height: calc(75vh - 120px);
  overflow-y: auto;
  padding: 15px;
}

:deep(.image-preview-dialog) {
  z-index: 8888 !important;
}

:deep(.image-preview-dialog .el-dialog) {
  margin-top: 10vh !important;
  max-height: 80vh;
}

.image-preview-container,
.video-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.el-upload__tip {
  color: #606266;
  font-size: 12px;
  margin-top: 7px;
}

/* 分页样式 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* Element Plus 组件样式覆盖 */
:deep(.el-pagination) {
  margin-top: 20px;
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

/* AI智能问答样式 */
.video-ai-container {
  display: flex;
  gap: 20px;
  height: 55vh;
  min-height: 450px;
  max-height: calc(75vh - 150px);
}

.video-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.ai-chat-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fff;
}

.ai-chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #f8f9fa;
  border-radius: 8px 8px 0 0;
}

.ai-chat-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 16px;
}

.ai-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.ai-status.online {
  background: #e8f5e8;
  color: #67c23a;
}

.ai-status.offline {
  background: #fef0f0;
  color: #f56c6c;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background: #fafafa;
}

.welcome-message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.welcome-message .ai-avatar {
  font-size: 24px;
  margin-right: 10px;
  margin-top: 5px;
}

.welcome-message .message-content {
  background: #fff;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  max-width: 80%;
}

.welcome-message .message-content p {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.welcome-message .message-content ul {
  margin: 10px 0 0 0;
  padding-left: 20px;
}

.welcome-message .message-content li {
  margin: 5px 0;
  color: #606266;
}

.message-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
}

.message-item.user {
  justify-content: flex-end;
}

.message-item.user .message-content {
  background: #409eff;
  color: white;
  border-radius: 12px 12px 4px 12px;
}

.message-item.ai .message-content {
  background: #fff;
  color: #2c3e50;
  border-radius: 12px 12px 12px 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  margin: 0 8px;
  flex-shrink: 0;
}

.message-item.user .message-avatar {
  order: 1;
  background: #409eff;
  color: white;
}

.message-item.ai .message-avatar {
  background: #f0f0f0;
}

.message-content {
  max-width: 70%;
  padding: 10px 15px;
  position: relative;
}

.message-text {
  line-height: 1.6;
  word-wrap: break-word;
}

.message-text strong {
  font-weight: 600;
  color: #2c3e50;
}

.message-item.ai .message-text strong {
  color: #409eff;
}

.message-text br + strong {
  margin-top: 8px;
  display: inline-block;
}

.message-text em {
  font-style: normal;
  font-weight: 500;
}

.message-text span {
  vertical-align: middle;
}

.message-time {
  font-size: 11px;
  color: rgba(255,255,255,0.7);
  margin-top: 5px;
}

.message-item.ai .message-time {
  color: #999;
}

.thinking-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
}

.thinking-indicator .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #409eff;
  animation: thinking 1.4s infinite ease-in-out;
}

.thinking-indicator .dot:nth-child(1) {
  animation-delay: -0.32s;
}

.thinking-indicator .dot:nth-child(2) {
  animation-delay: -0.16s;
}

.thinking-text {
  color: #666;
  font-size: 14px;
}

@keyframes thinking {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.chat-input-area {
  border-top: 1px solid #e4e7ed;
  padding: 15px;
  background: #fff;
  border-radius: 0 0 8px 8px;
}

.input-wrapper {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.chat-input {
  flex: 1;
}

.send-button {
  height: 36px;
  padding: 0 20px;
}

.quick-questions {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.quick-label {
  font-size: 12px;
  color: #909399;
  margin-right: 5px;
}

.quick-question-btn {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 12px;
  background: #f0f2f5;
  color: #606266;
  border: none;
  transition: all 0.3s;
}

.quick-question-btn:hover {
  background: #409eff;
  color: white;
}

.quick-question-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .video-ai-container {
    flex-direction: column;
    height: auto;
    min-height: 600px;
  }
  
  .video-section,
  .ai-chat-section {
    flex: none;
  }
  
  .ai-chat-section {
    height: 400px;
  }
  
  :deep(.video-preview-dialog .el-dialog) {
    top: 10vh !important;
    max-height: 85vh !important;
  }
  
  :deep(.video-preview-dialog .el-dialog__body) {
    max-height: calc(85vh - 120px);
  }
  
  .video-ai-container {
    height: 60vh;
    min-height: 400px;
  }
}
</style>
