<template>
  <div class="approval-container">
    <!-- 页面标题 -->
    <div class="approval-header">
      <div class="header-content">
        <h1 class="page-title">审核管理</h1>
        <p class="page-description">统一管理课程、会议、资讯的审核流程</p>
      </div>
    </div>

    <!-- 审核统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card pending">
            <div class="stats-content">
              <div class="stats-icon">
                <i class="icon">⏳</i>
              </div>
              <div class="stats-info">
                <h3>{{ totalPending }}</h3>
                <p>待审核</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card approved">
            <div class="stats-content">
              <div class="stats-icon">
                <i class="icon">✅</i>
              </div>
              <div class="stats-info">
                <h3>{{ totalApproved }}</h3>
                <p>已通过</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card rejected">
            <div class="stats-content">
              <div class="stats-icon">
                <i class="icon">❌</i>
              </div>
              <div class="stats-info">
                <h3>{{ totalRejected }}</h3>
                <p>已拒绝</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card total">
            <div class="stats-content">
              <div class="stats-icon">
                <i class="icon">📊</i>
              </div>
              <div class="stats-info">
                <h3>{{ totalAll }}</h3>
                <p>总计</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 审核内容区域 -->
    <div class="approval-content">
      <el-card class="approval-card">
        <!-- 筛选器 -->
        <div class="filter-section">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-select v-model="currentType" placeholder="选择类型" @change="handleTypeChange">
                <el-option label="全部" value="all"></el-option>
                <el-option label="课程" value="course"></el-option>
                <el-option label="会议" value="conference"></el-option>
                <el-option label="资讯" value="information"></el-option>
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-select v-model="currentStatus" placeholder="审核状态" @change="handleStatusChange">
                <el-option label="全部" value="all"></el-option>
                <el-option label="待审核" value="pending"></el-option>
                <el-option label="已通过" value="approved"></el-option>
                <el-option label="已拒绝" value="rejected"></el-option>
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-input 
                v-model="searchKeyword" 
                placeholder="搜索标题或创建人..."
                clearable
                @input="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="4">
              <el-button type="primary" @click="fetchData" :loading="loading">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </el-col>
          </el-row>
        </div>

        <!-- 审核列表 -->
        <div class="approval-list">
          <el-table 
            :data="approvalData" 
            v-loading="loading" 
            style="width: 100%"
            :row-class-name="getRowClassName"
          >
            <el-table-column prop="type" label="类型" width="80">
              <template #default="scope">
                <el-tag :type="getTypeTagType(scope.row.type)" size="small">
                  {{ getTypeLabel(scope.row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column prop="title" label="标题" min-width="200">
              <template #default="scope">
                <div class="title-cell">
                  <h4>{{ scope.row.title }}</h4>
                  <p>{{ scope.row.description }}</p>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column prop="creator" label="创建人" width="120"></el-table-column>
            
            <el-table-column prop="approvalStatus" label="审核状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.approvalStatus)" size="small">
                  {{ getStatusLabel(scope.row.approvalStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column prop="createTime" label="创建时间" width="150">
              <template #default="scope">
                {{ formatDateTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            
            <el-table-column prop="rejectionReason" label="拒绝原因" width="150">
              <template #default="scope">
                <span v-if="scope.row.rejectionReason" class="rejection-reason">
                  {{ scope.row.rejectionReason }}
                </span>
                <span v-else class="no-reason">-</span>
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="scope">
                <div class="action-buttons">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="viewDetails(scope.row)"
                    :icon="View"
                  >
                    查看
                  </el-button>
                  
                  <el-button 
                    v-if="scope.row.approvalStatus === 'pending'" 
                    type="success" 
                    size="small" 
                    @click="approve(scope.row)"
                    :icon="Check"
                  >
                    通过
                  </el-button>
                  
                  <el-button 
                    v-if="scope.row.approvalStatus === 'pending'" 
                    type="danger" 
                    size="small" 
                    @click="reject(scope.row)"
                    :icon="Close"
                  >
                    拒绝
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-section">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 详情对话框 -->
    <el-dialog 
      v-model="detailsVisible" 
      :title="detailsTitle" 
      width="60%"
      class="details-dialog"
    >
      <div class="details-content" v-if="currentItem">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="类型">
            <el-tag :type="getTypeTagType(currentItem.type)">
              {{ getTypeLabel(currentItem.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag :type="getStatusTagType(currentItem.approvalStatus)">
              {{ getStatusLabel(currentItem.approvalStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="标题" :span="2">
            {{ currentItem.title }}
          </el-descriptions-item>
          <el-descriptions-item label="创建人">
            {{ currentItem.creator }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(currentItem.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">
            {{ currentItem.description }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentItem.rejectionReason" label="拒绝原因" :span="2">
            <span class="rejection-reason">{{ currentItem.rejectionReason }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 如果有封面图片，显示预览 -->
        <div v-if="currentItem.coverImage" class="cover-preview">
          <h4>封面预览</h4>
          <el-image 
            :src="currentItem.coverImage" 
            style="width: 200px; height: 150px; object-fit: cover; border-radius: 8px;"
            :preview-src-list="[currentItem.coverImage]"
          />
        </div>

        <!-- 如果是课程类型且有视频，显示视频预览 -->
        <div v-if="currentItem.type === 'course' && currentItem.videoPath" class="video-preview">
          <h4>课程视频预览</h4>
          <div class="video-container">
            <video 
              :src="getVideoUrl(currentItem.videoPath)" 
              controls 
              style="width: 100%; max-width: 500px; height: 300px; border-radius: 8px;"
              @error="handleVideoError"
            >
              您的浏览器不支持视频播放
            </video>
          </div>
        </div>

        <!-- 内容预览 -->
        <div v-if="currentItem.content" class="content-preview">
          <h4>内容预览</h4>
          <div class="content-box" v-html="currentItem.content"></div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailsVisible = false">关闭</el-button>
          <el-button 
            v-if="currentItem && currentItem.approvalStatus === 'pending'" 
            type="success" 
            @click="approve(currentItem)"
          >
            审核通过
          </el-button>
          <el-button 
            v-if="currentItem && currentItem.approvalStatus === 'pending'" 
            type="danger" 
            @click="reject(currentItem)"
          >
            审核拒绝
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 拒绝原因对话框 -->
    <el-dialog 
      v-model="rejectVisible" 
      title="审核拒绝" 
      width="400px"
      class="reject-dialog"
    >
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因" required>
          <el-input 
            v-model="rejectForm.reason" 
            type="textarea" 
            :rows="4"
            placeholder="请输入拒绝原因..."
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rejectVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmReject" :loading="submitting">
            确认拒绝
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Check, Close } from '@element-plus/icons-vue'
import axios from '../utils/request.js'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const currentType = ref('all')
const currentStatus = ref('pending')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const approvalData = ref([] as any[])
const detailsVisible = ref(false)
const rejectVisible = ref(false)
const currentItem = ref(null as any)

const rejectForm = ref({
  reason: ''
})

// 统计数据
const totalPending = ref(0)
const totalApproved = ref(0)
const totalRejected = ref(0)
const totalAll = computed(() => totalPending.value + totalApproved.value + totalRejected.value)

const detailsTitle = computed(() => {
  if (!currentItem.value) return '详情'
  return `${getTypeLabel(currentItem.value.type)}详情 - ${currentItem.value.title}`
})

// 方法
const fetchData = async () => {
  loading.value = true
  try {
    // 清空现有数据，防止重复
    approvalData.value = []
    
    // 根据类型和状态获取数据
    if (currentType.value === 'all' || currentType.value === 'course') {
      await fetchCourses()
    }
    if (currentType.value === 'all' || currentType.value === 'conference') {
      await fetchConferences()
    }
    if (currentType.value === 'all' || currentType.value === 'information') {
      await fetchInformation()
    }
    
    await fetchStats()
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const fetchCourses = async () => {
  try {
    let url = 'http://localhost:9049/api/courses'
    if (currentStatus.value !== 'all') {
      url += `/status/${currentStatus.value}`
    }
    
    const response = await axios.get(url)
    const courses = response.data.courses || []
    
    const courseData = courses.map(course => ({
      id: course.courseID,
      type: 'course',
      title: course.coursename,
      description: course.courseintro,
      creator: course.owner,
      approvalStatus: course.approvalStatus || 'pending',
      rejectionReason: course.rejectionReason,
      createTime: course.createTime,
      coverImage: course.coverpath ? `http://localhost:9049${course.coverpath}` : null,
      videoPath: course.videopath, // 添加视频路径
      content: course.courseintro,
      originalData: course
    }))
    
    if (currentType.value === 'course') {
      approvalData.value = courseData
    } else {
      approvalData.value = [...approvalData.value, ...courseData]
    }
  } catch (error) {
    console.error('获取课程数据失败:', error)
  }
}

const fetchConferences = async () => {
  try {
    let url = 'http://localhost:9049/conferences'
    if (currentStatus.value !== 'all') {
      url += `/status/${currentStatus.value}`
    }
    
    const response = await axios.get(url)
    const conferences = response.data.meetings || []
    
    const conferenceData = conferences.map(conference => ({
      id: conference.conferenceID,
      type: 'conference',
      title: conference.conferencename,
      description: '会议内容',
      creator: conference.creator,
      approvalStatus: conference.approvalStatus || 'pending',
      rejectionReason: conference.rejectionReason,
      createTime: conference.starttime,
      coverImage: conference.coverpath ? `http://localhost:9049${conference.coverpath}` : null,
      content: conference.contentspath,
      originalData: conference
    }))
    
    if (currentType.value === 'conference') {
      approvalData.value = conferenceData
    } else {
      approvalData.value = [...approvalData.value, ...conferenceData]
    }
  } catch (error) {
    console.error('获取会议数据失败:', error)
  }
}

const fetchInformation = async () => {
  try {
    let url = 'http://localhost:9049/information'
    if (currentStatus.value !== 'all') {
      url += `/status/${currentStatus.value}`
    } else {
      url += '/path/1' // 假设路径前缀为1
    }
    
    const response = await axios.get(url)
    const informationList = response.data.informationList || []
    
    const informationData = informationList.map(info => ({
      id: info.id,
      type: 'information',
      title: info.title,
      description: info.introduction,
      creator: info.author,
      approvalStatus: info.approvalStatus || 'pending',
      rejectionReason: info.rejectionReason,
      createTime: info.createTime,
      coverImage: info.picture ? `http://localhost:9049${info.picture}` : null,
      content: info.content,
      originalData: info
    }))
    
    if (currentType.value === 'information') {
      approvalData.value = informationData
    } else {
      approvalData.value = [...approvalData.value, ...informationData]
    }
  } catch (error) {
    console.error('获取资讯数据失败:', error)
  }
}

const fetchStats = async () => {
  try {
    // 获取统计数据
    const [courseStats, conferenceStats, infoStats] = await Promise.all([
      axios.get('http://localhost:9049/api/courses/status/pending').catch(() => ({ data: { courses: [] } })),
      axios.get('http://localhost:9049/conferences/status/pending').catch(() => ({ data: { meetings: [] } })),
      axios.get('http://localhost:9049/information/status/pending').catch(() => ({ data: { informationList: [] } }))
    ])
    
    const [courseApproved, conferenceApproved, infoApproved] = await Promise.all([
      axios.get('http://localhost:9049/api/courses/status/approved').catch(() => ({ data: { courses: [] } })),
      axios.get('http://localhost:9049/conferences/status/approved').catch(() => ({ data: { meetings: [] } })),
      axios.get('http://localhost:9049/information/status/approved').catch(() => ({ data: { informationList: [] } }))
    ])
    
    const [courseRejected, conferenceRejected, infoRejected] = await Promise.all([
      axios.get('http://localhost:9049/api/courses/status/rejected').catch(() => ({ data: { courses: [] } })),
      axios.get('http://localhost:9049/conferences/status/rejected').catch(() => ({ data: { meetings: [] } })),
      axios.get('http://localhost:9049/information/status/rejected').catch(() => ({ data: { informationList: [] } }))
    ])
    
    totalPending.value = (courseStats.data.courses?.length || 0) + 
                         (conferenceStats.data.meetings?.length || 0) + 
                         (infoStats.data.informationList?.length || 0)
    
    totalApproved.value = (courseApproved.data.courses?.length || 0) + 
                          (conferenceApproved.data.meetings?.length || 0) + 
                          (infoApproved.data.informationList?.length || 0)
    
    totalRejected.value = (courseRejected.data.courses?.length || 0) + 
                          (conferenceRejected.data.meetings?.length || 0) + 
                          (infoRejected.data.informationList?.length || 0)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const handleTypeChange = () => {
  currentPage.value = 1
  approvalData.value = []
  fetchData()
}

const handleStatusChange = () => {
  currentPage.value = 1
  approvalData.value = []
  fetchData()
}

const handleSearch = () => {
  // 实现搜索逻辑
  if (searchKeyword.value) {
    const filtered = approvalData.value.filter(item => 
      item.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      item.creator.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
    // 这里应该实际过滤数据，为了简化，我们重新获取数据
  }
  fetchData()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  fetchData()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchData()
}

const viewDetails = (item: any) => {
  currentItem.value = item
  detailsVisible.value = true
}

const approve = async (item: any) => {
  try {
    await ElMessageBox.confirm('确定要通过此审核吗？', '确认审核', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    const url = getApprovalUrl(item)
    await axios.post(url, {
      approvalStatus: 'approved',
      rejectionReason: null
    })
    
    ElMessage.success('审核通过成功')
    detailsVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核通过失败:', error)
      ElMessage.error('审核通过失败')
    }
  }
}

const reject = (item: any) => {
  currentItem.value = item
  rejectForm.value.reason = ''
  rejectVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.value.reason.trim()) {
    ElMessage.error('请输入拒绝原因')
    return
  }
  
  submitting.value = true
  try {
    const url = getApprovalUrl(currentItem.value)
    await axios.post(url, {
      approvalStatus: 'rejected',
      rejectionReason: rejectForm.value.reason
    })
    
    ElMessage.success('审核拒绝成功')
    rejectVisible.value = false
    detailsVisible.value = false
    fetchData()
  } catch (error) {
    console.error('审核拒绝失败:', error)
    ElMessage.error('审核拒绝失败')
  } finally {
    submitting.value = false
  }
}

const getApprovalUrl = (item: any) => {
  switch (item.type) {
    case 'course':
      return `http://localhost:9049/api/courses/${item.id}/approve`
    case 'conference':
      return `http://localhost:9049/conferences/${item.id}/approve`
    case 'information':
      return `http://localhost:9049/information/${item.id}/approve`
    default:
      throw new Error('未知的审核类型')
  }
}

// 辅助方法
const getTypeLabel = (type: string) => {
  const labels = {
    course: '课程',
    conference: '会议',
    information: '资讯'
  }
  return labels[type] || type
}

const getTypeTagType = (type: string) => {
  const types = {
    course: 'primary',
    conference: 'success',
    information: 'warning'
  }
  return types[type] || 'info'
}

const getStatusLabel = (status: string) => {
  const labels = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return labels[status] || status
}

const getStatusTagType = (status: string) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return types[status] || 'info'
}

const getRowClassName = ({ row }: { row: any }) => {
  if (row.approvalStatus === 'pending') return 'pending-row'
  if (row.approvalStatus === 'approved') return 'approved-row'
  if (row.approvalStatus === 'rejected') return 'rejected-row'
  return ''
}

const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 视频相关函数
const getVideoUrl = (videoPath: string) => {
  if (!videoPath) return ''
  if (videoPath.startsWith('http')) {
    return videoPath
  }
  return `http://localhost:9049${videoPath}`
}

const handleVideoError = (event: any) => {
  console.error('视频加载失败:', event)
  ElMessage.error('视频加载失败，请检查视频文件是否存在')
}

// 生命周期
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.approval-container {
  padding: 0;
  background: linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 50%, #B3E5FC 100%);
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.approval-header {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  padding: 40px 32px;
  border-bottom: 1px solid rgba(0, 188, 212, 0.12);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #263238;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #00BCD4 0%, #81C784 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-description {
  font-size: 16px;
  color: #546E7A;
  margin: 0;
}

.stats-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px;
}

.stats-card {
  border-radius: 16px;
  border: none;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.stats-card.pending {
  background: linear-gradient(135deg, #FFF3CD 0%, #FFEAA7 100%);
}

.stats-card.approved {
  background: linear-gradient(135deg, #D4EDDA 0%, #A8E6CF 100%);
}

.stats-card.rejected {
  background: linear-gradient(135deg, #F8D7DA 0%, #FFB3BA 100%);
}

.stats-card.total {
  background: linear-gradient(135deg, #D1ECF1 0%, #B3E5FC 100%);
}

.stats-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stats-icon {
  font-size: 24px;
  margin-right: 16px;
}

.stats-info h3 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 4px 0;
  color: #263238;
}

.stats-info p {
  font-size: 14px;
  color: #546E7A;
  margin: 0;
}

.approval-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px 32px;
}

.approval-card {
  border-radius: 20px;
  border: none;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.filter-section {
  padding: 24px;
  border-bottom: 1px solid #ECEFF1;
  margin-bottom: 24px;
}

.approval-list {
  padding: 0 24px;
}

.title-cell h4 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #263238;
}

.title-cell p {
  font-size: 12px;
  color: #78909C;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 180px;
}

.action-buttons {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-start;
}

.action-buttons .el-button {
  margin: 0;
  padding: 4px 12px;
  font-size: 12px;
  min-width: auto;
}

.rejection-reason {
  color: #F44336;
  font-size: 12px;
}

.no-reason {
  color: #9E9E9E;
  font-style: italic;
}

.pagination-section {
  padding: 24px;
  display: flex;
  justify-content: center;
}

/* 表格行样式 */
:deep(.pending-row) {
  background-color: #FFF8E1;
}

:deep(.approved-row) {
  background-color: #E8F5E8;
}

:deep(.rejected-row) {
  background-color: #FFEBEE;
}

/* 详情对话框样式 */
.details-content {
  max-height: 60vh;
  overflow-y: auto;
}

.cover-preview {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ECEFF1;
}

.cover-preview h4 {
  margin: 0 0 16px 0;
  color: #263238;
}

.video-preview {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ECEFF1;
}

.video-preview h4 {
  margin: 0 0 16px 0;
  color: #263238;
}

.video-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.video-container video {
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.content-preview {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ECEFF1;
}

.content-preview h4 {
  margin: 0 0 16px 0;
  color: #263238;
}

.content-box {
  max-height: 200px;
  overflow-y: auto;
  padding: 16px;
  background-color: #F5F5F5;
  border-radius: 8px;
  border: 1px solid #E0E0E0;
}

/* 拒绝对话框样式 */
.reject-dialog .el-textarea__inner {
  border-radius: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-section .el-col {
    margin-bottom: 16px;
  }
  
  .filter-section .el-col {
    margin-bottom: 12px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style> 