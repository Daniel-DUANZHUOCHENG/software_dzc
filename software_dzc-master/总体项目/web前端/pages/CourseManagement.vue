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
      :data="courses"
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

    <!-- 视频预览对话框 -->
    <el-dialog 
      v-model="videoPreviewVisible" 
      title="视频预览" 
      width="80%"
      class="video-preview-dialog"
      :close-on-click-modal="true"
    >
      <div class="video-preview-container">
        <video 
          :src="previewVideoUrl" 
          controls
          style="width: 100%; max-height: 70vh;"
          preload="metadata"
          @error="handleVideoError"
          @loadeddata="handleVideoLoaded"
        >
          您的浏览器不支持视频播放。
        </video>
        <div v-if="previewVideoUrl" style="margin-top: 10px; text-align: center; color: #666; font-size: 12px;">
          视频地址: {{ previewVideoUrl }}
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeVideoPreview">关闭</el-button>
          <el-button type="primary" @click="openVideoInNewTab">在新标签页打开</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from '../utils/request.js';
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
    const pageSize = ref(6);
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

    onMounted(fetchCourses);

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
      fetchCourses();
    };

    const handleCurrentChange = (page) => {
      currentPage.value = page;
      fetchCourses();
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

    const closeVideoPreview = () => {
      videoPreviewVisible.value = false;
      previewVideoUrl.value = '';
    };

    const handleVideoError = (event) => {
      console.error('视频加载失败:', event);
      // 不显示错误提示，因为智能URL查找会处理这个问题
      // 如果所有URL都失败，会在previewVideo函数中显示统一的错误消息
    };

    const handleVideoLoaded = (event) => {
      console.log('视频加载成功:', event);
    };

    const openVideoInNewTab = () => {
      if (previewVideoUrl.value) {
        window.open(previewVideoUrl.value, '_blank');
      }
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

    return {
      searchForm,
      courses,
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
      getApprovalStatusLabel,
      getApprovalStatusType,
      resubmitForApproval
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
</style>
