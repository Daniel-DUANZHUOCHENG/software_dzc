<template>
  <div class="home-container">
    <!-- 统计面板 -->
    <div class="stats-panel">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ realTimeVisitors }}</div>
          <div class="stat-label">实时访客</div>
        </div>
        <div class="stat-trend up">
          <el-icon><TrendCharts /></el-icon>
          <span>+12%</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Connection /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ onlineUsers }}</div>
          <div class="stat-label">在线用户</div>
        </div>
        <div class="stat-trend up">
          <el-icon><TrendCharts /></el-icon>
          <span>+8%</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><House /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ tenants.length }}</div>
          <div class="stat-label">合作成员</div>
        </div>
        <div class="stat-trend up">
          <el-icon><TrendCharts /></el-icon>
          <span>+5%</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Calendar /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">24</div>
          <div class="stat-label">本月会议</div>
        </div>
        <div class="stat-trend down">
          <el-icon><TrendCharts /></el-icon>
          <span>-3%</span>
        </div>
      </div>
    </div>

    <!-- 轮播图展示 -->
    <div class="carousel-section">
      <div class="section-header">
        <h2>最新动态</h2>
        <el-button type="primary" @click="openEditDialog" size="small">
          <el-icon><Setting /></el-icon>
          管理轮播图
        </el-button>
      </div>
      
      <el-carousel 
        :interval="4000" 
        arrow="always" 
        height="400px"
        class="main-carousel"
        indicator-position="outside"
      >
        <el-carousel-item v-for="(image, index) in carouselImages" :key="index">
          <div class="carousel-item">
            <el-image
              :src="image"
              :preview-src-list="carouselImages"
              :initial-index="index"
              class="carousel-image"
              :alt="`轮播图${index + 1}`"
              style="cursor: pointer; width: 100%; height: 100%; object-fit: cover;"
              @error="event => event.target.src = '/images/default-icon.jpg'"
            />
            <div class="carousel-overlay">
              <h3>测盟汇系统</h3>
              <p>专业的会议管理与信息交流平台</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 协会简介 -->
    <div class="intro-section">
      <div class="section-header">
        <h2>协会简介</h2>
        <div class="section-subtitle">中国电子质量管理协会计算机软硬件和信息系统质量测评分会</div>
      </div>
      
      <div class="intro-content">
        <div class="intro-card">
          <div class="intro-icon">
            <el-icon><Medal /></el-icon>
          </div>
          <div class="intro-text">
            <h3>专业评估机构</h3>
            <p>计算机软硬件和信息系统质量测评分会是中国电子质量管理协会设立的17个分支机构之一。分会可以对计算机软硬件和信息系统所有潜在的、现有的风险进行评估及分析。</p>
          </div>
        </div>
        
        <div class="intro-card">
          <div class="intro-icon">
            <el-icon><Aim /></el-icon>
          </div>
          <div class="intro-text">
            <h3>客观公正评审</h3>
            <p>对自愿申请参加能力评审的造价评估机构进行客观、公正的能力评审，最终给出能力评审结论，确保行业标准的严格执行。</p>
          </div>
        </div>
        
        <div class="intro-card">
          <div class="intro-icon">
            <el-icon><Connection /></el-icon>
          </div>
          <div class="intro-text">
            <h3>行业交流平台</h3>
            <p>为会员单位提供信息交流、决策计划、组织事务处理、吸引新成员以及社交网络建立的综合性服务平台。</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 合作成员展示 -->
    <div class="members-section">
      <div class="section-header">
        <h2>合作成员</h2>
        <div class="section-subtitle">携手共建行业生态</div>
      </div>
      
      <div class="members-grid">
        <div 
          class="member-card" 
          v-for="(tenant, index) in tenants" 
          :key="index" 
          @click="goToTenantDetail(tenant.id)"
        >
          <div class="member-avatar">
            <img 
              :src="getTenantIcon(tenant.icon || '')" 
              :alt="tenant.tenantName" 
              @error="handleImageError"
            />
          </div>
          <div class="member-info">
            <h4>{{ tenant.tenantName }}</h4>
            <p>{{ tenant.contactPerson }}</p>
            <div class="member-status">
              <span class="status-dot active"></span>
              <span>活跃成员</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 轮播图管理对话框 -->
    <el-dialog 
      v-model="editDialogVisible" 
      title="轮播图管理" 
      width="60%"
      class="carousel-dialog"
    >
      <div class="dialog-content">
        <el-upload
          action="http://localhost:9049/carousel/upload"
          list-type="picture-card"
          :on-success="handleUploadSuccess"
          :on-remove="handleRemove"
          :file-list="fileList"
          class="upload-area"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeEditDialog">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { 
  User, Connection, House, Calendar, TrendCharts, Setting, 
  Medal, Aim, Plus 
} from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 定义租户接口
interface Tenant {
  id: string
  tenantName: string
  contactPerson: string
  icon?: string
}

const router = useRouter()

const carouselImages = ref([
  '/images/banner/banner1.jpg'
])

const tenants = ref<Tenant[]>([])
const onlineUsers = ref(0)
const realTimeVisitors = ref(0)
const editDialogVisible = ref(false)
const fileList = ref<any[]>([])
const formData = ref({})

onMounted(() => {
  fetchTenants()
  incrementOnlineUsers()
  fetchStats()
  window.addEventListener('beforeunload', handleBeforeUnload)
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload)
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  decrementOnlineUsers()
  updateRealTimeVisitors()
})

const fetchTenants = async () => {
  try {
    const response = await axios.get('http://localhost:9049/tenants/all')
    if (response.data && response.data.tenantList) {
      tenants.value = response.data.tenantList
    }
  } catch (error) {
    console.error('获取租户列表失败:', error)
  }
}

