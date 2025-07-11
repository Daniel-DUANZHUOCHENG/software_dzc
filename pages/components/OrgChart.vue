<template>
  <div>
    <div class="org-chart-mask" @click="handleClose"></div>
    <div class="org-chart-container">
      <div class="chart-header">
        <h3>组织架构知识图谱</h3>
        <el-button
          type="text"
          class="close-button"
          @click="handleClose"
        >
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
      <div ref="chartRef" class="chart-content"></div>
    </div>

    <!-- 公司信息弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedCompany?.name"
      width="500px"
      :close-on-click-modal="false"
      class="company-dialog"
      destroy-on-close
    >
      <div class="company-info" v-if="selectedCompany">
        <div class="info-card">
          <div class="info-header">
            <el-icon><OfficeBuilding /></el-icon>
            <span>基本信息</span>
          </div>
          <div class="info-content">
            <div class="info-item">
              <span class="label">公司名称：</span>
              <span>{{ selectedCompany.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">成立时间：</span>
              <span>{{ selectedCompany.establishDate }}</span>
            </div>
            <div class="info-item">
              <span class="label">公司规模：</span>
              <span>{{ selectedCompany.size }}</span>
            </div>
          </div>
        </div>

        <div class="info-card">
          <div class="info-header">
            <el-icon><Location /></el-icon>
            <span>联系方式</span>
          </div>
          <div class="info-content">
            <div class="info-item">
              <span class="label">地址：</span>
              <span>{{ selectedCompany.address }}</span>
            </div>
            <div class="info-item">
              <span class="label">电话：</span>
              <span>{{ selectedCompany.phone }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱：</span>
              <span>{{ selectedCompany.email }}</span>
            </div>
          </div>
        </div>

        <div class="info-card">
          <div class="info-header">
            <el-icon><Document /></el-icon>
            <span>公司简介</span>
          </div>
          <div class="info-content">
            <p class="company-description">{{ selectedCompany.description }}</p>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="dialogVisible = false">
            关闭
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import { Close, Location, Document, OfficeBuilding } from '@element-plus/icons-vue'

// 添加emit
const emit = defineEmits(['close'])

// 添加关闭处理函数
const handleClose = () => {
  emit('close')
}

// 定义公司信息的接口
interface CompanyInfo {
  name: string
  establishDate: string
  size: string
  address: string
  phone: string
  email: string
  description: string
}

// 初始化公司信息
const companyInfo: Record<string, CompanyInfo> = {
  'tenant-1': {
    name: '京都动画',
    establishDate: '1981年7月12日',
    size: '200-500人',
    address: '日本京都府宇治市',
    phone: '+81-774-33-3000',
    email: 'info@kyotoanimation.co.jp',
    description: '京都动画是日本著名的动画制作公司，以精良的作画质量和独特的艺术风格而闻名。'
  },
  'tenant-2': {
    name: 'MAPPA公司',
    establishDate: '2011年6月14日',
    size: '300-600人',
    address: '东京都杉并区',
    phone: '+81-3-5347-3500',
    email: 'info@mappa.co.jp',
    description: 'MAPPA是一家新兴的动画制作公司，成立于2011年。以制作《进击的巨人》最终季、《咒术回战》等作品而闻名，是目前日本动画业界最受关注的公司之一。'
  },
  'tenant-3': {
    name: 'Madhouse公司',
    establishDate: '1972年10月17日',
    size: '400-700人',
    address: '东京都中野区',
    phone: '+81-3-3384-4700',
    email: 'info@madhouse.co.jp',
    description: 'Madhouse是日本知名的动画制作公司，成立于1972年。以制作《死神》、《一拳超人》等经典作品而闻名，是日本动画界的老牌制作公司。'
  },
  'tenant-4': {
    name: '阿里巴巴集团',
    establishDate: '1999年6月28日',
    size: '50000+人',
    address: '浙江省杭州市余杭区文一西路969号',
    phone: '400-800-1688',
    email: 'contact@alibaba.com',
    description: '阿里巴巴集团创立于1999年，是全球最大的电子商务公司之一。业务涵盖电商、云计算、数字支付、人工智能等多个领域，致力于让天下没有难做的生意。'
  },
  'tenant-5': {
    name: '腾讯科技',
    establishDate: '1998年11月11日',
    size: '40000+人',
    address: '深圳市南山区高新科技园腾讯大厦',
    phone: '400-123-4567',
    email: 'contact@tencent.com',
    description: '腾讯是中国最大的互联网综合服务提供商之一，也是中国最大的游戏开发和运营商。公司业务涵盖社交、游戏、数字内容、金融科技、企业服务等领域。'
  },
  'tenant-6': {
    name: '字节跳动',
    establishDate: '2012年3月12日',
    size: '30000+人',
    address: '北京市海淀区科学院南路2号融科资讯中心',
    phone: '400-800-9999',
    email: 'contact@bytedance.com',
    description: '字节跳动是全球领先的科技公司，以人工智能技术为驱动，创造了包括今日头条、抖音、TikTok等多个深受欢迎的应用程序，服务全球用户。'
  },
  'tenant-7': {
    name: '百度',
    establishDate: '2000年1月1日',
    size: '35000+人',
    address: '北京市海淀区上地十街10号百度大厦',
    phone: '400-890-0000',
    email: 'contact@baidu.com',
    description: '百度是中国最大的搜索引擎公司，也是全球领先的人工智能公司。致力于用科技让复杂的世界更简单，为用户提供最便捷的信息获取方式。'
  },
  'tenant-8': {
    name: '华为技术',
    establishDate: '1987年9月15日',
    size: '100000+人',
    address: '深圳市龙岗区坂田华为基地',
    phone: '400-830-8300',
    email: 'contact@huawei.com',
    description: '华为是全球领先的ICT（信息与通信）基础设施和智能终端提供商。致力于把数字世界带入每个人、每个家庭、每个组织，构建万物互联的智能世界。'
  },
  'tenant-9': {
    name: '小米科技',
    establishDate: '2010年4月6日',
    size: '20000+人',
    address: '北京市海淀区清河中街68号华润五彩城',
    phone: '400-100-5678',
    email: 'contact@xiaomi.com',
    description: '小米是一家以手机、智能硬件和IoT平台为核心的互联网公司。公司愿景是"让每个人都能享受科技的乐趣"，致力于以创新的技术打造优质的智能产品。'
  },
  'tenant-10': {
    name: '网易',
    establishDate: '1997年6月24日',
    size: '25000+人',
    address: '杭州市滨江区网易大厦',
    phone: '400-820-6666',
    email: 'contact@netease.com',
    description: '网易是中国领先的互联网技术公司，在游戏开发、音乐服务、电子商务、教育科技等领域都有重要布局。致力于通过技术创新为用户创造更多价值。'
  },
  'tenant-11': {
    name: '东北大学软件学院',
    establishDate: '2000年6月15日',
    size: '1000-2000人',
    address: '沈阳市浑南区东北大学软件园',
    phone: '024-8368-7777',
    email: 'contact@neu.edu.cn',
    description: '东北大学软件学院是教育部批准设立的首批35所示范性软件学院之一，致力于培养高素质、创新型、应用型软件人才，在人才培养、科学研究、社会服务等方面取得显著成绩。'
  }
}

interface Department {
  id: number | string;
  departmentName?: string;
  name?: string;
  children?: Department[];
  tenantId?: string; // 新增属性，用于表示租户ID
}

const props = defineProps({
  data: {
    type: Array as () => Department[],
    required: true,
    default: () => []
  }
})

const chartRef = ref<HTMLElement | null>(null)
let chart: echarts.ECharts | null = null

// 处理数据，转换为图谱格式
const processData = (data: Department[]) => {
  const nodes: any[] = []
  const links: any[] = []
  
  // 添加根节点
  nodes.push({
    id: 'root',
    name: '组织架构',
    symbolSize: 60,
    itemStyle: {
      color: '#2B2947'
    },
    label: {
      show: true,
      fontSize: 16,
      fontWeight: 'bold',
      color: '#fff'
    }
  })
  
  // 处理租户节点
  data.forEach((tenant: Department) => {
    // 从租户节点的ID中提取实际的租户ID
    // 租户节点的ID格式应该是 'tenant-{actualTenantId}'
    let actualTenantId = tenant.id
    if (typeof tenant.id === 'string' && tenant.id.startsWith('tenant-')) {
      actualTenantId = tenant.id // 直接使用完整的ID
    } else if (tenant.tenantId) {
      actualTenantId = `tenant-${tenant.tenantId}` // 使用租户的实际ID
    } else {
      actualTenantId = `tenant-${tenant.id}` // 备用方案
    }
    
    console.log('处理租户节点:', tenant.departmentName, '实际ID:', actualTenantId)
    
    nodes.push({
      id: actualTenantId,
      name: tenant.departmentName || tenant.name || '未命名部门',
      symbolSize: 50,
      itemStyle: {
        color: '#409EFF'
      },
      label: {
        show: true,
        fontSize: 14,
        color: '#fff',
        position: 'inside'
      }
    })
    
    // 连接根节点和租户
    links.push({
      source: 'root',
      target: actualTenantId,
      lineStyle: {
        color: '#409EFF',
        width: 2
      }
    })
    
    // 处理部门节点
    if (tenant.children && Array.isArray(tenant.children)) {
      tenant.children.forEach((dept: Department) => {
        const deptId = `dept-${dept.id || Math.random()}`
        nodes.push({
          id: deptId,
          name: dept.departmentName || dept.name || '未命名子部门',
          symbolSize: 40,
          itemStyle: {
            color: '#67C23A'
          },
          label: {
            show: true,
            fontSize: 12,
            color: '#fff',
            position: 'inside'
          }
        })
        
        // 连接租户和部门
        links.push({
          source: actualTenantId,
          target: deptId,
          lineStyle: {
            color: '#67C23A',
            width: 1.5
          }
        })
      })
    }
  })
  
  return { nodes, links }
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  // 销毁现有的图表实例
  if (chart) {
    chart.off('click') // 移除之前的点击事件
    chart.dispose()
  }
  
  // 创建新的图表实例
  chart = echarts.init(chartRef.value)
  const { nodes, links } = processData(props.data)
  
  const option: EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: '#ccc',
      borderWidth: 1,
      padding: [5, 10],
      textStyle: {
        color: '#fff'
      }
    },
    animationDurationUpdate: 1500,
    animationEasingUpdate: 'quinticOut', // 修改为有效的动画类型
    series: [{
      type: 'graph',
      layout: 'force',
      data: nodes,
      links: links,
      roam: true,
      draggable: true,
      symbol: 'circle',
      symbolSize: 50,
      center: ['50%', '50%'],
      zoom: 0.8,
      label: {
        show: true,
        position: 'inside',
        formatter: '{b}',
        fontSize: 12,
        color: '#fff'
      },
      force: {
        repulsion: 2000,
        edgeLength: [150, 250],
        gravity: 0.1,
        layoutAnimation: true
      },
      edgeSymbol: ['circle', 'arrow'],
      edgeSymbolSize: [4, 10],
      lineStyle: {
        opacity: 0.9,
        width: 2,
        curveness: 0.1
      },
      emphasis: {
        focus: 'adjacency',
        lineStyle: {
          width: 4
        },
        label: {
          fontSize: 14,
          fontWeight: 'bold'
        }
      },
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 1
      }
    }]
  }
  
  chart.setOption(option)
  
  // 添加点击事件监听
  chart.on('click', (params: any) => {
    console.log('Node clicked:', params)
    console.log('Node ID:', params.data?.id)
    console.log('Available company info keys:', Object.keys(companyInfo))
    
    if (params.dataType === 'node') {
      const nodeId = params.data.id
      console.log('Checking nodeId:', nodeId)
      
      if (nodeId.startsWith('tenant-')) {
        const company = companyInfo[nodeId]
        console.log('Found company:', company)
        
        if (company) {
          selectedCompany.value = company
          dialogVisible.value = true
          console.log('Dialog should open now')
        } else {
          console.log('No company info found for:', nodeId)
        }
      }
    }
  })
}

