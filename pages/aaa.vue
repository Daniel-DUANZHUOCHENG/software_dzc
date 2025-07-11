<template>
  <div class="container">
    <div>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input v-model="searchForm.tenantName" placeholder="租户名称"></el-input>
        </el-col>
        <el-col :span="6">
          <el-input v-model="searchForm.contactPerson" placeholder="联系人"></el-input>
        </el-col>
        <el-col :span="6">
          <el-input v-model="searchForm.phone" placeholder="联系电话"></el-input>
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
        :data="tenants"
        style="width: 100%; margin-top: 20px;"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="tenantName" label="租户名称" width="180"></el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="180"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="180"></el-table-column>
        <el-table-column label="租户图标" width="180">
          <template v-slot="scope">
            <img :src="`http://localhost:9049/${scope.row.icon}`" alt="租户图标" style="width: 100px; height: auto;">
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180"></el-table-column>
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

      <!-- 新增租户弹窗 -->
      <el-dialog v-model="addDialogVisible" title="新增租户" width="60%">
        <div v-if="currentStep === 1">
          <el-form :model="formData" :rules="rules" ref="tenantForm" label-width="100px" class="dialog-form">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="租户名称" prop="tenantName">
                  <el-input v-model="formData.tenantName"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="租户图标" prop="icon">
                  <el-upload
                    action="http://localhost:9049/tenants/upload-icon"
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
                <el-form-item label="联系人" prop="contactPerson">
                  <el-input v-model="formData.contactPerson"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系电话" prop="phone">
                  <el-input v-model="formData.phone"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="创建时间" prop="createdAt">
                  <el-date-picker v-model="formData.createdAt" type="datetime" placeholder="选择创建时间"></el-date-picker>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-form-item label="备注" prop="remark">
                <div class="editor-wrapper">
                  <div id="editor" class="editor-container"></div>
                </div>
              </el-form-item>
            </el-row>
          </el-form>
        </div>
        <div v-else-if="currentStep === 2">
          <el-form :model="formData" label-width="100px" class="dialog-form">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="管理员用户名" prop="adminUsername">
                  <el-input v-model="formData.adminUsername"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="密码" prop="password">
                  <el-input type="password" v-model="formData.password"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <template #footer>
          <el-button @click="closeAddDialog">取消</el-button>
          <el-button v-if="currentStep > 1" @click="prevStep">上一步</el-button>
          <el-button v-if="currentStep < 2" type="primary" @click="nextStep">下一步</el-button>
          <el-button v-if="currentStep === 2" type="primary" @click="submitForm">确定</el-button>
        </template>
      </el-dialog>

      <!-- 修改租户弹窗 -->
      <el-dialog v-model="editDialogVisible" title="修改租户" width="60%">
        <el-form :model="formData" :rules="rules" ref="editTenantForm" label-width="100px" class="dialog-form">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="租户名称" prop="tenantName">
                <el-input v-model="formData.tenantName"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="租户图标" prop="icon">
                <el-upload
                  action="http://localhost:9049/tenants/upload-icon"
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
              <el-form-item label="联系人" prop="contactPerson">
                <el-input v-model="formData.contactPerson"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="formData.phone"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="创建时间" prop="createdAt">
                <el-date-picker v-model="formData.createdAt" type="datetime" placeholder="选择创建时间"></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-form-item label="备注" prop="remark">
              <div class="editor-wrapper">
                <div id="edit-editor" class="editor-container"></div>
              </div>
            </el-form-item>
          </el-row>
        </el-form>
        <template #footer>
          <el-button @click="closeEditDialog">取消</el-button>
          <el-button type="primary" @click="submitEditForm">确定</el-button>
        </template>
      </el-dialog>

      <!-- 租户详情弹窗 -->
      <el-dialog v-model="detailsDialogVisible" title="租户详情" width="60%">
        <el-form :model="detailsFormData" label-width="100px" class="dialog-form" disabled>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="租户名称">
                <el-input v-model="detailsFormData.tenantName" disabled></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="租户图标">
                <img :src="`http://localhost:9049/${detailsFormData.icon}`" alt="租户图标" style="width: 100px; height: auto;">
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系人">
                <el-input v-model="detailsFormData.contactPerson" disabled></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话">
                <el-input v-model="detailsFormData.phone" disabled></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="创建时间">
                <el-date-picker v-model="detailsFormData.createdAt" type="datetime" disabled></el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="备注">
                <el-input type="textarea" v-model="detailsFormData.remark" disabled></el-input>
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
import axios from '../utils/request.js';
import { ref, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import Quill from 'quill';
import 'quill/dist/quill.core.css';
import 'quill/dist/quill.snow.css';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

export default {
  name: 'TenantManagement',
  setup() {
    const searchForm = ref({
      tenantName: '',
      contactPerson: '',
      phone: ''
    });
    const tenants = ref([]);
    const selectedRow = ref(null);
    const loading = ref(false);
    const currentPage = ref(1);
    const pageSize = ref(6);
    const total = ref(0);
    const addDialogVisible = ref(false);
    const editDialogVisible = ref(false);
    const detailsDialogVisible = ref(false);
    const formData = ref({
      tenantName: '',
      contactPerson: '',
      phone: '',
      icon: '',
      createdAt: '',
      remark: '',
      adminUsername: '',
      password: ''
    });
    const detailsFormData = ref({
      tenantName: '',
      contactPerson: '',
      phone: '',
      icon: '',
      createdAt: '',
      remark: ''
    });
    const fileList = ref([]);
    const rules = ref({
      tenantName: [{ required: true, message: '请输入租户名称', trigger: 'blur' }],
      contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
      phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
      icon: [{ required: true, message: '请上传租户图标', trigger: 'change' }],
      createdAt: [{ required: true, message: '请选择创建时间', trigger: 'change' }],
      remark: [{ required: true, message: '请输入备注', trigger: 'blur' }]
    });
    const currentStep = ref(1);

    const fetchTenants = async () => {
      loading.value = true;
      try {
        const response = await axios.get('/api/tenants/all');
        tenants.value = response.data.tenantList;
        total.value = response.data.total;
      } catch (error) {
        ElMessage.error('获取租户数据失败');
      } finally {
        loading.value = false;
      }
    };

    onMounted(fetchTenants);

    const handleSearch = () => {
      const params = {
        tenantName: searchForm.value.tenantName,
        contactPerson: searchForm.value.contactPerson,
        phone: searchForm.value.phone
      };
      axios.get('http://localhost:9049/tenants/search', { params })
        .then(response => {
          tenants.value = response.data.tenantList;
          total.value = response.data.total;
        })
        .catch(error => {
          console.error('Error searching data:', error);
        });
    };

    const handleReset = () => {
      searchForm.value.tenantName = '';
      searchForm.value.contactPerson = '';
      searchForm.value.phone = '';
      fetchTenants();
    };

    const handleExport = async () => {
      try {
        const response = await axios.get('/api/tenants/all');
        const exportData = response.data.tenantList.map(item => {
          return {
            租户ID: item.id,
            租户名称: item.tenantName,
            联系人: item.contactPerson,
            联系电话: item.phone,
            图标: item.icon,
            创建时间: item.createdAt,
            备注: item.remark
          };
        });
        const worksheet = XLSX.utils.json_to_sheet(exportData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, '租户列表');
        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
        const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
        saveAs(blob, '租户列表.xlsx');
      } catch (error) {
        console.error('Error exporting data:', error);
      }
    };

    const resetForm = () => {
      formData.value = {
        tenantName: '',
        contactPerson: '',
        phone: '',
        icon: '',
        createdAt: '',
        remark: '',
        adminUsername: '',
        password: '',
        rootDepartmentId: null
      };
      fileList.value = [];
      currentStep.value = 1;
    };

    const openAddDialog = () => {
      addDialogVisible.value = true;
      resetForm();
      nextTick(() => {
        initializeEditor('#editor', false);
      });
    };

    const closeAddDialog = () => {
      addDialogVisible.value = false;
    };

    const nextStep = () => {
      if (currentStep.value < 2) {
        currentStep.value++;
      }
    };

    const prevStep = () => {
      if (currentStep.value > 1) {
        currentStep.value--;
      }
    };

    const openEditDialog = (row) => {
      if (row) {
        Object.assign(formData.value, row);
      } else if (selectedRow.value) {
        Object.assign(formData.value, selectedRow.value);
      } else {
        ElMessage.error('请先选择要修改的租户');
        return;
      }
      fileList.value = [
        {
          name: '租户图标',
          url: `http://localhost:9049/${formData.value.icon}`
        }
      ];
      editDialogVisible.value = true;
      nextTick(() => {
        initializeEditor('#edit-editor', true, formData.value.remark);
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
      formData.value.icon = response.url;
    };

    const handleRemove = (file, fileList) => {
      formData.value.icon = '';
    };

    const submitForm = () => {
      if (!formData.value.tenantName || !formData.value.remark || !formData.value.createdAt || !formData.value.icon || !formData.value.adminUsername || !formData.value.password) {
        ElMessage.error('请填写所有必填项');
        return;
      }

      axios.post('api/tenants/insert', formData.value).then(() => {
        ElMessage.success('租户创建成功');
        closeAddDialog();
        fetchTenants();
      }).catch(error => {
        ElMessage.error('租户创建失败: ' + error.message);
      });
    };

    const submitEditForm = () => {
      if (!formData.value.tenantName || !formData.value.remark || !formData.value.createdAt || !formData.value.icon) {
        ElMessage.error('请填写所有必填项');
        return;
      }

      axios.put(`/api/tenants/${formData.value.id}`, formData.value).then(() => {
        ElMessage.success('租户修改成功');
        closeEditDialog();
        fetchTenants();
      }).catch(error => {
        ElMessage.error('租户修改失败: ' + error.message);
      });
    };

    const handleDeleteConfirm = () => {
      if (!selectedRow.value) {
        ElMessage.error('请先选择要删除的租户');
        return;
      }
      ElMessageBox.confirm('此操作将永久删除该租户, 是否继续?', '提示', {
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
      axios.delete(`/api/tenants/delete/${row.id}`)
        .then(() => {
          ElMessage.success('租户删除成功');
          fetchTenants();
        })
        .catch(error => {
          ElMessage.error('租户删除失败: ' + error.message);
        });
    };

    const handleSelectionChange = (rows) => {
      selectedRow.value = rows.length ? rows[0] : null;
    };

    const handleSizeChange = (size) => {
      pageSize.value = size;
      fetchTenants();
    };

    const handleCurrentChange = (page) => {
      currentPage.value = page;
      fetchTenants();
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
        formData.value.remark = quill.root.innerHTML;
      });
    };

    return {
      searchForm,
      tenants,
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
      handleCurrentChange,
      nextStep,
      prevStep,
      currentStep
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
