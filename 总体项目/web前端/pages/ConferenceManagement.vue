<template>
  <div class="container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-input v-model="searchForm.conferencename" placeholder="会议名称"></el-input>
      </el-col>
      <el-col :span="6">
        <el-input v-model="searchForm.creator" placeholder="创建人"></el-input>
      </el-col>
      <el-col :span="6">
        <el-date-picker
          v-model="searchForm.starttime"
          type="date"
          placeholder="开始时间"
        ></el-date-picker>
      </el-col>
      <el-col :span="6">
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col>
        <!-- 只有管理员和租户管理员可以看到新增修改删除按钮 -->
        <el-button v-if="canManageMeeting" type="primary" @click="openAddDialog">新增</el-button>
        <el-button v-if="canManageMeeting" type="warning" @click="openEditDialog" :disabled="!selectedRow">修改</el-button>
        <el-button v-if="canManageMeeting" type="danger" @click="handleDeleteConfirm" :disabled="!selectedRow">删除</el-button>
        <!-- 导出按钮对所有用户可见 -->
        <el-button type="success" @click="handleExport">导出</el-button>
      </el-col>
    </el-row>
    <el-table
      v-loading="loading"
      :data="meetings"
      style="width: 100%; margin-top: 20px;"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="conferencename" label="会议名称" width="160"></el-table-column>
      <el-table-column prop="creator" label="创建人" width="120"></el-table-column>
      <el-table-column label="会议状态" width="120">
        <template v-slot="scope">
          <el-tag :type="getStatusType(scope.row)">
            {{ getMeetingStatus(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="会议封面" width="120">
        <template v-slot="scope">
          <img 
            :src="scope.row.coverpath ? `http://localhost:9049${scope.row.coverpath}` : '/images/default-icon.jpg'" 
            alt="会议封面" 
            style="width: 80px; height: 60px; object-fit: cover; border-radius: 4px; cursor: pointer;"
            @error="event => event.target.src = '/images/default-icon.jpg'"
            @click="showImagePreview(scope.row.coverpath ? `http://localhost:9049${scope.row.coverpath}` : '/images/default-icon.jpg')"
            title="点击预览大图"
          >
        </template>
      </el-table-column>
      <el-table-column prop="starttime" label="开始时间" width="150">
        <template v-slot="scope">
          {{ formatDateTime(scope.row.starttime) }}
        </template>
      </el-table-column>
      <el-table-column prop="endtime" label="结束时间" width="150">
        <template v-slot="scope">
          {{ formatDateTime(scope.row.endtime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template v-slot="scope">
          <div class="action-buttons">
            <el-button type="link" size="small" @click="openDetailsDialog(scope.row)">详情</el-button>
            <!-- 只有管理员和租户管理员可以修改删除，且只能操作自己租户的会议 -->
            <el-button 
              v-if="canModifyMeeting(scope.row)" 
              type="link" 
              size="small" 
              @click="openEditDialog(scope.row)"
            >
              修改
            </el-button>
            <el-button 
              v-if="canModifyMeeting(scope.row)" 
              type="link" 
              size="small" 
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
            <!-- 普通用户可以申请入会，企业管理员可以审批入会 -->
            <el-button 
              v-if="isRegularUser && !isOwnCompanyMeeting(scope.row)" 
              type="link" 
              size="small" 
              @click="applyToJoinMeeting(scope.row)"
              style="color: #67C23A;"
            >
              申请入会
            </el-button>
            <el-button 
              v-if="isTenantAdmin && isOwnCompanyMeeting(scope.row)" 
              type="link" 
              size="small" 
              @click="openMeetingApprovalDialog(scope.row)"
              style="color: #E6A23C;"
            >
              入会审批
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

    <!-- 新增会议弹窗 -->
    <el-dialog v-model="addDialogVisible" title="新增会议" width="60%">
      <el-form :model="formData" :rules="rules" ref="conferenceForm" label-width="100px" class="dialog-form">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="会议名称" prop="conferencename">
              <el-input v-model="formData.conferencename"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="会议封面" prop="coverpath">
              <el-upload
                action="http://localhost:9049/conferences/upload-cover"
                list-type="picture-card"
                :on-success="handleUploadSuccess"
                :on-remove="handleRemove"
                :file-list="fileList">
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="创建人" prop="creator">
              <el-input v-model="formData.creator"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会议状态" prop="state">
              <el-select v-model="formData.state" placeholder="请选择会议状态">
                <el-option label="进行中" value="进行中"></el-option>
                <el-option label="已结束" value="已结束"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="starttime">
              <el-date-picker v-model="formData.starttime" type="datetime" placeholder="选择开始时间"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endtime">
              <el-date-picker v-model="formData.endtime" type="datetime" placeholder="选择结束时间"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" class="editor-section">
          <el-col :span="24">
            <el-form-item label="会议内容" prop="contentspath">
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
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="closeAddDialog">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 修改会议弹窗 -->
    <el-dialog v-model="editDialogVisible" title="修改会议" width="60%">
      <el-form :model="formData" :rules="rules" ref="editConferenceForm" label-width="100px" class="dialog-form">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="会议名称" prop="conferencename">
              <el-input v-model="formData.conferencename"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="会议封面" prop="coverpath">
              <el-upload
                action="http://localhost:9049/conferences/upload-cover"
                list-type="picture-card"
                :on-success="handleUploadSuccess"
                :on-remove="handleRemove"
                :file-list="fileList">
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="创建人" prop="creator">
              <el-input v-model="formData.creator"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会议状态" prop="state">
              <el-select v-model="formData.state" placeholder="请选择会议状态">
                <el-option label="进行中" value="进行中"></el-option>
                <el-option label="已结束" value="已结束"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="starttime">
              <el-date-picker v-model="formData.starttime" type="datetime" placeholder="选择开始时间"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endtime">
              <el-date-picker v-model="formData.endtime" type="datetime" placeholder="选择结束时间"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" class="editor-section">
          <el-col :span="24">
            <el-form-item label="会议内容" prop="contentspath">
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
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="closeEditDialog">取消</el-button>
        <el-button type="primary" @click="submitEditForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 会议详情弹窗 -->
    <el-dialog v-model="detailsDialogVisible" title="会议详情" width="60%">
      <el-form :model="detailsFormData" label-width="100px" class="dialog-form" disabled>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="会议名称">
              <el-input v-model="detailsFormData.conferencename" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="会议封面">
              <img 
                :src="detailsFormData.coverpath ? `http://localhost:9049${detailsFormData.coverpath}` : '/images/default-icon.jpg'" 
                alt="会议封面" 
                style="width: 100px; height: auto;"
                @error="event => event.target.src = '/images/default-icon.jpg'"
              >
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="创建人">
              <el-input v-model="detailsFormData.creator" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会议状态">
              <el-input v-model="detailsFormData.state" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker v-model="detailsFormData.starttime" type="datetime" disabled></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker v-model="detailsFormData.endtime" type="datetime" disabled></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" class="editor-section">
          <el-col :span="24">
            <el-form-item label="会议内容">
              <div class="editor-wrapper">
                <div id="details-editor" class="editor-container" v-html="detailsFormData.contentspath"></div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="closeDetailsDialog">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 会议申请审批对话框 -->
    <el-dialog 
      v-model="meetingApprovalVisible" 
      title="会议入会申请审批" 
      width="80%"
      class="meeting-approval-dialog"
    >
      <div class="approval-content">
        <h3>{{ currentMeeting.conferencename }} - 入会申请列表</h3>
        
        <el-table :data="meetingApplications" style="width: 100%; margin-top: 20px;">
          <el-table-column prop="applicantName" label="申请人" width="120"></el-table-column>
          <el-table-column prop="applicantCompany" label="申请人公司" width="150"></el-table-column>
          <el-table-column prop="applicationTime" label="申请时间" width="180">
            <template v-slot="scope">
              {{ formatDateTime(scope.row.applicationTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="审批状态" width="100">
            <template v-slot="scope">
              <el-tag 
                :type="scope.row.status === 'approved' ? 'success' : scope.row.status === 'rejected' ? 'danger' : 'warning'"
                size="small"
              >
                {{ scope.row.status === 'approved' ? '已通过' : scope.row.status === 'rejected' ? '已拒绝' : '待审批' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="rejectionReason" label="拒绝原因" width="150" show-overflow-tooltip>
            <template v-slot="scope">
              <span v-if="scope.row.status === 'rejected' && scope.row.rejectionReason">
                {{ scope.row.rejectionReason }}
              </span>
              <span v-else style="color: #999;">-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template v-slot="scope">
              <div v-if="scope.row.status === 'pending'">
                <el-button type="success" size="small" @click="approveMeetingApplication(scope.row)">
                  通过
                </el-button>
                <el-button type="danger" size="small" @click="rejectMeetingApplication(scope.row)">
                  拒绝
                </el-button>
              </div>
              <div v-else>
                <span style="color: #999;">已处理</span>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <div v-if="meetingApplications.length === 0" style="text-align: center; padding: 40px; color: #999;">
          暂无入会申请
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeMeetingApprovalDialog">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 会议申请拒绝原因对话框 -->
    <el-dialog 
      v-model="rejectionReasonVisible" 
      title="填写拒绝原因" 
      width="500px"
    >
      <el-form>
        <el-form-item label="拒绝原因">
          <el-input 
            v-model="rejectionReason" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入拒绝该申请的原因"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rejectionReasonVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmRejectApplication">确认拒绝</el-button>
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
  </div>
</template>

<script>
import axios from '../utils/request.js';
import { ref, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import Quill from 'quill';
import 'quill/dist/quill.core.css';
import 'quill/dist/quill.snow.css';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

export default {
  name: 'MeetingManagement',
  setup() {
    const searchForm = ref({
      conferencename: '',
      creator: '',
      starttime: ''
    });
    const meetings = ref([]);
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
    
    // 会议申请审批相关状态
    const meetingApprovalVisible = ref(false);
    const rejectionReasonVisible = ref(false);
    const currentMeeting = ref({});
    const meetingApplications = ref([]);
    const rejectionReason = ref('');
    const currentApplication = ref({});
    
    // 用户权限状态
          const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    const isRegularUser = userInfo.role === 'User';
    const isTenantAdmin = userInfo.role === 'TAdmin';
    const isSystemAdmin = userInfo.role === 'Admin';
    
    // 权限控制计算属性
    const canManageMeeting = isSystemAdmin || isTenantAdmin;
    const formData = ref({
      conferencename: '',
      creator: '',
      coverpath: '',
      contentspath: '',
      state: '',
      starttime: '',
      endtime: '',
      tenantID: null // 新增字段
    });
    const detailsFormData = ref({
      conferencename: '',
      creator: '',
      coverpath: '',
      contentspath: '',
      state: '',
      starttime: '',
      endtime: ''
    });
    const fileList = ref([]);
    const rules = ref({
      conferencename: [{ required: true, message: '请输入会议名称', trigger: 'blur' }],
      creator: [{ required: true, message: '请输入创建人', trigger: 'blur' }],
      coverpath: [{ required: true, message: '请上传会议封面', trigger: 'change' }],
      contentspath: [{ required: true, message: '请输入会议内容', trigger: 'blur' }],
      state: [{ required: true, message: '请选择会议状态', trigger: 'change' }],
      starttime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
      endtime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
    });

    const fetchMeetings = async () => {
      loading.value = true;
      try {
        const response = await axios.get('http://localhost:9049/conferences');
        meetings.value = response.data.meetings;
        total.value = response.data.total;
      } catch (error) {
        ElMessage.error('获取会议数据失败');
      } finally {
        loading.value = false;
      }
    };

    onMounted(fetchMeetings);

    const handleSearch = () => {
      const params = {
        conferencename: searchForm.value.conferencename,
        creator: searchForm.value.creator,
        starttime: searchForm.value.starttime ? searchForm.value.starttime : ''
      };
      axios.get('http://localhost:9049/conferences/search', { params })
        .then(response => {
          meetings.value = response.data.meetings;
          total.value = response.data.total;
        })
        .catch(error => {
          console.error('Error searching data:', error);
        });
    };

    const handleReset = () => {
      searchForm.value.conferencename = '';
      searchForm.value.creator = '';
      searchForm.value.starttime = '';
      fetchMeetings();
    };

    const handleExport = async () => {
      try {
        const response = await axios.get('http://localhost:9049/conferences');
        const exportData = response.data.meetings.map(item => {
          return {
            会议ID: item.conferenceID,
            会议名称: item.conferencename,
            创建人: item.creator,
            会议封面: item.coverpath,
            会议内容: item.contentspath,
            开始时间: item.starttime,
            结束时间: item.endtime,
            状态: item.state,
            租户ID: item.tenantID
          };
        });
        const worksheet = XLSX.utils.json_to_sheet(exportData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, '会议列表');
        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
        const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
        saveAs(blob, '会议列表.xlsx');
      } catch (error) {
        console.error('Error exporting data:', error);
      }
    };

    const openAddDialog = () => {
      addDialogVisible.value = true;
      resetForm();

      // 获取当前登录用户的 TenantId
      const user = JSON.parse(localStorage.getItem('userInfo'));
      formData.value.tenantID = user ? user.tenantId : null;

      nextTick(() => {
        initializeEditor('#editor', false);
      });
    };

    const closeAddDialog = () => {
      addDialogVisible.value = false;
    };

    const openEditDialog = (row) => {
      const user = JSON.parse(localStorage.getItem('userInfo'));
      if (user.role === 'Admin' && user.tenantId !== row.tenantID) {
        ElMessage.error('您无权修改其他租户的会议');
        return;
      }
      if (row) {
        Object.assign(formData.value, row);
      } else if (selectedRow.value) {
        Object.assign(formData.value, selectedRow.value);
      } else {
        ElMessage.error('请先选择要修改的会议');
        return;
      }
      // 加载会议封面到 fileList
      fileList.value = [
        {
          name: '会议封面',
          url: `http://localhost:9049${formData.value.coverpath}`
        }
      ];
      editDialogVisible.value = true;
      nextTick(() => {
        initializeEditor('#edit-editor', true, formData.value.contentspath);
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
      formData.value.coverpath = response.url; // 假设后端返回的 URL 字段是 'url'
    };

    const handleRemove = (file, fileList) => {
      formData.value.coverpath = '';
    };

    const submitForm = () => {
      if (!formData.value.conferencename || !formData.value.contentspath || !formData.value.starttime || !formData.value.coverpath) {
        ElMessage.error('请填写所有必填项');
        return;
      }

      axios.post('http://localhost:9049/conferences', formData.value).then(() => {
        ElMessage.success('会议创建成功');
        closeAddDialog();
        fetchMeetings();
      }).catch(error => {
        console.error('会议创建失败:', error);
        ElMessage.error('会议创建失败: ' + (error.response?.data?.message || error.message));
      });
    };

    const submitEditForm = () => {
      if (!formData.value.conferencename || !formData.value.contentspath || !formData.value.starttime || !formData.value.coverpath) {
        ElMessage.error('请填写所有必填项');
        return;
      }

      axios.put(`http://localhost:9049/conferences/${formData.value.conferenceID}`, formData.value).then(() => {
        ElMessage.success('会议修改成功');
        closeEditDialog();
        fetchMeetings();
      }).catch(error => {
        console.error('会议修改失败:', error);
        ElMessage.error('会议修改失败: ' + (error.response?.data?.message || error.message));
      });
    };

    const handleDeleteConfirm = () => {
      if (!selectedRow.value) {
        ElMessage.error('请先选择要删除的会议');
        return;
      }
      ElMessageBox.confirm('此操作将永久删除该会议, 是否继续?', '提示', {
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
      if (user.role === 'TAdmin' && user.tenantId !== row.tenantID) {
        ElMessage.error('您无权删除其他租户的会议');
        return;
      }

      axios.delete(`http://localhost:9049/conferences/${row.conferenceID}`)
        .then(() => {
          ElMessage.success('会议删除成功');
          fetchMeetings();
        })
        .catch(error => {
          ElMessage.error('会议删除失败: ' + error.message);
        });
    };

    const handleSelectionChange = (rows) => {
      selectedRow.value = rows.length ? rows[0] : null;
    };

    const handleSizeChange = (size) => {
      pageSize.value = size;
      fetchMeetings();
    };

    const handleCurrentChange = (page) => {
      currentPage.value = page;
      fetchMeetings();
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
          showImagePreview(e.target.src);
        }
      });

      quill.on('text-change', () => {
        formData.value.contentspath = quill.root.innerHTML;
      });

      // 保存当前编辑器实例供外部按钮使用
      currentQuillInstance = quill;

      return quill;
    };

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
              
              if (currentQuillInstance) {
                const range = currentQuillInstance.getSelection() || { index: 0 };
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
              
              if (currentQuillInstance) {
                const range = currentQuillInstance.getSelection() || { index: 0 };
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

    const showImagePreview = (imageUrl) => {
      previewImageUrl.value = imageUrl;
      imagePreviewVisible.value = true;
    };

    const closeImagePreview = () => {
      imagePreviewVisible.value = false;
      previewImageUrl.value = '';
    };

    // 权限控制方法
    const canModifyMeeting = (meeting) => {
      // 系统管理员可以修改所有会议
      if (isSystemAdmin) {
        return true;
      }
      // 租户管理员只能修改自己租户的会议
      if (isTenantAdmin) {
        return userInfo.tenantId === meeting.tenantID;
      }
      // 普通用户不能修改会议
      return false;
    };

    const isOwnCompanyMeeting = (meeting) => {
      return userInfo.tenantId === meeting.tenantID;
    };

    // 会议申请入会功能
    const applyToJoinMeeting = async (meeting) => {
      if (!userInfo.id || !userInfo.tenantId) {
        ElMessage.error('用户信息不完整，无法申请');
        return;
      }

      try {
        // 设置请求头
        const headers = {
          'User-Role': userInfo.role,
          'User-Id': userInfo.id.toString(),
          'User-Tenant-Id': userInfo.tenantId.toString()
        };

        // 先检查是否已经申请过
        const checkResponse = await axios.get('http://localhost:9049/api/meeting-applications/check', {
          params: {
            meetingId: meeting.conferenceID,
            applicantId: userInfo.id
          },
          headers: headers
        });

        if (checkResponse.data.success && checkResponse.data.hasApplied) {
          ElMessage.warning('您已经申请过该会议，无需重复申请');
          return;
        }

        // 构建申请数据
        const applicationData = {
          meetingId: meeting.conferenceID,
          applicantId: userInfo.id,
          applicantName: userInfo.nickname || userInfo.username,
          applicantCompany: getTenantName(userInfo.tenantId),
          tenantId: userInfo.tenantId,
          meetingTenantId: meeting.tenantID || meeting.tenantId
        };

        // 提交申请
        const response = await axios.post('http://localhost:9049/api/meeting-applications/submit', applicationData, {
          headers: headers
        });

        if (response.data.success) {
          ElMessage.success('申请提交成功，等待企业管理员审批');
        } else {
          ElMessage.error(response.data.message || '申请提交失败');
        }

      } catch (error) {
        console.error('申请入会失败:', error);
        if (error.response && error.response.data && error.response.data.message) {
          ElMessage.error(error.response.data.message);
        } else {
          ElMessage.error('申请入会失败，请重试');
        }
      }
    };

    // 打开会议审批对话框
    const openMeetingApprovalDialog = async (meeting) => {
      currentMeeting.value = meeting;
      
      try {
        // 设置请求头
        const headers = {
          'User-Role': userInfo.role,
          'User-Id': userInfo.id.toString(),
          'User-Tenant-Id': userInfo.tenantId.toString()
        };

        // 获取该会议的申请列表
        const response = await axios.get(`http://localhost:9049/api/meeting-applications/meeting/${meeting.conferenceID}`, {
          headers: headers
        });
        
        if (response.data.success) {
          meetingApplications.value = response.data.applications.map(app => ({
            id: app.id,
            applicantName: app.applicantName,
            applicantCompany: app.applicantCompany,
            applicationTime: app.applicationTime,
            status: app.status,
            rejectionReason: app.rejectionReason,
            meetingId: app.meetingId,
            applicantId: app.applicantId
          }));
        } else {
          console.error('获取申请列表失败:', response.data.message);
          meetingApplications.value = [];
        }
      } catch (error) {
        console.error('获取申请列表失败:', error);
        ElMessage.error('获取申请列表失败');
        meetingApplications.value = [];
      }
      
      meetingApprovalVisible.value = true;
    };

    const closeMeetingApprovalDialog = () => {
      meetingApprovalVisible.value = false;
      currentMeeting.value = {};
      meetingApplications.value = [];
    };

    // 通过会议申请
    const approveMeetingApplication = async (application) => {
      try {
        await ElMessageBox.confirm('确定要通过该申请吗？', '确认审批', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        });

        const approvalData = {
          status: 'approved',
          rejectionReason: null,
          approverId: userInfo.id,
          approverName: userInfo.nickname || userInfo.username
        };

        // 设置请求头
        const headers = {
          'User-Role': userInfo.role,
          'User-Id': userInfo.id.toString(),
          'User-Tenant-Id': userInfo.tenantId.toString()
        };

        const response = await axios.post(`http://localhost:9049/api/meeting-applications/${application.id}/approve`, approvalData, {
          headers: headers
        });

        if (response.data.success) {
          ElMessage.success('申请审批通过');
          // 重新加载申请列表
          openMeetingApprovalDialog(currentMeeting.value);
        } else {
          ElMessage.error(response.data.message || '审批失败');
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('审批失败:', error);
          ElMessage.error('审批失败，请重试');
        }
      }
    };

    // 拒绝会议申请
    const rejectMeetingApplication = (application) => {
      currentApplication.value = application;
      rejectionReasonVisible.value = true;
    };

    const confirmRejectApplication = async () => {
      if (!rejectionReason.value.trim()) {
        ElMessage.error('请输入拒绝原因');
        return;
      }

      try {
        const approvalData = {
          status: 'rejected',
          rejectionReason: rejectionReason.value,
          approverId: userInfo.id,
          approverName: userInfo.nickname || userInfo.username
        };

        // 设置请求头
        const headers = {
          'User-Role': userInfo.role,
          'User-Id': userInfo.id.toString(),
          'User-Tenant-Id': userInfo.tenantId.toString()
        };

        const response = await axios.post(`http://localhost:9049/api/meeting-applications/${currentApplication.value.id}/approve`, approvalData, {
          headers: headers
        });

        if (response.data.success) {
          ElMessage.success('申请已拒绝');
          rejectionReasonVisible.value = false;
          // 重新加载申请列表
          openMeetingApprovalDialog(currentMeeting.value);
        } else {
          ElMessage.error(response.data.message || '审批失败');
        }
      } catch (error) {
        console.error('拒绝申请失败:', error);
        ElMessage.error('拒绝申请失败，请重试');
      }
    };

    // 获取租户名称
    const getTenantName = (tenantId) => {
      // 这里可以根据租户ID返回租户名称
      // 可以维护一个租户映射表或者调用API获取
      const tenantMap = {
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
      };
      return tenantMap[tenantId] || '未知公司';
    };

    const formatDateTime = (dateTimeString) => {
      if (!dateTimeString) return '';
      const date = new Date(dateTimeString);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    };

    // 根据开始时间和结束时间判断会议状态
    const getMeetingStatus = (meeting) => {
      if (!meeting.starttime || !meeting.endtime) {
        return '待定';
      }
      
      const now = new Date();
      const startTime = new Date(meeting.starttime);
      const endTime = new Date(meeting.endtime);
      
      if (now < startTime) {
        return '未开始';
      } else if (now >= startTime && now <= endTime) {
        return '进行中';
      } else {
        return '已结束';
      }
    };

    // 获取状态对应的标签类型
    const getStatusType = (meeting) => {
      const status = getMeetingStatus(meeting);
      
      switch (status) {
        case '未开始':
          return 'warning';
        case '进行中':
          return 'success';
        case '已结束':
          return 'info';
        case '待定':
        default:
          return 'info';
      }
    };



    const resetForm = () => {
      formData.value = {
        conferencename: '',
        creator: '',
        coverpath: '',
        contentspath: '',
        state: '',
        starttime: '',
        endtime: '',
        tenantID: null // 重置 tenantID
      };
      fileList.value = [];
    };

    return {
      searchForm,
      meetings,
      selectedRow,
      loading,
      currentPage,
      pageSize,
      total,
      addDialogVisible,
      editDialogVisible,
      detailsDialogVisible,
      imagePreviewVisible,
      previewImageUrl,
      meetingApprovalVisible,
      rejectionReasonVisible,
      currentMeeting,
      meetingApplications,
      rejectionReason,
      currentApplication,
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
      handleRemove,
      handleDeleteConfirm,
      handleDelete,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      handleAudioUpload,
      handleVideoUpload,
      showImagePreview,
      closeImagePreview,
      applyToJoinMeeting,
      openMeetingApprovalDialog,
      closeMeetingApprovalDialog,
      approveMeetingApplication,
      rejectMeetingApplication,
      confirmRejectApplication,
      isOwnCompanyMeeting,
      getTenantName,
      formatDateTime,
      getMeetingStatus,
      getStatusType,
      isRegularUser,
      isTenantAdmin,
      isSystemAdmin,
      canManageMeeting,
      canModifyMeeting
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
    radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
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
}

.editor-wrapper {
  width: 100%;
  height: 400px;
}

.editor-container {
  height: 350px;
}

.dialog-form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
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
:deep(.ql-editor) img {
  cursor: pointer;
  transition: transform 0.3s ease;
  max-width: 100%;
  height: auto;
}

:deep(.ql-editor) img:hover {
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