// 监听数据变化
watch(() => props.data, () => {
  if (chart) {
    initChart()
  }
}, { deep: true })

// 监听窗口大小变化
const handleResize = () => {
  if (chart) {
    chart.resize()
  }
}

// 将 drawerVisible 改为 dialogVisible
const dialogVisible = ref(false)
const selectedCompany = ref<CompanyInfo | null>(null)

onMounted(() => {
  // 确保DOM已经渲染完成
  nextTick(() => {
    initChart()
  })
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (chart) {
    chart.off('click') // 移除点击事件
    chart.dispose()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.org-chart-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1999;
}

.org-chart-container {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%;
  height: 80vh;
  min-height: 600px;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  z-index: 2000;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.chart-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-button {
  font-size: 20px;
  color: #909399;
  transition: color 0.3s;
}

.close-button:hover {
  color: #409EFF;
}

.chart-content {
  flex: 1;
  width: 100%;
  min-height: 600px;
  margin-top: 20px;
}

.company-info {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background-color: #ddd;
    border-radius: 3px;
  }
}

.info-card {
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;

  &:last-child {
    margin-bottom: 0;
  }
}

.info-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background-color: #f8f9fa;
  color: #333;
  font-size: 16px;
  font-weight: 500;
  border-bottom: 1px solid #eee;

  .el-icon {
    font-size: 18px;
    color: #409EFF;
  }
}

.info-content {
  padding: 16px;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  line-height: 1.5;

  &:last-child {
    margin-bottom: 0;
  }
}

.label {
  color: #666;
  min-width: 80px;
  margin-right: 8px;
}

.company-description {
  color: #666;
  line-height: 1.6;
  text-align: justify;
  margin: 0;
}

.dialog-footer {
  text-align: right;
}
</style> 