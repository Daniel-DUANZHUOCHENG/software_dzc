<template>
  <div class="container">
    <div>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input v-model="searchForm.action" placeholder="行为类型"></el-input>
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="searchForm.timestamp"
            type="date"
            placeholder="日期"
          ></el-date-picker>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col>
          <el-button type="success" @click="handleExport">导出</el-button>
        </el-col>
      </el-row>
      <el-table
        v-loading="loading"
        :data="paginatedUserBehaviors"
        style="width: 100%; margin-top: 20px;"
      >
        <el-table-column prop="action" label="行为类型" width="180"></el-table-column>
        <el-table-column prop="page" label="页面" width="180"></el-table-column>
        <el-table-column prop="timestamp" label="时间" width="180"></el-table-column>
      </el-table>
      <el-pagination
        style="margin-top: 20px; text-align: right;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[6, 12, 18, 24]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="filteredUserBehaviors.length"
      >
      </el-pagination>
    </div>

    <!-- 实时数据面板 -->
    <div class="real-time-panel">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="stat-card">
            <div class="stat-header">实时访客统计</div>
            <div class="stat-value">{{ realTimeVisitors }}</div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="stat-card">
            <div class="stat-header">在线用户统计</div>
            <div class="stat-value">{{ onlineUsers }}</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 用户画像 -->
    <div class="user-portrait-chart-container">
		<el-row :gutter="20">
		  <el-col :span="12">
		    <div id="user-portrait-chart" style="width: 100%; height: 400px;"></div>
		  </el-col>
		  <el-col :span="12">
		    <div id="user-segmentation-chart" style="width: 100%; height: 400px;"></div>
		  </el-col>
		</el-row>
      
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import * as echarts from 'echarts';

