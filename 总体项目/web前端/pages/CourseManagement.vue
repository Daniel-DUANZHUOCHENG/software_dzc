<template>
  <div>
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
        <el-button type="warning" @click="openEditDialog">修改</el-button>
        <el-button type="danger" @click="handleDeleteConfirm">删除</el-button>
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
      <el-table-column prop="courseID" label="课程ID" width="180"></el-table-column>
      <el-table-column prop="coursename" label="课程名称" width="180"></el-table-column>
      <el-table-column prop="courseintro" label="课程简介" width="180"></el-table-column>
      <el-table-column prop="owner" label="课程作者" width="180"></el-table-column>
      <el-table-column label="操作" width="240">
        <template v-slot="scope">
          <div class="action-buttons">
            <el-button type="link" size="small" @click="openEditDialog(scope.row)">修改</el-button>
            <el-button type="link" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
              <el-input v-model="formData.coursename"></el-input>
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
                :on-remove="handleRemove"
                :file-list="fileList">
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程简介" prop="courseintro">
              <el-input v-model="formData.courseintro"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程排序" prop="number">
              <el-input v-model="formData.number"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程视频" prop="videopath">
              <el-upload
                action="http://localhost:9049/api/courses/upload-video"
                :on-success="handleVideoUploadSuccess"
                :on-remove="handleVideoRemove"
                :file-list="videoFileList">
                <el-button>上传视频</el-button>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程作者" prop="owner">
              <el-input v-model="formData.owner"></el-input>
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
              <el-input v-model="formData.coursename"></el-input>
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
                :on-remove="handleRemove"
                :file-list="fileList">
                <i class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程简介" prop="courseintro">
              <el-input v-model="formData.courseintro"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程排序" prop="number">
              <el-input v-model="formData.number"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程视频" prop="videopath">
              <el-upload
                action="http://localhost:9049/api/courses/upload-video"
                :on-success="handleVideoUploadSuccess"
                :on-remove="handleVideoRemove"
                :file-list="videoFileList">
                <el-button>上传视频</el-button>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程作者" prop="owner">
              <el-input v-model="formData.owner"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="closeEditDialog">取消</el-button>
        <el-button type="primary" @click="submitEditForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';
import { ref, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
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

    const fetchCourses = async () => {
      loading.value = true;
      try {
        const response = await axios.get('http://localhost:9049/api/courses');
        courses.value = response.data.courses;
        total.value = response.data.total;
      } catch (error) {
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
      const user = JSON.parse(localStorage.getItem('user'));
      formData.value.tenantID = user ? user.tenantId : null;
    };

    const closeAddDialog = () => {
      addDialogVisible.value = false;
    };

    const openEditDialog = (row) => {
      const user = JSON.parse(localStorage.getItem('user'));
      if (user.role === 'Admin' && user.tenantId !== row.tenantID) {
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
      fileList.value = [
        {
          name: '课程封面',
          url: `http://localhost:9049${formData.value.coverpath}`
        }
      ];
      // 加载课程视频到 videoFileList
      videoFileList.value = [
        {
          name: '课程视频',
          url: `http://localhost:9049${formData.value.videopath}`
        }
      ];
      editDialogVisible.value = true;
    };

    const closeEditDialog = () => {
      editDialogVisible.value = false;
    };

    const handleUploadSuccess = (response, file, fileList) => {
      formData.value.coverpath = response.url;
    };

    const handleRemove = (file, fileList) => {
      formData.value.coverpath = '';
    };

    const handleVideoUploadSuccess = (response, file, fileList) => {
      formData.value.videopath = response.url;
    };

    const handleVideoRemove = (file, fileList) => {
      formData.value.videopath = '';
    };

    const submitForm = () => {
      if (!formData.value.coursename || !formData.value.courseintro || !formData.value.number || !formData.value.owner) {
        ElMessage.error('请填写所有必填项');
        return;
      }

      axios.post('http://localhost:9049/api/courses', formData.value).then(() => {
        ElMessage.success('课程创建成功');
        closeAddDialog();
        fetchCourses();
      }).catch(error => {
        ElMessage.error('课程创建失败: ' + error.message);
      });
    };

    const submitEditForm = () => {
      if (!formData.value.coursename || !formData.value.courseintro || !formData.value.number || !formData.value.owner) {
        ElMessage.error('请填写所有必填项');
        return;
      }

      axios.put(`http://localhost:9049/api/courses/${formData.value.courseID}`, formData.value).then(() => {
        ElMessage.success('课程修改成功');
        closeEditDialog();
        fetchCourses();
      }).catch(error => {
        ElMessage.error('课程修改失败: ' + error.message);
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
      const user = JSON.parse(localStorage.getItem('user'));
      if (user.role === 'Admin' && user.tenantId !== row.tenantID) {
        ElMessage.error('您无权删除其他租户的课程');
        return;
      }

      axios.delete(`http://localhost:9049/api/courses/${row.courseID}`)
        .then(() => {
          ElMessage.success('课程删除成功');
          fetchCourses();
        })
        .catch(error => {
          ElMessage.error('课程删除失败: ' + error.message);
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

    const handleExport = () => {
      const exportData = courses.value.map((item) => {
        return {
          课程ID: item.courseID,
          课程名称: item.coursename,
          课程简介: item.courseintro,
          课程排序: item.number,
          课程视频: item.videopath,
          课程作者: item.owner,
          封面路径: item.coverpath,
          租户ID: item.tenantID
        };
      });
      const worksheet = XLSX.utils.json_to_sheet(exportData);
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, '课程列表');
      const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
      const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
      saveAs(blob, '课程列表.xlsx');
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
      formData,
      fileList,
      videoFileList,
      rules,
      handleSearch,
      handleReset,
      openAddDialog,
      closeAddDialog,
      openEditDialog,
      closeEditDialog,
      handleUploadSuccess,
      handleRemove,
      handleVideoUploadSuccess,
      handleVideoRemove,
      submitForm,
      submitEditForm,
      handleDeleteConfirm,
      handleDelete,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      handleExport
    };
  }
};
</script>

<style scoped>
.el-row {
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  justify-content: space-around;
}

.dialog-form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}
</style>
