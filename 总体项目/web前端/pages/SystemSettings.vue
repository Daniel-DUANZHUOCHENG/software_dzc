<template>
  <div class="settings-container">
    <div class="settings-header">
      <div class="header-content">
        <h1 class="page-title">系统设置</h1>
        <p class="page-description">配置和管理系统参数</p>
      </div>
    </div>

    <div class="settings-content">
      <!-- 基本设置 -->
      <div class="settings-section">
        <div class="section-header">
          <h2 class="section-title">基本设置</h2>
          <p class="section-description">系统基础配置选项</p>
        </div>
        
        <div class="settings-grid">
          <div class="setting-item">
            <div class="setting-info">
              <h3>系统名称</h3>
              <p>修改系统显示名称</p>
            </div>
            <el-input v-model="systemSettings.name" placeholder="测盟汇管理系统" />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>系统版本</h3>
              <p>当前系统版本号</p>
            </div>
            <el-input v-model="systemSettings.version" placeholder="v1.0.0" readonly />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>管理员邮箱</h3>
              <p>系统管理员联系邮箱</p>
            </div>
            <el-input v-model="systemSettings.adminEmail" placeholder="admin@example.com" />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>自动备份</h3>
              <p>启用系统数据自动备份</p>
            </div>
            <el-switch v-model="systemSettings.autoBackup" />
          </div>
        </div>
      </div>

      <!-- 安全设置 -->
      <div class="settings-section">
        <div class="section-header">
          <h2 class="section-title">安全设置</h2>
          <p class="section-description">系统安全相关配置</p>
        </div>
        
        <div class="settings-grid">
          <div class="setting-item">
            <div class="setting-info">
              <h3>会话超时</h3>
              <p>用户会话超时时间（分钟）</p>
            </div>
            <el-input-number v-model="systemSettings.sessionTimeout" :min="5" :max="1440" />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>密码强度</h3>
              <p>要求用户使用强密码</p>
            </div>
            <el-switch v-model="systemSettings.strongPassword" />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>登录验证</h3>
              <p>启用两步验证</p>
            </div>
            <el-switch v-model="systemSettings.twoFactorAuth" />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>IP白名单</h3>
              <p>启用IP地址白名单</p>
            </div>
            <el-switch v-model="systemSettings.ipWhitelist" />
          </div>
        </div>
      </div>

      <!-- 通知设置 -->
      <div class="settings-section">
        <div class="section-header">
          <h2 class="section-title">通知设置</h2>
          <p class="section-description">系统通知和提醒配置</p>
        </div>
        
        <div class="settings-grid">
          <div class="setting-item">
            <div class="setting-info">
              <h3>邮件通知</h3>
              <p>启用邮件通知功能</p>
            </div>
            <el-switch v-model="systemSettings.emailNotification" />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>短信通知</h3>
              <p>启用短信通知功能</p>
            </div>
            <el-switch v-model="systemSettings.smsNotification" />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>浏览器推送</h3>
              <p>启用浏览器推送通知</p>
            </div>
            <el-switch v-model="systemSettings.browserNotification" />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>系统维护通知</h3>
              <p>系统维护时通知用户</p>
            </div>
            <el-switch v-model="systemSettings.maintenanceNotification" />
          </div>
        </div>
      </div>

      <!-- 存储设置 -->
      <div class="settings-section">
        <div class="section-header">
          <h2 class="section-title">存储设置</h2>
          <p class="section-description">文件存储和数据库配置</p>
        </div>
        
        <div class="settings-grid">
          <div class="setting-item">
            <div class="setting-info">
              <h3>文件上传限制</h3>
              <p>单个文件最大上传大小（MB）</p>
            </div>
            <el-input-number v-model="systemSettings.maxFileSize" :min="1" :max="1024" />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>存储空间清理</h3>
              <p>自动清理临时文件</p>
            </div>
            <el-switch v-model="systemSettings.autoCleanup" />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>缓存有效期</h3>
              <p>系统缓存有效期（小时）</p>
            </div>
            <el-input-number v-model="systemSettings.cacheExpiry" :min="1" :max="72" />
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <h3>数据库优化</h3>
              <p>定期优化数据库</p>
            </div>
            <el-switch v-model="systemSettings.dbOptimization" />
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="settings-actions">
        <el-button type="primary" @click="saveSettings" :loading="saving">
          <el-icon><Check /></el-icon>
          保存设置
        </el-button>
        
        <el-button @click="resetSettings">
          <el-icon><RefreshLeft /></el-icon>
          重置为默认
        </el-button>
        
        <el-button type="danger" @click="clearCache">
          <el-icon><Delete /></el-icon>
          清空缓存
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, RefreshLeft, Delete } from '@element-plus/icons-vue'

const saving = ref(false)