const getTenantIcon = (iconPath: string) => {
  console.log('Home getTenantIcon called with:', iconPath)
  
  // 1. 没有icon，返回默认图片
  if (!iconPath || iconPath === 'null' || iconPath === 'undefined') {
    console.log('No icon path provided, using default icon')
    return '/images/default-icon.jpg'
  }
  
  // 2. 完整URL（如官网图片）
  if (iconPath.startsWith('http')) {
    console.log('Using full URL:', iconPath)
    return iconPath
  }
  
  // 3. 如果是相对路径，添加服务器地址
  if (iconPath.startsWith('/')) {
    const fullUrl = `http://localhost:9049${iconPath}`
    console.log('Using relative path with server:', fullUrl)
    return fullUrl
  }
  
  // 4. 默认情况，添加服务器地址
  const fullUrl = `http://localhost:9049/${iconPath}`
  console.log('Using default path with server:', fullUrl)
  return fullUrl
}

const goToTenantDetail = (tenantId: string) => {
  router.push({ path: `/tenantDetail/${tenantId}` })
}

const handleBeforeUnload = (event: Event) => {
  updateRealTimeVisitors()
  event.preventDefault()
}

const updateRealTimeVisitors = async () => {
  try {
    await axios.post('http://localhost:9049/userBehavior/updateRealTimeVisitors')
  } catch (error) {
    console.error('更新实时访客失败:', error)
  }
}

const incrementOnlineUsers = async () => {
  try {
    await axios.post('http://localhost:9049/userBehavior/increment')
  } catch (error) {
    console.error('增加在线用户失败:', error)
  }
}

const decrementOnlineUsers = async () => {
  try {
    await axios.post('http://localhost:9049/userBehavior/decrement')
  } catch (error) {
    console.error('减少在线用户失败:', error)
  }
}

const fetchStats = () => {
  setInterval(async () => {
    try {
      const [onlineResponse, visitorsResponse] = await Promise.all([
        axios.get('http://localhost:9049/userBehavior/onlineUsers'),
        axios.get('http://localhost:9049/userBehavior/realTimeVisitors')
      ])
      
      onlineUsers.value = onlineResponse.data.onlineUsers
      realTimeVisitors.value = visitorsResponse.data.realTimeVisitors
    } catch (error) {
      console.error('获取统计数据失败:', error)
    }
  }, 5000)
}

const openEditDialog = () => {
  editDialogVisible.value = true
}

const closeEditDialog = () => {
  editDialogVisible.value = false
}

const handleUploadSuccess = (response: any, file: any, fileList: any[]) => {
  console.log('Upload response:', response)
  const imageUrl = response.url ? `http://localhost:9049${response.url}` : response
  carouselImages.value.push(imageUrl)
  ElMessage.success('图片上传成功')
}

const handleRemove = (file: any, fileList: any[]) => {
  const index = carouselImages.value.findIndex(img => img === file.url)
  if (index > -1) {
    carouselImages.value.splice(index, 1)
  }
  ElMessage.success('图片删除成功')
}

const submitForm = () => {
  // 提交轮播图配置
  closeEditDialog()
  ElMessage.success('轮播图配置已保存')
}

const handleVisibilityChange = () => {
  if (document.hidden) {
    decrementOnlineUsers()
  } else {
    incrementOnlineUsers()
  }
}

const handleImageError = (event: Event) => {
  console.log('Image load error, using default icon')
  const target = event.target as HTMLImageElement
  target.src = '/images/default-icon.jpg'
}
</script>

<style scoped>
.home-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

/* 统计面板 */
.stats-panel {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 6px;
}

.stat-trend.up {
  color: #27ae60;
  background: rgba(39, 174, 96, 0.1);
}

.stat-trend.down {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
}

/* 轮播图区域 */
.carousel-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
}

.section-subtitle {
  font-size: 16px;
  color: #7f8c8d;
  margin-top: 8px;
}

.main-carousel {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.carousel-item {
  position: relative;
  height: 100%;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
  padding: 40px;
  text-align: center;
}

.carousel-overlay h3 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
}

.carousel-overlay p {
  font-size: 18px;
  opacity: 0.9;
}

/* 简介区域 */
.intro-section {
  margin-bottom: 32px;
}

.intro-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.intro-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.intro-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.intro-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  color: white;
  font-size: 32px;
}

.intro-text h3 {
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 16px;
}

.intro-text p {
  font-size: 16px;
  line-height: 1.6;
  color: #7f8c8d;
}

/* 成员展示区域 */
.members-section {
  margin-bottom: 32px;
}

.members-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.member-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
}

.member-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.member-avatar {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.member-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.member-info {
  flex: 1;
}

.member-info h4 {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.member-info p {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 8px;
}

.member-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #27ae60;
  font-weight: 500;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.active {
  background: #27ae60;
}

/* 对话框样式 */
.carousel-dialog {
  border-radius: 16px;
}

.dialog-content {
  padding: 20px 0;
}

.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ecf0f1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home-container {
    padding: 16px;
  }
  
  .stats-panel {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .intro-content {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .members-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .carousel-overlay {
    padding: 20px;
  }
  
  .carousel-overlay h3 {
    font-size: 24px;
  }
  
  .carousel-overlay p {
    font-size: 16px;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-carousel__item) {
  border-radius: 16px;
}

:deep(.el-carousel__indicators) {
  bottom: 20px;
}

:deep(.el-carousel__indicator) {
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  width: 12px;
  height: 12px;
  margin: 0 4px;
}

:deep(.el-carousel__indicator.is-active) {
  background: white;
}

:deep(.el-upload--picture-card) {
  border-radius: 12px;
  border: 2px dashed #d9d9d9;
  transition: all 0.3s ease;
}

:deep(.el-upload--picture-card:hover) {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}
</style>