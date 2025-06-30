<template>
	<div class="container">

  <div>
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
        <el-button type="primary" @click="openAddDialog">新增</el-button>
        <el-button type="warning" @click="openEditDialog">修改</el-button>
        <el-button type="danger" @click="handleDeleteConfirm">删除</el-button>
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
      <el-table-column prop="conferencename" label="会议名称" width="180"></el-table-column>
      <el-table-column prop="creator" label="创建人" width="180"></el-table-column>
      <el-table-column prop="state" label="会议状态" width="180"></el-table-column>
      <el-table-column label="会议封面" width="180">
        <template v-slot="scope">
          <img 
            :src="scope.row.coverpath ? `http://localhost:9049${scope.row.coverpath}` : '/images/default-icon.jpg'" 
            alt="会议封面" 
            style="width: 100px; height: auto;"
            @error="event => event.target.src = '/images/default-icon.jpg'"
          >
        </template>
      </el-table-column>
      <el-table-column prop="starttime" label="开始时间" width="180"></el-table-column>
      <el-table-column label="操作" width="240">
        <template v-slot="scope">
          <div class="action-buttons">
            <el-button type="link" size="small" @click="openDetailsDialog(scope.row)">详情</el-button>
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
              <div class="editor-wrapper">
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
              <div class="editor-wrapper">
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
  </div>
  </div>
</template>

<script>
import axios from 'axios';
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
        const response = await axios.get('/api/conferences');
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
      axios.get('http://localhost:9049/api/conferences/search', { params })
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
        const response = await axios.get('/api/conferences');
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
      const user = JSON.parse(localStorage.getItem('user'));
      formData.value.tenantID = user ? user.tenantId : null;

      nextTick(() => {
        initializeEditor('#editor', false);
      });
    };

    const closeAddDialog = () => {
      addDialogVisible.value = false;
    };

    const openEditDialog = (row) => {
      const user = JSON.parse(localStorage.getItem('user'));
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

      axios.post('/api/conferences', formData.value).then(() => {
        ElMessage.success('会议创建成功');
        closeAddDialog();
        fetchMeetings();
      }).catch(error => {
        ElMessage.error('会议创建失败: ' + error.message);
      });
    };

    const submitEditForm = () => {
      if (!formData.value.conferencename || !formData.value.contentspath || !formData.value.starttime || !formData.value.coverpath) {
        ElMessage.error('请填写所有必填项');
        return;
      }

      axios.put(`/api/conferences/${formData.value.conferenceID}`, formData.value).then(() => {
        ElMessage.success('会议修改成功');
        closeEditDialog();
        fetchMeetings();
      }).catch(error => {
        ElMessage.error('会议修改失败: ' + error.message);
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
      const user = JSON.parse(localStorage.getItem('user'));
      if (user.role === 'TAdmin' && user.tenantId !== row.tenantID) {
        ElMessage.error('您无权删除其他租户的会议');
        return;
      }

      axios.delete(`/api/conferences/${row.conferenceID}`)
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

      quill.on('text-change', () => {
        formData.value.contentspath = quill.root.innerHTML;
      });
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
      handleCurrentChange
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

.editor-wrapper {
  width: 100%;
  height: 400px;
}

.dialog-form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}
</style>