export default {
  name: 'UserBehaviorManagement',
  setup() {
    const searchForm = ref({
      action: '',
      timestamp: ''
    });
    const userBehaviors = ref([]);
    const loading = ref(false);
    const currentPage = ref(1);
    const pageSize = ref(6);
    const total = ref(0);
    const realTimeVisitors = ref(0);
    const onlineUsers = ref(0);
    const filteredUserBehaviors = ref([]);
    const userPortrait = ref([]);

    const fetchUserBehaviors = async () => {
      loading.value = true;
      try {
        const response = await axios.get('/api/userBehavior');
        userBehaviors.value = response.data.behaviors;
        filteredUserBehaviors.value = response.data.behaviors;
        total.value = response.data.total;
      } catch (error) {
        ElMessage.error('获取用户行为数据失败');
      } finally {
        loading.value = false;
      }
    };

    const fetchStats = async () => {
      setInterval(async () => {
        try {
          let response = await axios.get('http://localhost:9049/userBehavior/onlineUsers');
          onlineUsers.value = response.data.onlineUsers;

          response = await axios.get('http://localhost:9049/userBehavior/realTimeVisitors');
          realTimeVisitors.value = response.data.realTimeVisitors;
        } catch (err) {
          console.error('Error fetching stats', err);
        }
      }, 1000); // 每秒获取一次实时数据
    };

    const fetchUserPortrait = async () => {
      try {
        const response = await axios.get('http://localhost:9049/userBehavior/portraits');
        userPortrait.value = response.data;
        alert(JSON.stringify(userPortrait.value))
        processUserPortraitData();
		processUserSegmentationData();
      } catch (error) {
        ElMessage.error('获取用户画像失败');
      }
    };

   const processUserPortraitData = () => {
         const genderCounts = { Male: 0, Female: 0, Other: 0, Unknown: 0 };
         userPortrait.value.forEach(portrait => {
           if (portrait.gender) {
             genderCounts[portrait.gender] += 1;
           } else {
             genderCounts['Unknown'] += 1;
           }
         });
   
         const chart = echarts.init(document.getElementById('user-portrait-chart'));
         const option = {
           title: {
             text: '用户画像',
             subtext: '性别分布',
             left: 'center'
           },
           tooltip: {
             trigger: 'item'
           },
           legend: {
             orient: 'vertical',
             left: 'left'
           },
           series: [
             {
               name: '性别',
               type: 'pie',
               radius: '50%',
			   label: {
			             normal: {
			               formatter: '{b}: {c} ({d}%)',
			               position: 'outside'
			             }
			           },
               data: [
                 { value: genderCounts.Male, name: 'Male' },
                 { value: genderCounts.Female, name: 'Female' },
                 { value: genderCounts.Other, name: 'Other' },
                 { value: genderCounts.Unknown, name: 'Unknown' }
               ],
               emphasis: {
                 itemStyle: {
                   shadowBlur: 10,
                   shadowOffsetX: 0,
                   shadowColor: 'rgba(0, 0, 0, 0.5)'
                 }
               }
             }
           ]
         };
         chart.setOption(option);
       };
   
       const processUserSegmentationData = () => {
         const segmentationCounts = {
           activeUsers: 0,
           potentialUsers: 0,
           inactiveUsers: 0
         };
   
         userPortrait.value.forEach(portrait => {
           if (portrait.behaviorStats.pageViews > 10 && portrait.behaviorStats.videoPlay >= 1) {
             segmentationCounts.activeUsers += 1;
           } else if (portrait.behaviorStats.pageViews > 0 && portrait.behaviorStats.videoPlay >= 0) {
             segmentationCounts.potentialUsers += 1;
           } else {
             segmentationCounts.inactiveUsers += 1;
           }
         });
   
         const chart = echarts.init(document.getElementById('user-segmentation-chart'));
         const option = {
           title: {
             text: '用户分群分析',
             subtext: '根据行为特征划分',
             left: 'center'
           },
           tooltip: {
             trigger: 'item'
           },
           legend: {
             orient: 'vertical',
             left: 'left'
           },
           series: [
             {
               name: '用户类型',
               type: 'pie',
               radius: '50%',
			   label: {
			             normal: {
			               formatter: '{b}: {c} ({d}%)',
			               position: 'outside'
			             }
			           },
               data: [
                 { value: segmentationCounts.activeUsers, name: '活跃用户' },
                 { value: segmentationCounts.potentialUsers, name: '潜在用户' },
                 { value: segmentationCounts.inactiveUsers, name: '不活跃用户' }
               ],
               emphasis: {
                 itemStyle: {
                   shadowBlur: 10,
                   shadowOffsetX: 0,
                   shadowColor: 'rgba(0, 0, 0, 0.5)'
                 }
               }
             }
           ]
         };
         chart.setOption(option);
       };

    onMounted(() => {
      fetchUserBehaviors();
      fetchStats();
      fetchUserPortrait(); // 获取所有用户的用户画像
    });
	
	

    const handleSearch = () => {
      filteredUserBehaviors.value = userBehaviors.value.filter(behavior => {
        return (
          (!searchForm.value.action || behavior.action.includes(searchForm.value.action)) &&
          (!searchForm.value.timestamp || behavior.timestamp.includes(searchForm.value.timestamp))
        );
      });
      currentPage.value = 1;
    };

    const handleReset = () => {
      searchForm.value.action = '';
      searchForm.value.timestamp = '';
      filteredUserBehaviors.value = userBehaviors.value;
      currentPage.value = 1;
    };

    const handleExport = async () => {
      try {
        const response = await axios.get('/api/userBehavior');
        const exportData = response.data.behaviors.map(item => {
          return {
            行为类型: item.action,
            页面: item.page,
            时间: item.timestamp
          };
        });
        const worksheet = XLSX.utils.json_to_sheet(exportData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, '用户行为列表');
        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
        const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
        saveAs(blob, '用户行为列表.xlsx');
      } catch (error) {
        console.error('Error exporting data:', error);
      }
    };

    const handleSizeChange = (size) => {
      pageSize.value = size;
      currentPage.value = 1;
    };

    const handleCurrentChange = (page) => {
      currentPage.value = page;
    };

    return {
      searchForm,
      userBehaviors,
      loading,
      currentPage,
      pageSize,
      total,
      realTimeVisitors,
      onlineUsers,
      filteredUserBehaviors,
      handleSearch,
      handleReset,
      handleExport,
      handleSizeChange,
      handleCurrentChange,
      userPortrait
    };
  },
  computed: {
    paginatedUserBehaviors() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredUserBehaviors.slice(start, end);
    }
  }
};
</script>

<style scoped>
.el-row {
  margin-bottom: 20px;
}

.real-time-panel {
  margin-top: 40px;
}

.stat-card {
  background-color: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}

.stat-header {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
}

.user-portrait-chart-container {
  margin-top: 40px;
}
</style>