const systemSettings = ref({
  name: '测盟汇管理系统',
  version: 'v1.0.0',
  adminEmail: 'admin@example.com',
  autoBackup: true,
  sessionTimeout: 30,
  strongPassword: true,
  twoFactorAuth: false,
  ipWhitelist: false,
  emailNotification: true,
  smsNotification: false,
  browserNotification: true,
  maintenanceNotification: true,
  maxFileSize: 100,
  autoCleanup: true,
  cacheExpiry: 24,
  dbOptimization: true
})

onMounted(() => {
  loadSettings()
})

const loadSettings = () => {
  // 从localStorage加载设置
  const savedSettings = localStorage.getItem('systemSettings')
  if (savedSettings) {
    try {
      Object.assign(systemSettings.value, JSON.parse(savedSettings))
    } catch (error) {
      console.error('加载系统设置失败:', error)
    }
  }
}

const saveSettings = async () => {
  try {
    saving.value = true
    
    // 保存到localStorage
    localStorage.setItem('systemSettings', JSON.stringify(systemSettings.value))
    
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('设置保存成功')
  } catch (error) {
    console.error('保存设置失败:', error)
    ElMessage.error('保存设置失败')
  } finally {
    saving.value = false
  }
}

const resetSettings = async () => {
  try {
    await ElMessageBox.confirm('确定要重置所有设置为默认值吗？', '确认重置', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 重置为默认值
    systemSettings.value = {
      name: '测盟汇管理系统',
      version: 'v1.0.0',
      adminEmail: 'admin@example.com',
      autoBackup: true,
      sessionTimeout: 30,
      strongPassword: true,
      twoFactorAuth: false,
      ipWhitelist: false,
      emailNotification: true,
      smsNotification: false,
      browserNotification: true,
      maintenanceNotification: true,
      maxFileSize: 100,
      autoCleanup: true,
      cacheExpiry: 24,
      dbOptimization: true
    }
    
    localStorage.removeItem('systemSettings')
    ElMessage.success('设置已重置为默认值')
  } catch (error) {
    // 用户取消操作
  }
}

const clearCache = async () => {
  try {
    await ElMessageBox.confirm('确定要清空系统缓存吗？这可能会影响系统性能。', '确认清空', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 模拟清空缓存
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    ElMessage.success('缓存清空成功')
  } catch (error) {
    // 用户取消操作
  }
}
</script>

<style scoped>
.settings-container {
  padding: 0;
  background: linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 50%, #B3E5FC 100%);
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.settings-header {
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

.settings-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 32px;
}

.settings-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 32px;
  border: 1px solid rgba(0, 188, 212, 0.12);
  box-shadow: 
    0 8px 32px rgba(0, 188, 212, 0.08),
    0 4px 16px rgba(0, 0, 0, 0.04);
}

.section-header {
  margin-bottom: 32px;
  text-align: center;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #263238;
  margin: 0 0 8px 0;
}

.section-description {
  font-size: 14px;
  color: #546E7A;
  margin: 0;
}

.settings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: rgba(248, 250, 252, 0.8);
  border-radius: 12px;
  border: 1px solid rgba(0, 188, 212, 0.08);
  transition: all 0.3s ease;
}

.setting-item:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(0, 188, 212, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 188, 212, 0.1);
}

.setting-info {
  flex: 1;
  margin-right: 20px;
}

.setting-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #263238;
  margin: 0 0 4px 0;
}

.setting-info p {
  font-size: 14px;
  color: #546E7A;
  margin: 0;
}

.settings-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 40px;
  padding: 32px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(0, 188, 212, 0.12);
}

:deep(.el-button) {
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
}

:deep(.el-input) {
  border-radius: 8px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-switch) {
  --el-switch-on-color: #00BCD4;
}

/* 深色模式支持 */
[data-theme="dark"] .settings-container {
  background: linear-gradient(135deg, #0F1419 0%, #1A1A1A 50%, #212121 100%);
}

[data-theme="dark"] .settings-header {
  background: rgba(30, 30, 30, 0.95);
  border-bottom-color: rgba(0, 188, 212, 0.2);
}

[data-theme="dark"] .page-title {
  color: #FFFFFF;
}

[data-theme="dark"] .page-description {
  color: #B0BEC5;
}

[data-theme="dark"] .settings-section {
  background: rgba(40, 40, 40, 0.95);
  border-color: rgba(0, 188, 212, 0.2);
}

[data-theme="dark"] .section-title {
  color: #FFFFFF;
}

[data-theme="dark"] .section-description {
  color: #B0BEC5;
}

[data-theme="dark"] .setting-item {
  background: rgba(50, 50, 50, 0.8);
  border-color: rgba(0, 188, 212, 0.15);
}

[data-theme="dark"] .setting-item:hover {
  background: rgba(60, 60, 60, 0.9);
  border-color: rgba(0, 188, 212, 0.3);
}

[data-theme="dark"] .setting-info h3 {
  color: #FFFFFF;
}

[data-theme="dark"] .setting-info p {
  color: #B0BEC5;
}

[data-theme="dark"] .settings-actions {
  background: rgba(40, 40, 40, 0.95);
  border-color: rgba(0, 188, 212, 0.2);
}
</style> 